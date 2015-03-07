<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/metadataHandler.js">

var success = false;
var carpeta = null;
var type = null;

var id = String(arguments.metadades.secuencial_identificador_expedient);
//var query = "+PATH:\"app:company_home/st:sites/cm:rm/cm:documentLibrary/cm:Fons_x0020_Universitat_x0020_de_x0020_Lleida/*\" +@udlrm\\:secuencial_identificador_expedient:\"" + id + "\"";
var query = "+PATH:\"app:company_home/st:sites/cm:rm/cm:documentLibrary/cm:Fons_x0020_Universitat_x0020_de_x0020_Lleida/cm:Serie_x0020_1/*\"";

var expedient = search.luceneSearch(query);

if (expedient == undefined || expedient.length == 0) {
	model.error = "Folder '" + arguments.carpetaPath + "' not found.";
	status.code = 500;
	status.message = "Folder '" + arguments.carpetaPath + "' not found.";
	status.redirect = true;	

} else {
	try {
		expedient[3].properties["udlrm:localitzacio_1_expedient"] = String(arguments.metadades.localitzacio_1_expedient);
		expedient[3].properties["udlrm:localitzacio_2_expedient"] = String(arguments.metadades.localitzacio_2_expedient);
		
		expedient[3].save();
		
	}catch(e){
		status.code = 500;
		status.message = handleError(e, "updateExpedientRM.post.js");
		status.redirect = true;	
	}
}

model.expedient = expedient[3];
model.success = String(success);