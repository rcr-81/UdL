package com.smile.webscripts.expedient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.alfresco.repo.audit.AuditComponent;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ApplicationContextHelper;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.w3c.dom.Element;

import com.smile.webscripts.audit.AuditUdl;
import com.smile.webscripts.helper.Arguments;
import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Impersonate;

public class TransferirTotsExpedients extends DeclarativeWebScript implements ConstantsUdL {

	private ServiceRegistry serviceRegistry;
	private AuditComponent auditComponent;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	public void setAuditComponent(AuditComponent auditComponent) {
		this.auditComponent = auditComponent;
	}

	/**
	 * Transfer a folder (expediente) consists in:
	 * 		- Move all content (folders and documents) from a Content Management space 
	 * 		  to Records Management file plan categories (this is mapped through content metadata). 
	 * 		- Declare all content moved to Records Management as Records (all mandatory metadata must be filled before).
	 * 		- Cutoff all content moved to Records Management (all content must be declared as Record 
	 *        and cutoff action must be available on the category).
	 */
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		UserTransaction trx = serviceRegistry.getTransactionService().getUserTransaction();
		HashMap<String, Object> model = new HashMap<String,Object>();
		Element args = null;
		String username = null;
		Scriptable scope = null;
		try {
			args = Arguments.getArguments(req);
			username = args.getElementsByTagName(FORM_PARAM_USERNAME).item(0).getFirstChild().getNodeValue();
			if("session".equals(username)) {
				username = serviceRegistry.getAuthenticationService().getCurrentUserName();				
			}
			model.put(FTL_USERNAME, username);
			Impersonate.impersonate(username);
			
			Context cx = Context.enter();
			scope = cx.initStandardObjects();
			
			String docID = args.getElementsByTagName(FORM_PARAM_EXP_ID).item(0).getFirstChild().getNodeValue();			
			NodeRef expedientNodeRef = new NodeRef(AUDIT_NODEREF_PREFIX + docID);			
			ScriptNode expedient = new ScriptNode(expedientNodeRef, serviceRegistry, scope);
			List<FileInfo> childrenFolders = serviceRegistry.getFileFolderService().listFolders(expedient.getParent().getNodeRef());

			Iterator<?> it = childrenFolders.iterator();
			List<NodeRef> toTransfer = new ArrayList<NodeRef>();
			while (it.hasNext()){
				FileInfo f = (FileInfo)it.next();
				String type = (String)serviceRegistry.getNodeService().getType(f.getNodeRef()).getLocalName();
				if(type.equals("expedient") || type.equals("agregacio")){
					toTransfer.add(f.getNodeRef());
				}
			}
			
			
			Iterator<NodeRef> it2 = toTransfer.iterator();
			TransferirExpedient trasnfer = new TransferirExpedient(serviceRegistry);

			trx.begin();
			
			while (it2.hasNext()){
				NodeRef expNodeRef = it2.next();
				String typeExpArxiuActiu = expedient.getTypeShort();
				String siteExpArxiuActiu = expedient.getSiteShortName();
				Set<QName> aspects = serviceRegistry.getNodeService().getAspects(expNodeRef);
				Map<QName, Serializable> allNodeProps = serviceRegistry.getNodeService().getProperties(expNodeRef);
				
				//Auditar transferir expedient
				if(typeExpArxiuActiu.equals("udl:expedient")){
					AuditUdl.auditRecord(auditComponent, username, expNodeRef.toString(), AUDIT_ACTION_TRANSFER_EXPEDIENT,  typeExpArxiuActiu, siteExpArxiuActiu);
				}
				else if(typeExpArxiuActiu.equals("udl:agregacio")){
					AuditUdl.auditRecord(auditComponent, username, expNodeRef.toString(), AUDIT_ACTION_TRANSFER_AGREGACIO,  typeExpArxiuActiu, siteExpArxiuActiu);
				}		

				//initTransfer(args, username, model, req, scope, (NodeRef)it2.next());
				NodeRef ref = trasnfer.transferNodes(username, expNodeRef);
				trasnfer.declareRecordTree(ref, serviceRegistry);
				// El cutoff se hace automáticamente, ya que la serie de destino
				// tendrá un calendario de conservación con el evento cutoff
				// planificado
				ScriptNode expedientRma = new ScriptNode(ref, serviceRegistry, scope);				
				trasnfer.addAspects(expedientRma.getNodeRef(), aspects, allNodeProps);
				model.put(FTL_EXPEDIENT, expedientRma);
				expedientRma.save();
			}

			trx.commit();
			
			model.put(FTL_SUCCESS, String.valueOf(true));

		}catch (Exception e) {
			e.printStackTrace();
			model.put(FTL_ERROR, e.getMessage());
			model.put(FTL_SUCCESS, String.valueOf(false));
			status.setCode(500); 

			try {
				if (trx.getStatus() == javax.transaction.Status.STATUS_ACTIVE) {
					trx.rollback();
				}
			} catch (SystemException ex) {
				e.printStackTrace();
			}
		}

