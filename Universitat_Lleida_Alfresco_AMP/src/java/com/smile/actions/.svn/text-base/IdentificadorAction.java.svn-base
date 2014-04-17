package com.smile.actions;

import java.net.URL;
import java.security.MessageDigest;
import java.util.List;

import javax.xml.rpc.ParameterMode;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
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

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.UdlProperties;

public class IdentificadorAction extends ActionExecuterAbstractBase implements ConstantsUdL {
	private static Log logger = LogFactory.getLog(IdentificadorAction.class);
	private ServiceRegistry serviceRegistry;

	QName idDocSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "secuencial_identificador_documentSimple");
	QName idAgregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "secuencial_identificador_agregacio");
	QName idExpedient = QName.createQName("http://www.smile.com/model/udl/1.0", "secuencial_identificador_expedient");
	QName idSerie = QName.createQName("http://www.smile.com/model/udl/1.0", "secuencial_identificador_serie");
	QName idFons = QName.createQName("http://www.smile.com/model/udl/1.0", "secuencial_identificador_fons");
	QName idGrupFons = QName.createQName("http://www.smile.com/model/udl/1.0", "secuencial_identificador_grupDeFons");
	
	QName idDocSimpleRm = QName.createQName("http://www.smile.com/model/udlrm/1.0", "secuencial_identificador_documentSimple");
	QName idAgregacioRm = QName.createQName("http://www.smile.com/model/udlrm/1.0", "secuencial_identificador_agregacio");
	QName idExpedientRm = QName.createQName("http://www.smile.com/model/udlrm/1.0", "secuencial_identificador_expedient");
	QName idSerieRm = QName.createQName("http://www.smile.com/model/udlrm/1.0", "secuencial_identificador_serie");
	QName idFonsRm = QName.createQName("http://www.smile.com/model/udlrm/1.0", "secuencial_identificador_fons");
	
	QName docSimpleRm = QName.createQName(UDLRM_URI, "documentSimple");
	QName agregacioRm = QName.createQName(UDLRM_URI, "agregacio");
	QName expedientRm = QName.createQName(UDLRM_URI, "expedient");
	QName serieRm = QName.createQName(UDLRM_URI, "serie");
	QName fonsRm = QName.createQName(UDLRM_URI, "fons");
	QName grupDeFonsRm = QName.createQName(UDLRM_URI, "grupDeFons");	

	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		try {
			logger.debug("START identificador action!");
			UdlProperties props = new UdlProperties();
			serviceRegistry.getAuthenticationService().authenticate(props.getProperty(ADMIN_USERNAME),
					props.getProperty(ADMIN_PASSWORD).toCharArray());

			NodeService nodeService = serviceRegistry.getNodeService();
			String type = nodeService.getType(nodeRef).getLocalName();
			String identificador = "";

			QName esquemaDocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "esquema_identificador_documentSimple");
			QName esquemaAgregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "esquema_identificador_agregacio");
			QName esquemaExpedient = QName.createQName("http://www.smile.com/model/udl/1.0", "esquema_identificador_expedient");
			QName esquemaSerie = QName.createQName("http://www.smile.com/model/udl/1.0", "esquema_identificador_serie");
			QName esquemaFons = QName.createQName("http://www.smile.com/model/udl/1.0", "esquema_identificador_fons");
			QName esquemaGrupDeFons = QName.createQName("http://www.smile.com/model/udl/1.0", "esquema_identificador_grupDeFons");
			
			QName esquemaDocumentSimpleRm = QName.createQName("http://www.smile.com/model/udlrm/1.0", "esquema_identificador_documentSimple");
			QName esquemaAgregacioRm = QName.createQName("http://www.smile.com/model/udlrm/1.0", "esquema_identificador_agregacio");
			QName esquemaExpedientRm = QName.createQName("http://www.smile.com/model/udlrm/1.0", "esquema_identificador_expedient");
			QName esquemaSerieRm = QName.createQName("http://www.smile.com/model/udlrm/1.0", "esquema_identificador_serie");
			QName esquemaFonsRm = QName.createQName("http://www.smile.com/model/udlrm/1.0", "esquema_identificador_fons");
						
			logger.debug("NodeRef: " + nodeRef.getId());
			
			if (DOCUMENT_SIMPLE.equalsIgnoreCase(type) && isNewNode(nodeRef, DOCUMENT_SIMPLE)) {
				identificador = callWS(DOCUMENT_SIMPLE);
				nodeService.setProperty(nodeRef, idDocSimple, identificador);
				nodeService.setProperty(nodeRef, esquemaDocumentSimple, MASCARA_DOCUMENT_SIMPLE);
				logger.debug("Document simple properties set!");

			}
			else if (nodeService.hasAspect(nodeRef, docSimpleRm) && isNewNode(nodeRef, docSimpleRm)) {
				identificador = callWS(DOCUMENT_SIMPLE);
				nodeService.setProperty(nodeRef, idDocSimpleRm, identificador);
				nodeService.setProperty(nodeRef, esquemaDocumentSimpleRm, MASCARA_DOCUMENT_SIMPLE);
				logger.debug("Document simple properties set!");

			}
			else if (AGREGACIO.equalsIgnoreCase(type) && isNewNode(nodeRef, AGREGACIO)) {
				identificador = callWS(AGREGACIO);
				nodeService.setProperty(nodeRef, idAgregacio, identificador);
				nodeService.setProperty(nodeRef, esquemaAgregacio, MASCARA_AGREGACIO);
				logger.debug("Agregacio properties set!");
			} 
			else if (nodeService.hasAspect(nodeRef, agregacioRm) && isNewNode(nodeRef, agregacioRm)) {
				identificador = callWS(AGREGACIO);
				nodeService.setProperty(nodeRef, idAgregacioRm, identificador);
				nodeService.setProperty(nodeRef, esquemaAgregacioRm, MASCARA_AGREGACIO);
				logger.debug("Agregacio properties set!");
			} 
			else if (EXPEDIENT.equalsIgnoreCase(type) && isNewNode(nodeRef, EXPEDIENT)) {
				identificador = callWS(EXPEDIENT);
				nodeService.setProperty(nodeRef, idExpedient, identificador);
				nodeService.setProperty(nodeRef, esquemaExpedient, MASCARA_EXPEDIENT);
				logger.debug("Expedient properties set!");
			}
			else if (nodeService.hasAspect(nodeRef, expedientRm) && isNewNode(nodeRef, expedientRm)) {
				identificador = callWS(EXPEDIENT);
				nodeService.setProperty(nodeRef, idExpedientRm, identificador);
				nodeService.setProperty(nodeRef, esquemaExpedientRm, MASCARA_EXPEDIENT);
				logger.debug("Expedient properties set!");
			}
			else if (SERIE.equalsIgnoreCase(type) && isNewNode(nodeRef, SERIE)) {
				identificador = callWS(SERIE);
				nodeService.setProperty(nodeRef, idSerie, identificador);
				nodeService.setProperty(nodeRef, esquemaSerie, MASCARA_SERIE);
				logger.debug("Serie properties set!");
			}
			else if (nodeService.hasAspect(nodeRef, serieRm) && isNewNode(nodeRef, serieRm)) {
				identificador = callWS(SERIE);
				nodeService.setProperty(nodeRef, idSerieRm, identificador);
				nodeService.setProperty(nodeRef, esquemaSerieRm, MASCARA_SERIE);
				logger.debug("Serie properties set!");
			}
			else if (FONS.equalsIgnoreCase(type) && isNewNode(nodeRef, FONS)) {
				identificador = callWS(FONS);
				nodeService.setProperty(nodeRef, idFons, identificador);
				nodeService.setProperty(nodeRef, esquemaFons, MASCARA_FONS);
				logger.debug("Fons properties set!");
			} 
			else if (nodeService.hasAspect(nodeRef, fonsRm) && isNewNode(nodeRef, fonsRm)) {
				identificador = callWS(FONS);
				nodeService.setProperty(nodeRef, idFonsRm, identificador);
				nodeService.setProperty(nodeRef, esquemaFonsRm, MASCARA_FONS);
				logger.debug("Fons properties set!");
			}
			else if (GRUP_DE_FONS.equalsIgnoreCase(type) && isNewNode(nodeRef, GRUP_DE_FONS)) {
				identificador = callWS(GRUP_DE_FONS);
				nodeService.setProperty(nodeRef, idGrupFons, identificador);
				nodeService.setProperty(nodeRef, esquemaGrupDeFons, MASCARA_GRUP_DE_FONS);
				logger.debug("Grup de fons properties set!");
			}
			logger.debug("END identificador action!");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error obtenint identificador via WebService.", e);
		}
	}

	/**
	 * Retorna true si el node es nou / false en cas contrari (moviment o copia
	 * de nodes)
	 * 
	 * @param type
	 * @return
	 */
	private boolean isNewNode(NodeRef nodeRef, String type) throws Exception {
		boolean result = false;
		String query = "";
		String id = "";

		if (DOCUMENT_SIMPLE.equals(type)) {
			id = (String) serviceRegistry.getNodeService().getProperty(nodeRef, idDocSimple);
			query = "@udl\\:secuencial_identificador_documentSimple:\"" + id + "\" OR @udlrm\\:secuencial_identificador_documentSimple:\"" + id + "\"";

		} else if (AGREGACIO.equals(type)) {
			id = (String) serviceRegistry.getNodeService().getProperty(nodeRef, idAgregacio);
			query = "@udl\\:secuencial_identificador_agregacio:\"" + id + "\" OR @udlrm\\:secuencial_identificador_agregacio:\"" + id + "\"";

		} else if (EXPEDIENT.equals(type)) {
			id = (String) serviceRegistry.getNodeService().getProperty(nodeRef, idExpedient);
			query = "@udl\\:secuencial_identificador_expedient:\"" + id + "\" OR @udlrm\\:secuencial_identificador_expedient:\"" + id + "\"";

		} else if (SERIE.equals(type)) {
			id = (String) serviceRegistry.getNodeService().getProperty(nodeRef, idSerie);
			query = "@udl\\:secuencial_identificador_serie:\"" + id + "\" OR @udlrm\\:secuencial_identificador_serie:\"" + id + "\"";

		} else if (FONS.equals(type)) {
			id = (String) serviceRegistry.getNodeService().getProperty(nodeRef, idFons);
			query = "@udl\\:secuencial_identificador_fons:\"" + id + "\" OR @udlrm\\:secuencial_identificador_fons:\"" + id + "\"";

		} else if (GRUP_DE_FONS.equals(type)) {
			id = (String) serviceRegistry.getNodeService().getProperty(nodeRef, idGrupFons);
			query = "@udl\\:secuencial_identificador_grupDeFons:\"" + id + "\" OR @udlrm\\:secuencial_identificador_grupDeFons:\"" + id + "\"";
		}

		StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
		ResultSet resultSet = serviceRegistry.getSearchService().query(storeRef, SearchService.LANGUAGE_LUCENE, query);

		// Un node que no té informada la metadada secuencial_identificar és un
		// nou node
		if (id == null || "".equals(id)) {
			result = true;

		} else {
			// Si la metadada está informada podem estar davant una copia o
			// moviment d'un node.
			// Si hi ha més d'un node amb el mateix identificador => copia de
			// nodes (es tracta d'un nou node)
			if (resultSet.length() > 1) {
				result = true;
			}
		}

		return result;
	}

	private boolean isNewNode(NodeRef nodeRef, QName type) throws Exception {
		boolean result = false;
		String query = "";
		String id = "";

		if (docSimpleRm.equals(type)) {
			id = (String) serviceRegistry.getNodeService().getProperty(nodeRef, idDocSimpleRm);
			query = "@udl\\:secuencial_identificador_documentSimple:\"" + id + "\" OR @udlrm\\:secuencial_identificador_documentSimple:\"" + id + "\"";

		} else if (agregacioRm.equals(type)) {
			id = (String) serviceRegistry.getNodeService().getProperty(nodeRef, idAgregacioRm);
			query = "@udl\\:secuencial_identificador_agregacio:\"" + id + "\" OR @udlrm\\:secuencial_identificador_agregacio:\"" + id + "\"";

		} else if (expedientRm.equals(type)) {
			id = (String) serviceRegistry.getNodeService().getProperty(nodeRef, idExpedientRm);
			query = "@udl\\:secuencial_identificador_expedient:\"" + id + "\" OR @udlrm\\:secuencial_identificador_expedient:\"" + id + "\"";

		} else if (serieRm.equals(type)) {
			id = (String) serviceRegistry.getNodeService().getProperty(nodeRef, idSerieRm);
			query = "@udl\\:secuencial_identificador_serie:\"" + id + "\" OR @udlrm\\:secuencial_identificador_serie:\"" + id + "\"";

		} else if (fonsRm.equals(type)) {
			id = (String) serviceRegistry.getNodeService().getProperty(nodeRef, idFonsRm);
			query = "@udl\\:secuencial_identificador_fons:\"" + id + "\" OR @udlrm\\:secuencial_identificador_fons:\"" + id + "\"";

		} else if (grupDeFonsRm.equals(type)) {
			id = (String) serviceRegistry.getNodeService().getProperty(nodeRef, idFonsRm);
			query = "@udl\\:secuencial_identificador_grupDeFons:\"" + id + "\" OR @udlrm\\:secuencial_identificador_grupDeFons:\"" + id + "\"";
		} 

		StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
		ResultSet resultSet = serviceRegistry.getSearchService().query(storeRef, SearchService.LANGUAGE_LUCENE, query);

		// Un node que no té informada la metadada secuencial_identificar és un
		// nou node
		if (id == null || "".equals(id)) {
			result = true;

		} else {
			// Si la metadada está informada podem estar davant una copia o
			// moviment d'un node.
			// Si hi ha més d'un node amb el mateix identificador => copia de
			// nodes (es tracta d'un nou node)
			if (resultSet.length() > 1) {
				result = true;
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

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		paramList.add(new ParameterDefinitionImpl("a-parameter", DataTypeDefinition.TEXT, false, getParamDisplayLabel("a-parameter")));
	}	

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
}
