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
	document = document.checkout();
	model.document = document;
	success = true;
	//audit consultar contenido
	//audit("url_audit_view_documentSimple",arguments.docID, document.getTypeShort(), document.getSiteShortName(),arguments.username);
	
	/*
	status.code = 307;
	status.location = "http://localhost:8080/alfresco" + document.downloadUrl;	
	*/
}

model.mapMetadatos = getMetadatos(document);
model.success = String(success);