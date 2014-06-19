package es.cesca.alfresco.wizard;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.wizard.BaseWizardBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;

import es.cesca.alfresco.Object.Extraccio;
import es.cesca.alfresco.scheduled.ExecuterAbstractBase.ExecuterException;
import es.cesca.alfresco.util.CescaUtil;
import es.cesca.alfresco.wizardExecuter.TractamentExtraccions;

public class ExtraccioWizard extends BaseWizardBean{

	private static final long serialVersionUID = -557955106388369334L;
	private static Log logger = LogFactory.getLog(ExtraccioWizard.class);
	private ServiceRegistry serviceRegistry;
	
	private static String EXPEDIENT = "Expedient";
	private static String DOCUMENT  = "Document";
	private static String SIGNATURA = "Signatura";

	//Extraccio
	private String nombreExtraccio;

	private String tipusDocumentalExp;
	private String tipusDocumentalDoc;
	private String tipusDocumentalSign;
	
	private String selExp;
	private String selDoc;
	private String selSign;
	
	private String plantillaExp;
	private String plantillaDoc;
	private String plantillaSign;
	
	private String plantillaConfigRefOld;
	private String plantillaExpRefOld;
	private String plantillaDocRefOld;
	private String plantillaDocSenseExpRefOld;
	private String plantillaSignRefOld;
	
	//Params
	private String error = null;
	private String result = null;
	private String edit = null;
	private transient DataModel nodes;
	private NodeRef nodeReferencia = null;

	protected List<SelectItem> tipusDocumentals;
	
	protected List<SelectItem> listPlantilla;
	protected List<SelectItem> listPlantillaDoc;
	protected List<SelectItem> listPlantillaSign;
	
	private boolean checkboxExpedient;
	private boolean checkboxDocument;
	
	public String getPlantillaDocSenseExpRefOld() {
		return plantillaDocSenseExpRefOld;
	}

	public void setPlantillaDocSenseExpRefOld(String plantillaDocSenseExpRefOld) {
		this.plantillaDocSenseExpRefOld = plantillaDocSenseExpRefOld;
	}
	
	public List<SelectItem> getListPlantillaDoc() {
		if(this.listPlantillaDoc==null){
			showTablePlantillasDoc(DOCUMENT);
		}
		return listPlantillaDoc;
	}

	public void setListPlantillaDoc(List<SelectItem> listPlantillaDoc) {
		this.listPlantillaDoc = listPlantillaDoc;
	}

	public List<SelectItem> getListPlantillaSign() {
		if(this.listPlantillaSign==null){
			showTablePlantillasSign(SIGNATURA);
		}
		return listPlantillaSign;
	}

	public void setListPlantillaSign(List<SelectItem> listPlantillaSign) {
		this.listPlantillaSign = listPlantillaSign;
	}

	public String getPlantillaConfigRefOld() {
		return plantillaConfigRefOld;
	}

	public void setPlantillaConfigRefOld(String plantillaConfigRefOld) {
		this.plantillaConfigRefOld = plantillaConfigRefOld;
	}

	public String getPlantillaExpRefOld() {
		return plantillaExpRefOld;
	}

	public void setPlantillaExpRefOld(String plantillaExpRefOld) {
		this.plantillaExpRefOld = plantillaExpRefOld;
	}

	public String getPlantillaDocRefOld() {
		return plantillaDocRefOld;
	}

	public void setPlantillaDocRefOld(String plantillaDocRefOld) {
		this.plantillaDocRefOld = plantillaDocRefOld;
	}

	public String getPlantillaSignRefOld() {
		return plantillaSignRefOld;
	}

	public void setPlantillaSignRefOld(String plantillaSignRefOld) {
		this.plantillaSignRefOld = plantillaSignRefOld;
	}
	
	private String tipusPlantilla;

	public String getTipusPlantilla() {
		return tipusPlantilla;
	}

	public void setTipusPlantilla(String tipusPlantilla) {
		this.tipusPlantilla = tipusPlantilla;
	}

	public String getTipusDocumentalExp() {
		return tipusDocumentalExp;
	}

	public void setTipusDocumentalExp(String tipusDocumentalExp) {
		this.tipusDocumentalExp = tipusDocumentalExp;
	}

	public String getTipusDocumentalDoc() {
		return tipusDocumentalDoc;
	}

	public void setTipusDocumentalDoc(String tipusDocumentalDoc) {
		this.tipusDocumentalDoc = tipusDocumentalDoc;
	}

	public String getTipusDocumentalSign() {
		return tipusDocumentalSign;
	}

	public void setTipusDocumentalSign(String tipusDocumentalSign) {
		this.tipusDocumentalSign = tipusDocumentalSign;
	}

	public String getSelExp() {
		return selExp;
	}

	public void setSelExp(String selExp) {
		this.selExp = selExp;
	}

	public String getSelDoc() {
		return selDoc;
	}

	public void setSelDoc(String selDoc) {
		this.selDoc = selDoc;
	}

	public String getSelSign() {
		return selSign;
	}

	public void setSelSign(String selSign) {
		this.selSign = selSign;
	}

	public String getPlantillaExp() {
		return plantillaExp;
	}

	public void setPlantillaExp(String plantillaExp) {
		this.plantillaExp = plantillaExp;
	}

	public String getPlantillaDoc() {
		return plantillaDoc;
	}

	public void setPlantillaDoc(String plantillaDoc) {
		this.plantillaDoc = plantillaDoc;
	}

	public String getPlantillaSign() {
		return plantillaSign;
	}

	public void setPlantillaSign(String plantillaSign) {
		this.plantillaSign = plantillaSign;
	}

	public String getNombreExtraccio() {
		return nombreExtraccio;
	}

	public void setNombreExtraccio(String nombreExtraccio) {
		this.nombreExtraccio = nombreExtraccio;
	}
	
	public List<SelectItem> getListPlantilla() {
		if(this.listPlantilla==null){
			showTablePlantillas(EXPEDIENT);
		}
		return this.listPlantilla;
	}

	public void setListPlantilla(List<SelectItem> listPlantilla) {
		this.listPlantilla = listPlantilla;
	}

	public boolean isCheckboxExpedient() {
		return checkboxExpedient;
	}

	public void setCheckboxExpedient(boolean checkboxExpedient) {
		this.checkboxExpedient = checkboxExpedient;
	}

	public boolean isCheckboxDocument() {
		return checkboxDocument;
	}

