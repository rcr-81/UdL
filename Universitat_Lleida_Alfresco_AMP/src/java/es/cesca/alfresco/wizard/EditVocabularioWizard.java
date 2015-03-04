package es.cesca.alfresco.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.wizard.BaseWizardBean;
import org.alfresco.web.data.IDataContainer;
import org.alfresco.web.data.QuickSort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.cesca.alfresco.scheduled.ExecuterAbstractBase.ExecuterException;
import es.cesca.alfresco.util.CescaUtil;
import es.cesca.alfresco.wizardExecuter.TractamentVocabularis;

public class EditVocabularioWizard extends BaseWizardBean{

	private static final long serialVersionUID = -557955106388369334L;
	private static Log logger = LogFactory.getLog(VocabulariWizard.class);
	private ServiceRegistry serviceRegistry;
	
	//Params
	private String error = null;
	private String errorNodeVocabulario = null;
	private String result = null;
	private transient DataModel nodesAtr;
	private FileInfo node;
	private String edit = null;
	private NodeRef nodeReferencia = null;
	private NodeRef nodeReferenciaVoc = null;
	private String tipusDocumentalExtra = null;

	//Vocabulario
	private String nombreVoc = null;

	//Atributo
	private String nombreAtr;
	private String valor;
	private boolean obligatori;
	private String tipus;

	//Afegeix valor
	private String constant;
	private String cerca;
	protected List<SelectItem> atributs;
	protected String atribut;
	
	private boolean checkboxTipusDocumental;
	private boolean checkboxTipusDocumentalParser;
	private boolean checkboxConstant;
	private boolean checkboxCerca;
	
	public List<SelectItem> getAtributs() {
		return atributs;
	}
	
	public String getTipusDocumentalExtra() {
		return tipusDocumentalExtra;
	}

	public void setTipusDocumentalExtra(String tipusDocumentalExtra) {
		this.tipusDocumentalExtra = tipusDocumentalExtra;
	}

	public String getErrorNodeVocabulario() {
		return errorNodeVocabulario;
	}

	public void setErrorNodeVocabulario(String errorNodeVocabulario) {
		this.errorNodeVocabulario = errorNodeVocabulario;
	}

	
	public void setAtributs(List<SelectItem> atributs) {
		this.atributs = atributs;
	}

	public String getAtribut() {
		return atribut;
	}

	public void setAtribut(String atribut) {
		this.atribut = atribut;
	}

	public boolean isCheckboxTipusDocumental() {
		return checkboxTipusDocumental;
	}
	
	public boolean isCheckboxTipusDocumentalParser() {
		return checkboxTipusDocumentalParser;
	}

	public void setCheckboxTipusDocumental(boolean checkboxTipusDocumental) {
		this.checkboxTipusDocumental = checkboxTipusDocumental;
	}
	
	public void setCheckboxTipusDocumentalParser(boolean checkboxTipusDocumentalParser) {
		this.checkboxTipusDocumentalParser = checkboxTipusDocumentalParser;
	}

	public boolean isCheckboxConstant() {
		return checkboxConstant;
	}

	public void setCheckboxConstant(boolean checkboxConstant) {
		this.checkboxConstant = checkboxConstant;
	}

	public boolean isCheckboxCerca() {
		return checkboxCerca;
	}

	public void setCheckboxCerca(boolean checkboxCerca) {
		this.checkboxCerca = checkboxCerca;
	}
	
	public String getNombreVoc() {
		return nombreVoc;
	}

	public void setNombreVoc(String nombreVoc) {
		this.nombreVoc = nombreVoc;
	}

	protected List<SelectItem> tipusDocumentals;
	protected String tipusDocumental;
	protected String tipusDocumentalParser;
	
