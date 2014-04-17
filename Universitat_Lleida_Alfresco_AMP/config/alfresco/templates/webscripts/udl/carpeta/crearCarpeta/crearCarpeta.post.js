<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/metadataHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

var success = false;
var carpeta = null;
var type = null;

var parentFolder = roothome.childByNamePath(arguments.carpetaPath);
if (parentFolder == undefined || !parentFolder.isContainer) {
	model.error = "Folder '" + arguments.carpetaPath + "' not found.";
	status.code = 500;
	status.message = "Folder '" + arguments.carpetaPath + "' not found.";
	status.redirect = true;	
}
else {
	try {
		type = arguments.carpetaType;
		carpeta = parentFolder.createNode(arguments.carpetaName, type);			
		//carpeta.setOwner(arguments.username);
		setMetadatos(carpeta, arguments);
		carpeta.save();
		success = true;
		
		//audit crear carpeta
		var type = carpeta.getTypeShort();
		var site = carpeta.getSiteShortName();
		var nodeRef = carpeta.getNodeRef();
		
		if(type == "udl:expedient") {
		    audit("url_audit_create_expedient", nodeRef, type, site, arguments.username);	
		}
		else if(type == "udl:agregacio") {
		    audit("url_audit_create_agregacio", nodeRef, type, site, arguments.username);	
		}
		else if(type == "udl:serie") {
		    audit("url_audit_create_serie", nodeRef, type, site, arguments.username);	
		}
		else if(type == "udl:fons") {
		    audit("url_audit_create_fons", nodeRef, type, site, arguments.username);	
		}
		else if(type == "udl:grupDeFons") {		
		    audit("url_audit_create_grup_de_fons", nodeRef, type, site, arguments.username);
		}
	}
	catch(e){
		model.error = handleError(e, "crearCarpeta.post.js");
		//status.code = 500;
		//status.message = handleError(e, "crearCarpeta.post.js");
		status.redirect = true;	
	}
}

model.mapMetadatos = getMetadatos(carpeta);
model.carpeta = carpeta;
model.success = String(success);