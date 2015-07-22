package es.cesca.alfresco.scheduled;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.modelmbean.XMLParseException;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.bean.repository.Repository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;

import com.smile.webscripts.helper.ConstantsUdL;

import es.cesca.alfresco.util.CescaUtil;
import es.cesca.alfresco.util.UDLHelper;
import es.cesca.alfresco.util.XMLHelper;
import es.cesca.alfresco.util.XMLHelper.MandatoryFieldException;
import es.cesca.ws.client.CescaiArxiuWSClient;
import es.cesca.ws.client.exception.ParameterException;

public final class EnviamentExpedientsIArxiuExecuter extends ExecuterAbstractBase {

	private static Log logger = LogFactory.getLog(EnviamentExpedientsIArxiuExecuter.class);
	
	private CescaiArxiuWSClient client;
	
	private static final String EXPEDIENT = "expedient";
	private static final String DOCUMENT = "document";
	/* Encarregat de cercar totes les CESCA:CESCA_PETICIO_IARXIU amb l’estat a Pendent i realitzarà
	 * comunicacions via servei web amb el middleware
	 * 
	 * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	protected void executeImpl(Action action, NodeRef nodePet) {
		//Nodos en estado pendiente
		System.out.println("Entrant: EnviamentExpedientsIArxiuExecuter: executeImpl");
		
		logger.info("Entrant: EnviamentExpedientsIArxiuExecuter: executeImpl");
		if(logger.isDebugEnabled())
			logger.debug("Inici enviament expedients a iArxiu: "+ nodePet.getId());
				
		// Envia l'expedient a iArxiu
		boolean marcarEnviamentErroni = false;
		String filename = CescaUtil.dateFormat.format(new Date());
		FileAppender appender = null;
		UserTransaction tx = null;
		String result = null;
		
		try {
	        tx = this.getServiceRegistry().getTransactionService().getNonPropagatingUserTransaction();
	        tx.begin();
			
			String idNode 			= (String)getServiceRegistry().getNodeService().getProperty(nodePet, CescaUtil.PETICIO_PROP_ID);//ES UNIC
			String numExp 			= (String)getServiceRegistry().getNodeService().getProperty(nodePet, CescaUtil.PETICIO_PROP_NUMEXP);
			String numDoc 			= (String)getServiceRegistry().getNodeService().getProperty(nodePet, CescaUtil.PETICIO_PROP_NUM_DOC);
			String idConfiguracio 	= (String)getServiceRegistry().getNodeService().getProperty(nodePet, CescaUtil.PETICIO_PROP_ID_CONFIG);
			
			System.out.println("valor idNode " + idNode);
			System.out.println("valor numExp " + numExp);
			System.out.println("valor numDoc " + numDoc);
			
			boolean isExp = true;
			System.out.println("iniciamos WS");
			client = new CescaiArxiuWSClient();
			String idexp = null;
			String idDocumentMiddleware = null;
			
			//***Start tracer***
			filename = idNode + filename;//Conte el timestamp per evitar que dues execucions del mateix node produeixin un error a mes cal mantenir el logs diferenciats per cada execucio
			appender = this.startTracer(filename);
			setTracerMessage("Peticio "+idNode+" Expedient: "+numExp +" Document: "+numDoc);
			
			if (CescaUtil.isEmpty(idNode)) {
				//Error and send message >> no idNode found > peticio broken
				setTracerErrorMessage("No hi ha identificador de l'expedient/document a enviar, no es pot procedir amb l'execucio.");
				marcarEnviamentErroni = true;
				return;
			}
			
			NodeRef node = new NodeRef(CescaUtil.STORE, idNode);
			
			if (CescaUtil.isEmpty(numExp) && CescaUtil.isEmpty(numDoc)) {
				//Error and send message >> no exp ni doc found > peticio broken
				setTracerErrorMessage("No hi ha identificador d'expedient ni identificador de document, no es pot procedir amb l'execucio.");
				return;
			} else if (CescaUtil.isEmpty(numDoc)) {//treballem amb expedient
				System.out.println("enviament expedient");
				if(logger.isDebugEnabled())
					logger.debug("Node actual es un expedient > "+numExp);
				
				setTracerMessage("Procediment d'enviament de l'expedient");
				
				idexp = client.obrirConnexio(numExp);

				setTracerMessage(idexp);
				
				if (!this.isPeticioOK(idexp)) {
					//Error and send message
					setTracerErrorMessage("No s'ha pogut obrir connexio amb iArxiu. "+idexp);
					marcarEnviamentErroni = true;
					result = idexp;
					return;
				}
				setTracerMessage("Connexio establerta per l'expedient: "+numExp+ " amb identificador d'expedient d'iArxiu: "+idexp);
				// Peticions expedient (una per cada expedient)
				//this.peticioExpedient(idexp, numExp, node, idConfiguracio);
				// Adaptación UdL 
				this.peticioExpedient(idexp, idNode, node, idConfiguracio);

				setTracerMessage("S'han enviat tots els documents i signatures associades" + idexp);
				result = client.tancarConnexio(idexp,EXPEDIENT);
				setTracerMessage("valor result tancar " + result);
				if (!this.isPeticioOK(result)) {
					//Error and send message
					setTracerErrorMessage("No s'ha pogut tancar connexio amb iArxiu. "+result);
					marcarEnviamentErroni = true;
					return;
				}
				setTracerMessage("S'han tancat correctament la connexio amb iArxiu");
				
			} else if (CescaUtil.isEmpty(numExp)) {//treballem amb un document indepe
				System.out.println("enviament document");
				isExp = false;
				if(logger.isDebugEnabled())
					logger.debug("Node actual es un document > "+numDoc);
				
				setTracerMessage("Procediment d'enviament del document independent: "+numDoc);
				idDocumentMiddleware = this.peticioDocumentSinExp(null, node, numDoc, idConfiguracio);

				setTracerMessage("Finalitzat l'enviament del document independent i les signatures associades, i tancant connexio");
				client.tancarConnexio(idDocumentMiddleware,DOCUMENT);
				setTracerMessage("Enviament i tancar connexio correctes");
			}
			
			// Establiment de connexio i identificacio del expedient (una per cada expedient) / o pel document independent
			
			System.out.println("update de la peticio amb idExp = "+idexp + " idDocumentMiddleware=" +idDocumentMiddleware);
			updatePeticioToEnTramit(nodePet, isExp, idexp, idDocumentMiddleware);
			setTracerMessage("S'ha actualitzat l'estat de la peticio [Aquest es el darrer pas de l'enviament]");
			
	        // commit the transaction
	        tx.commit();
			tx = null;
		} catch (ParameterException e) {
			result = "El parametre: " +e.getMessage()+" es obligatori per fer la crida iArxiu";
			setTracerErrorMessage(result);
			marcarEnviamentErroni = true;
		} catch (XMLParseException e) {
			result = "L'xml de l'arxiu de configuracio no es valid: " +e.getMessage();
			setTracerErrorMessage(result);
			marcarEnviamentErroni = true;
		} catch (MandatoryFieldException e) {
			result = "L'xml amb el vocabulari o les dades que l'han d'omplir son incorrectes: " +e.getMessage();
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
				if (marcarEnviamentErroni) {
					this.marcaErrorAPeticio(nodePet,result);
					setTracerMessage("S'ha actualitzat l'estat de la peticio com a erronea");
				}
			} catch(Exception e) {
				logger.error(e);
			}
			//SEND MAIL
			if (containsError())
				try {
					//complete + move to abstract
					//this.sendErrorMail(CescaUtil.PROCESS_ENVIAMENT, filename);
				} catch (Exception e) {
					logger.error("No s'ha pogut enviar el mail: ", e);
				}
			//END TRACER
			try {
				this.storeTracer(CescaUtil.PROCESS_ENVIAMENT, filename, appender, containsError());
			} catch (Exception e) {
				logger.error(e);
			}

		}
		
	}
	
	private void peticioExpedient(String idMexMid, String numExp, NodeRef expNode, String idConfiguracio) throws MandatoryFieldException, XMLParseException, RemoteException, ParameterException, ExecuterException {
		//Con el nodo recuperamos su tipo y buscamos su configuracion activa
		QName type = this.getServiceRegistry().getNodeService().getType(expNode);
		NodeRef configuration = this.getExpedientCFG(idConfiguracio);
		if (configuration == null)
			throw new ExecuterException("No s'ha trobat l'arxiu de configuracio per l'expedient amb tipus: "+type.toPrefixString());
		setTracerMessage("Tipus d'expedient: "+type.toPrefixString() +" node amb la configuracio: "+configuration.getId());
		ContentReader reader = this.getServiceRegistry().getContentService().getReader(configuration, ContentModel.PROP_CONTENT);
		String xmlbase = reader.getContentString();
		//---
		
		//Recupera metadades ---
		Map<QName, Serializable> props = this.getServiceRegistry().getNodeService().getProperties(expNode);
		Map<String, Serializable> toBind = this.getProperties(props);
		
		XMLHelper helper = new XMLHelper(xmlbase);
		
		// Inicio personalización UDL: adaptar metadatos al modelo iArxiu
		UDLHelper UDLHelper = new es.cesca.alfresco.util.UDLHelper(serviceRegistry, toBind, expNode, null);
		toBind = UDLHelper.bindPropsToUDL("expedient");
		// Fin 

		String metadata = helper.xmlToIArxiu(toBind, this.serviceRegistry, true);
		
		logger.debug("valor xml expedient per enviar a WS " + metadata);
		
		if (!helper.isExp()) {
			//No puede ser que el xml de un expediente diga que no lo es >> Exception
			throw new ExecuterException("El node actual es un expedient pero no s'ha pogut localitzar la propietat que l'identifica com expedient");
		}
		setTracerMessage("La peticio a iArxiu per expedient amb dades: "+metadata);
		//metadata = this.encodeBase64(metadata.getBytes());
		try {
			metadata = org.apache.axis.encoding.Base64.encode(metadata.getBytes("UTF-8"));
		} catch(UnsupportedEncodingException e) {
			System.out.println("Error al codificar les metadades a UTF-8");
			throw new ExecuterException("No s'han pogut codificar correctament les metadades a UTF-8");
		}
		helper = null;//Helper will not be required anymore > break reference >> GC
		// ---
		
		String result = client.peticioExpedient(idMexMid, metadata);
		System.out.println("Response exp: " + result);

		if (result != null && this.isPeticioOK(result)) {
			
			setTracerMessage("La peticio a iArxiu per expedient ha resultat correcta");
			//Recupera documents de l'expedient
			String query4Docs = (String)this.getServiceRegistry().getNodeService().getProperty(configuration, CescaUtil.CFG_EXP_PROP_CRITERI_CERCA_DOCS);
			if (CescaUtil.isEmpty(query4Docs)) {
				//throw exception > not a valid query or empty
				throw new ExecuterException("Els expedients de tipus "+type.toString()+" no disposen de consulta per cercar els documents associats");
			}
			query4Docs = query4Docs.replace(CescaUtil.REPLACE_NUMEXP, numExp);
			List<NodeRef> documents = this.searchForNodes(query4Docs);
			if(documents == null){
				setTracerMessage("L'expedient "+numExp+" no te o no s'han tobat documents associats");
			} else{
				setTracerMessage("L'expedient "+numExp+" te "+documents.size()+" documents associats");
				for (NodeRef docNode: documents) {
					String resultDoc = this.peticioDocumentConExp(idMexMid, docNode, null, idConfiguracio);
					System.out.println("Response doc: " + resultDoc);
				}
			}
		} else {
			//throw exception > end flow
			throw new ExecuterException("La peticio a iArxiu amb id: "+idMexMid+" per expedient "+numExp+" ha donat error: "+ result);
		}
			
	}
	
	private String peticioDocumentSinExp(String idMexMid, NodeRef docNode, String numDoc, String idConfiguracio) 
			throws RemoteException, ParameterException, MandatoryFieldException, XMLParseException, ExecuterException {

		System.out.println("peticion documentSinExp");
	
		//Con el nodo recuperamos su tipo y buscamos su configuracion activa
		QName type = this.getServiceRegistry().getNodeService().getType(docNode);
		
		System.out.println("type " + type.toPrefixString());

		NodeRef configuration = this.getDocumentSinExpCFG(idConfiguracio);
		if (configuration == null)
			throw new ExecuterException("No s'ha trobat l'arxiu de configuracio pel document amb tipus: "+type.toPrefixString());
		ContentReader reader = this.getServiceRegistry().getContentService().getReader(configuration, ContentModel.PROP_CONTENT);
		String xmlbase = reader.getContentString();
		//---
		
		//Recuperar les metadades del node
		Map<QName, Serializable> props = this.getServiceRegistry().getNodeService().getProperties(docNode);
		Map<String, Serializable> propsToBind = this.getProperties(props);
		XMLHelper helper = new XMLHelper(xmlbase);
		
		// Inicio personalización UDL: adaptar metadatos al modelo iArxiu
		UDLHelper UDLHelper = new es.cesca.alfresco.util.UDLHelper(serviceRegistry, propsToBind, null, docNode);
		propsToBind = UDLHelper.bindPropsToUDL("");
		// Fin 

		String metadata = helper.xmlToIArxiu(propsToBind, this.serviceRegistry, true);
		
		System.out.println("valor xml a enviar " + metadata);
		
		logger.debug("valor xml documet per enviar a WS " + metadata);
		if (!helper.isDoc()) {
			throw new ExecuterException("El node actual es un document pero no s'ha pogut localitzar la propietat que l'identifica com document");
		}
		//recuperar num de document - nomes els documents independents el passaran com a paramatre
		String numDoc_Temp = helper.getNumDoc();
		String idDocSignat = "";
		try{
			String RM_URI = "http://www.alfresco.org/model/recordsmanagement/1.0";
			numDoc = (String)this.getServiceRegistry().getNodeService().getProperty(docNode, ContentModel.PROP_NAME);
			idDocSignat = (String)this.getServiceRegistry().getNodeService().getProperty(docNode, QName.createQName(RM_URI, "id_document_signat"));
			System.out.println("idDocSignat: " + idDocSignat);
			
		}catch (Exception e) {
			numDoc = numDoc_Temp;
			// TODO: handle exception
		}
		//JMP
	//	if (numDoc != null && !numDoc.equals(numDoc_Temp) && !numDoc.equals("P-" + numDoc_Temp) && !("P-" + numDoc).equals(numDoc_Temp)) {
		//		throw new ExecuterException("El node actual es un document independent pero el nom del document guardat a la peticio i el dl node no coincideixen: "+numDoc_Temp+" - "+numDoc);
		//	} else {
	//numDoc = numDoc_Temp;
	//}
		helper = null;//Helper will not be required anymore > break reference >> GC
		setTracerMessage("La peticio a iArxiu per document "+numDoc+" amb dades: "+metadata);
		
		try {
			metadata = org.apache.axis.encoding.Base64.encode(metadata.getBytes("UTF-8"));
		} catch(UnsupportedEncodingException e) {
			System.out.println("Error al codificar les metadades a UTF-8");
			throw new ExecuterException("No s'han pogut codificar correctament les metadades a UTF-8");
		}
		
		
		//Recuperar el contingut del node
		String document = this.encodeBase64ToString(docNode);
		
		// Peticions de document (una pel document)
		System.out.println("peticio document a WS " + numDoc + "idMexMid " + idMexMid);
		String result = client.peticioDocument(idMexMid, metadata, numDoc, document);
		System.out.println("result peticion document " + result);
		if (this.isPeticioOK(result)) {//Aquesta peticio si va be torna un ID-DOC que s'ha d'utilitzar amb les signatures
			setTracerMessage("La peticio a iArxiu per document ha resultat correcta");			
			//Procedir amb les signatures
			String query4Sig = (String)this.getServiceRegistry().getNodeService().getProperty(configuration, CescaUtil.CFG_DOC_PROP_CRITERI_CERCA_SIG);
			System.out.println("query sign " + query4Sig);
			if (!CescaUtil.isEmpty(query4Sig)) {
				//throw exception > not a valid query or empty
				//throw new ExecuterException("El document de tipus "+type.toString()+" no disposen de consulta per cercar les signatures associades");
				System.out.println("reemplanzando " + numDoc);
				//query4Sig = query4Sig.replace(CescaUtil.REPLACE_NUMDOC, numDoc);
				// Personalización UdL
				if(!"".equalsIgnoreCase(idDocSignat) && idDocSignat != null) {
					query4Sig = query4Sig.replace(CescaUtil.REPLACE_NUMDOC, idDocSignat);					
				}
				List<NodeRef> signas = this.searchForNodes(query4Sig);
				if(signas == null){
					setTracerMessage("El document "+numDoc+" no te o no s'han tobat signatures associades");
				} else {
					setTracerMessage("El document "+numDoc+" te "+signas.size()+" signatures associades");
					String idDoc = result;
					for (NodeRef sigNode: signas) {
						this.peticioSignaturaSinExp(idMexMid, idDoc, sigNode, idConfiguracio);//Se pasa un idDOc creado por iArxiu no el original
					}
				}
			}
			
			//AQUUUUUUUUUUUUUUUUUUUUUU
			//query4Sig = query4Sig.replace(CescaUtil.REPLACE_NUMDOC, helper.getNumDoc());
			return result;
		} else {
			//throw exception no ha anat be
			throw new ExecuterException("La peticio a iArxiu amb id: "+idMexMid+" per document "+numDoc+" ha donat error: "+ result);
		}
	}

	private String peticioDocumentConExp(String idMexMid, NodeRef docNode, String numDoc, String idConfiguracio) 
			throws RemoteException, ParameterException, MandatoryFieldException, XMLParseException, ExecuterException {

		System.out.println("peticioDocumentConExp");

		//Con el nodo recuperamos su tipo y buscamos su configuracion activa
		QName type = this.getServiceRegistry().getNodeService().getType(docNode);
		
		System.out.println("type " + type.toPrefixString());
		NodeRef configuration = this.getDocumentConExpCFG(idConfiguracio);
		if (configuration == null)
			throw new ExecuterException("No s'ha trobat l'arxiu de configuracio pel document amb tipus: "+type.toPrefixString());
		ContentReader reader = this.getServiceRegistry().getContentService().getReader(configuration, ContentModel.PROP_CONTENT);
		String xmlbase = reader.getContentString();
		//---
		
		//Recuperar les metadades del node
		Map<QName, Serializable> props = this.getServiceRegistry().getNodeService().getProperties(docNode);
		Map<String, Serializable> propsToBind = this.getProperties(props);
		XMLHelper helper = new XMLHelper(xmlbase);

		// Inicio personalización UDL: adaptar metadatos al modelo iArxiu
		UDLHelper UDLHelper = new es.cesca.alfresco.util.UDLHelper(serviceRegistry, propsToBind, null, docNode);
		propsToBind = UDLHelper.bindPropsToUDL("document");
		// Fin 

		String metadata = helper.xmlToIArxiu(propsToBind, this.serviceRegistry, true);
		
		System.out.println("valor xml a enviar " + metadata);
		
		logger.debug("valor xml documet per enviar a WS " + metadata);
		if (!helper.isDoc()) {
			throw new ExecuterException("El node actual es un document pero no s'ha pogut localitzar la propietat que l'identifica com document");
		}
		//recuperar num de document - nomes els documents independents el passaran com a paramatre
		String numDoc_Temp = helper.getNumDoc();
		String idDocSignat = "";
		List<AssociationRef> assocs = new ArrayList<AssociationRef>();
		try{
			//String RM_URI = "http://www.alfresco.org/model/recordsmanagement/1.0";
			String CM_URI = "http://www.alfresco.org/model/content/1.0";
			numDoc = (String)this.getServiceRegistry().getNodeService().getProperty(docNode, ContentModel.PROP_NAME);
			assocs = serviceRegistry.getNodeService().getTargetAssocs(docNode, QName.createQName(CM_URI, "signaturesDocumentRm"));
			//idDocSignat = (String)this.getServiceRegistry().getNodeService().getProperty(docNode, QName.createQName(RM_URI, "secuencial_identificador_documentSimple"));
			//idDocSignat = (String)this.getServiceRegistry().getNodeService().getProperty(docNode, QName.createQName(RM_URI, "id_document_signat"));			
			System.out.println("idDocSignat: " + idDocSignat);
			
		}catch (Exception e) {
			numDoc = numDoc_Temp;
			// TODO: handle exception
		}
		//JMP
	//	if (numDoc != null && !numDoc.equals(numDoc_Temp) && !numDoc.equals("P-" + numDoc_Temp) && !("P-" + numDoc).equals(numDoc_Temp)) {
		//		throw new ExecuterException("El node actual es un document independent pero el nom del document guardat a la peticio i el dl node no coincideixen: "+numDoc_Temp+" - "+numDoc);
		//	} else {
	//numDoc = numDoc_Temp;
	//}
		helper = null;//Helper will not be required anymore > break reference >> GC
		setTracerMessage("La peticio a iArxiu per document "+numDoc+" amb dades: "+metadata);
		
		try {
			metadata = org.apache.axis.encoding.Base64.encode(metadata.getBytes("UTF-8"));
		} catch(UnsupportedEncodingException e) {
			System.out.println("Error al codificar les metadades a UTF-8");
			throw new ExecuterException("No s'han pogut codificar correctament les metadades a UTF-8");
		}
		
		
		//Recuperar el contingut del node
		String document = this.encodeBase64ToString(docNode);
		
		// Peticions de document (una pel document)
		System.out.println("peticio document a WS " + numDoc + "idMexMid " + idMexMid);
		String result = client.peticioDocument(idMexMid, metadata, numDoc, document);
		System.out.println("result peticion document " + result);
		if (result != null && this.isPeticioOK(result)) { //Aquesta peticio si va be torna un ID-DOC que s'ha d'utilitzar amb les signatures
			setTracerMessage("La peticio a iArxiu per document ha resultat correcta");			
			//Procedir amb les signatures
			String query4Sig = (String)this.getServiceRegistry().getNodeService().getProperty(configuration, CescaUtil.CFG_DOC_PROP_CRITERI_CERCA_SIG);
			System.out.println("query signatures: " + query4Sig);
			if (!CescaUtil.isEmpty(query4Sig)) {
				//throw exception > not a valid query or empty
				//throw new ExecuterException("El document de tipus "+type.toString()+" no disposen de consulta per cercar les signatures associades");
				System.out.println("reemplanzando " + numDoc);
				//query4Sig = query4Sig.replace(CescaUtil.REPLACE_NUMDOC, numDoc);
				//Personalización UdL
				if(!"".equalsIgnoreCase(idDocSignat) && idDocSignat != null) {
					query4Sig = query4Sig.replace(CescaUtil.REPLACE_NUMDOC, idDocSignat);
				}
				//List<NodeRef> signas = this.searchForNodes(query4Sig);
				List<NodeRef> signas = new ArrayList<NodeRef>();
				Iterator<AssociationRef> it = assocs.iterator();
				while(it.hasNext()) {
					AssociationRef assoc = (AssociationRef)it.next();
					NodeRef signaturaRef = assoc.getTargetRef();
					signas.add(signaturaRef);
				}
				
				if(signas == null){
					setTracerMessage("El document "+numDoc+" no te o no s'han tobat signatures associades");
				} else {
					setTracerMessage("El document "+numDoc+" te "+signas.size()+" signatures associades");
					String idDoc = result;
					for (NodeRef sigNode: signas) {
						this.peticioSignaturaConExp(idMexMid, idDoc, sigNode, idConfiguracio);//Se pasa un idDOc creado por iArxiu no el original
					}
				}
			}
			
			//query4Sig = query4Sig.replace(CescaUtil.REPLACE_NUMDOC, helper.getNumDoc());
			return result;
		} else {
			//throw exception no ha anat be
			throw new ExecuterException("La peticio a iArxiu amb id: "+idMexMid+" per document "+numDoc+" ha donat error: "+ result);
		}
	}
	
	private void peticioSignaturaConExp(String idMexMid, String idDoc, NodeRef signNode, String idConfiguracio) 
			throws RemoteException, ParameterException, MandatoryFieldException, XMLParseException, ExecuterException{
		//Con el nodo recuperamos su tipo y buscamos su configuracion activa
		System.out.println("peticions signatura con exp");
		
		QName type = this.getServiceRegistry().getNodeService().getType(signNode);
		NodeRef configuration = this.getSignaturaConExpCFG(idConfiguracio);
		if (configuration == null)
			throw new ExecuterException("No s'ha trobat l'arxiu de configuracio per la signatura amb tipus: "+type.toPrefixString());
		ContentReader reader = this.getServiceRegistry().getContentService().getReader(configuration, ContentModel.PROP_CONTENT);
		String xmlbase = reader.getContentString();
		//---
		
		//Recupera metadades
		Map<QName, Serializable> props = this.getServiceRegistry().getNodeService().getProperties(signNode);
		Map<String, Serializable> propsToBind = this.getProperties(props);
		XMLHelper helper = new XMLHelper(xmlbase);
		
		// Inicio personalización UDL: adaptar metadatos al modelo iArxiu
		//UDLHelper UDLHelper = new es.cesca.alfresco.util.UDLHelper(serviceRegistry, propsToBind, null, null);
		//propsToBind = UDLHelper.bindPropsToUDL("signatura");
		// Fin 

		String metadata = helper.xmlToIArxiu(propsToBind, this.serviceRegistry, true);
		logger.debug("valor xml signatura per enviar a WS " + metadata);
		if (!helper.isSignature()) {

			throw new ExecuterException("El node actual es una signatura pero no s'ha pogut localitzar la propietat que l'identifica com signatura");
		}
		//recupera el nomSignatura
		String nomSignatura = helper.getIdSignatura();
		setTracerMessage("La peticio a iArxiu per signatura "+nomSignatura+" amb dades: "+metadata);
		
		try {
			metadata = org.apache.axis.encoding.Base64.encode(metadata.getBytes("UTF-8"));
		} catch(UnsupportedEncodingException e) {
			System.out.println("Error al codificar les metadades a UTF-8");
			throw new ExecuterException("No s'han pogut codificar correctament les metadades a UTF-8");
		}
		
		
		helper = null;
		
		//Recuperar el contingut del node
		String signatura = this.encodeBase64ToString(signNode);
		String result = client.peticioSignatura(idMexMid, idDoc, metadata, nomSignatura, signatura);
		if (this.isPeticioOK(result)) {
			//nothing to do >> life's good
			setTracerMessage("La signatura "+nomSignatura+" associada al document "+idDoc+" s'ha enviat correctament (id exp: "+idMexMid+")");
		} else {
			//throw exception no ha anat be, despres de tant bucle
			throw new ExecuterException("La peticio a iArxiu amb id: "+idMexMid+" per la signatura "+nomSignatura+" del document "+idDoc+" ha donat error: "+ result);
		}
	}
	
	private void peticioSignaturaSinExp(String idMexMid, String idDoc, NodeRef signNode, String idConfiguracio) 
			throws RemoteException, ParameterException, MandatoryFieldException, XMLParseException, ExecuterException{
		//Con el nodo recuperamos su tipo y buscamos su configuracion activa
		QName type = this.getServiceRegistry().getNodeService().getType(signNode);
		NodeRef configuration = this.getSignaturaSinExpCFG(idConfiguracio);
		if (configuration == null)
			throw new ExecuterException("No s'ha trobat l'arxiu de configuracio per la signatura amb tipus: "+type.toPrefixString());
		ContentReader reader = this.getServiceRegistry().getContentService().getReader(configuration, ContentModel.PROP_CONTENT);
		String xmlbase = reader.getContentString();
		//---
		
		//Recupera metadades
		Map<QName, Serializable> props = this.getServiceRegistry().getNodeService().getProperties(signNode);
		Map<String, Serializable> propsToBind = this.getProperties(props);
		XMLHelper helper = new XMLHelper(xmlbase);

		// Inicio personalización UDL: adaptar metadatos al modelo iArxiu
		UDLHelper UDLHelper = new es.cesca.alfresco.util.UDLHelper(serviceRegistry, propsToBind, null, null);
		propsToBind = UDLHelper.bindPropsToUDL("");
		// Fin 
		
		String metadata = helper.xmlToIArxiu(propsToBind, this.serviceRegistry, true);
		
		logger.debug("valor xml signatura per enviar a WS " + metadata);
		if (!helper.isSignature()) {

			throw new ExecuterException("El node actual es una signatura pero no s'ha pogut localitzar la propietat que l'identifica com signatura");
		}
		//recupera el nomSignatura
		String nomSignatura = helper.getIdSignatura();
		setTracerMessage("La peticio a iArxiu per signatura "+nomSignatura+" amb dades: "+metadata);
		
		try {
			metadata = org.apache.axis.encoding.Base64.encode(metadata.getBytes("UTF-8"));
		} catch(UnsupportedEncodingException e) {
			System.out.println("Error al codificar les metadades a UTF-8");
			throw new ExecuterException("No s'han pogut codificar correctament les metadades a UTF-8");
		}
		
		helper = null;
		
		//Recuperar el contingut del node
		String signatura = this.encodeBase64ToString(signNode);
		String result = client.peticioSignatura(idMexMid, idDoc, metadata, nomSignatura, signatura);
		if (this.isPeticioOK(result)) {
			//nothing to do >> life's good
			setTracerMessage("La signatura "+nomSignatura+" associada al document "+idDoc+" s'ha enviat correctament (id exp: "+idMexMid+")");
		} else {
			//throw exception no ha anat be, despres de tant bucle
			throw new ExecuterException("La peticio a iArxiu amb id: "+idMexMid+" per la signatura "+nomSignatura+" del document "+idDoc+" ha donat error: "+ result);
		}
	}
	
	private void updatePeticioToEnTramit(NodeRef node, boolean isExpedient, String id_exp, String id_doc_middleware) {
		NodeService nodeService = getServiceRegistry().getNodeService();
		nodeService.setProperty(node, CescaUtil.PETICIO_PROP_ESTAT, CescaUtil.STATUS_ENTRAMITACIO);
		
		if(isExpedient) {
			//Aquest camp no el tindrem fins que s'obri la peticio a iArxiu
			setTracerMessage("Node en tramitacio correctament: "+ node.getId() +" Per expedient amb id_exp: "+id_exp);
			nodeService.setProperty(node, CescaUtil.PETICIO_PROP_ID_EXP, id_exp);
			
			// Se marca el expediente como en tramitación de envio a iArxiu
			// Se obtiene el campo id_peticion que coincide con el nodeRef del expediente
			String idPeticion = (String)nodeService.getProperty(node, CescaUtil.PETICIO_PROP_ID);
			NodeRef expNodeRef = new NodeRef(Repository.getStoreRef(), idPeticion);
			nodeService.setProperty(expNodeRef, ConstantsUdL.UDL_PETICIO_PROP_ESTAT, CescaUtil.STATUS_ENTRAMITACIO);
			
		} else{
			setTracerMessage("Node en tramitacio correctament: "+ node.getId() +" per al document independent");
			nodeService.setProperty(node, CescaUtil.PETICIO_PROP_NUM_DOC, id_doc_middleware);
		}
	}
	
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> arg0) {
		//Nothing to do
	}

}