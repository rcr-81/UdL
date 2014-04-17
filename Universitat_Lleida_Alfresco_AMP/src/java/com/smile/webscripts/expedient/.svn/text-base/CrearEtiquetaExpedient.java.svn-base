package com.smile.webscripts.expedient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.w3c.dom.Element;

import com.smile.webscripts.helper.Arguments;
import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Impersonate;
import com.smile.webscripts.helper.UdlProperties;

public class CrearEtiquetaExpedient extends DeclarativeWebScript implements ConstantsUdL {

	private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public CrearEtiquetaExpedient() {
	}

	public CrearEtiquetaExpedient(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		NodeService nodeService = serviceRegistry.getNodeService();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		UdlProperties props = new UdlProperties();

		try {
			Impersonate.impersonate(props.getProperty(ADMIN_USERNAME));
			Element args = Arguments.getArguments(req);
			String expID = args.getElementsByTagName(FORM_PARAM_EXP_ID).item(0).getFirstChild().getNodeValue();
			NodeRef expedientNodeRef = new NodeRef(AUDIT_NODEREF_PREFIX + expID);

			String grupCreador = null;
			String quantitat = null;
			String codiClassificacio1 = null;
			String denominacioClasse1 = null;
			String codiClassificacio2 = null;
			String denominacioClasse2 = null;
			String nomNaturalAspecte = null;
			String nomNatural = null;
			String id = null;
			String localitzacio1 = null;
			String localitzacio2 = null;
			String stringDataInici = null;
			String stringDataFi = null;
			Boolean essencial = null;

			String tipo = nodeService.getType(expedientNodeRef).getLocalName();

			if (EXPEDIENT.equalsIgnoreCase(tipo)) {
				grupCreador = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDL_URI, "grup_creador_expedient"));
				quantitat = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDL_URI, "quantitat_expedient"));
				codiClassificacio1 = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDL_URI, "codi_classificacio_1_expedient"));
				denominacioClasse1 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "denominacio_classe_1_expedient"));
				codiClassificacio2 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "codi_classificacio_2_expedient"));
				denominacioClasse2 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "denominacio_classe_2_expedient"));

				Date dataInici = (Date) nodeService.getProperty(expedientNodeRef, QName.createQName(UDL_URI, "data_inici_expedient"));
				if (dataInici != null) {
					stringDataInici = df.format(dataInici);
				}

				Date dataFi = (Date) nodeService.getProperty(expedientNodeRef, QName.createQName(UDL_URI, "data_fi_expedient"));
				if (dataFi != null) {
					stringDataFi = df.format(dataFi);
				}

				if(nodeService.hasAspect(expedientNodeRef, QName.createQName(UDL_URI, "persona"))) {
					nomNaturalAspecte = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "nom_natural_persona"));

				}else if(nodeService.hasAspect(expedientNodeRef, QName.createQName(UDL_URI, "organ"))) {
					nomNaturalAspecte = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "nom_natural_organ"));
					
				}else if(nodeService.hasAspect(expedientNodeRef, QName.createQName(UDL_URI, "institucio"))) {
					nomNaturalAspecte = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "nom_natural_institucio"));
				}

				nomNatural = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "nom_natural_expedient"));
				id = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "secuencial_identificador_expedient"));
				localitzacio1 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "localitzacio_1_expedient"));
				localitzacio2 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "localitzacio_2_expedient"));

			}else if(RECORD_FOLDER.equalsIgnoreCase(tipo)) {
				grupCreador = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDLRM_URI, "grup_creador_expedient"));
				quantitat = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDLRM_URI, "quantitat_expedient"));
				codiClassificacio1 = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDLRM_URI, "codi_classificacio_1_expedient"));
				denominacioClasse1 = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDLRM_URI, "denominacio_classe_1_expedient"));
				codiClassificacio2 = (String) nodeService.getProperty(expedientNodeRef, QName.createQName(UDLRM_URI, "codi_classificacio_2_expedient"));
				denominacioClasse2 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDLRM_URI, "denominacio_classe_2_expedient"));

				Date dataInici = (Date) nodeService.getProperty(expedientNodeRef, QName.createQName(UDLRM_URI, "data_inici_expedient"));
				if (dataInici != null) {
					stringDataInici = df.format(dataInici);
				}

				Date dataFi = (Date) nodeService.getProperty(expedientNodeRef, QName.createQName(UDLRM_URI, "data_fi_expedient"));
				if (dataFi != null) {
					stringDataFi = df.format(dataFi);
				}

				if(nodeService.hasAspect(expedientNodeRef, QName.createQName(UDL_URI, "persona"))) {
					nomNaturalAspecte = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "nom_natural_persona"));

				}else if(nodeService.hasAspect(expedientNodeRef, QName.createQName(UDL_URI, "organ"))) {
					nomNaturalAspecte = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "nom_natural_organ"));
					
				}else if(nodeService.hasAspect(expedientNodeRef, QName.createQName(UDL_URI, "institucio"))) {
					nomNaturalAspecte = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDL_URI, "nom_natural_institucio"));
				}
				
				nomNatural = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDLRM_URI, "nom_natural_expedient"));
				id = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDLRM_URI, "secuencial_identificador_expedient"));
				localitzacio1 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDLRM_URI, "localitzacio_1_expedient"));
				localitzacio2 = (String) nodeService.getProperty(expedientNodeRef,	QName.createQName(UDLRM_URI, "localitzacio_2_expedient"));
				essencial = (Boolean) nodeService.getProperty(expedientNodeRef,	QName.createQName(RM_URI, "vitalRecordIndicator"));
			}
			
			model.put(PARAM_GRUP_CREADOR, grupCreador);
			model.put(PARAM_QUANTITAT, quantitat);
			model.put(PARAM_CODI_CLASSIFICACIO_1, codiClassificacio1);
			model.put(PARAM_DENOMINACIO_CLASSE_1, denominacioClasse1);			
			model.put(PARAM_CODI_CLASSIFICACIO_2, codiClassificacio2);
			model.put(PARAM_DENOMINACIO_CLASSE_2, denominacioClasse2);
			model.put(PARAM_DATA_INICI, stringDataInici);
			model.put(PARAM_DATA_FI, stringDataFi);
			model.put(PARAM_NOM_NATURAL_ASPECTE, nomNaturalAspecte);
			model.put(PARAM_NOM_NATURAL, nomNatural);
			model.put(PARAM_ID, id);
			model.put(PARAM_LOCALITZACIO_1, localitzacio1);
			model.put(PARAM_LOCALITZACIO_2, localitzacio2);
			model.put(PARAM_ESSENCIAL, essencial);
			
/*			
			model.put(PARAM_GRUP_CREADOR, "SECCIÓ DE TÍTOLS");
			model.put(PARAM_QUANTITAT, "15");
			model.put(PARAM_CODI_CLASSIFICACIO_1, "J127");
			model.put(PARAM_DENOMINACIO_CLASSE_1, "Expedients de títols");
			model.put(PARAM_CODI_CLASSIFICACIO_2, "I114");
			model.put(PARAM_DENOMINACIO_CLASSE_2, "Registre de patents i beques");
			model.put(PARAM_DATA_INICI, "20/07/2004");
			model.put(PARAM_DATA_FI, "20/07/2004");
			model.put(PARAM_NOM_NATURAL_ASPECTE, "ERES BADIA, MARTA JOANA");
			model.put(PARAM_NOM_NATURAL, "Expedient de títol de mestre en l'especialitat d'educació infantil 20040787. LOT 20050006. 2004");
			model.put(PARAM_ID, "38211");
*/
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
}
