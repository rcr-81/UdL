package com.smile.actions;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.UdlProperties;

public class RecalcularTamanyAction extends ActionExecuterAbstractBase implements ConstantsUdL {
	
	QName tamanyDocumentSimpleRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "tamany_logic_documentSimple");
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
		Date now = new Date();
		System.out.println(DateFormat.getInstance().format(now) + " START: Recalcular tamany scheduled action.");
		String usernameAuth = serviceRegistry.getAuthenticationService().getCurrentUserName();
		UdlProperties props = new UdlProperties();
		try {
			serviceRegistry.getAuthenticationService().authenticate(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD).toCharArray());
		} 
		catch (Exception e) {			
			e.printStackTrace();
		}
		NodeService nodeService = serviceRegistry.getNodeService();		
		setSize(nodeRef, nodeService);
		AuthenticationUtil.setRunAsUser(usernameAuth);
		AuthenticationUtil.setFullyAuthenticatedUser(usernameAuth);
		now = new Date();
		System.out.println(DateFormat.getInstance().format(now) + " END: Recalcular tamany scheduled action.");
	}	
	
	/**
	 * @param nodeRef
	 * @param nodeService
	 * @return
	 */
	private long setSize(NodeRef nodeRef, NodeService nodeService){
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(nodeRef);
		long totalSize = 0;
		long size = 0;
		if(childAssocs.size()>0){
			for (ChildAssociationRef childAssociationRef : childAssocs) {
				NodeRef child = childAssociationRef.getChildRef();
				if(nodeService.hasAspect(child, documentSimpleRM) || nodeService.hasAspect(child, signaturaDettachedRM)){					
					ContentData contentRef =  (ContentData)nodeService.getProperty(child, ContentModel.PROP_CONTENT);
					size = contentRef.getSize();
					totalSize += Long.valueOf(size);
				}
				else if(nodeService.hasAspect(child, expedientRM)){
					size =  setSize(child, nodeService);
					totalSize += size;
				}
				else if(nodeService.hasAspect(child, agregacioRM)){
					size =  setSize(child, nodeService);
					totalSize += size;
				}
				else if(nodeService.hasAspect(child, serieRM)){
					size =  setSize(child, nodeService);
					totalSize += size;
				}
				else if(nodeService.hasAspect(child, fonsRM)){
					size =  setSize(child, nodeService);
					totalSize += size;
				}
			}			
		}
		else {
			try {
				totalSize = (Long)nodeService.getProperties(nodeRef).get(ContentModel.PROP_SIZE_CURRENT);
			}
			catch (Exception e){
				totalSize = 0;
			}
		}
		
		long totalSizeKb = totalSize;
		if(nodeService.hasAspect(nodeRef, documentSimpleRM)){
			nodeService.setProperty(nodeRef, tamanyDocumentSimpleRM, String.valueOf(totalSizeKb));
		}	
		else if(nodeService.hasAspect(nodeRef, expedientRM)){
			nodeService.setProperty(nodeRef, tamanyExpedientRM, String.valueOf(totalSizeKb));
		}
		else if(nodeService.hasAspect(nodeRef, agregacioRM)){
			nodeService.setProperty(nodeRef, tamanyAgregacioRM, String.valueOf(totalSizeKb));
		}
		else if(nodeService.hasAspect(nodeRef, serieRM)){
			nodeService.setProperty(nodeRef, tamanySerieRM, String.valueOf(totalSizeKb));
		}
		else if(nodeService.hasAspect(nodeRef, fonsRM)){
			nodeService.setProperty(nodeRef, tamanyFonsRM, String.valueOf(totalSizeKb));
		}
		return totalSize;
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		paramList.add(new ParameterDefinitionImpl("a-parameter", DataTypeDefinition.TEXT, false, getParamDisplayLabel("a-parameter")));      
	}
}