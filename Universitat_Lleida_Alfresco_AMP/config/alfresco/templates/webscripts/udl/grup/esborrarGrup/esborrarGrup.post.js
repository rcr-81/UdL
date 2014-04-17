<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

var success = false;

try {
	var grup = people.getGroup("GROUP_" + String(arguments.nomGrup));
	model.nomGrup = grup.name;
	model.idGrup = grup.id;
	people.deleteGroup(grup);		
	success = true;
	//audit("url_audit_view_documentSimple",arguments.docID, document.getTypeShort(), document.getSiteShortName(),arguments.username);	
}
catch(e){
	model.error = handleError(e);
}

model.success = String(success);