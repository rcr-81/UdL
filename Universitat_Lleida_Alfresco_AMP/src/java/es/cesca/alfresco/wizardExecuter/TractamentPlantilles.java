package es.cesca.alfresco.wizardExecuter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.modelmbean.XMLParseException;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cesca.alfresco.scheduled.ExecuterAbstractBase;
import es.cesca.alfresco.util.CescaUtil;
import es.cesca.alfresco.util.XMLHelper.MandatoryFieldException;

public class TractamentPlantilles extends ExecuterAbstractBase {

	private static Log logger = LogFactory.getLog(TractamentPlantilles.class);
	
	@Override
	protected void executeImpl(Action action, NodeRef node) {
	//Nothing to do
	}
	
	public void createNodePlantilla(String nombrePlantilla, String tipoDocumental) 
		throws ExecuterException, MandatoryFieldException, XMLParseException {
		
		if(logger.isDebugEnabled())
			logger.debug("createNodePlantilla: " + nombrePlantilla + "tipo documental: " + tipoDocumental);

		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
		
		nombrePlantilla = nombrePlantilla.replaceAll("#", "_");
		
		props.put(CescaUtil.PLANTILLA_PROP_ID, nombrePlantilla);
		props.put(CescaUtil.PLANTILLA_PROP_NAME, nombrePlantilla);
		props.put(CescaUtil.PLANTILLA_PROP_TIPO, tipoDocumental);
		props.put(ContentModel.PROP_NAME, nombrePlantilla);
		props.put(ContentModel.PROP_TITLE, nombrePlantilla);
		
		NodeRef parent = getPeticionsFolderPlantillaNode();
	
		FileInfo info = getServiceRegistry().getFileFolderService().create(parent, nombrePlantilla, CescaUtil.TYPE_PLANTILLA);
		NodeRef node = info.getNodeRef();
		
		getServiceRegistry().getNodeService().setProperties(node, props);	
	}
	
	public void createNodePlantillaWithVoc(String nombrePlantilla, String nombreVoc) 
		throws ExecuterException, MandatoryFieldException, XMLParseException {
		
		if(logger.isDebugEnabled())
			logger.debug("createNodePlantillaWithVoca: " + nombrePlantilla + "nombre vocabulario: " + nombreVoc);
	
		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
		
		nombrePlantilla = nombrePlantilla.replaceAll("#", "_");
		
		props.put(CescaUtil.PLANTILLA_PROP_ID, nombrePlantilla+"#"+nombreVoc);
		props.put(CescaUtil.PLANTILLA_PROP_NAME, nombrePlantilla+"#"+nombreVoc);
		props.put(CescaUtil.PLANTILLA_PROP_PLAN_REF, nombrePlantilla);
		props.put(CescaUtil.PLANTILLA_PROP_VOC_REF, nombreVoc);
		props.put(ContentModel.PROP_NAME, nombrePlantilla+"#"+nombreVoc);
		props.put(ContentModel.PROP_TITLE, nombrePlantilla+"#"+nombreVoc);
		
		NodeRef parent = getPeticionsFolderPlantillaNode();
	
		FileInfo info = getServiceRegistry().getFileFolderService().create(parent, nombrePlantilla+"#"+nombreVoc, CescaUtil.TYPE_PLANTILLA);
		NodeRef node = info.getNodeRef();
		
		getServiceRegistry().getNodeService().setProperties(node, props);	
	}
	
	public void editNodePlantilla(NodeRef noderef, String nombrePlantilla, String tipoDocumental) 
	throws ExecuterException, MandatoryFieldException, XMLParseException {
		
		if(logger.isDebugEnabled())
			logger.debug("editNodePlantilla: " + nombrePlantilla + "tipo documental: " + tipoDocumental);
		
		nombrePlantilla = nombrePlantilla.replaceAll("#", "_");
		
		NodeService nodeService = getServiceRegistry().getNodeService();
		nodeService.setProperty(noderef, ContentModel.PROP_NAME, nombrePlantilla);
		nodeService.setProperty(noderef, ContentModel.PROP_TITLE, nombrePlantilla);
		nodeService.setProperty(noderef, CescaUtil.PLANTILLA_PROP_ID, nombrePlantilla);
		nodeService.setProperty(noderef, CescaUtil.PLANTILLA_PROP_NAME, nombrePlantilla);
		nodeService.setProperty(noderef, CescaUtil.PLANTILLA_PROP_TIPO, tipoDocumental);
	}

	
	private static NodeRef peticioFolderPlantilla = null;
	
	protected NodeRef getPeticionsFolderPlantillaNode() {
		
		if (peticioFolderPlantilla == null) {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Cerca folder plantilles");
			
    		peticioFolderPlantilla = searchForANode(CescaUtil.PATH_PLANTILLES);

		}
		
		if (peticioFolderPlantilla == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar els plantilles no existeix");
		}
		return peticioFolderPlantilla;
		
	}
	
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> arg0) {
		//Nothing to do
	}
	
}
