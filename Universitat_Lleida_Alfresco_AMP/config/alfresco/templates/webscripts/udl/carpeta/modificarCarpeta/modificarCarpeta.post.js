<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/metadataHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

var success = false;

var carpeta = search.findNode("workspace://SpacesStore/" + arguments.carpetaID);
if (carpeta == undefined || !carpeta.isContainer) {
	model.error = "Expedient with id '" + arguments.carpetaID + "' not found.";
}
else {
	var estatExpedient = carpeta.properties['udl:estat'];
	if(estatExpedient == 'tancat' || estatExpedient == 'arxivat'){
		model.error = "No es pot modificar un carpeta " + estatExpedient + ".";			
	}
	else {
		try {
			carpeta.properties.name = String(arguments.carpetaName);
			carpeta.properties["udl:estat"] = String(arguments.metadades.estat);	
			carpeta.properties["udl:num_carpeta"] = String(Math.floor(Math.random()* 99999999));  
			carpeta.properties["udl:codi_classificacio"] = String(Math.floor(Math.random()* 99999999));		
			carpeta.properties["udl:desti_transferencia"] = String(arguments.metadades.destiTransferencia);			
			setMetadatos(carpeta, arguments);
			carpeta.save();
			model.carpeta = carpeta;
			success = true;

			var type = carpeta.getTypeShort();
			var site = carpeta.getSiteShortName();
			var nodeRef = carpeta.getNodeRef();
			
			//Audit modificar carpeta
			if(type == "udl:expedient") {
			    audit("url_audit_update_expedient", nodeRef, type, site, arguments.username);		
			}
			else if(type == "udl:agregacio") {
			    audit("url_audit_update_agregacio", nodeRef, type, site, arguments.username);		
			}
			else if(type == "udl:serie") {
			    audit("url_audit_update_serie", nodeRef, type, site, arguments.username);		
			}
			else if(type == "udl:fons") {
			    audit("url_audit_update_fons", nodeRef, type, site, arguments.username);
			}
			else if(type == "udl:grupDeFons") {		
			    audit("url_audit_update_grup_de_fons", nodeRef, type, site, arguments.username);
			}			
		}
		catch(e){
			model.error = handleError(e);
		}
	}
}

model.mapMetadatos = getMetadatos(carpeta);
model.success = String(success);