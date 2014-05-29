package com.smile.webscripts.cercarExpedients;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.LimitBy;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
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

		HashMap<String, Object> model = new HashMap<String, Object>();
		String filtro = req.getParameter(FILTRO);
		String expedients = null;
	
		try{
			UdlProperties props = new UdlProperties();
			Impersonate.impersonate(props.getProperty(ADMIN_USERNAME));

			ResultSet result = executeQuery(filtro);
			expedients = buildJSON(result);

		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Error en la cerca d'expedients.", e);

		}finally {
			model.put("expedients", expedients);
			model.put("filtro", filtro);
		}
		
		return model;
	}

	/**
	 * Ejecuta consulta full text buscando expedientes o agregaciones ubicados en el site RM.
	 * 
	 * @param filtro
	 * @return
	 */
	public ResultSet executeQuery(String filtro) {
		
		ResultSet result = null;

		try{
			long time_start = System.currentTimeMillis();
			
			SearchService searchService = serviceRegistry.getSearchService();
			SearchParameters sp = new SearchParameters();
			sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
			sp.setQuery(buildQuery(filtro));
			sp.setDefaultFTSOperator(SearchParameters.AND);
			sp.setLimitBy(LimitBy.NUMBER_OF_PERMISSION_EVALUATIONS);
			sp.setLimit(0);
			result = searchService.query(sp);
			
			long time_end = System.currentTimeMillis();
			System.out.println("La consulta ha tardado "+ ( (time_end - time_start)/1000 ) +" seg");

		}catch(Exception e) {
			e.printStackTrace();
			logger.error("Error executeQuery method.", e);
		}
		
		return result;
	}

	/**
	 * Monta un JSON a partir del resultset obtenido de la consulta lucene.
	 * 
	 * @param result
	 * @return
	 */
	public String buildJSON(ResultSet result) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		NodeService nodeService = serviceRegistry.getNodeService();
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		List<Object> data = new ArrayList<Object>();
		JSONObject json = null;
		
		long time_start = System.currentTimeMillis();
		
		try{
			if(result != null) {
				Iterator<NodeRef> iter = result.getNodeRefs().iterator();
				
				while (iter.hasNext()) {
					NodeRef nodeRef = (NodeRef) iter.next();
					ScriptNode exp = new ScriptNode(nodeRef, serviceRegistry, scope);
					HashMap<String, Object> map = new HashMap<String, Object>();

					if(exp.hasAspect(ASPECT_AGREGACIO)) {
						map.put("nomNatural", nodeService.getProperty(nodeRef, ContentModel.PROP_NAME));
						if(nodeService.getProperty(nodeRef, QName.createQName(UDL_URI, "nom_natural_institucio")) != null){
			 				map.put("nomNaturalInstitucio", nodeService.getProperty(nodeRef, QName.createQName(UDL_URI, "nom_natural_institucio"))); 						
						}
						if(nodeService.getProperty(nodeRef, QName.createQName(UDL_URI, "nom_natural_organ")) != null) {
							map.put("nomNaturalOrgan", nodeService.getProperty(nodeRef, QName.createQName(UDL_URI, "nom_natural_organ"))); 						
						}
						map.put("numExp", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "secuencial_identificador_agregacio")));
						if(nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "data_inici_agregacio")) != null) {
							map.put("dataInici", sdf.format((Date)nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "data_inici_agregacio"))));							
						}
						if(nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "data_fi_agregacio")) != null) {
							map.put("dataFi", sdf.format((Date)nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "data_fi_agregacio"))));							
						}
						map.put("codiClass", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "codi_classificacio_1_agregacio")));			
						map.put("localitzacio", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "localitzacio_1_agregacio")));
						map.put("grupCreador", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "grup_creador_agregacio")));
						
					}else if(exp.hasAspect(ASPECT_EXPEDIENT)) {
						map.put("nomNatural", nodeService.getProperty(nodeRef, ContentModel.PROP_NAME));
						if(nodeService.getProperty(nodeRef, QName.createQName(UDL_URI, "nom_natural_institucio")) != null) {
							map.put("nomNaturalInstitucio", nodeService.getProperty(nodeRef, QName.createQName(UDL_URI, "nom_natural_institucio")));	
						}
						if(nodeService.getProperty(nodeRef, QName.createQName(UDL_URI, "nom_natural_organ")) != null) {
							map.put("nomNaturalOrgan", nodeService.getProperty(nodeRef, QName.createQName(UDL_URI, "nom_natural_organ"))); 						
						}
						map.put("numExp", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "secuencial_identificador_expedient")));
						if(nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "data_inici_expedient")) != null) {
							map.put("dataInici", sdf.format((Date)nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "data_inici_expedient"))));
						}
						if(nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "data_fi_expedient")) != null) {
							map.put("dataFi", sdf.format((Date)nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "data_fi_expedient"))));							
						}
						map.put("codiClass", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "codi_classificacio_1_expedient")));
						map.put("localitzacio", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "localitzacio_1_expedient")));
						map.put("grupCreador", nodeService.getProperty(nodeRef, QName.createQName(UDLRM_URI, "grup_creador_expedient")));					
					}
		
					json = new JSONObject();
					json.putAll(map);
					data.add(json);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("Error executeQuery method.", e);
		}
		
		long time_end = System.currentTimeMillis();
		System.out.println("Montar el json ha tardado "+ ( (time_end - time_start)/1000 ) +" seg");
		
		return data.toString();
	}
	
	/**
	 * Contruye la consulta a realizar.
	 * 
	 * @param filtro
	 * @return
	 * @throws Exception
	 */
	private String buildQuery(String filtro) throws Exception {
	
		String queryFons = "PATH:\"/app:company_home/st:sites/cm:rm/cm:documentLibrary/cm:Fons_x0020_Universitat_x0020_de_x0020_Lleida/*/*\"";
		String queryAspect = "(NOT ASPECT:\"dod:ghosted\" AND (ASPECT:\"udlrm:agregacio\" OR ASPECT:\"udlrm:expedient\"))";
		String queryAcces = "(@udlrm\\:classificacio_acces_expedient:\"Públic\" OR @udlrm\\:classificacio_acces_agregacio:\"Públic\")";
		String query = queryAcces + " AND " + queryFons + " AND " +  queryAspect;

		// El filtro por intervalo se aplica a fecha de inicio únicamente
		if(isRange(filtro)){
			String stringRange = formatRange(filtro);
			query = query + " AND (@udlrm\\:data_inici_expedient:" + stringRange + " OR @udlrm\\:data_inici_agregacio:" + stringRange + ")";

		// El filtro por fecha se aplica a fecha de inicio únicamente
		}else if(isDate(filtro)) {
			String stringDate = formatDate(filtro);
			query = query + " AND (@udlrm\\:data_inici_expedient:" + stringDate + " OR @udlrm\\:data_inici_agregacio:" + stringDate + ")";

		// Si la consulta no es vacia o "*" se hace una búsqueda full text para todos los metadatos
		}else if(!"*".equals(filtro) && !"".equals(filtro)) {
			String[] subFiltro = filtro.split(" ");
			
			if(subFiltro.length > 1) {
				String querySubFiltros = "";

				for (int i = 0; i < subFiltro.length; i++) {
					querySubFiltros = querySubFiltros + "ALL:\"" + subFiltro[i] + "\" AND ";
				}

				query = query + " AND " + StringUtils.chomp(querySubFiltros, " AND ");
				
			}else {
				query = query + "AND ALL:\"" + filtro + "\"";				
			}
		}
		
		return query;
	}
	
	/**
	 * Retorna true si la cadena recibida como parámetro se puede parsear com una fecha.
	 * 
	 * @param filtro
	 * @return
	 */
	public boolean isDate(String filtro) {
		boolean result = false;
		DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
		 
		try{
			if(filtro.contains("/")) {
				df1.parse(filtro);
				result = true;
				
			}else if(filtro.contains("-")) {
				df2.parse(filtro);
				result = true;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("Error isDate method.", e);
		}
		
		return result;
	}
	
	/**
	 * Si el filtro contiene corchetes se interpeta que el usuario quiere hacer una busqueda por rango.
	 * 
	 * @param filtro
	 * @return
	 */
	public boolean isRange(String filtro) {
		boolean result = false;
		
		if(filtro.contains("[") && filtro.contains("]")) {
			result = true;
		}
		
		return result;
	}
	
	/**
	 * Recibe una fecha como string y la transforma en el formato que acepta el buscador
	 * 
	 * @param filtro
	 * @return
	 */
	public String formatDate(String filtro) {
		String result = filtro;
		Date date = null;
		Pattern patronFecha = Pattern.compile("^([0][1-9]|[12][0-9]|3[01])(\\/|-)([0][1-9]|[1][0-2])\\2(\\d{4})$");
		Matcher matcher = patronFecha.matcher(filtro);
		DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
		DateFormat dfAlfresco = new SimpleDateFormat("yyyy-MM-dd");

		
		try{
			if(matcher.matches()) {
				if(filtro.contains("/")) {
					date = df1.parse(filtro);
					result = "\"" + dfAlfresco.format(date) + "\"";
				
				}else if(filtro.contains("-")) {
					date = df2.parse(filtro);
					result = "\"" + dfAlfresco.format(date) + "\"";
				}

			}else {
				result = filtro.toUpperCase();
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("Error formatDate method.", e);
		}
		
		return result;
	}

	/**
	 * Recibe una fecha como string y la transforma en el formato que acepta el buscador
	 * 
	 * @param filtro
	 * @return
	 */
	public String formatRange(String filtro) {
		String result = "";
		Pattern rangoFechas = Pattern.compile("^\\[([0][1-9]|[12][0-9]|3[01])(\\/|-)([0][1-9]|[1][0-2])\\2(\\d{4})\\s(TO|to)\\s([0][1-9]|[12][0-9]|3[01])(\\/|-)([0][1-9]|[1][0-2])\\2(\\d{4})\\]$");
		Pattern rangoMinNowFecha = Pattern.compile("^\\[(MIN|NOW)\\s(TO|to)\\s([0][1-9]|[12][0-9]|3[01])(\\/|-)([0][1-9]|[1][0-2])\\4(\\d{4})\\]$");
		Pattern rangoFechaMaxNow = Pattern.compile("^\\[([0][1-9]|[12][0-9]|3[01])(\\/|-)([0][1-9]|[1][0-2])\\2(\\d{4})\\s(TO|to)\\s(MAX|NOW)\\]$");
		Pattern rangoMinMaxNow = Pattern.compile("^\\[(MIN|NOW)\\s(TO|to)\\s(MAX|NOW)\\]$");
		

		Matcher mFechas = rangoFechas.matcher(filtro);
		Matcher mMinNowFecha = rangoMinNowFecha.matcher(filtro);
		Matcher mFechaMaxNow = rangoFechaMaxNow.matcher(filtro);
		Matcher mMinMaxNow = rangoMinMaxNow.matcher(filtro);
		
		try{
			if(mFechas.matches() || mMinNowFecha.matches() || mFechaMaxNow.matches() || mMinMaxNow.matches()) {
				String[] aux = filtro.replace("[", "").replace("]", "").split(" ");
				String fecha1 = aux[0];
				String fecha2 = aux[2];

				result = "[" + formatDate(fecha1) + " TO " + formatDate(fecha2) + "]";

			}else {
				result = filtro;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("Error formatDate method.", e);
		}
		
		return result;
	}
	
	/**
	 * Parse given date with ISO8601 format.
	 * @param sDate
	 * @return
	 */
	public String parse(String sDate){
		String ret = null;
		if(sDate != null && !sDate.equals("") && !sDate.equals("null")){
			sDate = sDate.replaceAll("-", "/");
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Date date;
			try {
				date = df.parse(sDate);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}		
			ret = ISO8601DateFormat.format(date);
		}		
		return ret;
	}
}