<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

var success = false;
var carpeta = search.findNode("workspace://SpacesStore/" + arguments.carpetaID);

if (carpeta == undefined || !carpeta.isContainer) {		
	model.error = "Carpeta with id '" + arguments.carpetaID + "' not found.";
}
else {
	try {
		var carp = new Array();
		carp.id = carpeta.id;
		carp.nodeRef = carpeta.nodeRef;
		carp.displayPath = carpeta.displayPath;
		carp.name = carpeta.properties.name;
		carp.type = carpeta.typeShort;
		carp.site = carpeta.siteShortName;
		model.carpeta = carp;
		success = carpeta.remove();	
		
		//Audit esborrar carpeta
		if(carp.type == "udl:expedient") {
		    audit("url_audit_delete_expedient", carp.nodeRef, carp.type, carp.site, arguments.username);	
		}
		else if(carp.type == "udl:agregacio") {
		    audit("url_audit_delete_agregacio", carp.nodeRef, carp.type, carp.site, arguments.username);	
		}
		else if(carp.type == "udl:serie") {
		    audit("url_audit_delete_serie", carp.nodeRef, carp.type, carp.site, arguments.username);	
		}
		else if(carp.type == "udl:fons") {
		    audit("url_audit_delete_fons", carp.nodeRef, carp.type, carp.site, arguments.username);	
		}
		else if(carp.type == "udl:grupDeFons") {		
		    audit("url_audit_delete_grup_de_fons", carp.nodeRef, carp.type, carp.site, arguments.username);
		}		
	}
	catch(e){
		model.error = handleError(e);
	}
}

model.success = String(success);