package com.smile.webscripts;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.alfresco.service.ServiceRegistry;
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

import com.smile.webscripts.helper.ConstantsUdL;

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
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Properties propiedades = new Properties();
		HashMap<String, Object> model = new HashMap<String, Object>();
		NodeService nodeService = serviceRegistry.getNodeService();
		SearchService searchService = serviceRegistry.getSearchService();
		
		try{
			InputStream entrada = new FileInputStream("/opt/alfresco-3.4.13/tomcat/test.properties");
			propiedades.load(entrada);
			
			//String query = "ISNULL:\"udlrm:suport_origen_expedient\" AND TYPE:\"rma:recordFolder\" AND ASPECT:\"udlrm:expedient\"";
			//String query = "PARENT:\"workspace://SpacesStore/fd6aa21f-0217-450f-9433-38cc4cbd8229\" AND ISNULL:\"udlrm:suport_origen_expedient\" AND TYPE:\"rma:recordFolder\" AND ASPECT:\"udlrm:expedient\"";
		
			String query = propiedades.getProperty("query");
			System.out.println("query: " + query);
			
			ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, query);
			
			int i = 0;
			NodeRef expNodeRef = null;
			Iterator<NodeRef> it = resultSet.getNodeRefs().iterator();
			
			while (it.hasNext()) {
				expNodeRef = (NodeRef) it.next();
				nodeService.setProperty(expNodeRef, suportOrigenExp, "Físic");
				System.out.println(i++ + ": Metadada suport orígen actualitzada (Expedient: " + expNodeRef.getId().toString() + ")");
			}
			model.put("success", "OK");
			
		}catch(Exception e) {
			model.put("error", e.getMessage());
		}
		
		return model;
	}
}
