<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">

var success = false;
var codiClass = String(arguments.codiClass);
var path = "Espacio de empresa/Sitios/rm/documentLibrary/Fons Migració";
var query = "+PATH:\"app:company_home/st:sites/cm:rm/cm:documentLibrary/cm:Fons_x0020_Migració/*\" +@udlrm\\:codi_classificacio_serie:\"" + codiClass + "\"";

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