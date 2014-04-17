package com.smile.sso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.smile.utils.Utils;
import com.smile.webscripts.helper.ConstantsUdL;

import es.prise.crypt.AESTool;
import es.prise.crypt.RSATool;
import es.prise.openpapi.filter.LCookToken;
import es.prise.openpapi.filter.OpenPAPIFilter;
import es.prise.openpapi.http.HttpPapiRequestWrapper;
import es.prise.openpapi.logout.LogOutHelper;


/**
 * @author juseg
 * Integration between Alfresco and AdAS SSO using PAPI protocol.
 * Set Alfresco session attribute REMOTE_USER.
 * Add or update user from Alfresco database.
 * Override getRemoteUser() from PAPIfilter request wrapper.
 * Based on the original source of Prise OpenPAPIFilter: OpenPAPIFilter.java
 */
public class OpenPAPIFilterAlfresco extends OpenPAPIFilter implements Filter {

	private static Logger log = Logger.getLogger(OpenPAPIFilter.class);
	public static final String PAPIFILTER_CONFIG_FILE = "configfile";
	public static final String GPOA_URL_PARAM = "com.prise.adas.papifilterlegacy.gpoa_url";
	public static final String GPOA_KEY_PARAM = "com.prise.adas.papifilterlegacy.gpoa_pubkey";
	public static final String LKEY_PARAM = "com.prise.adas.papifilterlegacy.lkey";
	public static final String POA_LOCATION_PARAM = "com.prise.adas.papifilterlegacy.poa_location";
	public static final String POA_SERVICEID_PARAM = "com.prise.adas.papifilterlegacy.poa_serviceid";
	public static final String POA_PASS_URL_PARAM = "com.prise.adas.papifilterlegacy.passurl.";
	public static final String POA_FILTER_PARAM = "com.prise.adas.papifilterlegacy.filter.";
	public static final String POA_FILTER_URL_PARAM = "url";
	public static final String POA_FILTER_ATTRS_PARAM = "attrs";
	public static final String POA_FILTER_ACTION_PARAM = "action";
	public static final String POA_ERROR_PAGE_PARAM = "com.prise.adas.papifilterlegacy.error_page";
	public static final String POA_LOGGEDOUT_PAGE_PARAM = "com.prise.adas.papifilterlegacy.loggedout_page";
	public static final String POA_ATTR_USERID_PARAM = "com.prise.adas.papifilterlegacy.uid.attrname";
	public static final String POA_LAZYSLO_PARAM = "com.prise.adas.papifilterlegacy.lazy_slo";
	private FilterConfig filterConfig;
	private String gpoaURL;
	private String poaLocation;
	private String poaServiceID;
	private String errorPage;
	private String loggedOutPage;
	private RSATool rsaGPoA;
	private AESTool aesTool;
	private List passURLs;
	private List filters;
	private String uidAttrName;
	private boolean lazySLO;

	//Alfresco integration
	private static final String PARAM_REMOTE_USER = "remoteUser"; 
	protected static final String SESS_PARAM_REMOTE_USER = OpenPAPIFilterAlfresco.class.getName() + '.' + PARAM_REMOTE_USER; 


	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.loadConfig();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		LCookToken lcook = null;
		if (containsValidMessage(httpRequest)) {
			String action = httpRequest.getParameter("ACTION");
			if (action.equals("CHECKED")) {
				log.debug("Procesando mensaje CHECKED");
				lcook = processCheckedMessage(request);
			} else if (action.equals("PAPILOGGEDOUT")) {
				lcook = null;
				if (loggedOutPage != null) {
					httpResponse.sendRedirect(loggedOutPage);
					return;
				}
			} else if (action.equals("PAPILOGOUT")) {
				if (lazySLO == true) {
					HttpPapiRequestWrapper wrapper = new HttpPapiRequestWrapper(httpRequest, "", new HashMap(), getConfigPAPI());
					wrapper.setLogout(true);
					chain.doFilter(wrapper, httpResponse);
				} else {
					lcook = null;
					LogOutHelper loh = new LogOutHelper();
					loh.clearPAPISession(poaLocation, httpResponse);
					String theGPoAURL = httpRequest.getParameter("URL");
					String url = loh.getLoggedOutURL(theGPoAURL, poaServiceID, httpRequest.getRequestURL().toString());
					httpResponse.sendRedirect(url);
				}
				return;
			} else {
				httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
		} else {
			log.debug("Procesando peticion");
			lcook = getLCookCookie(httpRequest);
		}
		checkURL(httpRequest, httpResponse, chain, lcook);
	}

