package es.cesca.alfresco.util;

import java.text.SimpleDateFormat;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;

public class CescaUtil {
	
	//PROCESS
	public final static String PROCESS_CERCA 			= "Cerca expedients";
	public final static String PROCESS_ENVIAMENT 		= "Enviament a iArxiu";
	public final static String PROCESS_ACTUALITZACIO 	= "Actualitzacio de peticions";
	
	//VAR
	public final static String MAIL_NO_REPLY = "no-reply@cesca.cat";
	public final static String SRV_HOME = "CESCA_FOLDER";
	public final static String LOG_EXTENSION = "_CESCA_LOG.txt";
	public final static String REPLACE_NUMEXP = "{numexp}";
	public final static String REPLACE_NUMDOC = "{num_doc}";
	public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	public final static SimpleDateFormat humanDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	//PATHS
	public final static String PATH_PETICIO 			= "+PATH:\"/app:company_home/cm:CESCA/cm:Peticions_iArxiu\"";
	public final static String PATH_REBUT 				= "+PATH:\"/app:company_home/cm:CESCA/cm:Rebuts_iArxiu\"";
	public final static String PATH_LOG	 				= "+PATH:\"/app:company_home/cm:CESCA/cm:Logs\"";
	public final static String PATH_VOCABULARI			= "+PATH:\"/app:company_home/cm:CESCA/cm:Vocabularis\"";
	public final static String PATH_ATRIBUTO			= "+PATH:\"/app:company_home/cm:CESCA/cm:Atributs\"";
	public final static String PATH_PLANTILLES			= "+PATH:\"/app:company_home/cm:CESCA/cm:Plantilles\"";
	public final static String PATH_EXTRACCIONS			= "+PATH:\"/app:company_home/cm:CESCA/cm:Extraccions\"";
	public final static String PATH_EXPEDIENTES			= "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Expedients\"";
	public final static String PATH_DOCUMENTS			= "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Documents\"";
	public final static String PATH_SIGNATURES			= "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Signatures\"";
	public final static String PATH_CONFIG				= "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Configuracio\"";
	public final static String PATH_DOCUMENTS_SENSE		= "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:DocumentsSenseExpedient\"";
	
	//QUERIES
	public final static String QUERY_MAIL_TEMPLATE 	= "+PATH:\"/app:company_home/cm:CESCA/cm:MailTemplates//.\" +TYPE:\"cesca:CESCA_MAIL_TEMPLATE\"";
	public final static String QUERY_CFG_EXP = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Expedients//.\" +TYPE:\"cesca:CESCA_CFG_EXPEDIENT\" +@cesca\\:cfg_expedient_en_suspens:false +@cesca\\:cfg_expedient_id_configuracio:\"{0}\"";
	public final static String QUERY_CFG_EXP_DEFAULT = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Expedients//.\" +TYPE:\"cesca:CESCA_CFG_EXPEDIENT\" +@cesca\\:cfg_expedient_en_suspens:false +@cesca\\:cfg_expedient_configuracio_per_defecte:true";
	public final static String QUERY_CFG_DOC = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Documents//.\" +TYPE:\"cesca:CESCA_CFG_DOCUMENT\" +@cesca\\:cfg_document_en_suspens:false +@cesca\\:_cfg_document_id_configuracio:\"{0}\"";
	public final static String QUERY_CFG_DOC_CON_EXP = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Documents//.\" +TYPE:\"cesca:CESCA_CFG_DOCUMENT\" +@cesca\\:peticio_docind:false +@cesca\\:cfg_document_en_suspens:false +@cesca\\:cfg_document_id_configuracio:\"{0}\"";
	public final static String QUERY_CFG_DOC_SIN_EXP = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Documents//.\" +TYPE:\"cesca:CESCA_CFG_DOCUMENT\" +@cesca\\:peticio_docind:true +@cesca\\:cfg_document_en_suspens:false +@cesca\\:cfg_document_id_configuracio:\"{0}\"";
	public final static String QUERY_CFG_DOC_SIN_EXP_DEFAULT = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Documents//.\" +TYPE:\"cesca:CESCA_CFG_DOCUMENT\" +@cesca\\:peticio_docind:true +@cesca\\:cfg_document_en_suspens:false +@cesca\\:cfg_document_configuracio_per_defecte:true";
	public final static String QUERY_CFG_SIG_CON_EXP = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Signatures//.\" +TYPE:\"cesca:CESCA_CFG_SIGNATURA\" +@cesca\\:peticiosign_docind:false +@cesca\\:cfg_signatura_en_suspens:false +@cesca\\:cfg_signatura_id_configuracio:\"{0}\"";
	public final static String QUERY_CFG_SIG_SIN_EXP = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:Signatures//.\" +TYPE:\"cesca:CESCA_CFG_SIGNATURA\" +@cesca\\:peticiosign_docind:true +@cesca\\:cfg_signatura_en_suspens:false +@cesca\\:cfg_signatura_id_configuracio:\"{0}\"";
	
	
	//ESTATS
	public final static String STATUS_PENDENT 		= "Pendent";
	public final static String STATUS_ENTRAMITACIO 	= "En tramitacio";//* no es un error ortografic, no canviar
	public final static String STATUS_TRANSFERIT 	= "Transferit";
	public final static String STATUS_ERROR 		= "Error";
	public final static String SEARCH_ALL           = "Tots";
	
