package com.smile.actions;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.UdlProperties;

public class HeretarMetadades extends ActionExecuterAbstractBase implements ConstantsUdL{

	private ServiceRegistry serviceRegistry;
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		String usernameAuth = serviceRegistry.getAuthenticationService().getCurrentUserName();
		UdlProperties props = new UdlProperties();
		try {
			serviceRegistry.getAuthenticationService().authenticate(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD).toCharArray());
		} 
		catch (Exception e) {			
			e.printStackTrace();
		}
		NodeService nodeService = serviceRegistry.getNodeService();
		NodeRef parent = nodeService.getPrimaryParent(nodeRef).getParentRef();
		if(parent != null){
			Map<QName, Serializable> metadata = nodeService.getProperties(parent);
			String type = nodeService.getType(parent).getLocalName();
			String codiClassificacio1 = "";
			String codiClassificacio2 = "";
			String denominacioClasse1 = "";
			String denominacioClasse2 = "";		
			String sensibilitatDades = "";
			String advertenciaSeguretat = "";
			String categoriaAdvertenciaSeguretat = "";
			String valoracio = "";
			String tipusDictamen1 = "";
			String tipusDictamen2 = "";
			String accioDictaminada1 = "";
			String accioDictaminada2 = "";

			QName codiClassificacioSerie = QName.createQName("http://www.smile.com/model/udl/1.0", "codi_classificacio_serie");		
			QName codiClassificacio1DocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "codi_classificacio_1_documentSimple");
			QName codiClassificacio2DocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "codi_classificacio_2_documentSimple");		
			QName codiClassificacio1Agregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "codi_classificacio_1_agregacio");
			QName codiClassificacio2Agregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "codi_classificacio_2_agregacio");		
			QName codiClassificacio1Expedient = QName.createQName("http://www.smile.com/model/udl/1.0", "codi_classificacio_1_expedient");			
			QName codiClassificacio2Expedient = QName.createQName("http://www.smile.com/model/udl/1.0", "codi_classificacio_2_expedient");		

			QName denominacioClasseSerie = QName.createQName("http://www.smile.com/model/udl/1.0", "denominacio_classe_serie");		
			QName denominacioClasse1DocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "denominacio_classe_1_documentSimple");
			QName denominacioClasse2DocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "denominacio_classe_2_documentSimple");		
			QName denominacioClasse1Agregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "denominacio_classe_1_agregacio");
			QName denominacioClasse2Agregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "denominacio_classe_2_agregacio");		
			QName denominacioClasse1Expedient = QName.createQName("http://www.smile.com/model/udl/1.0", "denominacio_classe_1_expedient");			
			QName denominacioClasse2Expedient = QName.createQName("http://www.smile.com/model/udl/1.0", "denominacio_classe_2_expedient");		

			QName sensibilitatDadesSerie = QName.createQName("http://www.smile.com/model/udl/1.0", "sensibilitat_dades_caracter_personal_serie");		
			QName sensibilitatDadesDocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "sensibilitat_dades_caracter_personal_documentSimple");
			QName sensibilitatDadesAgregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "sensibilitat_dades_caracter_personal_agregacio");
			QName sensibilitatDadesExpedient = QName.createQName("http://www.smile.com/model/udl/1.0", "sensibilitat_dades_caracter_personal_expedient");		

			QName advertenciaSeguretatSerie = QName.createQName("http://www.smile.com/model/udl/1.0", "advertencia_seguretat_serie");		
			QName advertenciaSeguretatDocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "advertencia_seguretat_documentSimple");
			QName advertenciaSeguretatAgregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "advertencia_seguretat_agregacio");
			QName advertenciaSeguretatExpedient = QName.createQName("http://www.smile.com/model/udl/1.0", "advertencia_seguretat_expedient");

			QName categoriaAdvertenciaSeguretatSerie = QName.createQName("http://www.smile.com/model/udl/1.0", "categoria_advertencia_seguretat_serie");		
			QName categoriaAdvertenciaSeguretatDocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "categoria_advertencia_seguretat_documentSimple");
			QName categoriaAdvertenciaSeguretatAgregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "categoria_advertencia_seguretat_agregacio");
			QName categoriaAdvertenciaSeguretatExpedient = QName.createQName("http://www.smile.com/model/udl/1.0", "categoria_advertencia_seguretat_expedient");

			QName valoracioSerie = QName.createQName("http://www.smile.com/model/udl/1.0", "valoracio_serie");		
			QName valoracioDocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "valoracio_documentSimple");
			QName valoracioAgregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "valoracio_agregacio");
			QName valoracioExpedient = QName.createQName("http://www.smile.com/model/udl/1.0", "valoracio_expedient");

			QName tipusDictamen1Serie = QName.createQName("http://www.smile.com/model/udl/1.0", "tipus_dictamen_1_serie");		
			QName tipusDictamen1DocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "tipus_dictamen_1_documentSimple");
			QName tipusDictamen1Agregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "tipus_dictamen_1_agregacio");
			QName tipusDictamen1Expedient = QName.createQName("http://www.smile.com/model/udl/1.0", "tipus_dictamen_1_expedient");

			QName tipusDictamen2Serie = QName.createQName("http://www.smile.com/model/udl/1.0", "tipus_dictamen_2_serie");		
			QName tipusDictamen2DocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "tipus_dictamen_2_documentSimple");
			QName tipusDictamen2Agregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "tipus_dictamen_2_agregacio");
			QName tipusDictamen2Expedient = QName.createQName("http://www.smile.com/model/udl/1.0", "tipus_dictamen_2_expedient");

			QName accioDictaminada1Serie = QName.createQName("http://www.smile.com/model/udl/1.0", "accio_dictaminada_1_serie");		
			QName accioDictaminada1DocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "accio_dictaminada_1_documentSimple");
			QName accioDictaminada1Agregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "accio_dictaminada_1_agregacio");
			QName accioDictaminada1Expedient = QName.createQName("http://www.smile.com/model/udl/1.0", "accio_dictaminada_1_expedient");

			QName accioDictaminada2Serie = QName.createQName("http://www.smile.com/model/udl/1.0", "accio_dictaminada_2_serie");		
			QName accioDictaminada2DocumentSimple = QName.createQName("http://www.smile.com/model/udl/1.0", "accio_dictaminada_2_documentSimple");
			QName accioDictaminada2Agregacio = QName.createQName("http://www.smile.com/model/udl/1.0", "accio_dictaminada_2_agregacio");
			QName accioDictaminada2Expedient = QName.createQName("http://www.smile.com/model/udl/1.0", "accio_dictaminada_2_expedient");

			if(type.equals("documentSimple")){
				//nada, el padre es documento, la regla no hace nada
			}
			else if(type.equals("agregacio")){
				codiClassificacio1 = (String)metadata.get(codiClassificacio1Agregacio);
				codiClassificacio2 = (String)metadata.get(codiClassificacio2Agregacio);			
				denominacioClasse1 = (String)metadata.get(denominacioClasse1Agregacio);
				denominacioClasse2 = (String)metadata.get(denominacioClasse2Agregacio);			
				sensibilitatDades = (String)metadata.get(sensibilitatDadesAgregacio);
				advertenciaSeguretat = (String)metadata.get(advertenciaSeguretatAgregacio);
				categoriaAdvertenciaSeguretat = (String)metadata.get(categoriaAdvertenciaSeguretatAgregacio);
				valoracio = (String)metadata.get(valoracioAgregacio);
				tipusDictamen1 = (String)metadata.get(tipusDictamen1Agregacio);
				tipusDictamen2 = (String)metadata.get(tipusDictamen2Agregacio);
				accioDictaminada1 = (String)metadata.get(accioDictaminada1Agregacio);
				accioDictaminada2 = (String)metadata.get(accioDictaminada2Agregacio);

				nodeService.setProperty(nodeRef, codiClassificacio1DocumentSimple, codiClassificacio1);
				nodeService.setProperty(nodeRef, codiClassificacio2DocumentSimple, codiClassificacio2);
				nodeService.setProperty(nodeRef, denominacioClasse1DocumentSimple, denominacioClasse1);
				nodeService.setProperty(nodeRef, denominacioClasse2DocumentSimple, denominacioClasse2);
				nodeService.setProperty(nodeRef, sensibilitatDadesDocumentSimple, sensibilitatDades);
				nodeService.setProperty(nodeRef, advertenciaSeguretatDocumentSimple, advertenciaSeguretat);
				nodeService.setProperty(nodeRef, categoriaAdvertenciaSeguretatDocumentSimple, categoriaAdvertenciaSeguretat);
				nodeService.setProperty(nodeRef, valoracioDocumentSimple, valoracio);
				nodeService.setProperty(nodeRef, tipusDictamen1DocumentSimple, tipusDictamen1);
				nodeService.setProperty(nodeRef, tipusDictamen2DocumentSimple, tipusDictamen2);
				nodeService.setProperty(nodeRef, accioDictaminada1DocumentSimple, accioDictaminada1);
				nodeService.setProperty(nodeRef, accioDictaminada2DocumentSimple, accioDictaminada2);
			}
			else if(type.equals("expedient")){
				codiClassificacio1 = (String)metadata.get(codiClassificacio1Expedient);
				codiClassificacio2 = (String)metadata.get(codiClassificacio2Expedient);
				denominacioClasse1 = (String)metadata.get(denominacioClasse1Expedient);
				denominacioClasse2 = (String)metadata.get(denominacioClasse2Expedient);
				sensibilitatDades = (String)metadata.get(sensibilitatDadesExpedient);
				advertenciaSeguretat = (String)metadata.get(advertenciaSeguretatExpedient);
				categoriaAdvertenciaSeguretat = (String)metadata.get(categoriaAdvertenciaSeguretatExpedient);
				valoracio = (String)metadata.get(valoracioExpedient);
				tipusDictamen1 = (String)metadata.get(tipusDictamen1Expedient);
				tipusDictamen2 = (String)metadata.get(tipusDictamen2Expedient);
				accioDictaminada1 = (String)metadata.get(accioDictaminada1Expedient);
				accioDictaminada2 = (String)metadata.get(accioDictaminada2Expedient);

				nodeService.setProperty(nodeRef, codiClassificacio1DocumentSimple, codiClassificacio1);
				nodeService.setProperty(nodeRef, codiClassificacio2DocumentSimple, codiClassificacio2);			
				nodeService.setProperty(nodeRef, denominacioClasse1DocumentSimple, denominacioClasse1);
				nodeService.setProperty(nodeRef, denominacioClasse2DocumentSimple, denominacioClasse2);
				nodeService.setProperty(nodeRef, sensibilitatDadesDocumentSimple, sensibilitatDades);
				nodeService.setProperty(nodeRef, advertenciaSeguretatDocumentSimple, advertenciaSeguretat);
				nodeService.setProperty(nodeRef, categoriaAdvertenciaSeguretatDocumentSimple, categoriaAdvertenciaSeguretat);
				nodeService.setProperty(nodeRef, valoracioDocumentSimple, valoracio);
				nodeService.setProperty(nodeRef, tipusDictamen1DocumentSimple, tipusDictamen1);
				nodeService.setProperty(nodeRef, tipusDictamen2DocumentSimple, tipusDictamen2);
				nodeService.setProperty(nodeRef, accioDictaminada1DocumentSimple, accioDictaminada1);
				nodeService.setProperty(nodeRef, accioDictaminada2DocumentSimple, accioDictaminada2);

			}
			else if(type.equals("serie")){
				codiClassificacio1 = (String)metadata.get(codiClassificacioSerie);
				denominacioClasse1 = (String)metadata.get(denominacioClasseSerie);
				sensibilitatDades = (String)metadata.get(sensibilitatDadesSerie);
				advertenciaSeguretat = (String)metadata.get(advertenciaSeguretatSerie);
				categoriaAdvertenciaSeguretat = (String)metadata.get(categoriaAdvertenciaSeguretatSerie);
				valoracio = (String)metadata.get(valoracioSerie);
				tipusDictamen1 = (String)metadata.get(tipusDictamen1Serie);
				tipusDictamen2 = (String)metadata.get(tipusDictamen2Serie);
				accioDictaminada1 = (String)metadata.get(accioDictaminada1Serie);
				accioDictaminada2 = (String)metadata.get(accioDictaminada2Serie);			
				String typeHijo = nodeService.getType(nodeRef).getLocalName();			

				if(typeHijo.equals("agregacio")){				
					String agrCodiClass2 = (String)nodeService.getProperty(nodeRef, codiClassificacio2Agregacio);
					String agrDenClass2 = (String)nodeService.getProperty(nodeRef, denominacioClasse2Agregacio);
					
					if("".equals(agrCodiClass2) || agrCodiClass2 == null) {
						nodeService.setProperty(nodeRef, codiClassificacio2Agregacio, "-");
					}
					if("".equals(agrDenClass2) || agrDenClass2 == null) {
						nodeService.setProperty(nodeRef, denominacioClasse2Agregacio, "-");
					}
					nodeService.setProperty(nodeRef, codiClassificacio1Agregacio, codiClassificacio1);
					nodeService.setProperty(nodeRef, denominacioClasse1Agregacio, denominacioClasse1);				
					nodeService.setProperty(nodeRef, sensibilitatDadesAgregacio, sensibilitatDades);
					nodeService.setProperty(nodeRef, advertenciaSeguretatAgregacio, advertenciaSeguretat);
					nodeService.setProperty(nodeRef, categoriaAdvertenciaSeguretatAgregacio, categoriaAdvertenciaSeguretat);
					nodeService.setProperty(nodeRef, valoracioAgregacio, valoracio);
					nodeService.setProperty(nodeRef, tipusDictamen1Agregacio, tipusDictamen1);
					nodeService.setProperty(nodeRef, tipusDictamen2Agregacio, tipusDictamen2);
					nodeService.setProperty(nodeRef, accioDictaminada1Agregacio, accioDictaminada1);
					nodeService.setProperty(nodeRef, accioDictaminada2Agregacio, accioDictaminada2);
				}
				else if(typeHijo.equals("expedient")){
					String expCodiClass2 = (String)nodeService.getProperty(nodeRef, codiClassificacio2Expedient);
					String expDenClass2 = (String)nodeService.getProperty(nodeRef, denominacioClasse2Expedient);
					
					if("".equals(expCodiClass2) || expCodiClass2 == null) {
						nodeService.setProperty(nodeRef, codiClassificacio2Expedient, "-");
					}
					if("".equals(expDenClass2) || expDenClass2 == null) {
						nodeService.setProperty(nodeRef, denominacioClasse2Expedient, "-");
					}
					nodeService.setProperty(nodeRef, codiClassificacio1Expedient, codiClassificacio1);
					nodeService.setProperty(nodeRef, denominacioClasse1Expedient, denominacioClasse1);
					nodeService.setProperty(nodeRef, sensibilitatDadesExpedient, sensibilitatDades);
					nodeService.setProperty(nodeRef, advertenciaSeguretatExpedient, advertenciaSeguretat);
					nodeService.setProperty(nodeRef, categoriaAdvertenciaSeguretatExpedient, categoriaAdvertenciaSeguretat);
					nodeService.setProperty(nodeRef, valoracioExpedient, valoracio);
					nodeService.setProperty(nodeRef, tipusDictamen1Expedient, tipusDictamen1);
					nodeService.setProperty(nodeRef, tipusDictamen2Expedient, tipusDictamen2);
					nodeService.setProperty(nodeRef, accioDictaminada1Expedient, accioDictaminada1);
					nodeService.setProperty(nodeRef, accioDictaminada2Expedient, accioDictaminada2);
				}
			}
		}
		AuthenticationUtil.setRunAsUser(usernameAuth);
		AuthenticationUtil.setFullyAuthenticatedUser(usernameAuth);
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {}
}