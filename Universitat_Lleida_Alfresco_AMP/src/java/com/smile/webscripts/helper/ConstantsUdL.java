package com.smile.webscripts.helper;

import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;

public interface ConstantsUdL {	
	
	public final static String FORM_PARAM_USERNAME = "username";
	public final static String FORM_PARAM_NODEREF = "nodeRef";
	public final static String FORM_PARAM_SERVER = "server";
	
	public final static String CURRENT_TICKET = "currentTicket";
	public final static String PERSON = "person";
	public final static String HOME_FOLDER = "homeFolder";
	
	public final static String FORM_PARAM_DOC_ID = "docID";
	public final static String FORM_PARAM_DOC_CONTENT = "docContent";
	public final static String FORM_PARAM_DOC_NAME = "docName";	
	public final static String FORM_PARAM_DOC_PATH = "docPath";
	public final static String FORM_PARAM_DOC_ESTADO = "docMetadata_estat";
	public final static String FORM_PARAM_DOC_TYPE = "docType";
	
	public final static String FORM_PARAM_NODE_TYPE = "nodeType";
	public final static String FORM_PARAM_NODE_NAME = "nodeName";	
	public final static String FORM_PARAM_NODE_PATH = "nodePath";
	
	public final static String FORM_PARAM_EXP_ID = "expID";
	public final static String FORM_PARAM_EXP_NAME = "expName";	
	public final static String FORM_PARAM_EXP_PATH = "expPath";
		
	public final static String FORM_PARAM_SIG_CONTENT = "sigContent";
	public final static String FORM_PARAM_SIG_NAME = "sigName";	
	public final static String FORM_PARAM_SIG_PATH = "sigPath";
	
	public final static String FORM_PARAM_DOCUMENTS = "documents";
	public final static String FORM_PARAM_SIGNATURES = "signatures";	
	public final static String FORM_PARAM_METADADES = "metadades";	
	public final static String FORM_PARAM_ARGS = "arguments";
		
	public final static String SIGNATURES_FOLDER_PREFIX = "signatures-";
	public final static String ATTACHED_SIGNATURES_PREFIX = "attachedSignatures-";
	
	public final static String XML_TAG = "<xml>";
	public final static String END_XML_TAG = "</xml>";
	public final static String DOCUMENT_TAG = "<document>";
	public final static String END_DOCUMENT_TAG = "</document>";
	public final static String SIGNATURA_TAG = "<signatura>";
	public final static String END_SIGNATURA_TAG = "</signatura>";
	public final static String SIGNATURES_TAG = "<signatures>";
	public final static String END_SIGNATURES_TAG = "</signatures>";
	
	public final static String UTF8 = "UTF-8";
	public final static String ISO8859 = "ISO-8859-1";
	
	public final static String UDL_PROPERTIES_FILE = "/udl.properties";
	public final static String ADMIN_USERNAME = "admin_username";
	public final static String ADMIN_PASSWORD = "admin_password";
	
	public final static String FTL_DOCUMENT = "document";
	public final static String FTL_ERROR = "error";
	public final static String FTL_EXPEDIENT = "expedient";
	public final static String FTL_SUCCESS = "success";
	public final static String FTL_USERNAME = "username";
	public final static String FTL_INFO_LIST = "infoList";
			
	public final static String URL_WEBSCRIPT_PUJAR_DOCUMENT_BASE64 = "/service/udl/document/pujarDocumentBase64";
	public final static String URL_WEBSCRIPT_ACTUALITZAR_DOCUMENT_BASE64 = "/service/udl/document/actualitzarDocumentBase64";
	public final static String URL_WEBSCRIPT_OBTENIR_FOLDER_SIGNATURES = "/service/udl/document/obtenirFolderSignatures";
	public final static String URL_WEBSCRIPT_CREAR_NODE = "/service/udl/helper/createNode";
	public final static String URL_WEBSCRIPT_TRANSFER_NODES = "/service/udl/helper/transferNodes";
	public final static String URL_WEBSCRIPT_DECLARE_RECORD = "/service/udl/helper/declareRecordRecursive";
	public final static String URL_WEBSCRIPT_CUTOFF = "/service/udl/helper/cutoff";
	
	public final static String SIGNATURA_NOM = "nom";
	public final static String SIGNATURA_NOM_VIEW = "nomView";	
	public final static String SIGNATURA_CARREC = "carrec";	
	public final static String SIGNATURA_DNI = "dni";
	public final static String SIGNATURA_DATA = "data";
	public final static String SIGNATURA_DATA_FI_VALIDESA_CERTIFICAT = "dataFiCert";	
	
	public final static String EXTENSION_XADES = "xades";
	public final static String EXTENSION_CADES = "cades";
	public final static String EXTENSION_PDF = "pdf";
	public final static String EXTENSION_DOC = "doc";
	public final static String EXTENSION_DOCX = "docx";
	public final static String EXTENSION_ODT = "odt";

