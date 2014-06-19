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

import es.cesca.alfresco.wizardExecuter.TractamentVocabularis;
import es.cesca.alfresco.scheduled.ExecuterAbstractBase.ExecuterException;
import es.cesca.alfresco.util.CescaUtil;

public class VocabulariWizard extends BaseWizardBean{

	private static final long serialVersionUID = -557955106388369334L;
	private static Log logger = LogFactory.getLog(VocabulariWizard.class);
	private ServiceRegistry serviceRegistry;
	
	//Vocabulari
	private String nombreVoc;

	//Atributo
	private String nombreAtr;
	private String valor;
	private boolean obligatori;
	private String tipus;
	
	//Afegeix valor
	private String constant;
	private String cerca;

	//Params
	private String error = null;
	private String errorAtr = null;
	private String result = null;
	private String edit = null;
	private transient DataModel nodes;
	private transient DataModel nodesAtr;
	private NodeRef nodeReferencia = null;

	protected List<SelectItem> tipusDocumentals;
	protected String tipusDocumental;
	protected String tipusDocumentalExtra;
	protected String tipusDocumentalParser;

	protected List<SelectItem> atributs;
	protected String atribut;
	
	private boolean checkboxTipusDocumental;
	private boolean checkboxTipusDocumentalParser;
	private boolean checkboxConstant;
	private boolean checkboxCerca;
	
	public String getTipusDocumentalExtra() {
		return tipusDocumentalExtra;
	}

