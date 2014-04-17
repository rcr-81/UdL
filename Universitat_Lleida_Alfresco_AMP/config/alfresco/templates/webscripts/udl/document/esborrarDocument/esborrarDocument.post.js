<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

var success = false;
var document = search.findNode("workspace://SpacesStore/" + arguments.docID);

if (document == undefined || document.isContainer) {		
	model.error = "Document with id '" + arguments.docID + "' not found.";
}
else {
	try {
		var estatExpedient = document.parent.properties['cm:estat'];
		if(estatExpedient == 'tancat' || estatExpedient == 'arxivat'){
			model.error = "No es pot esborrar documents d'un expedient " + estatExpedient + ".";			
		}
		else {
			var doc = new Array();
			doc.id = document.id;
			doc.nodeRef = document.nodeRef;
			doc.displayPath = document.displayPath;
			doc.name = document.properties.name;
			doc.type = document.typeShort;
			doc.site = document.siteShortName;
			model.doc = doc;
			success = document.remove();
			
			//audit para capturar el documento borrado
	      	if(doc.type == "udl:documentSimple") {
			    audit("url_audit_delete_document_simple", doc.nodeRef, doc.type, doc.site, arguments.username);
			}
			
			//esborrar signatures
			var pathFolderSignatures = doc.displayPath + "/signatures-" + doc.name; 
			var folderSignatures = roothome.childByNamePath(pathFolderSignatures);
			if (folderSignatures != undefined && folderSignatures.isContainer) {		
				success = folderSignatures.remove();
			}
		}
	}
	catch(e){
		model.error = handleError(e);
	}
}

model.success = String(success);