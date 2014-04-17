<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/metadataHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

var success = false;
var carpeta = search.findNode("workspace://SpacesStore/" + arguments.carpetaID);

if (carpeta == undefined || !carpeta.isContainer) {
	model.error = "Carpeta with id '" + arguments.carpetaID + "' not found.";

} else {	
	var desti = roothome.childByNamePath(arguments.carpetaPath);
	if (desti == undefined || !desti.isContainer) {
		model.error = "Folder at '" + arguments.carpetaPath + "' not found.";
	
	} else {
		try {
			carpeta.move(desti);
			model.carpeta = carpeta;
			success = true;
			var type = carpeta.getTypeShort();
			var site = carpeta.getSiteShortName();
			var nodeRef = carpeta.getNodeRef();
			
			//Audit moure carpeta
			if(type == "udl:expedient") {
			    audit("url_audit_move_expedient", nodeRef, type, site, arguments.username);		
			}
			else if(type == "udl:agregacio") {
			    audit("url_audit_move_agregacio", nodeRef, type, site, arguments.username);		
			}
			else if(type == "udl:serie") {
			    audit("url_audit_move_serie", nodeRef, type, site, arguments.username);		
			}
			else if(type == "udl:fons") {
			    audit("url_audit_move_fons", nodeRef, type, site, arguments.username);		
			}
			else if(type == "udl:grupDeFons") {		
			    audit("url_audit_move_grup_de_fons", nodeRef, type, site, arguments.username);
			}

		} catch(e){
			model.error = handleError(e);
		}	
	}
}

model.mapMetadatos = getMetadatos(carpeta);
model.success = String(success);