	@Override
	public void init(Map<String, String> parameters) {
		if (logger.isDebugEnabled())
			logger.debug("Init ");
		
		super.init(parameters);
		this.error = null;
		this.result = null;
		this.errorNodeVocabulario = null;
		this.resetFields();
		
		getRefNodeVocabulario();
		if(this.nodeReferenciaVoc!=null) {
			if (logger.isDebugEnabled())
				logger.debug("Editant vocabulari ");
			
			this.nombreVoc = this.node.getName();
			showTableAtributes();
		}else{
			if (logger.isDebugEnabled())
				logger.debug("No es un vocabulari ");
			
			this.errorNodeVocabulario = formatMessage("noNodeVocabulari");
		}
	}

	private void getRefNodeVocabulario() {
		if (logger.isDebugEnabled())
			logger.debug("getRefNodeVocabulario ");
		
		this.node = this.getServiceRegistry().getFileFolderService().getFileInfo(new NodeRef(CescaUtil.STORE,
				(String) this.parameters.get("id")));
		
		String queryVoc = "+PATH:\"/app:company_home/cm:CESCA/cm:Vocabularis//.\" +TYPE:\"cesca:CESCA_VOCABULARIO\" +@cesca\\:nombreVoc:\""+this.node.getName()+"\"";
		
		if (logger.isDebugEnabled())
			logger.debug("query(getRefNodeVocabulario) > "+ queryVoc);
		
		//Executa la consulta
    	SearchParameters spVoc = new SearchParameters();
    	spVoc.setLanguage(SearchService.LANGUAGE_LUCENE);
    	spVoc.setQuery(queryVoc);
    	spVoc.addStore(CescaUtil.STORE);
		
		ResultSet resultSetVoc = null;
		try {
			resultSetVoc = serviceRegistry.getSearchService().query(spVoc);
            if(resultSetVoc != null) {
            	for (NodeRef row : resultSetVoc.getNodeRefs()) {
            		this.nodeReferenciaVoc = row;
				}
            }	
		} catch (Exception e) {
			this.error = e.getMessage();
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Tanca resultset");
			if (resultSetVoc != null) {
				resultSetVoc.close();
			}
		}
	}
	
