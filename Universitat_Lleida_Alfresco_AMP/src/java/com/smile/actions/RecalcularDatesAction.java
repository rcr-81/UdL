package com.smile.actions;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.UdlProperties;

public class RecalcularDatesAction extends ActionExecuterAbstractBase implements ConstantsUdL{

	private QName nom = QName.createQName(CM_URI, "name");
	
	private QName dataIniDocumentRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_inici_documentSimple");
	private QName dataFiDocumentRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_fi_documentSimple");
	private QName dataIniExpedientRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_inici_expedient");
	private QName dataFiExpedientRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_fi_expedient");
	private QName dataIniAgregacioRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_inici_agregacio");
	private QName dataFiAgregacioRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_fi_agregacio");
	private QName dataIniSerieRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_inici_serie");
	private QName dataFiSerieRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_fi_serie");
	private QName dataIniFonsRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_inici_fons");
	private QName dataFiFonsRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_fi_fons");
	private QName documentSimple = QName.createQName("http://www.smile.com/model/udlrm/1.0", "documentSimple");
	private QName expedient = QName.createQName("http://www.smile.com/model/udlrm/1.0", "expedient");
	private QName agregacio = QName.createQName("http://www.smile.com/model/udlrm/1.0", "agregacio");

	private ServiceRegistry serviceRegistry;
		
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		String usernameAuth = authenticate();
		NodeService nodeService = serviceRegistry.getNodeService();
		String nomDocSimple = (String)nodeService.getProperty(nodeRef, nom);
		
		if(nodeService.hasAspect(nodeRef, documentSimple) && !nomDocSimple.equalsIgnoreCase(INDEX)) {			
			NodeRef expAgrNodeRef = nodeService.getPrimaryParent(nodeRef).getParentRef();
			NodeRef serieNodeRef = nodeService.getPrimaryParent(expAgrNodeRef).getParentRef();
			NodeRef fonsNodeRef = nodeService.getPrimaryParent(serieNodeRef).getParentRef();
			
			if(nodeService.hasAspect(expAgrNodeRef, expedient)) {
				updateExpedient(nodeService, nodeRef, expAgrNodeRef, nomDocSimple);
				
			}else if(nodeService.hasAspect(expAgrNodeRef, agregacio)) {
				updateAgregacio(nodeService, nodeRef, expAgrNodeRef, nomDocSimple);
			}
			
			updateSerie(nodeService, expAgrNodeRef, serieNodeRef, nomDocSimple);
			updateFons(nodeService, serieNodeRef, fonsNodeRef, nomDocSimple);
		}

