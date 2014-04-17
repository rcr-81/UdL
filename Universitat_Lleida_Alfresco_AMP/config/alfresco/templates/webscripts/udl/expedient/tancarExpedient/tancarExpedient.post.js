<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

var success = false;
var expedient = search.findNode("workspace://SpacesStore/" + arguments.expID);
if (expedient == undefined || !expedient.isContainer) {
	model.error = "Expedient with id '" + arguments.expID + "' not found.";
}
else {	
	try{
		expedient.properties["udl:estat"] = "tancat";
		expedient.save();
		success = true;

		//audit tancar expedient
		var type = expedient.getTypeShort(); 
		var site = expedient.getSiteShortName();
		
		if(type == "udl:expedient") {
		    audit("url_audit_close_expedient", arguments.expID, type, site, arguments.username);	
		}
		else if(type == "udl:agregacio") {
		    audit("url_audit_close_agregacio", arguments.expID, type, site, arguments.username);
		}

	}
	catch(e){
		model.error = handleError(e);
	}
}

model.expedient = expedient;
model.success = String(success);