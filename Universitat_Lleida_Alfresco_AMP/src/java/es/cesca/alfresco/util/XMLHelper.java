package es.cesca.alfresco.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.modelmbean.XMLParseException;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XMLHelper {

	private static Log logger = LogFactory.getLog(XMLHelper.class);
	private static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private static final String PRE = "dmattr.";
	private static final String QUERY = "query.";
	private static final String EXPEDIENT = "numero_expedient";//correspondència amb iArxiu
	private static final String DOCUMENT = "numero_document";//correspondència amb iArxiu
	private static final String SIGNATURE = "identificador";//correspondència amb iArxiu
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String wrappper_start = null;
	private String wrappper_end = null;
	private String numExp = null;
	private boolean isExp = false;
	private String numDoc = null;
	private boolean isDoc = false;
	private String idSignatura = null;
	private boolean isSignature = false;
	private Map<String, Pair<String, Boolean>> base;
	private Map<String, Map<String, Pair<String, Boolean>>> baseVoc;
	
	/**
	 * [PRE: All XML are from: urn:iarxiu:2.0.vocabularies:catcert:XXX 
	 *   In case this premise will be modified, review this code.
	 * ]
	 * 
	 * @param xmlbase in format:
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <tipus_signatura plantilla="urn:iarxiu:2.0:templates:catcert:PL_ufe_signatura">
	 * 	<vocabulari name="urn:iarxiu:2.0.vocabularies:catcert:Voc_ufe_signatura">
	 * 		...	
	 * 		<atribut name="identificador">
	 * 			<valor>dmattr.id</valor>
	 * 			<obligatori>si</obligatori>
	 * 			<tipus>txt</tipus>
	 * 		</atribut>
	 * 		...
	 * 	</vocabulari>
	 * </tipus_signatura>
	 * @throws MandatoryFieldException
	 * @throws XMLParseException 
	 */
	
	//This is not optimal > must use an XSD and play with Java objects instead of parse XML
	public XMLHelper(String xmlbase) throws MandatoryFieldException, XMLParseException {
		
		if (logger.isDebugEnabled()) {
			logger.debug(xmlbase);
		}
		
		if ( CescaUtil.isEmpty(xmlbase) ) {
			throw new MandatoryFieldException("XML");
		}

		//Remove first line > A well formed xml contains version and encoding
		String[] xmlToken = xmlbase.split("\\?>");
		if (xmlToken == null || xmlToken.length != 2) {
			throw new MandatoryFieldException("XML Header");
		}
		
		String[] xmlTemp = xmlToken[1].split(">");//Split > to save 2 first and 2 last elements
		wrappper_start = xmlTemp[0] +">";
		wrappper_end = xmlTemp[xmlTemp.length-1]+">";
		
		//Para n vocabularios
		String [] vocabularis = xmlToken[1].split("<vocabulari");
		
		baseVoc = new HashMap<String, Map<String, Pair<String, Boolean>>>();
		//iteramos vocabularios
		for(int j=1; j<vocabularis.length;j++){
			String voc = vocabularis[j];
			
			String sVoc = vocabularis[j].replaceAll(" ", "");
			int indexVoc = sVoc.indexOf("\"");
			String nameVoc = sVoc.substring(indexVoc+1, sVoc.indexOf("\"", indexVoc+1));
	
			try {
				if (!voc.contains("<atribut")) {
					throw new MandatoryFieldException("At least an 'atribut'");
				}
				String [] attributes = voc.split("<atribut");
				base = new HashMap<String, Pair<String,Boolean>>();
				
				for (int i = 1; i < attributes.length; i++) {
					if(logger.isDebugEnabled())
						logger.debug("atributos " + attributes[i]);
					
					String s = attributes[i].replaceAll(" ", "");
					
					int index = s.indexOf("\"");
					String nameAtr = s.substring(index+1, s.indexOf("\"", index+1));
					
					int indexInit = attributes[i].indexOf("<valor>");
					int indexEnd = attributes[i].indexOf("</valor>");
					String valor = attributes[i].substring(indexInit, indexEnd);
					valor = valor+"</valor>";
					
					if(logger.isDebugEnabled())
						logger.debug("valor " + valor);
					
					Boolean second = attributes[i].contains("<obligatori>si</obligatori>");
					
					Pair<String, Boolean> pair = new Pair<String, Boolean>(valor, second);
					base.put(nameAtr, pair);
				}	
				baseVoc.put(nameVoc, base);
			
			} catch (Exception e) {
				throw new XMLParseException(xmlbase);
			}
		}
	}
	
	public String xmlToIArxiu(Map<String, Serializable> propsToBind, ServiceRegistry serviceRegistry, boolean mandatory) throws MandatoryFieldException {
		
		if (propsToBind == null || propsToBind.isEmpty())
			throw new MandatoryFieldException("Properties from a node");
		
		StringBuilder sb = new StringBuilder(HEADER);
		sb.append(wrappper_start);
		
		Iterator it = propsToBind.entrySet().iterator();
		while (it.hasNext()) {
		Map.Entry e = (Map.Entry)it.next();
			if(logger.isDebugEnabled())
				logger.debug("valores propiedades nodo " + e.getKey() + " " + e.getValue());
		}
		
		Set<String> setVoc = baseVoc.keySet();
		
		//iteramos vocabularios
		if(logger.isDebugEnabled())
			logger.debug("iteramos vocabularios " + setVoc.size());
		
		for (String keyVoc : setVoc) {
			sb.append("<vocabulari name=\""+keyVoc+"\">");
			
			if(logger.isDebugEnabled())
				logger.debug("keyVoc " + keyVoc);
			
			Map<String, Pair<String, Boolean>> baseAtr = baseVoc.get(keyVoc);
			Set<String> setAtr = baseAtr.keySet();
			
			//iteramos atributos
			if(logger.isDebugEnabled())
				logger.debug("iteramos atributos " + setAtr.size());
			
			for (String keyAtr : setAtr) {
				Pair<String, Boolean> pair = baseAtr.get(keyAtr);
				String keyToBind = pair.getFirst();
				
				if(logger.isDebugEnabled())
					logger.debug("keyToBind " + keyToBind);
				
				String valorFinal = "";
				
				int firstIndexSplit = keyToBind.indexOf("<valor>");
				int secondIndexSplit = keyToBind.indexOf("</valor>");
				String valorSplit = keyToBind.substring(firstIndexSplit+ "<valor>".length(), secondIndexSplit);
				
				String[] splitKeyToBind =  valorSplit.split(";");
				
				for(int i=0;i<splitKeyToBind.length;i++){
					if(splitKeyToBind[i].contains(QUERY)){
						String valorQuery = "";
						System.out.println("Metadada query: " + splitKeyToBind[i]);
						System.out.println("Mandatory ? : " + mandatory);
						if(mandatory) {
							if(logger.isDebugEnabled())
								logger.debug("(es una query.)" + splitKeyToBind[i]);
							
							int secondIndex = splitKeyToBind[i].indexOf(QUERY);
							splitKeyToBind[i] = splitKeyToBind[i].substring(secondIndex+ QUERY.length(), splitKeyToBind[i].length());
							try{
								System.out.println("Query abans de treure els dmattr: " + splitKeyToBind[i]);
								String query = calculateDmattrFromQuery(splitKeyToBind[i],propsToBind,sb);
								System.out.println("Query a ejecutar con CMIS: " + query);
								SearchParameters sp = new SearchParameters();
								sp.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
								sp.setQuery(query);
								sp.addStore(CescaUtil.STORE);
								
								ResultSet resultSet = null;
								try {
									resultSet = serviceRegistry.getSearchService().query(sp);
									if(resultSet!=null&&resultSet.length()>0){
										System.out.println("Longitud resultSet: " + resultSet.length());
										//NodeRef node = resultSet.getNodeRef(0);
										String property = query.substring(query.indexOf("select")+7, query.indexOf("from")).trim();
										System.out.println("La property a cargar de la query: " + property);
										//String res = (String)resultSet.getRow(0).getValue(property);
										int regs = resultSet.length();
										for(int ii=0; ii<regs; ii++) {
											if(valorQuery.equals("")) {
												valorQuery = (String)resultSet.getRow(ii).getValue(property);
											} else {
												valorQuery = valorQuery + "|" + (String)resultSet.getRow(ii).getValue(property);
											}
										}
										//valorQuery = (String)resultSet.getRow(0).getValue(property);
										System.out.println("valor Query: " + valorQuery);
										if(logger.isDebugEnabled())
											logger.debug("(query correcta y con valores) ");
									}
								}catch(Exception e){
									logger.error(e.getMessage(), e);
									e.printStackTrace();
								}finally{
									if (resultSet != null) {
										resultSet.close();
									}
								}
							}catch(Exception e){
								logger.error(e.getMessage(), e);
								e.printStackTrace();
							}
							
							if(logger.isDebugEnabled())
								logger.debug("valor final query" +"<"+keyAtr+">"+valorQuery+"</"+keyAtr+">");
						
						}
						
						valorFinal = valorFinal + valorQuery;
					}
					else if(splitKeyToBind[i].contains(PRE)){
						if(logger.isDebugEnabled())
							logger.debug("(es un dmtrr.)" + splitKeyToBind[i]);
		
						System.out.println("Metadada dmattr: " + splitKeyToBind[i]);
						int secondIndex = splitKeyToBind[i].indexOf(PRE);
						splitKeyToBind[i] = splitKeyToBind[i].substring(secondIndex+ PRE.length(), splitKeyToBind[i].length());
						if(splitKeyToBind[i].indexOf(":") != -1) {
							splitKeyToBind[i] = splitKeyToBind[i].substring(splitKeyToBind[i].indexOf(":") +1);
						}
						Serializable serial = propsToBind.get(splitKeyToBind[i]);
						
						if (pair.getSecond() && serial == null){
							throw new MandatoryFieldException(keyAtr+"/"+splitKeyToBind[i]);								
						}
						
						String val = toString(serial);
						
						if(logger.isDebugEnabled())
							logger.debug("valor final dmattr " +"<"+keyAtr+">"+val+"</"+keyAtr+">");
						
						valorFinal = valorFinal + val;
					}
					else{
						if(logger.isDebugEnabled())
							logger.debug("(es un constant.)" + splitKeyToBind[i]);
						System.out.println("Metadada constant: " + splitKeyToBind[i]);
						String first = splitKeyToBind[i];
						
						if(logger.isDebugEnabled())
							logger.debug("valor final constant" +"<"+keyAtr+">"+first+"</"+keyAtr+">");
						
						valorFinal = valorFinal + first;
					}
					
					if (EXPEDIENT.equals(keyAtr)) {
						if(logger.isDebugEnabled())
							logger.debug("key expediente " +keyAtr + " val: " + valorFinal);
						this.isExp = true;
						numExp = valorFinal;
					} else if (DOCUMENT.equals(keyAtr)) {
						if(logger.isDebugEnabled())
							logger.debug("key document " +keyAtr + " val: " + valorFinal);
						this.isDoc = true;
						numDoc = valorFinal;
					} else if (SIGNATURE.equals(keyAtr)) {
						if(logger.isDebugEnabled())
							logger.debug("key signatura " +keyAtr + " val: " + valorFinal);
						this.isSignature = true;
						idSignatura = valorFinal;
					}
				}
				logger.debug("valor final " +"<"+keyAtr+">"+valorFinal+"</"+keyAtr+">");
				sb.append("<"+keyAtr+">").append(valorFinal).append("</"+keyAtr+">");
			}
			sb.append("</vocabulari>");
		}
		
		if (!isDoc && !isExp && !isSignature) {
			throw new MandatoryFieldException("Expedient, Document or Signature field");
		}
		
		sb.append(wrappper_end);
		
		return sb.toString();
	}
	
	//This is not optimal > must use an XSD and play with Java objects instead of parse XML
	/*public XMLHelper(String xmlbase, boolean cercaExp) throws MandatoryFieldException, XMLParseException {
		
		if (logger.isDebugEnabled()) {
			logger.debug(xmlbase);
		}
		
		if ( CescaUtil.isEmpty(xmlbase) ) {
			throw new MandatoryFieldException("XML");
		}

		//Remove first line > A well formed xml contains version and encoding
		String[] xmlToken = xmlbase.split("\\?>");
		if (xmlToken == null || xmlToken.length != 2) {
			throw new MandatoryFieldException("XML Header");
		}

		try {
			String[] xmlTemp = xmlToken[1].split(">");//Split > to save 2 first and 2 last elements
			wrappper_start = xmlTemp[0] +">"+xmlTemp[1]+">";
			wrappper_end = xmlTemp[xmlTemp.length-2] +">"+xmlTemp[xmlTemp.length-1]+">";
		
			if (!xmlToken[1].contains("<atribut")) {
				throw new MandatoryFieldException("At least an 'atribut'");
			}
			
			String [] attributes = xmlToken[1].split("<atribut");
			base = new HashMap<String, Pair<String,Boolean>>();
			
			for (int i = 1; i < attributes.length; i++) {
					if(logger.isDebugEnabled())
						logger.debug("atributos " + attributes);
				
					String s = attributes[i].replaceAll(" ", "");
					int index = s.indexOf("\"");
					String name = s.substring(index+1, s.indexOf("\"", index+1));
					int secondIndex = s.indexOf(PRE);
					String first = s.substring(secondIndex+ PRE.length(), s.indexOf("<", secondIndex + PRE.length()));
					Boolean second = attributes[i].contains("<obligatori>si</obligatori>");
					
					if(logger.isDebugEnabled())
						logger.debug("valor " +s+"\t\t\t\tval>"+name+"\t\tobl>"+second);
									
					Pair<String, Boolean> pair = new Pair<String, Boolean>(first, second);
					base.put(name, pair);
			}
		
		} catch (Exception e) {
			throw new XMLParseException(xmlbase);
		}
	}*/
	
	/*public String xmlToIArxiu(Map<String, Serializable> propsToBind) throws MandatoryFieldException {
		
		if (propsToBind == null || propsToBind.isEmpty())
			throw new MandatoryFieldException("Properties from a node");
		
		StringBuilder sb = new StringBuilder(HEADER);
		sb.append(wrappper_start);
		
		Set<String> set = base.keySet();
		for (String key : set) {
			
			if(logger.isDebugEnabled())
				logger.debug("key " +key);
			
			Pair<String, Boolean> pair = base.get(key);
			String keyToBind = pair.getFirst();//ID en el modelo actual
			
			Serializable serial = propsToBind.get(keyToBind);
			
			if(logger.isDebugEnabled())
				logger.debug("serial " + serial);
			if (pair.getSecond() && serial == null){
				throw new MandatoryFieldException(key+"/"+keyToBind);
			}
			String val = toString(serial);
			
			if (EXPEDIENT.equals(key)) {
				if(logger.isDebugEnabled())
					logger.debug("key expediente " +key + " val: " + val);
				this.isExp = true;
				numExp = val;
			} else if (DOCUMENT.equals(key)) {
				if(logger.isDebugEnabled())
					logger.debug("key document " +key + " val: " + val);
				this.isDoc = true;
				numDoc = val;
			} else if (SIGNATURE.equals(key)) {
				if(logger.isDebugEnabled())
					logger.debug("key signatura " +key + " val: " + val);
				this.isSignature = true;
				idSignatura = val;
			}
			sb.append("<"+key+">").append(val).append("</"+key+">");
			
			if(logger.isDebugEnabled())
				logger.debug("valor final (xmlToIArxiu) " +"<"+key+">"+val+"</"+key+">" );
		}
		
		if (!isDoc && !isExp && !isSignature) {
			throw new MandatoryFieldException("Expedient, Document or Signature field");
		}
		
		sb.append(wrappper_end);
		
		return sb.toString();
	}*/
	
	private String calculateDmattrFromQuery(String query, Map<String, Serializable> propsToBind, StringBuilder sb) throws Exception {
		if(logger.isDebugEnabled())
			logger.debug("calculateDmattrFromQuery ");
		
		while(query.contains("dmattr.")) {
			String[] queryParts = query.split("dmattr.");
			if(queryParts.length > 1) {
				String atribut = "";
				if(queryParts[1].indexOf(" ") != -1) {
					atribut = queryParts[1].substring(0, queryParts[1].indexOf(" "));
					if(atribut.contains("'")) {
						atribut = atribut.replace("'", "");
					}
				} else {
					if(queryParts[1].contains("'")) {
						atribut = queryParts[1].substring(0, queryParts[1].length()-1);
					} else {
						atribut = queryParts[1].substring(0, queryParts[1].length());	
					}
				}
				System.out.println("Atribut trobat a la query amb dmattr: " + atribut);
				String atributWithoutPrefix = null;
				if(atribut.contains(":")) {
					atributWithoutPrefix = atribut.substring(atribut.indexOf(":") + 1);
					System.out.println("La metadada conté prefix. La transformem a: " + atributWithoutPrefix);
				}
				if(logger.isDebugEnabled())
					logger.debug("valor propsToBind en query " + propsToBind.get(atribut));
				
				if (propsToBind.get(atribut)!=null) {
					System.out.println("Existeix l'atribut");
					query = query.replace("dmattr." + atribut, propsToBind.get(atribut).toString());
				} else if (propsToBind.get(atributWithoutPrefix) != null) {
					System.out.println("Existeix l'atribut sense prefix");
					query = query.replace("dmattr." + atribut, propsToBind.get(atributWithoutPrefix).toString());
				} else {
					logger.info("la consulta '" + query + "' no és correcte.");
					sb.append(getDataLog() + "la consulta '" + query + "' no és correcte. Conté una referència dmattr no existent a l'objecte que s'està tractant.");
					//sb.newLine();bw.newLine();
					
					query = query.replace("dmattr." + atribut, "");
				}
			}
		}
		return query;
	}
	
	private String getDataLog() {
		if(logger.isDebugEnabled())
			logger.debug("getDataLog ");
		
		Calendar calendar = new GregorianCalendar(); 
		
		StringBuffer linea = new StringBuffer();
		calendar.setTime(new Date());
    
		String dia = "" + calendar.get(Calendar.DAY_OF_MONTH);
		if(dia.length() < 2)  dia = "0" + dia;

	    String mes = "" + (calendar.get(Calendar.MONTH) + 1);
	    if (mes.length() < 2)  mes = "0" + mes;
 
	    String hora = "" + calendar.get(Calendar.HOUR_OF_DAY);
	    if (hora.length() < 2) hora = "0" + hora;

	    String minuto = "" + calendar.get(Calendar.MINUTE);
	    if (minuto.length() < 2) minuto = "0" + minuto;

		String seg = "" + calendar.get(Calendar.SECOND);
	    if (seg.length() < 2) seg = "0" + seg;
     
		linea.append(hora +":"+ minuto + ":" + seg);
		
		return "[" + linea.toString() + "] ";
	}
	
	private String toString(Serializable serial) throws MandatoryFieldException {
		if (serial == null)
			return null;
		else if (serial instanceof Date) {       	
            return dateFormat.format(serial);
		} else if (serial instanceof String || serial instanceof Number){
			return serial.toString();
		} else if (serial instanceof ArrayList){
			return serial.toString().replace(",", "|").replace("[", "").replace("]", "");
		} else {
			throw new MandatoryFieldException("A known type ["+serial.getClass()+"]");
		}
	}
	
	public class MandatoryFieldException extends Exception {

		private static final long serialVersionUID = 5424422737562071776L;
		private static final String CONDITION_MSG = " is/are mandatory";

		public MandatoryFieldException(String message, Throwable cause) {
			super(message + CONDITION_MSG, cause);
		}

		public MandatoryFieldException(String message) {
			super(message + CONDITION_MSG);
		}

		public MandatoryFieldException(Throwable cause) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	/**
	 * @return the numExp
	 */
	public String getNumExp() {
		return numExp;
	}

	/**
	 * @return the isExp
	 */
	public boolean isExp() {
		return isExp;
	}

	/**
	 * @return the numDoc
	 */
	public String getNumDoc() {
		return numDoc;
	}

	/**
	 * @return the isDoc
	 */
	public boolean isDoc() {
		return isDoc;
	}

	/**
	 * @return the idSignatura
	 */
	public String getIdSignatura() {
		return idSignatura;
	}

	/**
	 * @return the isSignature
	 */
	public boolean isSignature() {
		return isSignature;
	}

	public static void main(String[] args) {
		String[] xmlbase = {"<?xml version=\"1.0\" encoding=\"UTF-8\"?><tipus_expedient plantilla=\"urn:iarxiu:2.0:templates:catcert:PL_ufe_expedient\"><vocabulari name=\"urn:iarxiu:2.0.vocabularies:catcert:Voc_ufe_expedient\"><atribut name=\"codi_referencia\"><valor> dmattr.codi_ref</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut><atribut name=\"numero_expedient\"><valor> dmattr.num_exp</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut><atribut name=\"codi_classificacio\"><valor> dmattr.codi_class</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut><atribut name=\"serie_documental\"><valor> dmattr.serie_docum</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut><atribut name=\"nivell_descripcio\"><valor> dmattr.nivell_descr</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut><atribut name=\"titol\"><valor> dmattr.titol</valor><obligatori></obligatori><tipus>txt</tipus></atribut><atribut name=\"data_obertura\"><valor> dmattr.data_ob</valor><obligatori>si</obligatori><tipus>data</tipus></atribut><atribut name=\"data_tancament\"><valor> dmattr.data_tanc</valor><obligatori>si</obligatori> <tipus>data</tipus></atribut><atribut name=\"nom_productor\"><valor> dmattr.nom_prod</valor><obligatori>si</obligatori> <tipus>txt</tipus></atribut><atribut name=\"classificacio_seguretat_acces\"><valor> dmattr.class_seg</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut></vocabulari></tipus_expedient>",
							"<?xml version=\"1.0\" encoding=\"UTF-8\"?><tipus_signatura plantilla=\"urn:iarxiu:2.0:templates:catcert:PL_ufe_signatura\"><vocabulari name=\"urn:iarxiu:2.0.vocabularies:catcert:Voc_ufe_signatura\"><atribut name=\"identificador_signatura\"><valor>dmattr.id_sign</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut><atribut name=\"identificador_document\"><valor>dmattr.id_doc</valor> <obligatori>si</obligatori><tipus>txt</tipus></atribut><atribut name=\"tipus_signatura\"><valor>dmattr.tipus_sign</valor><obligatori>si</obligatori> <tipus>txt</tipus></atribut><atribut name=\"format_signatura\"><valor>dmattr.format_sign</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut><atribut name=\"nom_signatari\"><valor>dmattr.signatari</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut><atribut name=\"identificador_signatari\"><valor>dmattr.id_signatari</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut></vocabulari></tipus_signatura>",
							"<?xml version=\"1.0\" encoding=\"UTF-8\"?><tipus_document plantilla=\"urn:iarxiu:2.0:templates:catcert:PL_ufe_document\"><vocabulari name=\"urn:iarxiu:2.0.vocabularies:catcert:Voc_ufe_document\"><atribut name=\"codi_referencia\"><valor>dmattr.codi_ref</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut><atribut name=\"numero_document\"><valor>dmattr.num_doc</valor><obligatori>si</obligatori> <tipus>txt</tipus></atribut><atribut name=\"nivell_descripcio\"><valor>dmattr.nivell_descr</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut><atribut name=\"titol\"><valor>dmattr.titol</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut><atribut name=\"data_creacio\"><valor>dmattr.data_cr</valor><obligatori>si</obligatori><tipus>data</tipus></atribut><atribut name=\"suport\"><valor>dmattr.suport</valor><obligatori>si</obligatori><tipus>txt</tipus></atribut></vocabulari></tipus_document>"};
		for (String string : xmlbase) {
			try {
				XMLHelper help = new XMLHelper(string);
			} catch (MandatoryFieldException e) {
				e.printStackTrace();
			} catch (XMLParseException e) {
				e.printStackTrace();
			}
		}

	}
}