	public final static String EXTENSION_SIGN_DETACHED_PDF = "dpdf";
	public final static String EXTENSION_SIGN_DETACHED_DOC = "ddoc";
	public final static String EXTENSION_SIGN_DETACHED_DOCX = "ddocx";
	public final static String EXTENSION_SIGN_DETACHED_ODT = "dodt";
	public final static String EXTENSION_SIGNATURE_DETACHED_PDF = ".dpdf";
	public final static String EXTENSION_SIGNATURE_DETACHED_DOC = ".ddoc";
	public final static String EXTENSION_SIGNATURE_DETACHED_DOCX = ".ddocx";
	public final static String EXTENSION_SIGNATURE_DETACHED_ODT = ".dodt";

	public final static String EXTENSION_SIGN_XADES_PDF = "xpdf";
	public final static String EXTENSION_SIGN_XADES = "xades";
	public final static String EXTENSION_SIGN_XADES_DOC = "xdoc";
	public final static String EXTENSION_SIGN_XADES_DOCX = "xdocx";
	public final static String EXTENSION_SIGN_XADES_ODT = "xodt";
	public final static String EXTENSION_SIGNATURE_XADES_PDF = ".xpdf";
	public final static String EXTENSION_SIGNATURE_XADES_DOC = ".xdoc";
	public final static String EXTENSION_SIGNATURE_XADES_DOCX = ".xdocx";
	public final static String EXTENSION_SIGNATURE_XADES_ODT = ".xodt";
	
	public final static String EXTENSION_SIGN_CADES_PDF = "cpdf";
	public final static String EXTENSION_SIGN_CADES = "cades";
	public final static String EXTENSION_SIGN_CADES_DOC = "cdoc";
	public final static String EXTENSION_SIGN_CADES_DOCX = "cdocx";
	public final static String EXTENSION_SIGN_CADES_ODT = "codt";
	public final static String EXTENSION_SIGNATURE_CADES_PDF = ".cpdf";
	public final static String EXTENSION_SIGNATURE_CADES_DOC = ".cdoc";
	public final static String EXTENSION_SIGNATURE_CADES_DOCX = ".cdocx";
	public final static String EXTENSION_SIGNATURE_CADES_ODT = ".codt";	
	
	public final static String MIMETYPE_OCTET_STREAM = "application/octet-stream";	
	public final static String MIMETYPE_SIGNATURE_DETACHED_PDF = "application/dpdf";
	public final static String MIMETYPE_SIGNATURE_DETACHED_DOC = "application/ddoc";
	public final static String MIMETYPE_SIGNATURE_DETACHED_DOCX = "application/ddocx";
	public final static String MIMETYPE_SIGNATURE_DETACHED_ODT = "application/dodt";
	public final static String MIMETYPE_SIGNATURE_XADES = "application/xades";
	public final static String MIMETYPE_SIGNATURE_XADES_PDF = "application/xpdf";
	public final static String MIMETYPE_SIGNATURE_XADES_DOC = "application/xdoc";
	public final static String MIMETYPE_SIGNATURE_XADES_DOCX = "application/xdocx";
	public final static String MIMETYPE_SIGNATURE_XADES_ODT = "application/xodt";
	public final static String MIMETYPE_SIGNATURE_CADES = "application/cades";
	public final static String MIMETYPE_SIGNATURE_CADES_PDF = "application/cpdf";
	public final static String MIMETYPE_SIGNATURE_CADES_DOC = "application/cdoc";
	public final static String MIMETYPE_SIGNATURE_CADES_DOCX = "application/cdocx";
	public final static String MIMETYPE_SIGNATURE_CADES_ODT = "application/codt";
	
	public final static String MIMETYPE_TEXT_PLA = "text/plain";
	
	public final static String SIGNATURES = "signatures";
	
	public final static String NOM_INDEX = "Índex.txt";
	
	public final static String TYPE_AGREGACIO = "agregacio";
	public final static String TYPE_EXPEDIENT = "expedient";
	
	public final static String PREFIX_NAMESPACE_UDL = "udl";
	public final static String PREFIX_NAMESPACE_UDL_RM = "udlrm";
		
	public final static String CM_URI = "http://www.alfresco.org/model/content/1.0";
	public final static String RM_URI = "http://www.alfresco.org/model/recordsmanagement/1.0";
	
	public static QName ASPECT_RECORD_COMPONENT_ID = QName.createQName(RM_URI, "recordComponentIdentifier");
	public static QName PROP_IDENTIFIER = QName.createQName(RM_URI, "identifier");

	public final static String UDL_URI = "http://www.smile.com/model/udl/1.0";
	public final static String UDLRM_URI = "http://www.smile.com/model/udlrm/1.0";
	
