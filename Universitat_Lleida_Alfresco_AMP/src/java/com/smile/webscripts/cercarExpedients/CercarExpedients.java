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

		
		String queryMetadataAgr = "@cm\\:name:\"*" + filtro + "*\"" +
									" OR @udlrm\\:tipus_entitat_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:categoria_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:descripcio_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:idioma_1_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:idioma_2_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:idioma_3_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:idioma_4_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:secuencial_identificador_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:esquema_identificador_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:nom_natural_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:data_inici_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:data_creacio_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:data_fi_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:classificacio_acces_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:sensibilitat_dades_caracter_personal_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:classificacio_ENS_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:advertencia_seguretat_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:categoria_advertencia_seguretat_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:condicions_acces_1_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:condicions_acces_2_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:tipus_acces_1_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:tipus_acces_2_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:valoracio_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:tipus_dictamen_1_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:accio_dictaminada_1_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:tipus_dictamen_2_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:accio_dictaminada_2_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_origen_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:dimensions_fisiques_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:quantitat_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:unitats_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_1_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_2_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_3_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_4_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_5_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_6_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_7_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_8_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_9_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_10_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_11_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_12_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_13_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_14_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_15_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_16_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_17_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_18_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_19_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_20_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_21_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:localitzacio_1_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:localitzacio_2_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:denominacio_estat_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:codi_classificacio_1_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:denominacio_classe_1_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:codi_classificacio_2_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:denominacio_classe_2_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:grup_creador_agregacio:\"*" + filtro + "*\"" +
									" OR @udlrm\\:created:\"*" + filtro + "*\"" +
									" OR @udlrm\\:modified:\"*" + filtro + "*\"" +
									" OR @udlrm\\:modifier:\"*" + filtro + "*\"" +
									" OR @udlrm\\:creator:\"*" + filtro + "*\"" +
									" OR @udl\\:nom_natural_regulacio:\"*" + filtro + "*\"" +
									" OR @udl\\:nom_natural_institucio:\"*" + filtro + "*\"" +
									" OR @udl\\:nom_natural_organ:\"*" + filtro + "*\"" +
									" OR @udl\\:nom_dispositiu:\"*" + filtro + "*\"" +
									" OR @udl\\:nom_natural_persona:\"*" + filtro + "*\"";
		
		String queryMetadataExp = " @cm\\:name:\"*" + filtro + "*\"" +
									" OR @udlrm\\:grup_creador_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:tipus_entitat_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:categoria_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:descripcio_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:idioma_1_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:idioma_2_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:idioma_3_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:idioma_4_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:secuencial_identificador_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:esquema_identificador_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:nom_natural_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:data_inici_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:data_fi_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:classificacio_acces_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:sensibilitat_dades_caracter_personal_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:classificacio_ENS_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:advertencia_seguretat_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:categoria_advertencia_seguretat_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:condicions_acces_1_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:condicions_acces_2_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:tipus_acces_1_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:tipus_acces_2_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:valoracio_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:tipus_dictamen_1_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:accio_dictaminada_1_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:tipus_dictamen_2_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:accio_dictaminada_2_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_origen_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:dimensions_fisiques_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:quantitat_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:unitats_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_1_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_2_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_3_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_4_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_5_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_6_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_7_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_8_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_9_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_10_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_11_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_12_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_13_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_14_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_15_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_16_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_17_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_18_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_19_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_20_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:suport_21_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:localitzacio_1_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:localitzacio_2_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:denominacio_estat_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:codi_classificacio_1_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:denominacio_classe_1_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:codi_classificacio_2_expedient:\"*" + filtro + "*\"" +
									" OR @udlrm\\:denominacio_classe_2_expedient:\"*" + filtro + "*\"" +
									" OR @udl\\:nom_natural_regulacio:\"*" + filtro + "*\"" +
									" OR @udl\\:nom_natural_institucio:\"*" + filtro + "*\"" +
									" OR @udl\\:nom_natural_organ:\"*" + filtro + "*\"" +
									" OR @udl\\:nom_dispositiu:\"*" + filtro + "*\"" +
									" OR @udl\\:nom_natural_persona:\"*" + filtro + "*\"" +
									" OR @udlrm\\:created:\"*" + filtro + "*\"" +
									" OR @udlrm\\:modified:\"*" + filtro + "*\"" +
									" OR @udlrm\\:modifier:\"*" + filtro + "*\"" +
									" OR @udlrm\\:creator:\"*" + filtro + "*\"";

		/*
		String queryMetadataExp = " @cm\\:name:\"" + filtro + "\"" +
								  " OR @udlrm\\:tipus_entitat_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:categoria_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:secuencial_identificador_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:esquema_identificador_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:nom_natural_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:data_inici_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:data_fi_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:descripcio_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:classificacio_acces_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:codi_politica_control_acces_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:sensibilitat_dades_caracter_personal_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:tipus_acces_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:idioma_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:valoracio_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:tipus_dictamen_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:accio_dictaminada_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:suport_origen_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:nom_format_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:versio_format_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:nom_aplicacio_creacio_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:versio_aplicacio_creacio_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:registre_formats_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:dimensions_fisiques_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:tamany_logic_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:quantitat_expedient:\"" + filtro + "\"" +								  
								  " OR @udlrm\\:unitats_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:suport_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:localitzacio_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:algoritme_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:valor_expedient:\"" + filtro + "\"" +								  
								  " OR @udlrm\\:tipus_signatura_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:format_signatura_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:rol_signatura_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:denominacio_estat_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:accio_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:motiu_reglat_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:usuari_accio_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:modificacio_metadades_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:codi_classificacio_expedient:\"" + filtro + "\"" +
								  " OR @udlrm\\:denominacio_classe_expedient:\"" + filtro + "\"";

		String queryMetadataAgr = "@udlrm\\:tipus_entitat_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:categoria_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:secuencial_identificador_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:esquema_identificador_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:nom_natural_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:data_inici_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:data_fi_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:descripcio_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:classificacio_acces_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:codi_politica_control_acces_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:sensibilitat_dades_caracter_personal_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:tipus_acces_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:idioma_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:valoracio_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:tipus_dictamen_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:accio_dictaminada_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:suport_origen_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:nom_format_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:versio_format_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:nom_aplicacio_creacio_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:versio_aplicacio_creacio_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:registre_formats_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:dimensions_fisiques_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:tamany_logic_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:quantitat_agregacio:\"" + filtro + "\"" +								  
								  " OR @udlrm\\:unitats_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:suport_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:localitzacio_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:algoritme_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:valor_agregacio:\"" + filtro + "\"" +								  
								  " OR @udlrm\\:tipus_signatura_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:format_signatura_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:rol_signatura_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:denominacio_estat_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:accio_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:motiu_reglat_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:usuari_accio_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:modificacio_metadades_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:codi_classificacio_agregacio:\"" + filtro + "\"" +
								  " OR @udlrm\\:denominacio_classe_agregacio:\"" + filtro + "\"";
*/
		if("*".equals(filtro)) {
			query = queryFons + " AND " +  queryAspect;
			
		}else {
			query = queryFons + " AND " + queryAspect + " AND (" + queryMetadataExp + " OR " + queryMetadataAgr + ")";
		}
		
		return query;
	}
}