	public void setTipusDocumentalExtra(String tipusDocumentalExtra) {
		this.tipusDocumentalExtra = tipusDocumentalExtra;
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
	
	public String getTipusDocumentalParser() {
		return tipusDocumentalParser;
	}

	public void setTipusDocumentalParser(String tipusDocumental) {
		this.tipusDocumentalParser = tipusDocumental;
	}

	@Override
	public void init(Map<String, String> parameters) {
		super.init(parameters);
		this.error = null;
		this.errorAtr = null;
		this.result = null;
		resetFields();
		showTableVocabularis();
	}
	
	@Override
	public String back() {
		if (logger.isDebugEnabled())
			logger.debug("back");
		this.error = null;
		this.errorAtr = null;
		this.result = null;
		resetFieldsBackFromBack();
		showTableVocabularis();
		return super.back();
	}
	
	@Override
	public String next() {
		if (logger.isDebugEnabled())
			logger.debug("next");
		
		this.error = null;
		this.errorAtr = null;
		this.result = null;	
			if(CescaUtil.isEmpty(this.nombreVoc)){
				this.error = formatMessage("emptyFieldVoc");
				if (logger.isDebugEnabled())
					logger.debug("error nombre vacio");
				return super.next();
			}
			//Insertamos vocabulario
			TractamentVocabularis executer = new TractamentVocabularis();
			executer.setServiceRegistry(serviceRegistry);
			
			//Insertamos vocabulari
				try {
					//Create vocabulari
					if (logger.isDebugEnabled())
						logger.debug("nou vocabulari (nou)");
					
					executer.createNodeVocabulari(
							this.nombreVoc);
				} catch (FileExistsException e) {
					logger.error(e.getMessage(),e);
					e.printStackTrace();
					this.error = formatMessage("already_exists", e.getMessage());
				} catch (ExecuterException e) {
					logger.error(e.getMessage(),e);
					e.printStackTrace();
					this.error = formatMessage("error_generic", e.getMessage());
				} catch (AlfrescoRuntimeException e) {
					this.error = formatMessage("error_generic", e.getMessage());
					logger.error(e.getMessage(),e);
					e.printStackTrace();
				} catch (Exception e) {
					this.error = formatMessage("already_exists", e.getMessage());
					logger.error(e.getMessage(),e);
					e.printStackTrace();
				}
		return super.next();
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
	
	public void showTableAtributes() {
		String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Atributs//.\" +TYPE:\"cesca:CESCA_ATRIBUTO\" +@cesca\\:vocabulario_ref:\""+this.nombreVoc+"\"";
		
		if (logger.isDebugEnabled())
			logger.debug("Query (showTableAtributes) "+ query);
		
		//Executa la consulta
    	SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
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
			error = e.getMessage();
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Tanca resultset (showTableAtributes)");
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}
	
	public void netejaConcatenacio(ActionEvent event){
		if(logger.isDebugEnabled())
			logger.debug("Neteja concatenacio");
		this.valor = null;
		this.error = null;
		this.errorAtr = null;
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
			if(logger.isDebugEnabled())
				logger.debug("afegim tipus documental");
			
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
			this.errorAtr = null;
		}else if(this.isCheckboxConstant()){
			if(logger.isDebugEnabled())
				logger.debug("afegim tipus constant");
			
			if(this.valor!=null&&!CescaUtil.isEmpty(this.valor)) {
				this.valor = this.valor+";"+this.constant;
			}else{
				this.valor = this.constant;
			}
			this.tipusDocumentalExtra = null;
			this.error = null;
			this.result = null;
			this.errorAtr = null;
		}else if(this.isCheckboxCerca()){
			if(logger.isDebugEnabled())
				logger.debug("afegim i validem tipus cerca");
			
			String query = this.cerca;
			query = query.replaceAll("query.", "").replaceAll("dmattr.", "");
			
			SearchParameters sp = new SearchParameters();
			sp.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
			sp.setQuery(query);
			sp.addStore(CescaUtil.STORE);
    		
    		ResultSet resultSet = null;
    		try {
    			if(logger.isDebugEnabled())
    				logger.debug("query (validar- vocabulariWizard) " + query);
    			resultSet = serviceRegistry.getSearchService().query(sp);
    			if(resultSet!=null){
    				if(this.valor!=null&&!CescaUtil.isEmpty(this.valor)) {
    					this.valor = this.valor+";"+"query."+this.cerca;
    				}else{
    					this.valor = "query."+this.cerca;
    				}
    				this.tipusDocumentalExtra = null;
    				this.errorAtr = null;
	    			this.error = null;
	    			this.result = formatMessage("OKQuery");
    			}else{
    				this.errorAtr = formatMessage("errorQuery");
        			this.error = null;
        			this.result = null;
    			}
    		}catch(Exception e){
    			this.errorAtr = formatMessage("errorQuery");
    			this.error = null;
    			this.result = null;
    			logger.error(e.getMessage(), e);
    			e.printStackTrace();
    		}finally{
    			if (resultSet != null) {
    				resultSet.close();
    			}
    		}
		}else{
			this.errorAtr = formatMessage("emptyValorAtr");
			this.error = null;
			this.result = null;
		}
	}
	
	public void nouEditAtribut(ActionEvent event){
		if(logger.isDebugEnabled())
			logger.debug("nou atribut");
		
		if(CescaUtil.isEmpty(this.nombreAtr)){
			this.errorAtr = formatMessage("emptyFieldAtr");
			this.error = null;
			this.result = null;
			if(logger.isDebugEnabled())
				logger.debug("error nom atribut buit");
			return;
		}
		try {
			//Insertamos o modificamos atributo
			TractamentVocabularis executer = new TractamentVocabularis();
			executer.setServiceRegistry(serviceRegistry);
			
			if(this.edit!=null){
				if(logger.isDebugEnabled())
					logger.debug("editant atribut");
				
				executer.editNodeAtribute(
						this.nodeReferencia, this.nombreAtr, this.valor, this.tipus, this.obligatori, this.tipusDocumentalExtra, this.nombreVoc);
				this.result = formatMessage("OK_edit_atribut");
				this.errorAtr = null;
    			this.error = null;
    			//Mostramos lista de atributos
    			showTableAtributes();
    			resetFieldsBack();
			}
			else{
				if (logger.isDebugEnabled())
					logger.debug("nou atribut (nou)");
				
				executer.createNodeAtribute(this.nombreAtr, this.valor, this.tipus, this.obligatori, this.tipusDocumentalExtra, this.nombreVoc);
				this.result = formatMessage("OK_atribut");
				this.errorAtr = null;
    			this.error = null;
    			//Mostramos lista de atributos
    			showTableAtributes();
    			resetFieldsBack();
			}
		} catch (FileExistsException e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			this.errorAtr = formatMessage("already_exists", e.getMessage());
			this.error = null;
			this.result = null;
			showTableAtributes();
		} catch (ExecuterException e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			this.errorAtr = formatMessage("error_generic", e.getMessage());
			this.error = null;
			this.result = null;
			showTableAtributes();
		} catch (AlfrescoRuntimeException e) {
			this.errorAtr = formatMessage("error_generic", e.getMessage());
			this.error = null;
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			showTableAtributes();
		} catch (Exception e) {
			this.errorAtr = formatMessage("already_exists", e.getMessage());
			this.error = null;
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			showTableAtributes();
		}
	}
	
	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}

