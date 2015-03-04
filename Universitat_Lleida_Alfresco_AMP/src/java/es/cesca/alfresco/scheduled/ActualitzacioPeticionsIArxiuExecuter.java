package es.cesca.alfresco.scheduled;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.LimitBy;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.bean.repository.Repository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;

import com.smile.webscripts.helper.UdlProperties;

import es.cesca.alfresco.util.CescaUtil;
import es.cesca.ws.client.CescaiArxiuWSClient;
import es.cesca.ws.client.exception.ParameterException;

public class ActualitzacioPeticionsIArxiuExecuter extends ExecuterAbstractBase {

	private static Log logger = LogFactory.getLog(ActualitzacioPeticionsIArxiuExecuter.class);
	
	@Override
	protected void executeImpl(Action action, NodeRef node) {
		
		System.out.println("Entrant: ActualitzacioPeticionsIArxiuExecuter: executeImpl");
		if (node == null)
			return;
		
		boolean marcarEnviamentErroni = false;
		String filename = CescaUtil.dateFormat.format(new Date());
		FileAppender appender = null;
		UserTransaction tx = null;
		String result = null;//Sept mod: initialization
		try {
			tx = this.getServiceRegistry().getTransactionService().getNonPropagatingUserTransaction();
			tx.begin();
	         
			if(logger.isDebugEnabled())
				logger.debug("Inici actualitzacio peticio pel node: "+ node.getId());
			
			//Recupera el ID de peticio
			String idExpMid = (String)getServiceRegistry().getNodeService().getProperty(node, CescaUtil.PETICIO_PROP_ID_EXP);
			
			logger.debug("L'identificador de id_exp := " + idExpMid);
			
			if(idExpMid==null || idExpMid.equalsIgnoreCase("")){
				logger.debug("Es una peticio de document sense expedient");
				idExpMid = (String)getServiceRegistry().getNodeService().getProperty(node, CescaUtil.PETICIO_PROP_NUM_DOC);				
			}

			String peticio = (String)getServiceRegistry().getNodeService().getProperty(node, CescaUtil.PETICIO_PROP_ID);
			
			//***Start tracer***
			filename = peticio + filename;//Conte el timestamp per evitar que dues execucions del mateix node produeixin un error a mÃ©s cal mantenir el logs diferenciats per cada execuciÃ³
			appender = this.startTracer(filename);
			setTracerMessage("Actualitza Peticio "+peticio + "id_middleware " + idExpMid);
			
			
			if (CescaUtil.isEmpty(idExpMid)) {
				
				marcarEnviamentErroni = true;
				throw new ExecuterException("L'identificador d'expedient de iArxiu es buit a la peticio: "+ peticio);
	
			} else {
				//crida a iArxiu
				CescaiArxiuWSClient client = new CescaiArxiuWSClient();
				result = client.consultaEstatPeticio(idExpMid);
				
				System.out.println("result consulta WS " + result);
				
				if (!this.isPeticioOK(result.trim())) {
					//Error a les dades d'enviament o resultat erroni -> log + mail
					System.out.println("peticio incorrecte  " + result);
					marcarEnviamentErroni = true;
					throw new ExecuterException("La resposta a consultaEstatPeticio ha estat: "+ result);
	
				} else if(result.startsWith(CescaUtil.RESULT_REGISTRAT)) {//Enregistrat: {pia} --> S'ha enregistrat correctament
					setTracerMessage("S'ha enregistrat la peticio: "+peticio+" amb resultat: "+ result);
					//Get and validate pia
					//String[] tokens = result.split(":");//Tallem pels dos punts
					
					/*if (tokens == null || tokens.length != 2 || CescaUtil.isEmpty(tokens[1])){
						//Error a les dades d'enviament NO HI HA PIA -> log + mail
						marcarEnviamentErroni = true;
						throw new ExecuterException("La resposta correcta a consultaEstatPeticio no conté PIA (o el format no es correcte): "+ result);
					}*/
					String pia = null;
					if(result.split("Enregistrat")[1] != null) {
	            		pia = result.split("Enregistrat")[1];
					}else{
						marcarEnviamentErroni = true;
						throw new ExecuterException("La resposta correcta a consultaEstatPeticio no conte PIA (o el format no es correcte): "+ result);
					}
					//String pia = tokens[1].trim();
					System.out.println("update peticio amb pia " + pia);
					updateExpedientTransferit(node, pia);
					System.out.println("creant rebut");
					createRebutIArxiu(peticio, node, idExpMid, pia);
					System.out.println("peticio modificada i rebut creat correctament");
				}
			}
			
	        // commit the transaction
	        tx.commit();
			tx = null;
		} catch (ParameterException e) {
			result = "El parametre: " +e.getMessage()+" es obligatori per fer la crida iArxiu";
			setTracerErrorMessage(result);
			marcarEnviamentErroni = true;
		} catch (ExecuterException e) {
			result = e.getMessage();
			setTracerErrorMessage(result);
			marcarEnviamentErroni = true;
		} catch (RemoteException e){
			setTracerErrorMessage("S'ha produit un error de comunicacio amb iArxiu: " +e.getMessage(), e);
		} catch (AlfrescoRuntimeException e) {
			setTracerErrorMessage("S'ha produit un error al repositori alfresco: " +e.getMessage(), e);
		} catch (Exception e) {
			setTracerErrorMessage("S'ha produit un error no identificat: " +e.getMessage(), e);
		} finally {
			// rollback the transaction
			try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
			try {
				System.out.println("valor finally " + marcarEnviamentErroni + ", si true actualitzem peticio amb error");
				if (marcarEnviamentErroni) {
					this.marcaErrorAPeticio(node, result);//Sept mod: result added
					setTracerMessage("S'ha actualitzat l'estat de la peticio com a erronea");
				}
			} catch(Exception e) {
				logger.error(e);
			}
			//SEND MAIL
			if (containsError())
				try {
					//complete + move to abstract
					this.sendErrorMail(CescaUtil.PROCESS_ACTUALITZACIO, filename);
				}catch (Exception e) {
					logger.error("No s'ha pogut enviar el mail: ", e);
					e.printStackTrace();
				}
			//END TRACER
			try {
				this.storeTracer(CescaUtil.PROCESS_ACTUALITZACIO, filename, appender, containsError());
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			}
	
		}
		
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> arg0) {
		//Nothing to do

	}

