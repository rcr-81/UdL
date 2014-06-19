package es.cesca.alfresco.wizard;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.wizard.BaseWizardBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cesca.alfresco.util.CescaUtil;

public class ConsultaGeneralWizard extends BaseWizardBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5617697589249293402L;
	private static Log logger = LogFactory.getLog(ConsultaGeneralWizard.class);
	private ServiceRegistry serviceRegistry;
	private String tipus;
	private String error;
	private transient DataModel nodes;
	
	BufferedWriter out = null;
	FileWriter fstream = null;
	Properties properties = null;

	@Override
	public void init(Map<String, String> parameters) {
		super.init(parameters);
	}
	
	@Override
	protected String finishImpl(FacesContext ctx, String outcome)
			throws Throwable {
		return outcome;
	}

	@Override
	public String next() {
		String query = null;
		if(tipus.equalsIgnoreCase(CescaUtil.SEARCH_ALL))
			query = "+PATH:\"/app:company_home/cm:CESCA/cm:Peticions_iArxiu//.\" +TYPE:\"cesca:CESCA_PETICIO_IARXIU\"";
		else {
			String oldTipus = tipus;
			if(tipus.contains("En tramitaci")) {
				oldTipus = "En tramit*";
			}
			query = "+PATH:\"/app:company_home/cm:CESCA/cm:Peticions_iArxiu//.\" +TYPE:\"cesca:CESCA_PETICIO_IARXIU\" +@cesca\\:estat:\""+oldTipus+"\"";
			System.out.println("Query a executar a la cerca general: " + query);
		}
		if (logger.isDebugEnabled())
			logger.debug("Query > "+ query);
		
		//Executa la consulta
    	SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		try {
			resultSet = serviceRegistry.getSearchService().query(sp);
			logger.info("ResulSet > " + resultSet.length());
            if(resultSet != null) {
            	List<Map<String, Serializable>> list = new ArrayList<Map<String, Serializable>>();
            	for (NodeRef row : resultSet.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(row);
            		Map<String, Serializable> map = new HashMap<String, Serializable>(3);
            		map.put("name", fo.getName());
            		map.put("exp", fo.getProperties().get(CescaUtil.PETICIO_PROP_NUMEXP));
            		map.put("doc",fo.getProperties().get(CescaUtil.PETICIO_PROP_NUM_DOC));
               		list.add(map);
				}
    			nodes = new ListDataModel(list);
	    	}
		
		} catch (Exception e) {
			error = e.getMessage();
			logger.error(e);
		} finally {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Tanca resultset");
			if (resultSet != null) {
				resultSet.close();
			}
			
		}
		
		return super.next();
	}

	@Override
	public String back() {

		this.nodes = null;
		this.error = null;
		
		return super.back();
	}
	
	/**
	 * @return the tipus
	 */
	public String getTipus() {
		return tipus;
	}

	/**
	 * @param tipus the tipus to set
	 */
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}

	/**
	 * @return the serviceRegistry
	 */
	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	/**
	 * @param serviceRegistry the serviceRegistry to set
	 */
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @return the nodes
	 */
	public DataModel getNodes() {
		return nodes;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(DataModel nodes) {
		this.nodes = nodes;
	}
	
	protected String formatMessage(String txt) {

		return Application.getMessage(FacesContext.getCurrentInstance(), txt);
		
	}
}
