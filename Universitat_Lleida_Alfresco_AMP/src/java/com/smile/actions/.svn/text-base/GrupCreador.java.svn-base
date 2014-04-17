package com.smile.actions;

import java.util.List;
import java.util.Set;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.UdlProperties;

public class GrupCreador extends ActionExecuterAbstractBase implements ConstantsUdL{

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
		} 
		catch (Exception e) {			
			e.printStackTrace();
		}
		QName grupCreadorExpedient = QName.createQName("http://www.smile.com/model/udl/1.0", "grup_creador_expedient");
		QName grupCreadorAgregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "grup_creador_agregacio");
		QName grupCreadorDocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "grup_creador_documentSimple");
		NodeService nodeService = serviceRegistry.getNodeService();
		String owner = serviceRegistry.getOwnableService().getOwner(nodeRef);		
		Set<String> groups = serviceRegistry.getAuthorityService().getAuthoritiesForUser(owner);			
		String grupsCreador = "";
		java.util.Iterator<String> it = groups.iterator();

		while (it.hasNext()){
			String group = StringUtils.upperCase(it.next());
			if(group.startsWith(UDL_GROUP_PREFIX)){
				String descGrupCreador = serviceRegistry.getAuthorityService().getAuthorityDisplayName(group);
				grupsCreador += descGrupCreador;
				grupsCreador += " (" + group.replace(UDL_GROUP_PREFIX, "") + ")";
				grupsCreador += ", ";
			}
		}
		if(!grupsCreador.equals("")){
			grupsCreador = grupsCreador.substring(0, grupsCreador.lastIndexOf(","));
		}

		String type = nodeService.getType(nodeRef).getLocalName();		
		if(type.equals("agregacio")){
			nodeService.setProperty(nodeRef, grupCreadorAgregacio, grupsCreador);
		}
		else if(type.equals("expedient")){
			nodeService.setProperty(nodeRef, grupCreadorExpedient, grupsCreador);
		}
		else if(type.equals("documentSimple")){
			nodeService.setProperty(nodeRef, grupCreadorDocumentSimple, grupsCreador);
		}		

		AuthenticationUtil.setRunAsUser(usernameAuth);
		AuthenticationUtil.setFullyAuthenticatedUser(usernameAuth);
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {}
}