	public final static String AUDIT_APLICATION_PATH = "/udl/audit";	
	public final static String AUDIT_ACTION_CREATE_DOCUMENT_SIMPLE = "crear_document_simple";
	public final static String AUDIT_ACTION_UPDATE_DOCUMENT_SIMPLE = "modificar_document_simple";
	public final static String AUDIT_ACTION_DELETE_DOCUMENT_SIMPLE = "esborrar_document_simple";
	
	public final static String AUDIT_ACTION_VIEW_EXPEDIENT = "consultar_expedient";
	public final static String AUDIT_ACTION_CREATE_EXPEDIENT = "crear_expedient";
	public final static String AUDIT_ACTION_UPDATE_EXPEDIENT = "modificar_expedient";
	public final static String AUDIT_ACTION_DELETE_EXPEDIENT = "esborrar_expedient";
	public final static String AUDIT_ACTION_MOVE_EXPEDIENT = "moure_expedient";
	public final static String AUDIT_ACTION_ARCHIVE_EXPEDIENT = "arxivar_expedient";
	public final static String AUDIT_ACTION_ARCHIVE_AGREGACIO = "arxivar_agregacio";
	public final static String AUDIT_ACTION_TRANSFER_EXPEDIENT = "transferir_expedient";
	public final static String AUDIT_ACTION_TRANSFER_AGREGACIO = "transferir_agregacio";

	public final static String AUDIT_NODEREF_PREFIX = "workspace://SpacesStore/";
	
	public final static String UDL_GROUP_PREFIX = PermissionService.GROUP_PREFIX + "UDL_";
	
	public final static String WS_URL = "https://ercd.udl.cat:4443/SOA.asmx";
	public final static String WS_INICI_GENERA_CODI = "IniciaGeneraCodi";
	public final static String WS_GENERA_CODI = "GeneraCodi";
	public final static String WS_APP_ID = "AlFresco";
	
	public final static String DOCUMENT_SIMPLE = "DocumentSimple";
	public final static String AGREGACIO = "Agregacio";
	public final static String EXPEDIENT = "Expedient";
	public final static String SERIE = "Serie";
	public final static String FONS = "Fons";
	public final static String GRUP_DE_FONS = "GrupDeFons";
	
	public final static String RECORD_FOLDER = "recordFolder";

	public final static String MASCARA_DOCUMENT_SIMPLE = "CAT_AUdL_AAAA_ID_SERIAL1";
	public final static String MASCARA_AGREGACIO = "CAT_AUdL_AAAA_AGR._ID_SERIAL1";
	public final static String MASCARA_EXPEDIENT = "CAT_AUdL_AAAA_núm reg._          _núm. EXP. _ID_SERIAL2_Tipus";
	public final static String MASCARA_SERIE = "CAT_AUdL_AAAA_SERIE_ID_SERIAL1";
	public final static String MASCARA_FONS = "CAT_AUdL_AAAA_FONS._ID_SERIAL1";
	public final static String MASCARA_GRUP_DE_FONS = "CAT_AUdL_AAAA_GFONS._ID_SERIAL1";

	public final static String PARAM_ARGUMENTS = "arguments";
	public final static String PARAM_USERNAME = "username";
	public final static String PARAM_NAME = "name";
	public final static String PARAM_SURNAME1 = "surname1";
	public final static String PARAM_SURNAME2 = "surname2";
	
	public final static String PARAM_GRUP_CREADOR = "grupCreador";
	public final static String PARAM_CODI_CLASSIFICACIO_1 = "codiClassificacio1";
	public final static String PARAM_DENOMINACIO_CLASSE_1 = "denominacioClasse1";
	public final static String PARAM_CODI_CLASSIFICACIO_2 = "codiClassificacio2";
	public final static String PARAM_DENOMINACIO_CLASSE_2 = "denominacioClasse2";
	public final static String PARAM_DATA_INICI = "dataInici";
	public final static String PARAM_DATA_FI = "dataFi";
	public final static String PARAM_DESCRIPCIO = "descripcio";
	public final static String PARAM_QUANTITAT = "quantitat";
	public final static String PARAM_NOM_NATURAL_ASPECTE = "nomNaturalAspecte";
	public final static String PARAM_NOM_NATURAL = "nomNatural";
	public final static String PARAM_ID = "id";
	public final static String PARAM_LOCALITZACIO_1 = "localitzacio1";
	public final static String PARAM_LOCALITZACIO_2 = "localitzacio2";
	public final static String PARAM_ESSENCIAL = "essencial";
	
	public final static int MAX_LENGTH = 199;
	
	public final static String SIGNATURA_ENVELOPING = "enveloping";
	public final static String SIGNATURA_ATTACHED = "attached";
	public final static String SIGNATURA_DETTACHED = "dettached";
	
	public final static String SIGNATURA_XADES = "XAdES";
	public final static String SIGNATURA_CADES = "CAdES";
	public final static String SIGNATURA_PADES = "PAdES";
}