package es.cesca.alfresco.wizard;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.wizard.BaseWizardBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cesca.alfresco.scheduled.CercaExpedientsIArxiuExecuter;
import es.cesca.alfresco.scheduled.ExecuterAbstractBase.ExecuterException;
import es.cesca.alfresco.util.CescaUtil;

public class CreaPeticioIArxiuWizard extends BaseWizardBean {

	private static final long serialVersionUID = -5617697589249293402L;
	private static Log logger = LogFactory
			.getLog(CreaPeticioIArxiuWizard.class);
	private String result = null;
	private String error = null;
	private FileInfo node;
	private Boolean isExpedient = true;

	@Override
	public void init(Map<String, String> parameters) {
		super.init(parameters);
		try {
			node = this.getServiceRegistry().getFileFolderService().getFileInfo(new NodeRef(CescaUtil.STORE,
					(String) this.parameters.get("id")));
			isExpedient = new Boolean(this.parameters.get("isExpedient"));
		} catch (Exception e) {
			logger.error(e);
			error = formatMessage("node_can_not_be_found");
		}
	}
	
	@Override
	protected String finishImpl(FacesContext ctx, String outcome)
			throws Throwable {
		return outcome;
	}

	@Override
	public String next() {

		if (logger.isDebugEnabled())
			logger.debug((String) this.parameters.get("id"));

		CercaExpedientsIArxiuExecuter executer = new CercaExpedientsIArxiuExecuter();
		executer.setServiceRegistry(serviceRegistry);

		try {
			if (node !=null) {
				if(isExpedient) {
					System.out.println("Es procedeix a començar la generació manual de petició per un expedient");
				} else {
					System.out.println("Es procedeix a començar la generació manual de petició per un document");
				}
				executer.createPeticioIArxiu(
						new NodeRef(CescaUtil.STORE,
						(String) this.parameters.get("id")), isExpedient, "manual", null);
				
				result = formatMessage("OK");
			}
		} catch (FileExistsException e) {
			logger.error(e.getMessage());
			result = formatMessage("already_exists");
		} catch (ExecuterException e) {
			logger.error(e.getMessage());
			result = e.getMessage();
		} catch (AlfrescoRuntimeException e) {
			result = "S'ha produit un error al repositori alfresco: " +e.getMessage();
			logger.error(result, e);
		} catch (Exception e) {
			result = "S'ha produit un error no identificat: " +e.getMessage();
			logger.error(result, e);
		}
		
		return super.next();
	}
	
	protected String formatMessage(String txt) {

		return Application.getMessage(FacesContext.getCurrentInstance(), txt);
		
	}
	
	private ServiceRegistry serviceRegistry;

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
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * @return the node
	 */
	public FileInfo getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(FileInfo node) {
		this.node = node;
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
	
}