	public void deleteAtributo(ActionEvent event) {
		if (logger.isDebugEnabled())
			logger.debug("deleteAtributo ");
		
		FacesContext ctx = FacesContext.getCurrentInstance();  
        String nomAtributo = (String) ctx.getExternalContext().getRequestParameterMap().get("nomAtributo"); 
        
        if(nomAtributo!=null){
        	String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Atributs//.\" +TYPE:\"cesca:CESCA_ATRIBUTO\" +@cesca\\:nombreAtr:\""+nomAtributo+"#"+this.node.getName()+"\"";
        	
    		//Executa la consulta
        	SearchParameters sp = new SearchParameters();
    		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
    		sp.setQuery(query);
    		sp.addStore(CescaUtil.STORE);
    		
    		ResultSet resultSet = null;
    		try {
    			if (logger.isDebugEnabled())
    				logger.debug("query(deleteAtributo)> " + query);
    			
    			resultSet = serviceRegistry.getSearchService().query(sp);
                if(resultSet != null&&resultSet.length()>0) {
                	//Siempre devolverà solo un resultado
                	NodeRef noderef = resultSet.getNodeRef(0);
                	this.getServiceRegistry().getFileFolderService().delete(noderef);
        			//Mostramos lista de atributos
        			showTableAtributes();
        			this.result = formatMessage("OK_delete_atribut");
        			this.error = null;
        			resetFieldsBack();
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
	
	public void editAtributo(ActionEvent event) {
		if (logger.isDebugEnabled())
			logger.debug("editAtributo ");
		
		FacesContext ctx = FacesContext.getCurrentInstance();  
        String editAtributo = (String) ctx.getExternalContext().getRequestParameterMap().get("editAtributo"); 
        
        if(editAtributo!=null){
        	String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Atributs//.\" +TYPE:\"cesca:CESCA_ATRIBUTO\" +@cesca\\:nombreAtr:\""+editAtributo+"#"+this.node.getName()+"\"";
        	SearchParameters sp = new SearchParameters();
    		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
    		sp.setQuery(query);
    		sp.addStore(CescaUtil.STORE);
    		
    		ResultSet resultSet = null;
    		try {
    			if (logger.isDebugEnabled())
    				logger.debug("query(editAtributo)> " + query);
    			
    			resultSet = serviceRegistry.getSearchService().query(sp);
                if(resultSet != null&&resultSet.length()>0) {
                	//Siempre devolverà solo un resultado
                	NodeRef noderef = resultSet.getNodeRef(0);
                	this.nodeReferencia = noderef;
                	FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(noderef);
               		String []nombreAtributo = fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_NAME).toString().split("#");
        			this.nombreAtr = nombreAtributo[0];
        			this.tipus = (String) fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_TIPUS);
        			String obli = (String) fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_OBLIGATORI);
        			if(obli.equals("true")) this.obligatori = true;
        			else this.obligatori = false;
        			this.error = null;
        			this.result = null;
        			this.edit = "OK";
        			this.valor = (String) fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_VALOR);
        			
        			this.checkboxCerca = false;
					this.cerca = null;
					this.checkboxTipusDocumental = false;
					this.checkboxTipusDocumentalParser = false;
					this.atribut = null;
					this.checkboxConstant = false;
					this.constant = null;
					this.tipusDocumentalExtra = null;
					this.tipusDocumental = null;
					this.tipusDocumentalParser = null;
        			
        			if(!CescaUtil.isEmpty(this.valor)){
        				String[] split = this.valor.split(";");
        				if(split.length==1){
	        				if(this.valor.contains("query.")){
	        					this.checkboxCerca = true;
	        					this.cerca = this.valor;
	        					this.tipusDocumental = "cm:content";
	        				}else if(this.valor.contains("dmattr.")){	        			
        	        			if(fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_TIPUS_DOCUMENTAL)!=null){
    	        					this.checkboxTipusDocumental = true;     
	        	        			String atr = this.valor.replaceAll("dmattr.", "");
		        					this.atribut = atr;
		        					this.tipusDocumental = (String) fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_TIPUS_DOCUMENTAL);
        	        				this.tipusDocumentalExtra = this.tipusDocumental;
        	        			}
        	        			else{
		        					this.tipusDocumental = "cm:content";
        	        			}
	        				}else{
	        					this.checkboxConstant = true;
	        					this.constant = this.valor;
	        					this.tipusDocumental = "cm:content";
	        				}
        				}
        			}
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
	
	public void nouEditaAtributButton(ActionEvent event){
		//Creamos atributo
		if (logger.isDebugEnabled())
			logger.debug("nouAtributo ");
		try {
			//Insertamos o modificamos atributo
			TractamentVocabularis executer = new TractamentVocabularis();
			executer.setServiceRegistry(serviceRegistry);
			
			boolean changeVoc = false;

			if(!this.nombreVoc.equals(this.node.getName())){
				changeVoc = true;
				if(CescaUtil.isEmpty(this.nombreVoc)){
					this.error = formatMessage("emptyFieldVoc");
					this.result = null;
					if (logger.isDebugEnabled())
						logger.debug("error nombre vocabulario vacio ");
					return;
				}
				executer.editNodeVocabulari(
						this.nodeReferenciaVoc, this.nombreVoc);
				updateRefAtributes();
				updateRefPlantilles();
			}
			if(this.edit!=null){
				if (logger.isDebugEnabled())
					logger.debug("editando ");
				
				if(CescaUtil.isEmpty(this.nombreAtr)){
					if(!changeVoc) {
						this.error = formatMessage("emptyFieldAtr");
						this.result = null;
						if (logger.isDebugEnabled())
							logger.debug("error nombre atributo vacio ");
					} else {
						this.result = formatMessage("OK_vocabulari");
						this.error = null;
					}
					return;
				}
				
				executer.editNodeAtribute(
						this.nodeReferencia, this.nombreAtr, this.valor, this.tipus, this.obligatori, this.tipusDocumentalExtra, this.nombreVoc);
				this.result = formatMessage("OK_edit_atribut");
    			this.error = null;
    			//Mostramos lista de atributos
    			showTableAtributes();
    			this.resetFieldsBack();
			}else{
				if (logger.isDebugEnabled())
					logger.debug("nou atribut (nou)");
				
				if(CescaUtil.isEmpty(this.nombreAtr)){
					if(!changeVoc) {
						this.error = formatMessage("emptyFieldAtr");
						this.result = null;
						if (logger.isDebugEnabled())
							logger.debug("error nombre atributo vacio ");
					} else {
						this.result = formatMessage("OK_vocabulari");
						this.error = null;
					}
					return;
				}
				
				executer.createNodeAtribute(
						this.nombreAtr, this.valor, this.tipus, this.obligatori,this.tipusDocumentalExtra, this.nombreVoc);
				
				this.result = formatMessage("OK_atribut");
    			this.error = null;
    			//Mostramos lista de atributos
    			showTableAtributes();
    			this.resetFieldsBack();
			}
		} catch (FileExistsException e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			this.error = formatMessage("already_exists", e.getMessage());
			this.result = null;
			showTableAtributes();
			
		} catch (ExecuterException e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			this.error = formatMessage("error_generic", e.getMessage());
			this.result = null;
			showTableAtributes();
			
		} catch (AlfrescoRuntimeException e) {
			this.error = formatMessage("error_generic", e.getMessage());
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			showTableAtributes();
			
		} catch (Exception e) {
			this.error = formatMessage("already_exists", e.getMessage());
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			showTableAtributes();
		}	
	}

	@Override
	protected String finishImpl(FacesContext ctx, String outcome)
			throws Exception {
		return outcome;
	}
	
	public void resetFields(){
		if (logger.isDebugEnabled())
			logger.debug("resetFields ");
		
		this.nombreAtr = null;
		this.nombreVoc = null;
		this.tipus = null;
		this.valor = null;
		this.obligatori = false;
		this.constant = null;
		this.cerca = null;
		this.node = null;
		this.edit = null;
		this.tipusDocumental = null;
		this.tipusDocumentalParser = null;
		this.tipusDocumentalExtra = null;
		this.tipusDocumentals = null;
		this.atribut = null;
		this.atributs = null;
		this.checkboxCerca = false;
		this.checkboxConstant = false;
		this.checkboxTipusDocumental = false;
		this.checkboxTipusDocumentalParser = false;
		this.node = null;
		this.nodeReferencia = null;
		this.nodeReferenciaVoc = null;
		this.nodesAtr = null;	
	}
	
	public void resetFieldsBack(){
		if (logger.isDebugEnabled())
			logger.debug("resetFieldsBack");
		
		this.nombreAtr = null;
		this.tipus = null;
		this.valor = null;
		this.obligatori = false;
		this.constant = null;
		this.cerca = null;
		this.edit = null;
		this.tipusDocumental = null;
		this.tipusDocumentalParser = null;
		this.tipusDocumentalExtra = null;
		this.tipusDocumentals = null;
		this.atribut = null;
		this.atributs = null;
		this.checkboxCerca = false;
		this.checkboxConstant = false;
		this.checkboxTipusDocumental = false;
		this.checkboxTipusDocumentalParser = false;
	}
	
	public void netejaConcatenacio(ActionEvent event){
		if (logger.isDebugEnabled())
			logger.debug("concatenacio");
		
		this.valor = null;
		this.error = null;
		this.result = null;
	}
	
	public void validar(ActionEvent event){
		if(this.isCheckboxTipusDocumentalParser()) {
			if(this.isCheckboxTipusDocumental()) {
				
				if(this.valor!=null&&!CescaUtil.isEmpty(this.valor)) {
					if(!CescaUtil.isEmpty(this.atribut))
						this.valor = this.valor + ";" +  "query.select cesca:valorArxiu from " + this.tipusDocumentalParser 
													+ " WHERE cesca:valorUniversitat = 'dmattr." + this.atribut + "'";
				}else{
					if(!CescaUtil.isEmpty(this.atribut)) 
						this.valor = "query.select cesca:valorArxiu from " + this.tipusDocumentalParser 
											+ " WHERE cesca:valorUniversitat = 'dmattr." + this.atribut + "'";
				}
			} else if(this.isCheckboxCerca()) {
				System.out.println("TIPUS DOCUMENTAL PARSER i sentencia");
			}
		} else if(this.isCheckboxTipusDocumental()) {
			if (logger.isDebugEnabled())
				logger.debug("validar tipus documental");
			
			if(this.valor!=null&&!CescaUtil.isEmpty(this.valor)) {
				if(!CescaUtil.isEmpty(this.atribut))
					this.valor = this.valor+";"+"dmattr."+this.atribut;
			}else{
				if(!CescaUtil.isEmpty(this.atribut)) 
					this.valor = "dmattr."+this.atribut;
			}
			this.tipusDocumentalExtra = this.tipusDocumental;
			this.error = null;
			this.result = null;
		}else if(this.isCheckboxConstant()){
			if (logger.isDebugEnabled())
				logger.debug("validar tipus constant");
			
			if(this.valor!=null&&!CescaUtil.isEmpty(this.valor)) {
				this.valor = this.valor+";"+this.constant;
			}else{
				this.valor = this.constant;
			}
			this.tipusDocumentalExtra = null;
			this.error = null;
			this.result = null;
		}else if(this.isCheckboxCerca()) {
			if (logger.isDebugEnabled())
				logger.debug("validar tipus cerca");
			
			String query = this.cerca;
			query = query.replaceAll("query.", "").replaceAll("dmattr.", "");
    		
			SearchParameters sp = new SearchParameters();
			sp.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
			sp.setQuery(query);
			sp.addStore(CescaUtil.STORE);
			
    		
    		ResultSet resultSet = null;
    		try {
    			if (logger.isDebugEnabled())
    				logger.debug("query a validar " + query);
    			
    			resultSet = serviceRegistry.getSearchService(). query(sp);
    			if(resultSet !=null){
    				if(this.valor!=null&&!CescaUtil.isEmpty(this.valor)) {
    					this.valor = this.valor+";"+"query."+this.cerca;
    				}else{
    					this.valor = "query."+this.cerca;
    				}
        			this.error = null;
        			this.result = formatMessage("OKQuery");
        			this.tipusDocumentalExtra = null;
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
		}else{
			this.error = formatMessage("emptyValorAtr");
			this.result = null;
		}
	}
	
	protected String formatMessage(String txt, String _2replace) {
		return MessageFormat.format(Application.getMessage(
                FacesContext.getCurrentInstance(), txt), _2replace);
	}
	
	protected String formatMessage(String txt) {
		return Application.getMessage(FacesContext.getCurrentInstance(), txt);
		
	}
	
	public void showTableAtributes() {
		if (logger.isDebugEnabled())
			logger.debug("showTableAtributes");
		
		String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Atributs//.\" +TYPE:\"cesca:CESCA_ATRIBUTO\" +@cesca\\:vocabulario_ref:\""+this.nombreVoc+"\"";
		
		//Executa la consulta
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query(showTableAtributes) " + query);
			resultSet = serviceRegistry.getSearchService().query(sp);

            if(resultSet != null) {
            	List<Map<String, Serializable>> list = new ArrayList<Map<String, Serializable>>();
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		Map<String, Serializable> map = new HashMap<String, Serializable>(4);
            		String []nombreAtributo = fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_NAME).toString().split("#");
            		if(nombreAtributo[1].equals(this.nombreVoc)){
	            		map.put("nombreAtr", nombreAtributo[0]);
	            		map.put("valor", fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_VALOR));
	            		String obligatori = (String) fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_OBLIGATORI);
	            		if(obligatori.equals("true")) obligatori = "Si";
	            		else obligatori = "No";
	            		map.put("obligatori", obligatori);
	            		map.put("tipus", fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_TIPUS));
	            		
	            		list.add(map);
            		}
				}
    			nodesAtr = new ListDataModel(list);
    	}
		
		} catch (Exception e) {
			this.error = e.getMessage();
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
	
	public void updateRefPlantilles() {
		if (logger.isDebugEnabled())
			logger.debug("updateRefPlantilles ");
		
		String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Plantilles//.\" +TYPE:\"cesca:CESCA_PLANTILLA\" +@cesca\\:vocabulariRef:\""+this.node.getName()+"\"";

		//Executa la consulta
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query(updateRefPlantilles) " + query);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
            if(resultSet != null) {
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		String []nombrePlantillaSplit = fo.getProperties().get(CescaUtil.PLANTILLA_PROP_NAME).toString().split("#");
            		if(nombrePlantillaSplit[1].equals(this.node.getName())){
	            		this.getNodeService().setProperty(row, ContentModel.PROP_NAME, nombrePlantillaSplit[0]+"#"+this.nombreVoc);
	            		this.getNodeService().setProperty(row, ContentModel.PROP_TITLE, nombrePlantillaSplit[0]+"#"+this.nombreVoc);
	            		this.getNodeService().setProperty(row, CescaUtil.PLANTILLA_PROP_ID, nombrePlantillaSplit[0]+"#"+this.nombreVoc);
	            		this.getNodeService().setProperty(row, CescaUtil.PLANTILLA_PROP_NAME, nombrePlantillaSplit[0]+"#"+this.nombreVoc);
	            		this.getNodeService().setProperty(row, CescaUtil.PLANTILLA_PROP_VOC_REF, this.nombreVoc);
            		}
				}
    	}
		
		} catch (Exception e) {
			this.error = e.getMessage();
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
	
	public void updateRefAtributes() {

		if (logger.isDebugEnabled())
			logger.debug("updateRefAtributes ");
		
		String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Atributs//.\" +TYPE:\"cesca:CESCA_ATRIBUTO\" +@cesca\\:vocabulario_ref:\""+this.node.getName()+"\"";

		//Executa la consulta
		SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query(updateRefAtributes) " + query);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
            if(resultSet != null) {
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		String []nombreAtrSplit = fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_NAME).toString().split("#");
            		if(nombreAtrSplit[1].equals(this.node.getName())){
	            		this.getNodeService().setProperty(row, ContentModel.PROP_NAME, nombreAtrSplit[0]+"#"+this.nombreVoc);
	            		this.getNodeService().setProperty(row, ContentModel.PROP_TITLE, nombreAtrSplit[0]+"#"+this.nombreVoc);
	            		this.getNodeService().setProperty(row, CescaUtil.ATRIBUTO_PROP_ID, nombreAtrSplit[0]+"#"+this.nombreVoc);
	            		this.getNodeService().setProperty(row, CescaUtil.ATRIBUTO_PROP_NAME, nombreAtrSplit[0]+"#"+this.nombreVoc);
	            		this.getNodeService().setProperty(row, CescaUtil.ATRIBUTO_PROP_VOCABLUARI_REF, this.nombreVoc);
            		}
				}
    	}
		
		} catch (Exception e) {
			this.error = e.getMessage();
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
	
	public NodeRef getNodeReferenciaVoc() {
		return nodeReferenciaVoc;
	}

	public void setNodeReferenciaVoc(NodeRef nodeReferenciaVoc) {
		this.nodeReferenciaVoc = nodeReferenciaVoc;
	}
	
	public String getnombreVoc() {
		return nombreVoc;
	}

	public void setnombreVoc(String nombreVoc) {
		this.nombreVoc = nombreVoc;
	}
	
	public NodeRef getNodeReferencia() {
		return nodeReferencia;
	}

	public void setNodeReferencia(NodeRef nodeReferencia) {
		this.nodeReferencia = nodeReferencia;
	}
	
	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}

	public void setTipusDocumentals(List<SelectItem> tipusDocumentals) {
		this.tipusDocumentals = tipusDocumentals;
	}
	
	public String getNombreAtr() {
		return nombreAtr;
	}
	public void setNombreAtr(String nombreAtr) {
		this.nombreAtr = nombreAtr;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public boolean isObligatori() {
		return obligatori;
	}
	public void setObligatori(boolean obligatori) {
		this.obligatori = obligatori;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getConstant() {
		return constant;
	}
	public void setConstant(String constant) {
		this.constant = constant;
	}
	public String getCerca() {
		return cerca;
	}
	public void setCerca(String cerca) {
		this.cerca = cerca;
	}
	public String getTipusDocumental() {
		return tipusDocumental;
	}
	public String getTipusDocumentalParser() {
		return tipusDocumentalParser;
	}
	public void setTipusDocumental(String tipusDocumental) {
		this.tipusDocumental = tipusDocumental;
	}
	public void setTipusDocumentalParser(String tipusDocumental) {
		this.tipusDocumentalParser = tipusDocumental;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public FileInfo getNode() {
		return node;
	}
	public void setNode(FileInfo node) {
		this.node = node;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public DataModel getNodesAtr() {
		return nodesAtr;
	}
	public void setNodesAtr(DataModel nodesAtr) {
		this.nodesAtr = nodesAtr;
	}
	public static Log getLogger() {
		return logger;
	}
	public static void setLogger(Log logger) {
		EditVocabularioWizard.logger = logger;
	}
	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	
	public List<SelectItem> getTipusDocumentals() {
		if(logger.isDebugEnabled())
			logger.debug("getTipusDocumentals() ");
		
		if (this.tipusDocumentals == null)
	      {
	    	 ConfigService svc = Application.getConfigService(FacesContext.getCurrentInstance());
	         Config wizardCfg = svc.getConfig("Content Wizards");
	         if (wizardCfg != null)
	         {
	        	ConfigElement aspectsCfg = wizardCfg.getConfigElement("content-types");
	            if (aspectsCfg != null)
	            {
	               Element rootElement = null;
	               String path = System.getenv("CESCA_FOLDER");
		           System.out.println("obtenim properties database amb path=" + path +"/alfresco/extension/cescaIArxiuModel.xml");
		           try {
		        	   DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		        	   DocumentBuilder builder = factory.newDocumentBuilder();
		        	   Document document = builder.parse(new File(path +"/alfresco/extension/cescaIArxiuModel.xml"));
		        	   if(document != null) {
		        		   rootElement = document.getDocumentElement();
		        	   }
		           } catch(ParserConfigurationException e) {
		        	   logger.error(e);
					   e.printStackTrace();
		           } catch(IOException e) {
		        	   logger.error(e);
					   e.printStackTrace();
		           } catch(SAXException e) {
		        	   logger.error(e);
					   e.printStackTrace();
		           }
	            	
	               this.tipusDocumentals = new ArrayList<SelectItem>();
	               this.atributs = new ArrayList<SelectItem>();
	               for (ConfigElement child : aspectsCfg.getChildren())
	               {
	            	   if(logger.isDebugEnabled())
		           			logger.debug("valor contenty types() " + child.getAttribute("name"));

	                	  if(child.getAttribute("name").equals("cm:content")){
	                		  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), "Contenido"));
	                	  } else {
	                		  this.tipusDocumentals.add(new SelectItem(child.getAttribute("name"), child.getAttribute("name")));
	                		  
	                		  if(rootElement!= null) {
	                			NodeList list = rootElement.getElementsByTagName("type");
	                		  	for(int i=0; i<list.getLength(); i++) {
	                		  		Node tipo = list.item(i);
	                		  		if (tipo.getNodeType() == Node.ELEMENT_NODE) {
	                		  			Element tipoElement = (Element)tipo;
	                		  			if(child.getAttribute("name").equals(tipoElement.getAttribute("name"))) {
	                		  				NodeList propiedades = tipoElement.getElementsByTagName("property");
	                		  				for(int j=0; j<propiedades.getLength(); j++) {
	                		  					Node propiedad = propiedades.item(j);
	                		  					if (propiedad.getNodeType() == Node.ELEMENT_NODE) {
	                		  						Element prop = (Element)propiedad;
	                		  						this.atributs.add(new SelectItem(prop.getAttribute("name"), prop.getAttribute("name")));
	                		  					}
	                		  				}
	                		  			}
	                		  		}
	                		  	}
	                		  }
	                	  }
	               }
	            }  
	         }
	         this.tipusDocumentals.add(new SelectItem("cm:folder", "Carpeta"));
	         QuickSort sorter = new QuickSort(this.tipusDocumentals, "label", true, IDataContainer.SORT_CASEINSENSITIVE);
             sorter.sort();
	         
	         this.loadDatabaseProperties();
	      }
		
	      return this.tipusDocumentals;
	}
	
	private void loadDatabaseProperties(){
		  
		try
        {
			Properties props = new Properties();
	        FileInputStream fis = null;
	        try {
	        	String path = System.getenv("CESCA_FOLDER");
	        	System.out.println("obtenim properties database amb path=" + path +"/database.properties");
	        	fis = new FileInputStream(path +"/database.properties");
				props.load(fis);
			} catch (IOException e) {
				logger.error(e);
				e.printStackTrace();
			} finally {
				if(fis != null)
					try {
						fis.close();
					} catch (IOException e) {
						logger.warn(e.getMessage());
						e.printStackTrace();
					}
			}
		        String url = (String)props.get("url");;
				String user = (String)props.get("user");
				String password = (String)props.get("password");
				String driver = (String)props.get("driver");
	        	System.out.println("obtenim properties database url " + url);
	        	System.out.println("obtenim properties database user " + user);
	        	System.out.println("obtenim properties database password " + password);
				Class.forName(driver);

                Connection con = DriverManager.getConnection(url, user, password);
                Statement s = con.createStatement(java.sql.ResultSet.TYPE_SCROLL_SENSITIVE,
                		java.sql.ResultSet.CONCUR_READ_ONLY);
                
                //String sql = "SELECT local_name from alf_qname where ns_id = 17";
                String sql = "SELECT * from alf_qname where ns_id = 41 and (local_name like '%expedient' or local_name like '%documentSimple' or local_name like '%agregacio')";
                java.sql.ResultSet rs= s.executeQuery(sql);
                
                while (rs.next()){
                	this.atributs.add(new SelectItem(rs.getString("local_name"), rs.getString("local_name")));
                }
                
                rs.close();
                s.close();
                con.close();
                
    		  	QuickSort sorter = new QuickSort(this.atributs, "label", true, IDataContainer.SORT_CASEINSENSITIVE);
                sorter.sort();

        }
        catch(Exception e)
        {
               System.out.println(e.getMessage());
               e.printStackTrace();
        }
	}
	
	public void cargaLlistatAtributs(ActionEvent actionEvent) {
		if(logger.isDebugEnabled())
			logger.debug("cargaLlistatAtributs() ");
		
		System.out.println("Dentro de cargaLlistatVocabularis");
	    this.atributs = this.tipusDocumentals;     
	}
}
