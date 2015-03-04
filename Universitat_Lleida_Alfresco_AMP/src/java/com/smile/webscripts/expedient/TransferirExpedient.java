package com.smile.webscripts.expedient;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.xml.rpc.ParameterMode;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.audit.AuditComponent;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.OwnableService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.QName;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.w3c.dom.Element;

import com.smile.webscripts.audit.AuditUdl;
import com.smile.webscripts.document.DocumentUtils;
import com.smile.webscripts.helper.Arguments;
import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Cript;
import com.smile.webscripts.helper.Impersonate;
import com.smile.webscripts.helper.UdlProperties;

public class TransferirExpedient extends DeclarativeWebScript implements ConstantsUdL {

	public final static String RM_URI = "http://www.alfresco.org/model/recordsmanagement/1.0";
	public static QName ASPECT_CUT_OFF = QName.createQName(RM_URI, "cutOff");
	public static QName PROP_CUT_OFF_DATE = QName.createQName(RM_URI, "cutOffDate");
	public static QName ASPECT_DECLARED_RECORD = QName.createQName(RM_URI, "declaredRecord");
	public static QName PROP_DECLARED_AT = QName.createQName(RM_URI, "declaredAt");
	public static QName PROP_DECLARED_BY = QName.createQName(RM_URI, "declaredBy");

	private ServiceRegistry serviceRegistry;
	private AuditComponent auditComponent;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setAuditComponent(AuditComponent auditComponent) {
		this.auditComponent = auditComponent;
	}

	public TransferirExpedient() {
	}

