package com.smile.webscripts.expedient;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
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

public class TransferibleIarxiu extends DeclarativeWebScript implements ConstantsUdL {

	private ServiceRegistry serviceRegistry;

	public TransferibleIarxiu() {}
	
	public TransferibleIarxiu(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		NodeService nodeService = serviceRegistry.getNodeService();
		UdlProperties props = new UdlProperties();

		try {
			Impersonate.impersonate(props.getProperty(ADMIN_USERNAME));
			Element args = Arguments.getArguments(req);
			boolean transferible = Boolean.parseBoolean(args.getElementsByTagName(FORM_PARAM_EXP_TRANSFERIBLE).item(0).getFirstChild().getNodeValue());
			String expID = args.getElementsByTagName(FORM_PARAM_EXP_ID).item(0).getFirstChild().getNodeValue();
			NodeRef expedientNodeRef = new NodeRef(AUDIT_NODEREF_PREFIX + expID);

			if(nodeService.hasAspect(expedientNodeRef, ConstantsUdL.ASPECT_TRANSFERIBLE_IARXIU)) {
				nodeService.setProperty(expedientNodeRef, PROP_TRANSFERIBLE, transferible);
				
			}else {
				properties.put(PROP_TRANSFERIBLE, transferible);
				nodeService.addAspect(expedientNodeRef, ConstantsUdL.ASPECT_TRANSFERIBLE_IARXIU, properties);				
			}
			
			if(transferible) {
				model.put(PARAM_MESSAGE, PARAM_MESSAGE_TRANSFERIBLE_IARXIU);
				
			}else {
				model.put(PARAM_MESSAGE, PARAM_MESSAGE_NO_TRANSFERIBLE_IARXIU);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
}