	//RESULTS
	public static final String RESULT_REGISTRAT = "Enregistratcesca:";
	
	//MODEL
	public static StoreRef STORE 								= new StoreRef("workspace://SpacesStore");
	public final static String CESCA_MODEL_1_0_URI 				= "http://www.cesca.es/model";
	public static final QName TYPE_PETICIO 						= QName.createQName(CESCA_MODEL_1_0_URI, "CESCA_PETICIO_IARXIU");
	public static final QName TYPE_REBUT 						= QName.createQName(CESCA_MODEL_1_0_URI, "CESCA_REBUT_IARXIU");
	public static final QName TYPE_LOG 							= QName.createQName(CESCA_MODEL_1_0_URI, "CESCA_PROCES_LOG");
	public static final QName TYPE_VOCABULARI					= QName.createQName(CESCA_MODEL_1_0_URI, "CESCA_VOCABULARIO");
	public static final QName TYPE_ATRIBUTO						= QName.createQName(CESCA_MODEL_1_0_URI, "CESCA_ATRIBUTO");
	public static final QName TYPE_PLANTILLA					= QName.createQName(CESCA_MODEL_1_0_URI, "CESCA_PLANTILLA");
	public static final QName TYPE_EXTRACCIO					= QName.createQName(CESCA_MODEL_1_0_URI, "CESCA_EXTRACCIO");
	public static final QName TYPE_CONFIGURACIO					= QName.createQName(CESCA_MODEL_1_0_URI, "CESCA_CONFIGURACIO");
	public static final QName TYPE_DOCUMENT 					= QName.createQName(CESCA_MODEL_1_0_URI, "CESCA_CFG_DOCUMENT");
	public static final QName TYPE_DOCUMENT_SENSE				= QName.createQName(CESCA_MODEL_1_0_URI, "CESCA_CFG_DOCUMENT_SENSE_EXP");
	public static final QName TYPE_EXPEDIENT					= QName.createQName(CESCA_MODEL_1_0_URI, "CESCA_CFG_EXPEDIENT");
	public static final QName TYPE_SIGNATURA					= QName.createQName(CESCA_MODEL_1_0_URI, "CESCA_CFG_SIGNATURA");
		
	// cesca:CESCA_CONFIGURACIO Propietats
	public static final QName CONFIGURACIO_PROP_CRITERI_CERCA 	= QName.createQName(CESCA_MODEL_1_0_URI, "criteri_cerca");
	public static final QName CONFIGURACIO_PROP_EN_SUSPENS 		= QName.createQName(CESCA_MODEL_1_0_URI, "en_suspens");
	public static final QName CONFIGURACIO_PROP_PETICIO_DOC_EXP	= QName.createQName(CESCA_MODEL_1_0_URI, "peticio_docexp");
	public static final QName CONFIGURACIO_PROP_ID_CONFIG 		= QName.createQName(CESCA_MODEL_1_0_URI, "id_configuracio");

	// cesca:CESCA_CFG_EXPEDIENT Propietats
	public static final QName CFG_EXP_PROP_TIPUS_EXPEDIENT		= QName.createQName(CESCA_MODEL_1_0_URI, "tipus_expedient");
	public static final QName CFG_EXP_PROP_CRITERI_CERCA_DOCS 	= QName.createQName(CESCA_MODEL_1_0_URI, "cerca_documents");
	public static final QName CFG_EXP_PROP_EN_SUSPENS 			= QName.createQName(CESCA_MODEL_1_0_URI, "cfg_expedient_en_suspens");
	public static final QName CFG_EXP_PROP_ID_CONFIG 			= QName.createQName(CESCA_MODEL_1_0_URI, "cfg_expedient_id_configuracio");
	public static final QName CFG_EXP_PROP_DEFAULT_CONFIG 		= QName.createQName(CESCA_MODEL_1_0_URI, "cfg_expedient_configuracio_per_defecte");
	
