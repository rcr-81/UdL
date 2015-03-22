package com.smile.webscripts;

import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.rpc.ParameterMode;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.smile.actions.IdentificadorAction;
import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Impersonate;
import com.smile.webscripts.helper.UdlProperties;

public class AfegirIdentificadors extends DeclarativeWebScript implements ConstantsUdL {
	private static Log logger = LogFactory.getLog(AfegirIdentificadors.class);
	
	private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public AfegirIdentificadors() {
	}

	public AfegirIdentificadors(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	private StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
	private QName idFons = QName.createQName("http://www.smile.com/model/udl/1.0", "secuencial_identificador_fons");
	private QName idSerie = QName.createQName("http://www.smile.com/model/udl/1.0", "secuencial_identificador_serie");
	private QName idExpedient = QName.createQName("http://www.smile.com/model/udl/1.0", "secuencial_identificador_expedient");
	private QName idAgregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "secuencial_identificador_agregacio");
	private QName idDocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "secuencial_identificador_documentSimple");

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		UdlProperties props = new UdlProperties();
		
		try {
			Impersonate.impersonate(props.getProperty(ADMIN_USERNAME));
			
			model.put("successFons", afegirIdFons());
			model.put("successSerie", afegirIdSerie());
			model.put("successExpedient", afegirIdExpedient());
			model.put("successAgregacio", afegirIdAgregacio());
			model.put("successDocumentSimple", afegirIdDocumentSimple());
			
		}catch(Exception e) {
			model.put("error", e.getMessage().toString());
			e.printStackTrace();
		}
		
		return model;
	}

	/**
	 * Afegir id Fons
	 * 
	 * @return
	 * @throws Exception
	 */
	private String afegirIdFons() throws Exception {
		NodeService nodeService = serviceRegistry.getNodeService();
		SearchService searchService = serviceRegistry.getSearchService();
		
		String query = "ISNULL:udl\\:secuencial_identificador_fons AND TYPE:udl\\:fons";
		
		ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, query);
		
		Iterator<NodeRef> itFons = resultSet.getNodeRefs().iterator();
		
		while (itFons.hasNext()) {
			NodeRef fonsNodeRef = (NodeRef) itFons.next();
			nodeService.setProperty(fonsNodeRef, idFons, callWS(FONS));
		}

		return resultSet.length() + " identificadors de fons actualitzats";
	}

	/**
	 * Afegir id serie
	 * 
	 * @return
	 * @throws Exception
	 */
	private String afegirIdSerie() throws Exception {
		NodeService nodeService = serviceRegistry.getNodeService();
		SearchService searchService = serviceRegistry.getSearchService();
		
		String query = "ISNULL:udl\\:secuencial_identificador_serie AND TYPE:udl\\:serie";
		
		ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, query);
		
		Iterator<NodeRef> itSerie = resultSet.getNodeRefs().iterator();
		
		while (itSerie.hasNext()) {
			NodeRef serieNodeRef = (NodeRef) itSerie.next();
			nodeService.setProperty(serieNodeRef, idSerie, callWS(SERIE));
		}

		return resultSet.length() + " identificadors de sèrie actualitzats";
	}

	/**
	 * Afegir id expedient
	 * 
	 * @return
	 * @throws Exception
	 */
	private String afegirIdExpedient() throws Exception {
		NodeService nodeService = serviceRegistry.getNodeService();
		SearchService searchService = serviceRegistry.getSearchService();
		
		String query = "ISNULL:udl\\:secuencial_identificador_expedient AND TYPE:udl\\:expedient";
		
		ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, query);
		
		Iterator<NodeRef> itExp = resultSet.getNodeRefs().iterator();
		
		while (itExp.hasNext()) {
			NodeRef expNodeRef = (NodeRef) itExp.next();
			nodeService.setProperty(expNodeRef, idExpedient, callWS(EXPEDIENT));
		}

		return resultSet.length() + "  identificadors d'expedient actualitzats";
	}

	/**
	 * Afegir id Agregació
	 * 
	 * @return
	 * @throws Exception
	 */
	private String afegirIdAgregacio() throws Exception {
		NodeService nodeService = serviceRegistry.getNodeService();
		SearchService searchService = serviceRegistry.getSearchService();
		
		String query = "ISNULL:udl\\:secuencial_identificador_agregacio AND TYPE:udl\\:agregacio";
		
		ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, query);
		
		Iterator<NodeRef> itAgr = resultSet.getNodeRefs().iterator();
		
		while (itAgr.hasNext()) {
			NodeRef agrNodeRef = (NodeRef) itAgr.next();
			nodeService.setProperty(agrNodeRef, idAgregacio, callWS(AGREGACIO));
		}

		return resultSet.length() + "  identificadors d'agregació actualitzats";
	}

	/**
	 * Afegir id Document Simple
	 * 
	 * @return
	 * @throws Exception
	 */
	private String afegirIdDocumentSimple() throws Exception {
		NodeService nodeService = serviceRegistry.getNodeService();
		SearchService searchService = serviceRegistry.getSearchService();
		
		String query = "ISNULL:udl\\:secuencial_identificador_documentSimple AND TYPE:udl\\:documentSimple";
		
		ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, query);
		
		Iterator<NodeRef> itDoc = resultSet.getNodeRefs().iterator();
		
		while (itDoc.hasNext()) {
			NodeRef docNodeRef = (NodeRef) itDoc.next();
			nodeService.setProperty(docNodeRef, idDocumentSimple, callWS(DOCUMENT_SIMPLE));
		}

		return resultSet.length() + "  identificadors de document simple actualitzats";
	}

	/**
	 * Crida un webservice de la UdL que genera un identificador en funció del
	 * tipus documental rebut com a paràmetre.
	 * 
	 * @param tipus
	 * @return
	 */
	private String callWS(String type) throws Exception {
		logger.debug("CALL WS identificador action!");
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
		logger.debug("WS identificador: " + id);
		
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
}