	public void destroy() {}

	/**
	 * Integration with Alfresco External Authentication Subsystem. 
	 * 
	 * @param req
	 * @param res
	 * @param chain
	 * @param lcook
	 * @throws IOException
	 * @throws ServletException
	 */
	private void authenticateAlfrescoRemoteUser(HttpServletRequest req, HttpServletResponse res, FilterChain chain, LCookToken lcook) throws IOException, ServletException {
		//Retrive user properties from AdAS token.		
		String attributes = req.getAttribute("lcook").toString();
		String aAttributes[] = attributes.split(",");
		Map<String,String> mAttributes = new HashMap<String,String>();
		for (int i = 0; i < aAttributes.length; i++) {
			String par = aAttributes[i];
			String aPar[] = par.split("=");
			mAttributes.put(aPar[0], aPar[1]);
		}		
		handleAlfrescoRemoteUserSSO(mAttributes.get("uid"), mAttributes.get("givenName"), mAttributes.get("sn"), mAttributes.get("mail"), mAttributes.get("businessCategory"));
	
		//Set Alfresco session attribute REMOTE_USER.
		HttpSession session = req.getSession(); 
		session.setAttribute(SESS_PARAM_REMOTE_USER, mAttributes.get("uid"));

		//Override getRemoteUser() from PAPIfilter request wrapper.
		HttpPapiRequestWrapperAlfresco reqWrap = new HttpPapiRequestWrapperAlfresco(req, getUserID(lcook), lcook.getAttributes(), getConfigPAPI());			        
		chain.doFilter(reqWrap, res);		 
	}

	/**
	 * Add or update user from Alfresco database.
	 * 
	 * @param username
	 * @param nom
	 * @param cognom1
	 * @param cognom2
	 * @return
	 */
	private String handleAlfrescoRemoteUserSSO(String username, String nom, String cognom1, String mail, String grup) {		
		Map<String, Object> params = new HashMap<String,Object>();
		if(username == null){
			throw new RuntimeException("Username is null!");
		}
		params.put(ConstantsUdL.PARAM_USERNAME, username);
		if(nom == null){
			throw new RuntimeException("Name is null!");
		}
		params.put(ConstantsUdL.PARAM_NAME, nom);
		if(cognom1 == null){
			cognom1 = "";
		}
		params.put(ConstantsUdL.PARAM_SURNAME1, cognom1);
		if(grup == null){
			grup = "";
		}		
		params.put("grup", grup);
		if(mail == null){
			mail = "";
		}
		params.put("email", mail);		
		String response = "";
		try {			
			String xmlArgs = crearXMLArgs(ConstantsUdL.URL_ADD_ALFRESCO_USER_SSO, params);
			response = Utils.callAlfrescoWebScript(ConstantsUdL.URL_ADD_ALFRESCO_USER_SSO, xmlArgs);

		} 
		catch (Exception e) {
			throw new RuntimeException("Failed to add/update user Alfresco", e);
		}
		return response;
	}

	private static String crearXMLArgs(String url, Map<String, Object> params) throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		Element arguments = document.createElement(ConstantsUdL.PARAM_ARGUMENTS);
		document.appendChild(arguments);

		Element username = document.createElement(ConstantsUdL.PARAM_USERNAME);
		username.appendChild(document.createTextNode((String) params.get(ConstantsUdL.PARAM_USERNAME)));
		arguments.appendChild(username);

		Element name = document.createElement(ConstantsUdL.PARAM_NAME);
		name.appendChild(document.createTextNode((String) params.get(ConstantsUdL.PARAM_NAME)));
		arguments.appendChild(name);

		Element surname1 = document.createElement(ConstantsUdL.PARAM_SURNAME1);
		surname1.appendChild(document.createTextNode((String) params.get(ConstantsUdL.PARAM_SURNAME1)));
		arguments.appendChild(surname1);

		Element email = document.createElement("email");
		email.appendChild(document.createTextNode((String) params.get("email")));
		arguments.appendChild(email);
		
