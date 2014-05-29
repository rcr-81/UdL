package com.smile.webscripts.expedient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.w3c.dom.Element;

import com.smile.webscripts.helper.Arguments;
import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Impersonate;
import com.smile.webscripts.helper.UdlProperties;

public class CrearEtiquetaExpedient extends DeclarativeWebScript implements ConstantsUdL {

	private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public CrearEtiquetaExpedient() {
	}

	public CrearEtiquetaExpedient(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		NodeService nodeService = serviceRegistry.getNodeService();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		UdlProperties props = new UdlProperties();

		try {
			Impersonate.impersonate(props.getProperty(ADMIN_USERNAME));
			Element args = Arguments.getArguments(req);
			String expID = args.getElementsByTagName(FORM_PARAM_EXP_ID).item(0).getFirstChild().getNodeValue();
			NodeRef expedientNodeRef = new NodeRef(AUDIT_NODEREF_PREFIX + expID);

			String idGrupCreador = null;
			String any = null;
			String codiClassificacio1 = null;
			String denominacioClasse1 = null;
			String codiClassificacio2 = null;
			String denominacioClasse2 = null;
			String nomNaturalAgent = null;
			String nomNatural = null;
			String id = null;
			String localitzacio1 = null;
			String localitzacio2 = null;
			String stringDataInici = null;
			String stringDataFi = null;
			Boolean essencial = null;

			String tipo = nodeService.getType(expedientNodeRef).getLocalName();

			if (EXPEDIENT.equalsIgnoreCase(tipo)) {
				idGrupCreador = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDL_URI, "grup_creador_expedient"));
				codiClassificacio1 = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDL_URI, "codi_classificacio_1_expedient"));
				denominacioClasse1 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "denominacio_classe_1_expedient"));
				codiClassificacio2 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "codi_classificacio_2_expedient"));
				denominacioClasse2 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "denominacio_classe_2_expedient"));

				Date dataInici = (Date) nodeService.getProperty(expedientNodeRef, QName.createQName(UDL_URI, "data_inici_expedient"));
				if (dataInici != null) {
					stringDataInici = df.format(dataInici);
				}
				
				Date dataFi = (Date) nodeService.getProperty(expedientNodeRef, QName.createQName(UDL_URI, "data_fi_expedient"));
				if (dataFi != null) {
					stringDataFi = df.format(dataFi);
				}

				any = getAny(stringDataInici, stringDataFi);
				
				if(nodeService.hasAspect(expedientNodeRef, QName.createQName(UDL_URI, "persona"))) {
					nomNaturalAgent = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "nom_natural_persona"));

				}else if(nodeService.hasAspect(expedientNodeRef, QName.createQName(UDL_URI, "organ"))) {
					nomNaturalAgent = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "nom_natural_organ"));
					
				}else if(nodeService.hasAspect(expedientNodeRef, QName.createQName(UDL_URI, "institucio"))) {
					nomNaturalAgent = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "nom_natural_institucio"));
				}

				nomNatural = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(CM_URI, "name"));
				id = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "secuencial_identificador_expedient"));
				localitzacio1 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "localitzacio_1_expedient"));
				localitzacio2 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "localitzacio_2_expedient"));

			}else if(RECORD_FOLDER.equalsIgnoreCase(tipo)) {
				idGrupCreador = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDLRM_URI, "grup_creador_expedient"));
				codiClassificacio1 = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDLRM_URI, "codi_classificacio_1_expedient"));
				denominacioClasse1 = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDLRM_URI, "denominacio_classe_1_expedient"));
				codiClassificacio2 = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDLRM_URI, "codi_classificacio_2_expedient"));
				denominacioClasse2 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDLRM_URI, "denominacio_classe_2_expedient"));

				Date dataInici = (Date) nodeService.getProperty(expedientNodeRef, QName.createQName(UDLRM_URI, "data_inici_expedient"));
				if (dataInici != null) {
					stringDataInici = df.format(dataInici);
				}
				
				Date dataFi = (Date) nodeService.getProperty(expedientNodeRef, QName.createQName(UDLRM_URI, "data_fi_expedient"));
				if (dataFi != null) {
					stringDataFi = df.format(dataFi);
				}

				any = getAny(stringDataInici, stringDataFi);
				
				if(nodeService.hasAspect(expedientNodeRef, QName.createQName(UDL_URI, "persona"))) {
					nomNaturalAgent = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "nom_natural_persona"));

				}else if(nodeService.hasAspect(expedientNodeRef, QName.createQName(UDL_URI, "organ"))) {
					nomNaturalAgent = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "nom_natural_organ"));
					
				}else if(nodeService.hasAspect(expedientNodeRef, QName.createQName(UDL_URI, "institucio"))) {
					nomNaturalAgent = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "nom_natural_institucio"));
				}
				
				nomNatural = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(CM_URI, "name"));
				id = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDLRM_URI, "secuencial_identificador_expedient"));
				localitzacio1 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDLRM_URI, "localitzacio_1_expedient"));
				localitzacio2 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDLRM_URI, "localitzacio_2_expedient"));
				essencial = (Boolean) nodeService.getProperty(expedientNodeRef,	QName.createQName(RM_URI, "vitalRecordIndicator"));
			}
			
			model.put(PARAM_GRUP_CREADOR_DESC, getDescripcioGrupCreador(idGrupCreador));
			model.put(PARAM_GRUP_CREADOR_ID, idGrupCreador);
			model.put(PARAM_ANY, any);
			model.put(PARAM_CODI_CLASSIFICACIO_1, codiClassificacio1);
			model.put(PARAM_DENOMINACIO_CLASSE_1, denominacioClasse1);			
			model.put(PARAM_CODI_CLASSIFICACIO_2, codiClassificacio2);
			model.put(PARAM_DENOMINACIO_CLASSE_2, denominacioClasse2);
			model.put(PARAM_DATA_INICI, stringDataInici);
			model.put(PARAM_DATA_FI, stringDataFi);
			model.put(PARAM_NOM_NATURAL_AGENT, nomNaturalAgent);
			model.put(PARAM_NOM_NATURAL, nomNatural);
			model.put(PARAM_ID, id);
			model.put(PARAM_LOCALITZACIO_1, localitzacio1);
			model.put(PARAM_LOCALITZACIO_2, localitzacio2);
			model.put(PARAM_ESSENCIAL, essencial);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	/**
	 * Retorna la descripció del grup creador consultant la llista de dades GrupCreador
	 * 
	 * @param paramId
	 * @return
	 */
	private String getDescripcioGrupCreador(String paramId) {
		String result = "";
		SearchService searchService = serviceRegistry.getSearchService();
		NodeService nodeService = serviceRegistry.getNodeService();
		String query = "PATH:\"/app:company_home/st:sites/cm:dm/cm:dataLists/cm:GrupCreador\"";
		StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
		ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, query);

		if (resultSet.length() > 0){
			NodeRef dl = resultSet.getNodeRef(0);

			for(ChildAssociationRef ca : nodeService.getChildAssocs(dl)){
				NodeRef elem = ca.getChildRef();
				String id = (String)nodeService.getProperty(elem, QName.createQName("http://www.alfresco.org/model/datalist/1.0", "identificador_grup"));
				
				if(id.equalsIgnoreCase(paramId)) {
					result = (String)nodeService.getProperty(elem, QName.createQName("http://www.alfresco.org/model/datalist/1.0", "descripcio_grup"));					
				}
			}
		}

		return result;
	}
	
	/**
	 * Retorna l'any de l'expedient.
	 * Si la data d'inici i de fi de l'expedient son ens anys diferents, s'indiquen els dos anys separats per un guió.
	 * 
	 * @param dataInici
	 * @param dataFi
	 * @return
	 */
	private String getAny(String dataInici, String dataFi) {
		String result = "";
		String anyInici = dataInici.split("/")[2];
		String anyFi = dataFi.split("/")[2];
		
		if(anyInici.equalsIgnoreCase(anyFi)) {
			result = anyInici;

		}else {
			result = anyInici + " - " + anyFi;			
		}
		
		return result;
	}
}