	// cesca:CESCA_CFG_DOCUMENT Propietats
	public static final QName CFG_DOC_PROP_TIPUS_DOCUMENT		= QName.createQName(CESCA_MODEL_1_0_URI, "tipus_document");
	public static final QName CFG_DOC_PROP_CRITERI_CERCA_SIG 	= QName.createQName(CESCA_MODEL_1_0_URI, "cerca_signatures");
	public static final QName CFG_DOC_PROP_EN_SUSPENS 			= QName.createQName(CESCA_MODEL_1_0_URI, "cfg_document_en_suspens");
	public static final QName CFG_DOC_PROP_PETICIO_DOC_IND		= QName.createQName(CESCA_MODEL_1_0_URI, "peticio_docind");
	public static final QName CFG_DOC_PROP_ID_CONFIG 			= QName.createQName(CESCA_MODEL_1_0_URI, "cfg_document_id_configuracio");
	
	// cesca:CESCA_CFG_DOCUMENT_SENSE_EXP Propietats
	public static final QName CFG_DOC_PROP_CRITERI_CERCA_SIG_SENSE 	= QName.createQName(CESCA_MODEL_1_0_URI, "cerca_signatures_sense_exp");
	public static final QName CFG_DOC_PROP_EN_SUSPENS_SENSE			= QName.createQName(CESCA_MODEL_1_0_URI, "cfg_document_en_suspens_sense_exp");

	// cesca:CESCA_CFG_SIGNATURA Propietats
	public static final QName CFG_SIG_PROP_TIPUS_SIGNATURA		= QName.createQName(CESCA_MODEL_1_0_URI, "tipus_signatura");
	public static final QName CFG_SIG_PROP_EN_SUSPENS 			= QName.createQName(CESCA_MODEL_1_0_URI, "cfg_signatura_en_suspens");
	public static final QName CFG_SIG_PROP_PETICIO_SIGN_DOC_IND	= QName.createQName(CESCA_MODEL_1_0_URI, "peticiosign_docind");
	public static final QName CFG_SIG_PROP_ID_CONFIG 			= QName.createQName(CESCA_MODEL_1_0_URI, "cfg_signatura_id_configuracio");
	
	// cesca:CESCA_PETICIO_IARXIU Properties
	public static final QName PETICIO_PROP_ID 					= QName.createQName(CESCA_MODEL_1_0_URI, "id_peticio");
	public static final QName PETICIO_PROP_ID_EXP 				= QName.createQName(CESCA_MODEL_1_0_URI, "id_exp");
	public static final QName PETICIO_PROP_NUM_DOC 				= QName.createQName(CESCA_MODEL_1_0_URI, "num_doc");
	public static final QName PETICIO_PROP_NUMEXP 				= QName.createQName(CESCA_MODEL_1_0_URI, "numexp");
	public static final QName PETICIO_PROP_ESTAT 				= QName.createQName(CESCA_MODEL_1_0_URI, "estat");
	public static final QName PETICIO_PROP_DESC_ERROR		 	= QName.createQName(CESCA_MODEL_1_0_URI, "desc_error");
	public static final QName PETICIO_PROP_PIA 					= QName.createQName(CESCA_MODEL_1_0_URI, "pia_");
	public static final QName PETICIO_PROP_ID_CONFIG 			= QName.createQName(CESCA_MODEL_1_0_URI, "peticio_id_configuracio");
	
	// cesca:CESCA_VOCABULARIO Properties
	public static final QName VOCABULARIO_PROP_ID 				= QName.createQName(CESCA_MODEL_1_0_URI, "id_vocabulario");
	public static final QName VOCABULARIO_PROP_NAME 			= QName.createQName(CESCA_MODEL_1_0_URI, "nombreVoc");
	
	// cesca:CESCA_ATRIBUTO Properties
	public static final QName ATRIBUTO_PROP_ID 					= QName.createQName(CESCA_MODEL_1_0_URI, "id_atributo");
	public static final QName ATRIBUTO_PROP_NAME 				= QName.createQName(CESCA_MODEL_1_0_URI, "nombreAtr");
	public static final QName ATRIBUTO_PROP_VALOR 				= QName.createQName(CESCA_MODEL_1_0_URI, "valor");
	public static final QName ATRIBUTO_PROP_TIPUS 				= QName.createQName(CESCA_MODEL_1_0_URI, "tipus");
	public static final QName ATRIBUTO_PROP_OBLIGATORI 			= QName.createQName(CESCA_MODEL_1_0_URI, "obligatori");
	public static final QName ATRIBUTO_PROP_VOCABLUARI_REF		= QName.createQName(CESCA_MODEL_1_0_URI, "vocabulario_ref");
	public static final QName ATRIBUTO_PROP_TIPUS_DOCUMENTAL	= QName.createQName(CESCA_MODEL_1_0_URI, "tipus_documental");
	
