<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

var success = false;

function removeDoc(docID){
	var success = false;	
	var doc = new Array();	
	var document = search.findNode("workspace://SpacesStore/" + docID);
	if (document == undefined || document.isContainer) {		
		doc.error = "Document with id '" + docID + "' not found.";
	}
	else {
		try {
			//fase activa: mirar l'estat expedient.properties["cm:estat"] = "obert";
			//si està tancat o arxivat no es pot esborrar res
			//fase semi-activa ?
			var estat_expedient = document.parent.properties['cm:estat'];
			if(estat_expedient == 'tancat' || estat_expedient == 'arxivat'){
				model.error = "No es pot esborrar documents d'un expedient " + estat_expedient + "."
			}
			else {
				doc.id = document.id;
				doc.nodeRef = document.nodeRef;
				doc.displayPath = document.displayPath;
				doc.name = document.properties.name;
				doc.type = document.typeShort;
				doc.site = document.siteShortName;
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
			doc.error = handleError(e);
		}		
	}	
	
	doc.success = String(success);
	return doc;
}

var removeInfo = new Array();

for each(d in arguments.documents.document){	
	var info = removeDoc(d.docID);
	removeInfo.push(info);
}

model.removeInfo = removeInfo;