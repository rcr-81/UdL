package es.cesca.alfresco.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import com.smile.webscripts.helper.ConstantsUdL;

public class UDLHelper implements ConstantsUdL {
	private static String METADATA_EXPEDIENT = "_expedient";
	private static String METADATA_DOCUMENT = "_documentSimple";
	private static String METADATA_SIGNATURA = "_signatura";
	
	// El nombre de la variable es el nombre del metadato en iArxiu
	// El valor es el nombre del metadato en Alfresco
	//private static String METADATA_CODI_CLASSIFICACIO = "codi_classificacio_1_expedient";
	private static String METADATA_CLASSIFICACIO_SEGURETAT_ACCES_EXPEDIENT = "classificacio_acces_expedient";
	private static String METADATA_SENSIBILITAT_DADES_LOPD_EXPEDIENT = "sensibilitat_dades_caracter_personal_expedient";
	private static String METADATA_TITOL_EXPEDIENT = "categoria_expedient";
	//private static String METADATA_TITOL_SERIE_DOCUMENTAL_EXPEDIENT = "idioma_1_expedient";
	private static String METADATA_DESCRIPTORS_EXPEDIENT = "idioma_1_expedient";
	
	private static String METADATA_CLASSIFICACIO_SEGURETAT_ACCES_DOCUMENT_SIMPLE = "classificacio_acces_documentSimple";
	private static String METADATA_SENSIBILITAT_DADES_LOPD_DOCUMENT_SIMPLE = "sensibilitat_dades_caracter_personal_documentSimple";
	private static String METADATA_TIPUS_DOCUMENT_SIMPLE = "tipus_documental_documentSimple";
	private static String METADATA_TITOL_DOCUMENT_SIMPLE = "tipus_entitat_documentSimple";
	private static String METADATA_TITOL_SERIE_DOCUMENTAL_DOCUMENT_SIMPLE = "categoria_documentSimple";
	private static String METADATA_DESCRIPTORS_DOCUMENT_SIMPLE = "idioma_1_documentSimple";
	
	private static String METADATA_TIPUS_SIGNATURA = "tipus_signatura";
	
	QName identificadorSerie = QName.createQName(UDLRM_URI, "secuencial_identificador_serie");
	QName codiClassificacioExpedient = QName.createQName(UDLRM_URI, "codi_classificacio_1_expedient");
	
	QName creador = QName.createQName(CM_URI, "creator");
	QName nom = QName.createQName(CM_URI, "name");
	QName organ = QName.createQName(UDL_URI, "organ");
	QName institucio = QName.createQName(UDL_URI, "institucio");
	QName persona = QName.createQName(UDL_URI, "persona");
	QName nomNaturalOrgan = QName.createQName(UDL_URI, "nom_natural_organ");
	QName nomNaturalInstitucio = QName.createQName(UDL_URI, "nom_natural_institucio");
	QName nomNaturalPersona = QName.createQName(UDL_URI, "nom_natural_persona");
		
	private static Log logger = LogFactory.getLog(XMLHelper.class);
	private ServiceRegistry serviceRegistry;
	private Map<String, Serializable> propsToBind;
	private NodeRef expNodeRef;
	private NodeRef docNodeRef;
	
	public UDLHelper(ServiceRegistry serviceRegistry, Map<String, Serializable> propsToBind, NodeRef expNodeRef, NodeRef docNodeRef) {
		System.out.println("INICIO UDLHelper");
		this.serviceRegistry = serviceRegistry;
		this.propsToBind = propsToBind;
		this.expNodeRef = expNodeRef;
		this.docNodeRef = docNodeRef;
		
		if(expNodeRef == null && docNodeRef != null) {
			this.expNodeRef = serviceRegistry.getNodeService().getPrimaryParent(docNodeRef).getParentRef();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("Contruir objeto UDLHelper.");
		}
	}
	
	public Map<String, Serializable> bindPropsToUDL(String type) {
		try {
			if(logger.isDebugEnabled()) {
				logger.debug("INICIO proceso de adaptación de los metadatos de la UdL al modelo iArxiu.");
			}
	
			Iterator it = propsToBind.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();
							
				if("expedient".equalsIgnoreCase(type)) {
					e = bindPropsExpedient(e);
					
				}else if("document".equalsIgnoreCase(type)) {
					e = bindPropsDocument(e);
					
				}else if("signatura".equalsIgnoreCase(type)) {
					e = bindPropsSignatura(e);					
				}

				propsToBind.put((String)e.getKey(), (Serializable)e.getValue());
			}
			
			if(logger.isDebugEnabled()) {
				logger.debug("FIN proceso de adaptación de los metadatos de la UdL al modelo iArxiu.");
			}
			System.out.println("FIN UDLHelper");
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("ERROR: mapeo metadatos iArxiu");
		}
		
