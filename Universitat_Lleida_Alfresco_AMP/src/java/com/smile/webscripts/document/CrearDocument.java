package com.smile.webscripts.document;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.audit.AuditComponent;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.antlr.grammar.v3.ANTLRv3Parser.exceptionGroup_return;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.w3c.dom.Element;

import com.smile.webscripts.audit.AuditUdl;
import com.smile.webscripts.helper.Arguments;
import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Impersonate;

public class CrearDocument extends DeclarativeWebScript implements ConstantsUdL {

	private ServiceRegistry serviceRegistry;
	private AuditComponent auditComponent;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	public void setAuditComponent(AuditComponent auditComponent) {
		this.auditComponent = auditComponent;
	}

	/**
	 * Upload an encoded Base64 file to the repository and decode it back once it's uploaded.
	 */
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		HashMap<String, Object> model = new HashMap<String,Object>();
		UserTransaction trx = serviceRegistry.getTransactionService().getUserTransaction();
		
		try {
			trx.begin();
			System.out.println(trx.hashCode());
			Element args = Arguments.getArguments(req);
			String username = args.getElementsByTagName(FORM_PARAM_USERNAME).item(0).getFirstChild().getNodeValue();		

			model.put(FTL_USERNAME, username);			
			Impersonate.impersonate(username);

			String ref = DocumentUtils.pujarDocumentBase64(req, args, username).trim();
			NodeRef nodeRef = new NodeRef(ref);

			Map<QName, Serializable> props = serviceRegistry.getNodeService().getProperties(nodeRef);
			ContentReader reader = serviceRegistry.getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);
			byte[] contentDecoded = es.mityc.firmaJava.libreria.utilidades.Base64.decode(reader.getContentString());
			ContentWriter writer = serviceRegistry.getContentService().getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
			writer.putContent(new ByteArrayInputStream(contentDecoded));	
			
			serviceRegistry.getOwnableService().setOwner(nodeRef, username);			
			
			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();
			ScriptNode document = new ScriptNode(nodeRef, serviceRegistry, scope);
			
			model.put(FTL_DOCUMENT, document);
			model.put(FTL_SUCCESS, String.valueOf(true));

			//Auditar creación de documento	
			String type = document.getTypeShort();
			String site = document.getSiteShortName();
			if(type.equals("udl:documentSimple")){
				AuditUdl.auditRecord(auditComponent, username, document.getNodeRef().toString(),AUDIT_ACTION_CREATE_DOCUMENT_SIMPLE, type, site);
				QName qNameIdDocSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "secuencial_identificador_documentSimple");
				String idDocSimple = (String)serviceRegistry.getNodeService().getProperty(nodeRef, qNameIdDocSimple);
				
				if("".equals(idDocSimple) || idDocSimple == null) {
					//serviceRegistry.getNodeService().deleteNode(nodeRef);
					throw new Exception("Error obtenint identificador via WebService.");
				}
			}
			
			trx.commit();

		}catch (Exception e) {	
			e.printStackTrace();
			model.put(FTL_ERROR, e.getMessage());
			model.put(FTL_SUCCESS, String.valueOf(false));
			
			try {
				if (trx.getStatus() == javax.transaction.Status.STATUS_ACTIVE) {
					System.out.println(trx.hashCode());
					trx.rollback();
				}
			} catch (SystemException ex) {
				e.printStackTrace();
			}
		}	

		return model;
	}
}