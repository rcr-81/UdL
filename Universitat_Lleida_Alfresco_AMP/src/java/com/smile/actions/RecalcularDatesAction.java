package com.smile.actions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;

import com.smile.webscripts.helper.ConstantsUdL;

public class RecalcularDatesAction extends ActionExecuterAbstractBase implements ConstantsUdL{

	QName dataIniDocumentRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_inici_documentSimple");
	QName dataFiDocumentRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_fi_documentSimple");
	QName dataIniExpedientRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_inici_expedient");
	QName dataFiExpedientRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_fi_expedient");
	QName dataIniAgregacioRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_inici_agregacio");
	QName dataFiAgregacioRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_fi_agregacio");
	QName dataIniSerieRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_inici_serie");
	QName dataFiSerieRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_fi_serie");
	QName dataIniFonsRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_inici_fons");
	QName dataFiFonsRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_fi_fons");
	QName dataIniGrupFonsRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_inici_grupDeFons");
	QName dataFiGrupFonsRM = QName.createQName("http://www.smile.com/model/udlrm/1.0", "data_fi_grupDeFons");
	QName expedient = QName.createQName("http://www.smile.com/model/udlrm/1.0", "expedient");
	QName agregacio = QName.createQName("http://www.smile.com/model/udlrm/1.0", "agregacio");
	QName serie = QName.createQName("http://www.smile.com/model/udlrm/1.0", "serie");
	QName fons = QName.createQName("http://www.smile.com/model/udlrm/1.0", "fons");
	QName grupDeFons = QName.createQName("http://www.smile.com/model/udlrm/1.0", "grupDeFons");

	private ServiceRegistry serviceRegistry;
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		Date now = new Date();
		System.out.println(DateFormat.getInstance().format(now) + " START: Recalcular dates scheduled action with expedient/agregacio: " + nodeRef);		
		NodeService nodeService = serviceRegistry.getNodeService();
		if(nodeService.hasAspect(nodeRef, expedient)){			
			revisarFechas(nodeRef, nodeService, dataIniDocumentRM, dataFiDocumentRM, null, null, dataIniExpedientRM, dataFiExpedientRM);	
		}
		else if(nodeService.hasAspect(nodeRef, agregacio)){
			revisarFechas(nodeRef, nodeService, dataIniDocumentRM, dataFiDocumentRM, null, null, dataIniAgregacioRM, dataFiAgregacioRM);		
		}		
	}
	
	private void revisarFechas(NodeRef nodeRef, NodeService nodeService, QName dataIniToSearch, QName dataFiToSearch, 
			QName alternativeDataIniToSearch, QName alternativeDataFiToSearch, QName dataIniToUpdate, QName dataFiToUpdate){
		//get the oldest date ini from the contained documents				
		Date now = new Date();
		SearchParameters sp = new SearchParameters();
		sp.addStore(nodeRef.getStoreRef());
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery("PARENT:\"" + nodeRef.toString() + "\"");
		ResultSet results = null;
		List<Date> dateIniList = new ArrayList<Date>();
		List<Date> dateFiList = new ArrayList<Date>();
		try{
			results = serviceRegistry.getSearchService().query(sp);
			for(ResultSetRow row : results){
				NodeRef ref = row.getNodeRef();
				Date dataIni = (Date)nodeService.getProperties(ref).get(dataIniToSearch);
				Date dataFi = (Date)nodeService.getProperties(ref).get(dataFiToSearch);
				if(dataIni == null){
					dataIni = (Date)nodeService.getProperties(ref).get(alternativeDataIniToSearch);		
				}
				if(dataFi == null){
					dataFi = (Date)nodeService.getProperties(ref).get(alternativeDataFiToSearch);
				}
				if(dataIni != null){
					dateIniList.add(dataIni);
				}
				if(dataFi != null){
					dateFiList.add(dataFi);
				}
			}
			if(!dateIniList.isEmpty()){
				Collections.sort(dateIniList);
			}
			if(!dateFiList.isEmpty()){
				Collections.sort(dateFiList);
			}
			if(!dateIniList.isEmpty()){
				nodeService.setProperty(nodeRef, dataIniToUpdate, dateIniList.get(0));
			}
			if(!dateFiList.isEmpty()){
				nodeService.setProperty(nodeRef, dataFiToUpdate, dateFiList.get(dateFiList.size()-1));
			}
			System.out.println("Node: " + nodeRef + " updated.");
		}
		finally{
			if(results != null){
				results.close();
			}
		} 	

		if(!nodeService.hasAspect(nodeRef, fons)){
			recursiveRevision(nodeService.getPrimaryParent(nodeRef).getParentRef(), nodeService);
		}
		else {
			System.out.println(DateFormat.getInstance().format(now) + " END: Recalcular dates scheduled action.");
		}
	}

	private void recursiveRevision(NodeRef nodeRef, NodeService nodeService){
		Date now = new Date();
		if(nodeService.hasAspect(nodeRef, serie)){
			System.out.println(DateFormat.getInstance().format(now) + " START: Recalcular dates scheduled action with serie: " + nodeRef);
			revisarFechas(nodeRef, nodeService, dataIniExpedientRM, dataFiExpedientRM, dataIniAgregacioRM, dataFiAgregacioRM, dataIniSerieRM, dataFiSerieRM);			 	
		}
		else if(nodeService.hasAspect(nodeRef, fons)){
			System.out.println(DateFormat.getInstance().format(now) + " START: Recalcular dates scheduled action with fons: " + nodeRef);
			revisarFechas(nodeRef, nodeService, dataIniSerieRM, dataFiSerieRM, null, null, dataIniFonsRM, dataFiFonsRM);			
		}
		/*
		else if(nodeService.hasAspect(nodeRef, grupDeFons)){
			revisarFechas(nodeRef, nodeService, dataIniFonsRM, dataFiFonsRM, null, null, dataIniGrupFonsRM, dataFiGrupFonsRM);
		}	
		*/
	}
	
	public void runRecalcularDates(String sNodeRef, ServiceRegistry serviceRegistry){
		NodeRef nodeRef = new NodeRef(sNodeRef);
		setServiceRegistry(serviceRegistry);
		executeImpl(null, nodeRef);
	}

	

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		paramList.add(new ParameterDefinitionImpl("a-parameter", DataTypeDefinition.TEXT, false, getParamDisplayLabel("a-parameter")));      
	}
}