		return model;
	}

	/**
	 * @param args
	 * @param username
	 * @param model
	 * @param req
	 * @return
	 */
	/*private Map<String, Object> initTransfer(Element args, String username, Map<String, Object> model, WebScriptRequest req, Scriptable scope, NodeRef expedientNodeRef){
		try { 
			ScriptNode expedient = new ScriptNode(expedientNodeRef, serviceRegistry, scope);
			String nodeRefExpArxiuActiu = expedient.getNodeRef().toString();
			String typeExpArxiuActiu = expedient.getTypeShort();
			String siteExpArxiuActiu = expedient.getSiteShortName();

			Set<QName> aspects = serviceRegistry.getNodeService().getAspects(expedient.getNodeRef());
			Map<QName, Serializable> allNodeProps = serviceRegistry.getNodeService().getProperties(expedient.getNodeRef());

			String ref = transferirNodes(username, expedient.getDisplayPath(), expedient.getName(), req);			
			//El indice se crea en el webscript transferir nodes

			declareRecordRecursive(username, ref, req);
			//El cutoff se hace automáticamente, ya que la serie de destino tendrá un calendario de conservación con el evento cutoff planificado
			//cutoff(username, ref, req);

			ScriptNode expedientRma = new ScriptNode(new NodeRef(ref), serviceRegistry, scope);				
			model.put(FTL_EXPEDIENT, expedientRma);
			model.put(FTL_SUCCESS, String.valueOf(true));

			addAspects(expedientRma.getNodeRef(), aspects, allNodeProps);
			expedientRma.save();

			//Auditar transferir expedient
			if(typeExpArxiuActiu.equals("udl:expedient")){
				AuditUdl.auditRecord(auditComponent, username, nodeRefExpArxiuActiu, AUDIT_ACTION_TRANSFER_EXPEDIENT,  typeExpArxiuActiu, siteExpArxiuActiu);
			}
			else if(typeExpArxiuActiu.equals("udl:agregacio")){
				AuditUdl.auditRecord(auditComponent, username, nodeRefExpArxiuActiu, AUDIT_ACTION_TRANSFER_AGREGACIO,  typeExpArxiuActiu, siteExpArxiuActiu);
			}		
		}
		catch (Exception e) {	
			e.printStackTrace();
			model.put(FTL_ERROR, e.getMessage());
			model.put(FTL_SUCCESS, String.valueOf(false));		
		}
		return model;
	}*/


	/**
	 * Transfer Content Management nodes recursively to a Records Management site.
	 * @param username
	 * @param expPath
	 * @param expName
	 * @param req
	 * @return root Records Management transfered node.
	 * @throws Exception
	 */
	/*protected static String transferirNodes(String username, String expPath, String expName, WebScriptRequest req) throws Exception {
		//create HttpClient object
		HttpClient client = new HttpClient();

		//pre-authenticate with alfresco server
		client.getParams().setAuthenticationPreemptive(true);
		UdlProperties props = new UdlProperties();
		Credentials loginCreds = new UsernamePasswordCredentials(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD));

		//Use AuthScope.ANY since the HttpClient connects to just one URL
		client.getState().setCredentials(AuthScope.ANY, loginCreds);
		PostMethod post = new PostMethod(req.getServerPath() + req.getContextPath() + URL_WEBSCRIPT_TRANSFER_NODES);
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		//Alfresco only reads the POST parameters if they are in the body of the request, so we need to create a RequestEntity to populate the body.
		Part[] parts = {
				new StringPart(FORM_PARAM_USERNAME, username, UTF8), 
				new StringPart(FORM_PARAM_EXP_PATH, expPath, UTF8), 
				new StringPart(FORM_PARAM_EXP_NAME, expName, UTF8)};
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		post.setRequestEntity(entity);

		//submit the request and get the response code
		int statusCode = client.executeMethod(post);
		InputStream responseBody = post.getResponseBodyAsStream();
		String nodeRef = DocumentUtils.convertStreamToString(responseBody);

		//Check for code 200		
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception(nodeRef);
		}

		return nodeRef;
	}*/

	/**
	 * @param username
	 * @param nodeRef
	 * @param req
	 * @return
	 * @throws Exception
	 */
	/*protected static String declareRecordRecursive(String username, String nodeRef, WebScriptRequest req) throws Exception {
		//create HttpClient object
		HttpClient client = new HttpClient();

		//pre-authenticate with alfresco server
		client.getParams().setAuthenticationPreemptive(true);
		UdlProperties udlProperties = new UdlProperties();
		Credentials loginCreds = new UsernamePasswordCredentials(udlProperties.getProperty(ADMIN_USERNAME), udlProperties.getProperty(ADMIN_PASSWORD));

		//Use AuthScope.ANY since the HttpClient connects to just one URL
		client.getState().setCredentials(AuthScope.ANY, loginCreds);
		PostMethod post = new PostMethod(req.getServerPath() + req.getContextPath() + URL_WEBSCRIPT_DECLARE_RECORD);
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		//Alfresco only reads the POST parameters if they are in the body of the request, so we need to create a RequestEntity to populate the body.
		Part[] parts = {
				new StringPart(FORM_PARAM_USERNAME, username, UTF8), 
				new StringPart(FORM_PARAM_NODEREF, nodeRef, UTF8), 
				new StringPart(ADMIN_USERNAME, udlProperties.getProperty(ADMIN_USERNAME), UTF8), 
				new StringPart(ADMIN_PASSWORD, udlProperties.getProperty(ADMIN_PASSWORD), UTF8), 
				new StringPart(FORM_PARAM_SERVER, req.getServerPath(), UTF8)};
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		post.setRequestEntity(entity);

		//submit the request and get the response code
		int statusCode = client.executeMethod(post);
		InputStream responseBody = post.getResponseBodyAsStream();
		String ref = DocumentUtils.convertStreamToString(responseBody);

		//Check for code 200		
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception(ref);
		}

		return ref;
	}*/

	/**
	 * Before to run this, cutoff action must be added to the category where is placed the folder (expediente) 
	 * and all the document contained in the folder must be declared as a record.
	 * @param username
	 * @param nodeRef
	 * @param req
	 * @return
	 * @throws Exception
	 */
	/*protected static String cutoff(String username, String nodeRef, WebScriptRequest req) throws Exception {
		//create HttpClient object
		HttpClient client = new HttpClient();

		//pre-authenticate with alfresco server
		client.getParams().setAuthenticationPreemptive(true);
		UdlProperties udlProperties = new UdlProperties();
		Credentials loginCreds = new UsernamePasswordCredentials(udlProperties.getProperty(ADMIN_USERNAME), udlProperties.getProperty(ADMIN_PASSWORD));

		//Use AuthScope.ANY since the HttpClient connects to just one URL
		client.getState().setCredentials(AuthScope.ANY, loginCreds);
		PostMethod post = new PostMethod(req.getServerPath() + req.getContextPath() + URL_WEBSCRIPT_CUTOFF);
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		//Alfresco only reads the POST parameters if they are in the body of the request, so we need to create a RequestEntity to populate the body.
		Part[] parts = {
				new StringPart(FORM_PARAM_USERNAME, username, UTF8), 
				new StringPart(FORM_PARAM_NODEREF, nodeRef, UTF8), 
				new StringPart(ADMIN_USERNAME, udlProperties.getProperty(ADMIN_USERNAME), UTF8), 
				new StringPart(ADMIN_PASSWORD, udlProperties.getProperty(ADMIN_PASSWORD), UTF8), 
				new StringPart(FORM_PARAM_SERVER, req.getServerPath(), UTF8)};
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		post.setRequestEntity(entity);

		//submit the request and get the response code
		int statusCode = client.executeMethod(post);
		InputStream responseBody = post.getResponseBodyAsStream();
		String ref = DocumentUtils.convertStreamToString(responseBody);

		//Check for code 200		
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception(ref);
		}

		return ref;
	}*/

	/**
	 * Transfer the aspects of the record (from DM record to RM record).
	 * 
	 * @param nodeRef
	 * @throws Exception
	 */
	/*protected void addAspects(NodeRef nodeRef, Set<QName> aspects, Map<QName, Serializable> allNodeProps) throws Exception {
		NodeService nodeService = serviceRegistry.getNodeService();
		DictionaryService dictionaryService = serviceRegistry.getDictionaryService();
		Iterator<QName> it = aspects.iterator();

		while (it.hasNext()) {
			QName aspect = (QName) it.next();
			Map<QName, PropertyDefinition> aspectPropDefs = dictionaryService.getAspect(aspect).getProperties();
			Map<QName, Serializable> nodeProps = new HashMap<QName, Serializable>(aspectPropDefs.size());
			Iterator<QName> iter = aspectPropDefs.keySet().iterator();

			while (iter.hasNext()) {
				QName propQName = (QName) iter.next();

				if(aspect.getNamespaceURI().equals("http://www.smile.com/model/udl/1.0")) {
					Serializable value = allNodeProps.get(propQName);
					if (value != null) {
						nodeProps.put(propQName, value);
					}
				}
			}

			if(aspect.getNamespaceURI().equals("http://www.smile.com/model/udl/1.0")) {
				nodeService.addAspect(nodeRef, aspect, nodeProps);
			}
		}
	}*/
}