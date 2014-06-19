package es.cesca.alfresco.wizard;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.xml.rpc.ServiceException;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.wizard.BaseWizardBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cesca.alfresco.util.CescaUtil;
import es.cesca.ws.client.CescaiArxiuWSClient;
import es.cesca.ws.client.exception.ParameterException;

public class RecuperaPeticioEmmagatzemadaWizard extends BaseWizardBean {

	private static final long serialVersionUID = -2433172349904876665L;
	private static Log logger = LogFactory.getLog(RecuperaPeticioEmmagatzemadaWizard.class);
	private ServiceRegistry serviceRegistry;
	private String pia;
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
			System.out.println("init recupera peticio PIA " + pia);
			CescaiArxiuWSClient client = new CescaiArxiuWSClient();

			if (CescaUtil.isEmpty(pia)){
				result = "";
			} else {
				String temp = client.recuperaPIC(pia);
				result = temp;
				
				if (temp == null){
					System.out.println("No result returned");
					result = "No result returned";//No ha de passar mai
			    } else if(temp.startsWith("Error")) {
			    	System.out.println("Error");
			    	//result = temp;//En cas que es produeixi un error
				} else {
					//Retorna un String codificat en base64 correponent al binari d'un fitxer zip retornat per iArxiu on està
					//empaquetat tot l'expedient referent al pia enviat

					//Crear node amb el ZIP a l'espai de l'usuari pq se'l descarregui
					NodeRef node = saveContent(result);
					if (node == null) {
						error = "No s'ha pogut guardar la peticio retornada";
					} else {
						result = "Pot descarregar el document del seu espai personal";
					}
				}
			}
		} catch (FileExistsException e) {
			logger.error(e.getMessage());
			error = formatMessage("FileExistsException", e.getMessage());
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

	protected NodeRef saveContent(String strContent) throws Exception {
		
		//Source of method: CreateContentWizard / BaseContentWizard
		String piaReplace = pia.replaceAll(":","-");
		String fileName = "PE-" + piaReplace + ".zip";
		
		System.out.println("valor pia saveContent " + piaReplace);
		
		String nodeId =	this.navigator.getCurrentUser().getHomeSpaceId();
		NodeRef containerNodeRef;
		if (nodeId == null) {
			return null;
		} else {
			containerNodeRef = new NodeRef(Repository.getStoreRef(), nodeId);
		}

		Map<QName, Serializable> editProps = new HashMap<QName, Serializable>();

		editProps.put(ContentModel.PROP_NAME, fileName);
		editProps.put(ContentModel.PROP_TITLE, fileName);

		NodeRef fileNodeRef = this.getServiceRegistry().getNodeService().createNode(
				containerNodeRef,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName("http://www.alfresco.org/model/content/1.0",
						fileName),
				ContentModel.TYPE_CONTENT, editProps)
				.getChildRef();

		if (logger.isDebugEnabled()) {
			logger.debug("Created file node for file: " + fileName);
		}

		ContentWriter writer = this.getServiceRegistry().getContentService().getWriter(fileNodeRef,
				ContentModel.PROP_CONTENT, true);

		
		
		writer.setMimetype("application/zip");//ZIP always
		writer.setEncoding("ISO-8859-1");//FIXME UTF-8 always??
		
		xtrim.util.codec.Base64Decoder dec = new xtrim.util.codec.Base64Decoder(strContent);
        byte[] processByte = dec.processBytes();
        
        writer.putContent(strContent == null ? "" : new String(processByte, "ISO-8859-1"));

		return fileNodeRef;
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