		Element grup = document.createElement("grup");
		grup.appendChild(document.createTextNode((String) params.get("grup")));
		arguments.appendChild(grup);

		StringWriter sw = new StringWriter();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, ConstantsUdL.XML);
		transformer.setOutputProperty(OutputKeys.ENCODING, ConstantsUdL.UTF8);
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(sw);
		transformer.transform(source, result);

		String xmlSerializado = sw.toString();
		return xmlSerializado;
	}


	private void loadConfig() {
		log.debug("PAPILegacyFilter.loadConfig()");

		errorPage = null;
		loggedOutPage = null;
		uidAttrName = null;
		passURLs = new ArrayList();
		filters = new ArrayList();
		lazySLO = false;

		rsaGPoA = new RSATool();
		aesTool = new AESTool();
		try {
			log.debug("PAPILegacyFilter.loadConfig() - Cargando fichero configuracion: " + this.filterConfig.getInitParameter(OpenPAPIFilter.PAPIFILTER_CONFIG_FILE));
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(this.filterConfig.getInitParameter(OpenPAPIFilter.PAPIFILTER_CONFIG_FILE));
			Properties prop = new Properties();
			prop.load(is);

			gpoaURL = prop.getProperty(OpenPAPIFilter.GPOA_URL_PARAM);
			poaServiceID = prop.getProperty(OpenPAPIFilter.POA_SERVICEID_PARAM);
			poaLocation = prop.getProperty(OpenPAPIFilter.POA_LOCATION_PARAM);
			String pubkey = prop.getProperty(OpenPAPIFilter.GPOA_KEY_PARAM);
			rsaGPoA.readRsaPublicKeyPEM(pubkey);
			aesTool.addKey("lkey", prop.getProperty(OpenPAPIFilter.LKEY_PARAM).getBytes());
			if (prop.containsKey(OpenPAPIFilter.POA_ERROR_PAGE_PARAM)) {
				errorPage = prop.getProperty(OpenPAPIFilter.POA_ERROR_PAGE_PARAM);
			}
			if (prop.containsKey(OpenPAPIFilter.POA_ATTR_USERID_PARAM)) {
				uidAttrName = prop.getProperty(OpenPAPIFilter.POA_ATTR_USERID_PARAM);
			}
			if (prop.containsKey(OpenPAPIFilter.POA_LOGGEDOUT_PAGE_PARAM)) {
				loggedOutPage = prop.getProperty(OpenPAPIFilter.POA_LOGGEDOUT_PAGE_PARAM);
			}
			if (prop.containsKey(OpenPAPIFilter.POA_LAZYSLO_PARAM)) {
				lazySLO = prop.getProperty(POA_LAZYSLO_PARAM).equals("true");
			}

			int i = 0;
			String name = OpenPAPIFilter.POA_PASS_URL_PARAM + i + "." + OpenPAPIFilter.POA_FILTER_URL_PARAM;
			while (prop.containsKey(name)) {
				String value = prop.getProperty(name);
				log.debug("Cargando filtro pass URL: [" + value + "]");
				passURLs.add(value);

				i++;
				name = OpenPAPIFilter.POA_PASS_URL_PARAM + i + "." + OpenPAPIFilter.POA_FILTER_URL_PARAM;
			}

			i = 0;
			String name1 = OpenPAPIFilter.POA_FILTER_PARAM + i + "." + OpenPAPIFilter.POA_FILTER_URL_PARAM;
			String name2 = OpenPAPIFilter.POA_FILTER_PARAM + i + "." + OpenPAPIFilter.POA_FILTER_ATTRS_PARAM;
			String name3 = OpenPAPIFilter.POA_FILTER_PARAM + i + "." + OpenPAPIFilter.POA_FILTER_ACTION_PARAM;
			while (prop.containsKey(name1)) {
				String value1 = prop.getProperty(name1);
				String value2 = prop.getProperty(name2);
				String value3 = prop.getProperty(name3);
				log.debug("Cargando filtro authR: [" + value1 + "," + value2 + "," + value3 + "]");
				filters.add(new String[]{value1, value2, value3});

				i++;
				name1 = OpenPAPIFilter.POA_FILTER_PARAM + i + "." + OpenPAPIFilter.POA_FILTER_URL_PARAM;
				name2 = OpenPAPIFilter.POA_FILTER_PARAM + i + "." + OpenPAPIFilter.POA_FILTER_ATTRS_PARAM;
				name3 = OpenPAPIFilter.POA_FILTER_PARAM + i + "." + OpenPAPIFilter.POA_FILTER_ACTION_PARAM;
			}
		} catch (FileNotFoundException e) {
			log.error("PAPILegacyFilter.loadConfig() - Fichero de configuracion no encontrado");
			e.printStackTrace();
		} catch (Exception e) {
			log.error("PAPILegacyFilter.loadConfig() - Error inesperado");
			e.printStackTrace();
		}
	}



	private LCookToken processCheckedMessage(ServletRequest request) throws ServletException {
		LCookToken res = null;

		int now = (int) System.currentTimeMillis() / 1000;

		String encData = request.getParameter("DATA");
		String data = rsaGPoA.decode(encData);
		log.debug("Parametro DATA desencriptado: " + data);
		String[] dataResponse = getDataResponse(data);
		String assertion = dataResponse[0];
		if (now < Integer.parseInt(dataResponse[1])) {
			String idAS = getIDAS(assertion);
			log.debug("ID AS: " + idAS);
			String attributes = getRawAttributes(assertion);
			log.debug("Atributos: " + attributes);

			res = new LCookToken();
			res.setConfigLocation(poaLocation);
			res.setConfigServiceID(poaServiceID);
			res.setAssertion(attributes);
		} else {
			throw new ServletException("Aserción del Proveedor de Identidad caducada");
		}

		return res;
	}

	private void checkURL(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			FilterChain chain, LCookToken lcook) throws IOException, ServletException {
		String relativeRequestUri = httpRequest.getServletPath();
		boolean checkPass = checkPassFilters(relativeRequestUri);

		if (checkPass == true) {
			if (lcook != null) {
				httpRequest.setAttribute("lcook", lcook.getAssertion());				
			}
			chain.doFilter(httpRequest, httpResponse);
		} else {
			checkAccess(httpRequest, httpResponse, chain, lcook);
		}
	}

	private boolean checkPassFilters(String relativeRequestUri) {
		boolean res = false;

		Iterator it = passURLs.iterator();
		while (it.hasNext() && res == false) {
			String passURL = (String) it.next();
			log.debug("** Checking '" + passURL + "' against '" + relativeRequestUri + "'");
			Pattern pUrl = Pattern.compile(passURL);
			Matcher matchUrl = pUrl.matcher(relativeRequestUri);
			if (matchUrl.matches()) {
				res = true;
			}
		}

		return res;
	}

	private LCookToken getLCookCookie(HttpServletRequest httpRequest) {
		LCookToken res = null;

		Cookie[] cookies = httpRequest.getCookies();
		for (int i = 0; cookies != null && i < cookies.length; i++) {
			if (cookies[i].getName().equals("papi_lcook")) {
				String value = cookies[i].getValue();
				res = new LCookToken();
				res.setConfigLocation(poaLocation);
				res.setConfigServiceID(poaServiceID);
				res.setDataToken(aesTool.decode("lkey", value));
			}
		}

		return res;
	}

	private void checkAccess(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
			FilterChain chain, LCookToken lcook) throws ServletException, IOException {
		log.debug("Comprobando autorizacion");
		if (lcook == null || !lcook.validateToken()) {
			log.debug("Sin cookies validas");
			String requestedUri = getRequestURI(httpRequest);

			String gpoaRequest = getRedirectGPoA(requestedUri);
			log.debug("Redireccion GPoA: " + gpoaRequest);
			URL urlRequest = new URL(gpoaRequest);
			httpResponse.sendRedirect(urlRequest.toString());
		} else {
			log.debug("Cookie 'papi_lcook' valida");
			lcook.updateToken();
			Cookie lcookie = new Cookie("papi_lcook", aesTool.encode("lkey", lcook.getDataToken()));
			lcookie.setPath(poaLocation);
			httpResponse.addCookie(lcookie);

			if (checkAuthR(httpRequest, lcook)) {
				log.debug("Autorizacion valida");
				httpRequest.setAttribute("lcook", lcook.getAssertion());
				//HttpPapiRequestWrapper wrapper = new HttpPapiRequestWrapper(httpRequest, getUserID(lcook), lcook.getAttributes(), getConfigPAPI());
				//if (httpRequest.getSession().getAttribute(SESS_PARAM_REMOTE_USER) == null){
				authenticateAlfrescoRemoteUser(httpRequest, httpResponse, chain, lcook);							
				//}				
			} else {
				log.debug("Autorizacion NO valida");
				if (errorPage == null) {
					httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
				} else {
					URL urlRequest = new URL(errorPage);
					httpResponse.sendRedirect(urlRequest.toString());
				}
			}
		}
	}

	private Map getConfigPAPI() {
		Map res = new HashMap();
		res.put("GPOA-URL", gpoaURL);
		res.put("POA-LOCATION", poaLocation);
		res.put("POA-ID", poaServiceID);

		return res;
	}

	private String getUserID(LCookToken lcook) {
		String res = "";
		if (lcook.hasAttributes(uidAttrName)) {
			String[] temp = lcook.getAttributes(uidAttrName);
			res = temp[0];
		}
		return res;
	}

	private boolean checkAuthR(HttpServletRequest httpRequest, LCookToken lcook) {
		boolean res = true;
		String requestedUri = getRequestURI(httpRequest);

		Iterator it = filters.iterator();
		while (it.hasNext()) {
			String[] filter = (String[]) it.next();
			log.debug("** AuthR - Checking URL '" + filter[0] + "' against '" + requestedUri + "'");
			Pattern pUrl = Pattern.compile(filter[0]);
			Matcher matchUrl = pUrl.matcher(requestedUri);
			if (matchUrl.matches()) {
				log.debug("** AuthR - Checking Attrs '" + filter[1] + "' against '" + lcook.getAssertion() + "'");
				Pattern pAttrs = Pattern.compile(filter[1]);
				Matcher matchAttrs = pAttrs.matcher(lcook.getAssertion());
				if (matchAttrs.matches()) {
					log.debug("** AuthR - Processing Action '" + filter[2] + "'");
					return filter[2].equals("accept");
				}
			}
		}

		return res;
	}

	private String[] getDataResponse(String data) {
		String[] tokens = data.split(":");
		String nonce = tokens[tokens.length - 1];
		String timestamp2 = tokens[tokens.length - 2];
		String timestamp1 = tokens[tokens.length - 3];
		String[] assertionTemp = new String[tokens.length - 3];
		System.arraycopy(tokens, 0, assertionTemp, 0, assertionTemp.length);
		String assertion = implode(":", assertionTemp);

		return new String[]{assertion, timestamp1, timestamp2, nonce};
	}

	private boolean containsValidMessage(HttpServletRequest httpRequest) {
		Map params = httpRequest.getParameterMap();
		return params.containsKey("ACTION");
	}

	private String getIDAS(String assertion) {
		String[] tokens = assertion.split("@");
		String res = tokens[tokens.length - 1];

		return res;
	}

	private String getRawAttributes(String assertion) {
		String[] tokens = assertion.split("@");
		String[] attrTemp = new String[tokens.length - 1];
		System.arraycopy(tokens, 0, attrTemp, 0, attrTemp.length);
		String attrs = implode("@", attrTemp);

		return attrs;
	}

	private String getRequestURI(HttpServletRequest request) {
		String res = request.getRequestURL().toString();
		String params = "";
		if (request.getQueryString() != null) {
			params = "?" + request.getQueryString();
		}
		res = res + params;

		return res;
	}

	private String getRedirectGPoA(String request) {
		URLCodec codec = new URLCodec();

		String gpoaRequest = null;
		try {
			gpoaRequest = gpoaURL + "?ACTION=CHECK&DATA=random&URL=" + codec.encode(request);
		} catch (EncoderException e) {
			gpoaRequest = gpoaURL + "?ACTION=CHECK&DATA=random&URL=" + URLEncoder.encode(request);
		}

		return gpoaRequest;
	}

	private String implode(String separator, String[] data) {
		String res = "";
		for (int i = 0; i < data.length; i++) {
			if (!res.equals("")) {
				res = res + separator;
			}
			res = res + data[i];
		}
		return res;
	}
}