	public void deleteAtributo(ActionEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("eliminant atribut");
		
		FacesContext ctx = FacesContext.getCurrentInstance();  
        String nomAtributo = (String) ctx.getExternalContext().getRequestParameterMap().get("nomAtributo"); 
        
        if(nomAtributo!=null){
        	String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Atributs//.\" +TYPE:\"cesca:CESCA_ATRIBUTO\" +@cesca\\:nombreAtr:\""+nomAtributo+"#"+this.nombreVoc+"\"";
        	
    		//Executa la consulta
        	SearchParameters sp = new SearchParameters();
    		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
    		sp.setQuery(query);
    		sp.addStore(CescaUtil.STORE);
    		
    		ResultSet resultSet = null;
    		try {
    			if(logger.isDebugEnabled())
					logger.debug("query(deleteAtributo) " + query);
    			
    			resultSet = serviceRegistry.getSearchService().query(sp);
                if(resultSet != null&&resultSet.length()>0) {
                	//Siempre devolverà solo un resultado
                	NodeRef noderef = resultSet.getNodeRef(0);
                	this.getServiceRegistry().getFileFolderService().delete(noderef);
        			//Mostramos lista de atributos
        			showTableAtributes();
        			this.result = formatMessage("OK_delete_atribut");
        			this.error = null;
        			this.errorAtr = null;
        			resetFieldsBack();
                }
    		}catch (Exception e) {
    			this.errorAtr = e.getMessage();
    			this.error = null;
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
		if(logger.isDebugEnabled())
			logger.debug("editant atribut");
		
		FacesContext ctx = FacesContext.getCurrentInstance();  
        String editAtributo = (String) ctx.getExternalContext().getRequestParameterMap().get("editAtributo"); 
        
        if(editAtributo!=null){
        	String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Atributs//.\" +TYPE:\"cesca:CESCA_ATRIBUTO\" +@cesca\\:nombreAtr:\""+editAtributo+"#"+this.nombreVoc+"\"";
        	
    		//Executa la consulta
        	SearchParameters sp = new SearchParameters();
    		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
    		sp.setQuery(query);
    		sp.addStore(CescaUtil.STORE);
    		
    		ResultSet resultSet = null;
    		try {
    			if(logger.isDebugEnabled())
					logger.debug("query(editAtributo) " + query);
    			
    			resultSet = serviceRegistry.getSearchService().query(sp);
                if(resultSet != null&&resultSet.length()>0) {
                	//Siempre devolverà solo un resultado
                	NodeRef noderef = resultSet.getNodeRef(0);
                	this.nodeReferencia = noderef;
                	FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(noderef);
        			String []nombreAtributo = fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_NAME).toString().split("#");
        			this.nombreAtr = nombreAtributo[0];
        			this.valor = (String) fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_VALOR);
        			
        			this.checkboxCerca = false;
					this.cerca = null;
					this.checkboxTipusDocumental = false;
					this.checkboxTipusDocumentalParser = false;
					this.atribut = null;
					this.checkboxConstant = false;
					this.constant = null;
					this.tipusDocumental = null;
					this.tipusDocumentalExtra = null;
					
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
        			this.tipus = (String) fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_TIPUS);
        			String obli = (String) fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_OBLIGATORI);
        			if(obli.equals("true")) this.obligatori = true;
        			else this.obligatori = false;
        			this.edit = "OK";
        			this.error = null;
        			this.errorAtr = null;
        			this.result = null;
                }
    		}catch (Exception e) {
    			this.errorAtr = e.getMessage();
    			this.error = null;
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
	
	public void resetFields(){
		if(logger.isDebugEnabled())
			logger.debug("resetFields");
		
		this.nombreAtr = null;
		this.nombreVoc = null;
		this.tipus = null;
		this.valor = null;
		this.obligatori = false;
		this.constant = null;
		this.cerca = null;
		this.tipusDocumental = null;
		this.tipusDocumentalExtra = null;
		this.tipusDocumentals = null;
		this.atribut = null;
		this.atributs = null;
		this.checkboxCerca = false;
		this.checkboxConstant = false;
		this.checkboxTipusDocumental = false;
		this.checkboxTipusDocumentalParser = false;
		this.nodes = null;
		this.nodesAtr = null;
		this.edit = null;
		this.nodeReferencia = null;
		this.nodes = null;
		this.nodesAtr = null;
	}
	
	public void resetFieldsBack(){
		if(logger.isDebugEnabled())
			logger.debug("resetFieldsBack");
		
		this.nombreAtr = null;
		this.tipus = null;
		this.valor = null;
		this.obligatori = false;
		this.checkboxCerca = false;
		this.checkboxConstant = false;
		this.checkboxTipusDocumental = false;
		this.constant = null;
		this.cerca = null;
		this.tipusDocumental = null;
		this.tipusDocumentalParser = null;
		this.tipusDocumentalExtra = null;
		this.tipusDocumentals = null;
		this.atribut = null;
		this.atributs = null;
		this.edit = null;
		this.nodeReferencia = null;
	}
	
	public void resetFieldsBackFromBack(){
		if(logger.isDebugEnabled())
			logger.debug("resetFieldsBackFromBack");
		
		this.nombreAtr = null;
		this.tipus = null;
		this.valor = null;
		this.obligatori = false;
		this.constant = null;
		this.cerca = null;
		this.edit = null;
		this.nodeReferencia = null;
		this.tipusDocumental = null;
		this.tipusDocumentals = null;
		this.atribut = null;
		this.atributs = null;
		this.nodesAtr = null;
		this.checkboxCerca = false;
		this.checkboxConstant = false;
		this.checkboxTipusDocumental = false;
	}
	
	public NodeRef getNodeReferencia() {
		return nodeReferencia;
	}

	public void setNodeReferencia(NodeRef nodeReferencia) {
		this.nodeReferencia = nodeReferencia;
	}
	
	public String getErrorAtr() {
		return errorAtr;
	}

	public void setErrorAtr(String errorAtr) {
		this.errorAtr = errorAtr;
	}

	public static Log getLogger() {
		return logger;
	}

	public static void setLogger(Log logger) {
		VocabulariWizard.logger = logger;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	
	public DataModel getNodesAtr() {
		return nodesAtr;
	}

	public void setNodesAtr(DataModel nodesAtr) {
		this.nodesAtr = nodesAtr;
	}
	
	public String getTipusDocumental() {
		return tipusDocumental;
	}

	public void setTipusDocumental(String tipusDocumental) {
		this.tipusDocumental = tipusDocumental;
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

	public String getTipus() {
		return tipus;
	}

	public void setTipus(String tipus) {
		this.tipus = tipus;
	}

	public boolean isObligatori() {
		return obligatori;
	}

	public void setObligatori(boolean obligatori) {
		this.obligatori = obligatori;
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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getNombreVoc() {
		return nombreVoc;
	}

	public void setNombreVoc(String nombreVoc) {
		this.nombreVoc = nombreVoc;
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
	
	private void showTableVocabularis() {
		String query = null;
		query = "+PATH:\"/app:company_home/cm:CESCA/cm:Vocabularis//.\" +TYPE:\"cesca:CESCA_VOCABULARIO\"";
		
		if (logger.isDebugEnabled())
			logger.debug("Query (showTableVocabularis)> "+ query);
		
		//Executa la consulta
    	SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			resultSet = serviceRegistry.getSearchService().query(sp);

            if(resultSet != null) {
            	List<Map<String, Serializable>> list = new ArrayList<Map<String, Serializable>>();
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		Map<String, Serializable> map = new HashMap<String, Serializable>(1);
            		map.put("nombreVoc", fo.getProperties().get(CescaUtil.VOCABULARIO_PROP_NAME));
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
	
	public void deleteVocabulario(ActionEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("delete vocabulario event ");
		
		deleteAtributos();
		deleteVocabulario();
	}

	private void deleteVocabulario() {
		if(logger.isDebugEnabled())
			logger.debug("delete vocabulario event ");
		
		FacesContext ctx = FacesContext.getCurrentInstance();  
        String nomVocabulario = (String) ctx.getExternalContext().getRequestParameterMap().get("nomVocabulario"); 
        
        if(nomVocabulario!=null){
        	String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Vocabularis//.\" +TYPE:\"cesca:CESCA_VOCABULARIO\" +@cesca\\:nombreVoc:\""+nomVocabulario+"\"";
        	
    		//Executa la consulta
        	SearchParameters sp = new SearchParameters();
    		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
    		sp.setQuery(query);
    		sp.addStore(CescaUtil.STORE);
    		
    		ResultSet resultSet = null;
    		try {
    			if(logger.isDebugEnabled())
    				logger.debug("query(deleteVocabulario) " + query);
    			
    			resultSet = serviceRegistry.getSearchService().query(sp);
                if(resultSet != null&&resultSet.length()>0) {
                	//Siempre devolverà solo un resultado
                	NodeRef noderef = resultSet.getNodeRef(0);
                	this.getServiceRegistry().getFileFolderService().delete(noderef);
        			//Mostramos lista de atributos
        			showTableVocabularis();
        			this.result = formatMessage("OK_delete_vocabulario");
        			this.error = null;
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

	private void deleteAtributos() {
		if(logger.isDebugEnabled())
			logger.debug("delete atributos() ");
		
		FacesContext ctx = FacesContext.getCurrentInstance();  
        String nomVocabulario = (String) ctx.getExternalContext().getRequestParameterMap().get("nomVocabulario"); 
        
        if(nomVocabulario!=null){
        	String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Atributs//.\" +TYPE:\"cesca:CESCA_ATRIBUTO\" +@cesca\\:vocabulario_ref:\""+nomVocabulario+"\"";
        	
    		//Executa la consulta
        	SearchParameters sp = new SearchParameters();
    		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
    		sp.setQuery(query);
    		sp.addStore(CescaUtil.STORE);
    		
    		ResultSet resultSet = null;
    		try {
    			if(logger.isDebugEnabled())
    				logger.debug("query(deleteAtributos) " + query);
    			
    			resultSet = serviceRegistry.getSearchService().query(sp);
                if(resultSet != null) {
                	for (NodeRef row : resultSet.getNodeRefs()) {
                		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
                		String []nombreAtributo = fo.getProperties().get(CescaUtil.ATRIBUTO_PROP_NAME).toString().split("#");
                		if(nombreAtributo[1].equals(nomVocabulario)){
                			this.getServiceRegistry().getFileFolderService().delete(row);
                		}
                	}
                }
    		}catch (Exception e) {
    			this.error = e.getMessage();
    			this.result = null;
    			logger.error(e.getMessage(), e);
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
	
	public List<SelectItem> getAtributs() {
		if(this.atributs==null) atributs = new ArrayList<SelectItem>();
		return atributs;
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
			System.out.println("holaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			Properties props = new Properties();
	        FileInputStream fis = null;
	        try {
	        	String path = System.getenv("CESCA_FOLDER");
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
                
                String sql = "SELECT local_name from alf_qname where ns_id = 6";
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
