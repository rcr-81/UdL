package com.smile.webscripts.document;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Impersonate;

public class AttachSignatures extends DeclarativeWebScript implements ConstantsUdL {

	private ServiceRegistry serviceRegistry;
	private Repository repository;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	public void setRepository(Repository repo) {
		this.repository = repo;
	}

	/**
	 * Duplicates the original document, adding the prefix "attachedSignatures-" to its name, 
	 * and replaces its content with XML data containing original document data, and the 
	 * signatures content list contained in the signatures folder associated with the 
	 * original document (a folder with the name of the document and the prefix "signatures-".
	 */
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		HashMap<String, Object> model = new HashMap<String,Object>();
		String username = req.getParameter(FORM_PARAM_USERNAME);
		model.put(FTL_USERNAME, username);

		try {
			Impersonate.impersonate(username);

			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();

			String docPath = req.getParameter(FORM_PARAM_DOC_PATH);
			String docName = req.getParameter(FORM_PARAM_DOC_NAME);
			String pathDoc = docPath + "/" + docName;						
			NodeRef rootHome = repository.getRootHome();

			ScriptNode root = new ScriptNode(rootHome, serviceRegistry, scope);
			ScriptNode doc = root.childByNamePath(pathDoc);
			if(doc == null){
				model.put(FTL_ERROR, "Document at '" + pathDoc + "' not found.");
				model.put(FTL_SUCCESS, String.valueOf(false));
			}
			else {
				ContentReader reader = serviceRegistry.getContentService().getReader(doc.getNodeRef(), ContentModel.PROP_CONTENT);
				String content = reader.getContentString();
				//copiamos el documento original
				String ref = DocumentUtils.pujarDocumentBase64(req, "-", ATTACHED_SIGNATURES_PREFIX + docName, docPath, "udl:attachedSignaturesDocument", null).trim();
				NodeRef nodeRef = new NodeRef(ref);
				
				byte[] xmlTag = XML_TAG.getBytes();
				byte[] endXmlTag = END_XML_TAG.getBytes();
				byte[] documentTag = DOCUMENT_TAG.getBytes();
				byte[] endDocumentTag = END_DOCUMENT_TAG.getBytes();
				byte[] signaturaTag = SIGNATURA_TAG.getBytes();
				byte[] endSignaturaTag = END_SIGNATURA_TAG.getBytes();
				byte[] signaturesTag = SIGNATURES_TAG.getBytes();
				byte[] endSignaturesTag = END_SIGNATURES_TAG.getBytes();

				ContentService contentService = serviceRegistry.getContentService();
				ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
				OutputStream os = writer.getContentOutputStream();
				os.write(xmlTag);
				os.write(documentTag);
				os.write(content.getBytes());
				os.write(endDocumentTag);
				os.write(signaturesTag);

				String folderSignaturesName = SIGNATURES_FOLDER_PREFIX + req.getParameter(FORM_PARAM_DOC_NAME);
				String folderSignaturesPath = DocumentUtils.obtenirFolderSignatures(req, username, docPath, folderSignaturesName);

				ScriptNode folderSignatures = root.childByNamePath(folderSignaturesPath);
				if(folderSignatures == null){
					model.put(FTL_ERROR, "Folder at '" + folderSignaturesPath + "' not found.");
					model.put(FTL_SUCCESS, String.valueOf(false));
				}			
				
				List<ChildAssociationRef> children = serviceRegistry.getNodeService().getChildAssocs(folderSignatures.getNodeRef());
				for (ChildAssociationRef childAssoc : children) {
					NodeRef childNodeRef = childAssoc.getChildRef();
					ContentReader readerSignatura = serviceRegistry.getContentService().getReader(childNodeRef, ContentModel.PROP_CONTENT);
					os.write(signaturaTag);					
					os.write(readerSignatura.getContentString().getBytes());
					os.write(endSignaturaTag);		
				}

				os.write(endSignaturesTag);
				os.write(endXmlTag);
				os.flush();
				os.close();

				ScriptNode document = new ScriptNode(nodeRef, serviceRegistry, scope);
				model.put(FTL_DOCUMENT, document);
				model.put(FTL_SUCCESS, String.valueOf(true));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			model.put(FTL_ERROR, e.getMessage());
			model.put(FTL_SUCCESS, String.valueOf(false));		
		}	

		return model;
	}
}