	public TransferirExpedient(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * Transfer a folder (expediente) consists in: - Move all content (folders
	 * and documents) from a Content Management space to Records Management file
	 * plan categories (this is mapped through content metadata). - Declare all
	 * content moved to Records Management as Records (all mandatory metadata
	 * must be filled before). - Cutoff all content moved to Records Management
	 * (all content must be declared as Record and cutoff action must be
	 * available on the category).
	 */
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		UserTransaction trx = serviceRegistry.getTransactionService().getUserTransaction();

		try {
			Element args = Arguments.getArguments(req);
			String username = args.getElementsByTagName(FORM_PARAM_USERNAME).item(0).getFirstChild().getNodeValue();
			if ("session".equals(username)) {
				username = serviceRegistry.getAuthenticationService().getCurrentUserName();
			}
			model.put(FTL_USERNAME, username);
			Impersonate.impersonate(username);
			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();
			String docID = args.getElementsByTagName(FORM_PARAM_EXP_ID).item(0).getFirstChild().getNodeValue();
			NodeRef expedientNodeRef = new NodeRef(AUDIT_NODEREF_PREFIX + docID);
			ScriptNode expedient = new ScriptNode(expedientNodeRef, serviceRegistry, scope);
			String nodeRefExpArxiuActiu = expedient.getNodeRef().toString();
			String typeExpArxiuActiu = expedient.getTypeShort();
			String siteExpArxiuActiu = expedient.getSiteShortName();
			Set<QName> aspects = serviceRegistry.getNodeService().getAspects(expedient.getNodeRef());
			Map<QName, Serializable> allNodeProps = serviceRegistry.getNodeService().getProperties(expedient.getNodeRef());

			trx.begin();
			NodeRef ref = transferNodes(username, expedientNodeRef);
			// El indice se crea en el webscript transferir nodes
			declareRecordTree(ref, serviceRegistry);
			// El cutoff se hace automáticamente, ya que la serie de destino
			// tendrá un calendario de conservación con el evento cutoff
			// planificado
			ScriptNode expedientRma = new ScriptNode(ref, serviceRegistry, scope);
			addAspects(expedientRma.getNodeRef(), aspects, allNodeProps);
			expedientRma.save();
			model.put(FTL_EXPEDIENT, expedientRma);
			model.put(FTL_SUCCESS, String.valueOf(true));
			trx.commit();

			// Auditar transferir expedient
			if (typeExpArxiuActiu.equals("udl:expedient")) {
				AuditUdl.auditRecord(auditComponent, username, nodeRefExpArxiuActiu, AUDIT_ACTION_TRANSFER_EXPEDIENT, typeExpArxiuActiu,
						siteExpArxiuActiu);
			} else if (typeExpArxiuActiu.equals("udl:agregacio")) {
				AuditUdl.auditRecord(auditComponent, username, nodeRefExpArxiuActiu, AUDIT_ACTION_TRANSFER_AGREGACIO, typeExpArxiuActiu,
						siteExpArxiuActiu);
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.put(FTL_ERROR, e.getMessage());
			model.put(FTL_SUCCESS, String.valueOf(false));
			status.setCode(500);

			try {
				if (trx.getStatus() == javax.transaction.Status.STATUS_ACTIVE) {
					trx.rollback();
				}
			} catch (SystemException ex) {
				e.printStackTrace();
			}
		}

		return model;
	}

	public NodeRef transferNodes(String username, NodeRef expedient) throws Exception, InvalidQNameException, InvalidNodeRefException,
			NoSuchAlgorithmException {
		InfoTransferencia infoTransferencia = transferirNode(expedient, null, true, username, null, null);
		Map<NodeRef, List<NodeRef>> infoSignatures = infoTransferencia.getInfoSignatures();
		Map<NodeRef, NodeRef> signaturesDmRm = infoTransferencia.getSignaturesDmRm();
		Set<NodeRef> docsSignats = infoSignatures.keySet();
		Iterator<?> it = docsSignats.iterator();

		while (it.hasNext()) {
			NodeRef docSignat = (NodeRef) it.next();
			List<NodeRef> signaturesDoc = (List<NodeRef>) infoSignatures.get(docSignat);
			Iterator<?> signatures = signaturesDoc.iterator();
			while (signatures.hasNext()) {
				NodeRef oldSignatura = (NodeRef) signatures.next();
				NodeRef newSignatura = signaturesDmRm.get(oldSignatura);
				if (newSignatura != null) {
					serviceRegistry.getNodeService().createAssociation(docSignat, newSignatura,
							QName.createQName(CM_URI, "signaturesDocumentRm"));
				}
			}
		}

		serviceRegistry.getNodeService().deleteNode(expedient);
		return infoTransferencia.getRootRm();
	}

	private InfoTransferencia transferirNode(NodeRef nodeCm, NodeRef rmParent, boolean root, String username, NodeRef indexParent,
			InfoTransferencia infoTransferencia) throws Exception, InvalidQNameException, InvalidNodeRefException, NoSuchAlgorithmException {
		Date now = Calendar.getInstance().getTime();
		NodeRef newNodeRma = null;
		NodeService nodeService = serviceRegistry.getNodeService();
		FileFolderService fileFolderService = serviceRegistry.getFileFolderService();

		if (nodeCm != null) {
			NodeRef nodeRma = null;
			if (root) {
				infoTransferencia = new InfoTransferencia();
				NodeRef serie = nodeService.getPrimaryParent(nodeCm).getParentRef();
				String rmUuidSerie = (String) nodeService.getProperty(serie, QName.createQName(UDL_URI, "rm_uuid_serie"));

				nodeRma = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE.toString() + "/" + rmUuidSerie);

			} else {
				nodeRma = rmParent;
			}
			if (nodeRma != null) {
				if (serviceRegistry.getFileFolderService().getFileInfo(nodeCm).isFolder()) {
					if (nodeService.getType(nodeCm).toString().equals("{http://www.smile.com/model/udl/1.0}expedient")) {
						String name = (String) nodeService.getProperty(nodeCm, QName.createQName(CM_URI, "name"));

						newNodeRma = fileFolderService.create(nodeRma, name, QName.createQName(RM_URI, "recordFolder")).getNodeRef();
						// newNodeRma = (nodeService.createNode(nodeRma,
						// ContentModel.ASSOC_CONTAINS, QName.createQName(name),
						// QName.createQName(RM_URI,
						// "recordFolder"))).getChildRef();
						nodeService.addAspect(newNodeRma, QName.createQName(UDLRM_URI, "expedient"), null);
						Map<QName, Serializable> props = new HashMap<QName, Serializable>();

						props.put(QName.createQName(RM_URI, "identifier"), newNodeRma.getId());
						props.put(QName.createQName(CM_URI, "name"), nodeService.getProperty(nodeCm, QName.createQName(CM_URI, "name")));
						props.put(ContentModel.PROP_CREATOR, nodeService.getProperty(nodeCm, ContentModel.PROP_CREATOR));
						props.put(ContentModel.PROP_CREATED, nodeService.getProperty(nodeCm, ContentModel.PROP_CREATED));
						props.put(ContentModel.PROP_MODIFIER, nodeService.getProperty(nodeCm, ContentModel.PROP_MODIFIER));
						props.put(ContentModel.PROP_MODIFIED, nodeService.getProperty(nodeCm, ContentModel.PROP_MODIFIED));
						props.put(QName.createQName(UDLRM_URI, "grup_creador_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "grup_creador_expedient")));
						props.put(QName.createQName(UDLRM_URI, "descripcio_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "descripcio_expedient")));
						props.put(QName.createQName(UDLRM_URI, "tipus_entitat_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_entitat_expedient")));
						props.put(QName.createQName(UDLRM_URI, "categoria_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "categoria_expedient")));
						props.put(QName.createQName(UDLRM_URI, "secuencial_identificador_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "secuencial_identificador_expedient")));
						props.put(QName.createQName(UDLRM_URI, "esquema_identificador_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "esquema_identificador_expedient")));
						props.put(QName.createQName(UDLRM_URI, "data_inici_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "data_inici_expedient")));
						props.put(QName.createQName(UDLRM_URI, "data_fi_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "data_fi_expedient")));
						props.put(QName.createQName(UDLRM_URI, "classificacio_acces_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "classificacio_acces_expedient")));
						props.put(QName.createQName(UDLRM_URI, "advertencia_seguretat_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "advertencia_seguretat_expedient")));
						props.put(QName.createQName(UDLRM_URI, "categoria_advertencia_seguretat_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "categoria_advertencia_seguretat_expedient")));
						props.put(
								QName.createQName(UDLRM_URI, "sensibilitat_dades_caracter_personal_expedient"),
								nodeService.getProperty(nodeCm,
										QName.createQName(UDL_URI, "sensibilitat_dades_caracter_personal_expedient")));
						props.put(QName.createQName(UDLRM_URI, "condicions_acces_1_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "condicions_acces_1_expedient")));
						props.put(QName.createQName(UDLRM_URI, "condicions_acces_2_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "condicions_acces_2_expedient")));
						props.put(QName.createQName(UDLRM_URI, "tipus_acces_1_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_acces_1_expedient")));
						props.put(QName.createQName(UDLRM_URI, "tipus_acces_2_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_acces_2_expedient")));
						props.put(QName.createQName(UDLRM_URI, "idioma_1_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "idioma_1_expedient")));
						props.put(QName.createQName(UDLRM_URI, "idioma_2_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "idioma_2_expedient")));
						props.put(QName.createQName(UDLRM_URI, "idioma_3_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "idioma_3_expedient")));
						props.put(QName.createQName(UDLRM_URI, "idioma_4_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "idioma_4_expedient")));
						props.put(QName.createQName(UDLRM_URI, "valoracio_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "valoracio_expedient")));
						props.put(QName.createQName(UDLRM_URI, "tipus_dictamen_1_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_dictamen_1_expedient")));
						props.put(QName.createQName(UDLRM_URI, "tipus_dictamen_2_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_dictamen_2_expedient")));
						props.put(QName.createQName(UDLRM_URI, "accio_dictaminada_1_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "accio_dictaminada_1_expedient")));
						props.put(QName.createQName(UDLRM_URI, "accio_dictaminada_2_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "accio_dictaminada_2_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_origen_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_origen_expedient")));
						props.put(QName.createQName(UDLRM_URI, "dimensions_fisiques_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "dimensions_fisiques_expedient")));
						props.put(QName.createQName(UDLRM_URI, "quantitat_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "quantitat_expedient")));
						props.put(QName.createQName(UDLRM_URI, "unitats_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "unitats_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_1_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_1_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_2_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_2_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_3_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_3_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_4_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_4_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_5_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_5_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_6_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_6_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_7_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_7_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_8_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_8_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_9_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_9_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_10_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_10_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_11_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_11_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_12_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_12_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_13_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_13_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_14_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_14_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_15_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_15_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_16_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_16_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_17_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_17_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_18_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_18_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_19_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_19_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_20_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_20_expedient")));
						props.put(QName.createQName(UDLRM_URI, "suport_21_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_21_expedient")));
						props.put(QName.createQName(UDLRM_URI, "localitzacio_1_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "localitzacio_1_expedient")));
						props.put(QName.createQName(UDLRM_URI, "localitzacio_2_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "localitzacio_2_expedient")));
						props.put(QName.createQName(UDLRM_URI, "algoritme_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "algoritme_expedient")));
						props.put(QName.createQName(UDLRM_URI, "valor_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "valor_expedient")));
						props.put(QName.createQName(UDLRM_URI, "codi_classificacio_1_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "codi_classificacio_1_expedient")));
						props.put(QName.createQName(UDLRM_URI, "codi_classificacio_2_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "codi_classificacio_2_expedient")));
						props.put(QName.createQName(UDLRM_URI, "denominacio_classe_1_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "denominacio_classe_1_expedient")));
						props.put(QName.createQName(UDLRM_URI, "denominacio_classe_2_expedient"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "denominacio_classe_2_expedient")));

						nodeService.addProperties(newNodeRma, props);
						indexParent = newNodeRma;
						infoTransferencia.setFoliacio(infoTransferencia.getFoliacio() + "<expedient>\n<nom>"
								+ nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "name")) + "</nom>\n<id>"
								+ nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "secuencial_identificador_expedient"))
								+ "</id>\n<idIntern>" + newNodeRma.getId() + "</idIntern>\n" + "<creador>"
								+ nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "creator")) + "</creador>\n"
								+ "<dataCreacio>" + nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "created"))
								+ "</dataCreacio>\n" + "<modificador>"
								+ nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "modifier")) + "</modificador>\n"
								+ "<dataModificacio>" + nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "modified"))
								+ "</dataModificacio>\n" + "</expedient>\n");
					}

					else if (nodeService.getType(nodeCm).toString().equals("{http://www.smile.com/model/udl/1.0}agregacio")) {
						String name = (String) nodeService.getProperty(nodeCm, QName.createQName(CM_URI, "name"));
						newNodeRma = fileFolderService.create(nodeRma, name, QName.createQName(RM_URI, "recordFolder")).getNodeRef();
						// newNodeRma = (nodeService.createNode(nodeRma,
						// ContentModel.ASSOC_CONTAINS, QName.createQName(name),
						// QName.createQName(RM_URI,
						// "recordFolder"))).getChildRef();
						nodeService.addAspect(newNodeRma, QName.createQName(UDLRM_URI, "agregacio"), null);
						nodeService.addAspect(newNodeRma, QName.createQName(CM_URI, "taggable"), null);
						serviceRegistry.getTaggingService().addTag(newNodeRma, "Agregació");
						Map<QName, Serializable> props = new HashMap<QName, Serializable>();

						props.put(QName.createQName(RM_URI, "identifier"), newNodeRma.getId());
						props.put(QName.createQName(CM_URI, "name"), nodeService.getProperty(nodeCm, QName.createQName(CM_URI, "name")));
						props.put(ContentModel.PROP_CREATOR, nodeService.getProperty(nodeCm, ContentModel.PROP_CREATOR));
						props.put(ContentModel.PROP_CREATED, nodeService.getProperty(nodeCm, ContentModel.PROP_CREATED));
						props.put(ContentModel.PROP_MODIFIER, nodeService.getProperty(nodeCm, ContentModel.PROP_MODIFIER));
						props.put(ContentModel.PROP_MODIFIED, nodeService.getProperty(nodeCm, ContentModel.PROP_MODIFIED));
						props.put(QName.createQName(UDLRM_URI, "grup_creador_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "grup_creador_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "descripcio_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "descripcio_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "tipus_entitat_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_entitat_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "categoria_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "categoria_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "secuencial_identificador_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "secuencial_identificador_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "esquema_identificador_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "esquema_identificador_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "data_inici_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "data_inici_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "data_fi_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "data_fi_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "classificacio_acces_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "classificacio_acces_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "advertencia_seguretat_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "advertencia_seguretat_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "categoria_advertencia_seguretat_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "categoria_advertencia_seguretat_agregacio")));
						props.put(
								QName.createQName(UDLRM_URI, "sensibilitat_dades_caracter_personal_agregacio"),
								nodeService.getProperty(nodeCm,
										QName.createQName(UDL_URI, "sensibilitat_dades_caracter_personal_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "condicions_acces_1_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "condicions_acces_1_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "condicions_acces_2_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "condicions_acces_2_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "tipus_acces_1_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_acces_1_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "tipus_acces_2_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_acces_2_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "idioma_1_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "idioma_1_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "idioma_2_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "idioma_2_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "idioma_3_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "idioma_3_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "idioma_4_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "idioma_4_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "valoracio_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "valoracio_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "tipus_dictamen_1_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_dictamen_1_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "tipus_dictamen_2_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_dictamen_2_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "accio_dictaminada_1_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "accio_dictaminada_1_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "accio_dictaminada_2_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "accio_dictaminada_2_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_origen_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_origen_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "dimensions_fisiques_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "dimensions_fisiques_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "unitats_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "unitats_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_1_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_1_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_2_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_2_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_3_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_3_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_4_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_4_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_5_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_5_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_6_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_6_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_7_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_7_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_8_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_8_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_9_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_9_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_10_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_10_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_11_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_11_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_12_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_12_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_13_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_13_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_14_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_14_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_15_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_15_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_16_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_16_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_17_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_17_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_18_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_18_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_19_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_19_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_20_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_20_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "suport_21_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_21_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "algoritme_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "algoritme_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "valor_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "valor_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "codi_classificacio_1_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "codi_classificacio_1_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "codi_classificacio_2_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "codi_classificacio_2_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "denominacio_classe_1_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "denominacio_classe_1_agregacio")));
						props.put(QName.createQName(UDLRM_URI, "denominacio_classe_2_agregacio"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "denominacio_classe_2_agregacio")));

						nodeService.addProperties(newNodeRma, props);
						indexParent = newNodeRma;
						infoTransferencia.setFoliacio(infoTransferencia.getFoliacio() + "<agregacio>\n<nom>"
								+ nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "name")) + "</nom>\n<id>"
								+ newNodeRma.getId() + "</id>\n" + "<creador>"
								+ nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "creator")) + "</creador>\n"
								+ "<dataCreacio>" + nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "created"))
								+ "</dataCreacio>\n" + "<modificador>"
								+ nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "modifier")) + "</modificador>\n"
								+ "<dataModificacio>" + nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "modified"))
								+ "</dataModificacio>\n" + "</agregacio>\n");
					}

					List<ChildAssociationRef> nodesCm = nodeService.getChildAssocs(nodeCm);
					Iterator<?> it = nodesCm.iterator();
					while (it.hasNext()) {
						ChildAssociationRef ref = (ChildAssociationRef) it.next();
						NodeRef child = ref.getChildRef();
						transferirNode(child, newNodeRma, false, username, indexParent, infoTransferencia);
					}
				} else {
					String name = (String) nodeService.getProperty(nodeCm, ContentModel.PROP_NAME);
					newNodeRma = serviceRegistry.getFileFolderService().create(nodeRma, name, ContentModel.TYPE_CONTENT).getNodeRef();
					Map<QName, Serializable> props = new HashMap<QName, Serializable>();
					props.put(ContentModel.PROP_CONTENT, nodeService.getProperty(nodeCm, ContentModel.PROP_CONTENT));
					// props.put(ContentModel.PROP_NAME,
					// StringUtils.left((String)nodeService.getProperty(nodeCm,
					// ContentModel.PROP_NAME), MAX_LENGTH));
					// props.put(ContentModel.PROP_TITLE,
					// StringUtils.left((String)nodeService.getProperty(nodeCm,
					// ContentModel.PROP_NAME), MAX_LENGTH));
					props.put(ContentModel.PROP_NAME, nodeService.getProperty(nodeCm, ContentModel.PROP_NAME));
					props.put(ContentModel.PROP_TITLE, nodeService.getProperty(nodeCm, ContentModel.PROP_NAME));
					props.put(ContentModel.PROP_DESCRIPTION, nodeService.getProperty(nodeCm, ContentModel.PROP_DESCRIPTION));
					props.put(QName.createQName(RM_URI, "originator"), nodeService.getProperty(nodeCm, ContentModel.PROP_CREATOR));
					props.put(QName.createQName(RM_URI, "originatingOrganization"),
							nodeService.getProperty(nodeCm, ContentModel.PROP_CREATOR));
					props.put(QName.createQName(RM_URI, "publicationDate"), now);
					props.put(QName.createQName(RM_URI, "identifier"), newNodeRma.getId());
					props.put(QName.createQName(RM_URI, "dateFiled"), now);
					props.put(QName.createQName(RM_URI, "creator"), username);
					props.put(ContentModel.PROP_CREATED, nodeService.getProperty(nodeCm, ContentModel.PROP_CREATED));
					props.put(ContentModel.PROP_MODIFIED, nodeService.getProperty(nodeCm, ContentModel.PROP_MODIFIED));
					nodeService.addProperties(newNodeRma, props);

					if (nodeService.getType(nodeCm).toString().equals("{http://www.smile.com/model/udl/1.0}documentSimple")) {
						addAspects(nodeCm, newNodeRma);
						Map<QName, Serializable> props1 = new HashMap<QName, Serializable>();

						props1.put(ContentModel.PROP_CREATOR, nodeService.getProperty(nodeCm, ContentModel.PROP_CREATOR));
						props1.put(ContentModel.PROP_CREATED, nodeService.getProperty(nodeCm, ContentModel.PROP_CREATED));
						props1.put(ContentModel.PROP_MODIFIER, nodeService.getProperty(nodeCm, ContentModel.PROP_MODIFIER));
						props1.put(ContentModel.PROP_MODIFIED, nodeService.getProperty(nodeCm, ContentModel.PROP_MODIFIED));
						props1.put(QName.createQName(UDLRM_URI, "grup_creador_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "grup_creador_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "tipus_documental_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_documental_documentSimple")));
						// props1.put(QName.createQName(UDLRM_URI,
						// "tipus_entitat_documentSimple"),
						// nodeService.getProperty(nodeCm,
						// QName.createQName(UDL_URI,
						// "tipus_entitat_documentSimple")));
						// props1.put(QName.createQName(UDLRM_URI,
						// "categoria_documentSimple"),
						// nodeService.getProperty(nodeCm,
						// QName.createQName(UDL_URI,
						// "categoria_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "tipus_entitat_documentSimple"), "Document");
						props1.put(QName.createQName(UDLRM_URI, "categoria_documentSimple"), "Document simple");
						props1.put(QName.createQName(UDLRM_URI, "secuencial_identificador_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "secuencial_identificador_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "esquema_identificador_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "esquema_identificador_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "data_inici_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "data_inici_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "data_fi_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "data_fi_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "data_registre_entrada_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "data_registre_entrada_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "data_registre_sortida_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "data_registre_sortida_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "classificacio_acces_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "classificacio_acces_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "advertencia_seguretat_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "advertencia_seguretat_documentSimple")));
						props1.put(
								QName.createQName(UDLRM_URI, "categoria_advertencia_seguretat_documentSimple"),
								nodeService.getProperty(nodeCm,
										QName.createQName(UDL_URI, "categoria_advertencia_seguretat_documentSimple")));
						props1.put(
								QName.createQName(UDLRM_URI, "sensibilitat_dades_caracter_personal_documentSimple"),
								nodeService.getProperty(nodeCm,
										QName.createQName(UDL_URI, "sensibilitat_dades_caracter_personal_documentSimple")));
						props1.put(
								QName.createQName(UDLRM_URI, "verificacio_integritat_algorisme_documentSimple"),
								nodeService.getProperty(nodeCm,
										QName.createQName(UDL_URI, "verificacio_integritat_algorisme_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "verificacio_integritat_valor_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "verificacio_integritat_valor_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "idioma_1_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "idioma_1_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "idioma_2_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "idioma_2_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "valoracio_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "valoracio_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "tipus_dictamen_1_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_dictamen_1_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "tipus_dictamen_2_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_dictamen_2_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "accio_dictaminada_1_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "accio_dictaminada_1_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "accio_dictaminada_2_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "accio_dictaminada_2_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "suport_origen_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_origen_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "versio_format_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "versio_format_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "nom_aplicacio_creacio_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "nom_aplicacio_creacio_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "versio_aplicacio_creacio_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "versio_aplicacio_creacio_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "registre_formats_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "registre_formats_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "resolucio_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "resolucio_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "dimensions_fisiques_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "dimensions_fisiques_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "unitats_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "unitats_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "suport_1_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "suport_1_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "localitzacio_1_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "localitzacio_1_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "localitzacio_2_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "localitzacio_2_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "denominacio_estat_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "denominacio_estat_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "tipus_copia_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_copia_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "motiu_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "motiu_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "codi_classificacio_1_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "codi_classificacio_1_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "codi_classificacio_2_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "codi_classificacio_2_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "denominacio_classe_1_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "denominacio_classe_1_documentSimple")));
						props1.put(QName.createQName(UDLRM_URI, "denominacio_classe_2_documentSimple"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "denominacio_classe_2_documentSimple")));

						nodeService.addAspect(newNodeRma, QName.createQName(UDLRM_URI, "documentSimple"), props1);
						ContentData contentRef = (ContentData) nodeService.getProperty(newNodeRma, ContentModel.PROP_CONTENT);
						String nodeSize = "";
						String mimeType = "";
						if (contentRef != null) {
							nodeSize = String.valueOf(contentRef.getSize());
							mimeType = contentRef.getMimetype();
						}
						ContentService contentService = serviceRegistry.getContentService();
						ContentReader reader = contentService.getReader(newNodeRma, ContentModel.PROP_CONTENT);
						String sha512 = "";
						if (reader != null) {
							reader.setEncoding(ConstantsUdL.UTF8);
							try {
								sha512 = Cript.sha512(reader.getContentString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						infoTransferencia.setFoliacio(infoTransferencia.getFoliacio() + "<documentSimple>\n<nom>"
								+ nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "name")) + "</nom>\n<id>"
								+ newNodeRma.getId() + "</id>\n<sha-512>" + sha512 + "</sha-512>\n" + "<creador>"
								+ nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "creator")) + "</creador>\n"
								+ "<dataCreacio>" + nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "created"))
								+ "</dataCreacio>\n" + "<modificador>"
								+ nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "modifier")) + "</modificador>\n"
								+ "<dataModificacio>" + nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "modified"))
								+ "</dataModificacio>\n" + "<tamany>" + nodeSize + "</tamany>\n" + "<tipusMIME>" + mimeType
								+ "</tipusMIME>\n</documentSimple>\n");
						List<NodeRef> signaturesDoc = new ArrayList<NodeRef>();
						List<AssociationRef> assocs = serviceRegistry.getNodeService().getTargetAssocs(nodeCm,
								QName.createQName(UDL_URI, "signaturesDocument"));
						if (assocs != null) {
							Iterator<AssociationRef> it = assocs.iterator();
							while (it.hasNext()) {
								AssociationRef assoc = (AssociationRef) it.next();
								NodeRef signaturaRef = assoc.getTargetRef();
								if (serviceRegistry.getNodeService().getType(signaturaRef)
										.equals(QName.createQName(UDL_URI, "signaturaDettached"))) {
									signaturesDoc.add(signaturaRef);
								}
							}
						}

						Map<NodeRef, List<NodeRef>> infoSign = infoTransferencia.getInfoSignatures();
						infoSign.put(newNodeRma, signaturesDoc);
						infoTransferencia.setInfoSignatures(infoSign);
					}

					else if (nodeService.getType(nodeCm).toString().equals("{http://www.smile.com/model/udl/1.0}signaturaDettached")) {
						Map<QName, Serializable> props1 = new HashMap<QName, Serializable>();
						props1.put(QName.createQName(UDLRM_URI, "tipus_signatura"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_signatura")));
						props1.put(QName.createQName(UDLRM_URI, "id_document_signat"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "id_document_signat")));
						System.out.println("-------------------------------> id_document_signat: " + nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "id_document_signat")));
						props1.put(QName.createQName(UDLRM_URI, "data_signatura"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "data_signatura")));
						props1.put(QName.createQName(UDLRM_URI, "data_ini_validacio_signatura"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "data_ini_validacio_signatura")));
						props1.put(QName.createQName(UDLRM_URI, "data_fi_validacio_signatura"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "data_fi_validacio_signatura")));
						props1.put(QName.createQName(UDLRM_URI, "validacio_signatura"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "validacio_signatura")));
						props1.put(QName.createQName(UDLRM_URI, "nom_signatari_signatura"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "nom_signatari_signatura")));
						props1.put(QName.createQName(UDLRM_URI, "identificador_signatari_signatura"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "identificador_signatari_signatura")));
						props1.put(QName.createQName(UDLRM_URI, "organitzacio_signatura"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "organitzacio_signatura")));
						props1.put(QName.createQName(UDLRM_URI, "unitat_organica_signatura"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "unitat_organica_signatura")));
						props1.put(QName.createQName(UDLRM_URI, "politica_signatura"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "politica_signatura")));
						props1.put(QName.createQName(UDLRM_URI, "proveidor_certificat_signatura"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "proveidor_certificat_signatura")));
						props1.put(QName.createQName(UDLRM_URI, "tipus_certificat_signatura"),
								nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_certificat_signatura")));
						nodeService.addAspect(newNodeRma, QName.createQName(UDLRM_URI, "signaturaDettached"), props1);
						ContentData contentRef = (ContentData) nodeService.getProperty(newNodeRma, ContentModel.PROP_CONTENT);
						String nodeSize = "";
						String mimeType = "";
						if (contentRef != null) {
							nodeSize = String.valueOf(contentRef.getSize());
							mimeType = contentRef.getMimetype();
						}
						ContentService contentService = serviceRegistry.getContentService();
						ContentReader reader = contentService.getReader(newNodeRma, ContentModel.PROP_CONTENT);
						String sha512 = "";
						if (reader != null) {
							reader.setEncoding(ConstantsUdL.UTF8);
							try {
								sha512 = Cript.sha512(reader.getContentString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						infoTransferencia.setFoliacio(infoTransferencia.getFoliacio() + "<signaturaDettached>\n<nomSignatari>"
								+ nodeService.getProperty(newNodeRma, QName.createQName(UDLRM_URI, "nom_signatari_signatura"))
								+ "</nomSignatari>\n" + newNodeRma.getId() + "</id>\n<sha-512>" + sha512 + "</sha-512>\n" + "<creador>"
								+ nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "creator")) + "</creador>\n"
								+ "<dataCreacio>" + nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "created"))
								+ "</dataCreacio>\n" + "<modificador>"
								+ nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "modifier")) + "</modificador>\n"
								+ "<dataModificacio>" + nodeService.getProperty(newNodeRma, QName.createQName(CM_URI, "modified"))
								+ "</dataModificacio>\n" + "<tamany>" + nodeSize + "</tamany>\n" + "<tipusMIME>" + mimeType
								+ "</tipusMIME>\n</signaturaDettached>\n");
						Map<NodeRef, NodeRef> signDmRm = infoTransferencia.getSignaturesDmRm();
						signDmRm.put(nodeCm, newNodeRma);
						infoTransferencia.setSignaturesDmRm(signDmRm);
					}
				}
			}
		}

		if (root == true) {
			infoTransferencia.setFoliacio("<indexTransferencia>" + infoTransferencia.getFoliacio() + "</indexTransferencia>");
			String filename = "index.xml";
			NodeRef index = fileFolderService.create(indexParent, filename, ContentModel.TYPE_CONTENT).getNodeRef();
			// NodeRef index = (nodeService.createNode(indexParent,
			// ContentModel.ASSOC_CONTAINS, QName.createQName(filename),
			// ContentModel.TYPE_CONTENT).getChildRef());
			ContentWriter writer = serviceRegistry.getContentService().getWriter(index, QName.createQName(CM_URI, "content"), true);
			writer.setEncoding(UTF8);
			writer.setMimetype("text/xml");
			writer.putContent(infoTransferencia.getFoliacio());
			Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			props.put(QName.createQName(CM_URI, "name"), filename);
			props.put(QName.createQName(CM_URI, "title"), filename);
			props.put(QName.createQName(RM_URI, "originator"), "system");
			props.put(QName.createQName(RM_URI, "originatingOrganization"), "system");
			props.put(QName.createQName(RM_URI, "publicationDate"), now);
			props.put(QName.createQName(RM_URI, "identifier"), index.getId());
			props.put(QName.createQName(RM_URI, "dateFiled"), now);
			nodeService.addProperties(index, props);
			
			props = new HashMap<QName, Serializable>();
			props.put(QName.createQName(CM_URI, "name"), filename);
			props.put(ContentModel.PROP_CREATOR, "System");
			props.put(ContentModel.PROP_CREATED, new Date());
			props.put(ContentModel.PROP_MODIFIER, "System");
			props.put(ContentModel.PROP_MODIFIED, new Date());
			props.put(QName.createQName(UDLRM_URI, "grup_creador_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "tipus_documental_documentSimple"), "U01 INFORME");
			props.put(QName.createQName(UDLRM_URI, "tipus_entitat_documentSimple"), "Document");
			props.put(QName.createQName(UDLRM_URI, "categoria_documentSimple"), "Document simple");
			props.put(QName.createQName(UDLRM_URI, "secuencial_identificador_documentSimple"), callWS(DOCUMENT_SIMPLE));
			//props.put(QName.createQName(UDLRM_URI, "secuencial_identificador_documentSimple"), "id de test");
			props.put(QName.createQName(UDLRM_URI, "esquema_identificador_documentSimple"), MASCARA_DOCUMENT_SIMPLE);
			props.put(QName.createQName(UDLRM_URI, "data_inici_documentSimple"), new Date());
			props.put(QName.createQName(UDLRM_URI, "data_fi_documentSimple"), new Date());
			props.put(QName.createQName(UDLRM_URI, "data_registre_entrada_documentSimple"), new Date());
			props.put(QName.createQName(UDLRM_URI, "data_registre_sortida_documentSimple"), new Date());
			props.put(QName.createQName(UDLRM_URI, "classificacio_acces_documentSimple"), "Públic");
			props.put(QName.createQName(UDLRM_URI, "advertencia_seguretat_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "categoria_advertencia_seguretat_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "sensibilitat_dades_caracter_personal_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "verificacio_integritat_algorisme_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "verificacio_integritat_valor_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "idioma_1_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "idioma_2_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "valoracio_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "tipus_dictamen_1_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "tipus_dictamen_2_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "accio_dictaminada_1_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "accio_dictaminada_2_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "suport_origen_documentSimple"), "Electrònic");
			props.put(QName.createQName(UDLRM_URI, "versio_format_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "nom_aplicacio_creacio_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "versio_aplicacio_creacio_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "registre_formats_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "resolucio_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "dimensions_fisiques_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "unitats_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "suport_1_documentSimple"), "Electrònic");
			props.put(QName.createQName(UDLRM_URI, "localitzacio_1_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "localitzacio_2_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "denominacio_estat_documentSimple"), "Original");
			props.put(QName.createQName(UDLRM_URI, "tipus_copia_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "motiu_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "codi_classificacio_1_documentSimple"), "-");
			props.put(QName.createQName(UDLRM_URI, "codi_classificacio_2_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "denominacio_classe_1_documentSimple"), "");
			props.put(QName.createQName(UDLRM_URI, "denominacio_classe_2_documentSimple"), "");
			nodeService.addAspect(index, QName.createQName(UdlProperties.UDLRM_URI, "documentSimple"), props);
			
			PermissionService permissionService = serviceRegistry.getPermissionService();
			permissionService.setInheritParentPermissions(index, false);
			permissionService.setPermission(index, permissionService.getAllAuthorities(), PermissionService.CONSUMER, true);
			serviceRegistry.getOwnableService().setOwner(index, "admin");
		}

		infoTransferencia.setRootRm(indexParent);
		return infoTransferencia;
	}

	private void addAspects(NodeRef nodeCm, NodeRef newNodeRma) {
		NodeService nodeService = serviceRegistry.getNodeService();

		if (nodeService.hasAspect(nodeCm, QName.createQName(UDL_URI, "regulacio"))) {
			Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			props.put(QName.createQName(UDL_URI, "nom_natural_regulacio"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "nom_natural_regulacio")));
			nodeService.addAspect(newNodeRma, QName.createQName(UDL_URI, "regulacio"), props);
		}

		if (nodeService.hasAspect(nodeCm, QName.createQName(UDL_URI, "institucio"))) {
			Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			props.put(QName.createQName(UDL_URI, "nom_natural_institucio"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "nom_natural_institucio")));
			nodeService.addAspect(newNodeRma, QName.createQName(UDL_URI, "institucio"), props);
		}

		if (nodeService.hasAspect(nodeCm, QName.createQName(UDL_URI, "organ"))) {
			Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			props.put(QName.createQName(UDL_URI, "nom_natural_organ"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "nom_natural_organ")));
			nodeService.addAspect(newNodeRma, QName.createQName(UDL_URI, "organ"), props);
		}

		if (nodeService.hasAspect(nodeCm, QName.createQName(UDL_URI, "dispositiu"))) {
			Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			props.put(QName.createQName(UDL_URI, "nom_dispositiu"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "nom_dispositiu")));
			nodeService.addAspect(newNodeRma, QName.createQName(UDL_URI, "dispositiu"), props);
		}

		if (nodeService.hasAspect(nodeCm, QName.createQName(UDL_URI, "persona"))) {
			Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			props.put(QName.createQName(UDL_URI, "secuencial_identificador_persona"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "secuencial_identificador_persona")));
			props.put(QName.createQName(UDL_URI, "esquema_identificador_persona"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "esquema_identificador_persona")));
			props.put(QName.createQName(UDL_URI, "nom_natural_persona"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "nom_natural_persona")));
			props.put(QName.createQName(UDL_URI, "esquema_nom_persona"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "esquema_nom_persona")));
			props.put(QName.createQName(UDL_URI, "data_inici_persona"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "data_inici_persona")));
			props.put(QName.createQName(UDL_URI, "data_fi_persona"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "data_fi_persona")));
			props.put(QName.createQName(UDL_URI, "tipus_contacte_persona"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "tipus_contacte_persona")));
			props.put(QName.createQName(UDL_URI, "dada_contacte_persona"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "dada_contacte_persona")));
			props.put(QName.createQName(UDL_URI, "ocupacio_persona"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "ocupacio_persona")));
			nodeService.addAspect(newNodeRma, QName.createQName(UDL_URI, "persona"), props);
		}

		if (nodeService.hasAspect(nodeCm, QName.createQName(UDL_URI, "signatura"))) {
			Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			props.put(QName.createQName(UDL_URI, "signatura1_nom"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura1_nom")));
			props.put(QName.createQName(UDL_URI, "signatura1_carrec"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura1_carrec")));
			props.put(QName.createQName(UDL_URI, "signatura1_dni"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura1_dni")));
			props.put(QName.createQName(UDL_URI, "signatura1_data"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura1_data")));
			props.put(QName.createQName(UDL_URI, "signatura1_data_fi_certificat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura1_data_fi_certificat")));
			props.put(QName.createQName(UDL_URI, "signatura1_data_caducitat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura1_data_caducitat")));
			props.put(QName.createQName(UDL_URI, "signatura2_nom"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura2_nom")));
			props.put(QName.createQName(UDL_URI, "signatura2_carrec"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura2_carrec")));
			props.put(QName.createQName(UDL_URI, "signatura2_dni"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura2_dni")));
			props.put(QName.createQName(UDL_URI, "signatura2_data"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura2_data")));
			props.put(QName.createQName(UDL_URI, "signatura2_data_fi_certificat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura2_data_fi_certificat")));
			props.put(QName.createQName(UDL_URI, "signatura2_data_caducitat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura2_data_caducitat")));
			props.put(QName.createQName(UDL_URI, "signatura3_nom"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura3_nom")));
			props.put(QName.createQName(UDL_URI, "signatura3_carrec"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura3_carrec")));
			props.put(QName.createQName(UDL_URI, "signatura3_dni"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura3_dni")));
			props.put(QName.createQName(UDL_URI, "signatura3_data"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura3_data")));
			props.put(QName.createQName(UDL_URI, "signatura3_data_fi_certificat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura3_data_fi_certificat")));
			props.put(QName.createQName(UDL_URI, "signatura3_data_caducitat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura3_data_caducitat")));
			props.put(QName.createQName(UDL_URI, "signatura4_nom"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura4_nom")));
			props.put(QName.createQName(UDL_URI, "signatura4_carrec"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura4_carrec")));
			props.put(QName.createQName(UDL_URI, "signatura4_dni"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura4_dni")));
			props.put(QName.createQName(UDL_URI, "signatura4_data"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura4_data")));
			props.put(QName.createQName(UDL_URI, "signatura4_data_fi_certificat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura4_data_fi_certificat")));
			props.put(QName.createQName(UDL_URI, "signatura4_data_caducitat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura4_data_caducitat")));
			props.put(QName.createQName(UDL_URI, "signatura5_nom"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura5_nom")));
			props.put(QName.createQName(UDL_URI, "signatura5_carrec"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura5_carrec")));
			props.put(QName.createQName(UDL_URI, "signatura5_dni"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura5_dni")));
			props.put(QName.createQName(UDL_URI, "signatura5_data"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura5_data")));
			props.put(QName.createQName(UDL_URI, "signatura5_data_fi_certificat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura5_data_fi_certificat")));
			props.put(QName.createQName(UDL_URI, "signatura5_data_caducitat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura5_data_caducitat")));
			props.put(QName.createQName(UDL_URI, "signatura6_nom"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura6_nom")));
			props.put(QName.createQName(UDL_URI, "signatura6_carrec"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura6_carrec")));
			props.put(QName.createQName(UDL_URI, "signatura6_dni"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura6_dni")));
			props.put(QName.createQName(UDL_URI, "signatura6_data"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura6_data")));
			props.put(QName.createQName(UDL_URI, "signatura6_data_fi_certificat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura6_data_fi_certificat")));
			props.put(QName.createQName(UDL_URI, "signatura6_data_caducitat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura6_data_caducitat")));
			props.put(QName.createQName(UDL_URI, "signatura7_nom"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura7_nom")));
			props.put(QName.createQName(UDL_URI, "signatura7_carrec"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura7_carrec")));
			props.put(QName.createQName(UDL_URI, "signatura7_dni"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura7_dni")));
			props.put(QName.createQName(UDL_URI, "signatura7_data"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura7_data")));
			props.put(QName.createQName(UDL_URI, "signatura7_data_fi_certificat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura7_data_fi_certificat")));
			props.put(QName.createQName(UDL_URI, "signatura7_data_caducitat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura7_data_caducitat")));
			props.put(QName.createQName(UDL_URI, "signatura8_nom"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura8_nom")));
			props.put(QName.createQName(UDL_URI, "signatura8_carrec"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura8_carrec")));
			props.put(QName.createQName(UDL_URI, "signatura8_dni"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura8_dni")));
			props.put(QName.createQName(UDL_URI, "signatura8_data"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura8_data")));
			props.put(QName.createQName(UDL_URI, "signatura8_data_fi_certificat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura8_data_fi_certificat")));
			props.put(QName.createQName(UDL_URI, "signatura8_data_caducitat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura8_data_caducitat")));
			props.put(QName.createQName(UDL_URI, "signatura9_nom"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura9_nom")));
			props.put(QName.createQName(UDL_URI, "signatura9_carrec"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura9_carrec")));
			props.put(QName.createQName(UDL_URI, "signatura9_dni"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura9_dni")));
			props.put(QName.createQName(UDL_URI, "signatura9_data"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura9_data")));
			props.put(QName.createQName(UDL_URI, "signatura9_data_fi_certificat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura9_data_fi_certificat")));
			props.put(QName.createQName(UDL_URI, "signatura9_data_caducitat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura9_data_caducitat")));
			props.put(QName.createQName(UDL_URI, "signatura10_nom"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura10_nom")));
			props.put(QName.createQName(UDL_URI, "signatura10_carrec"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura10_carrec")));
			props.put(QName.createQName(UDL_URI, "signatura10_dni"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura10_dni")));
			props.put(QName.createQName(UDL_URI, "signatura10_data"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura10_data")));
			props.put(QName.createQName(UDL_URI, "signatura10_data_fi_certificat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura10_data_fi_certificat")));
			props.put(QName.createQName(UDL_URI, "signatura10_data_caducitat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura10_data_caducitat")));
			props.put(QName.createQName(UDL_URI, "signatura11_nom"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura11_nom")));
			props.put(QName.createQName(UDL_URI, "signatura11_carrec"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura11_carrec")));
			props.put(QName.createQName(UDL_URI, "signatura11_dni"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura11_dni")));
			props.put(QName.createQName(UDL_URI, "signatura11_data"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura11_data")));
			props.put(QName.createQName(UDL_URI, "signatura11_data_fi_certificat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura11_data_fi_certificat")));
			props.put(QName.createQName(UDL_URI, "signatura11_data_caducitat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura11_data_caducitat")));
			props.put(QName.createQName(UDL_URI, "signatura12_nom"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura12_nom")));
			props.put(QName.createQName(UDL_URI, "signatura12_carrec"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura12_carrec")));
			props.put(QName.createQName(UDL_URI, "signatura12_dni"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura12_dni")));
			props.put(QName.createQName(UDL_URI, "signatura12_data"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura12_data")));
			props.put(QName.createQName(UDL_URI, "signatura12_data_fi_certificat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura12_data_fi_certificat")));
			props.put(QName.createQName(UDL_URI, "signatura12_data_caducitat"),
					nodeService.getProperty(nodeCm, QName.createQName(UDL_URI, "signatura12_data_caducitat")));

			nodeService.addAspect(newNodeRma, QName.createQName(UDL_URI, "signatura"), props);
		}
	}

	/**
	 * Marks the record or record folder as declared record
	 * 
	 * @param nodeRef
	 * @throws DipuLleidaException
	 */
	protected void declareRecordTree(NodeRef nodeRef, ServiceRegistry serviceRegistry) throws Exception {
		NodeService nodeService = serviceRegistry.getNodeService();
		FileFolderService fileFolderService = serviceRegistry.getFileFolderService();
		FileInfo fileInfo = fileFolderService.getFileInfo(nodeRef);
		if (fileInfo != null) {
			if (fileInfo.isFolder()) {
				List<ChildAssociationRef> childAsocs = nodeService.getChildAssocs(nodeRef);
				for (ChildAssociationRef childAsocRef : childAsocs) {
					declareRecordTree(childAsocRef.getChildRef(), serviceRegistry);
				}
			} else {
				declareRecord(nodeRef, serviceRegistry);
			}
		}
	}

	/**
	 * Marks the record or record folder as declared record
	 * 
	 * @param nodeRef
	 * @throws DipuLleidaException
	 */
	static protected void declareRecord(NodeRef nodeRef, ServiceRegistry serviceRegistry) throws Exception {
		List<String> missingProperties = new ArrayList<String>();
		NodeService nodeService = serviceRegistry.getNodeService();
		OwnableService ownableService = serviceRegistry.getOwnableService();
		if (nodeService.hasAspect(nodeRef, ASPECT_DECLARED_RECORD) == false) {
			boolean isMandatoryPropertiesSet = mandatoryPropertiesSet(nodeRef, missingProperties, serviceRegistry);
			if (isMandatoryPropertiesSet == true) {
				Map<QName, Serializable> decRecProps = new HashMap<QName, Serializable>(1);
				decRecProps.put(PROP_DECLARED_AT, new Date());
				decRecProps.put(PROP_DECLARED_BY, AuthenticationUtil.getRunAsUser());
				nodeService.addAspect(nodeRef, ASPECT_DECLARED_RECORD, decRecProps);
				ownableService.setOwner(nodeRef, OwnableService.NO_OWNER);
			} else {
				throw new Exception("Faltan las siguientes propiedades obligatorias: " + missingProperties);
			}
		}
	}

	/**
	 * Return true if all mandatory properties are completed
	 * 
	 * @param nodeRef
	 * @param missingProperties
	 * @return
	 */
	static protected boolean mandatoryPropertiesSet(NodeRef nodeRef, List<String> missingProperties, ServiceRegistry serviceRegistry) {
		boolean result = true;
		NodeService nodeService = serviceRegistry.getNodeService();
		DictionaryService dictionaryService = serviceRegistry.getDictionaryService();
		Map<QName, Serializable> nodeRefProps = nodeService.getProperties(nodeRef);
		QName nodeRefType = nodeService.getType(nodeRef);
		TypeDefinition typeDef = dictionaryService.getType(nodeRefType);

		for (PropertyDefinition propDef : typeDef.getProperties().values()) {
			if (propDef.isMandatory() == true) {
				if (nodeRefProps.get(propDef.getName()) == null) {
					missingProperties.add(propDef.getName().toString());
					result = false;
					break;
				}
			}
		}

		if (result != false) {
			Set<QName> aspects = nodeService.getAspects(nodeRef);
			for (QName aspect : aspects) {
				AspectDefinition aspectDef = dictionaryService.getAspect(aspect);
				for (PropertyDefinition propDef : aspectDef.getProperties().values()) {
					if (propDef.isMandatory() == true) {
						if (nodeRefProps.get(propDef.getName()) == null) {
							missingProperties.add(propDef.getName().toString());
							result = false;
							break;
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * Transfer Content Management nodes recursively to a Records Management
	 * site.
	 * 
	 * @param username
	 * @param expPath
	 * @param expName
	 * @param req
	 * @return root Records Management transfered node.
	 * @throws Exception
	 */
	protected static String transferirNodes(String username, String expPath, String expName, WebScriptRequest req) throws Exception {
		// create HttpClient object
		HttpClient client = new HttpClient();

		// pre-authenticate with alfresco server
		client.getParams().setAuthenticationPreemptive(true);
		UdlProperties props = new UdlProperties();
		Credentials loginCreds = new UsernamePasswordCredentials(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD));

		// Use AuthScope.ANY since the HttpClient connects to just one URL
		client.getState().setCredentials(AuthScope.ANY, loginCreds);
		PostMethod post = new PostMethod(req.getServerPath() + req.getContextPath() + URL_WEBSCRIPT_TRANSFER_NODES);
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		// Alfresco only reads the POST parameters if they are in the body of
		// the request, so we need to create a RequestEntity to populate the
		// body.
		Part[] parts = { new StringPart(FORM_PARAM_USERNAME, username, UTF8), new StringPart(FORM_PARAM_EXP_PATH, expPath, UTF8),
				new StringPart(FORM_PARAM_EXP_NAME, expName, UTF8) };
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		post.setRequestEntity(entity);

		// submit the request and get the response code
		int statusCode = client.executeMethod(post);
		InputStream responseBody = post.getResponseBodyAsStream();
		String nodeRef = DocumentUtils.convertStreamToString(responseBody);

		// Check for code 200
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception(nodeRef);
		}

		return nodeRef;
	}

	/**
	 * @param username
	 * @param nodeRef
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected static String declareRecordRecursive(String username, String nodeRef, WebScriptRequest req) throws Exception {
		// create HttpClient object
		HttpClient client = new HttpClient();

		// pre-authenticate with alfresco server
		client.getParams().setAuthenticationPreemptive(true);
		UdlProperties udlProperties = new UdlProperties();
		Credentials loginCreds = new UsernamePasswordCredentials(udlProperties.getProperty(ADMIN_USERNAME),
				udlProperties.getProperty(ADMIN_PASSWORD));

		// Use AuthScope.ANY since the HttpClient connects to just one URL
		client.getState().setCredentials(AuthScope.ANY, loginCreds);
		PostMethod post = new PostMethod(req.getServerPath() + req.getContextPath() + URL_WEBSCRIPT_DECLARE_RECORD);
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		// Alfresco only reads the POST parameters if they are in the body of
		// the request, so we need to create a RequestEntity to populate the
		// body.
		Part[] parts = { new StringPart(FORM_PARAM_USERNAME, username, UTF8), new StringPart(FORM_PARAM_NODEREF, nodeRef, UTF8),
				new StringPart(ADMIN_USERNAME, udlProperties.getProperty(ADMIN_USERNAME), UTF8),
				new StringPart(ADMIN_PASSWORD, udlProperties.getProperty(ADMIN_PASSWORD), UTF8),
				new StringPart(FORM_PARAM_SERVER, req.getServerPath(), UTF8) };
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		post.setRequestEntity(entity);

		// submit the request and get the response code
		int statusCode = client.executeMethod(post);
		InputStream responseBody = post.getResponseBodyAsStream();
		String ref = DocumentUtils.convertStreamToString(responseBody);

		// Check for code 200
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception(ref);
		}

		return ref;
	}

	/**
	 * Before to run this, cutoff action must be added to the category where is
	 * placed the folder (expediente) and all the document contained in the
	 * folder must be declared as a record.
	 * 
	 * @param username
	 * @param nodeRef
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected static String cutoff(String username, String nodeRef, WebScriptRequest req) throws Exception {
		// create HttpClient object
		HttpClient client = new HttpClient();

		// pre-authenticate with alfresco server
		client.getParams().setAuthenticationPreemptive(true);
		UdlProperties udlProperties = new UdlProperties();
		Credentials loginCreds = new UsernamePasswordCredentials(udlProperties.getProperty(ADMIN_USERNAME),
				udlProperties.getProperty(ADMIN_PASSWORD));

		// Use AuthScope.ANY since the HttpClient connects to just one URL
		client.getState().setCredentials(AuthScope.ANY, loginCreds);
		PostMethod post = new PostMethod(req.getServerPath() + req.getContextPath() + URL_WEBSCRIPT_CUTOFF);
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		// Alfresco only reads the POST parameters if they are in the body of
		// the request, so we need to create a RequestEntity to populate the
		// body.
		Part[] parts = { new StringPart(FORM_PARAM_USERNAME, username, UTF8), new StringPart(FORM_PARAM_NODEREF, nodeRef, UTF8),
				new StringPart(ADMIN_USERNAME, udlProperties.getProperty(ADMIN_USERNAME), UTF8),
				new StringPart(ADMIN_PASSWORD, udlProperties.getProperty(ADMIN_PASSWORD), UTF8),
				new StringPart(FORM_PARAM_SERVER, req.getServerPath(), UTF8) };
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		post.setRequestEntity(entity);

		// submit the request and get the response code
		int statusCode = client.executeMethod(post);
		InputStream responseBody = post.getResponseBodyAsStream();
		String ref = DocumentUtils.convertStreamToString(responseBody);

		// Check for code 200
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception(ref);
		}

		return ref;
	}

	/**
	 * Transfer the aspects of the record (from DM record to RM record).
	 * 
	 * @param nodeRef
	 * @throws Exception
	 */
	protected void addAspects(NodeRef nodeRef, Set<QName> aspects, Map<QName, Serializable> allNodeProps) throws Exception {
		NodeService nodeService = serviceRegistry.getNodeService();
		DictionaryService dictionaryService = serviceRegistry.getDictionaryService();
		Iterator<QName> it = aspects.iterator();

		while (it.hasNext()) {
			QName aspect = (QName) it.next();
			Map<QName, PropertyDefinition> aspectPropDefs = dictionaryService.getAspect(aspect).getProperties();
			Map<QName, Serializable> nodeProps = new HashMap<QName, Serializable>(aspectPropDefs.size());
			Iterator<QName> iter = aspectPropDefs.keySet().iterator();

			while (iter.hasNext()) {
				QName propQName = (QName) iter.next();

				if (aspect.getNamespaceURI().equals("http://www.smile.com/model/udl/1.0")) {
					Serializable value = allNodeProps.get(propQName);
					if (value != null) {
						nodeProps.put(propQName, value);
					}
				}
			}

			if (aspect.getNamespaceURI().equals("http://www.smile.com/model/udl/1.0")) {
				nodeService.addAspect(nodeRef, aspect, nodeProps);
			}
		}
	}
	
	/**
	 * Crida un webservice de la UdL que genera un identificador en funció del
	 * tipus documental rebut com a paràmetre.
	 * 
	 * @param tipus
	 * @return
	 */
	private String callWS(String type) throws Exception {
		String id = "";
		Service service = new Service();
		Call call = (Call) service.createCall();

		// Call to first service (IniciaGeneraCodi)
		call.setProperty(Call.SOAPACTION_USE_PROPERTY, new Boolean(true));
		call.setProperty(Call.SOAPACTION_URI_PROPERTY, "http://tempuri.org/IniciaGeneraCodi");
		call.setTargetEndpointAddress(new URL(WS_URL));
		call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/", WS_INICI_GENERA_CODI));
		call.addParameter(new javax.xml.namespace.QName("http://tempuri.org/", "AppId"), XMLType.XSD_STRING, ParameterMode.IN);
		call.addParameter(new javax.xml.namespace.QName("http://tempuri.org/", "ObjectId"), XMLType.XSD_STRING, ParameterMode.IN);
		call.setReturnType(XMLType.XSD_STRING);

		id = (String) call.invoke(new Object[] { WS_APP_ID, type });

		// Call to second service (GeneraCodi)
		call = (Call) service.createCall();
		call.setProperty(Call.SOAPACTION_URI_PROPERTY, "http://tempuri.org/GeneraCodi");
		call.setTargetEndpointAddress(new URL(WS_URL));
		call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/", WS_GENERA_CODI));
		call.addParameter(new javax.xml.namespace.QName("http://tempuri.org/", "Key"), XMLType.XSD_STRING, ParameterMode.IN);
		call.setReturnType(XMLType.XSD_STRING);

		id = (String) call.invoke(new Object[] { encodeKey(id) });
		
		return id;
	}
	
	/**
	 * Built encoded key with SHA2 algorithm.
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private String encodeKey(String id) throws Exception {
		String cadenaConfianza = "kax9fejew2wzA89BSDFG.JAe9edg9";
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		StringBuffer hexString = new StringBuffer();

		String key = id + cadenaConfianza;
		byte[] hash = digest.digest(key.getBytes("UTF-8"));

		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}

			hexString.append(hex);
		}

		return hexString.toString();
	}
}