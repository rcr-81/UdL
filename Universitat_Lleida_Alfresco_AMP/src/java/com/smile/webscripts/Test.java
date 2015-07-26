package com.smile.webscripts;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.rpc.ParameterMode;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.OwnableService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.smile.webscripts.document.DocumentUtils;
import com.smile.webscripts.expedient.InfoTransferencia;
import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.UdlProperties;

import es.cesca.alfresco.util.CescaUtil;

public class Test extends DeclarativeWebScript implements ConstantsUdL {
	private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public Test() {
	}

	public Test(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	private StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
	private QName suportOrigenExp = QName.createQName("http://www.smile.com/model/udlrm/1.0", "suport_origen_expedient");
	private QName aspectDocumentSimple = QName.createQName("http://www.smile.com/model/udlrm/1.0", "documentSimple");
	public static QName ASPECT_DECLARED_RECORD = QName.createQName(RM_URI, "declaredRecord");
	public static QName PROP_DECLARED_AT = QName.createQName(RM_URI, "declaredAt");
	public static QName PROP_DECLARED_BY = QName.createQName(RM_URI, "declaredBy");

	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Properties propiedades = new Properties();
		HashMap<String, Object> model = new HashMap<String, Object>();
		NodeService nodeService = serviceRegistry.getNodeService();
		SearchService searchService = serviceRegistry.getSearchService();
		
		try{
//			InputStream entrada = new FileInputStream("/opt/alfresco-3.4.13/tomcat/test.properties");
//			InputStream entrada = new FileInputStream("/home/racor/Documentos/test.properties");
//			propiedades.load(entrada);
//			String nodeRef = (String) propiedades.get("nodeRef");
			
			//String query = "ISNULL:\"udlrm:suport_origen_expedient\" AND TYPE:\"rma:recordFolder\" AND ASPECT:\"udlrm:expedient\"";
			//String query = "PARENT:\"workspace://SpacesStore/fd6aa21f-0217-450f-9433-38cc4cbd8229\" AND ISNULL:\"udlrm:suport_origen_expedient\" AND TYPE:\"rma:recordFolder\" AND ASPECT:\"udlrm:expedient\"";
			//String query = "PARENT:\"" + nodeRef + "\"" + " AND TYPE:\"rma:recordFolder\" AND ASPECT:\"udlrm:expedient\" AND @udlrm\\:suport_origen_expedient:\"Físic\"";
			String query = "ASPECT:\"udl:iarxiu\"";
			
//			String query = propiedades.getProperty("query");
			System.out.println("query: " + query);
			System.out.println("");
			
			ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, query);
			
			int i = 1;
			NodeRef expNodeRef = null;
			Iterator<NodeRef> it = resultSet.getNodeRefs().iterator();
			
			while (it.hasNext()) {
				expNodeRef = (NodeRef) it.next();
				
				try {
					//createIndex(nodeService, expNodeRef, i, req);
					updateIArxiuMetadata(nodeService, i, expNodeRef);
					i++;

				}catch(Exception e) {
//					System.out.println("ERROR: Seguramente ya existe un index.xml");
					e.printStackTrace();
				}

//				setExpedientFisic(nodeService, i, expNodeRef);
			}
			model.put("success", "OK");
			
		}catch(Exception e) {
			model.put("error", e.getMessage());
		}
		
		return model;
	}

	private void updateIArxiuMetadata(NodeService nodeService, int i, NodeRef expNodeRef) throws Exception {
		System.out.println("EXPEDIENT: " + i);
		
		if(nodeService.getProperty(expNodeRef, ConstantsUdL.UDL_PETICIO_PROP_ESTAT) == null 
				|| "".equals(nodeService.getProperty(expNodeRef, ConstantsUdL.UDL_PETICIO_PROP_ESTAT))) {
			
//			System.out.println("ESTAT: " + nodeService.getProperty(expNodeRef, ConstantsUdL.UDL_PETICIO_PROP_ESTAT));
			nodeService.setProperty(expNodeRef, ConstantsUdL.UDL_PETICIO_PROP_ESTAT, CescaUtil.STATUS_TRANSFERIT);	
		}
		
		//if(nodeService.getProperty(expNodeRef, ConstantsUdL.DATA_TRANSFERENCIA) == null
		//		|| "".equals(nodeService.getProperty(expNodeRef, ConstantsUdL.DATA_TRANSFERENCIA))) {

		Date fecha = (Date)nodeService.getProperty(expNodeRef, ConstantsUdL.DATA_TRANSFERENCIA);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaString = sdf.format(fecha);
		
		System.out.println("fechaString: " + fechaString);
		
		if("01/08/2015".equalsIgnoreCase(fechaString)) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2015);
			cal.set(Calendar.MONTH, 6);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			
			System.out.println("New DATE: " + sdf.format(cal.getTime()));
					
