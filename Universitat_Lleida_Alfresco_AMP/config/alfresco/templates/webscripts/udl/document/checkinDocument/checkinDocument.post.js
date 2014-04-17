<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/metadataHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

var success = false;
var document = search.findNode("workspace://SpacesStore/" + arguments.docID);

if (document == undefined || document.isContainer) {		
	model.error = "Document with id '" + arguments.docID + "' not found.";

} else {
	
	var isWorkingCopy = false;
	
	for each (aspect in document.aspects){
		if(aspect == '{http://www.alfresco.org/model/content/1.0}workingcopy') {
			isWorkingCopy = true;
		}
	}

	if(isWorkingCopy) {
		document = document.checkin();
		model.document = document;
		success = true;
		//audit consultar contenido
		//audit("url_audit_view_documentSimple",arguments.docID, document.getTypeShort(), document.getSiteShortName(),arguments.username);
	} else {
		model.error = "Document with id '" + arguments.docID + "' is not a working copy.";
	}
}

model.mapMetadatos = getMetadatos(document);
model.success = String(success);
