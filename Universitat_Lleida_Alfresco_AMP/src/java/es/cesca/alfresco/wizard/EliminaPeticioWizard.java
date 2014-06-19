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

public class EliminaPeticioWizard extends BaseWizardBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5617697589249293402L;
	private static Log logger = LogFactory.getLog(EliminaPeticioWizard.class);
	private ServiceRegistry serviceRegistry;
	private String pia;
	private String result;
	private String error;

	/* Ha d'eliminar les dades de la petició emmagatzemada a iArxiu a partir de
	   l'identificador de referència del PIA.
	 * (non-Javadoc)
	 * @see org.alfresco.web.bean.dialog.BaseDialogBean#finishImpl(javax.faces.context.FacesContext, java.lang.String)
	 */
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
			if (CescaUtil.isEmpty(pia)){
				result = "";
			} else {
				result = client.eliminaPeticio(pia);
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
	 * @return the pia
	 */
	public String getPia() {
		return pia;
	}

	/**
	 * @param pia the pia to set
	 */
	public void setPia(String pia) {
		this.pia = pia;
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
	protected String formatMessage(String txt, String _2replace) {
		return MessageFormat.format(Application.getMessage(
                FacesContext.getCurrentInstance(), txt), _2replace);
	}
}
