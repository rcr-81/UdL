<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">

var success = false;
var id = String(arguments.id);
var query = "@udlrm\\:secuencial_identificador_expedient:\"" + "*" + id + "*" + "\"";

try {
	var nodes = search.luceneSearch(query);
	success = true;

} catch(e){
	model.error = handleError(e);
}

model.success = String(success);
if(nodes != undefined && nodes.length > 0){
	model.nodes = nodes;
}