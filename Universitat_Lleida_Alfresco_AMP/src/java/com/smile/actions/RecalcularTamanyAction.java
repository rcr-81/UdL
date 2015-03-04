package com.smile.actions;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.UdlProperties;

public class RecalcularTamanyAction extends ActionExecuterAbstractBase implements ConstantsUdL {
	
	QName tamanyDocumentSimpleRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "dimensions_fisiques_documentSimple");
	QName tamanyExpedientRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "dimensions_fisiques_expedient");
	QName tamanyAgregacioRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "dimensions_fisiques_agregacio");
	QName tamanySerieRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "dimensions_fisiques_serie");
	QName tamanyFonsRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "tamany_logic_fons");	
	QName documentSimpleRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "documentSimple");		
	QName signaturaDettachedRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "signaturaDettached");	
	QName expedientRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "expedient");	
	QName agregacioRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "agregacio");	
	QName serieRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "serie");	
	QName fonsRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "fons");	
	
	private ServiceRegistry serviceRegistry;
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		String usernameAuth = authenticate();
		NodeService nodeService = serviceRegistry.getNodeService();
		NodeRef parentNodeRef = nodeService.getPrimaryParent(nodeRef).getParentRef();
		
		if(nodeService.hasAspect(nodeRef, documentSimpleRM)) {			
			if(nodeService.hasAspect(parentNodeRef, expedientRM)) {
				updateExpedient(nodeService, nodeRef, parentNodeRef);
				
			}else if(nodeService.hasAspect(parentNodeRef, agregacioRM)) {
				updateAgregacio(nodeService, nodeRef, parentNodeRef);
			}
			
		}else if(nodeService.hasAspect(nodeRef, expedientRM)) {
			updateSerie(nodeService, nodeRef, parentNodeRef);
			
		}else if(nodeService.hasAspect(nodeRef, agregacioRM)) {
			updateSerie(nodeService, nodeRef, parentNodeRef);
			
		}else if(nodeService.hasAspect(nodeRef, serieRM)) {
			updateFons(nodeService, nodeRef, parentNodeRef);
		}

		AuthenticationUtil.setRunAsUser(usernameAuth);
		AuthenticationUtil.setFullyAuthenticatedUser(usernameAuth);
	}	

	/**
	 * Actualitza el tamany de l'expedient.
	 * 
	 * @param nodeRef
	 * @param parentNodeRef
	 */
	private void updateExpedient(NodeService nodeService, NodeRef docNodeRef, NodeRef expNodeRef) {
		System.out.println(DateFormat.getInstance().format(new Date()) + " START: Recalcular tamany expedient.");
		int tamany = 0;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(expNodeRef);
		
		for (ChildAssociationRef childAssoc : children) {
			NodeRef childNodeRef = childAssoc.getChildRef();
			Serializable tamanySerial = nodeService.getProperty(childNodeRef, tamanyDocumentSimpleRM);
			
			if(tamanySerial != null && !"".equals(tamanySerial)) {
				tamany = tamany + (Integer.parseInt((String)tamanySerial));
			}
		}
		
		nodeService.setProperty(expNodeRef, tamanyExpedientRM, String.valueOf(tamany));
		Date now = new Date();
		System.out.println(DateFormat.getInstance().format(now) + " Update tamany expedient: " + expNodeRef);
		System.out.println(DateFormat.getInstance().format(now) + " END: Recalcular tamany expedient.");
	}
	
	/**
	 * Actualitza el tamany de l'agregació.
	 * 
	 * @param nodeRef
	 * @param parentNodeRef
	 */
	private void updateAgregacio(NodeService nodeService, NodeRef docNodeRef, NodeRef agrNodeRef) {
		System.out.println(DateFormat.getInstance().format(new Date()) + " START: Recalcular tamany agregació.");
		int tamany = 0;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(agrNodeRef);
		
		for (ChildAssociationRef childAssoc : children) {
			NodeRef childNodeRef = childAssoc.getChildRef();
			Serializable tamanySerial = nodeService.getProperty(childNodeRef, tamanyDocumentSimpleRM);
			
			if(tamanySerial != null) {
				tamany = tamany + (Integer.parseInt((String)tamanySerial));				
			}
		}
		
		nodeService.setProperty(agrNodeRef, tamanyAgregacioRM, String.valueOf(tamany));
		Date now = new Date();
		System.out.println(DateFormat.getInstance().format(now) + " Update tamany agregació: " + agrNodeRef);
		System.out.println(DateFormat.getInstance().format(now) + " END: Recalcular tamany agregació.");
	}
	
	/**
	 * Actualitza el tamany de la sèrie.
	 * 
	 * @param nodeRef
	 * @param parentNodeRef
	 */
	private void updateSerie(NodeService nodeService, NodeRef nodeRef, NodeRef serieNodeRef) {
		System.out.println(DateFormat.getInstance().format(new Date()) + " START: Recalcular tamany sèrie.");
		int tamany = 0;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(serieNodeRef);
		
		for (ChildAssociationRef childAssoc : children) {
			NodeRef childNodeRef = childAssoc.getChildRef();

			if(nodeService.hasAspect(childNodeRef, expedientRM)) {
				Serializable tamanySerial = nodeService.getProperty(childNodeRef, tamanyExpedientRM);
				
				if(tamanySerial != null) {
					tamany = tamany + (Integer.parseInt((String)tamanySerial));				
				}
				
			}else if(nodeService.hasAspect(childNodeRef, agregacioRM)) {
				Serializable tamanySerial = nodeService.getProperty(childNodeRef, tamanyAgregacioRM);
				
				if(tamanySerial != null) {
					tamany = tamany + (Integer.parseInt((String)tamanySerial));				
				}
			}
		}
		
		nodeService.setProperty(serieNodeRef, tamanySerieRM, String.valueOf(tamany));
		Date now = new Date();
		System.out.println(DateFormat.getInstance().format(now) + " Update tamany sèrie: " + serieNodeRef);
		System.out.println(DateFormat.getInstance().format(now) + " END: Recalcular tamany sèrie.");
	}
	
	/**
	 * Actualitza el tamany del fons.
	 * 
	 * @param nodeRef
	 * @param parentNodeRef
	 */
	private void updateFons(NodeService nodeService, NodeRef serieNodeRef, NodeRef fonsNodeRef) {
		System.out.println(DateFormat.getInstance().format(new Date()) + " START: Recalcular tamany fons.");
		int tamany = 0;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(fonsNodeRef);
		
		for (ChildAssociationRef childAssoc : children) {
			NodeRef childNodeRef = childAssoc.getChildRef();
			Serializable tamanySerial = nodeService.getProperty(childNodeRef, tamanySerieRM);
			
			if(tamanySerial != null) {
				tamany = tamany + (Integer.parseInt((String)tamanySerial));				
			}
		}
		
		nodeService.setProperty(fonsNodeRef, tamanyFonsRM, String.valueOf(tamany));
		Date now = new Date();
		System.out.println(DateFormat.getInstance().format(now) + " Update tamany fons: " + fonsNodeRef);
		System.out.println(DateFormat.getInstance().format(now) + " END: Recalcular tamany fons.");
	}
	
	/**
	 * S'autentica como administrador i retorna l'usuari que ha originat l'execució de l'acció. 
	 * 
	 * @return
	 */
	private String authenticate() {
		String usernameAuth = serviceRegistry.getAuthenticationService().getCurrentUserName();
		UdlProperties props = new UdlProperties();
		try {
			serviceRegistry.getAuthenticationService().authenticate(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD).toCharArray());

		}catch (Exception e) {			
			e.printStackTrace();
		}
		
		return usernameAuth;
	}
	
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		paramList.add(new ParameterDefinitionImpl("a-parameter", DataTypeDefinition.TEXT, false, getParamDisplayLabel("a-parameter")));      
	}
}