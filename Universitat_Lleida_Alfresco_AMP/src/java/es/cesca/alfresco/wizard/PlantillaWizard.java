package es.cesca.alfresco.wizard;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.wizard.BaseWizardBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cesca.alfresco.Object.Vocabulari;
import es.cesca.alfresco.scheduled.ExecuterAbstractBase.ExecuterException;
import es.cesca.alfresco.util.CescaUtil;
import es.cesca.alfresco.wizardExecuter.TractamentPlantilles;

public class PlantillaWizard extends BaseWizardBean{

	private static final long serialVersionUID = -557955106388369334L;
	private static Log logger = LogFactory.getLog(PlantillaWizard.class);
	private ServiceRegistry serviceRegistry;
	
	//Plantilla
	private String nombrePlantilla;
	private String nombrePlantillaOld;
	public String getNombrePlantillaOld() {
		return nombrePlantillaOld;
	}

	public void setNombrePlantillaOld(String nombrePlantillaOld) {
		this.nombrePlantillaOld = nombrePlantillaOld;
	}

	private List<Vocabulari> items = new ArrayList<Vocabulari>();

	//Params
	private String error = null;
	private String result = null;
	private transient DataModel nodes;
	private String edit = null;
	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}

	public String getNombrePlantilla() {
		return nombrePlantilla;
	}

	public void setNombrePlantilla(String nombrePlantilla) {
		this.nombrePlantilla = nombrePlantilla;
	}

	private NodeRef nodeReferencia = null;

	private String tipusDocumental;

	@Override
	public void init(Map<String, String> parameters) {
		if (logger.isDebugEnabled())
			logger.debug("init ");
		
		super.init(parameters);
		this.error = null;
		this.result = null;
		resetFields();
		showTablePlantillas();
		showTableVocabularis();
	}
	
	@Override
	protected String finishImpl(FacesContext ctx, String outcome)
			throws Exception {
		return outcome;
	}
	
	protected String formatMessage(String txt, String _2replace) {
		return MessageFormat.format(Application.getMessage(
                FacesContext.getCurrentInstance(), txt), _2replace);
	}
	
	protected String formatMessage(String txt) {
		return Application.getMessage(FacesContext.getCurrentInstance(), txt);		
	}

	public void deletePlantilles(ActionEvent event) {
		deletePlantilla();
		deletePlantillaWithVocFromButton();
		showTableVocabularis();
		showTablePlantillas();
	}
	
	public void resetFields(){
		if (logger.isDebugEnabled())
			logger.debug("resetFields ");
		
		this.nombrePlantilla = null;
		this.nombrePlantillaOld = null;
		this.nodes = null;
		this.nodeReferencia = null;
		this.tipusDocumental = null;
		this.edit = null;
		this.items = new ArrayList<Vocabulari>();
	}
	
	public void resetFieldsBack(){
		if (logger.isDebugEnabled())
			logger.debug("resetFieldsBack ");
		
		this.nombrePlantilla = null;
		this.nombrePlantillaOld = null;
		this.nodeReferencia = null;
		this.tipusDocumental = null;
		this.edit = null;
		this.nodes = null;
		this.items = new ArrayList<Vocabulari>();
	}
	
	public NodeRef getNodeReferencia() {
		return nodeReferencia;
	}

	public void setNodeReferencia(NodeRef nodeReferencia) {
		this.nodeReferencia = nodeReferencia;
	}

	public List<Vocabulari> getItems() {
		return items;
	}

	public void setItems(List<Vocabulari> items) {
		this.items = items;
	}

	public static void setLogger(Log logger) {
		PlantillaWizard.logger = logger;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	public String getTipusDocumental() {
		return tipusDocumental;
	}

	public void setTipusDocumental(String tipusDocumental) {
		this.tipusDocumental = tipusDocumental;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
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
	
	private void showTablePlantillas() {
		if (logger.isDebugEnabled())
			logger.debug("showTablePlantillas ");
		
		String query = null;
		query = "+PATH:\"/app:company_home/cm:CESCA/cm:Plantilles//.\" +TYPE:\"cesca:CESCA_PLANTILLA\"";
		
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
				logger.debug("query(showTablePlantillas) " + query);
			resultSet = serviceRegistry.getSearchService().query(sp);
            if(resultSet != null) {
            	List<Map<String, Serializable>> list = new ArrayList<Map<String, Serializable>>();
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		Map<String, Serializable> map = new HashMap<String, Serializable>(2);
            		if(fo.getProperties().get(CescaUtil.PLANTILLA_PROP_PLAN_REF)==null){
	            		map.put("nombrePlantilla", fo.getProperties().get(CescaUtil.PLANTILLA_PROP_NAME));
	            		map.put("tipoDocumental", fo.getProperties().get(CescaUtil.PLANTILLA_PROP_TIPO));
	            		list.add(map);
            		}
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

	private void deletePlantillaWithVocFromButton() {
		if (logger.isDebugEnabled())
			logger.debug("deletePlantillaWithVocFromButton ");
		
		FacesContext ctx = FacesContext.getCurrentInstance();  
        String nomPlantilla = (String) ctx.getExternalContext().getRequestParameterMap().get("nomPlantilla"); 
        
        if(nomPlantilla!=null){
        	String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Plantilles//.\" +TYPE:\"cesca:CESCA_PLANTILLA\" +@cesca\\:plantillaRef:\""+nomPlantilla+"\"";
        	
    		//Executa la consulta
        	SearchParameters sp = new SearchParameters();
    		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
    		sp.setQuery(query);
    		sp.addStore(CescaUtil.STORE);
    		
    		ResultSet resultSet = null;
    		try {
    			if (logger.isDebugEnabled())
    				logger.debug("query(deletePlantillaWithVocFromButton) ");
    			
    			resultSet = serviceRegistry.getSearchService().query(sp);
                if(resultSet != null) {
                	for (NodeRef row : resultSet.getNodeRefs()) {
                		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
                		String [] nombrePlantilla = fo.getProperties().get(CescaUtil.PLANTILLA_PROP_NAME).toString().split("#");
                		if(nombrePlantilla[0].equals(nomPlantilla)){
	                		NodeService nodeService = getServiceRegistry().getNodeService();
	                		nodeService.deleteNode(row);
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
	
	private void showTableVocabularis() {
		if (logger.isDebugEnabled())
			logger.debug("showTableVocabularis ");
		
		String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Vocabularis//.\" +TYPE:\"cesca:CESCA_VOCABULARIO\"";
		
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
				logger.debug("(query) showTableVocabularis " + query);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
            if(resultSet != null) {
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		Vocabulari voc = new Vocabulari();
            		voc.setAddPlantilla(false);
            		voc.setNombreVoc((String) fo.getProperties().get(CescaUtil.VOCABULARIO_PROP_NAME));
            		items.add(voc);
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
	
	private void showTableVocabularisEdit(String nomPlantilla) {
		if (logger.isDebugEnabled())
			logger.debug("showTableVocabularisEdit ");
		
		String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Plantilles//.\" +TYPE:\"cesca:CESCA_PLANTILLA\" +@cesca\\:plantillaRef:\""+nomPlantilla+"\"";
		
		if (logger.isDebugEnabled())
			logger.debug("Query > "+ query);
		
		//Executa la consulta
    	SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (showTableVocabularisEdit) " + query);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
            if(resultSet != null) {
            	items = new ArrayList<Vocabulari>();
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		String [] nombreVocabulario = fo.getProperties().get(CescaUtil.PLANTILLA_PROP_NAME).toString().split("#");
            		if(nombreVocabulario[0].equals(nomPlantilla)){
            			map.put(nombreVocabulario[1], nombreVocabulario[1]);
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
		if (logger.isDebugEnabled())
			logger.debug("Obtenemos todos los vocabularios pero con el check a TRUE los que correspondan ");
		
		query = "+PATH:\"/app:company_home/cm:CESCA/cm:Vocabularis//.\" +TYPE:\"cesca:CESCA_VOCABULARIO\"";
		
		if (logger.isDebugEnabled())
			logger.debug("Query > "+ query);
		
    	sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("(query) > Obtenemos todos los vocabularios pero con el check a TRUE los que correspondan " + query);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
            if(resultSet != null) {
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		Vocabulari voc = new Vocabulari();
            		voc.setNombreVoc((String) fo.getProperties().get(CescaUtil.VOCABULARIO_PROP_NAME));
            		if(map.get(fo.getProperties().get(CescaUtil.VOCABULARIO_PROP_NAME))!=null){
            			voc.setAddPlantilla(true);
            		}else{
            			voc.setAddPlantilla(false);
            		}
            		items.add(voc);
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
	
	public void novaEditPlantilla(ActionEvent event){
		if (logger.isDebugEnabled())
			logger.debug("nova plantilla ");
		
		if(CescaUtil.isEmpty(this.nombrePlantilla)){
			this.error = formatMessage("emptyFieldPlantilla");
			this.result = null;
			if (logger.isDebugEnabled())
				logger.debug("error nom plantilal buit ");
			return;
		}
		try {
			//Insertamos o modificamos atributo
			TractamentPlantilles executer = new TractamentPlantilles();
			executer.setServiceRegistry(serviceRegistry);
			
			if(this.edit!=null){
				if (logger.isDebugEnabled())
					logger.debug("nova plantilla (edita)");
				
				executer.editNodePlantilla(
						this.nodeReferencia, this.nombrePlantilla, this.tipusDocumental);
				this.result = formatMessage("OK_edit_plantilla");
    			this.error = null;
    			deletePlantillaWithVoc();
    			for(int i=0;i<this.items.size();i++){
    				Vocabulari voc = this.items.get(i);
    				if(voc.isAddPlantilla()){
    					executer.createNodePlantillaWithVoc(this.nombrePlantilla, voc.getNombreVoc());
    				}
    			}
    			resetFieldsBack();
    			showTablePlantillas();
    			showTableVocabularis();
			}
			else{
				if (logger.isDebugEnabled())
					logger.debug("nova plantilla (nou)");
				
				executer.createNodePlantilla(this.nombrePlantilla, this.tipusDocumental);
				this.result = formatMessage("OK_plantilla");
    			this.error = null;
    			for(int i=0;i<this.items.size();i++){
    				Vocabulari voc = this.items.get(i);
    				if(voc.isAddPlantilla()){
    					executer.createNodePlantillaWithVoc(this.nombrePlantilla, voc.getNombreVoc());
    				}
    			}
    			resetFieldsBack();
    			showTablePlantillas();
    			showTableVocabularis();
			}
		} catch (FileExistsException e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			this.error= formatMessage("already_exists_plantilla", e.getMessage());
			this.result = null;
			showTablePlantillas();
		} catch (ExecuterException e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			this.error = formatMessage("error_generic", e.getMessage());
			this.result = null;
			showTablePlantillas();
		} catch (AlfrescoRuntimeException e) {
			this.error = formatMessage("error_generic", e.getMessage());
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			showTablePlantillas();
		} catch (Exception e) {
			this.error = formatMessage("already_exists_plantilla", e.getMessage());
			this.result = null;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			showTablePlantillas();
		}	
	}
	
	public void editPlantilla(ActionEvent event) {
		if (logger.isDebugEnabled())
			logger.debug("editPlantilla ");
		
		FacesContext ctx = FacesContext.getCurrentInstance();  
        String editPlantilla = (String) ctx.getExternalContext().getRequestParameterMap().get("editPlantilla"); 
        
        if(editPlantilla!=null){
        	String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Plantilles//.\" +TYPE:\"cesca:CESCA_PLANTILLA\" +@cesca\\:nombrePlantilla:\""+editPlantilla+"\"";
        	SearchParameters sp = new SearchParameters();
    		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
    		sp.setQuery(query);
    		sp.addStore(CescaUtil.STORE);
    		
    		ResultSet resultSet = null;
    		try {
    			if (logger.isDebugEnabled())
    				logger.debug("query (editPlantilla) " + query);
    			
    			resultSet = serviceRegistry.getSearchService().query(sp);
                if(resultSet != null&&resultSet.length()>0) {
                	//Siempre devolverà solo un resultado
                	NodeRef noderef = resultSet.getNodeRef(0);
                	this.nodeReferencia = noderef;
                	FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(noderef);
        			this.nombrePlantilla= (String) fo.getProperties().get(CescaUtil.PLANTILLA_PROP_NAME);
        			this.tipusDocumental = (String) fo.getProperties().get(CescaUtil.PLANTILLA_PROP_TIPO);
        			this.nombrePlantillaOld = this.nombrePlantilla;
        			this.error = null;
        			this.result = null;
        			this.edit = "OK";
                }
                showTableVocabularisEdit(editPlantilla);
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
	
	private void deletePlantilla() {
		if (logger.isDebugEnabled())
			logger.debug("deletePlantilla ");
		
		FacesContext ctx = FacesContext.getCurrentInstance();  
        String nomPlantilla = (String) ctx.getExternalContext().getRequestParameterMap().get("nomPlantilla"); 
        
        if(nomPlantilla!=null){
        	String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Plantilles//.\" +TYPE:\"cesca:CESCA_PLANTILLA\" +@cesca\\:nombrePlantilla:\""+nomPlantilla+"\"";
        	SearchParameters sp = new SearchParameters();
    		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
    		sp.setQuery(query);
    		sp.addStore(CescaUtil.STORE);
    		
    		ResultSet resultSet = null;
    		try {
    			if (logger.isDebugEnabled())
    				logger.debug("query(deletePlantilla) " + query);
    			
    			resultSet = serviceRegistry.getSearchService().query(sp);
                if(resultSet != null&&resultSet.length()>0) {
                	//Siempre devolverà solo un resultado
                	NodeRef noderef = resultSet.getNodeRef(0);
                	this.getServiceRegistry().getFileFolderService().delete(noderef);
        			this.result = formatMessage("OK_delete_plantilla");
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
	
	private void deletePlantillaWithVoc() {   
		if (logger.isDebugEnabled())
			logger.debug("deletePlantillaWithVoc ");
		
		String query = "+PATH:\"/app:company_home/cm:CESCA/cm:Plantilles//.\" +TYPE:\"cesca:CESCA_PLANTILLA\" +@cesca\\:plantillaRef:\""+this.nombrePlantillaOld+"\"";
    	SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("query (deletePlantillaWithVoc) " + query);
			
			resultSet = serviceRegistry.getSearchService().query(sp);
            if(resultSet!=null){
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		String [] nombrePlantilla = fo.getProperties().get(CescaUtil.PLANTILLA_PROP_NAME).toString().split("#");
            		if(nombrePlantilla[0].equals(this.nombrePlantillaOld)){
    	            	this.getServiceRegistry().getFileFolderService().delete(row);
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

