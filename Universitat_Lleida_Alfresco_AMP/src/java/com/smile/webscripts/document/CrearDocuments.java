package com.smile.webscripts.document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.audit.AuditComponent;
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

import com.smile.webscripts.audit.AuditUdl;
import com.smile.webscripts.helper.Arguments;
import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Impersonate;

public class CrearDocuments extends DeclarativeWebScript implements ConstantsUdL {

	private ServiceRegistry serviceRegistry;
	private AuditComponent auditComponent;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	public void setAuditComponent(AuditComponent auditComponent) {
		this.auditComponent = auditComponent;
	}

	/**
	 * Upload a maximum of 5 encoded Base64 files to the repository and decode them back once they're uploaded.
	 */
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		HashMap<String, Object> model = new HashMap<String,Object>();
				
		try {
			Element args = Arguments.getArguments(req);
			
			String username = args.getElementsByTagName(FORM_PARAM_USERNAME).item(0).getFirstChild().getNodeValue();
			model.put(FTL_USERNAME, username);	
			Impersonate.impersonate(username);

			List<DocumentUploadInfo> nodeRefs = DocumentUtils.pujarDocumentsBase64(req, args);
			List<Map<String, Object>> infoList = new ArrayList<Map<String,Object>>();
			
			for (DocumentUploadInfo uploadInfo : nodeRefs) {		
				boolean success = uploadInfo.isSuccess();
				HashMap<String, Object> info = new HashMap<String,Object>();						

				if(success){
					NodeRef nodeRef = new NodeRef(uploadInfo.getNodeRef());
					ContentReader reader = serviceRegistry.getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);
					byte[] content = es.mityc.firmaJava.libreria.utilidades.Base64.decode(reader.getContentString());
					ContentWriter writer = serviceRegistry.getContentService().getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
					writer.putContent(new ByteArrayInputStream(content));		
					
					Context cx = Context.enter();
					Scriptable scope = cx.initStandardObjects();
					ScriptNode document = new ScriptNode(nodeRef, serviceRegistry, scope);
					info.put(FTL_DOCUMENT, document);
					
					//Auditar creación de documento	
					String type = document.getTypeShort();
					String site = document.getSiteShortName();					
					if(type.equals("udl:documentSimple")){
						AuditUdl.auditRecord(auditComponent, username, document.getNodeRef().toString(), AUDIT_ACTION_CREATE_DOCUMENT_SIMPLE, type, site);
					}
				}	

				info.put(FTL_ERROR, uploadInfo.getError());
				info.put(FTL_SUCCESS, String.valueOf(success));
				infoList.add(info);
			}					

			model.put(FTL_INFO_LIST, infoList);
			model.put(FTL_SUCCESS, true);
		}
		catch (Exception e) {
			e.printStackTrace();
			model.put(FTL_ERROR, e.getMessage());
			model.put(FTL_SUCCESS, false);
		}	
		
		return model;
	}
}