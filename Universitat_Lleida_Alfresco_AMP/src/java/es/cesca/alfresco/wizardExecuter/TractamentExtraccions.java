package es.cesca.alfresco.wizardExecuter;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.cesca.alfresco.Object.Extraccio;
import es.cesca.alfresco.scheduled.ExecuterAbstractBase;
import es.cesca.alfresco.util.CescaUtil;
import es.cesca.alfresco.util.XMLHelper.MandatoryFieldException;

public class TractamentExtraccions extends ExecuterAbstractBase {

	private static Log logger = LogFactory.getLog(TractamentExtraccions.class);
	
	@Override
	protected void executeImpl(Action action, NodeRef node) {
	//Nothing to do
	}
	
	public void executeExtraccio(String nombreExtraccio, Extraccio extraccio, String idConfiguracio) 
		throws ExecuterException, MandatoryFieldException, XMLParseException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("executeExtraccio: " + nombreExtraccio);
		
		try{
			saveContentAndCreateNodes(nombreExtraccio, extraccio, idConfiguracio);
		}catch(Exception e){
			if (logger.isDebugEnabled()) {
				logger.debug("Error: " + e.getMessage());
			}
			throw new Exception(e.getMessage());
		}
	}
	
	private void saveContentAndCreateNodes(String nombreExtraccio, Extraccio extraccio, String idConfiguracio) throws Exception {
		if(logger.isDebugEnabled())
			logger.debug("saveContentAndCreateNodes: " + nombreExtraccio);
		
		try{
			if(extraccio.isCheckboxExpedient()){
				System.out.println("estamos guardando extraccion de documentos con expedientes");
				createNodeConfiguracio(extraccio, nombreExtraccio, idConfiguracio);
				createNodeExpedient(extraccio, idConfiguracio);
				createNodeDocument(extraccio, idConfiguracio);
				if(extraccio.getSelSign()!=null&&!extraccio.getSelSign().trim().equals("")) createNodeSignatura(extraccio, idConfiguracio);
			}else if(extraccio.isCheckboxDocument()){
				System.out.println("estamos guardando extraccion de documentos sin expedientes");
				createNodeConfiguracioDocSenseExpedient(extraccio, nombreExtraccio, idConfiguracio);
				createNodeDocumentSinExp(extraccio, idConfiguracio);
				if(extraccio.getSelSign()!=null&&!extraccio.getSelSign().trim().equals("")) createNodeSignaturaDocSenseExpedient(extraccio, idConfiguracio);
			}
			//Creamos nodo extraccions para todos los casos
			System.out.println("creados ficheros de configuracion y xml, ahora creamos objeto extraccion");
			createNodeExtraccioFinal(nombreExtraccio, extraccio);	
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}
	}

	private void createNodeExtraccioFinal(String nombreExtraccio,
			Extraccio extraccio) {
		
		System.out.println("createNodeExtraccioFinal: valores a crear " + nombreExtraccio);
		System.out.println("createNodeExtraccioFinal: valores a crear " + extraccio.getPlantillaDoc());
		System.out.println("createNodeExtraccioFinal: valores a crear " + extraccio.getPlantillaExp());
		System.out.println("createNodeExtraccioFinal: valores a crear " + extraccio.getPlantillaSign());
		System.out.println("createNodeExtraccioFinal: valores a crear " + extraccio.getSelDoc());
		System.out.println("createNodeExtraccioFinal: valores a crear " + extraccio.getSelExp());
		System.out.println("createNodeExtraccioFinal: valores a crear " + extraccio.getSelSign());
		System.out.println("createNodeExtraccioFinal: valores a crear " + extraccio.getTipusDocumentalDoc());
		System.out.println("createNodeExtraccioFinal: valores a crear " + extraccio.getTipusDocumentalExp());
		System.out.println("createNodeExtraccioFinal: valores a crear " + extraccio.getTipusDocumentalSign());
			
		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
				
		String [] split1 = null;
		String [] split2 = null;
		String [] split3 = null;
		if(extraccio.getPlantillaDoc()!=null) split1 = extraccio.getPlantillaDoc().split("#");
		if(extraccio.getPlantillaExp()!=null) split2 = extraccio.getPlantillaExp().split("#");
		if(extraccio.getPlantillaSign()!=null) split3 = extraccio.getPlantillaSign().split("#");
		
		nombreExtraccio = nombreExtraccio.replaceAll("#", "_");
		
		props.put(CescaUtil.EXTRACCIO_PROP_ID, nombreExtraccio);
		props.put(CescaUtil.EXTRACCIO_PROP_NAME, nombreExtraccio);
		
		if(split1!=null) props.put(CescaUtil.EXTRACCIO_PROP_PLA_DOC, split1[0]);
		if(split2!=null) props.put(CescaUtil.EXTRACCIO_PROP_PLA_EXP, split2[0]);
		if(split3!=null) props.put(CescaUtil.EXTRACCIO_PROP_PLA_SIG, split3[0]);
		
		props.put(CescaUtil.EXTRACCIO_PROP_SEL_DOC, extraccio.getSelDoc());
		props.put(CescaUtil.EXTRACCIO_PROP_SEL_EXP, extraccio.getSelExp());
		props.put(CescaUtil.EXTRACCIO_PROP_SEL_SIG, extraccio.getSelSign());
		
		props.put(CescaUtil.EXTRACCIO_PROP_TIPUS_DOC, extraccio.getTipusDocumentalDoc());
		props.put(CescaUtil.EXTRACCIO_PROP_TIPUS_EXP, extraccio.getTipusDocumentalExp());
		props.put(CescaUtil.EXTRACCIO_PROP_TIPUS_SIG, extraccio.getTipusDocumentalSign());

		String[] fileNameSplitExp = null;
		String[] fileNameSplitDoc = null;
		String[] fileNameSplitSign = null;
		
		if(extraccio.isCheckboxExpedient()) fileNameSplitExp = extraccio.getTipusDocumentalExp().split(":");
		if(extraccio.isCheckboxDocument()) fileNameSplitDoc = extraccio.getTipusDocumentalDoc().split(":");
		if(extraccio.getTipusDocumentalSign()!=null) fileNameSplitSign = extraccio.getTipusDocumentalSign().split(":");
		
		if(fileNameSplitExp!=null){
			if(fileNameSplitExp.length>1){
				props.put(CescaUtil.EXTRACCIO_PROP_REF_FILE_CONF, nombreExtraccio+"_cfg");
				props.put(CescaUtil.EXTRACCIO_PROP_REF_FILE_EXP, nombreExtraccio + "_cfg.xml");
			}
			else {
				props.put(CescaUtil.EXTRACCIO_PROP_REF_FILE_CONF, nombreExtraccio+"_cfg");
				props.put(CescaUtil.EXTRACCIO_PROP_REF_FILE_EXP, nombreExtraccio + "_cfg.xml");
			}
			
		}
		if(fileNameSplitDoc!=null){
			if(fileNameSplitDoc.length>1) {
				if(!extraccio.isCheckboxExpedient()){
					props.put(CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC, nombreExtraccio + "_docind_cfg.xml");
					props.put(CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC_SENSE_EXP, nombreExtraccio+"_cfg");
				}else{
					props.put(CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC, nombreExtraccio + "_docexp_cfg.xml");
				}
			}
			else {
				if(!extraccio.isCheckboxExpedient()){
					props.put(CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC, nombreExtraccio + "_docind_cfg.xml");
				}else{
					props.put(CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC, nombreExtraccio + "_docexp_cfg.xml");
				}
			}
		}
		if(fileNameSplitSign!=null){
			if(fileNameSplitSign.length>1) {
				if(!extraccio.isCheckboxExpedient()){
					props.put(CescaUtil.EXTRACCIO_PROP_REF_FILE_SIGN, nombreExtraccio + "_signdocind_cfg.xml");
				}else{
					props.put(CescaUtil.EXTRACCIO_PROP_REF_FILE_SIGN, nombreExtraccio + "_signdocexp_cfg.xml");
				}
			}
			else {
				if(!extraccio.isCheckboxExpedient()){
					props.put(CescaUtil.EXTRACCIO_PROP_REF_FILE_SIGN, nombreExtraccio + "_signdocind_cfg.xml");
				}else{
					props.put(CescaUtil.EXTRACCIO_PROP_REF_FILE_SIGN, nombreExtraccio + "_signdocexp_cfg.xml");
				}
			}
		}
		
		props.put(ContentModel.PROP_NAME, nombreExtraccio);
		props.put(ContentModel.PROP_TITLE, nombreExtraccio);
		
		NodeRef parent = getPeticionsFolderExtraccioNode();

		FileInfo info = getServiceRegistry().getFileFolderService().create(parent, nombreExtraccio, CescaUtil.TYPE_EXTRACCIO);
		NodeRef node = info.getNodeRef();
		
		getServiceRegistry().getNodeService().setProperties(node, props);
	}

	public void createNodeConfiguracio(Extraccio extraccio, String nombreExtraccio, String idConfiguracio) {
		//String[] fileNameSplit = extraccio.getTipusDocumentalExp().split(":");
		//String fileName = fileNameSplit[1]+"_cfg";
		String fileName = nombreExtraccio+"_cfg";
		NodeRef parentExp = getPeticionsFolderConfiguracioNode();
		
		if(logger.isDebugEnabled())
			logger.debug("createNodeConfiguracio: " + fileName);
		
		Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();

		editProps.put(ContentModel.PROP_NAME, fileName);
		editProps.put(ContentModel.PROP_TITLE, fileName);
		editProps.put(CescaUtil.CONFIGURACIO_PROP_CRITERI_CERCA, extraccio.getSelExp());
		editProps.put(CescaUtil.CONFIGURACIO_PROP_EN_SUSPENS, false);
		editProps.put(CescaUtil.CONFIGURACIO_PROP_PETICIO_DOC_EXP, true);
		editProps.put(CescaUtil.CONFIGURACIO_PROP_ID_CONFIG, idConfiguracio);
		
		FileInfo info = getServiceRegistry().getFileFolderService().create(parentExp, fileName, CescaUtil.TYPE_CONFIGURACIO);
		NodeRef node = info.getNodeRef();
		
		getServiceRegistry().getNodeService().setProperties(node, editProps);
	}
	
	public void createNodeSignatura(Extraccio extraccio, String idConfiguracio) throws Exception{
		System.out.println("creando createNodeSignatura");
		if(extraccio.getPlantillaSign()!=null){
			//String[] fileNameSplit = extraccio.getTipusDocumentalSign().split(":");
			String fileName = extraccio.getName() + "_signdocexp_cfg.xml";
			NodeRef parentExp = getPeticionsFolderSignaturaNode();
			
			if(logger.isDebugEnabled())
				logger.debug("createNodeSignatura: " + fileName);
			
			Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();
	
			editProps.put(ContentModel.PROP_NAME, fileName);
			editProps.put(ContentModel.PROP_TITLE, fileName);
			editProps.put(CescaUtil.CFG_SIG_PROP_TIPUS_SIGNATURA, extraccio.getTipusDocumentalSign());
			//editProps.put(CescaUtil.CFG_SIG_PROP_TIPUS_SIGNATURA, "cm:content");
			editProps.put(CescaUtil.CFG_SIG_PROP_EN_SUSPENS, false);
			editProps.put(CescaUtil.CFG_SIG_PROP_PETICIO_SIGN_DOC_IND, false);
			editProps.put(CescaUtil.CFG_SIG_PROP_ID_CONFIG, idConfiguracio);
	
			FileInfo info = getServiceRegistry().getFileFolderService().create(parentExp, fileName, CescaUtil.TYPE_SIGNATURA);
			NodeRef fileNodeRef = info.getNodeRef();
			
			getServiceRegistry().getNodeService().setProperties(fileNodeRef, editProps);
	
			if (logger.isDebugEnabled()) {
				logger.debug("Vamos a crear xml signatura: " + fileName);
			}
	
			ContentWriter writer = this.getServiceRegistry().getContentService().getWriter(fileNodeRef,
					ContentModel.PROP_CONTENT, true);
	
			writer.setMimetype("application/xml");
			writer.setEncoding("UTF-8");
			try{
				writer.putContent(new String(generaFitxerExtraccio(extraccio.getPlantillaSign(), extraccio)));
			}catch(Exception e){
				throw new Exception(e.getMessage());
			}
		}
	}
	
	public void createNodeSignaturaDocSenseExpedient(Extraccio extraccio, String idConfiguracio) throws Exception{
		if(extraccio.getPlantillaSign()!=null){
			System.out.println("creando createNodeSignaturaDocSenseExpedient");
			//String[] fileNameSplit = extraccio.getTipusDocumentalSign().split(":");
			String fileName = extraccio.getName() + "_signdocind_cfg.xml";
			NodeRef parentExp = getPeticionsFolderSignaturaNode();
			
			if(logger.isDebugEnabled())
				logger.debug("createNodeSignaturaDocSenseExpedient: " + fileName);
			
			Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();
	
			editProps.put(ContentModel.PROP_NAME, fileName);
			editProps.put(ContentModel.PROP_TITLE, fileName);
			editProps.put(CescaUtil.CFG_SIG_PROP_TIPUS_SIGNATURA, extraccio.getTipusDocumentalSign());
			//editProps.put(CescaUtil.CFG_SIG_PROP_TIPUS_SIGNATURA, "cm:content");
			editProps.put(CescaUtil.CFG_SIG_PROP_EN_SUSPENS, false);
			editProps.put(CescaUtil.CFG_SIG_PROP_PETICIO_SIGN_DOC_IND, true);
			editProps.put(CescaUtil.CFG_SIG_PROP_ID_CONFIG, idConfiguracio);
	
			System.out.println("filename createNodeSignaturaDocSenseExpedient " + fileName);
			FileInfo info = getServiceRegistry().getFileFolderService().create(parentExp, fileName, CescaUtil.TYPE_SIGNATURA);
			NodeRef fileNodeRef = info.getNodeRef();
			
			getServiceRegistry().getNodeService().setProperties(fileNodeRef, editProps);
	
			if (logger.isDebugEnabled()) {
				logger.debug("Vamos a crear xml signatura: " + fileName);
			}
	
			ContentWriter writer = this.getServiceRegistry().getContentService().getWriter(fileNodeRef,
					ContentModel.PROP_CONTENT, true);
	
			writer.setMimetype("application/xml");
			writer.setEncoding("UTF-8");
			try{
				writer.putContent(new String(generaFitxerExtraccio(extraccio.getPlantillaSign(), extraccio)));
			}catch(Exception e){
				throw new Exception(e.getMessage());
			}
		}
	}
	
	public void createNodeDocumentSinExp(Extraccio extraccio, String idConfiguracio) throws Exception{
		System.out.println("creando createNodeDocumentSinExp");
		//String[] fileNameSplit = extraccio.getTipusDocumentalDoc().split(":");
		String fileName = extraccio.getName() + "_docind_cfg.xml";
		NodeRef parentExp = getPeticionsFolderDocumentNode();
		
		if(logger.isDebugEnabled())
			logger.debug("createNodeDocumentSinExp: " + fileName);
		
		Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();

		editProps.put(ContentModel.PROP_NAME, fileName);
		editProps.put(ContentModel.PROP_TITLE, fileName);
		editProps.put(CescaUtil.CFG_DOC_PROP_CRITERI_CERCA_SIG, extraccio.getSelSign());
		//editProps.put(CescaUtil.CFG_DOC_PROP_TIPUS_DOCUMENT, "cm:content");
		editProps.put(CescaUtil.CFG_DOC_PROP_TIPUS_DOCUMENT, extraccio.getTipusDocumentalDoc());
		editProps.put(CescaUtil.CFG_DOC_PROP_EN_SUSPENS, false);
		editProps.put(CescaUtil.CFG_DOC_PROP_PETICIO_DOC_IND, true);
		editProps.put(CescaUtil.CFG_DOC_PROP_ID_CONFIG, idConfiguracio);

		System.out.println("filename createNodeDocumentSinExp " + fileName);
		
		FileInfo info = getServiceRegistry().getFileFolderService().create(parentExp, fileName, CescaUtil.TYPE_DOCUMENT);
		NodeRef fileNodeRef = info.getNodeRef();
		
		getServiceRegistry().getNodeService().setProperties(fileNodeRef, editProps);

		if (logger.isDebugEnabled()) {
			logger.debug("Vamos a crear xml document: " + fileName);
		}

		ContentWriter writer = this.getServiceRegistry().getContentService().getWriter(fileNodeRef,
				ContentModel.PROP_CONTENT, true);

		writer.setMimetype("application/xml");
		writer.setEncoding("UTF-8");
		try{
			writer.putContent(new String(generaFitxerExtraccio(extraccio.getPlantillaDoc(), extraccio)));
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}

	}
	
	public void createNodeDocument(Extraccio extraccio, String idConfiguracio) throws Exception{
		//String[] fileNameSplit = extraccio.getTipusDocumentalDoc().split(":");
		String fileName = extraccio.getName() + "_docexp_cfg.xml";
		NodeRef parentExp = getPeticionsFolderDocumentNode();
		
		if(logger.isDebugEnabled())
			logger.debug("createNodeDocument: " + fileName);
		
		Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();

		editProps.put(ContentModel.PROP_NAME, fileName);
		editProps.put(ContentModel.PROP_TITLE, fileName);
		editProps.put(CescaUtil.CFG_DOC_PROP_CRITERI_CERCA_SIG, extraccio.getSelSign());
		editProps.put(CescaUtil.CFG_DOC_PROP_TIPUS_DOCUMENT, extraccio.getTipusDocumentalDoc());
		editProps.put(CescaUtil.CFG_DOC_PROP_EN_SUSPENS, false);
		editProps.put(CescaUtil.CFG_DOC_PROP_PETICIO_DOC_IND, false);
		editProps.put(CescaUtil.CFG_DOC_PROP_ID_CONFIG, idConfiguracio);

		FileInfo info = getServiceRegistry().getFileFolderService().create(parentExp, fileName, CescaUtil.TYPE_DOCUMENT);
		NodeRef fileNodeRef = info.getNodeRef();
		
		getServiceRegistry().getNodeService().setProperties(fileNodeRef, editProps);

		if (logger.isDebugEnabled()) {
			logger.debug("Vamos a crear xml document: " + fileName);
		}

		ContentWriter writer = this.getServiceRegistry().getContentService().getWriter(fileNodeRef,
				ContentModel.PROP_CONTENT, true);

		writer.setMimetype("application/xml");
		writer.setEncoding("UTF-8");
		try{
			writer.putContent(new String(generaFitxerExtraccio(extraccio.getPlantillaDoc(), extraccio)));
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}

	}
	
	public void createNodeConfiguracioDocSenseExpedient(Extraccio extraccio, String nombreExtraccio, String idConfiguracio) throws Exception{
		//String[] fileNameSplit = extraccio.getTipusDocumentalDoc().split(":");
		//String fileName = fileNameSplit[1]+"_cfg";
		String fileName = nombreExtraccio+"_cfg";
		//NodeRef parentExp = getPeticionsFolderDocsSenseExpNode();
		NodeRef parentExp = getPeticionsFolderConfiguracioNode();
		
		if(logger.isDebugEnabled())
			logger.debug("createNodeConfiguracioDocSenseExpedient: " + fileName);
		
		Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();

		editProps.put(ContentModel.PROP_NAME, fileName);
		editProps.put(ContentModel.PROP_TITLE, fileName);
		//editProps.put(CescaUtil.CFG_DOC_PROP_CRITERI_CERCA_SIG_SENSE, extraccio.getSelSign());
		//editProps.put(CescaUtil.CFG_DOC_PROP_EN_SUSPENS_SENSE, false);
		editProps.put(CescaUtil.CONFIGURACIO_PROP_CRITERI_CERCA, extraccio.getSelDoc());
		editProps.put(CescaUtil.CONFIGURACIO_PROP_EN_SUSPENS, false);
		editProps.put(CescaUtil.CONFIGURACIO_PROP_PETICIO_DOC_EXP, false);
		editProps.put(CescaUtil.CONFIGURACIO_PROP_ID_CONFIG, idConfiguracio);
		
		//FileInfo info = getServiceRegistry().getFileFolderService().create(parentExp, fileName, CescaUtil.TYPE_DOCUMENT_SENSE);
		FileInfo info = getServiceRegistry().getFileFolderService().create(parentExp, fileName, CescaUtil.TYPE_CONFIGURACIO);
		NodeRef node = info.getNodeRef();
		
		getServiceRegistry().getNodeService().setProperties(node, editProps);

		if (logger.isDebugEnabled()) {
			logger.debug("Created file node for file: " + fileName);
		}
	}

	public void createNodeExpedient(Extraccio extraccio, String idConfiguracio) throws Exception{
		//String[] fileNameSplit = extraccio.getTipusDocumentalExp().split(":");
		String fileName = extraccio.getName() + "_cfg.xml";
		NodeRef parentExp = getPeticionsFolderExpedientNode();
		
		if(logger.isDebugEnabled())
			logger.debug("createNodeExpedient: " + fileName);
		
		Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();

		editProps.put(ContentModel.PROP_NAME, fileName);
		editProps.put(ContentModel.PROP_TITLE, fileName);
		editProps.put(CescaUtil.CFG_EXP_PROP_CRITERI_CERCA_DOCS, extraccio.getSelDoc());
		editProps.put(CescaUtil.CFG_EXP_PROP_TIPUS_EXPEDIENT, extraccio.getTipusDocumentalExp());
		editProps.put(CescaUtil.CFG_EXP_PROP_EN_SUSPENS, false);
		editProps.put(CescaUtil.CFG_EXP_PROP_ID_CONFIG, idConfiguracio);

		FileInfo info = getServiceRegistry().getFileFolderService().create(parentExp, fileName, CescaUtil.TYPE_EXPEDIENT);
		NodeRef fileNodeRef = info.getNodeRef();
		
		getServiceRegistry().getNodeService().setProperties(fileNodeRef, editProps);

		if (logger.isDebugEnabled()) {
			logger.debug("Vamos a crear xml expedient: " + fileName);
		}

		ContentWriter writer = this.getServiceRegistry().getContentService().getWriter(fileNodeRef,
				ContentModel.PROP_CONTENT, true);

		writer.setMimetype("application/xml");
		writer.setEncoding("UTF-8");
		try{
			writer.putContent(new String(generaFitxerExtraccio(extraccio.getPlantillaExp(), extraccio)));
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}
	}
	
	public void editNodePlantilla(NodeRef noderef, String nombrePlantilla, String tipoDocumental) 
	throws ExecuterException, MandatoryFieldException, XMLParseException {
		
		if(logger.isDebugEnabled())
			logger.debug("editNodePlantilla: " + nombrePlantilla + " tipo documental " + tipoDocumental);
		
		NodeService nodeService = getServiceRegistry().getNodeService();
		nodeService.setProperty(noderef, ContentModel.PROP_NAME, nombrePlantilla);
		nodeService.setProperty(noderef, ContentModel.PROP_TITLE, nombrePlantilla);
		nodeService.setProperty(noderef, CescaUtil.PLANTILLA_PROP_ID, nombrePlantilla);
		nodeService.setProperty(noderef, CescaUtil.PLANTILLA_PROP_NAME, nombrePlantilla);
		nodeService.setProperty(noderef, CescaUtil.PLANTILLA_PROP_TIPO, tipoDocumental);
	}

	
	private static NodeRef peticioFolderExtraccio = null;
	private static NodeRef peticioFolderExpedient = null;
	private static NodeRef peticioFolderDocument = null;
	private static NodeRef peticioFolderSignatura = null;
	private static NodeRef peticioFolderConfiguracio = null;
	private static NodeRef peticioFolderDocsSenseExp = null;
	
	protected NodeRef getPeticionsFolderExtraccioNode() {
		
		if (peticioFolderExtraccio == null) {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Cerca folder extraccions");
			
    		peticioFolderExtraccio = searchForANode(CescaUtil.PATH_EXTRACCIONS);

		}
		
		if (peticioFolderExtraccio == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar les extraccions no existeix");
		}
		return peticioFolderExtraccio;
		
	}
	
	protected NodeRef getPeticionsFolderExpedientNode() {
		
		if (peticioFolderExpedient == null) {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Cerca folder expedientes");
			
    		peticioFolderExpedient = searchForANode(CescaUtil.PATH_EXPEDIENTES);

		}
		
		if (peticioFolderExpedient == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar els expedients no existeix");
		}
		return peticioFolderExpedient;
		
	}
	
	protected NodeRef getPeticionsFolderDocumentNode() {
		
		if (peticioFolderDocument == null) {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Cerca folder documents");
			
    		peticioFolderDocument = searchForANode(CescaUtil.PATH_DOCUMENTS);

		}
		
		if (peticioFolderDocument == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar els documents no existeix");
		}
		return peticioFolderDocument;
		
	}
	
	protected NodeRef getPeticionsFolderSignaturaNode() {
		
		if (peticioFolderSignatura == null) {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Cerca folder signatures");
			
    		peticioFolderSignatura = searchForANode(CescaUtil.PATH_SIGNATURES);

		}
		
		if (peticioFolderSignatura == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar les signatures no existeix");
		}
		return peticioFolderSignatura;
		
	}
	
	protected NodeRef getPeticionsFolderConfiguracioNode() {
		
		if (peticioFolderConfiguracio == null) {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Cerca folder configuracions");
			
    		peticioFolderConfiguracio = searchForANode(CescaUtil.PATH_CONFIG);

		}
		
		if (peticioFolderConfiguracio == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar les configuracions no existeix");
		}
		return peticioFolderConfiguracio;
		
	}
	
	protected NodeRef getPeticionsFolderDocsSenseExpNode() {
		
		if (peticioFolderDocsSenseExp == null) {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Cerca folder documents sense exp");
			
    		peticioFolderDocsSenseExp = searchForANode(CescaUtil.PATH_DOCUMENTS_SENSE);

		}
		
		if (peticioFolderDocsSenseExp == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar les configuracions dels documents sense exp no existeix");
		}
		return peticioFolderDocsSenseExp;
		
	}
	
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> arg0) {
		//Nothing to do
	}
	
	public byte[] generaFitxerExtraccio(String tipusPlantilla, Extraccio extraccio) throws Exception {
		if(logger.isDebugEnabled())
			logger.debug("generaFitxerExtraccio: " + tipusPlantilla); 
		
		ByteArrayOutputStream output = null;
		 byte[] outputArray = null;
		 
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document document = null;
			Element raiz = null;
			String[] tipusPlantillaSplit = tipusPlantilla.split("#");
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				document = builder.newDocument();
				if(tipusPlantillaSplit[1].equals("Expedient")) raiz = document.createElement("tipus_expedient");
				else if(tipusPlantillaSplit[1].equals("Document")) raiz = document.createElement("tipus_document");
				else if(tipusPlantillaSplit[1].equals("Signatura")) raiz = document.createElement("tipus_signatura");
				
				raiz.setAttribute("plantilla", "urn:iarxiu:2.0:templates:cesca:"+tipusPlantillaSplit[0]);
				
				logger.debug("es crea arrel...........");
			} catch(Exception e) {
				logger.error("Error generant el fitxer XML", e);
				throw new Exception(e.getMessage());
			}
		
			String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Plantilles//.\" +TYPE:\"cesca:CESCA_PLANTILLA\" +@cesca\\:plantillaRef:\""+tipusPlantillaSplit[0]+"\"";
        	
        	SearchParameters sp = new SearchParameters();
    		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
    		sp.setQuery(query);
    		sp.addStore(CescaUtil.STORE);
    		
    		ResultSet resultSet = null;
    		ResultSet resultSetAtr = null;
    		String obligatori = null;
    
    		try {
    			if(logger.isDebugEnabled())
    				logger.debug("query(generaFitxerExtraccio para plantillas) : " + query); 
    			
    			resultSet = serviceRegistry.getSearchService().query(sp);
                if(resultSet != null&&resultSet.length()>0) {
                	for (NodeRef rowPlan : resultSet.getNodeRefs()) {
                		FileInfo foPlan = this.getServiceRegistry().getFileFolderService().getFileInfo(rowPlan);
                		String[] nombreVoc = foPlan.getProperties().get(CescaUtil.PLANTILLA_PROP_NAME).toString().split("#");
                		String vocabulario = nombreVoc[1];
                		
                		if(nombreVoc[0].equals(tipusPlantillaSplit[0])){
	            			Element voc = document.createElement("vocabulari");
	                		voc.setAttribute("name", "urn:iarxiu:2.0:vocabularies:cesca:"+vocabulario);
	                		
	                		if(logger.isDebugEnabled())
	            				logger.debug("nombre vocabulario plantilla : " + vocabulario); 
	
	                		String queryAtr = "+PATH:\"/app:company_home/cm:CESCA/cm:Atributs//.\" +TYPE:\"cesca:CESCA_ATRIBUTO\" +@cesca\\:vocabulario_ref:\""+vocabulario+"\"";
	                    	
	                		SearchParameters spAtr = new SearchParameters();
	                		spAtr.setLanguage(SearchService.LANGUAGE_LUCENE);
	                		spAtr.setQuery(queryAtr);
	                		spAtr.addStore(CescaUtil.STORE);
	                		
	                		if(logger.isDebugEnabled())
	            				logger.debug("query(generaFitxerExtraccio para vocabularios) : " + query); 
	                		
	                		resultSetAtr = serviceRegistry.getSearchService().query(spAtr);
	                		if(resultSetAtr!=null&&resultSetAtr.length()>0){
	                			for (NodeRef rowAtr : resultSetAtr.getNodeRefs()) {
	                				FileInfo foAtr = this.getServiceRegistry().getFileFolderService().getFileInfo(rowAtr);
	                				String nomAtribut = (String) foAtr.getProperties().get(CescaUtil.ATRIBUTO_PROP_NAME);
	            					String valor = (String) foAtr.getProperties().get(CescaUtil.ATRIBUTO_PROP_VALOR);
	            					String obligatoriBoolean = (String) foAtr.getProperties().get(CescaUtil.ATRIBUTO_PROP_OBLIGATORI);
	             					String tipus = (String) foAtr.getProperties().get(CescaUtil.ATRIBUTO_PROP_TIPUS);
	             					
	            					String[] nomAtributSplit = nomAtribut.split("#");
	            					
	            					if(nomAtributSplit[1].equals(vocabulario)){
		            					if(obligatoriBoolean.equals("true")) obligatori = "si";
		            					else obligatori = "no";
		            					
		            					Element atr = document.createElement("atribut");
		            					atr.setAttribute("name", nomAtributSplit[0]);
		            					
		            					Element nodeValor = document.createElement("valor");
		            					nodeValor.appendChild(document.createTextNode(valor));
		            					
		            					Element nodeOblig = document.createElement("obligatori");
		            					nodeOblig.appendChild(document.createTextNode(obligatori));
		            					
		            					Element nodeTipus = document.createElement("tipus");
		            					nodeTipus.appendChild(document.createTextNode(tipus));
		            					
		            					atr.appendChild(nodeValor);
		            					atr.appendChild(nodeOblig);
		            					atr.appendChild(nodeTipus);
		            					
		            					voc.appendChild(atr);
	            					}
	                			}
	                		}
	                		resultSetAtr.close();
	                		raiz.appendChild(voc);
                		}
                	}
                }
            }catch(Exception e){
            	logger.error(e.getMessage(),e);
            	e.printStackTrace();
            	throw new Exception("Error generando XML de la plantilla " + tipusPlantillaSplit[0]);
    		} finally {
        		if(logger.isDebugEnabled())
        			logger.debug("Tanca resultset");
    			if (resultSet != null) {
    				resultSet.close();
    			}
    			if (resultSetAtr != null) {
    				resultSetAtr.close();
    			}
    		}
        	document.appendChild(raiz);
			try {	
				if(logger.isDebugEnabled())
    				logger.debug("DOM : "); 
				
				DOMSource source = new DOMSource(document);
				
				output = new ByteArrayOutputStream();
	            Result result = new StreamResult(output);
	            TransformerFactory fact = TransformerFactory.newInstance();
	            Transformer transformer = fact.newTransformer();
	            transformer.transform(source, result);
	            
	            outputArray = output.toByteArray();

			} catch(TransformerConfigurationException e) {
				logger.error("Transformer configuration error", e);
				e.printStackTrace();
				throw new Exception("Error generant DOM de la plantilla " + tipusPlantillaSplit[0]);
			} catch(TransformerException e) {
				logger.error("Transformer exception", e);
				e.printStackTrace();
				throw new Exception("Error generant DOM de la plantilla " + tipusPlantillaSplit[0]);
			}		
		return outputArray;
	}
	
	public void editNodeExtraccio(NodeRef noderef, String nombreExtraccio, Extraccio extraccio) 
		throws ExecuterException, MandatoryFieldException, XMLParseException {
		
		System.out.println("createNodeExtraccioFinal: valores a editar " + nombreExtraccio);
		System.out.println("createNodeExtraccioFinal: valores a editar " + extraccio.getPlantillaDoc());
		System.out.println("createNodeExtraccioFinal: valores a editar " + extraccio.getPlantillaExp());
		System.out.println("createNodeExtraccioFinal: valores a editar " + extraccio.getPlantillaSign());
		System.out.println("createNodeExtraccioFinal: valores a editar " + extraccio.getSelDoc());
		System.out.println("createNodeExtraccioFinal: valores a editar " + extraccio.getSelExp());
		System.out.println("createNodeExtraccioFinal: valores a editar " + extraccio.getSelSign());
		System.out.println("createNodeExtraccioFinal: valores a editar " + extraccio.getTipusDocumentalDoc());
		System.out.println("createNodeExtraccioFinal: valores a editar " + extraccio.getTipusDocumentalExp());
		System.out.println("createNodeExtraccioFinal: valores a editar " + extraccio.getTipusDocumentalSign());
			
		
		String [] split1 = null;
		String [] split2 = null;
		String [] split3 = null;
		if(extraccio.getPlantillaDoc()!=null) split1 = extraccio.getPlantillaDoc().split("#");
		if(extraccio.getPlantillaExp()!=null) split2 = extraccio.getPlantillaExp().split("#");
		if(extraccio.getPlantillaSign()!=null) split3 = extraccio.getPlantillaSign().split("#");
		
	    //updateamos
		
		NodeService nodeService = getServiceRegistry().getNodeService();
		
		nombreExtraccio = nombreExtraccio.replaceAll("#", "_");
		
		nodeService.setProperty(noderef, ContentModel.PROP_NAME, nombreExtraccio);
		nodeService.setProperty(noderef, ContentModel.PROP_TITLE, nombreExtraccio);
		nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_ID, nombreExtraccio);
		nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_NAME, nombreExtraccio);
		
		if(split1!=null) nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_PLA_DOC, split1[0]);
		else nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_PLA_DOC, null);
		if(split2!=null) nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_PLA_EXP, split2[0]);
		else nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_PLA_EXP, null);
		if(split3!=null) nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_PLA_SIG,  split3[0]);
		else nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_PLA_SIG,  null);
		
		nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_SEL_DOC, extraccio.getSelDoc());
		nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_SEL_EXP,  extraccio.getSelExp());
		nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_SEL_SIG, extraccio.getSelSign());
		
		nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_TIPUS_DOC, extraccio.getTipusDocumentalDoc());
		nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_TIPUS_EXP, extraccio.getTipusDocumentalExp());
		nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_TIPUS_SIG, extraccio.getTipusDocumentalSign());
		
		String[] fileNameSplitExp = null;
		String[] fileNameSplitDoc = null;
		String[] fileNameSplitSign = null;
		
		if(extraccio.isCheckboxExpedient()) fileNameSplitExp = extraccio.getTipusDocumentalExp().split(":");
		if(extraccio.isCheckboxDocument()) fileNameSplitDoc = extraccio.getTipusDocumentalDoc().split(":");
		if(extraccio.getTipusDocumentalSign()!=null) fileNameSplitSign = extraccio.getTipusDocumentalSign().split(":");
		
		if(fileNameSplitExp!=null) {
			nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_CONF, nombreExtraccio + "_cfg");
			nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_EXP, nombreExtraccio + "_cfg.xml");
		}else{
			nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_CONF, null);
			nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_EXP, null);
		}
		
		if(fileNameSplitDoc!=null){
			if(fileNameSplitDoc.length>1) {
				if(!extraccio.isCheckboxExpedient()){
					nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC, nombreExtraccio + "_docind_cfg.xml");
					nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC_SENSE_EXP, nombreExtraccio + "_cfg");
				}else{
					nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC, nombreExtraccio + "_docexp_cfg.xml");
					nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC_SENSE_EXP, null);
				}
			}
			else {
				if(!extraccio.isCheckboxExpedient()){
					nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC, nombreExtraccio + "_docind_cfg.xml");
					nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC_SENSE_EXP, nombreExtraccio+"_cfg");
				}else{
					nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC, nombreExtraccio + "_docexp_cfg.xml");
					nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC_SENSE_EXP, null);
				}
			}
		}else {
			nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC, null);
			nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC_SENSE_EXP, null);
		}
	
		if(fileNameSplitSign!=null) {
			if(fileNameSplitSign.length>1) {
				if(!extraccio.isCheckboxExpedient()){
					nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_SIGN, nombreExtraccio + "_signdocind_cfg.xml");
				}else{
					nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_SIGN, nombreExtraccio + "_signdocexp_cfg.xml");
				}
			}
			else {
				if(!extraccio.isCheckboxExpedient()){
					nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_SIGN, nombreExtraccio + "_signdocind_cfg.xml");
				}else{
					nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_SIGN, nombreExtraccio + "_signdocexp_cfg.xml");
				}
			}
		}
		else nodeService.setProperty(noderef, CescaUtil.EXTRACCIO_PROP_REF_FILE_SIGN, null);
	}
	
	/*public void editNodeConfig(NodeRef noderef, Extraccio extraccio) 
		throws ExecuterException, MandatoryFieldException, XMLParseException {
		
		if(logger.isDebugEnabled())
			logger.debug("Inici editNodeAtribute: "+ noderef.getId());
		
	    //updateamos
		NodeService nodeService = getServiceRegistry().getNodeService();
		nodeService.setProperty(noderef, CescaUtil.CONFIGURACIO_PROP_CRITERI_CERCA, extraccio.getSelExp());
	}
	
	public void editNodeExpedient(NodeRef noderef, Extraccio extraccio) 
		throws ExecuterException, MandatoryFieldException, XMLParseException {
		
		if(logger.isDebugEnabled())
			logger.debug("Inici editNodeExpedient: "+ noderef.getId());
		
	    //updateamos
		NodeService nodeService = getServiceRegistry().getNodeService();
		nodeService.setProperty(noderef, CescaUtil.CFG_EXP_PROP_CRITERI_CERCA_DOCS, extraccio.getSelDoc());
	}
	
	public void editNodeDocument(NodeRef noderef, Extraccio extraccio) 
		throws ExecuterException, MandatoryFieldException, XMLParseException {
		
		if(logger.isDebugEnabled())
			logger.debug("Inici editNodeDocument: "+ noderef.getId());
		
	    //updateamos
		NodeService nodeService = getServiceRegistry().getNodeService();
		nodeService.setProperty(noderef, CescaUtil.CFG_DOC_PROP_CRITERI_CERCA_SIG, extraccio.getSelSign());
	}*/
}