	// cesca:CESCA_EXTRACCIO Properties
	public static final QName EXTRACCIO_PROP_ID 				= QName.createQName(CESCA_MODEL_1_0_URI, "id_extraccio");
	public static final QName EXTRACCIO_PROP_NAME 				= QName.createQName(CESCA_MODEL_1_0_URI, "nombreExtraccio");
	public static final QName EXTRACCIO_PROP_TIPUS_EXP 			= QName.createQName(CESCA_MODEL_1_0_URI, "tipusDocumentalExp");
	public static final QName EXTRACCIO_PROP_TIPUS_DOC 			= QName.createQName(CESCA_MODEL_1_0_URI, "tipusDocumentalDoc");
	public static final QName EXTRACCIO_PROP_TIPUS_SIG 			= QName.createQName(CESCA_MODEL_1_0_URI, "tipusDocumentalSign");
	public static final QName EXTRACCIO_PROP_SEL_EXP			= QName.createQName(CESCA_MODEL_1_0_URI, "selExp");
	public static final QName EXTRACCIO_PROP_SEL_DOC			= QName.createQName(CESCA_MODEL_1_0_URI, "selDoc");
	public static final QName EXTRACCIO_PROP_SEL_SIG			= QName.createQName(CESCA_MODEL_1_0_URI, "selSign");
	public static final QName EXTRACCIO_PROP_PLA_EXP			= QName.createQName(CESCA_MODEL_1_0_URI, "plantillaExp");
	public static final QName EXTRACCIO_PROP_PLA_DOC			= QName.createQName(CESCA_MODEL_1_0_URI, "plantillaDoc");
	public static final QName EXTRACCIO_PROP_PLA_SIG			= QName.createQName(CESCA_MODEL_1_0_URI, "plantillaSign");
	public static final QName EXTRACCIO_PROP_REF_FILE_CONF		= QName.createQName(CESCA_MODEL_1_0_URI, "ref_file_conf");
	public static final QName EXTRACCIO_PROP_REF_FILE_EXP		= QName.createQName(CESCA_MODEL_1_0_URI, "ref_file_exp");
	public static final QName EXTRACCIO_PROP_REF_FILE_DOC		= QName.createQName(CESCA_MODEL_1_0_URI, "ref_file_doc");
	public static final QName EXTRACCIO_PROP_REF_FILE_DOC_SENSE_EXP		= QName.createQName(CESCA_MODEL_1_0_URI, "ref_file_doc_sense_exp");
	public static final QName EXTRACCIO_PROP_REF_FILE_SIGN		= QName.createQName(CESCA_MODEL_1_0_URI, "ref_file_sign");
	
	// cesca:CESCA_PLANTILLA Properties
	public static final QName PLANTILLA_PROP_ID 				= QName.createQName(CESCA_MODEL_1_0_URI, "id_plantilla");
	public static final QName PLANTILLA_PROP_NAME 				= QName.createQName(CESCA_MODEL_1_0_URI, "nombrePlantilla");
	public static final QName PLANTILLA_PROP_TIPO 				= QName.createQName(CESCA_MODEL_1_0_URI, "tipoDocumental");
	public static final QName PLANTILLA_PROP_PLAN_REF 			= QName.createQName(CESCA_MODEL_1_0_URI, "plantillaRef");
	public static final QName PLANTILLA_PROP_VOC_REF 			= QName.createQName(CESCA_MODEL_1_0_URI, "vocabulariRef");

	// cesca:CESCA_REBUT_IARXIU Properties
	public static final QName REBUT_PROP_ID_EXP 				= QName.createQName(CESCA_MODEL_1_0_URI, "id");
	public static final QName REBUT_PROP_PIA 					= QName.createQName(CESCA_MODEL_1_0_URI, "pia");
	
	// cesca:CESCA_PROCES_LOG Properties
	public static final QName LOG_PROP_USUARI 					= QName.createQName(CESCA_MODEL_1_0_URI, "usuari");
	public static final QName LOG_PROP_DATA 					= QName.createQName(CESCA_MODEL_1_0_URI, "data");
	public static final QName LOG_PROP_PROCESS 					= QName.createQName(CESCA_MODEL_1_0_URI, "proces");
	public static final QName LOG_PROP_ISERROR 					= QName.createQName(CESCA_MODEL_1_0_URI, "is_error");
	
	// cesca:CESCA_MAIL_TEMPLATE Properties
	public static final QName MAIL_PROP_USUARIS					= QName.createQName(CESCA_MODEL_1_0_URI, "grup_usuaris");
	public static final QName MAIL_PROP_SUBJECT					= QName.createQName(CESCA_MODEL_1_0_URI, "titol_correu");
	
	
	//Utilities
	public static boolean isEmpty(String txt) {
		
		if (txt == null || txt.trim().length() == 0)
			return true;
		
		return false;
	}
	
    public static boolean isNumeric(String cadena){
        try {
        Integer.parseInt(cadena);
        return true;
        } catch (NumberFormatException nfe){
        return false;
        }
        }

}
