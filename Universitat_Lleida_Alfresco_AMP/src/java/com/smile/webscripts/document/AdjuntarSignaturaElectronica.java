package com.smile.webscripts.document;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.w3c.dom.Element;

import com.smile.webscripts.helper.Arguments;
import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Impersonate;

/**
 * Upload an encoded Base64 signature file to the repository and decode it back once it's uploaded.
 * The signature file is stored in a espace with the same name of the signed document with the prefix "signatures-".
 */
public class AdjuntarSignaturaElectronica extends DeclarativeWebScript implements ConstantsUdL {

	private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		HashMap<String, Object> model = new HashMap<String,Object>();

		try {			
			Element args = Arguments.getArguments(req);

			String username = args.getElementsByTagName(FORM_PARAM_USERNAME).item(0).getFirstChild().getNodeValue();							
			model.put(FTL_USERNAME, username);
			Impersonate.impersonate(username);

			String docID = args.getElementsByTagName(FORM_PARAM_DOC_ID).item(0).getFirstChild().getNodeValue();			
			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();

			NodeRef documentToSignNodeRef = new NodeRef("workspace://SpacesStore/" + docID);
			ScriptNode documentToSign = new ScriptNode(documentToSignNodeRef, serviceRegistry, scope);			
			String folderSignaturesName = SIGNATURES_FOLDER_PREFIX + documentToSign.getName();
			String folderSignaturesPath = DocumentUtils.obtenirFolderSignatures(req, username, documentToSign.getDisplayPath(), folderSignaturesName);

			String ref = DocumentUtils.pujarSignaturaBase64(req, args, folderSignaturesPath).trim();
			NodeRef nodeRef = new NodeRef(ref);

			ContentReader reader = serviceRegistry.getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);
			byte[] contentDecoded = es.mityc.firmaJava.libreria.utilidades.Base64.decode(reader.getContentString());
			ContentWriter writer = serviceRegistry.getContentService().getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
			writer.putContent(new ByteArrayInputStream(contentDecoded));		

			ScriptNode document = new ScriptNode(nodeRef, serviceRegistry, scope);	
			model.put(FTL_DOCUMENT, document);
			model.put(FTL_SUCCESS, String.valueOf(true));
		}
		catch (Exception e) {
			e.printStackTrace();
			model.put(FTL_ERROR, e.getMessage());
			model.put(FTL_SUCCESS, String.valueOf(false));
		}

		return model;
	}
}