		AuthenticationUtil.setRunAsUser(usernameAuth);
		AuthenticationUtil.setFullyAuthenticatedUser(usernameAuth);
	}

	/**
	 * S'autentica como administrador i retorna l'usuari que ha originat l'execució de l'acció. 
	 * 
	 * @return
	 */
	private String authenticate() {
		String usernameAuth = serviceRegistry.getAuthenticationService().getCurrentUserName();
		UdlProperties props = new UdlProperties();
		try {
			serviceRegistry.getAuthenticationService().authenticate(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD).toCharArray());

		}catch (Exception e) {			
			e.printStackTrace();
		}
		
		return usernameAuth;
	}
	
	/**
	 * Actualitza les dates de l'expedient si és necessari.
	 * 
	 * @param nodeRef
	 * @param parentNodeRef
	 */
	private void updateExpedient(NodeService nodeService, NodeRef docNodeRef, NodeRef expNodeRef, String nomDocSimple) {
		//System.out.println(DateFormat.getInstance().format(new Date()) + " START: Recalcular dates expedient.");
		Date docDataInici = (Date)nodeService.getProperties(docNodeRef).get(dataIniDocumentRM);
		Date docDataFi = (Date)nodeService.getProperties(docNodeRef).get(dataFiDocumentRM);
		Date expDataInici = (Date)nodeService.getProperties(expNodeRef).get(dataIniExpedientRM);
		Date expDataFi = (Date)nodeService.getProperties(expNodeRef).get(dataFiExpedientRM);

		if(expDataInici == null || "".equals(expDataInici) || expDataFi == null || "".equals(expDataFi)) {
			if(expDataInici == null || "".equals(expDataInici)) {
				nodeService.setProperty(expNodeRef, dataIniExpedientRM, docDataInici);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data inici expedient: " + expNodeRef);
			}
	
			if(expDataFi == null || "".equals(expDataFi)) {
				nodeService.setProperty(expNodeRef, dataFiExpedientRM, docDataFi);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data fi expedient: " + expNodeRef);
			}

		}else {
			// Si la data inici del document és anterior a la de l'expedient => s'actualitza la data inici de l'expedient 
			if(docDataInici != null && docDataInici.before(expDataInici)) {
				nodeService.setProperty(expNodeRef, dataIniExpedientRM, docDataInici);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data inici expedient: " + expNodeRef);
				
				// Si la data d'inici del document és posterior a la de l'expedient => cal revisar tots els documents de l'expedient
			}else if(docDataInici != null && docDataInici.after(expDataInici)) {
				Date firstData = getDataFirstDoc(nodeService, expNodeRef);
				nodeService.setProperty(expNodeRef, dataIniExpedientRM, firstData);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data inici expedient: " + expNodeRef);
			}
			
			// Si la data fi del document és posterior a la de l'expedient => s'actualitza la data fi de l'expedient
			if(docDataFi != null && docDataFi.after(expDataFi)) {
				nodeService.setProperty(expNodeRef, dataFiExpedientRM, docDataFi);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data fi expedient: " + expNodeRef);
	
				// Si la data fi del document és anterior a la de l'expedient => cal revisar tots els documents de l'expedient
			}else if(docDataFi != null && docDataFi.before(expDataFi)) {
				Date lastData = getDataLastDoc(nodeService, expNodeRef);
				nodeService.setProperty(expNodeRef, dataFiExpedientRM, lastData);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data fi expedient: " + expNodeRef);
			}
		}
		
		//System.out.println(DateFormat.getInstance().format(new Date()) + " END: Recalcular dates expedient.");
	}
	
	/**
	 * Actualitza les dates de l'agregació si és necessari.
	 * 
	 * @param nodeRef
	 * @param parentNodeRef
	 */
	private void updateAgregacio(NodeService nodeService, NodeRef docNodeRef, NodeRef agrNodeRef, String nomDocSimple) {
		//System.out.println(DateFormat.getInstance().format(new Date()) + " START: Recalcular dates agregació.");
		Date docDataInici = (Date)nodeService.getProperties(docNodeRef).get(dataIniDocumentRM);
		Date docDataFi = (Date)nodeService.getProperties(docNodeRef).get(dataFiDocumentRM);
		Date agrDataInici = (Date)nodeService.getProperties(agrNodeRef).get(dataIniAgregacioRM);
		Date agrDataFi = (Date)nodeService.getProperties(agrNodeRef).get(dataFiAgregacioRM);

		if(agrDataInici == null || "".equals(agrDataInici) || agrDataFi == null || "".equals(agrDataFi)) {
			if(agrDataInici == null || "".equals(agrDataInici)) {
				nodeService.setProperty(agrNodeRef, dataIniAgregacioRM, docDataInici);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data inici agregació: " + agrNodeRef);
			}
	
			if(agrDataFi == null || "".equals(agrDataFi)) {
				nodeService.setProperty(agrNodeRef, dataFiAgregacioRM, docDataFi);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data fi agregació: " + agrNodeRef);
			}

		}else {
			if(docDataInici != null && docDataInici.before(agrDataInici)) {
				nodeService.setProperty(agrNodeRef, dataIniAgregacioRM, docDataInici);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data inici agregació: " + agrNodeRef);
	
			}else if(docDataInici != null && docDataInici.after(agrDataInici)) {
				Date firstData = getDataFirstDoc(nodeService, agrNodeRef);
				nodeService.setProperty(agrNodeRef, dataIniAgregacioRM, firstData);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data inici agregació: " + agrNodeRef);
			}
			
			if(docDataFi != null && docDataFi.after(agrDataFi)) {
				nodeService.setProperty(agrNodeRef, dataFiAgregacioRM, docDataFi);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data fi agregació: " + agrNodeRef);
	
			}else if(docDataFi != null && docDataFi.before(agrDataFi)) {
				Date lastData = getDataLastDoc(nodeService, agrNodeRef);
				nodeService.setProperty(agrNodeRef, dataFiAgregacioRM, lastData);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data fi agregació: " + agrNodeRef);
			}
		}
		
		//System.out.println(DateFormat.getInstance().format(new Date()) + " END: Recalcular dates agregació.");
	}
	
	/**
	 * Actualitza les dates de la sèrie si es necessari.
	 * 
	 * @param nodeRef
	 * @param parentNodeRef
	 */
	private void updateSerie(NodeService nodeService, NodeRef nodeRef, NodeRef serieNodeRef, String nomDocSimple) {
		//System.out.println(DateFormat.getInstance().format(new Date()) + " START: Recalcular dates sèrie.");
		Date serieDataInici = (Date)nodeService.getProperties(serieNodeRef).get(dataIniSerieRM);
		Date serieDataFi = (Date)nodeService.getProperties(serieNodeRef).get(dataFiSerieRM);
		Date dataInici = null;
		Date dataFi = null;
		
		if(nodeService.hasAspect(nodeRef, expedient)) {
			dataInici = (Date)nodeService.getProperties(nodeRef).get(dataIniExpedientRM);
			dataFi = (Date)nodeService.getProperties(nodeRef).get(dataFiExpedientRM);
			
		}else if(nodeService.hasAspect(nodeRef, agregacio)) {
			dataInici = (Date)nodeService.getProperties(nodeRef).get(dataIniAgregacioRM);
			dataFi = (Date)nodeService.getProperties(nodeRef).get(dataFiAgregacioRM);
		}
		
		if(dataInici != null && !"".equals(dataInici)) {
			if(serieDataInici == null || "".equals(serieDataInici)) {
				nodeService.setProperty(serieNodeRef, dataIniSerieRM, dataInici);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data inici sèrie: " + serieNodeRef);
				
			}else {
				if(dataInici.before(serieDataInici)) {
					nodeService.setProperty(serieNodeRef, dataIniSerieRM, dataInici);
					Date now = new Date();
					System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data inici sèrie: " + serieNodeRef);
		
				}else if(dataInici.after(serieDataInici)){
					Date firstData = getDataFirstExp(nodeService, serieNodeRef);
					nodeService.setProperty(serieNodeRef, dataIniSerieRM, firstData);
					Date now = new Date();
					System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data inici sèrie: " + serieNodeRef);
				}
			}
		}
		
		if(dataFi != null && !"".equals(dataFi)) {
			if(serieDataFi == null || "".equals(serieDataFi)) {
				nodeService.setProperty(serieNodeRef, dataFiSerieRM, dataFi);
				Date now = new Date();
				System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data fi sèrie: " + serieNodeRef);
				
			}else {
				if(dataFi.after(serieDataFi)) {
					nodeService.setProperty(serieNodeRef, dataFiSerieRM, dataFi);
					Date now = new Date();
					System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data fi sèrie: " + serieNodeRef);
		
				}else if(dataFi.before(serieDataFi)) {
					Date lastData =  getDataLastExp(nodeService, serieNodeRef);
					nodeService.setProperty(serieNodeRef, dataFiSerieRM, lastData);
					Date now = new Date();
					System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data fi sèrie: " + serieNodeRef);
				}
			}
		}
		
		//System.out.println(DateFormat.getInstance().format(new Date()) + " END: Recalcular dates sèrie.");
	}
	
	/**
	 * Actualitza les dates del fons si és necessari.
	 * 
	 * @param nodeRef
	 * @param parentNodeRef
	 */
	private void updateFons(NodeService nodeService, NodeRef serieNodeRef, NodeRef fonsNodeRef, String nomDocSimple) {
		//System.out.println(DateFormat.getInstance().format(new Date()) + " START: Recalcular dates fons.");
		Date serieDataInici = (Date)nodeService.getProperties(serieNodeRef).get(dataIniSerieRM);
		Date serieDataFi = (Date)nodeService.getProperties(serieNodeRef).get(dataFiSerieRM);
		Date fonsDataInici = (Date)nodeService.getProperties(fonsNodeRef).get(dataIniFonsRM);
		Date fonsDataFi = (Date)nodeService.getProperties(fonsNodeRef).get(dataFiFonsRM);
		
		if(serieDataInici.before(fonsDataInici)) {
			nodeService.setProperty(fonsNodeRef, dataIniFonsRM, serieDataInici);
			Date now = new Date();
			System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data inici fons: " + fonsNodeRef);

		}else if(serieDataInici.after(fonsDataInici)) {
			Date firstData = getDataFirstSerie(nodeService, fonsNodeRef);
			nodeService.setProperty(fonsNodeRef, dataIniFonsRM, firstData);
			Date now = new Date();
			System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data inici fons: " + fonsNodeRef);
		}
		
		if(serieDataFi.after(fonsDataFi)) {
			nodeService.setProperty(fonsNodeRef, dataFiFonsRM, serieDataFi);
			Date now = new Date();
			System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data fi fons: " + fonsNodeRef);

		}else if(serieDataFi.before(fonsDataFi)) {
			Date lastData = getDataLastSerie(nodeService, fonsNodeRef);
			nodeService.setProperty(fonsNodeRef, dataFiFonsRM, lastData);
			Date now = new Date();
			System.out.println(DateFormat.getInstance().format(now) + " Doc. Simple: " + nomDocSimple + " Update data fi fons: " + fonsNodeRef);
		}
		
		//System.out.println(DateFormat.getInstance().format(new Date()) + " END: Recalcular dates fons.");
	}

	/**
	 * Retorna la mínima data inici dels documents que hi ha a l'expedient/agregació rebut com a paràmetre.
	 * 
	 * @param nodeRef
	 * @return
	 */
	private Date getDataFirstDoc(NodeService nodeService, NodeRef nodeRef) {
		Date result = null;
		Date aux = null;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);
		
		for (ChildAssociationRef childAssoc : children) {
			NodeRef childNodeRef = childAssoc.getChildRef();
			
			if(nodeService.hasAspect(childNodeRef, documentSimple)) {
				aux = (Date)nodeService.getProperty(childNodeRef, dataIniDocumentRM);
				
				if(result == null) {
					result = aux;
	
				}else if(aux != null && aux.before(result)) {
					result = aux;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Retorna la màxima data fi dels documents que hi ha a l'expedient/agregació rebut com a paràmetre.
	 * 
	 * @param nodeRef
	 * @return
	 */
	private Date getDataLastDoc(NodeService nodeService, NodeRef nodeRef) {
		Date result = null;
		Date aux = null;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);
		
		for (ChildAssociationRef childAssoc : children) {
			NodeRef childNodeRef = childAssoc.getChildRef();

			if(nodeService.hasAspect(childNodeRef, documentSimple)) {
				aux = (Date)nodeService.getProperty(childNodeRef, dataFiDocumentRM);
				
				if(result == null) {
					result = aux;
	
				}else if(aux != null && aux.after(result)) {
					result = aux;
				}
			}
		}
		
		return result;
	}

	/**
	 * Retorna la mínima data inici dels expedients/agregacions que hi ha a la sèrie rebuda com a paràmetre.
	 * 
	 * @param serieNodeRef
	 * @return
	 */
	private Date getDataFirstExp(NodeService nodeService, NodeRef serieNodeRef) {
		Date result = null;
		Date aux = null;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(serieNodeRef);
		
		for (ChildAssociationRef childAssoc : children) {
			NodeRef childNodeRef = childAssoc.getChildRef();
			
			if(nodeService.hasAspect(childNodeRef, expedient)) {
				aux = (Date)nodeService.getProperty(childNodeRef, dataIniExpedientRM);
				
			}else if(nodeService.hasAspect(childNodeRef, agregacio)) {
				aux = (Date)nodeService.getProperty(childNodeRef, dataIniAgregacioRM);
			}
			
			if(result == null) {
				result = aux;

			}else if(aux != null && aux.before(result)) {
				result = aux;				
			}
		}
		
		return result;
	}
	
	/**
	 * Retorna la màxima data fi dels expedients/agregacions que hi ha a la sèrie rebuda com a paràmetre.
	 * 
	 * @param serieNodeRef
	 * @return
	 */
	private Date getDataLastExp(NodeService nodeService, NodeRef serieNodeRef) {
		Date result = null;
		Date aux = null;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(serieNodeRef);
		
		for (ChildAssociationRef childAssoc : children) {
			NodeRef childNodeRef = childAssoc.getChildRef();
			
			if(nodeService.hasAspect(childNodeRef, expedient)) {
				aux = (Date)nodeService.getProperty(childNodeRef, dataFiExpedientRM);
				
			}else if(nodeService.hasAspect(childNodeRef, agregacio)) {
				aux = (Date)nodeService.getProperty(childNodeRef, dataFiAgregacioRM);
			}
			
			if(result == null) {
				result = aux;

			}else if(aux != null && aux.after(result)) {
				result = aux;
			}
		}
		
		return result;
	}

	/**
	 * Retorna la mínima data inici de les sèries que hi ha al fons rebut com a paràmetre.
	 * 
	 * @param fonsNodeRef
	 * @return
	 */
	private Date getDataFirstSerie(NodeService nodeService, NodeRef fonsNodeRef) {
		Date result = null;
		Date aux = null;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(fonsNodeRef);
		
		for (ChildAssociationRef childAssoc : children) {
			NodeRef childNodeRef = childAssoc.getChildRef();
			aux = (Date)nodeService.getProperty(childNodeRef, dataIniSerieRM);
			
			if(result == null) {
				result = aux;
				
			}else if(aux != null && aux.before(result)) {
				result = aux;
			}
		}
		
		return result;
	}
	
	/**
	 * Retorna la màxima data fi de les sèries que hi ha al fons rebut com a paràmetre.
	 * 
	 * @param fonsNodeRef
	 * @return
	 */
	private Date getDataLastSerie(NodeService nodeService, NodeRef fonsNodeRef) {
		Date result = null;
		Date aux = null;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(fonsNodeRef);
		
		for (ChildAssociationRef childAssoc : children) {
			NodeRef childNodeRef = childAssoc.getChildRef();
			aux = (Date)nodeService.getProperty(childNodeRef, dataFiSerieRM);
			
			if(result == null) {
				result = aux;

			}else if(aux != null && aux.after(result)) {
				result = aux;
			}
		}
		
		return result;
	}

	public void runRecalcularDates(String sNodeRef, ServiceRegistry serviceRegistry){
		NodeRef nodeRef = new NodeRef(sNodeRef);
		setServiceRegistry(serviceRegistry);
		executeImpl(null, nodeRef);
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {}
}