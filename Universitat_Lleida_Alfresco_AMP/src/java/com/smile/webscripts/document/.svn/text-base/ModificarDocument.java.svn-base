package com.smile.webscripts.document;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.audit.AuditComponent;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.w3c.dom.Element;

import com.smile.webscripts.audit.AuditUdl;
import com.smile.webscripts.helper.Arguments;
import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Impersonate;

public class ModificarDocument extends DeclarativeWebScript implements ConstantsUdL {

	private ServiceRegistry serviceRegistry;
	private AuditComponent auditComponent;
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	public void setAuditComponent(AuditComponent auditComponent) {
		this.auditComponent = auditComponent;
	}

	/**
	 * Upload an encoded Base64 file to the repository and decode it back once it's uploaded.
	 */
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		HashMap<String, Object> model = new HashMap<String,Object>();
						
		try {
			Element args = Arguments.getArguments(req);
			String username = args.getElementsByTagName(FORM_PARAM_USERNAME).item(0).getFirstChild().getNodeValue();

			model.put(FTL_USERNAME, username);			
			Impersonate.impersonate(username);

			String ref = DocumentUtils.actualitzarDocumentBase64(req, args).trim();
			NodeRef nodeRef = new NodeRef(ref);

			String content = args.getElementsByTagName(FORM_PARAM_DOC_CONTENT).item(0).getFirstChild().getNodeValue();
			byte[] contentDecoded = es.mityc.firmaJava.libreria.utilidades.Base64.decode(content);
			ContentWriter writer = serviceRegistry.getContentService().getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
			writer.putContent(new ByteArrayInputStream(contentDecoded));	
			
			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();
			ScriptNode document = new ScriptNode(nodeRef, serviceRegistry, scope);

			model.put(FTL_DOCUMENT, document);
			model.put(FTL_SUCCESS, String.valueOf(true));
				
			//Auditar creación de documento	
			String type = document.getTypeShort();
			String site = document.getSiteShortName();
			if(type.equals("udl:documentSimple")){
				AuditUdl.auditRecord(auditComponent, username, document.getNodeRef().toString(), AUDIT_ACTION_UPDATE_DOCUMENT_SIMPLE, type, site);
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