		return this.propsToBind;
	}
	
	/**
	 * Tracta expedients
	 * 
	 * @param e
	 * @return
	 * @throws Exception
	 */
	private Map.Entry bindPropsExpedient(Map.Entry e) throws Exception {
		String key = (String)e.getKey();
		String value = "";

		if(key.contains(METADATA_EXPEDIENT)) {
			/*
			if(METADATA_CODI_CLASSIFICACIO.equalsIgnoreCase(key)) {
				value = (String)e.getValue();
				e.setValue(getCodiACUP(value));
				
			}else
			*/
			if(METADATA_CLASSIFICACIO_SEGURETAT_ACCES_EXPEDIENT.equalsIgnoreCase(key)) {
				value = (String)e.getValue();
				
				if("Públic".equalsIgnoreCase(value)) {
					e.setValue("Accés públic");

				}else if("Restringit".equalsIgnoreCase(value)) {
					e.setValue("Accés restringit");					
				
				}else if("Confidencial".equalsIgnoreCase(value)) {
					e.setValue("Accés confidencial");
				}
			
			}else if(METADATA_SENSIBILITAT_DADES_LOPD_EXPEDIENT.equalsIgnoreCase(key)) {
				value = (String)e.getValue();

				if("Basic".equalsIgnoreCase(value)) {
					e.setValue("Nivell baix");

				}else if("Mig".equalsIgnoreCase(value)) {
					e.setValue("Nivell mig");					
				
				}else if("Alt".equalsIgnoreCase(value)) {
					e.setValue("Nivell alt");
				}

			}else if(METADATA_TITOL_EXPEDIENT.equalsIgnoreCase(key)) {
				if(expNodeRef != null) {
					String nomNatural = (String)serviceRegistry.getNodeService().getProperty(expNodeRef, nom);
					String nomNaturalOrgan = "";
					String nomNaturalInstitucio = "";
					String nomNaturalPersona = "";
					
					if(serviceRegistry.getNodeService().hasAspect(expNodeRef, organ)) {
						nomNaturalOrgan = (String)serviceRegistry.getNodeService().getProperty(expNodeRef, this.nomNaturalOrgan);
						nomNaturalOrgan = " - " + nomNaturalOrgan;
					}
					if(serviceRegistry.getNodeService().hasAspect(expNodeRef, institucio)) {
						nomNaturalInstitucio = (String)serviceRegistry.getNodeService().getProperty(expNodeRef, this.nomNaturalInstitucio);
						nomNaturalInstitucio = " - " + nomNaturalInstitucio;
					}	
					if(serviceRegistry.getNodeService().hasAspect(expNodeRef, persona)) {
						nomNaturalPersona = (String)serviceRegistry.getNodeService().getProperty(expNodeRef, this.nomNaturalPersona);
						nomNaturalPersona = " - " + nomNaturalPersona;
					}
					
					e.setValue(nomNatural + nomNaturalOrgan + nomNaturalInstitucio + nomNaturalPersona);
				}
				
			}else if(METADATA_DESCRIPTORS_EXPEDIENT.equalsIgnoreCase(key)) {
				if(expNodeRef != null) {
					e.setValue(getCodiACUP((String)serviceRegistry.getNodeService().getProperty(expNodeRef, codiClassificacioExpedient)));
				}
			}
			/*
			else if(METADATA_TITOL_SERIE_DOCUMENTAL_EXPEDIENT.equalsIgnoreCase(key)) {
				if(expNodeRef != null) {
					NodeRef parentnodeRef = serviceRegistry.getNodeService().getPrimaryParent(expNodeRef).getParentRef();
					e.setValue((String)serviceRegistry.getNodeService().getProperty(parentnodeRef, nom));
				}
			}
			*/
		}
		
		return e;
	}
	
	/**
	 * Tracta documents simples
	 * 
	 * @param e
	 * @return
	 * @throws Exception
	 */
	private Map.Entry bindPropsDocument(Map.Entry e) throws Exception {
		String key = (String)e.getKey();
		String value = "";
		
		if(key.contains(METADATA_DOCUMENT)) {
			if(METADATA_CLASSIFICACIO_SEGURETAT_ACCES_DOCUMENT_SIMPLE.equalsIgnoreCase(key)) {
				value = (String)e.getValue();
				
				if("Públic".equalsIgnoreCase(value)) {
					e.setValue("Accés públic");

				}else if("Restringit".equalsIgnoreCase(value)) {
					e.setValue("Accés restringit");					
				
				}else if("Confidencial".equalsIgnoreCase(value)) {
					e.setValue("Accés confidencial");
				}
			
			}else if(METADATA_SENSIBILITAT_DADES_LOPD_DOCUMENT_SIMPLE.equalsIgnoreCase(key)) {
				value = (String)e.getValue();

				if("Basic".equalsIgnoreCase(value)) {
					e.setValue("Nivell baix");

				}else if("Mig".equalsIgnoreCase(value)) {
					e.setValue("Nivell mig");					
				
				}else if("Alt".equalsIgnoreCase(value)) {
					e.setValue("Nivell alt");
				}
			
			}else if(METADATA_TIPUS_DOCUMENT_SIMPLE.equalsIgnoreCase(key)) {
				value = (String)e.getValue();
	
				if("U10 ACTA".equalsIgnoreCase(value)) {
					e.setValue("Acta");
	
				}else if("U06 ACORD".equalsIgnoreCase(value)) {
					e.setValue("Acord");					
				
				}else if("U12 ALBARÀ".equalsIgnoreCase(value)) {
					e.setValue("Albarà");					
				
				}else if("U13 AL·LEGACIÓ".equalsIgnoreCase(value)) {
					e.setValue("Al·legació");
					
				}else if("U15 BUROFAX".equalsIgnoreCase(value)) {
					e.setValue("Burofax");
	
				}else if("U16 CARTA".equalsIgnoreCase(value)) {
					e.setValue("Carta");					
				
				}else if("U18 CATÀLEG".equalsIgnoreCase(value)) {
					e.setValue("Catàleg");
					
				}else if("U19 CERTIFICAT".equalsIgnoreCase(value)) {
					e.setValue("Certificat");					
				
				}else if("U20 CIRCULAR".equalsIgnoreCase(value)) {
					e.setValue("Circular");
	
				}else if("U21 CITACIÓ".equalsIgnoreCase(value)) {
					e.setValue("Citació");					
				
				}else if("U23 COMUNICAT".equalsIgnoreCase(value)) {
					e.setValue("Comunicat");
					
				}else if("U25 CONVENI".equalsIgnoreCase(value)) {
					e.setValue("Conveni");					
				
				}else if("U26 CONVOCATÒRIA/ORDRE DEL DIA".equalsIgnoreCase(value)) {
					e.setValue("Convocatòria");
	
				}else if("U31 DILIGÈNCIA".equalsIgnoreCase(value)) {
					e.setValue("Diligència");
					
				}else if("U00 DOCUMENT ADMINISTRATIU".equalsIgnoreCase(value)) {
					e.setValue("Document Administratiu");					
				
				}else if("U03 ENQUESTA".equalsIgnoreCase(value)) {
					e.setValue("Enquesta");
	
				}else if("U02 ESTADÍSTICA".equalsIgnoreCase(value)) {
					e.setValue("Estadística");					
				
				}else if("U33 ESTUDI".equalsIgnoreCase(value)) {
					e.setValue("Estudi");
					
				}else if("U35 EXTRACTE".equalsIgnoreCase(value)) {
					e.setValue("Extracte");					
				
				}else if("U36 FACTURA".equalsIgnoreCase(value)) {
					e.setValue("Factura");
	
				}else if("U37 FAX".equalsIgnoreCase(value)) {
					e.setValue("Fax");					
				
				}else if("U39 FOTOGRAFIA".equalsIgnoreCase(value)) {
					e.setValue("Fotografia");					
				
				}else if("U01 INFORME".equalsIgnoreCase(value)) {
					e.setValue("Informe");
	
				}else if("U43 INSTRUCCIÓ".equalsIgnoreCase(value)) {
					e.setValue("Instrucció");					
				
				}else if("U44 INVENTARI".equalsIgnoreCase(value)) {
					e.setValue("Inventari");
					
				}else if("U45 INVITACIÓ".equalsIgnoreCase(value)) {
					e.setValue("Invitació");					
				
				}else if("U49 LLISTAT".equalsIgnoreCase(value)) {
					e.setValue("Llista/Llistat");
	
				}else if("U54 NOMENAMENT".equalsIgnoreCase(value)) {
					e.setValue("Nomenament");					
				
				}else if("U58 NOTIFICACIÓ".equalsIgnoreCase(value)) {
					e.setValue("Notificació");

				}else if("U60 OFICI".equalsIgnoreCase(value)) {
					e.setValue("Ofici");					
				
				}else if("U64 PLA".equalsIgnoreCase(value)) {
					e.setValue("Pla");					
				
				}else if("U68 PRESSUPOST".equalsIgnoreCase(value)) {
					e.setValue("Pressupost");
	
				}else if("U70 PROGRAMA".equalsIgnoreCase(value)) {
					e.setValue("Programa");					
				
				}else if("U71 PROJECTE".equalsIgnoreCase(value)) {
					e.setValue("Projecte");
					
				}else if("U74 QUEIXA".equalsIgnoreCase(value)) {
					e.setValue("Queixa");					
				
				}else if("U76 REBUT".equalsIgnoreCase(value)) {
					e.setValue("Rebut");
					
				}else if("U77 RECLAMACIÓ".equalsIgnoreCase(value)) {
					e.setValue("Reclamació");					
				
				}else if("U78 RECURS".equalsIgnoreCase(value)) {
					e.setValue("Recurs");
	
				}else if("U79 REGISTRE".equalsIgnoreCase(value)) {
					e.setValue("Registre");					
				
				}else if("U80 REGLAMENT".equalsIgnoreCase(value)) {
					e.setValue("Reglament");

				}else if("U05 RESOLUCIÓ".equalsIgnoreCase(value)) {
					e.setValue("Resolució");
					
				}else if("U83 SALUDA".equalsIgnoreCase(value)) {
					e.setValue("Saluda");
	
				}else if("U85 SOL·LICITUD".equalsIgnoreCase(value)) {
					e.setValue("Sol·licitud");					
				
				}else if("U86 TELEGRAMA".equalsIgnoreCase(value)) {
					e.setValue("Telegrama");
				}
			
			}else if(METADATA_TITOL_DOCUMENT_SIMPLE.equalsIgnoreCase(key)) {
				String nomNatural = (String)serviceRegistry.getNodeService().getProperty(docNodeRef, nom);
				String nomNaturalOrgan = "";
				String nomNaturalInstitucio = "";
				String nomNaturalPersona = "";
				
				if(serviceRegistry.getNodeService().hasAspect(docNodeRef, organ)) {
					nomNaturalOrgan = (String)serviceRegistry.getNodeService().getProperty(docNodeRef, this.nomNaturalOrgan);
					nomNaturalOrgan = " - " + nomNaturalOrgan;
				}
				if(serviceRegistry.getNodeService().hasAspect(docNodeRef, institucio)) {
					nomNaturalInstitucio = (String)serviceRegistry.getNodeService().getProperty(docNodeRef, this.nomNaturalInstitucio);
					nomNaturalInstitucio = " - " + nomNaturalInstitucio;
				}	
				if(serviceRegistry.getNodeService().hasAspect(docNodeRef, persona)) {
					nomNaturalPersona = (String)serviceRegistry.getNodeService().getProperty(docNodeRef, this.nomNaturalPersona);
					nomNaturalPersona = " - " + nomNaturalPersona;
				}
				
				e.setValue(nomNatural + nomNaturalOrgan + nomNaturalInstitucio + nomNaturalPersona);
	
			}else if(METADATA_TITOL_SERIE_DOCUMENTAL_DOCUMENT_SIMPLE.equalsIgnoreCase(key)) {
				NodeRef expNodeRef = serviceRegistry.getNodeService().getPrimaryParent(docNodeRef).getParentRef();
				NodeRef serieNodeRef = serviceRegistry.getNodeService().getPrimaryParent(expNodeRef).getParentRef();
				
				e.setValue((String)serviceRegistry.getNodeService().getProperty(serieNodeRef, identificadorSerie));

			}else if(METADATA_DESCRIPTORS_DOCUMENT_SIMPLE.equalsIgnoreCase(key)) {
				if(docNodeRef != null) {
					NodeRef expNodeRef = serviceRegistry.getNodeService().getPrimaryParent(docNodeRef).getParentRef();
					e.setValue(getCodiACUP((String)serviceRegistry.getNodeService().getProperty(expNodeRef, codiClassificacioExpedient)));
			}
		}

		}
		
		return e;
	}

	/**
	 * Tracta signatures
	 * 
	 * @param e
	 * @return
	 * @throws Exception
	 */
	private Map.Entry bindPropsSignatura(Map.Entry e) throws Exception {
		String key = (String)e.getKey();
		String value = "";
		
		if(key.contains(METADATA_SIGNATURA)) {
			if(METADATA_TIPUS_SIGNATURA.equalsIgnoreCase(key)) {
				value = (String)e.getValue();
				
				if("enveloping".equalsIgnoreCase(value)) {
					e.setValue("enveloping");

				}else if("attached".equalsIgnoreCase(value)) {
					e.setValue("enveloped");					
				
				}else if("dettached".equalsIgnoreCase(value)) {
					e.setValue("detached");
				}
			}	
		}
		
		return e;
	}
	
	/**
	 * Retorna el codi de classificació i la denominació de classe corresponent al quadre de classificació ACUP
	 * 
	 * @param codiUdL: codi classificacio UdL
	 * @param codiComplet: codi classificació UdL + denominació classe UdL
	 * @return
	 */
	private String getCodiACUP(String codiUdL) {
		String result = "";
		
		//
		// A100
		//
		if(codiUdL.equalsIgnoreCase("A100")) {
			result = "A1000 ESTRUCTURA, GOVERN I ADMINISTRACIÓ"; 
					
		}else if(codiUdL.equalsIgnoreCase("A101")) {
			result = "A1001 Creació i Constitució";
			
		}else if(codiUdL.equalsIgnoreCase("A102")) {
			result = "A1007 Òrgans de govern col·legiats";
			
		}else if(codiUdL.equalsIgnoreCase("A102 E1")) {
			result = "A1007 Òrgans de govern col·legiats";
			
		}else if(codiUdL.equalsIgnoreCase("A102 E2")) {
			result = "A1007 Òrgans de govern col·legiats";
			
		}else if(codiUdL.equalsIgnoreCase("A102 E3")) {
			result = "A1007 Òrgans de govern col·legiats";
			
		}else if(codiUdL.equalsIgnoreCase("A102 E4")) {
			result = "A1007 Òrgans de govern col·legiats";
			
		}else if(codiUdL.equalsIgnoreCase("A102 E5")) {
			result = "A1007 Òrgans de govern col·legiats";
			
		}else if(codiUdL.equalsIgnoreCase("A102 E6")) {
			result = "A1007 Òrgans de govern col·legiats";
			
		}else if(codiUdL.equalsIgnoreCase("A102 E7")) {
			result = "A1007 Òrgans de govern col·legiats";
			
		}else if(codiUdL.equalsIgnoreCase("A102 E8")) {
			result = "A1007 Òrgans de govern col·legiats";
			
		}else if(codiUdL.equalsIgnoreCase("A102 E9")) {
			result = "A1007 Òrgans de govern col·legiats";
			
		}else if(codiUdL.equalsIgnoreCase("A102 E10")) {
			result = "A1007 Òrgans de govern col·legiats";
			
		}else if(codiUdL.equalsIgnoreCase("A103")) {
			result = "A1029 Organització administrativa";
			
		}else if(codiUdL.equalsIgnoreCase("A104")) {
			result = "A1062 Processos i procediments";
			
		}else if(codiUdL.equalsIgnoreCase("A105")) {
			result = "A1029 Organització administrativa";
			
		}else if(codiUdL.equalsIgnoreCase("A122")) {
			result = "A1063 Mapa de processos";
			
		}else if(codiUdL.equalsIgnoreCase("A106")) {
			result = "A1064 Fitxes de procediments";
			
		}else if(codiUdL.equalsIgnoreCase("A123")) {
			result = "A1051 Certificació digital";
			
		}else if(codiUdL.equalsIgnoreCase("A107")) {
			result = "A1030 Organigrama";

		}else if(codiUdL.equalsIgnoreCase("A107 E1")) {
			result = "A1030 Organigrama";

		}else if(codiUdL.equalsIgnoreCase("A107 E2")) {
			result = "A1030 Organigrama";

		}else if(codiUdL.equalsIgnoreCase("A108")) {
			result = "A1034 Plantilla";
			
		}else if(codiUdL.equalsIgnoreCase("A109")) {
			result = "A1035 Relació de llocs de treball";

		}else if(codiUdL.equalsIgnoreCase("A109 E1")) {
			result = "A1035 Relació de llocs de treball";
			
		}else if(codiUdL.equalsIgnoreCase("A124")) {
			result = "A1036 Catàleg de llocs de treball";
 
		}else if(codiUdL.equalsIgnoreCase("A124 E1")) {
			result = "A1039 Modificacions";
		
		}else if(codiUdL.equalsIgnoreCase("A124 E2")) {
			result = "A1038 Reclamacions";
		
		}else if(codiUdL.equalsIgnoreCase("A124 E3")) {
			result = "A1041 Places no catalogades";
		
		}else if(codiUdL.equalsIgnoreCase("A124 E4")) {
			result = "A1037 Expedients d’assimilació";
		
		}else if(codiUdL.equalsIgnoreCase("A124 E5")) {
			result = "A1036 Catàleg de llocs de treball";
			
		}else if(codiUdL.equalsIgnoreCase("A110")) {
			result = "A1032 Pressupostat";
			
		}else if(codiUdL.equalsIgnoreCase("A111")) {
			result = "A1031 Funcional";
			
		}else if(codiUdL.equalsIgnoreCase("A112")) {
			result = "A1036 Catàleg de llocs de treball";
			
		}else if(codiUdL.equalsIgnoreCase("A125")) {
			result = "A1042 Identitat/Identitats";
			
		}else if(codiUdL.equalsIgnoreCase("A113")) {
			result = "A1029 Organització administrativa";
			
		}else if(codiUdL.equalsIgnoreCase("A114")) {
			result = "A1029 Organització administrativa";
			
		}else if(codiUdL.equalsIgnoreCase("A115")) {
			result = "A1029 Organització administrativa";
			
		}else if(codiUdL.equalsIgnoreCase("A116")) {
			result = "A1029 Organització administrativa";
			
		}else if(codiUdL.equalsIgnoreCase("A117")) {
			result = "A1026 Planificació estratègica";
			
		}else if(codiUdL.equalsIgnoreCase("A117 E1")) {
			result = "A1027 Pla general";
			
		}else if(codiUdL.equalsIgnoreCase("A117 E2")) {
			result = "A1026 Planificació estratègica";
			
		}else if(codiUdL.equalsIgnoreCase("A117 E3")) {
			result = "A1028 Plans sectorials";
			
		}else if(codiUdL.equalsIgnoreCase("A118")) {
			result = "E1002 Programació plurianual";
			
		}else if(codiUdL.equalsIgnoreCase("A119")) {
			result = "A1026 Planificació estratègica";
			
		}else if(codiUdL.equalsIgnoreCase("A120")) {
			result = "A1026 Planificació estratègica";
			
		}else if(codiUdL.equalsIgnoreCase("A121")) {
			result = "A1059 Sistema de qualitat";
		
		}else if(codiUdL.equalsIgnoreCase("A121 E1")) {
			result = "A1059 Sistema de qualitat";
		

		//
		// B100
		//
		}else if(codiUdL.equalsIgnoreCase("B100")) {
			result = "B1000 GESTIÓ DE LA INFORMACIÓ I LES COMUNICACIONS";

		}else if(codiUdL.equalsIgnoreCase("B101")) {
			result = "B1001 Creació i disseny de documents";

		}else if(codiUdL.equalsIgnoreCase("B101 E1")) {
			result = "B1001 Creació i disseny de documents";
		
		}else if(codiUdL.equalsIgnoreCase("B101 E2")) {
			result = "B1001 Creació i disseny de documents";
		
		}else if(codiUdL.equalsIgnoreCase("B101 E3")) {
			result = "B1001 Creació i disseny de documents";
		
		}else if(codiUdL.equalsIgnoreCase("B102")) {
			result = "B1032 Gestió dels documents administratius i els arxius";

		}else if(codiUdL.equalsIgnoreCase("B103")) {
			result = "B1032 Gestió dels documents administratius i els arxius";
			
		}else if(codiUdL.equalsIgnoreCase("B103 E1")) {
			result = "B1032 Gestió dels documents administratius i els arxius";
			
		}else if(codiUdL.equalsIgnoreCase("B103 E2")) {
			result = "B1041 Fons aliens";
			
		}else if(codiUdL.equalsIgnoreCase("B104")) {
			result = "B1032 Gestió del documents administratius i els arxius";
			
		}else if(codiUdL.equalsIgnoreCase("B105")) {
			result = "B1034 Quadre de classificació";
			
		}else if(codiUdL.equalsIgnoreCase("B106")) {
			result = "B1035 Avaluació i accés";
			
		}else if(codiUdL.equalsIgnoreCase("B107")) {
			result = "B1032 Gestió del documents administratius i els arxius";
			
		}else if(codiUdL.equalsIgnoreCase("B108")) {
			result = "B1039 Descripció, control i recuperació";
			
		}else if(codiUdL.equalsIgnoreCase("B109")) {
			result = "B1038 Quadre d’accés i seguretat";
			
		}else if(codiUdL.equalsIgnoreCase("B110")) {
			result = "B1048 Consulta i préstec";
			
		}else if(codiUdL.equalsIgnoreCase("B111")) {
			result = "B1048 Consulta i préstec";
			
		}else if(codiUdL.equalsIgnoreCase("B112")) {
			result = "B1033 Classificació";
			
		}else if(codiUdL.equalsIgnoreCase("B113")) {
			result = "B1040 Arxius actius";
			
		}else if(codiUdL.equalsIgnoreCase("B114")) {
			result = "B1045 Preservació i conservació";
			
		}else if(codiUdL.equalsIgnoreCase("B115")) {
			result = "B1045 Preservació i conservació";
			
		}else if(codiUdL.equalsIgnoreCase("B116")) {
			result = "B1045 Preservació i conservació";
			
		}else if(codiUdL.equalsIgnoreCase("B117")) {
			result = "B1035 Avaluació i accés";
			
		}else if(codiUdL.equalsIgnoreCase("B118")) {
			result = "B1050 Tractament de dades de caràcter personal";
			
		}else if(codiUdL.equalsIgnoreCase("B119")) {
			result = "A1051 Certificació digital";
			
		}else if(codiUdL.equalsIgnoreCase("B120")) {
			result = "B1043 Tria i eliminació documental";
			
		}else if(codiUdL.equalsIgnoreCase("B121")) {
			result = "A1066 Auditories";
			
		}else if(codiUdL.equalsIgnoreCase("B122")) {
			result = "B1064 Gestió dels fons bibliogràfics i hemerogràfics";
			
		}else if(codiUdL.equalsIgnoreCase("B122 E1")) {
			result = "B1064 Gestió dels fons bibliogràfics i hemerogràfics";
			
		}else if(codiUdL.equalsIgnoreCase("B122 E2")) {
			result = "B1064 Gestió dels fons bibliogràfics i hemerogràfics";
			
		}else if(codiUdL.equalsIgnoreCase("B122 E3")) {
			result = "B1064 Gestió dels fons bibliogràfics i hemerogràfics";
			
		}else if(codiUdL.equalsIgnoreCase("B122 E4")) {
			result = "B1064 Gestió dels fons bibliogràfics i hemerogràfics";
			
		}else if(codiUdL.equalsIgnoreCase("B123")) {
			result = "B1064 Gestió dels fons bibliogràfics i hemerogràfics";
			
		}else if(codiUdL.equalsIgnoreCase("B124")) {
			result = "B1064 Gestió dels fons bibliogràfics i hemerogràfics";
			
		}else if(codiUdL.equalsIgnoreCase("B125")) {
			result = "B1065 Adquisició de fons";
			
		}else if(codiUdL.equalsIgnoreCase("B126")) {
			result = "B1065 Adquisicions de fons";
			
		}else if(codiUdL.equalsIgnoreCase("B127")) {
			result = "B1069 Donacions i llegats";
			
		}else if(codiUdL.equalsIgnoreCase("B128")) {
			result = "B1070 Cessions";
			
		}else if(codiUdL.equalsIgnoreCase("B129")) {
			result = "B1071 Intercanvis";
			
		}else if(codiUdL.equalsIgnoreCase("B130")) {
			result = "B1072 Catalogació";
			
		}else if(codiUdL.equalsIgnoreCase("B131")) {
			result = "B1073 Serveis a l’usuari";
			
		}else if(codiUdL.equalsIgnoreCase("B132")) {
			result = "B1074 Informació bibliogràfica";
			
		}else if(codiUdL.equalsIgnoreCase("B133")) {
			result = "B1075 Préstec i consultes";
			
		}else if(codiUdL.equalsIgnoreCase("B134")) {
			result = "B1076 Préstec interbibliotecari";
			
		}else if(codiUdL.equalsIgnoreCase("B136")) {
			result = "B1077 Reprografia";
			
		}else if(codiUdL.equalsIgnoreCase("B137")) {
			result = "B1078 Conservació i restauració";
			
		}else if(codiUdL.equalsIgnoreCase("B138")) {
			result = "B1079 Eliminació";
			
		}else if(codiUdL.equalsIgnoreCase("B139")) {
			result = "B1022 Tecnologies de la Informació";
			
		}else if(codiUdL.equalsIgnoreCase("B140")) {
			result = "B1031 Consultes i incidències";
			
		}else if(codiUdL.equalsIgnoreCase("B141")) {
			result = "B1025 Aplicacions informàtiques";
			
		}else if(codiUdL.equalsIgnoreCase("B142")) {
			result = "B1024 Sistemes informàtics";
			
		}else if(codiUdL.equalsIgnoreCase("B143")) {
			result = "B1028 Gestió dels bancs de dades";
			
		}else if(codiUdL.equalsIgnoreCase("B166")) {
			result = "B1029 Seguretat informàtica";
			
		}else if(codiUdL.equalsIgnoreCase("B144")) {
			result = "B1007 Gestió de les comunicacions";
			
		}else if(codiUdL.equalsIgnoreCase("B145")) {
			result = "B1008 Gestió del correu ordinari";
			
		}else if(codiUdL.equalsIgnoreCase("B146")) {
			result = "B1019 Registre General d'Entrada i Sortida";
			
		}else if(codiUdL.equalsIgnoreCase("B147")) {
			result = "B1020 Registre intern";
			
		}else if(codiUdL.equalsIgnoreCase("B148")) {
			result = "B1010 Correu extern";
			
		}else if(codiUdL.equalsIgnoreCase("B149")) {
			result = "B1009 Correu intern";
			
		}else if(codiUdL.equalsIgnoreCase("B150")) {
			result = "B1011 Missatgeria";
			
		}else if(codiUdL.equalsIgnoreCase("B151")) {
			result = "B1012 Telecomunicacions";
			
		}else if(codiUdL.equalsIgnoreCase("B152")) {
			result = "B1013 Telefonia";
			
		}else if(codiUdL.equalsIgnoreCase("B153")) {
			result = "B1013 Telefonia";
			
		}else if(codiUdL.equalsIgnoreCase("B154")) {
			result = "B1013 Telefonia";
			
		}else if(codiUdL.equalsIgnoreCase("B155")) {
			result = "B1013 Telefonia";
			
		}else if(codiUdL.equalsIgnoreCase("B156")) {
			result = "B1014 Telefax i Burofax";
			
		}else if(codiUdL.equalsIgnoreCase("B157")) {
			result = "B1015 Correu electrònic";
			
		}else if(codiUdL.equalsIgnoreCase("B167")) {
			result = "A1048 Seu electrònica";
			
		}else if(codiUdL.equalsIgnoreCase("B158")) {
			result = "B1058 Producció i gestió de les publicacions";
			
		}else if(codiUdL.equalsIgnoreCase("B159")) {
			result = "B1059 Edició";
			
		}else if(codiUdL.equalsIgnoreCase("B160")) {
			result = "B1059 Edició";
			
		}else if(codiUdL.equalsIgnoreCase("B161")) {
			result = "B1060 Disseny i revisió";
			
		}else if(codiUdL.equalsIgnoreCase("B162")) {
			result = "B1061 Publicació";
			
		}else if(codiUdL.equalsIgnoreCase("B163")) {
			result = "B1062 Distribució i venda";
			
		}else if(codiUdL.equalsIgnoreCase("B164")) {
			result = "B1063 Drets d’autor, dipòsit legal i ISBN";
			
		}else if(codiUdL.equalsIgnoreCase("B165")) {
			result = "B1063 Drets d’autor, dipòsit legal i ISBN";
		
			
		//
		// C100
		//
		}else if(codiUdL.equalsIgnoreCase("C100")) {
			result = "C1000 REPRESENTACIÓ I RELACIONS PÚBLIQUES";
			
		}else if(codiUdL.equalsIgnoreCase("C101")) {
			result = "C1001 Actes oficials i protocol·laris";
			
		}else if(codiUdL.equalsIgnoreCase("C102")) {
			result = "C1002 Inauguracions";
			
		}else if(codiUdL.equalsIgnoreCase("C103")) {
			result = "C1006 Recepcions";
			
		}else if(codiUdL.equalsIgnoreCase("C104")) {
			result = "C1001 Actes oficials i protocolaris";
			
		}else if(codiUdL.equalsIgnoreCase("C105")) {
			result = "C1002 Inauguracions";
			
		}else if(codiUdL.equalsIgnoreCase("C106")) {
			result = "C1001 Actes oficials i protocol·laris";
			
		}else if(codiUdL.equalsIgnoreCase("C107")) {
			result = "C1007 Commemoracions";
			
		}else if(codiUdL.equalsIgnoreCase("C108")) {
			result = "C1003 Homenatges i distincions";

		}else if(codiUdL.equalsIgnoreCase("C108 E1")) {
			result = "C1004 Doctor honoris causa";
			
		}else if(codiUdL.equalsIgnoreCase("C108 E2")) {
			result = "C1005 Medalles i diplomes";
			
		}else if(codiUdL.equalsIgnoreCase("C108 E3")) {
			result = "C1005 Medalles i diplomes";
			
		}else if(codiUdL.equalsIgnoreCase("C108 E4")) {
			result = "C1005 Medalles i diplomes";

		}else if(codiUdL.equalsIgnoreCase("C109")) {
			result = "C1001 Actes oficials i protocol·laris";
			
		}else if(codiUdL.equalsIgnoreCase("C110")) {
			result = "C1001 Actes oficials i protocol·laris";
			
		}else if(codiUdL.equalsIgnoreCase("C111")) {
			result = "C1009 Relacions públiques";
			
		}else if(codiUdL.equalsIgnoreCase("C112")) {
			result = "C1010 Promoció de la Universitat";
			
		}else if(codiUdL.equalsIgnoreCase("C112 E1")) {
			result = "C1022 Sol·licituds d'informació";
			
		}else if(codiUdL.equalsIgnoreCase("C112 E2")) {
			result = "C1011 Concursos i premis";
			
		}else if(codiUdL.equalsIgnoreCase("C113")) {
			result = "C1013 Imatge corporativa";
			
		}else if(codiUdL.equalsIgnoreCase("C114")) {
			result = "C1014 Distintius gràfics";
			
		}else if(codiUdL.equalsIgnoreCase("C115")) {
			result = "C1015 Aplicació de la imatge corporativa";
			
		}else if(codiUdL.equalsIgnoreCase("C116")) {
			result = "C1013 Imatge corporativa";
			
		}else if(codiUdL.equalsIgnoreCase("C117")) {
			result = "C1024 Visites i viatges";
			
		}else if(codiUdL.equalsIgnoreCase("C118")) {
			result = "C1023 Suggeriments i queixes";
			
		}else if(codiUdL.equalsIgnoreCase("C119")) {
			result = "C1025 Agraïments i felicitacions";
			
		}else if(codiUdL.equalsIgnoreCase("C120")) {
			result = "C1026 Condols";
			
		}else if(codiUdL.equalsIgnoreCase("C121")) {
			result = "C1016 Relacions amb els mitjans de comunicació";
			
		}else if(codiUdL.equalsIgnoreCase("C122")) {
			result = "C1016 Relacions amb els mitjans de comunicació";
			
		}else if(codiUdL.equalsIgnoreCase("C123")) {
			result = "C1017 Notes de premsa";
			
		}else if(codiUdL.equalsIgnoreCase("C124")) {
			result = "C1020 Conferències de premsa";
			
		}else if(codiUdL.equalsIgnoreCase("C125")) {
			result = "C1021 Recull de premsa";
			
		}else if(codiUdL.equalsIgnoreCase("C126")) {
			result = "C1027 Relacions exteriors";
			
		}else if(codiUdL.equalsIgnoreCase("C127")) {
			result = "C1033 Generalitat de Catalunya";
			
		}else if(codiUdL.equalsIgnoreCase("C128")) {
			result = "C1033 Generalitat de Catalunya";
			
		}else if(codiUdL.equalsIgnoreCase("C129")) {
			result = "C1033 Generalitat de Catalunya";
			
		}else if(codiUdL.equalsIgnoreCase("C130")) {
			result = "C1031 Institucions i Administració General de l'Estat";
			
		}else if(codiUdL.equalsIgnoreCase("C131")) {
			result = "C1031 Institucions i Administració General de l'Estat";
			
		}else if(codiUdL.equalsIgnoreCase("C132")) {
			result = "C1032 Institucions i administracions autonòmiques";
			
		}else if(codiUdL.equalsIgnoreCase("C133")) {
			result = "C1034 Administració local";
			
		}else if(codiUdL.equalsIgnoreCase("C134")) {
			result = "C1035 Diputacions";
			
		}else if(codiUdL.equalsIgnoreCase("C135")) {
			result = "C1036 Consells comarcals";
			
		}else if(codiUdL.equalsIgnoreCase("C136")) {
			result = "C1037 Ajuntaments";
			
		}else if(codiUdL.equalsIgnoreCase("C160")) {
			result = "C1038 Administració de Justícia";
			
		}else if(codiUdL.equalsIgnoreCase("C138")) {
			result = "C1029 Unió Europea";
			
		}else if(codiUdL.equalsIgnoreCase("C139")) {
			result = "C1030 Institucions i organismes de països estrangers";
			
		}else if(codiUdL.equalsIgnoreCase("C140")) {
			result = "C1028 Organismes internacionals i supraestatals";
			
		}else if(codiUdL.equalsIgnoreCase("C141")) {
			result = "C1029 Unió Europea";
			
		}else if(codiUdL.equalsIgnoreCase("C142")) {
			result = "C1030 Institucions i organismes de països estrangers";
			
		}else if(codiUdL.equalsIgnoreCase("C143")) {
			result = "C1039 Universitats";
			
		}else if(codiUdL.equalsIgnoreCase("C144")) {
			result = "C1039 Universitats";
			
		}else if(codiUdL.equalsIgnoreCase("C145")) {
			result = "C1039 Universitats";
			
		}else if(codiUdL.equalsIgnoreCase("C146")) {
			result = "C1039 Universitats";
			
		}else if(codiUdL.equalsIgnoreCase("C147")) {
			result = "C1039 Universitats";
			
		}else if(codiUdL.equalsIgnoreCase("C148")) {
			result = "C1039 Universitats";
			
		}else if(codiUdL.equalsIgnoreCase("C149")) {
			result = "C1039 Universitats";
			
		}else if(codiUdL.equalsIgnoreCase("C150")) {
			result = "C1040 Ensenyament no universitari";
			
		}else if(codiUdL.equalsIgnoreCase("C151")) {
			result = "C1041 Institucions, associacions i entitats";
			
		}else if(codiUdL.equalsIgnoreCase("C152")) {
			result = "C1042 Sector privat";
			
		}else if(codiUdL.equalsIgnoreCase("C153")) {
			result = "C1052 Relacions interiors";
			
		}else if(codiUdL.equalsIgnoreCase("C154")) {
			result = "C1052 Relacions interiors";
			
		}else if(codiUdL.equalsIgnoreCase("C155")) {
			result = "C1052 Relacions interiors";
			
		}else if(codiUdL.equalsIgnoreCase("C156")) {
			result = "C1052 Relacions interiors";
			
		}else if(codiUdL.equalsIgnoreCase("C157")) {
			result = "C1001 Actes oficials i protocolaris";
			
		}else if(codiUdL.equalsIgnoreCase("C157 E1")) {
			result = "C1001 Actes oficials i protocolaris";
			
		}else if(codiUdL.equalsIgnoreCase("C157 E2")) {
			result = "C1001 Actes oficials i protocolaris";
			
		}else if(codiUdL.equalsIgnoreCase("C158")) {
			result = "C1027 Relacions exteriors";
			
			
		//
		// D100
		//
		}else if(codiUdL.equalsIgnoreCase("D100")) {
			result = "D1000 GESTIÓ DELS RECURSOS HUMANS";
			
		}else if(codiUdL.equalsIgnoreCase("D101") || codiUdL.contains("D101")) {
			result = "D1004 Dotació de personal";
			
		}else if(codiUdL.equalsIgnoreCase("D102") || codiUdL.contains("D102")) {
			result = "D1005 Anàlisi i planificació de les necessitats";
			
		}else if(codiUdL.equalsIgnoreCase("D103") || codiUdL.contains("D103")) {
			result = "D1006 Peticions de personal";
			
		}else if(codiUdL.equalsIgnoreCase("D104") || codiUdL.contains("D104")) {
			result = "D1018 Borsa de treball";
			
		}else if(codiUdL.equalsIgnoreCase("D105") || codiUdL.contains("D105")) {
			result = "D1007 Oferta pública d'ocupació";
			
		}else if(codiUdL.equalsIgnoreCase("D106") || codiUdL.contains("D106")) {
			result = "D1008 Selecció i provisió de llocs de treball";
			
		}else if(codiUdL.equalsIgnoreCase("D107") || codiUdL.contains("D107")) {
			result = "D1011 Concursos de nou ingrés";
			
		}else if(codiUdL.equalsIgnoreCase("D108") || codiUdL.contains("D108")) {
			result = "D1009 Oposicions";
			
		}else if(codiUdL.equalsIgnoreCase("D109") || codiUdL.contains("D109")) {
			result = "D1014 Concursos de mèrits";
			
		}else if(codiUdL.equalsIgnoreCase("D110") || codiUdL.contains("D110")) {
			result = "D1010 Concursos-oposicions";
			
		}else if(codiUdL.equalsIgnoreCase("D111") || codiUdL.contains("D111")) {
			result = "D1013 Concursos de promoció interna";
			
		}else if(codiUdL.equalsIgnoreCase("D112") || codiUdL.contains("D112")) {
			result = "D1012 Concursos de trasllats";
			
		}else if(codiUdL.equalsIgnoreCase("D113") || codiUdL.contains("D113")) {
			result = "D1015 Convocatòries de lliure designació";
			
		}else if(codiUdL.equalsIgnoreCase("D114") || codiUdL.contains("D114")) {
			result = "D1016 Comissió de serveis";
			
		}else if(codiUdL.equalsIgnoreCase("D186")) {
			result = "D1017 Mobilitat";
			
		}else if(codiUdL.equalsIgnoreCase("D115") || codiUdL.contains("D115")) {
			result = "D1019 Nomenaments";
			
		}else if(codiUdL.equalsIgnoreCase("D187")) {
			result = "D1020 Contractació";
			
		}else if(codiUdL.equalsIgnoreCase("D188")) {
			result = "D1045 Carrera, promoció i avaluació professional";
			
		}else if(codiUdL.equalsIgnoreCase("D116") || codiUdL.contains("D116")) {
			result = "D1021 Expedients de personal";
			
		}else if(codiUdL.equalsIgnoreCase("D117") || codiUdL.contains("D117")) {
			result = "D1033 Règim disciplinari";
			
		}else if(codiUdL.equalsIgnoreCase("D118") || codiUdL.contains("D118")) {
			result = "D1033 Règim disciplinari";
			
		}else if(codiUdL.equalsIgnoreCase("D119") || codiUdL.contains("D119")) {
			result = "D1077 Règim laboral";
			
		}else if(codiUdL.equalsIgnoreCase("D120") || codiUdL.contains("D120")) {
			result = "D1078 Jornada laboral i horari";
			
		}else if(codiUdL.equalsIgnoreCase("D121") || codiUdL.contains("D121")) {
			result = "D1083 Vacances, permisos i llicències";
			
		}else if(codiUdL.equalsIgnoreCase("D122") || codiUdL.contains("D122")) {
			result = "D1085 Permisos";
			
		}else if(codiUdL.equalsIgnoreCase("D123") || codiUdL.contains("D123")) {
			result = "D1086 Llicències";
			
		}else if(codiUdL.equalsIgnoreCase("D124") || codiUdL.contains("D124")) {
			result = "D1084 Vacances";
			
		}else if(codiUdL.equalsIgnoreCase("D125") || codiUdL.contains("D125")) {
			result = "D1087 Assumptes personals";
			
		}else if(codiUdL.equalsIgnoreCase("D126") || codiUdL.contains("D126")) {
			result = "D1077 Règim laboral";
			
		}else if(codiUdL.equalsIgnoreCase("D127") || codiUdL.contains("D127")) {
			result = "D1049 Retribucions";
			
		}else if(codiUdL.equalsIgnoreCase("D128") || codiUdL.contains("D128")) {
			result = "D1050 Nòmina";
			
		}else if(codiUdL.equalsIgnoreCase("D129") || codiUdL.contains("D129")) {
			result = "D1060 IRPF";
			
		}else if(codiUdL.equalsIgnoreCase("D130") || codiUdL.contains("D130")) {
			result = "D1055 Triennis";
			
		}else if(codiUdL.equalsIgnoreCase("D131") || codiUdL.contains("D131")) {
			result = "D1051 Variacions de nòmina";
			
		}else if(codiUdL.equalsIgnoreCase("D132") || codiUdL.contains("D132")) {
			result = "D1059 Gratificacions per serveis extraordinaris";
			
		}else if(codiUdL.equalsIgnoreCase("D133") || codiUdL.contains("D133")) {
			result = "D1049 Retribucions";
			
		}else if(codiUdL.equalsIgnoreCase("D134") || codiUdL.contains("D134")) {
			result = "D1061 Retencions judicials";
			
		}else if(codiUdL.equalsIgnoreCase("D135") || codiUdL.contains("D135")) {
			result = "D1062 Bestretes";
			
		}else if(codiUdL.equalsIgnoreCase("D136") || codiUdL.contains("D136")) {
			result = "D1082 Compatibilitats";
			
		}else if(codiUdL.equalsIgnoreCase("D137") || codiUdL.contains("D137")) {
			result = "D1074 Fons d'acció social";
			
		}else if(codiUdL.equalsIgnoreCase("D138") || codiUdL.contains("D138")) {
			result = "D1102 Prevenció de riscos laborals";
			
		}else if(codiUdL.equalsIgnoreCase("D139") || codiUdL.contains("D139")) {
			result = "D1110 Vigilància de la salut";
			
		}else if(codiUdL.equalsIgnoreCase("D189")) {
			result = "D1103 Pla de riscos laborals";
			
		}else if(codiUdL.equalsIgnoreCase("D140")) {
			result = "D1107 Pla d'autoprotecció";
			
		}else if(codiUdL.equalsIgnoreCase("D141")) {
			result = "D1104 Planificació de l'activitat preventiva";
			
		}else if(codiUdL.equalsIgnoreCase("D190")) {
			result = "D1105 Avaluació de riscos laborals";
			
		}else if(codiUdL.equalsIgnoreCase("D191")) {
			result = "D1106 Investigació d'accidents/incidents";
			
		}else if(codiUdL.equalsIgnoreCase("D192")) {
			result = "D1108 Informació i divulgació sobre la prevenció i seguretat laboral";
			
		}else if(codiUdL.equalsIgnoreCase("D142") || codiUdL.contains("D142")) {
			result = "D1049 Retribucions";
			
		}else if(codiUdL.equalsIgnoreCase("D143") || codiUdL.contains("D143")) {
			result = "D1063 Avantatges socials";
			
		}else if(codiUdL.equalsIgnoreCase("D144") || codiUdL.contains("D144")) {
			result = "D1064 Seguretat Social (SS)";
			
		}else if(codiUdL.equalsIgnoreCase("D145") || codiUdL.contains("D145")) {
			result = "D1066 Altes i baixes";
			
		}else if(codiUdL.equalsIgnoreCase("D193")) {
			result = "D1068 Baixes i altes ILT";
			
		}else if(codiUdL.equalsIgnoreCase("D146") || codiUdL.contains("D146")) {
			result = "D1065 Llibre de matrícula";
			
		}else if(codiUdL.equalsIgnoreCase("D147") || codiUdL.contains("D147")) {
			result = "D1067 Liquidacions TC1-TC2";
			
		}else if(codiUdL.equalsIgnoreCase("D148") || codiUdL.contains("D148")) {
			result = "D1067 Liquidacions TC1-TC2";
			
		}else if(codiUdL.equalsIgnoreCase("D149") || codiUdL.contains("D149")) {
			result = "D1067 Liquidacions TC1-TC2";
			
		}else if(codiUdL.equalsIgnoreCase("D150") || codiUdL.contains("D150")) {
			result = "D1064 Seguretat Social";
			
		}else if(codiUdL.equalsIgnoreCase("D194") || codiUdL.contains("D194")) {
			result = "D1069 Drets passius";
			
		}else if(codiUdL.equalsIgnoreCase("D151") || codiUdL.contains("D151")) {
			result = "D1070 Mutualitats";
			
		}else if(codiUdL.equalsIgnoreCase("D152") || codiUdL.contains("D152")) {
			result = "D1070 Mutualitats";
			
		}else if(codiUdL.equalsIgnoreCase("D153") || codiUdL.contains("D153")) {
			result = "D1071 Mútues";
			
		}else if(codiUdL.equalsIgnoreCase("D154")) {
			result = "D1072 Accidents laborals";
			
		}else if(codiUdL.equalsIgnoreCase("D155") || codiUdL.contains("D155")) {
			result = "K1020 Assistència sanitària";
			
		}else if(codiUdL.equalsIgnoreCase("D156") || codiUdL.contains("D156")) {
			result = "D1093 Relacions laborals";
			
		}else if(codiUdL.equalsIgnoreCase("D157") || codiUdL.contains("D157")) {
			result = "D1094 Representació del personal";
			
		}else if(codiUdL.equalsIgnoreCase("D158") || codiUdL.contains("D158")) {
			result = "D1094 Representació del personal";
			
		}else if(codiUdL.equalsIgnoreCase("D159") || codiUdL.contains("D159")) {
			result = "D1094 Representació del personal";
			
		}else if(codiUdL.equalsIgnoreCase("D160")) {
			result = "D1096 Seccions sindicals";
			
		}else if(codiUdL.equalsIgnoreCase("D161")) {
			result = "D1096 Seccions sindicals";
			
		}else if(codiUdL.equalsIgnoreCase("D162")) {
			result = "D1096 Seccions sindicals";
			
		}else if(codiUdL.equalsIgnoreCase("D163")) {
			result = "D1096 Seccions sindicals";
			
		}else if(codiUdL.equalsIgnoreCase("D164")) {
			result = "D1098 Conveni col·lectiu";
			
		}else if(codiUdL.equalsIgnoreCase("D165") || codiUdL.contains("D165")) {
			result = "D1097 Negociacions i consultes";
			
		}else if(codiUdL.equalsIgnoreCase("D166") || codiUdL.contains("D166")) {
			result = "D1099 Reivindicacions i reclamacions";
			
		}else if(codiUdL.equalsIgnoreCase("D167")) {
			result = "D1100 Vagues";
			
		}else if(codiUdL.equalsIgnoreCase("D168")) {
			result = "D1101 Conciliació i arbitratge";
			
		}else if(codiUdL.equalsIgnoreCase("D169") || codiUdL.contains("D169")) {
			result = "D1089 Formació i perfeccionament";
			
		}else if(codiUdL.equalsIgnoreCase("D170")) {
			result = "D1091 Programació de serveis formatius";
			
		}else if(codiUdL.equalsIgnoreCase("D171") || codiUdL.contains("D171")) {
			result = "D1091 Programació de serveis formatius";
			
		}else if(codiUdL.equalsIgnoreCase("D172") || codiUdL.contains("D172")) {
			result = "D1090 Catàleg de serveis formatius";
			
		}else if(codiUdL.equalsIgnoreCase("D173") || codiUdL.contains("D173")) {
			result = "D1092 Accions formatives";
			
		}else if(codiUdL.equalsIgnoreCase("D174") || codiUdL.contains("D174")) {
			result = "D1091 Programació de serveis formatius";
			
		}else if(codiUdL.equalsIgnoreCase("D175") || codiUdL.contains("D175")) {
			result = "D1091 Programació de serveis formatius";
			
		}else if(codiUdL.equalsIgnoreCase("D176") || codiUdL.contains("D176")) {
			result = "D1022 Situacions administratives";
			
		}else if(codiUdL.equalsIgnoreCase("D177") || codiUdL.contains("D177")) {
			result = "D1022 Situacions administratives";
			
		}else if(codiUdL.equalsIgnoreCase("D178") || codiUdL.contains("D178")) {
			result = "D1022 Situacions administratives";
			
		}else if(codiUdL.equalsIgnoreCase("D179") || codiUdL.contains("D179")) {
			result = "D1023 Comissió de serveis";
			
		}else if(codiUdL.equalsIgnoreCase("D180") || codiUdL.contains("D180")) {
			result = "D1022 Situacions administratives";
			
		}else if(codiUdL.equalsIgnoreCase("D181") || codiUdL.contains("D181")) {
			result = "D1026 Excedències";
			
		}else if(codiUdL.equalsIgnoreCase("D182") || codiUdL.contains("D182")) {
			result = "D1024 Serveis especials";
			
		}else if(codiUdL.equalsIgnoreCase("D183") || codiUdL.contains("D183")) {
			result = "D1031 Suspensió de funcions";
			
		}else if(codiUdL.equalsIgnoreCase("D184") || codiUdL.contains("D184")) {
			result = "D1022 Situacions administratives";
			
		}else if(codiUdL.equalsIgnoreCase("D195")) {
			result = "D1025 Serveis en altres Administracions Públiques";
			
		}else if(codiUdL.equalsIgnoreCase("D185") || codiUdL.contains("D185")) {
			result = "D1037 Pèrdua de la relació de servei";
		

		//
		// F100
		//
		}else if(codiUdL.equalsIgnoreCase("F100")) {
			result = "E1000 GESTIÓ DELS RECURSOS ECONÒMICS";
			
		}else if(codiUdL.equalsIgnoreCase("F101")) {
			result = "E1001 Programació econòmica";
			
		}else if(codiUdL.equalsIgnoreCase("F138")) {
			result = "E1002 Programació plurianual";
			
		}else if(codiUdL.equalsIgnoreCase("F102")) {
			result = "E1003 Pressupost de la Universitat";
			
		}else if(codiUdL.equalsIgnoreCase("F103")) {
			result = "E1004 Avantprojecte de pressupost";
			
		}else if(codiUdL.equalsIgnoreCase("F104")) {
			result = "E1005 Estimació d'ingressos i despeses";
			
		}else if(codiUdL.equalsIgnoreCase("F105")) {
			result = "E1005 Estimació d'ingressos i despeses";
			
		}else if(codiUdL.equalsIgnoreCase("F106")) {
			result = "E1005 Estimació d'ingressos i despeses";
			
		}else if(codiUdL.equalsIgnoreCase("F107")) {
			result = "E1006 Avantprojectes de les unitats estructurals";
			
		}else if(codiUdL.equalsIgnoreCase("F108")) {
			result = "E1006 Avantprojecte de les unitats estructurals";
			
		}else if(codiUdL.equalsIgnoreCase("F109")) {
			result = "E1006 Avantprojecte de les unitats estructurals";
			
		}else if(codiUdL.equalsIgnoreCase("F110")) {
			result = "E1005 Estimació d'ingressos i despeses";
			
		}else if(codiUdL.equalsIgnoreCase("F139")) {
			result = "E1007 Inversions";
			
		}else if(codiUdL.equalsIgnoreCase("F111")) {
			result = "E1009 Esmenes al pressupost";
			
		}else if(codiUdL.equalsIgnoreCase("F112")) {
			result = "E1010 Pressupost aprovat";
			
		}else if(codiUdL.equalsIgnoreCase("F113")) {
			result = "E1011 Prorroga del pressupost ";
			
		}else if(codiUdL.equalsIgnoreCase("F114")) {
			result = "E1012 Modificació del pressupost";
			
		}else if(codiUdL.equalsIgnoreCase("F115")) {
			result = "E1018 Estats d'execució del pressupost";
			
		}else if(codiUdL.equalsIgnoreCase("F116")) {
			result = "E1026 Gestió dels ingressos";
			
		}else if(codiUdL.equalsIgnoreCase("F116 E1")) {
			result = "E1034 Reconeixement de dret (RD)";
			
		}else if(codiUdL.equalsIgnoreCase("F116 E2")) {
			result = "E1036 Manaments d'ingrés (MI)";
			
		}else if(codiUdL.equalsIgnoreCase("F116 E3")) {
			result = "E1037 Devolució d'ingressos indeguts";
			
		}else if(codiUdL.equalsIgnoreCase("F140")) {
			result = "E1030 Activitats i prestacions";
			
		}else if(codiUdL.equalsIgnoreCase("F141")) {
			result = "E1031 Factures de la Universitat";
			
		}else if(codiUdL.equalsIgnoreCase("F117")) {
			result = "E1027 Preus públics / taxes";
			
		}else if(codiUdL.equalsIgnoreCase("F118")) {
			result = "E1026 Gestió dels ingressos";
			
		}else if(codiUdL.equalsIgnoreCase("F119")) {
			result = "E1032 Altres ingressos";
			
		}else if(codiUdL.equalsIgnoreCase("F120 E1")) {
			result = "E1033 Documents comptables dels ingressos";
			
		}else if(codiUdL.equalsIgnoreCase("F120 E2")) {
			result = "E1033 Documents comptables dels ingressos";
			
		}else if(codiUdL.equalsIgnoreCase("F121")) {
			result = "E1039 Gestió de les despeses";
			
		}else if(codiUdL.equalsIgnoreCase("F122 E1")) {
			result = "E1042 Documents comptables de la despesa";
			
		}else if(codiUdL.equalsIgnoreCase("F122 E2")) {
			result = "E1042 Documents comptables de la despesa";
			
		}else if(codiUdL.equalsIgnoreCase("F122 E3")) {
			result = "E1042 Documents comptables de la despesa";
			
		}else if(codiUdL.equalsIgnoreCase("F122 E4")) {
			result = "E1048 Ordre de pagament (OP)";
			
		}else if(codiUdL.equalsIgnoreCase("F122 E5")) {
			result = "E1052 Gestió extrapressupostària";
			
		}else if(codiUdL.equalsIgnoreCase("F122 E6")) {
			result = "E1042 Documents comptables de la despesa";
			
		}else if(codiUdL.equalsIgnoreCase("F122 E8")) {
			result = "E1042 Documents comptables de la despesa";
			
		}else if(codiUdL.equalsIgnoreCase("F123")) {
			result = "E1039 Gestió de les despeses";
			
		}else if(codiUdL.equalsIgnoreCase("F124 E1")) {
			result = "E1039 Gestió de les despeses";
			
		}else if(codiUdL.equalsIgnoreCase("F124 E2")) {
			result = "E1039 Gestió de les despeses";
			
		}else if(codiUdL.equalsIgnoreCase("F125")) {
			result = "E1039 Gestió de les despeses";
			
		}else if(codiUdL.equalsIgnoreCase("F126")) {
			result = "E1070 Contractació";
			
		}else if(codiUdL.equalsIgnoreCase("F126 E1")) {
			result = "E1088 Contractes de treballs específics, concrets no habituals";
			
		}else if(codiUdL.equalsIgnoreCase("F126 E2")) {
			result = "E1076 Contractes de subministrament";
			
		}else if(codiUdL.equalsIgnoreCase("F126 E3")) {
			result = "E1071 Contractes d'obres";
			
		}else if(codiUdL.equalsIgnoreCase("F126 E4")) {
			result = "E1079 Contractes de serveis";
			
		}else if(codiUdL.equalsIgnoreCase("F127")) {
			result = "E1019 Liquidació del pressupost";
			
		}else if(codiUdL.equalsIgnoreCase("F128")) {
			result = "E1020 Liquidació pressupostaria";
			
		}else if(codiUdL.equalsIgnoreCase("F142")) {
			result = "E1021 Deutors i creditors";
			
		}else if(codiUdL.equalsIgnoreCase("F129")) {
			result = "E1022 Comptes anuals";
			
		}else if(codiUdL.equalsIgnoreCase("F143")) {
			result = "E1023 Comptabilitat pressupostaria";
			
		}else if(codiUdL.equalsIgnoreCase("F130")) {
			result = "E1024 Comptabilitat financera ";

		}else if(codiUdL.equalsIgnoreCase("F144")) {
			result = "E1025 Compte general";
			
		}else if(codiUdL.equalsIgnoreCase("F131")) {
			result = "E1055 Gestió de tresoreria";
			
		}else if(codiUdL.equalsIgnoreCase("F132")) {
			result = "E1059 Comptes bancaris";
			
		}else if(codiUdL.equalsIgnoreCase("F133")) {
			result = "E1056 Previsions de tresoreria";

		}else if(codiUdL.equalsIgnoreCase("F134")) {
			result = "E1066 Obligacions fiscals";
			
		}else if(codiUdL.equalsIgnoreCase("F135")) {
			result = "E1067 Liquidacions d'IVA";
			
		}else if(codiUdL.equalsIgnoreCase("F136")) {
			result = "E1068 Liquidacions d'IRPF";
			
		}else if(codiUdL.equalsIgnoreCase("F145")) {
			result = "E1063 Fiscalització";
			
		}else if(codiUdL.equalsIgnoreCase("F137")) {
			result = "E1065 Auditories";
			
		}else if(codiUdL.equalsIgnoreCase("F146")) {
			result = "E1064 Control de fiscalització";
			
			
		//
		// G100
		//
		}else if(codiUdL.equalsIgnoreCase("G100")) {
			result = "G1000 Gestió dels béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G101") || codiUdL.contains("G101")) {
			result = "G1010 Adquisició de l'ús de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G102")) {
			result = "G1001 Adquisició en propietat de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G103")) {
			result = "G1010 Adquisició de l'ús de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G119")) {
			result = "G1002 Compra de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G104") || codiUdL.contains("G104")) {
			result = "G1012 Lloguer de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G105") || codiUdL.contains("G105")) {
			result = "G1009 Permuta de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G106") || codiUdL.contains("G106")) {
			result = "G1011 Cessió gratuïta / Dret d'ús de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G107") || codiUdL.contains("G107")) {
			result = "G1008 Donació de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G108") || codiUdL.contains("G108")) {
			result = "G1006 Dossier de proveïdors";
			
		}else if(codiUdL.equalsIgnoreCase("G109") || codiUdL.contains("G109")) {
			result = "G1027 Inventari de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G110") || codiUdL.contains("G110")) {
			result = "G1030 Utilització i manteniment de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G111") || codiUdL.contains("G111")) {
			result = "G1031 Manteniment de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G112")) {
			result = "G1032 Transport i muntatge de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G113")) {
			result = "G1030 Utilització i manteniment de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G114") || codiUdL.contains("G114")) {
			result = "G1014 Desafectació de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G115") || codiUdL.contains("G115")) {
			result = "G1015 Cessió d'ús dels béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G116") || codiUdL.contains("G116")) {
			result = "G1026 Eliminació de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G117")) {
			result = "G1030 Utilització i manteniment de béns mobles";
			
		}else if(codiUdL.equalsIgnoreCase("G118") || codiUdL.contains("G118")) {
			result = "G1033 Assegurances i garanties de béns mobles";

			
		//
		// H100
		//
		}else if(codiUdL.equalsIgnoreCase("H100")) {
			result = "F1000 GESTIÓ DELS BÉNS IMMOBLES";
			
		}else if(codiUdL.equalsIgnoreCase("H101")) {
			result = "F1001 Adquisició en propietat de béns immobles";
			
		}else if(codiUdL.equalsIgnoreCase("H102")) {
			result = "F1001 Adquisició en propietat de béns immobles";
			
		}else if(codiUdL.equalsIgnoreCase("H103")) {
			result = "F1002 Compra de béns immobles";
			
		}else if(codiUdL.equalsIgnoreCase("H104")) {
			result = "F1006 Donació de béns immobles";
			
		}else if(codiUdL.equalsIgnoreCase("H105")) {
			result = "F1010 Cessió gratuïta / dret d'ús de béns immobles";
			
		}else if(codiUdL.equalsIgnoreCase("H106")) {
			result = "F1007 Permuta de béns immobles ";
			
		}else if(codiUdL.equalsIgnoreCase("H107")) {
			result = "F1008 Expropiació de béns immobles";
			
		}else if(codiUdL.equalsIgnoreCase("H108")) {
			result = "F1011 Arrendament de béns immobles";
			
		}else if(codiUdL.equalsIgnoreCase("H109 E1")) {
			result = "F1013 Construcció i adequació";
			
		}else if(codiUdL.equalsIgnoreCase("H109 E2")) {
			result = "F1013 Construcció i adequació";
			
		}else if(codiUdL.equalsIgnoreCase("H109 E3")) {
			result = "F1013 Construcció i adequació";
			
		}else if(codiUdL.equalsIgnoreCase("H110")) {
			result = "F1013 Construcció i adequació";
			
		}else if(codiUdL.equalsIgnoreCase("H111")) {
			result = "F1013 Construcció i adequació";
			
		}else if(codiUdL.equalsIgnoreCase("H112")) {
			result = "F1013 Construcció i adequació";
			
		}else if(codiUdL.equalsIgnoreCase("H113")) {
			result = "F1014 Obra principal";

		}else if(codiUdL.equalsIgnoreCase("H114")) {
			result = "F1029 Inventari de béns immobles";

		}else if(codiUdL.equalsIgnoreCase("H115")) {
			result = "F1033 Manteniment dels béns immobles";

		}else if(codiUdL.equalsIgnoreCase("H116")) {
			result = "F1033 Manteniment dels béns immobles";

		}else if(codiUdL.equalsIgnoreCase("H116 E1")) {
			result = "F1067 Serveis de neteja";
			
		}else if(codiUdL.equalsIgnoreCase("H1116 E2")) {
			result = "F1052 Aigua";
			
		}else if(codiUdL.equalsIgnoreCase("H1116 E3")) {
			result = "F1053 Electricitat";
			
		}else if(codiUdL.equalsIgnoreCase("H1116 E4")) {
			result = "F1055 Calefacció";
			
		}else if(codiUdL.equalsIgnoreCase("H1116 E5")) {
			result = "F1062 Aparells elevadors";
			
		}else if(codiUdL.equalsIgnoreCase("H1116 E6")) {
			result = "F1036 Espais verds";
			
		}else if(codiUdL.equalsIgnoreCase("H1116 E7")) {
			result = "F1033 Manteniment dels béns immobles" ;
			
		}else if(codiUdL.equalsIgnoreCase("H1116 E8")) {
			result = "F1069 Eliminació de residus";
			
		}else if(codiUdL.equalsIgnoreCase("H1116 E9")) {
			result = "F1033 Manteniment dels béns immobles";
			
		}else if(codiUdL.equalsIgnoreCase("H1116 E10")) {
			result = "F1064 Sanejament interior";
			
		}else if(codiUdL.equalsIgnoreCase("H1116 E11")) {
			result = "F1060 Instal·lacions contra incendis";
			
		}else if(codiUdL.equalsIgnoreCase("H117")) {
			result = "F1090 Assegurances i seguretat";
			
		}else if(codiUdL.equalsIgnoreCase("H118")) {
			result = "F1091 Assegurances i garanties";
			
		}else if(codiUdL.equalsIgnoreCase("H119")) {
			result = "F1085 Qualitat mediambiental / Ambientalització";
			
		}else if(codiUdL.equalsIgnoreCase("H120")) {
			result = "F1075 Ús dels béns immobles";
			
		}else if(codiUdL.equalsIgnoreCase("H121")) {
			result = "F1075 Ús de béns immobles";
			
		}else if(codiUdL.equalsIgnoreCase("H122")) {
			result = "F1017 Cessió d'ús dels béns immobles";
			
		}else if(codiUdL.equalsIgnoreCase("H123")) {
			result = "F1022 Venda de béns immobles";
			
		}else if(codiUdL.equalsIgnoreCase("H124")) {
			result = "F1016 Desafectació de béns immobles";
			
		}else if(codiUdL.equalsIgnoreCase("H125")) {
			result = "F1018 Cessió per arrendament";
			
		}else if(codiUdL.equalsIgnoreCase("H126")) {
			result = "F1025 Traspàs de béns immobles";
			
			
		//
		// I100
		//
		}else if(codiUdL.equalsIgnoreCase("I100")) {
			result = "H1000 LEGISLACIÓ I ASSUMPTES JURÍDICS";
			
		}else if(codiUdL.equalsIgnoreCase("I101")) {
			result = "H1001 Normativa de la Universitat";
			
		}else if(codiUdL.equalsIgnoreCase("I102")) {
			result = "H1002 Estatuts de la Universitat";
			
		}else if(codiUdL.equalsIgnoreCase("I103")) {
			result = "H1003 Reglaments de la Universitat";
			
		}else if(codiUdL.equalsIgnoreCase("I104")) {
			result = "H1013 Aplicació de la normativa";
			
		}else if(codiUdL.equalsIgnoreCase("I105")) {
			result = "H1014 Assessorament jurídic";

		}else if(codiUdL.equalsIgnoreCase("I106")) {
			result = "H1021 Processos judicials";

		}else if(codiUdL.equalsIgnoreCase("I106 E1")) {
			result = "H1022 Contenciós administratiu";
			
		}else if(codiUdL.equalsIgnoreCase("I106 E2")) {
			result = "H1023 Social";
			
		}else if(codiUdL.equalsIgnoreCase("I106 E3")) {
			result = "H1024 Civil";
			
		}else if(codiUdL.equalsIgnoreCase("I106 E4")) {
			result = "H1025 Penal";
			
		}else if(codiUdL.equalsIgnoreCase("I106 E5")) {
			result = "H1021 Processos judicials";
			
		}else if(codiUdL.equalsIgnoreCase("I107") || codiUdL.contains("I107")) {
			result = "H1016 Recursos administratius";
			
		}else if(codiUdL.equalsIgnoreCase("I108")) {
			result = "H1015 Revisió dels actes administratius";
			
		}else if(codiUdL.equalsIgnoreCase("I109")) {
			result = "H1020 Revisió d'ofici";
			
		}else if(codiUdL.equalsIgnoreCase("I121")) {
			result = "A1043 Competència administrativa";
			
		}else if(codiUdL.equalsIgnoreCase("I122")) {
			result = "A1044 Delegació i avocació";
			
		}else if(codiUdL.equalsIgnoreCase("I123")) {
			result = "A1045 Encàrrecs de gestió";
			
		}else if(codiUdL.equalsIgnoreCase("I124")) {
			result = "A1046 Autorització de signatura";
			
		}else if(codiUdL.equalsIgnoreCase("I110")) {
			result = "H1027 Inscripcions en Registres Oficials";
			
		}else if(codiUdL.equalsIgnoreCase("I111")) {
			result = "H1028 Registre de la propietat";
			
		}else if(codiUdL.equalsIgnoreCase("I112")) {
			result = "H1030 Registre de la propietat intel·lectual";
			
		}else if(codiUdL.equalsIgnoreCase("I113")) {
			result = "H1033 Registre d'entitats jurídiques i de dret";
			
		}else if(codiUdL.equalsIgnoreCase("I114")) {
			result = "H1031 Registre de patents i marques";
			
		}else if(codiUdL.equalsIgnoreCase("I125")) {
			result = "H1029 Registre mercantil";
			
		}else if(codiUdL.equalsIgnoreCase("I126")) {
			result = "H1027 Inscripcions en Registres Oficials";
			
		}else if(codiUdL.equalsIgnoreCase("I127")) {
			result = "H1027 Inscripcions en Registres Oficials";
			
		}else if(codiUdL.equalsIgnoreCase("I115") || codiUdL.contains("I115")) {
			result = "A1019 Síndic de la Universitat";
			
		}else if(codiUdL.equalsIgnoreCase("I116")) {
			result = "A1020 Expedients queixes";
			
		}else if(codiUdL.equalsIgnoreCase("I117")) {
			result = "A1019 Síndic de la universitat";
			
		}else if(codiUdL.equalsIgnoreCase("I118")) {
			result = "A1023 Intervenció d’ofici";
			
		}else if(codiUdL.equalsIgnoreCase("I119")) {
			result = "A1019 Síndic de la universitat";
			
		}else if(codiUdL.equalsIgnoreCase("I120")) {
			result = "A1024 Consultes";
			
			
		//
		// J100
		//
		}else if(codiUdL.equalsIgnoreCase("J100")) {
			result = "J1000 GESTIÓ ACADÈMICA";
			
		}else if(codiUdL.equalsIgnoreCase("J101")) {
			result = "J1001 Accés a la Universitat";
			
		}else if(codiUdL.equalsIgnoreCase("J102")) {
			result = "J1001 Accés a la Universitat";
			
		}else if(codiUdL.equalsIgnoreCase("J103")) {
			result = "J1026 Convalidacions";
			
		}else if(codiUdL.equalsIgnoreCase("J104") || codiUdL.contains("J104")) {
			result = "J1005 Proves d'accés";
			
		}else if(codiUdL.equalsIgnoreCase("J105") || codiUdL.contains("J105")) {
			result = "J1014 Preinscripció universitària";
			
		}else if(codiUdL.equalsIgnoreCase("J106") || codiUdL.contains("J106")) {
			result = "J1024 Matriculació";
			
		}else if(codiUdL.equalsIgnoreCase("J107") || codiUdL.contains("J107")) {
			result = "J1024 Matriculació";
			
		}else if(codiUdL.equalsIgnoreCase("J108") || codiUdL.contains("J108")) {
			result = "J1024 Matriculació";
			
		}else if(codiUdL.equalsIgnoreCase("J131")) {
			result = "J1030 Adaptacions";
			
		}else if(codiUdL.equalsIgnoreCase("J109")  || codiUdL.contains("J109")) {
			result = "J1028 Homologacions";
			
		}else if(codiUdL.equalsIgnoreCase("J110") || codiUdL.contains("J110")) {
			result = "J1024 Matriculació";
			
		}else if(codiUdL.equalsIgnoreCase("J111") || codiUdL.contains("J111")) {
			result = "J1031 Anul·lacions";
			
		}else if(codiUdL.equalsIgnoreCase("J112") || codiUdL.contains("J112")) {
			result = "J1024 Matriculació";
			
		}else if(codiUdL.equalsIgnoreCase("J113") || codiUdL.contains("J113")) {
			result = "J1024 Matriculació";
			
		}else if(codiUdL.equalsIgnoreCase("J114") || codiUdL.contains("J114")) {
			result = "J1024 Matriculació";
			
		}else if(codiUdL.equalsIgnoreCase("J115") || codiUdL.contains("J115")) {
			result = "J1033 Expedients acadèmics";
			
		}else if(codiUdL.equalsIgnoreCase("J116") || codiUdL.contains("J116")) {
			result = "M1030 Actes de qualificacions";
			
		}else if(codiUdL.equalsIgnoreCase("J117") || codiUdL.contains("J117")) {
			result = "M1030 Actes de qualificacions";
			
		}else if(codiUdL.equalsIgnoreCase("J118") || codiUdL.contains("J118")) {
			result = "J1037 Premis extraordinsari";
			
		}else if(codiUdL.equalsIgnoreCase("J119") || codiUdL.contains("J119")) {
			result = "J1040 Beques i ajuts";
			
		}else if(codiUdL.equalsIgnoreCase("J120") || codiUdL.contains("J120")) {
			result = "J1040 Beques i ajuts";
			
		}else if(codiUdL.equalsIgnoreCase("J121") || codiUdL.contains("J121")) {
			result = "J1041 Beques d'estudi";
			
		}else if(codiUdL.equalsIgnoreCase("J122") || codiUdL.contains("J122")) {
			result = "J1042 Beques de col·laboració";
			
		}else if(codiUdL.equalsIgnoreCase("J123") || codiUdL.contains("J123")) {
			result = "J1043 Beques d'intercanvi i mobilitat";
			
		}else if(codiUdL.equalsIgnoreCase("J124") || codiUdL.contains("J124")) {
			result = "J1041 Beques d'estudi";
			
		}else if(codiUdL.equalsIgnoreCase("J125") || codiUdL.contains("J125")) {
			result = "J1041 Beques d'estudi";
			
		}else if(codiUdL.equalsIgnoreCase("J126") || codiUdL.contains("J126")) {
			result = "J1045 Expedició de títols";
			
		}else if(codiUdL.equalsIgnoreCase("J127") || codiUdL.contains("J127")) {
			result = "J1046 Titols oficials";
			
		}else if(codiUdL.equalsIgnoreCase("J128") || codiUdL.contains("J128")) {
			result = "J1045 Expedició de títols";
			
		}else if(codiUdL.equalsIgnoreCase("J129") || codiUdL.contains("J129")) {
			result = "M1028 Tesis";
			
		}else if(codiUdL.equalsIgnoreCase("J130")) {
			result = "J1024 Matriculació";

			
			
		//
		// K100
		//
		}else if(codiUdL.equalsIgnoreCase("K100")) {
			result = "M1000 ORDENACIÓ DE LA DOCÈNCIA";
			
		}else if(codiUdL.equalsIgnoreCase("K101") || codiUdL.contains("K101")) {
			result = "M1001 Programació universitària";
			
		}else if(codiUdL.equalsIgnoreCase("K102") || codiUdL.contains("K102")) {
			result = "M1020 Oferta docent";
			
		}else if(codiUdL.equalsIgnoreCase("K103") || codiUdL.contains("K103")) {
			result = "M1002 Autorització";
			
		}else if(codiUdL.equalsIgnoreCase("K104")) {
			result = "M1006 Plans i programes d'estudis";
			
		}else if(codiUdL.equalsIgnoreCase("K145") || codiUdL.contains("K145")) {
			result = "M1004 Verificació";
			
		}else if(codiUdL.equalsIgnoreCase("K146") || codiUdL.contains("K146")) {
			result = "M1005 Acreditació";
			
		}else if(codiUdL.equalsIgnoreCase("K105") || codiUdL.contains("K105")) {
			result = "M1003 Homologació";
			
		}else if(codiUdL.equalsIgnoreCase("K106")) {
			result = "A1003 Creació, modificació i supressió";
			
		}else if(codiUdL.equalsIgnoreCase("K107")) {
			result = "A1004 Integració";
			
		}else if(codiUdL.equalsIgnoreCase("K108")) {
			result = "A1005 Adscripció i/o desadscripció";
			
		}else if(codiUdL.equalsIgnoreCase("K109") || codiUdL.contains("K109")) {
			result = "M1021 Calendari acadèmic";
			
		}else if(codiUdL.equalsIgnoreCase("K110") || codiUdL.contains("K110")) {
			result = "M1006 Plans i programes d'estudis";
			
		}else if(codiUdL.equalsIgnoreCase("K111") || codiUdL.contains("K111")) {
			result = "M1007 Directrius";
			
		}else if(codiUdL.equalsIgnoreCase("K112") || codiUdL.contains("K112")) {
			result = "M1008 Assignatures";
			
		}else if(codiUdL.equalsIgnoreCase("K113") || codiUdL.contains("K113")) {
			result = "M1011 Complements de formació";
			
		}else if(codiUdL.equalsIgnoreCase("K114") || codiUdL.contains("K114")) {
			result = "M1012 Formació en pràctiques";
			
		}else if(codiUdL.equalsIgnoreCase("K115") || codiUdL.contains("K115")) {
			result = "M1019 Pla docent";
			
		}else if(codiUdL.equalsIgnoreCase("K116") || codiUdL.contains("K116")) {
			result = "M1010 Intercanvis";
			
		}else if(codiUdL.equalsIgnoreCase("K117") || codiUdL.contains("K117")) {
			result = "M1019 Pla docent";
			
		}else if(codiUdL.equalsIgnoreCase("K118") || codiUdL.contains("K118")) {
			result = "M1048 Extensió de la docència";
			
		}else if(codiUdL.equalsIgnoreCase("K119") || codiUdL.contains("K119")) {
			result = "M1022 Horaris docents i tutories";
			
		}else if(codiUdL.equalsIgnoreCase("K120") || codiUdL.contains("K120")) {
			result = "M1048 Extensió de la docència";
			
		}else if(codiUdL.equalsIgnoreCase("K121") || codiUdL.contains("K121")) {
			result = "M1048 Extensió de la docència";
			
		}else if(codiUdL.equalsIgnoreCase("K122") || codiUdL.contains("K122")) {
			result = "M1048 Extensió de la docència";
			
		}else if(codiUdL.equalsIgnoreCase("K123") || codiUdL.contains("K123")) {
			result = "M1023 Relacions i fitxes d'estudiants";
			
		}else if(codiUdL.equalsIgnoreCase("K124") || codiUdL.contains("K124")) {
			result = "M1023 Relacions i fitxes d'estudiants";
			
		}else if(codiUdL.equalsIgnoreCase("K125") || codiUdL.contains("K125")) {
			result = "M1048 Extensió de la docència";
			
		}else if(codiUdL.equalsIgnoreCase("K126") || codiUdL.contains("K126")) {
			result = "M1024 Avaluació acadèmica i docent";
			
		}else if(codiUdL.equalsIgnoreCase("K127") || codiUdL.contains("K127")) {
			result = "M1026 Exàmens i treballs";
			
		}else if(codiUdL.equalsIgnoreCase("K128") || codiUdL.contains("K128")) {
			result = "M1029 Revisions de l'avaluació";
			
		}else if(codiUdL.equalsIgnoreCase("K129") || codiUdL.contains("K129")) {
			result = "M1013 Règim d epermanència¡a";
			
		}else if(codiUdL.equalsIgnoreCase("K147") || codiUdL.contains("K147")) {
			result = "M1013 Règim de permanència";
			
		}else if(codiUdL.equalsIgnoreCase("K130") || codiUdL.contains("K130")) {
			result = "M1026 Exàmens i treballs";
			
		}else if(codiUdL.equalsIgnoreCase("K131") || codiUdL.contains("K131")) {
			result = "M1013 Règim d epermanència";
			
		}else if(codiUdL.equalsIgnoreCase("K132") || codiUdL.contains("K132")) {
			result = "M1024 Avaluació acadèmica i docent";
			
		}else if(codiUdL.equalsIgnoreCase("K133") || codiUdL.contains("K133")) {
			result = "M1035 Avaluació del professorat";
			
		}else if(codiUdL.equalsIgnoreCase("K134") || codiUdL.contains("K134")) {
			result = "M1036 Qualitat i innovació docent";
			
		}else if(codiUdL.equalsIgnoreCase("K135") || codiUdL.contains("K135")) {
			result = "M1014 Cooperació interuniversitària";
			
		}else if(codiUdL.equalsIgnoreCase("K136") || codiUdL.contains("K136")) {
			result = "M1015 Intercanvis i mobilitat";
			
		}else if(codiUdL.equalsIgnoreCase("K148")) {
			result = "M1016 Programes europeus";
			
		}else if(codiUdL.equalsIgnoreCase("K149")) {
			result = "M1017 Altres programes";
			
		}else if(codiUdL.equalsIgnoreCase("K137") || codiUdL.contains("K137")) {
			result = "M1015 Intercanvis i mobilitat";
			
		}else if(codiUdL.equalsIgnoreCase("K138") || codiUdL.contains("K138")) {
			result = "C1044 Cooperació al desenvolupament";
			
		}else if(codiUdL.equalsIgnoreCase("K139") || codiUdL.contains("K139")) {
			result = "M1015 Intercanvis i mobilitat";
			
		}else if(codiUdL.equalsIgnoreCase("K140") || codiUdL.contains("K140")) {
			result = "M1048 Extensió de la docència";
			
		}else if(codiUdL.equalsIgnoreCase("K141") || codiUdL.contains("K141")) {
			result = "M1040 Formació i assessorament";
			
		}else if(codiUdL.equalsIgnoreCase("K142") || codiUdL.contains("K142")) {
			result = "M1045 Innovació pedagògica";
			
		}else if(codiUdL.equalsIgnoreCase("K143") || codiUdL.contains("K143")) {
			result = "M1044 Assessorament pedagògic";
			
		}else if(codiUdL.equalsIgnoreCase("K144") || codiUdL.contains("K144")) {
			result = "M1048 Extensió de la docència";
			
			
		//
		// L100
		//
		}else if(codiUdL.equalsIgnoreCase("L100")) {
			result = "N1000 RECERCA";
			
		}else if(codiUdL.equalsIgnoreCase("L101")) {
			result = "N1001 Organització de la recerca";
			
		}else if(codiUdL.equalsIgnoreCase("L115")) {
			result = "N1002 Política científica";
			
		}else if(codiUdL.equalsIgnoreCase("L116")) {
			result = "N1003 Organismes de gestió de la recerca";
			
		}else if(codiUdL.equalsIgnoreCase("L102")) {
			result = "N1004 Grups de recerca i investigadors";
			
		}else if(codiUdL.equalsIgnoreCase("L102 E1")) {
			result = "N1004 Grups de recerca i investigadors";
			
		}else if(codiUdL.equalsIgnoreCase("L102 E2")) {
			result = "N1004 Grups de recerca i investigadors";
			
		}else if(codiUdL.equalsIgnoreCase("L102 E3")) {
			result = "N1004 Grups de recerca i investigadors";
			
		}else if(codiUdL.equalsIgnoreCase("L102 E4")) {
			result = "N1004 Grups de recerca i investigadors";
			
		}else if(codiUdL.equalsIgnoreCase("L102 E5")) {
			result = "N1004 Grups de recerca i investigadors";
			
		}else if(codiUdL.equalsIgnoreCase("L102 E6")) {
			result = "N1004 Grups de recerca i investigadors";
			
		}else if(codiUdL.equalsIgnoreCase("L102 E7")) {
			result = "N1004 Grups de recerca i investigadors";
			
		}else if(codiUdL.equalsIgnoreCase("L103")) {
			result = "N1011 Incorporació de personal";
			
		}else if(codiUdL.equalsIgnoreCase("L103 E1")) {
			result = "N1011 Incorporació de personal";
			
		}else if(codiUdL.equalsIgnoreCase("L103 E2")) {
			result = "N1011 Incorporació de personal";
			
		}else if(codiUdL.equalsIgnoreCase("L103 E3")) {
			result = "N1011 Incorporació de personal";
			
		}else if(codiUdL.equalsIgnoreCase("L103 E4")) {
			result = "N1011 Incorporació de personal";
			
		}else if(codiUdL.equalsIgnoreCase("L103 E5")) {
			result = "N1011 Incorporació de personal";
			
		}else if(codiUdL.equalsIgnoreCase("L103 E6")) {
			result = "N1011 Incorporació de personal";
			
		}else if(codiUdL.equalsIgnoreCase("L105")) {
			result = "N1006 Finançament competitiu";
			
		}else if(codiUdL.equalsIgnoreCase("L106")) {
			result = "N1006 Finançament competitiu";
			
		}else if(codiUdL.equalsIgnoreCase("L107")) {
			result = "N1015 Transferència de tecnologia";
			
		}else if(codiUdL.equalsIgnoreCase("L107 E1")) {
			result = "N1016 Contractes de recerca";

		}else if(codiUdL.equalsIgnoreCase("L107 E2")) {
			result = "N1017 Prestacions i altres serveis de recerca";
			
		}else if(codiUdL.equalsIgnoreCase("L107 E3")) {
			result = "N1017 Prestacions i altres serveis de recerca";
			
		}else if(codiUdL.equalsIgnoreCase("L108")) {
			result = "N1006 Finançament competitiu";
			
		}else if(codiUdL.equalsIgnoreCase("L108 E1")) {
			result = "N1006 Finançament competitiu";
			
		}else if(codiUdL.equalsIgnoreCase("L108 E2")) {
			result = "N1006 Finançament competitiu";
			
		}else if(codiUdL.equalsIgnoreCase("L108 E3")) {
			result = "N1006 Finançament competitiu";
			
		}else if(codiUdL.equalsIgnoreCase("L108 E4")) {
			result = "N1006 Finançament competitiu";
			
		}else if(codiUdL.equalsIgnoreCase("L108 E5")) {
			result = "N1006 Finançament competitiu";
			
		}else if(codiUdL.equalsIgnoreCase("L109")) {
			result = "N1009 Infraestructura";
			
		}else if(codiUdL.equalsIgnoreCase("L110")) {
			result = "N1012 Mobilitat d'investigadors";
			
		}else if(codiUdL.equalsIgnoreCase("L117")) {
			result = "N1007 Projectes";
			
		}else if(codiUdL.equalsIgnoreCase("L118")) {
			result = "N1008 Grups i xarxes";
			
		}else if(codiUdL.equalsIgnoreCase("L111")) {
			result = "N1010 Beques";
			
		}else if(codiUdL.equalsIgnoreCase("L119")) {
			result = "N1013 Premis de recerca";
			
		}else if(codiUdL.equalsIgnoreCase("L113")) {
			result = "N1021 Difusió de la recerca";
			
		}else if(codiUdL.equalsIgnoreCase("L114")) {
			result = "N1023 Avaluació de la recerca";
			
			
		//
		// M100
		//
		}else if(codiUdL.equalsIgnoreCase("M100")) {
			result = "K1000 SERVEIS I ACTIVITATS PER A LA COMUNITAT UNIVERSITÀRIA";
			
		}else if(codiUdL.equalsIgnoreCase("M101") || codiUdL.contains("M101")) {
			result = "K1001 Activitats culturals";

		}else if(codiUdL.equalsIgnoreCase("M102")) {
			result = "K1003 Teatre";
			
		}else if(codiUdL.equalsIgnoreCase("M103")) {
			result = "K1004 Música";
			
		}else if(codiUdL.equalsIgnoreCase("M104")) {
			result = "K1001 Activitats culturals";
			
		}else if(codiUdL.equalsIgnoreCase("M105")) {
			result = "K1001 Activitats culturals";
			
		}else if(codiUdL.equalsIgnoreCase("M106")) {
			result = "K1001 Activitats culturals";
			
		}else if(codiUdL.equalsIgnoreCase("M107")) {
			result = "K1002 Activitats audiovisuals";

		}else if(codiUdL.equalsIgnoreCase("M108")) {
			result = "K1001 Activitats culturals";

		}else if(codiUdL.equalsIgnoreCase("M109")) {
			result = "K1006 Activitats esportives";
			
		}else if(codiUdL.equalsIgnoreCase("M110")) {
			result = "K1007 Pràctica esportiva lliure";
			
		}else if(codiUdL.equalsIgnoreCase("M111")) {
			result = "K1006 Activitats esportives";
			
		}else if(codiUdL.equalsIgnoreCase("M112")) {
			result = "K1006 Activitats esportives";
			
		}else if(codiUdL.equalsIgnoreCase("M113")) {
			result = "K1008 Competicions";
			
		}else if(codiUdL.equalsIgnoreCase("M114")) {
			result = "K1006 Activitats esportives";
			
		}else if(codiUdL.equalsIgnoreCase("M115")) {
			result = "C1043 Programes i accions socials";
			
		}else if(codiUdL.equalsIgnoreCase("M116")) {
			result = "K1015 Serveis de suport a la comunitat";
			
		}else if(codiUdL.equalsIgnoreCase("M117")) {
			result = "K1016 Acolliment";
			
		}else if(codiUdL.equalsIgnoreCase("M118")) {
			result = "K1022 Promoció de l'ocupació";
			
		}else if(codiUdL.equalsIgnoreCase("M119")) {
			result = "K1023 Servei d'ocupació";
			
		}else if(codiUdL.equalsIgnoreCase("M120")) {
			result = "K1017 Borsa d'habitatge";
			
		}else if(codiUdL.equalsIgnoreCase("M121")) {
			result = "J1032 Identificació/ identitat dels estudiants";
			
		}else if(codiUdL.equalsIgnoreCase("M122")) {
			result = "M1048 Extensió de la docència";
			
		}else if(codiUdL.equalsIgnoreCase("M123")) {
			result = "K1010 Activitats socials";
			
		}else if(codiUdL.equalsIgnoreCase("M124")) {
			result = "J1050 Organització dels estudiants";
			
		}else if(codiUdL.equalsIgnoreCase("M125")) {
			result = "J1051 Representació dels estudiants";
			
		}else if(codiUdL.equalsIgnoreCase("M126")) {
			result = "J1052 Associacions dels estudiants";
			
		}else if(codiUdL.equalsIgnoreCase("M127")) {
			result = "J1052 Associacions dels estudiants";
			
		}else if(codiUdL.equalsIgnoreCase("M128")) {
			result = "H1034 Registre d'associacions de la Universitat";

		//
		// Si no hay equivalencia se deja el mismo
		//
		}else {
			result = codiUdL;
		}
		
		return result;
	}
}
