package com.smile.webscripts.audit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.audit.AuditComponent;
import org.alfresco.repo.audit.model.AuditApplication;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.smile.webscripts.helper.ConstantsUdL;

public class AuditUdl implements ConstantsUdL{
	private static final Log logger = LogFactory.getLog(AuditUdl.class);
	
	public static void auditRecord(AuditComponent auditComponent, String username, String nodeRef,String action, String type, String site){
		Map<String, Serializable> auditMap = new HashMap<String, Serializable>();
		auditMap.put(AuditApplication.buildPath("/userName"), username);
		auditMap.put(AuditApplication.buildPath("/action"), action);
		auditMap.put(AuditApplication.buildPath("/noderef"), nodeRef);
		auditMap.put(AuditApplication.buildPath("/type"), type);
		auditMap.put(AuditApplication.buildPath("/site"), site);
		
		auditComponent.recordAuditValues(AUDIT_APLICATION_PATH, auditMap);
		
		if(logger.isDebugEnabled()){
			logger.debug("Audit source = \n "+AUDIT_APLICATION_PATH);
			logger.debug("Audit values = \n "+auditMap);
		}
	}
}
