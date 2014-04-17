package com.smile.actions;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Cript;
import com.smile.webscripts.helper.UdlProperties;

public class CalcularVerificacioAction extends ActionExecuterAbstractBase implements ConstantsUdL{

	private ServiceRegistry serviceRegistry;
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		String type = serviceRegistry.getNodeService().getType(nodeRef).getLocalName();
		if(type != null){
			if(type.equals("documentSimple")){
				String usernameAuth = serviceRegistry.getAuthenticationService().getCurrentUserName();
				UdlProperties props = new UdlProperties();
				try {
					serviceRegistry.getAuthenticationService().authenticate(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD).toCharArray());
				} 
				catch (Exception e) {			
					e.printStackTrace();
				}
				String sha512 = "";
				NodeService nodeService = serviceRegistry.getNodeService();
				ContentService contentService = serviceRegistry.getContentService();
				ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
				if (reader != null) {
					reader.setEncoding(ConstantsUdL.UTF8);
					try {
						sha512 = Cript.sha512(reader.getContentString());
					} catch (Exception e) {				
						e.printStackTrace();
					}
				}
				QName verificacioIntegritatValor = QName.createQName("http://www.smile.com/model/udl/1.0", "verificacio_integritat_valor_documentSimple");
				QName verificacioIntegritatAlgorisme = QName.createQName("http://www.smile.com/model/udl/1.0", "verificacio_integritat_algorisme_documentSimple");
				nodeService.setProperty(nodeRef, verificacioIntegritatValor, sha512);
				nodeService.setProperty(nodeRef, verificacioIntegritatAlgorisme, "SHA-512");
				AuthenticationUtil.setRunAsUser(usernameAuth);
				AuthenticationUtil.setFullyAuthenticatedUser(usernameAuth);			
			}
		}
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {}
}