//			System.out.println("DATA TRANSFERENCIA: " + nodeService.getProperty(expNodeRef, ConstantsUdL.DATA_TRANSFERENCIA));
			nodeService.setProperty(expNodeRef, ConstantsUdL.DATA_TRANSFERENCIA, cal.getTime());
		}
	}
	
	private void setExpedientFisic(NodeService nodeService, int i, NodeRef expNodeRef) throws Exception {
		nodeService.setProperty(expNodeRef, suportOrigenExp, "Físic");
		System.out.println(i++ + ": Metadada suport orígen actualitzada (Expedient: " + expNodeRef.getId().toString() + ")");
	}

	private void createIndex(NodeService nodeService, NodeRef expNodeRef, int i, WebScriptRequest req) throws Exception {
		
		String query = "PARENT:\"" + expNodeRef.toString() + "\"" + " AND @cm\\:name:\"index.xml\"";
//		System.out.println("query: " + query);
		ResultSet resultSet = serviceRegistry.getSearchService().query(storeRef, SearchService.LANGUAGE_LUCENE, query);
		
		// Si no hay índice lo creo
		if(resultSet.length() < 1) {
			NodeRef indexNodeRef = serviceRegistry.getFileFolderService().create(expNodeRef, "index.xml", ContentModel.TYPE_CONTENT).getNodeRef();
			nodeService.addAspect(indexNodeRef, aspectDocumentSimple, new HashMap<QName, Serializable>());
			
	//		if(nodeService.getChildAssocs(expNodeRef).size() < 1) {
				InfoTransferencia infoTransferencia = new InfoTransferencia();
				infoTransferencia.setFoliacio("<expedient>\n<nom>"
						+ nodeService.getProperty(indexNodeRef, QName.createQName(CM_URI, "name")) + "</nom>\n<id>"
						+ nodeService.getProperty(expNodeRef, QName.createQName(UDLRM_URI, "secuencial_identificador_expedient"))
						+ "</id>\n<idIntern>" + indexNodeRef.getId() + "</idIntern>\n" + "<creador>"
						+ nodeService.getProperty(indexNodeRef, QName.createQName(CM_URI, "creator")) + "</creador>\n"
						+ "<dataCreacio>" + nodeService.getProperty(indexNodeRef, QName.createQName(CM_URI, "created"))
						+ "</dataCreacio>\n" + "<modificador>"
						+ nodeService.getProperty(indexNodeRef, QName.createQName(CM_URI, "modifier")) + "</modificador>\n"
						+ "<dataModificacio>" + nodeService.getProperty(indexNodeRef, QName.createQName(CM_URI, "modified"))
						+ "</dataModificacio>\n" + "</expedient>\n");
				
				infoTransferencia.setFoliacio("<indexTransferencia>" + infoTransferencia.getFoliacio() + "</indexTransferencia>");
				
				ContentWriter writer = serviceRegistry.getContentService().getWriter(indexNodeRef, QName.createQName(CM_URI, "content"), true);
				writer.setEncoding(UTF8);
				writer.setMimetype("text/xml");
				writer.putContent(infoTransferencia.getFoliacio());
				Map<QName, Serializable> props = new HashMap<QName, Serializable>();
				props.put(QName.createQName(CM_URI, "name"), "index.xml");
				props.put(QName.createQName(CM_URI, "title"), "index.xml");
				props.put(QName.createQName(RM_URI, "originator"), "system");
				props.put(QName.createQName(RM_URI, "originatingOrganization"), "system");
				props.put(QName.createQName(RM_URI, "publicationDate"), new Date());
				props.put(QName.createQName(RM_URI, "identifier"), indexNodeRef.getId());
				props.put(QName.createQName(RM_URI, "dateFiled"), new Date());
				nodeService.addProperties(indexNodeRef, props);
				
				props.put(QName.createQName(CM_URI, "name"), "index.xml");
				props.put(ContentModel.PROP_CREATOR, "System");
				props.put(ContentModel.PROP_CREATED, new Date());
				props.put(ContentModel.PROP_MODIFIER, "System");
				props.put(ContentModel.PROP_MODIFIED, new Date());
				props.put(QName.createQName(UDLRM_URI, "grup_creador_documentSimple"), "system");
				props.put(QName.createQName(UDLRM_URI, "tipus_documental_documentSimple"), "U01 INFORME");
				props.put(QName.createQName(UDLRM_URI, "tipus_entitat_documentSimple"), "Document");
				props.put(QName.createQName(UDLRM_URI, "categoria_documentSimple"), "Document simple");
				props.put(QName.createQName(UDLRM_URI, "secuencial_identificador_documentSimple"), callWS(DOCUMENT_SIMPLE));
	//			props.put(QName.createQName(UDLRM_URI, "secuencial_identificador_documentSimple"), "id de test");
				props.put(QName.createQName(UDLRM_URI, "esquema_identificador_documentSimple"), MASCARA_DOCUMENT_SIMPLE);
				props.put(QName.createQName(UDLRM_URI, "data_inici_documentSimple"), new Date());
				props.put(QName.createQName(UDLRM_URI, "data_fi_documentSimple"), new Date());
				props.put(QName.createQName(UDLRM_URI, "data_registre_entrada_documentSimple"), new Date());
				props.put(QName.createQName(UDLRM_URI, "data_registre_sortida_documentSimple"), new Date());
				props.put(QName.createQName(UDLRM_URI, "classificacio_acces_documentSimple"), "Públic");
				props.put(QName.createQName(UDLRM_URI, "advertencia_seguretat_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "categoria_advertencia_seguretat_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "sensibilitat_dades_caracter_personal_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "verificacio_integritat_algorisme_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "verificacio_integritat_valor_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "idioma_1_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "idioma_2_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "valoracio_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "tipus_dictamen_1_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "tipus_dictamen_2_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "accio_dictaminada_1_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "accio_dictaminada_2_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "suport_origen_documentSimple"), "Electrònic");
				props.put(QName.createQName(UDLRM_URI, "versio_format_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "nom_aplicacio_creacio_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "versio_aplicacio_creacio_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "registre_formats_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "resolucio_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "dimensions_fisiques_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "unitats_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "suport_1_documentSimple"), "Electrònic");
				props.put(QName.createQName(UDLRM_URI, "localitzacio_1_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "localitzacio_2_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "denominacio_estat_documentSimple"), "Original");
				props.put(QName.createQName(UDLRM_URI, "tipus_copia_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "motiu_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "codi_classificacio_1_documentSimple"), "-");
				props.put(QName.createQName(UDLRM_URI, "codi_classificacio_2_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "denominacio_classe_1_documentSimple"), "");
				props.put(QName.createQName(UDLRM_URI, "denominacio_classe_2_documentSimple"), "");
				nodeService.addAspect(indexNodeRef, QName.createQName(UdlProperties.UDLRM_URI, "documentSimple"), props);
				
				PermissionService permissionService = serviceRegistry.getPermissionService();
				permissionService.setInheritParentPermissions(indexNodeRef, false);
				permissionService.setPermission(indexNodeRef, permissionService.getAllAuthorities(), PermissionService.CONSUMER, true);
				serviceRegistry.getOwnableService().setOwner(indexNodeRef, "admin");
	
				declareRecord(indexNodeRef, serviceRegistry);
				//uncutoff("admin", expNodeRef.toString(), req);
				//cutoff("admin", expNodeRef.toString(), req);
				
				System.out.println(i + ": Índex creat (Expedient: " + expNodeRef.getId().toString() + ") ");
		}
//		}
	}
	
	/**
	 * Marks the record or record folder as declared record
	 * 
	 * @param nodeRef
	 * @throws DipuLleidaException
	 */
	static protected void declareRecord(NodeRef nodeRef, ServiceRegistry serviceRegistry) throws Exception {
		List<String> missingProperties = new ArrayList<String>();
		NodeService nodeService = serviceRegistry.getNodeService();
		OwnableService ownableService = serviceRegistry.getOwnableService();
		if (nodeService.hasAspect(nodeRef, ASPECT_DECLARED_RECORD) == false) {
			boolean isMandatoryPropertiesSet = mandatoryPropertiesSet(nodeRef, missingProperties, serviceRegistry);
			if (isMandatoryPropertiesSet == true) {
				Map<QName, Serializable> decRecProps = new HashMap<QName, Serializable>(1);
				decRecProps.put(PROP_DECLARED_AT, new Date());
				decRecProps.put(PROP_DECLARED_BY, AuthenticationUtil.getRunAsUser());
				nodeService.addAspect(nodeRef, ASPECT_DECLARED_RECORD, decRecProps);
				ownableService.setOwner(nodeRef, OwnableService.NO_OWNER);
			} else {
				throw new Exception("Faltan las siguientes propiedades obligatorias: " + missingProperties);
			}
		}
	}
	
	/**
	 * Return true if all mandatory properties are completed
	 * 
	 * @param nodeRef
	 * @param missingProperties
	 * @return
	 */
	static protected boolean mandatoryPropertiesSet(NodeRef nodeRef, List<String> missingProperties, ServiceRegistry serviceRegistry) {
		boolean result = true;
		NodeService nodeService = serviceRegistry.getNodeService();
		DictionaryService dictionaryService = serviceRegistry.getDictionaryService();
		Map<QName, Serializable> nodeRefProps = nodeService.getProperties(nodeRef);
		QName nodeRefType = nodeService.getType(nodeRef);
		TypeDefinition typeDef = dictionaryService.getType(nodeRefType);

		for (PropertyDefinition propDef : typeDef.getProperties().values()) {
			if (propDef.isMandatory() == true) {
				if (nodeRefProps.get(propDef.getName()) == null) {
					missingProperties.add(propDef.getName().toString());
					result = false;
					break;
				}
			}
		}

		if (result != false) {
			Set<QName> aspects = nodeService.getAspects(nodeRef);
			for (QName aspect : aspects) {
				AspectDefinition aspectDef = dictionaryService.getAspect(aspect);
				for (PropertyDefinition propDef : aspectDef.getProperties().values()) {
					if (propDef.isMandatory() == true) {
						if (nodeRefProps.get(propDef.getName()) == null) {
							missingProperties.add(propDef.getName().toString());
							result = false;
							break;
						}
					}
				}
			}
		}

		return result;
	}
	
	/**
	 * Crida un webservice de la UdL que genera un identificador en funció del
	 * tipus documental rebut com a paràmetre.
	 * 
	 * @param tipus
	 * @return
	 */
	private String callWS(String type) throws Exception {
		String id = "";
		Service service = new Service();
		Call call = (Call) service.createCall();

		// Call to first service (IniciaGeneraCodi)
		call.setProperty(Call.SOAPACTION_USE_PROPERTY, new Boolean(true));
		call.setProperty(Call.SOAPACTION_URI_PROPERTY, "http://tempuri.org/IniciaGeneraCodi");
		call.setTargetEndpointAddress(new URL(WS_URL));
		call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/", WS_INICI_GENERA_CODI));
		call.addParameter(new javax.xml.namespace.QName("http://tempuri.org/", "AppId"), XMLType.XSD_STRING, ParameterMode.IN);
		call.addParameter(new javax.xml.namespace.QName("http://tempuri.org/", "ObjectId"), XMLType.XSD_STRING, ParameterMode.IN);
		call.setReturnType(XMLType.XSD_STRING);

		id = (String) call.invoke(new Object[] { WS_APP_ID, type });

		// Call to second service (GeneraCodi)
		call = (Call) service.createCall();
		call.setProperty(Call.SOAPACTION_URI_PROPERTY, "http://tempuri.org/GeneraCodi");
		call.setTargetEndpointAddress(new URL(WS_URL));
		call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/", WS_GENERA_CODI));
		call.addParameter(new javax.xml.namespace.QName("http://tempuri.org/", "Key"), XMLType.XSD_STRING, ParameterMode.IN);
		call.setReturnType(XMLType.XSD_STRING);

		id = (String) call.invoke(new Object[] { encodeKey(id) });
		
		return id;
	}
	
	/**
	 * Built encoded key with SHA2 algorithm.
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private String encodeKey(String id) throws Exception {
		String cadenaConfianza = "kax9fejew2wzA89BSDFG.JAe9edg9";
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		StringBuffer hexString = new StringBuffer();

		String key = id + cadenaConfianza;
		byte[] hash = digest.digest(key.getBytes("UTF-8"));

		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}

			hexString.append(hex);
		}

		return hexString.toString();
	}
	
	/**
	 * Before to run this, cutoff action must be added to the category where is
	 * placed the folder (expediente) and all the document contained in the
	 * folder must be declared as a record.
	 * 
	 * @param username
	 * @param nodeRef
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected static String cutoff(String username, String nodeRef, WebScriptRequest req) throws Exception {
		// create HttpClient object
		HttpClient client = new HttpClient();

		// pre-authenticate with alfresco server
		client.getParams().setAuthenticationPreemptive(true);
		UdlProperties udlProperties = new UdlProperties();
		Credentials loginCreds = new UsernamePasswordCredentials(udlProperties.getProperty(ADMIN_USERNAME),
				udlProperties.getProperty(ADMIN_PASSWORD));

		// Use AuthScope.ANY since the HttpClient connects to just one URL
		client.getState().setCredentials(AuthScope.ANY, loginCreds);
		PostMethod post = new PostMethod(req.getServerPath() + req.getContextPath() + URL_WEBSCRIPT_CUTOFF);
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		// Alfresco only reads the POST parameters if they are in the body of
		// the request, so we need to create a RequestEntity to populate the
		// body.
		Part[] parts = { new StringPart(FORM_PARAM_USERNAME, username, UTF8), new StringPart(FORM_PARAM_NODEREF, nodeRef, UTF8),
				new StringPart(ADMIN_USERNAME, udlProperties.getProperty(ADMIN_USERNAME), UTF8),
				new StringPart(ADMIN_PASSWORD, udlProperties.getProperty(ADMIN_PASSWORD), UTF8),
				new StringPart(FORM_PARAM_SERVER, req.getServerPath(), UTF8) };
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		post.setRequestEntity(entity);

		// submit the request and get the response code
		int statusCode = client.executeMethod(post);
		InputStream responseBody = post.getResponseBodyAsStream();
		String ref = DocumentUtils.convertStreamToString(responseBody);

		// Check for code 200
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception(ref);
		}

		return ref;
	}
	
	/**
	 * Before to run this, cutoff action must be added to the category where is
	 * placed the folder (expediente) and all the document contained in the
	 * folder must be declared as a record.
	 * 
	 * @param username
	 * @param nodeRef
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected static String uncutoff(String username, String nodeRef, WebScriptRequest req) throws Exception {
		// create HttpClient object
		HttpClient client = new HttpClient();

		// pre-authenticate with alfresco server
		client.getParams().setAuthenticationPreemptive(true);
		UdlProperties udlProperties = new UdlProperties();
		Credentials loginCreds = new UsernamePasswordCredentials(udlProperties.getProperty(ADMIN_USERNAME),
				udlProperties.getProperty(ADMIN_PASSWORD));

		// Use AuthScope.ANY since the HttpClient connects to just one URL
		client.getState().setCredentials(AuthScope.ANY, loginCreds);
		PostMethod post = new PostMethod(req.getServerPath() + req.getContextPath() + "/service/udl/helper/uncutoff");
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		// Alfresco only reads the POST parameters if they are in the body of
		// the request, so we need to create a RequestEntity to populate the
		// body.
		Part[] parts = { new StringPart(FORM_PARAM_USERNAME, username, UTF8), new StringPart(FORM_PARAM_NODEREF, nodeRef, UTF8),
				new StringPart(ADMIN_USERNAME, udlProperties.getProperty(ADMIN_USERNAME), UTF8),
				new StringPart(ADMIN_PASSWORD, udlProperties.getProperty(ADMIN_PASSWORD), UTF8),
				new StringPart(FORM_PARAM_SERVER, req.getServerPath(), UTF8) };
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		post.setRequestEntity(entity);

		// submit the request and get the response code
		int statusCode = client.executeMethod(post);
		InputStream responseBody = post.getResponseBodyAsStream();
		String ref = DocumentUtils.convertStreamToString(responseBody);

		// Check for code 200
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception(ref);
		}

		return ref;
	}
}
