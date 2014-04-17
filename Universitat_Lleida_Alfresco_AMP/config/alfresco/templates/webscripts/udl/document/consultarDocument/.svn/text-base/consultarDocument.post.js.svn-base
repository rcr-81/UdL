<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/metadataHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

var success = false;
var document = search.findNode("workspace://SpacesStore/" + arguments.docID);
if (document == undefined || document.isContainer) {		
	model.error = "Document with id '" + arguments.docID + "' not found.";
}
else {
	model.document = document;
	success = true;	
	
	//audit consultar contenido
	var type = document.getTypeShort();
	var site = document.getSiteShortName();
	
	if(type == "udl:documentSimple") {
	    audit("url_audit_view_document_simple", arguments.docID, type, site, arguments.username);		
	}
}

model.mapMetadatos = getMetadatos(document);
model.success = String(success);