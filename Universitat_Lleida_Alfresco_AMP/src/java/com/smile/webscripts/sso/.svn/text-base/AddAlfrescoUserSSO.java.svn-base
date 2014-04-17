package com.smile.webscripts.sso;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.MutableAuthenticationDao;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.PropertyMap;
import org.apache.log4j.Logger;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.UdlProperties;

public class AddAlfrescoUserSSO extends DeclarativeWebScript implements ConstantsUdL {

	private static Logger log = Logger.getLogger(AddAlfrescoUserSSO.class);

	private ServiceRegistry serviceRegistry;
	private AuthenticationComponent authComponent;
	private AuthenticationService authService;
	private TransactionService transactionService;
	private PersonService personService;
	private MutableAuthenticationDao authenticationDao;
	private SearchService searchService;

	public AddAlfrescoUserSSO() {
		super();
	}

	protected HashMap<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		try {
			UdlProperties props = new UdlProperties();
			try {				
				serviceRegistry.getAuthenticationService().authenticate(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD).toCharArray());
			} 
			catch (Exception e) {			
				throw new RuntimeException(e);
			}
			HashMap<String, Object> model = new HashMap<String, Object>();
			String xmlParams = req.getParameter(FORM_PARAM_ARGS);
			Map<String, Object> params = parseXmlParams(xmlParams);
			String userName = (String) params.get(PARAM_USERNAME);
			String firstName = (String) params.get(PARAM_NAME);
			String lastName = (String) params.get(PARAM_SURNAME1);
			String email = (String) params.get("email");
			String grup = (String) params.get("grup");		
			AuthorityService authorityService = serviceRegistry.getAuthorityService();
			Set<String> userGroups = authorityService.getAuthoritiesForUser(userName);	
			PropertyMap properties = new PropertyMap(7);
			properties.put(ContentModel.PROP_USERNAME, userName);
			properties.put(ContentModel.PROP_FIRSTNAME, firstName);
			properties.put(ContentModel.PROP_LASTNAME, lastName);
			properties.put(ContentModel.PROP_EMAIL, email);

			if (!personService.personExists(userName)) {		
				log.debug("User " + userName + " don't exists, creating user...");				
				personService.createPerson(properties);					
				log.debug("Created user: " + userName);				
			}
			else {
				log.debug("User " + userName + " exists, updating user properties...");			
				personService.setPersonProperties(userName, properties);				
				log.debug("Updated user: " + userName);
			}
			handleUserGroups(userGroups, authorityService, userName, grup);
			return model;
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param userGroups
	 * @param authorityService
	 * @param userName
	 * @param grup
	 */
	private void handleUserGroups(Set<String> userGroups, AuthorityService authorityService, String userName, String grup){
		Iterator<String> ugIt = userGroups.iterator();
		while(ugIt.hasNext()){
			String g = ugIt.next();
			if(g.startsWith("GROUP_") && !g.startsWith("GROUP_site")){						
				if(!g.equals("GROUP_EVERYONE")){
					authorityService.removeAuthority(g, userName);
				}
			}
		}
		log.debug("Previous groups of user: " + userName + " cleared");
		//reload user groups
		userGroups = authorityService.getAuthoritiesForUser(userName);
		if(!userGroups.contains("GROUP_" + grup)){
			if(!authorityService.authorityExists("GROUP_"+grup)){
				authorityService.createAuthority(AuthorityType.GROUP, grup);
				log.debug("Group created: " + grup);
			}						
			authorityService.addAuthority("GROUP_"+grup, userName);
			log.debug("Adding user " + userName + " to group: " + grup);
		}
	}

	/**
	 * Parsea el xml de parámetros.
	 * 
	 * @param xmlExpedient
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> parseXmlParams(String xml) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes(ConstantsUdL.UTF8));
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(is);
			NodeList nodeList = doc.getChildNodes().item(0).getChildNodes();
			for(int i=0;i<nodeList.getLength();i++){
				Node node = nodeList.item(i);
				result.put(node.getNodeName(), node.getFirstChild().getNodeValue());
			}
		} catch (Exception e) {
			log.error("Failed parsing xml", e);		
		}
		return result;
	}

	/**
	 * Return the noderef of the company home
	 * 
	 * @return
	 */
	protected NodeRef getCompanyHome() {
		StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
		ResultSet resultSet = searchService
				.query(storeRef, SearchService.LANGUAGE_LUCENE, "PATH:\"/app:company_home\"");
		return resultSet.getNodeRef(0);
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public AuthenticationComponent getAuthComponent() {
		return authComponent;
	}

	public void setAuthComponent(AuthenticationComponent authComponent) {
		this.authComponent = authComponent;
	}

	public AuthenticationService getAuthService() {
		return authService;
	}

	public void setAuthService(AuthenticationService authService) {
		this.authService = authService;
	}

	public TransactionService getTransactionService() {
		return transactionService;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public MutableAuthenticationDao getAuthenticationDao() {
		return authenticationDao;
	}

	public void setAuthenticationDao(MutableAuthenticationDao authenticationDao) {
		this.authenticationDao = authenticationDao;
	}

	public SearchService getSearchService() {
		return searchService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
}