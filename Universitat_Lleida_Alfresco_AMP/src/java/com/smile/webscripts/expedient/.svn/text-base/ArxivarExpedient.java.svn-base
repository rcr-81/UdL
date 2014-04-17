package com.smile.webscripts.expedient;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.audit.AuditComponent;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
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

public class ArxivarExpedient extends DeclarativeWebScript implements ConstantsUdL {

	private ServiceRegistry serviceRegistry;
	private AuditComponent auditComponent;
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	public void setAuditComponent(AuditComponent auditComponent) {
		this.auditComponent = auditComponent;
	}
	
	/**
	 * Change state metadata of a folder (expediente) and transfer
	 */
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		HashMap<String, Object> model = new HashMap<String,Object>();

		try {
			Element args = Arguments.getArguments(req);

			String username = args.getElementsByTagName(FORM_PARAM_USERNAME).item(0).getFirstChild().getNodeValue();		
			model.put(FTL_USERNAME, username);
			Impersonate.impersonate(username);

			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();

			String expID = args.getElementsByTagName(FORM_PARAM_EXP_ID).item(0).getFirstChild().getNodeValue();			
			NodeRef expedientNodeRef = new NodeRef(AUDIT_NODEREF_PREFIX + expID);
			ScriptNode expedient = new ScriptNode(expedientNodeRef, serviceRegistry, scope);
			String nodeRefExp = expedient.getNodeRef().toString();
			String typeExp = expedient.getTypeShort();
			String siteExp = expedient.getSiteShortName();
			
			String ref = TransferirExpedient.transferirNodes(username, expedient.getDisplayPath(), expedient.getName(), req);	
			TransferirExpedient.declareRecordRecursive(username, ref, req);
			TransferirExpedient.cutoff(username, ref, req);
			ScriptNode expedientRma = new ScriptNode(new NodeRef(ref), serviceRegistry, scope);				
			expedientRma.getProperties().put("cm:estat", "arxivat");
			expedientRma.save();
			model.put(FTL_EXPEDIENT, expedientRma);
			model.put(FTL_SUCCESS, String.valueOf(true));
			
			//Auditar archivar expedient				
			if(typeExp.equals("udl:expedient")){
				AuditUdl.auditRecord(auditComponent, username, AUDIT_NODEREF_PREFIX + nodeRefExp, AUDIT_ACTION_ARCHIVE_EXPEDIENT, typeExp, siteExp);
			}
			else if(typeExp.equals("udl:agregacio")){
				AuditUdl.auditRecord(auditComponent, username, AUDIT_NODEREF_PREFIX + nodeRefExp, AUDIT_ACTION_ARCHIVE_AGREGACIO, typeExp, siteExp);
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