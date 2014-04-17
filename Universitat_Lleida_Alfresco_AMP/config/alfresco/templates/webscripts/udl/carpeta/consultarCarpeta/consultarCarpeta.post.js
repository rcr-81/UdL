<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/metadataHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

var success = false;
var carpeta = search.findNode("workspace://SpacesStore/" + arguments.carpetaID);
if (carpeta == undefined || !carpeta.isContainer) {
	model.error = "Carpeta with id '" + arguments.carpetaID + "' not found.";
}
else {
	//audit consultar carpeta
	var type = carpeta.getTypeShort();
	var site = carpeta.getSiteShortName();
	if(type == "udl:expedient") {
	    audit("url_audit_view_expedient", arguments.carpetaID, type, site, arguments.username);	
	}
	else if(type == "udl:agregacio") {
	    audit("url_audit_view_agregacio", arguments.carpetaID, type, site, arguments.username);	
	}
	else if(type == "udl:serie") {
	    audit("url_audit_view_serie", arguments.carpetaID, type, site, arguments.username);	
	}
	else if(type == "udl:fons") {
	    audit("url_audit_view_fons", arguments.carpetaID, type, site, arguments.username);	
	}
	else if(type == "udl:grupDeFons") {		
	    audit("url_audit_view_grup_de_fons", arguments.carpetaID, type, site, arguments.username);
	}
	success = true;
}


model.mapMetadatos = getMetadatos(carpeta);
model.carpeta = carpeta;
model.success = String(success);