	protected void updateExpedientTransferit(NodeRef node, String pia) {
		
		NodeService nodeService = getServiceRegistry().getNodeService();
		nodeService.setProperty(node, CescaUtil.PETICIO_PROP_ESTAT, CescaUtil.STATUS_TRANSFERIT);
		nodeService.setProperty(node, CescaUtil.PETICIO_PROP_PIA, pia);
		setTracerMessage("Node transferit correctament amb pia > "+pia +" (actualitzat al repositori)");
		
		// Se marca el expediente como enviado a iArxiu
		addIArxiuAspect(node, pia);
	}

	private void addIArxiuAspect(NodeRef node, String pia) {
		// Se obtiene el campo id_peticion que coincide con el nodeRef
		NodeService nodeService = getServiceRegistry().getNodeService();
		String idPeticion = (String)nodeService.getProperty(node, CescaUtil.PETICIO_PROP_ID);
		
		// Se añade el aspecto iArxiu al expediente
		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
		props.put(QName.createQName(UdlProperties.UDL_URI, "id_ref_PIA"), pia);
		props.put(QName.createQName(UdlProperties.UDL_URI, "id_peticio"), idPeticion);
		nodeService.addAspect(new NodeRef(Repository.getStoreRef(), idPeticion), QName.createQName(UdlProperties.UDL_URI, "iarxiu"), props);
	}
	
	protected void createRebutIArxiu(String peticio, NodeRef peticioRef, String idExpMid, String pia) {
		if(logger.isDebugEnabled())
			logger.debug("Crea rebut Peticio: "+ peticio);

		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			
		//String name = (String)getServiceRegistry().getNodeService().getProperty(peticioRef, CescaUtil.PETICIO_PROP_ID);
		String name = pia.replaceAll(":", "-");
		
		System.out.println("valor pia creant rebut " + name);
		
		props.put(CescaUtil.REBUT_PROP_ID_EXP, idExpMid);
		props.put(CescaUtil.REBUT_PROP_PIA, pia);
		props.put(ContentModel.PROP_NAME, name);
		props.put(ContentModel.PROP_TITLE, name);
		
		NodeRef parent = getRebutsFolderNode();
	
		//crea node de tipus peticio amb les metadades corresponents
		FileInfo info = getServiceRegistry().getFileFolderService().create(parent, name, CescaUtil.TYPE_REBUT);
		NodeRef node = info.getNodeRef();
		
		getServiceRegistry().getNodeService().setProperties(node, props);
		
		//No hi ha contingut que guardar dins al rebut?
		setTracerMessage("S'ha generat el rebut de la peticio > "+peticio +" (actualitzat al repositori)");
	}
	
	private static NodeRef rebutFolder = null;
	protected NodeRef getRebutsFolderNode() {
		
		if (rebutFolder == null) {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Cerca folder rebuts");
			
    		rebutFolder = this.searchForANode(CescaUtil.PATH_REBUT);

		}
		
		if (rebutFolder == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar els rebuts no existeix");
		}
		return rebutFolder;
		
	}

}