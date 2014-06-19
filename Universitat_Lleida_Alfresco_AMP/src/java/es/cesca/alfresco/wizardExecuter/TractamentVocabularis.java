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

public class TractamentVocabularis extends ExecuterAbstractBase {

	private static Log logger = LogFactory.getLog(TractamentVocabularis.class);
	
	@Override
	protected void executeImpl(Action action, NodeRef node) {
	//Nothing to do
	}
	
	public void createNodeVocabulari(String nombreVoc) 
		throws ExecuterException, MandatoryFieldException, XMLParseException {
		
		if(logger.isDebugEnabled())
			logger.debug("createNodeVocabulari: " + nombreVoc);

		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
		
		nombreVoc = nombreVoc.replaceAll("#", "_");
		
		props.put(CescaUtil.VOCABULARIO_PROP_ID, nombreVoc);
		props.put(CescaUtil.VOCABULARIO_PROP_NAME, nombreVoc);
		props.put(ContentModel.PROP_NAME, nombreVoc);
		props.put(ContentModel.PROP_TITLE, nombreVoc);
		
		NodeRef parent = getPeticionsFolderVocabulariNode();
	
		FileInfo info = getServiceRegistry().getFileFolderService().create(parent, nombreVoc, CescaUtil.TYPE_VOCABULARI);
		NodeRef node = info.getNodeRef();
		
		getServiceRegistry().getNodeService().setProperties(node, props);	
	}
	
	public void createNodeAtribute(String nombreAtr, String valor, String tipus, boolean obligatori, String tipusDocumental, String nombreVoc) 
	throws ExecuterException, MandatoryFieldException, XMLParseException {
		
		if(logger.isDebugEnabled())
			logger.debug("createNodeAtribute: " + nombreAtr);
		
		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
		
		nombreAtr = nombreAtr.replaceAll("#", "_");
		
		props.put(CescaUtil.ATRIBUTO_PROP_ID, nombreAtr+"#"+nombreVoc);
		props.put(CescaUtil.ATRIBUTO_PROP_NAME, nombreAtr+"#"+nombreVoc);
		props.put(CescaUtil.ATRIBUTO_PROP_TIPUS, tipus);
		props.put(CescaUtil.ATRIBUTO_PROP_VALOR, valor);
		props.put(CescaUtil.ATRIBUTO_PROP_OBLIGATORI, obligatori);
		props.put(CescaUtil.ATRIBUTO_PROP_VOCABLUARI_REF, nombreVoc);
		if(!CescaUtil.isEmpty(tipusDocumental)) props.put(CescaUtil.ATRIBUTO_PROP_TIPUS_DOCUMENTAL, tipusDocumental);
		props.put(ContentModel.PROP_NAME, nombreAtr+"#"+nombreVoc);
		props.put(ContentModel.PROP_TITLE, nombreAtr+"#"+nombreVoc);
		
		NodeRef parent = getPeticionsFolderAtributosNode();

		//crea node de tipus petició amb les metadades corresponents
		FileInfo info = getServiceRegistry().getFileFolderService().create(parent, nombreAtr+"#"+nombreVoc, CescaUtil.TYPE_ATRIBUTO);
		NodeRef node = info.getNodeRef();
		
		getServiceRegistry().getNodeService().setProperties(node, props);
	}
	
	public void editNodeAtribute(NodeRef noderef, String nombreAtr, String valor, String tipus, boolean obligatori, String tipusDocumental, String nombreVoc) 
	throws ExecuterException, MandatoryFieldException, XMLParseException {
		
		if(logger.isDebugEnabled())
			logger.debug("editNodeAtribute: "+ noderef.getId() + " nombre atributo: " + nombreAtr);
		
		nombreAtr = nombreAtr.replaceAll("#", "_");
		
	    //updateamos
		NodeService nodeService = getServiceRegistry().getNodeService();
		nodeService.setProperty(noderef, ContentModel.PROP_NAME, nombreAtr+"#"+nombreVoc);
		nodeService.setProperty(noderef, ContentModel.PROP_TITLE, nombreAtr+"#"+nombreVoc);
		nodeService.setProperty(noderef, CescaUtil.ATRIBUTO_PROP_ID, nombreAtr+"#"+nombreVoc);
		nodeService.setProperty(noderef, CescaUtil.ATRIBUTO_PROP_NAME, nombreAtr+"#"+nombreVoc);
		nodeService.setProperty(noderef, CescaUtil.ATRIBUTO_PROP_TIPUS, tipus);
		nodeService.setProperty(noderef, CescaUtil.ATRIBUTO_PROP_VALOR, valor);
		nodeService.setProperty(noderef, CescaUtil.ATRIBUTO_PROP_OBLIGATORI, obligatori);
		if(!CescaUtil.isEmpty(tipusDocumental)) nodeService.setProperty(noderef, CescaUtil.ATRIBUTO_PROP_TIPUS_DOCUMENTAL, tipusDocumental);
	}
	
	public void editNodeVocabulari(NodeRef noderef, String nombreVoc) 
	throws ExecuterException, MandatoryFieldException, XMLParseException {
		
		if(logger.isDebugEnabled())
			logger.debug("editNodeVocabulari: "+ noderef.getId() + " nombre vocabulario: " + nombreVoc);
		
		nombreVoc = nombreVoc.replaceAll("#", "_");
		
		NodeService nodeService = getServiceRegistry().getNodeService();
		nodeService.setProperty(noderef, ContentModel.PROP_NAME, nombreVoc);
		nodeService.setProperty(noderef, ContentModel.PROP_TITLE, nombreVoc);
		nodeService.setProperty(noderef, CescaUtil.VOCABULARIO_PROP_NAME, nombreVoc);
		nodeService.setProperty(noderef, CescaUtil.VOCABULARIO_PROP_ID, nombreVoc);
	}

	
	private static NodeRef peticioFolderVocabulari = null;
	private static NodeRef peticioFolderAtribut = null;
	
	protected NodeRef getPeticionsFolderVocabulariNode() {
		
		if (peticioFolderVocabulari == null) {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Cerca folder vocabularis");
			
    		peticioFolderVocabulari = searchForANode(CescaUtil.PATH_VOCABULARI);

		}
		
		if (peticioFolderVocabulari == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar els vocabularis no existeix");
		}
		return peticioFolderVocabulari;
		
	}
	
	protected NodeRef getPeticionsFolderAtributosNode() {
		
		if (peticioFolderAtribut == null) {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Cerca folder atributos");
			
    		peticioFolderAtribut = searchForANode(CescaUtil.PATH_ATRIBUTO);

		}
		
		if (peticioFolderAtribut == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar els atributos no existeix");
		}
		return peticioFolderAtribut;
		
	}
	
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> arg0) {
		//Nothing to do
	}
	
}