	public void setCheckboxDocument(boolean checkboxDocument) {
		this.checkboxDocument = checkboxDocument;
	}
	
	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}
	
	public NodeRef getNodeReferencia() {
		return nodeReferencia;
	}

	public void setNodeReferencia(NodeRef nodeReferencia) {
		this.nodeReferencia = nodeReferencia;
	}

	public static Log getLogger() {
		return logger;
	}

	public static void setLogger(Log logger) {
		ExtraccioWizard.logger = logger;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getnombreExtraccio() {
		return nombreExtraccio;
	}

	public void setnombreExtraccio(String nombreExtraccio) {
		this.nombreExtraccio = nombreExtraccio;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public DataModel getNodes() {
		return nodes;
	}

	public void setNodes(DataModel nodes) {
		this.nodes = nodes;
	}

	public void setTipusDocumentals(List<SelectItem> tipusDocumentals) {
		this.tipusDocumentals = tipusDocumentals;
	}

	@Override
	public void init(Map<String, String> parameters) {
		if (logger.isDebugEnabled())
			logger.debug("init");
		
		super.init(parameters);
		this.error = null;
		this.result = null;
		resetFields();
		showTableExtraccions();
	}
	
	@Override
	protected String finishImpl(FacesContext ctx, String outcome)
			throws Throwable {
		return outcome;
	}
	
	protected String formatMessage(String txt, String _2replace) {
		return MessageFormat.format(Application.getMessage(
                FacesContext.getCurrentInstance(), txt), _2replace);
	}
	
	protected String formatMessage(String txt) {
		return Application.getMessage(FacesContext.getCurrentInstance(), txt);		
	}
	
	public void nouEditaExtraccioButton(ActionEvent event){
		if (logger.isDebugEnabled())
			logger.debug("nouEditaExtraccioButton ");
		
		if(CescaUtil.isEmpty(this.nombreExtraccio)){
			this.error = formatMessage("emptyFieldExtraccio");
			this.result = null;
			if (logger.isDebugEnabled())
				logger.debug("error nombre extraccio vacio ");
			return;
		}
		//Insertamos o modificamos atributo
		TractamentExtraccions executer = new TractamentExtraccions();
		executer.setServiceRegistry(serviceRegistry);
		
		Extraccio extraccio = new Extraccio();
		
		extraccio.setName(this.nombreExtraccio);
		
		if(!this.isCheckboxDocument()&&!this.isCheckboxExpedient()){
			this.error = formatMessage("emptyField");
			this.result = null;
			if (logger.isDebugEnabled())
				logger.debug("es obligatorio expedientes o documentos ");
			return;
		}
		if(this.isCheckboxExpedient()){
			extraccio.setCheckboxExpedient(this.isCheckboxExpedient());
			extraccio.setPlantillaExp(this.plantillaExp);
			extraccio.setSelExp(this.selExp);
			extraccio.setTipusDocumentalExp(this.tipusDocumentalExp);
			if(!this.isCheckboxDocument()){
				this.error = formatMessage("emptyFieldExtraccioDoc");
				this.result = null;
				if (logger.isDebugEnabled())
					logger.debug("el documento no puede estar vacío ");
				return;
			}
		}
		if(this.isCheckboxDocument()){
			extraccio.setCheckboxDocument(this.isCheckboxDocument());
			extraccio.setPlantillaDoc(this.plantillaDoc);
			extraccio.setSelDoc(this.selDoc);
			extraccio.setTipusDocumentalDoc(this.tipusDocumentalDoc);
			System.out.println("Set sel sign: " + this.selSign);
			//if(!CescaUtil.isEmpty(this.selSign)){
			System.out.println("Se setean los datos de la extracció referentes a la signatura");
			extraccio.setPlantillaSign(this.plantillaSign);
			extraccio.setSelSign(this.selSign);
			extraccio.setTipusDocumentalSign(this.tipusDocumentalSign);
			//}
		}
		try{
			Date d = new Date();
			String idConfiguracio = new Long(d.getTime()).toString();
			
			if(this.edit!=null){
				if (logger.isDebugEnabled())
					logger.debug("editant extraccio ");
				System.out.println("Editant extraccio...");
					boolean hasOldFileExp = false;
					boolean hasOldFileDoc = false;
					
					if(!CescaUtil.isEmpty(this.plantillaConfigRefOld)&&!CescaUtil.isEmpty(this.plantillaExpRefOld)) {
						if (logger.isDebugEnabled())
							logger.debug("editando fichero configuracion " + this.plantillaConfigRefOld);
						System.out.println("editando fichero configuracion " + this.plantillaConfigRefOld);
						//deleteFileConfig(this.plantillaConfigRefOld);
						idConfiguracio = modifyFileConfig(this.plantillaConfigRefOld, extraccio, this.nombreExtraccio, idConfiguracio, executer);
						
	    			}
					
					if(!CescaUtil.isEmpty(this.plantillaConfigRefOld)&&!CescaUtil.isEmpty(this.plantillaExpRefOld) && this.isCheckboxExpedient() && this.isCheckboxDocument()) {
						if (logger.isDebugEnabled())
							logger.debug("editando fichero expediente " + this.plantillaExpRefOld);
						System.out.println("editando fichero expediente " + this.plantillaExpRefOld);
						//deleteFileExpedient(this.plantillaExpRefOld);
						modifyFileExpedient(this.plantillaExpRefOld, extraccio, idConfiguracio, executer);
						if(this.isCheckboxExpedient()){
	    					if (logger.isDebugEnabled())
	    						logger.debug("creando fichero configuracion/expediente ");
	    					
	    					//executer.createNodeConfiguracio(extraccio, this.nombreExtraccio, idConfiguracio);
	    					//executer.createNodeExpedient(extraccio, idConfiguracio);
	    				}
	    				hasOldFileExp = true;
					}
					
					//abarcamos el caso en que se modifique el tipo de extraccion de expedientes a una de documentos
					if(!CescaUtil.isEmpty(this.plantillaConfigRefOld)&&!CescaUtil.isEmpty(this.plantillaExpRefOld)
							&& !this.isCheckboxExpedient() && this.isCheckboxDocument()) {
						
						deleteFileExpedient(this.plantillaExpRefOld);
					}
					
					//No tenia expedientes para referencia
					if(!hasOldFileExp){
	    				if(this.isCheckboxExpedient()) {
	    					if (logger.isDebugEnabled())
	    						logger.debug("creando fichero configuracion/expediente ");
	    					
	    					executer.createNodeExpedient(extraccio, idConfiguracio);
	    					executer.createNodeConfiguracio(extraccio, this.nombreExtraccio, idConfiguracio);
	    				}
	    			}
					
					if(!CescaUtil.isEmpty(this.plantillaDocRefOld)) {
						if (logger.isDebugEnabled())
							logger.debug("editando fichero documento " + this.plantillaDocRefOld);
						System.out.println("editando fichero documento " + this.plantillaDocRefOld);
						
						//deleteFileDocument(this.plantillaDocRefOld);
						/*if(!CescaUtil.isEmpty(this.plantillaDocSenseExpRefOld)) {
							if (logger.isDebugEnabled())
								logger.debug("editando fichero configuracion documento " + this.plantillaDocSenseExpRefOld);
							
							System.out.println("editando fichero documento " + this.plantillaDocSenseExpRefOld);
							deleteFileConfig(this.plantillaDocSenseExpRefOld);
						}
						if(!CescaUtil.isEmpty(this.plantillaSignRefOld)) {
							if (logger.isDebugEnabled())
								logger.debug("editando fichero signatura " + this.plantillaSignRefOld);
							System.out.println("editando fichero signatura " + this.plantillaSignRefOld);
							deleteFileSignatura(this.plantillaSignRefOld);
						}*/
						
						if(this.isCheckboxDocument()) {
							if(!this.isCheckboxExpedient()) {
								if (logger.isDebugEnabled())
		    						logger.debug("creando fichero configuracion documento ");
								
								//aseguramos cambios de tipo de extracciones
								if(!CescaUtil.isEmpty(this.plantillaConfigRefOld)&&!CescaUtil.isEmpty(this.plantillaExpRefOld)) {
									System.out.println("Editant la configuracio sense expedient amb canvi de tipologia: " + this.plantillaConfigRefOld);
									idConfiguracio = modifyNodeConfiguracioDocSenseExpedient(this.plantillaConfigRefOld, extraccio, this.nombreExtraccio, idConfiguracio, executer);
									System.out.println("ID configuracio editant extraccio per docs independents: " + idConfiguracio);
									//executer.createNodeDocumentSinExp(extraccio, idConfiguracio);
									modifyNodeDocumentSinExp(this.plantillaDocRefOld, extraccio, idConfiguracio, executer);
								} else {
									//executer.createNodeConfiguracioDocSenseExpedient(extraccio, this.nombreExtraccio, idConfiguracio);
									System.out.println("Editant la configuracio sense expedient: " + this.plantillaDocSenseExpRefOld);
									idConfiguracio = modifyNodeConfiguracioDocSenseExpedient(this.plantillaDocSenseExpRefOld, extraccio, this.nombreExtraccio, idConfiguracio, executer);
									System.out.println("ID configuracio editant extraccio per docs independents: " + idConfiguracio);
									//executer.createNodeDocumentSinExp(extraccio, idConfiguracio);
									modifyNodeDocumentSinExp(this.plantillaDocRefOld, extraccio, idConfiguracio, executer);
								}
										
							}else{
								//executer.createNodeDocument(extraccio, idConfiguracio);
								modifyNodeDocument(this.plantillaDocRefOld, extraccio, idConfiguracio, executer);
							}
							if (logger.isDebugEnabled())
	    						logger.debug("creando fichero documento ");
													
							if(!CescaUtil.isEmpty(this.selSign)){
								if (logger.isDebugEnabled())
		    						logger.debug("creando fichero signatura ");
								if(!this.isCheckboxExpedient()) {
									//executer.createNodeSignaturaDocSenseExpedient(extraccio, idConfiguracio);
									modifyNodeSignaturaDocSenseExpedient(this.plantillaSignRefOld, extraccio, idConfiguracio, executer);
									
								}else{
									//executer.createNodeSignatura(extraccio, idConfiguracio);
									modifyNodeSignatura(this.plantillaSignRefOld, extraccio, idConfiguracio, executer);
								}
							}
						}
						hasOldFileDoc = true;
	    			}
					//No tenia documentos para referencia
					if(!hasOldFileDoc){
						System.out.println("sin documentos de referencia");
	    				if(this.isCheckboxDocument()) {
	    					if(!this.isCheckboxExpedient()) {
	    						if (logger.isDebugEnabled())
	        						logger.debug("creando fichero configuracion documento ");
	    						
	    						executer.createNodeConfiguracioDocSenseExpedient(extraccio, this.nombreExtraccio, idConfiguracio);
	    						executer.createNodeDocumentSinExp(extraccio, idConfiguracio);
	    					}else{
								executer.createNodeDocument(extraccio, idConfiguracio);
							}
	    					if (logger.isDebugEnabled())
	    						logger.debug("creando fichero documento ");
	    					
							if(!CescaUtil.isEmpty(this.selSign)){
								if (logger.isDebugEnabled())
		    						logger.debug("creando fichero signatura ");
								
								if(!this.isCheckboxExpedient()) {
									executer.createNodeSignaturaDocSenseExpedient(extraccio, idConfiguracio);
								}else{
									executer.createNodeSignatura(extraccio, idConfiguracio);
								}
	    					}
						}
					}
					
					//try{
					executer.editNodeExtraccio(
							this.nodeReferencia, this.nombreExtraccio, extraccio);
					/*}catch (Exception e){
						logger.error(e.getMessage());
						this.error = formatMessage("already_exists_extraccio", e.getMessage());
						this.result = null;
						this.showTableExtraccions();
						return;
						}*/
	    			
					this.result = formatMessage("OK_edit_extraccio");
	    			this.error = null;
	    			
	    			//Mostramos lista de extracciones
	    			showTableExtraccions();
	    			resetFieldsBack();
			}else{
				//try{
					executer.executeExtraccio(this.nombreExtraccio, extraccio, idConfiguracio);
					this.result = formatMessage("OK_extraccio");
	    			this.error = null;
	    			//Mostramos lista de extracciones
	    			showTableExtraccions();
	    			resetFieldsBack();
				/*}catch (Exception e){
					logger.error(e.getMessage(),e);
	    			e.printStackTrace();
					this.error = formatMessage("already_exists_extraccio", e.getMessage());
					this.result = null;
					this.showTableExtraccions();
					}
				}*/
			}
		}catch (FileExistsException e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			this.error= formatMessage("already_exists_extraccio", e.getMessage());
			this.result = null;
			this.showTableExtraccions();
		} catch (ExecuterException e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			this.error = formatMessage("error_generic", e.getMessage());
			this.result = null;
			this.showTableExtraccions();
		} catch (AlfrescoRuntimeException e) {
			this.error = formatMessage("error_generic", e.getMessage());
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			this.showTableExtraccions();
		} catch (Exception e){
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			this.error = formatMessage("error_generic_extraccio", e.getMessage());
			this.result = null;
			showTableExtraccions();
		}	
	} 	
	
	public void editExtraccio(ActionEvent event) {
		if (logger.isDebugEnabled())
			logger.debug("editExtraccio ");
		
		FacesContext ctx = FacesContext.getCurrentInstance();  
        String editExtraccio = (String) ctx.getExternalContext().getRequestParameterMap().get("editExtraccio"); 
        System.out.println("Editant l'extracció: " + editExtraccio);
        if(editExtraccio!=null){
        	String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Extraccions//.\" +TYPE:\"cesca:CESCA_EXTRACCIO\" +@cesca\\:nombreExtraccio:\""+editExtraccio+"\"";
        	
    		//Executa la consulta
        	SearchParameters sp = new SearchParameters();
    		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
    		sp.setQuery(query);
    		sp.addStore(CescaUtil.STORE);
    		
    		ResultSet resultSet = null;
    		this.nombreExtraccio = editExtraccio;
    		try {
    			if (logger.isDebugEnabled())
    				logger.debug("query (editExtraccio) " + query);
    			
    			resultSet = serviceRegistry.getSearchService().query(sp);
                if(resultSet != null&&resultSet.length()>0) {
                	//Siempre devolverà solo un resultado
                	NodeRef noderef = resultSet.getNodeRef(0);
                	this.nodeReferencia = noderef;
                	FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(noderef);
                	if(fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_EXP)!=null&&fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_CONF)!=null) {
                		this.checkboxExpedient = true;
                		this.selExp = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_SEL_EXP);
                		this.tipusDocumentalExp = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_TIPUS_EXP);
                		this.plantillaExp = fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_PLA_EXP)+"#Expedient";
                		this.plantillaExpRefOld = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_EXP);
                		this.plantillaConfigRefOld = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_CONF);
                	}else{
                		this.checkboxExpedient = false;
                		this.selExp = null;
                		this.tipusDocumentalExp = null;
                		this.plantillaExp = null;
                		this.plantillaExpRefOld = null;
                		this.plantillaConfigRefOld = null;
                	}
                	
                	if(fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC)!=null) {
                		this.checkboxDocument = true;
                		this.selDoc = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_SEL_DOC);
                		this.tipusDocumentalDoc = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_TIPUS_DOC);
                		this.plantillaDoc = fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_PLA_DOC)+"#Document";
                		this.plantillaDocRefOld = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC);
                	}else{
                		this.checkboxDocument = false;
                		this.selDoc = null;
                		this.tipusDocumentalDoc = null;
                		this.plantillaDoc = null;
                		this.plantillaDocRefOld = null;
                	}
                	
                	if(fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC_SENSE_EXP)!=null) {
                		this.plantillaDocSenseExpRefOld = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC_SENSE_EXP);
                	}
                	else{
                		this.plantillaDocSenseExpRefOld = null;
                	}
                	
                	if(fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_SIGN)!=null) {
                		System.out.println("DENTRO DEL IF DE SIGN");
                		this.selSign = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_SEL_SIG);
                		this.tipusDocumentalSign = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_TIPUS_SIG);
                		this.plantillaSign = fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_PLA_SIG)+"#Signatura";
                		this.plantillaSignRefOld = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_SIGN);
                	}
                	else{
                		System.out.println("DENTRO DEL ELSE DE SIGN");
                		this.selSign = null;
                		this.tipusDocumentalSign = null;
                		this.plantillaSign = null;
                		this.plantillaSignRefOld = null;
                	}
                	
         			this.error = null;
        			this.result = null;
        			this.edit = "OK";
                }
    		}catch (Exception e) {
    			this.error = e.getMessage();
    			this.result = null;
    			logger.error(e.getMessage(),e);
    			e.printStackTrace();
    		} finally {
    			
        		if(logger.isDebugEnabled())
        			logger.debug("Tanca resultset");
    			if (resultSet != null) {
    				resultSet.close();
    			}
    		}
        }
	}
	
	private void showTableExtraccions() {
		if (logger.isDebugEnabled())
			logger.debug("showTableExtraccions ");
		
		String query = null;
		query = "+PATH:\"/app:company_home/cm:CESCA/cm:Extraccions//.\" +TYPE:\"cesca:CESCA_EXTRACCIO\"";
		
		if (logger.isDebugEnabled())
			logger.debug("Query > "+ query);

		//Executa la consulta
    	SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (showTableExtraccions) " + query);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
            if(resultSet != null) {
            	List<Map<String, Serializable>> list = new ArrayList<Map<String, Serializable>>();
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		Map<String, Serializable> map = new HashMap<String, Serializable>(4);
            		map.put("nombreExtraccio", fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_NAME));
            		map.put("expedient", fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_TIPUS_EXP));
            		map.put("document", fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_TIPUS_DOC));
            		map.put("signatura", fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_TIPUS_SIG));
            		list.add(map);
				}
    			nodes = new ListDataModel(list);
    			}
		
		} catch (Exception e) {
			error = e.getMessage();
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}	
		}
	}
	
	public void validarSign(ActionEvent event){
		if (logger.isDebugEnabled())
			logger.debug("validarSign ");
		
		if(!CescaUtil.isEmpty(this.selSign)){
			String query = this.selSign;
			query = query.replaceAll("query.", "").replaceAll("dmattr.", "");
			
			SearchParameters sp = new SearchParameters();
			sp.setLanguage(SearchService.LANGUAGE_LUCENE);
			sp.setQuery(query);
			sp.addStore(CescaUtil.STORE);
			
			ResultSet resultSet = null;
			try {
				if (logger.isDebugEnabled())
					logger.debug("query (validarSign) " + query);
				
				resultSet = serviceRegistry.getSearchService().query(sp);
				if(resultSet!=null&&resultSet.length()>0){
					this.error = null;
	    			this.result = formatMessage("OKQuery");
				}else{
					this.error = formatMessage("errorQuery");
					this.result = null;
				}
			}catch(Exception e){
				this.error = formatMessage("errorQuery");
				this.result = null;
				logger.error(e.getMessage(), e);
    			e.printStackTrace();
			}finally{
				if (resultSet != null) {
					resultSet.close();
				}
			}
		}
	}
	
	public void validarDoc(ActionEvent event){
		if (logger.isDebugEnabled())
			logger.debug("validarDoc ");
		
		if(!CescaUtil.isEmpty(this.selDoc)){
			String query = this.selDoc;
			query.replace("query.", "").replace("dmattr.", "");
			//Executa la consulta
	    	SearchParameters sp = new SearchParameters();
	    	sp.setLanguage(SearchService.LANGUAGE_LUCENE);
			sp.setQuery(query);
			sp.addStore(CescaUtil.STORE);
			
			ResultSet resultSet = null;
			try {
				if (logger.isDebugEnabled())
					logger.debug("query (validarDoc) " + query);
				
				resultSet = serviceRegistry.getSearchService().query(sp);
				if(resultSet!=null&&resultSet.length()>0){
					this.error = null;
	    			this.result = formatMessage("OKQuery");
				}else{
					this.error = formatMessage("errorQuery");
					this.result = null;
				}
			}catch(Exception e){
				this.error = formatMessage("errorQuery");
				this.result = null;
				logger.error(e.getMessage(), e);
    			e.printStackTrace();
			}finally{
				if (resultSet != null) {
					resultSet.close();
				}
			}
		}
	}
	
	public void validarExp(ActionEvent event){
		if (logger.isDebugEnabled())
			logger.debug("validarExp ");
		
		if(!CescaUtil.isEmpty(this.selExp)){
			String query = this.selExp;
			query.replace("query.", "").replace("dmattr.", "");
	    	SearchParameters sp = new SearchParameters();
	    	sp.setLanguage(SearchService.LANGUAGE_LUCENE);
			sp.setQuery(query);
			sp.addStore(CescaUtil.STORE);

			ResultSet resultSet = null;
			try {
				if (logger.isDebugEnabled())
					logger.debug("query (validarExp) " + query);
				
				resultSet = serviceRegistry.getSearchService().query(sp);
				if(resultSet!=null&&resultSet.length()>0){
					this.error = null;
	    			this.result = formatMessage("OKQuery");
				}else{
					this.error = formatMessage("errorQuery");
					this.result = null;
				}
			}catch(Exception e){
				this.error = formatMessage("errorQuery");
				this.result = null;
				logger.error(e.getMessage(), e);
    			e.printStackTrace();
			}finally{
				if (resultSet != null) {
					resultSet.close();
				}
			}
		}
	}
	
	public void deleteExtraccions(ActionEvent event) {
		if (logger.isDebugEnabled())
			logger.debug("deleteExtraccions ");
		
		deleteAll();
		showTableExtraccions();
	}
	
	private void deleteAll() {
		if (logger.isDebugEnabled())
			logger.debug("deleteAll ");
		
		FacesContext ctx = FacesContext.getCurrentInstance();  
        String nomExtraccio = (String) ctx.getExternalContext().getRequestParameterMap().get("nomExtraccio"); 
        
        String ref_file_conf = null;
        String ref_file_exp = null;
        String ref_file_doc = null;
        String ref_file_doc_sense_exp = null;
        String ref_file_sign = null;
        
        if(nomExtraccio!=null){
        	String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Extraccions//.\" +TYPE:\"cesca:CESCA_EXTRACCIO\" +@cesca\\:nombreExtraccio:\""+nomExtraccio+"\"";
        	SearchParameters sp = new SearchParameters();
    		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
    		sp.setQuery(query);
    		sp.addStore(CescaUtil.STORE);
    		
    		ResultSet resultSet = null;
    		try {
    			if (logger.isDebugEnabled())
    				logger.debug("query (deleteAll) " + query);
    			
    			resultSet = serviceRegistry.getSearchService().query(sp);
                if(resultSet != null&&resultSet.length()>0) {
                	//Siempre devolverà solo un resultado
                	NodeRef noderef = resultSet.getNodeRef(0);
                	FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(noderef);
                	if(fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_CONF)!=null) 
                		ref_file_conf = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_CONF);
                	if(fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_EXP)!=null) 
                		ref_file_exp = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_EXP);
                	if(fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC)!=null) 
                		ref_file_doc = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC);
                	if(fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC_SENSE_EXP)!=null) 
                		ref_file_doc_sense_exp = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_DOC_SENSE_EXP);
                	if(fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_SIGN)!=null) 
                		ref_file_sign = (String) fo.getProperties().get(CescaUtil.EXTRACCIO_PROP_REF_FILE_SIGN);
                	
                	this.getServiceRegistry().getFileFolderService().delete(noderef);
                }
    		}catch (Exception e) {
    			this.error = e.getMessage();
    			this.result = null;
    			logger.error(e.getMessage(),e);
    			e.printStackTrace();
    		} finally {
    			
        		if(logger.isDebugEnabled())
        			logger.debug("Tanca resultset");
    			if (resultSet != null) {
    				resultSet.close();
    			}
    		}
    		//Borramos archivo de configuracion
    		if(ref_file_conf!=null) deleteFileConfig(ref_file_conf);
    		if(ref_file_exp!=null) deleteFileExpedient(ref_file_exp);
    		if(ref_file_doc!=null) deleteFileDocument(ref_file_doc);
    		if(ref_file_sign!=null) deleteFileSignatura(ref_file_sign);
    		if(ref_file_doc_sense_exp!=null) deleteFileConfig(ref_file_doc_sense_exp);
    		//if(ref_file_doc_sense_exp!=null) deleteFileConfigDocumentsSenseExpedient(ref_file_doc_sense_exp);
    		
			//Mostramos lista de plantillas
			this.result = formatMessage("OK_delete_extraccio");
			this.error = null;
			resetFieldsBack();
        }
	}
	
	private void deleteFileSignatura(String ref_file_sign) {
		if (logger.isDebugEnabled())
			logger.debug("deleteFileSignatura ");
		
		String srchQuery = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Signatures//*\"";  
		srchQuery += " +@cm\\:name:\""+ref_file_sign+"//*\"";  
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(srchQuery);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (deleteFileSignatura) " + srchQuery);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
		    if(resultSet != null&&resultSet.length()>0) {
		    	//Siempre devolverà solo un resultado
		    	NodeRef noderef = resultSet.getNodeRef(0);
		    	this.getServiceRegistry().getFileFolderService().delete(noderef);
		    }
		 }catch (Exception e) {
			this.error = e.getMessage();
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
			if(logger.isDebugEnabled())
				logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}
	
	private void modifyNodeSignaturaDocSenseExpedient(String ref_file_sign, Extraccio extraccio, String idConfiguracio, TractamentExtraccions executer) {
		if (logger.isDebugEnabled())
			logger.debug("deleteFileSignatura ");
		
		String srchQuery = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Signatures//*\"";  
		srchQuery += " +@cm\\:name:\""+ref_file_sign+"//*\"";  
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(srchQuery);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (deleteFileSignatura) " + srchQuery);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
		    if(resultSet != null&&resultSet.length()>0) {
		    	//Siempre devolverà solo un resultado
		    	NodeRef noderef = resultSet.getNodeRef(0);
		    	
		    	Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();
		    	
				editProps.put(ContentModel.PROP_NAME, extraccio.getName() + "_signdocind_cfg.xml");
				editProps.put(ContentModel.PROP_TITLE, extraccio.getName() + "_signdocind_cfg.xml");
				editProps.put(CescaUtil.CFG_SIG_PROP_TIPUS_SIGNATURA, extraccio.getTipusDocumentalSign());
				//editProps.put(CescaUtil.CFG_SIG_PROP_TIPUS_SIGNATURA, "cm:content");
				editProps.put(CescaUtil.CFG_SIG_PROP_EN_SUSPENS, false);
				editProps.put(CescaUtil.CFG_SIG_PROP_PETICIO_SIGN_DOC_IND, true);
				editProps.put(CescaUtil.CFG_SIG_PROP_ID_CONFIG, idConfiguracio);
				
				getServiceRegistry().getNodeService().setProperties(noderef, editProps);
				
				ContentWriter writer = this.getServiceRegistry().getContentService().getWriter(noderef,
						ContentModel.PROP_CONTENT, true);
		
				writer.setMimetype("application/xml");
				writer.setEncoding("UTF-8");
				try{
					writer.putContent(new String(executer.generaFitxerExtraccio(extraccio.getPlantillaSign(), extraccio)));
				}catch(Exception e){
					throw new Exception(e.getMessage());
				}
		    	
		    } else {
		    	//creamos
		    	executer.createNodeSignaturaDocSenseExpedient(extraccio, idConfiguracio);
		    }
		 }catch (Exception e) {
			this.error = e.getMessage();
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
			if(logger.isDebugEnabled())
				logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}
	
	private void modifyNodeSignatura(String ref_file_sign, Extraccio extraccio, String idConfiguracio, TractamentExtraccions executer) {
		if (logger.isDebugEnabled())
			logger.debug("deleteFileSignatura ");
		
		String srchQuery = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Signatures//*\"";  
		srchQuery += " +@cm\\:name:\""+ref_file_sign+"//*\"";  
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(srchQuery);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (deleteFileSignatura) " + srchQuery);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
		    if(resultSet != null&&resultSet.length()>0) {
		    	//Siempre devolverà solo un resultado
		    	NodeRef noderef = resultSet.getNodeRef(0);
		    	
		    	Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();
		    	
		    	editProps.put(ContentModel.PROP_NAME, extraccio.getName() + "_signdocexp_cfg.xml");
				editProps.put(ContentModel.PROP_TITLE, extraccio.getName() + "_signdocexp_cfg.xml");
				editProps.put(CescaUtil.CFG_SIG_PROP_TIPUS_SIGNATURA, extraccio.getTipusDocumentalSign());
				//editProps.put(CescaUtil.CFG_SIG_PROP_TIPUS_SIGNATURA, "cm:content");
				editProps.put(CescaUtil.CFG_SIG_PROP_EN_SUSPENS, false);
				editProps.put(CescaUtil.CFG_SIG_PROP_PETICIO_SIGN_DOC_IND, false);
				editProps.put(CescaUtil.CFG_SIG_PROP_ID_CONFIG, idConfiguracio);
				
				getServiceRegistry().getNodeService().setProperties(noderef, editProps);
				
				ContentWriter writer = this.getServiceRegistry().getContentService().getWriter(noderef,
						ContentModel.PROP_CONTENT, true);
		
				writer.setMimetype("application/xml");
				writer.setEncoding("UTF-8");
				try{
					writer.putContent(new String(executer.generaFitxerExtraccio(extraccio.getPlantillaSign(), extraccio)));
				}catch(Exception e){
					throw new Exception(e.getMessage());
				}
		    	
		    } else {
		    	//creamos
		    	executer.createNodeSignatura(extraccio, idConfiguracio);
		    }
		 }catch (Exception e) {
			this.error = e.getMessage();
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
			if(logger.isDebugEnabled())
				logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}
	
	private void deleteFileDocument(String ref_file_doc) {
		if (logger.isDebugEnabled())
			logger.debug("query (deleteFileDocument) ");
		
		String srchQuery = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Documents//*\"";  
		srchQuery += " +@cm\\:name:\""+ref_file_doc+"//*\"";  
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(srchQuery);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (deleteFileDocument) " + srchQuery);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
		    if(resultSet != null&&resultSet.length()>0) {
		    	//Siempre devolverà solo un resultado
		    	NodeRef noderef = resultSet.getNodeRef(0);
		    	this.getServiceRegistry().getFileFolderService().delete(noderef);
		    }
		 }catch (Exception e) {
			this.error = e.getMessage();
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
			if(logger.isDebugEnabled())
				logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}
	
	private void modifyNodeDocumentSinExp(String ref_file_doc, Extraccio extraccio, String idConfiguracio, TractamentExtraccions executer) {
		if (logger.isDebugEnabled())
			logger.debug("query (deleteFileDocument) ");
		
		String srchQuery = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Documents//*\"";  
		srchQuery += " +@cm\\:name:\""+ref_file_doc+"//*\"";  
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(srchQuery);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (deleteFileDocument) " + srchQuery);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
		    if(resultSet != null&&resultSet.length()>0) {
		    	//Siempre devolverà solo un resultado
		    	NodeRef noderef = resultSet.getNodeRef(0);
		    	
		    	Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();

				editProps.put(ContentModel.PROP_NAME, extraccio.getName() + "_docind_cfg.xml");
				editProps.put(ContentModel.PROP_TITLE, extraccio.getName() + "_docind_cfg.xml");
				editProps.put(CescaUtil.CFG_DOC_PROP_CRITERI_CERCA_SIG, extraccio.getSelSign());
				//editProps.put(CescaUtil.CFG_DOC_PROP_TIPUS_DOCUMENT, "cm:content");
				editProps.put(CescaUtil.CFG_DOC_PROP_TIPUS_DOCUMENT, extraccio.getTipusDocumentalDoc());
				editProps.put(CescaUtil.CFG_DOC_PROP_EN_SUSPENS, false);
				editProps.put(CescaUtil.CFG_DOC_PROP_PETICIO_DOC_IND, true);
				editProps.put(CescaUtil.CFG_DOC_PROP_ID_CONFIG, idConfiguracio);
				
				getServiceRegistry().getNodeService().setProperties(noderef, editProps);
				
				ContentWriter writer = this.getServiceRegistry().getContentService().getWriter(noderef,
						ContentModel.PROP_CONTENT, true);

				writer.setMimetype("application/xml");
				writer.setEncoding("UTF-8");
				try{
					writer.putContent(new String(executer.generaFitxerExtraccio(extraccio.getPlantillaDoc(), extraccio)));
				}catch(Exception e){
					throw new Exception(e.getMessage());
				}
		    	
		    } else {
		    	//creamos
		    	executer.createNodeDocumentSinExp(extraccio, idConfiguracio);
		    }
		 }catch (Exception e) {
			this.error = e.getMessage();
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
			if(logger.isDebugEnabled())
				logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}
	
	private void modifyNodeDocument(String ref_file_doc, Extraccio extraccio, String idConfiguracio, TractamentExtraccions executer) {
		if (logger.isDebugEnabled())
			logger.debug("query (deleteFileDocument) ");
		
		String srchQuery = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Documents//*\"";  
		srchQuery += " +@cm\\:name:\""+ref_file_doc+"//*\"";
		System.out.println("Cerca objecte document: " + srchQuery);
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(srchQuery);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (deleteFileDocument) " + srchQuery);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
		    if(resultSet != null&&resultSet.length()>0) {
		    	//Siempre devolverà solo un resultado
		    	NodeRef noderef = resultSet.getNodeRef(0);

		    	Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();

				editProps.put(ContentModel.PROP_NAME, extraccio.getName() + "_docexp_cfg.xml");
				editProps.put(ContentModel.PROP_TITLE, extraccio.getName() + "_docexp_cfg.xml");
				editProps.put(CescaUtil.CFG_DOC_PROP_CRITERI_CERCA_SIG, extraccio.getSelSign());
				editProps.put(CescaUtil.CFG_DOC_PROP_TIPUS_DOCUMENT, extraccio.getTipusDocumentalDoc());
				editProps.put(CescaUtil.CFG_DOC_PROP_EN_SUSPENS, false);
				editProps.put(CescaUtil.CFG_DOC_PROP_PETICIO_DOC_IND, false);
				editProps.put(CescaUtil.CFG_DOC_PROP_ID_CONFIG, idConfiguracio);
				
				getServiceRegistry().getNodeService().setProperties(noderef, editProps);
				
				ContentWriter writer = this.getServiceRegistry().getContentService().getWriter(noderef,
						ContentModel.PROP_CONTENT, true);

				writer.setMimetype("application/xml");
				writer.setEncoding("UTF-8");
				try{
					writer.putContent(new String(executer.generaFitxerExtraccio(extraccio.getPlantillaDoc(), extraccio)));
				}catch(Exception e){
					throw new Exception(e.getMessage());
				}
		    	
		    } else {
		    	//creamos
		    	executer.createNodeDocument(extraccio, idConfiguracio);
		    }
		 }catch (Exception e) {
			this.error = e.getMessage();
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
			if(logger.isDebugEnabled())
				logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}
	
	private void deleteFileExpedient(String ref_file_exp) {
		if (logger.isDebugEnabled())
			logger.debug("deleteFileExpedient ");
		
		String srchQuery = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Expedients//*\"";  
		srchQuery += " +@cm\\:name:\""+ref_file_exp+"//*\"";  
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(srchQuery);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (deleteFileExpedient) " + srchQuery);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
		    if(resultSet != null&&resultSet.length()>0) {
		    	//Siempre devolverà solo un resultado
		    	NodeRef noderef = resultSet.getNodeRef(0);
		    	this.getServiceRegistry().getFileFolderService().delete(noderef);
		    }
		 }catch (Exception e) {
			this.error = e.getMessage();
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
			if(logger.isDebugEnabled())
				logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}
	
	private void modifyFileExpedient(String ref_file_exp, Extraccio extraccio, String idConfiguracio, TractamentExtraccions executer) {
		if (logger.isDebugEnabled())
			logger.debug("deleteFileExpedient ");
		
		String srchQuery = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Expedients//*\"";  
		srchQuery += " +@cm\\:name:\""+ref_file_exp+"//*\"";
		System.out.println("Cerca objecte expedient: " + srchQuery);
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(srchQuery);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (deleteFileExpedient) " + srchQuery);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
		    if(resultSet != null&&resultSet.length()>0) {
		    	//Siempre devolverà solo un resultado
		    	NodeRef noderef = resultSet.getNodeRef(0);
		    	
		    	Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();

				editProps.put(ContentModel.PROP_NAME, extraccio.getName() + "_cfg.xml");
				editProps.put(ContentModel.PROP_TITLE, extraccio.getName() + "_cfg.xml");
				editProps.put(CescaUtil.CFG_EXP_PROP_CRITERI_CERCA_DOCS, extraccio.getSelDoc());
				editProps.put(CescaUtil.CFG_EXP_PROP_TIPUS_EXPEDIENT, extraccio.getTipusDocumentalExp());
				System.out.println("Valor en suspens: " + (Boolean)getServiceRegistry().getNodeService().getProperty(noderef, CescaUtil.CFG_EXP_PROP_EN_SUSPENS));
				editProps.put(CescaUtil.CFG_EXP_PROP_EN_SUSPENS, (Boolean)getServiceRegistry().getNodeService().getProperty(noderef, CescaUtil.CFG_EXP_PROP_EN_SUSPENS));
				editProps.put(CescaUtil.CFG_EXP_PROP_ID_CONFIG, idConfiguracio);
				editProps.put(CescaUtil.CFG_EXP_PROP_DEFAULT_CONFIG, (Boolean)getServiceRegistry().getNodeService().getProperty(noderef, CescaUtil.CFG_EXP_PROP_DEFAULT_CONFIG));
				
				getServiceRegistry().getNodeService().setProperties(noderef, editProps);
				
				ContentWriter writer = this.getServiceRegistry().getContentService().getWriter(noderef,
						ContentModel.PROP_CONTENT, true);

				writer.setMimetype("application/xml");
				writer.setEncoding("UTF-8");
				try{
					writer.putContent(new String(executer.generaFitxerExtraccio(extraccio.getPlantillaExp(), extraccio)));
				}catch(Exception e){
					throw new Exception(e.getMessage());
				}
		    	
		    } else {
		    	//creamos
		    	executer.createNodeExpedient(extraccio, idConfiguracio);
		    }
		 }catch (Exception e) {
			this.error = e.getMessage();
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
			if(logger.isDebugEnabled())
				logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}

	private void deleteFileConfig(String ref_file_conf) {
		if (logger.isDebugEnabled())
			logger.debug("deleteFileConfig ");
		
		String srchQuery = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Configuracio//*\"";  
		srchQuery += " +@cm\\:name:\""+ref_file_conf+"//*\"";  
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(srchQuery);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (deleteFileConfig) " + srchQuery);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
		    if(resultSet != null&&resultSet.length()>0) {
		    	//Siempre devolverà solo un resultado
		    	NodeRef noderef = resultSet.getNodeRef(0);
		    	this.getServiceRegistry().getFileFolderService().delete(noderef);
		    }
		 }catch (Exception e) {
			this.error = e.getMessage();
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
			if(logger.isDebugEnabled())
				logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}
	
	private String modifyFileConfig(String ref_file_conf, Extraccio extraccio, String nomExtraccio, String idConfiguracio, TractamentExtraccions executer) {
		
		if (logger.isDebugEnabled())
			logger.debug("modifyFileConfig ");
		
		String idConf = "";
		
		String srchQuery = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Configuracio//*\"";  
		srchQuery += " +@cm\\:name:\""+ref_file_conf+"//*\"";
		System.out.println("Cerca objecte configuracio: " + srchQuery);
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(srchQuery);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (deleteFileConfig) " + srchQuery);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
		    if(resultSet != null&&resultSet.length()>0) {
		    	//Siempre devolverà solo un resultado
		    	NodeRef noderef = resultSet.getNodeRef(0);
		    	
		    	Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();

		    	idConf = (String)getServiceRegistry().getNodeService().getProperty(noderef, CescaUtil.CONFIGURACIO_PROP_ID_CONFIG);
		    	if(idConf == null || idConf.equals("")) {
		    		idConf = idConfiguracio;
		    	}
		    	
		    	editProps.put(ContentModel.PROP_NAME, nombreExtraccio+"_cfg");
				editProps.put(ContentModel.PROP_TITLE, nombreExtraccio+"_cfg");
				editProps.put(CescaUtil.CONFIGURACIO_PROP_CRITERI_CERCA, extraccio.getSelExp());
				editProps.put(CescaUtil.CONFIGURACIO_PROP_PETICIO_DOC_EXP, true);
				editProps.put(CescaUtil.CONFIGURACIO_PROP_ID_CONFIG, idConf);
				editProps.put(CescaUtil.CONFIGURACIO_PROP_EN_SUSPENS, (Boolean)getServiceRegistry().getNodeService().getProperty(noderef, CescaUtil.CONFIGURACIO_PROP_EN_SUSPENS));
				
				getServiceRegistry().getNodeService().setProperties(noderef, editProps);
		    } else {
		    	//creamos
		    	executer.createNodeConfiguracio(extraccio, this.nombreExtraccio, idConfiguracio);
		    	idConf = idConfiguracio;
		    }
		 }catch (Exception e) {
			this.error = e.getMessage();
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
			if(logger.isDebugEnabled())
				logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return idConf;
	}
	
	public String modifyNodeConfiguracioDocSenseExpedient(String ref_file_conf, Extraccio extraccio, String nombreExtraccio, 
			String idConfiguracio, TractamentExtraccions executer) throws Exception {
		
		String idConf = "";
		
		String srchQuery = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Configuracio//*\"";  
		srchQuery += " +@cm\\:name:\""+ref_file_conf+"//*\"";  
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(srchQuery);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (deleteFileConfig) " + srchQuery);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
			System.out.println("Nombre de resultats: " + resultSet.length());
		    if(resultSet != null&&resultSet.length()>0) {
		    	NodeRef noderef = resultSet.getNodeRef(0);
		    	
		    	Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();

		    	idConf = (String)getServiceRegistry().getNodeService().getProperty(noderef, CescaUtil.CONFIGURACIO_PROP_ID_CONFIG);
		    	if(idConf == null || idConf.equals("")) {
		    		idConf = idConfiguracio;
		    	}
		    	
		    	editProps.put(ContentModel.PROP_NAME, nombreExtraccio+"_cfg");
				editProps.put(ContentModel.PROP_TITLE, nombreExtraccio+"_cfg");
				editProps.put(CescaUtil.CONFIGURACIO_PROP_CRITERI_CERCA, extraccio.getSelDoc());
				editProps.put(CescaUtil.CONFIGURACIO_PROP_PETICIO_DOC_EXP, false);
				editProps.put(CescaUtil.CONFIGURACIO_PROP_ID_CONFIG, idConf);
				
				getServiceRegistry().getNodeService().setProperties(noderef, editProps);
		    } else {
		    	//creamos
		    	executer.createNodeConfiguracioDocSenseExpedient(extraccio, this.nombreExtraccio, idConfiguracio);
		    	idConf = idConfiguracio;
		    }
		} catch (Exception e) {
			this.error = e.getMessage();
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
			if(logger.isDebugEnabled())
				logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
		
		return idConf;
	}
	
	/*private void deleteFileConfigDocumentsSenseExpedient(String ref_file_conf) {
		if (logger.isDebugEnabled())
			logger.debug("deleteFileConfigDocumentsSenseExpedient ");
		
		String srchQuery = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:DocumentsSenseExpedient//*\"";  
		srchQuery += " +@cm\\:name:\""+ref_file_conf+"//*\"";  
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(srchQuery);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query(deleteFileConfigDocumentsSenseExpedient) " + srchQuery);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
		    if(resultSet != null&&resultSet.length()>0) {
		    	//Siempre devolverà solo un resultado
		    	NodeRef noderef = resultSet.getNodeRef(0);
		    	this.getServiceRegistry().getFileFolderService().delete(noderef);
		    }
		 }catch (Exception e) {
			this.error = e.getMessage();
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
			if(logger.isDebugEnabled())
				logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}*/
	
	private void showTablePlantillas(String tipoPlantilla) {
		if (logger.isDebugEnabled())
			logger.debug("showTablePlantillas ");
		
		String query = null;
		query = "+PATH:\"/app:company_home/cm:CESCA/cm:Plantilles//.\" +TYPE:\"cesca:CESCA_PLANTILLA\" +@cesca\\:tipoDocumental:\""+tipoPlantilla+"\"";
		
		if (logger.isDebugEnabled())
			logger.debug("Query > "+ query);

		//Executa la consulta
    	SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		this.listPlantilla = new ArrayList<SelectItem>();
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (showTablePlantillasDoc) " + query);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
            if(resultSet != null) {
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		if(fo.getProperties().get(CescaUtil.PLANTILLA_PROP_PLAN_REF)==null){
	            		this.listPlantilla.add(new SelectItem(fo.getProperties().get(CescaUtil.PLANTILLA_PROP_NAME)+"#"+fo.getProperties().get(CescaUtil.PLANTILLA_PROP_TIPO), (String)fo.getProperties().get(CescaUtil.PLANTILLA_PROP_NAME)));
            		}
				}
            }
		} catch (Exception e) {
			error = e.getMessage();
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
    		if(logger.isDebugEnabled())
    			logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}
	
	private void showTablePlantillasDoc(String tipoPlantilla) {
		if (logger.isDebugEnabled())
			logger.debug("showTablePlantillasDoc");
		
		String query = null;
		query = "+PATH:\"/app:company_home/cm:CESCA/cm:Plantilles//.\" +TYPE:\"cesca:CESCA_PLANTILLA\" +@cesca\\:tipoDocumental:\""+tipoPlantilla+"\"";
		
		if (logger.isDebugEnabled())
			logger.debug("Query > "+ query);

		//Executa la consulta
    	SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		this.listPlantillaDoc = new ArrayList<SelectItem>();
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (showTablePlantillasDoc) " + query);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
            if(resultSet != null) {
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		if(fo.getProperties().get(CescaUtil.PLANTILLA_PROP_PLAN_REF)==null){
	            		this.listPlantillaDoc.add(new SelectItem(fo.getProperties().get(CescaUtil.PLANTILLA_PROP_NAME)+"#"+fo.getProperties().get(CescaUtil.PLANTILLA_PROP_TIPO), (String)fo.getProperties().get(CescaUtil.PLANTILLA_PROP_NAME)));
            		}
				}
            }
		} catch (Exception e) {
			error = e.getMessage();
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
    		if(logger.isDebugEnabled())
    			logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}
	
	private void showTablePlantillasSign(String tipoPlantilla) {
		if (logger.isDebugEnabled())
			logger.debug("showTablePlantillasSign");
		
		String query = null;
		query = "+PATH:\"/app:company_home/cm:CESCA/cm:Plantilles//.\" +TYPE:\"cesca:CESCA_PLANTILLA\" +@cesca\\:tipoDocumental:\""+tipoPlantilla+"\"";
		
		if (logger.isDebugEnabled())
			logger.debug("Query > "+ query);

		//Executa la consulta
    	SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		this.listPlantillaSign = new ArrayList<SelectItem>();
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (showTablePlantillasSign) " + query);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
            if(resultSet != null) {
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		if(fo.getProperties().get(CescaUtil.PLANTILLA_PROP_PLAN_REF)==null){
	            		this.listPlantillaSign.add(new SelectItem(fo.getProperties().get(CescaUtil.PLANTILLA_PROP_NAME)+"#"+fo.getProperties().get(CescaUtil.PLANTILLA_PROP_TIPO), (String)fo.getProperties().get(CescaUtil.PLANTILLA_PROP_NAME)));
            		}
				}
            }
		} catch (Exception e) {
			error = e.getMessage();
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
    		if(logger.isDebugEnabled())
    			logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}
	
	public List<SelectItem> getTipusDocumentals() {
		if (logger.isDebugEnabled())
			logger.debug("getTipusDocumentals");
		
		if (this.tipusDocumentals == null)
	      { 
			ConfigService svc = Application.getConfigService(FacesContext.getCurrentInstance());
	         Config wizardCfg = svc.getConfig("Content Wizards");
	         if (wizardCfg != null)
	         {
	        	ConfigElement aspectsCfg = wizardCfg.getConfigElement("content-types");
	            if (aspectsCfg != null)
	            {
	               this.tipusDocumentals = new ArrayList<SelectItem>();
	               for (ConfigElement child : aspectsCfg.getChildren())
	               {
	            	   if(logger.isDebugEnabled())
		           			logger.debug("valor contenty types() " + child.getAttribute("name"));
	                  /*if(child.getAttribute("name").equals("cesca:CESCA_CONFIGURACIO")){
	                	  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Objecte de configuració principal"));
	                  }
	                  else if(child.getAttribute("name").equals("cesca:CESCA_CFG_EXPEDIENT"))
	                	  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Objecte de configuració d'expedients"));
	                  else if(child.getAttribute("name").equals("cesca:CESCA_CFG_DOCUMENT"))
	                	  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Objecte de configuració de documents dels expedients"));
	                  else if(child.getAttribute("name").equals("cesca:CESCA_CFG_SIGNATURA"))
	                	  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Objecte de configuració de signatures dels documents"));
	                  else if(child.getAttribute("name").equals("cesca:CESCA_PETICIO_IARXIU"))
	                	  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Objecte de petició de iarxiu"));
	                  else if(child.getAttribute("name").equals("cesca:CESCA_REBUT_IARXIU"))
	                	  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Objecte de rebut de iarxiu"));
	                  else if(child.getAttribute("name").equals("cesca:CESCA_PROCES_LOG"))
	                	  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Objecte de log"));
	                  else if(child.getAttribute("name").equals("cesca:CESCA_MAIL_TEMPLATE"))
	                	  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Objecte per a les plantilles dels correus"));
	                  else if(child.getAttribute("name").equals("cesca:CESCA_PLANTILLA"))
	                	  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Objecte de plantilla"));
	                  else if(child.getAttribute("name").equals("cesca:CESCA_EXTRACCIO"))
	                	  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Objecte de extracció"));
	                  else if(child.getAttribute("name").equals("cesca:CESCA_ATRIBUTO"))
	                	  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Objecte de atributo"));
	                  else if(child.getAttribute("name").equals("cesca:CESCA_VOCABULARIO"))
	                	  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Objecte de vocabulari"));
	                  else if(child.getAttribute("name").equals("cesca:CESCA_CFG_DOCUMENT_SENSE_EXP"))
	                	  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Objecte de configuració de documents sense expedients"));
	                  else{*/
	                	  if(child.getAttribute("name").equals("cm:content"))
	                		  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Contenido"));
	                	  else if(child.getAttribute("name").equals("ws:article"))
	                		  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Article"));
	                	  else if(child.getAttribute("name").equals("ws:image"))
	                		  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "General Image"));
	                	  else if(child.getAttribute("name").equals("ws:visitorFeedback"))
	                		  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Visitor Feedback List"));
	                	  else {
	                		  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), child.getAttribute("name")));
	                	  }
	                  //}
	               }
	            }  
	         }
	         this.tipusDocumentals.add(new SelectItem("cm:folder", "Carpeta"));
	      }
		
	      return this.tipusDocumentals;
	}
	
	public void resetFields(){
		if (logger.isDebugEnabled())
			logger.debug("resetFields");
		
		this.nombreExtraccio = null;
		this.tipusDocumentals = null;
		this.nodes = null;
		this.edit = null;
		this.nodeReferencia = null;
		this.nodes = null;
		this.tipusDocumentalExp = null;
		this.tipusDocumentalDoc = null;
		this.tipusDocumentalSign = null;
		this.selExp = null;
		this.selDoc = null;
		this.selSign = null;
		this.plantillaExp = null;
		this.plantillaDoc = null;
		this.plantillaSign = null;
		this.checkboxDocument = false;
		this.checkboxExpedient = false;
		this.listPlantilla = null;
		this.listPlantillaDoc = null;
		this.listPlantillaSign = null;
		this.tipusPlantilla = null;
		this.plantillaExpRefOld = null;
		this.plantillaDocRefOld = null;
		this.plantillaDocSenseExpRefOld = null;
		this.plantillaSignRefOld = null;
		this.plantillaConfigRefOld = null;
	}
	
	public void resetFieldsBack(){
		if (logger.isDebugEnabled())
			logger.debug("resetFieldsBack");
		
		this.tipusDocumentals = null;
		this.edit = null;
		this.nodeReferencia = null;
		this.nombreExtraccio = null;
		this.tipusDocumentalExp = null;
		this.tipusDocumentalDoc = null;
		this.tipusDocumentalSign = null;
		this.listPlantilla = null;
		this.listPlantillaDoc = null;
		this.listPlantillaSign = null;
		this.selExp = null;
		this.selDoc = null;
		this.selSign = null;
		this.plantillaExp = null;
		this.plantillaDoc = null;
		this.plantillaSign = null;
		this.checkboxDocument = false;
		this.checkboxExpedient = false;
		this.tipusPlantilla = null;
		this.plantillaExpRefOld = null;
		this.plantillaDocRefOld = null;
		this.plantillaDocSenseExpRefOld = null;
		this.plantillaSignRefOld = null;
		this.plantillaConfigRefOld = null;
	}
}
