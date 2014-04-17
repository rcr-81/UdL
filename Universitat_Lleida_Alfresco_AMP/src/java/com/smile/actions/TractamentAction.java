package com.smile.actions;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.UdlProperties;

public class TractamentAction extends ActionExecuterAbstractBase implements ConstantsUdL{

	private ServiceRegistry serviceRegistry;
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		String usernameAuth = serviceRegistry.getAuthenticationService().getCurrentUserName();
		UdlProperties props = new UdlProperties();
		try {
			serviceRegistry.getAuthenticationService().authenticate(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD).toCharArray());
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		NodeService nodeService = serviceRegistry.getNodeService();
		Map<QName, Serializable> metadata = nodeService.getProperties(nodeRef);
		String type = nodeService.getType(nodeRef).getLocalName();
		String tractament = "";

		QName classificacioDocument = QName.createQName("http://www.smile.com/model/udl/1.0", "classificacio_acces_documentSimple");
		QName classificacioDocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "classificacio_acces_documentSimple");
		QName classificacioAgregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "classificacio_acces_agregacio");
		QName classificacioExpedient = QName.createQName("http://www.smile.com/model/udl/1.0", "classificacio_acces_expedient");			

		String owner = serviceRegistry.getOwnableService().getOwner(nodeRef);
		PermissionService ps = serviceRegistry.getPermissionService();

		if(type.equals("document")){
			tractament = (String)metadata.get(classificacioDocument);
		}
		else if(type.equals("documentSimple")){
			tractament = (String)metadata.get(classificacioDocumentSimple);
		}
		else if(type.equals("agregacio")){
			tractament = (String)metadata.get(classificacioAgregacio); 
		}
		else if(type.equals("expedient")){
			tractament = (String)metadata.get(classificacioExpedient);
		}
		if(tractament != null){
			if(tractament.equals("Normal")){
				//inherited permissions
			}
			else if(tractament.equals("Restringit")){			
				Set<String> groups = serviceRegistry.getAuthorityService().getAuthoritiesForUser(owner);			
				ps.deletePermissions(nodeRef);			
				ps.setInheritParentPermissions(nodeRef, false);						
				java.util.Iterator<String> it = groups.iterator();
				while (it.hasNext()){
					String group = it.next();
					if(group.startsWith(UDL_GROUP_PREFIX)){
						ps.setPermission(nodeRef, group, ps.COORDINATOR, true);
					}
				}		
			}
			else if(tractament.equals("Confidencial")){
				ps.deletePermissions(nodeRef);
				ps.setInheritParentPermissions(nodeRef, false);
			}
			else if(tractament.equals("Públic")){
				//inherited permissions
			}
			else if(tractament.equals("Reservat")){
				ps.deletePermissions(nodeRef);
				ps.setInheritParentPermissions(nodeRef, false);
			}
		}

		AuthenticationUtil.setRunAsUser(usernameAuth);
		AuthenticationUtil.setFullyAuthenticatedUser(usernameAuth);
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {}
}