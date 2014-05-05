package com.smile.webscripts.cercarExpedients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Impersonate;
import com.smile.webscripts.helper.UdlProperties;

public class CercarExpedients extends DeclarativeWebScript implements ConstantsUdL {

	private String FILTRO = "filtro";
	private String ASPECT_EXPEDIENT = "udlrm:expedient";
	private String ASPECT_AGREGACIO = "udlrm:agregacio";	
	private String UDLRM_URI = "http://www.smile.com/model/udlrm/1.0";
	private static Log logger = LogFactory.getLog(CercarExpedients.class);
	private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

		NodeService nodeService = serviceRegistry.getNodeService();
		SearchService searchService = serviceRegistry.getSearchService();
		HashMap<String, Object> model = new HashMap<String, Object>();
		List<Object> data = new ArrayList<Object>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		String filtro = req.getParameter(FILTRO);
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
	
		try{
			UdlProperties props = new UdlProperties();
			Impersonate.impersonate(props.getProperty(ADMIN_USERNAME));
			String query = buildQuery(filtro);
	
			ResultSet result = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_LUCENE, query);
			Iterator<NodeRef> iter = result.getNodeRefs().iterator();
	
			while (iter.hasNext()) {
				NodeRef nodeRef = (NodeRef) iter.next();
				ScriptNode exp = new ScriptNode(nodeRef, serviceRegistry, scope);
				map = new HashMap<String, Object>();
				
				map.put("node", exp);
				 
				if(exp.hasAspect(ASPECT_AGREGACIO)) {
					map.put("nomNatural", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "nom_natural_agregacio")));
					map.put("nomNaturalInstitucio", nodeService.getProperty(nodeRef, QName.createQName(UDL_URI, "nom_natural_institucio")));
					map.put("nomNaturalOrgan", nodeService.getProperty(nodeRef, QName.createQName(UDL_URI, "nom_natural_organ")));
					map.put("numExp", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "secuencial_identificador_agregacio")));
					map.put("dataInici", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "data_inici_agregacio")));
					map.put("dataFi", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "data_fi_agregacio")));
					map.put("codiClass", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "codi_classificacio_1_agregacio")));			
					map.put("localitzacio", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "localitzacio_1_agregacio")));					
					
 				}else if(exp.hasAspect(ASPECT_EXPEDIENT)) {
 					map.put("nomNatural", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "nom_natural_expedient")));
 					map.put("nomNaturalInstitucio", nodeService.getProperty(nodeRef, QName.createQName(UDL_URI, "nom_natural_institucio")));
					map.put("nomNaturalOrgan", nodeService.getProperty(nodeRef, QName.createQName(UDL_URI, "nom_natural_organ")));
					map.put("numExp", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "secuencial_identificador_expedient")));
					map.put("dataInici", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "data_inici_expedient")));
					map.put("dataFi", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "data_fi_expedient")));
					map.put("codiClass", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "codi_classificacio_1_expedient")));
					map.put("localitzacio", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "localitzacio_1_expedient")));					
				}
				
				data.add(map);
			}
	
			model.put("expedients", data);
			model.put("size", result.length());
			model.put("filtro", filtro);
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Error en la cerca d'expedients.", e);
		}
		
		return model;
	}

	/**
	 * Construye la parte de la consulta que contiene los metadatos dinámicamente.
	 * 
	 * @param metadata
	 * @param filtro
	 */
	String addMetadata(String metadata, String filtro) {
		String query = "";
		String[] filtroSplit= filtro.split(" ");
		int index = 0;
		
		for(int i=0; i<filtroSplit.length; i++) {
			query = query + metadata + filtroSplit[i] + "*\" OR ";
			index = i;
		}
		
		if(index > 0){
			query = StringUtils.chomp(query, " OR ");	
		}
		
		return query;
		
	}
	
	/**
	 * Contruye la consulta a realizar.
	 * 
	 * @param filtro
	 * @return
	 * @throws Exception
	 */
	private String buildQuery(String filtro) throws Exception {
		String query = "";		
		
		//String queryFons = "PATH:\"/app:company_home/st:sites/cm:rm/cm:documentLibrary/cm:Fons_x0020_Universitat_x0020_de_x0020_Lleida\"";
		String queryFons = "PATH:\"/app:company_home/st:sites/cm:rm/cm:documentLibrary/cm:Fons_x0020_Universitat_x0020_de_x0020_Lleida/*/*\"";
		
		String queryAspect = "(NOT ASPECT:\"dod:ghosted\" AND (ASPECT:\"udlrm:agregacio\" OR ASPECT:\"udlrm:expedient\"))";

		String queryMetadataAgr = "";
		queryMetadataAgr = queryMetadataAgr + addMetadata("@cm\\:name:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:tipus_entitat_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:categoria_agregacio:\"*", filtro);		
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:descripcio_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:idioma_1_agregacio:\"*", filtro);								
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:idioma_2_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:idioma_3_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:idioma_4_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:secuencial_identificador_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:esquema_identificador_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:nom_natural_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:data_inici_agregacio:\"*", filtro);		
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:data_creacio_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:data_fi_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:classificacio_acces_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:sensibilitat_dades_caracter_personal_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:classificacio_ENS_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:advertencia_seguretat_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:categoria_advertencia_seguretat_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:condicions_acces_1_agregacio:\"*", filtro);		
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:condicions_acces_2_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:tipus_acces_1_agregacio:\"*", filtro);								
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:tipus_acces_2_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:valoracio_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:tipus_dictamen_1_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:accio_dictaminada_1_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:tipus_dictamen_2_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:accio_dictaminada_2_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_origen_agregacio:\"*", filtro);		
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:dimensions_fisiques_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:quantitat_agregacio:\"*", filtro);								
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:unitats_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_1_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_2_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_3_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_4_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_5_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_6_agregacio:\"*", filtro);		
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_7_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_8_agregacio:\"*", filtro);								
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_9_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_10_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_11_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_12_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_13_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_14_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_15_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_16_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_17_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_18_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_19_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_20_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:suport_21_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:localitzacio_1_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:localitzacio_2_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:denominacio_estat_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:codi_classificacio_1_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:denominacio_classe_1_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:codi_classificacio_2_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:denominacio_classe_2_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:grup_creador_agregacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:created:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:modified:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:modifier:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udlrm\\:creator:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udl\\:nom_natural_regulacio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udl\\:nom_natural_institucio:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udl\\:nom_natural_organ:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udl\\:nom_dispositiu:\"*", filtro);
		queryMetadataAgr = queryMetadataAgr + addMetadata("@udl\\:nom_natural_persona:\"*", filtro);

		String queryMetadataExp = "";
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:grup_creador_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:tipus_entitat_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:categoria_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:descripcio_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:idioma_1_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:idioma_2_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:idioma_3_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:idioma_4_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:secuencial_identificador_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:esquema_identificador_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:nom_natural_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:data_inici_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:data_fi_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:classificacio_acces_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:sensibilitat_dades_caracter_personal_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:classificacio_ENS_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:advertencia_seguretat_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:categoria_advertencia_seguretat_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:condicions_acces_1_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:condicions_acces_2_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:tipus_acces_1_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:tipus_acces_2_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:valoracio_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:tipus_dictamen_1_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:accio_dictaminada_1_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:tipus_dictamen_2_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:accio_dictaminada_2_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_origen_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:dimensions_fisiques_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:quantitat_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:unitats_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_1_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_2_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_3_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_4_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_5_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_6_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_7_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_8_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_9_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_10_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_11_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_12_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_13_expedient:\"*", filtro);				
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_14_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_15_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_16_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_17_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_18_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_19_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_20_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:suport_21_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:localitzacio_1_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:localitzacio_2_expedient:\"*", filtro);				
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:denominacio_estat_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:codi_classificacio_1_expedient:\"*", filtro);				
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:denominacio_classe_1_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:codi_classificacio_2_expedient:\"*", filtro);				
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:denominacio_classe_2_expedient:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udl\\:nom_natural_regulacio:\"*", filtro);				
		queryMetadataExp = queryMetadataExp + addMetadata("@udl\\:nom_natural_institucio:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udl\\:nom_natural_organ:\"*", filtro);				
		queryMetadataExp = queryMetadataExp + addMetadata("@udl\\:nom_dispositiu:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udl\\:nom_natural_persona:\"*", filtro);				
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:created:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:modified:\"*", filtro);				
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:modifier:\"*", filtro);
		queryMetadataExp = queryMetadataExp + addMetadata("@udlrm\\:creator:\"*", filtro);

		if("*".equals(filtro)) {
			query = queryFons + " AND " +  queryAspect;
			
		}else {
			query = queryFons + " AND " + queryAspect + " AND (" + StringUtils.chomp(queryMetadataExp, " OR ") + " OR " + StringUtils.chomp(queryMetadataAgr, " OR ") + ")";
		}
		
		return query;
	}
}