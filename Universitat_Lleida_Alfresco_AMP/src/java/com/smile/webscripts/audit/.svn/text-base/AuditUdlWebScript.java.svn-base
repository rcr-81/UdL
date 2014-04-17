package com.smile.webscripts.audit;

import java.io.IOException;

import org.alfresco.repo.audit.AuditComponent;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;


public class AuditUdlWebScript extends AbstractWebScript {
	

	private AuditComponent auditComponent;

	public void setAuditComponent(AuditComponent auditComponent) {
		this.auditComponent = auditComponent;
	}

	public void execute(WebScriptRequest req, WebScriptResponse res)throws IOException {
		
		String userName = req.getParameter("userName");
		String nodeRef = req.getParameter("nodeRef");
		String action = req.getParameter("action");
		String type = req.getParameter("type");
		String site = req.getParameter("site");
			
		AuditUdl.auditRecord(auditComponent, userName,nodeRef,action, type, site);
		
	}

	
}
