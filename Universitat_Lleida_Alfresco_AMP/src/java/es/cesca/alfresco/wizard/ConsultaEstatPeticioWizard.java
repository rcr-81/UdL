package es.cesca.alfresco.wizard;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.MessageFormat;

import javax.faces.context.FacesContext;
import javax.xml.rpc.ServiceException;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.wizard.BaseWizardBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cesca.alfresco.util.CescaUtil;
import es.cesca.ws.client.CescaiArxiuWSClient;
import es.cesca.ws.client.exception.ParameterException;

public class ConsultaEstatPeticioWizard extends BaseWizardBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -557955106388369334L;
	private static Log logger = LogFactory.getLog(ConsultaEstatPeticioWizard.class);
	private ServiceRegistry serviceRegistry;
	private String peticio;
	private String result;
	private String error;
	
	@Override
	protected String finishImpl(FacesContext ctx, String outcome)
			throws Throwable {
		
		return outcome;
	}
	
	@Override
	public String back() {
		this.result = null;
		this.error = null;
		return super.back();
	}

	@Override
	public String next() {
		
		try {
			CescaiArxiuWSClient client = new CescaiArxiuWSClient();
			if (CescaUtil.isEmpty(peticio)){
				result = "";
			} else {
				result = client.consultaEstatPeticio(peticio);
			}
			if(result == null || result.equals("")) {
				result = "S'ha produït una errada en la consulta. Comprova que l'identificador és correcte i que existeix connectivitat amb el CESCA.";
			}
		} catch (RemoteException e) {
			logger.error(e);
			error = formatMessage("RemoteException", e.getMessage());
		} catch (ParameterException e) {
			logger.error(e);
			error = formatMessage("ParameterException", e.getMessage());
		} catch (MalformedURLException e) {
			logger.error(e);
			error = formatMessage("RemoteException", e.getMessage());
		} catch (ServiceException e) {
			logger.error(e);
			error = formatMessage("RemoteException", e.getMessage());
		} catch (AlfrescoRuntimeException e) {
			logger.error(e);
			error = e.getMessage();
		} catch (Exception e) {
			logger.error(e);
			error = e.getMessage();
		}
		
		return super.next();
	}
	
	/**
	 * @return the peticio
	 */
	public String getPeticio() {
		return peticio;
	}

	/**
	 * @param peticio the peticio to set
	 */
	public void setPeticio(String peticio) {
		this.peticio = peticio;
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

	protected String formatMessage(String txt, String _2replace) {
		return MessageFormat.format(Application.getMessage(
                FacesContext.getCurrentInstance(), txt), _2replace);
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
	
}
