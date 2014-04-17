<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">

var docID = null;
var docContent = null;
var docName = null;
var estat = null;
var desti_transferencia = null;
var document = null;

for each (field in formdata.fields){
	if (field.name == "docID"){
		docID = field.value;

	}else if (field.name == "docContent"){
		docContent = field.value;

	}else if (field.name == "docName"){
		docName = field.value;

	}else if (field.name == "estat"){
		estat = field.value;

	}else if (field.name == "tipus_entitat_documentSimple"){	
		  var tipus_entitat_documentSimple = field.value;
	
	}else if (field.name == "categoria_documentSimple"){  	
		  var categoria_documentSimple = field.value;
	
	}else if (field.name == "secuencial_identificador_documentSimple"){
		  var secuencial_identificador_documentSimple = field.value;
	
	}else if (field.name == "esquema_identificador_documentSimple"){
		  var esquema_identificador_documentSimple = field.value;
	
	}else if (field.name == "data_inici_documentSimple"){
		  var data_inici_documentSimple = field.value;
	
	}else if (field.name == "data_fi_documentSimple"){
		  var data_fi_documentSimple = field.value;
	
	}else if (field.name == "classificacio_acces_documentSimple"){
		  var classificacio_acces_documentSimple = field.value;
	
	}else if (field.name == "sensibilitat_dades_caracter_personal_documentSimple"){
		  var sensibilitat_dades_caracter_personal_documentSimple = field.value;

	}else if (field.name == "valoracio_documentSimple"){
		  var valoracio_documentSimple = field.value;
	
	}else if (field.name == "suport_origen_documentSimple"){
		  var suport_origen_documentSimple = field.value;
	
	}else if (field.name == "versio_format_documentSimple"){
		  var versio_format_documentSimple = field.value;
	
	}else if (field.name == "nom_aplicacio_creacio_documentSimple"){
		  var nom_aplicacio_creacio_documentSimple = field.value;
	
	}else if (field.name == "versio_aplicacio_creacio_documentSimple"){
		  var versio_aplicacio_creacio_documentSimple = field.value;
	
	}else if (field.name == "registre_formats_documentSimple"){
		  var registre_formats_documentSimple = field.value;
	
	}else if (field.name == "resolucio_documentSimple"){
		  var resolucio_documentSimple = field.value;
	
	}else if (field.name == "dimensions_fisiques_documentSimple"){
		  var dimensions_fisiques_documentSimple = field.value;
	
	}else if (field.name == "unitats_documentSimple"){
		  var unitats_documentSimple = field.value;
	
	}else if (field.name == "tipus_documental_documentSimple"){
		  var tipus_documental_documentSimple = field.value;
	
	}else if (field.name == "denominacio_estat_documentSimple"){
		  var denominacio_estat_documentSimple = field.value;
	
	}else if (field.name == "tipus_copia_documentSimple"){
		  var tipus_copia_documentSimple = field.value;
	
	}else if (field.name == "motiu_documentSimple"){
		  var motiu_documentSimple = field.value;
	
	}else if(field.name == "data_registre_entrada_documentSimple") {
		var data_registre_entrada_documentSimple = field.value;

	}else if(field.name == "data_registre_sortida_documentSimple") {
		var data_registre_sortida_documentSimple = field.value;

	}else if(field.name == "advertencia_seguretat_documentSimple") {
		var advertencia_seguretat_documentSimple = field.value;

	}else if(field.name == "categoria_advertencia_seguretat_documentSimple") {
		var categoria_advertencia_seguretat_documentSimple = field.value;

	}else if(field.name == "verificacio_integritat_algorisme_documentSimple") {
		var verificacio_integritat_algorisme_documentSimple = field.value;

	}else if(field.name == "verificacio_integritat_valor_documentSimple") {
		var verificacio_integritat_valor_documentSimple = field.value;

	}else if(field.name == "idioma_1_documentSimple") {
		var idioma_1_documentSimple = field.value;

	}else if(field.name == "idioma_2_documentSimple") {
		var idioma_2_documentSimple  = field.value;

	}else if(field.name == "tipus_dictamen_1_documentSimple") {
		var tipus_dictamen_1_documentSimple = field.value;

	}else if(field.name == "tipus_dictamen_2_documentSimple") {
		var tipus_dictamen_2_documentSimple = field.value;

	}else if(field.name == "accio_dictaminada_1_documentSimple") {
		var accio_dictaminada_1_documentSimple = field.value;

	}else if(field.name == "accio_dictaminada_2_documentSimple") {
		var accio_dictaminada_2_documentSimple = field.value;

	}else if(field.name == "suport_1_documentSimple") {
		var suport_1_documentSimple = field.value;

	}else if(field.name == "localitzacio_1_documentSimple") {
		var localitzacio_1_documentSimple = field.value;

	}else if(field.name == "localitzacio_2_documentSimple") {
		var localitzacio_2_documentSimple = field.value;

	}else if(field.name == "codi_classificacio_1_documentSimple") {
		var codi_classificacio_1_documentSimple = field.value;

	}else if(field.name == "codi_classificacio_2_documentSimple") {
		var codi_classificacio_2_documentSimple = field.value;

	}else if(field.name == "denominacio_classe_1_documentSimple") {
		var denominacio_classe_1_documentSimple = field.value;

	}else if(field.name == "denominacio_classe_2_documentSimple") {
		var denominacio_classe_2_documentSimple = field.value;

	}else if (field.name == "username"){
		  var username = field.value;
	}
}

var document = search.findNode("workspace://SpacesStore/" + docID);
if (document == undefined || !document.isDocument) {
	status.code = 500;
	status.message = "Document with id '" + docID + "' not found.";
	status.redirect = true;	
}
else {
	document.content = docContent;
	document.properties.name = String(docName);

	if(document.getTypeShort() == 'udl:documentSimple') {
		document.properties["udl:tipus_documental_documentSimple"] = tipus_documental_documentSimple;
		document.properties["udl:tipus_entitat_documentSimple"] = tipus_entitat_documentSimple;
		document.properties["udl:categoria_documentSimple"] = categoria_documentSimple;
		document.properties["udl:secuencial_identificador_documentSimple"] = secuencial_identificador_documentSimple;					
		document.properties["udl:esquema_identificador_documentSimple"] = esquema_identificador_documentSimple; 
		document.properties["udl:data_inici_documentSimple"] = dateHelper.parse(data_inici_documentSimple); 
		document.properties["udl:data_fi_documentSimple"] = dateHelper.parse(data_fi_documentSimple);
		document.properties["udl:data_registre_entrada_documentSimple"] = dateHelper.parse(data_registre_entrada_documentSimple);
		document.properties["udl:data_registre_sortida_documentSimple"] = dateHelper.parse(data_registre_sortida_documentSimple);						
		document.properties["udl:classificacio_acces_documentSimple"] = classificacio_acces_documentSimple;
		document.properties["udl:advertencia_seguretat_documentSimple"] = advertencia_seguretat_documentSimple;
		document.properties["udl:categoria_advertencia_seguretat_documentSimple"] = categoria_advertencia_seguretat_documentSimple;
		document.properties["udl:sensibilitat_dades_caracter_personal_documentSimple"] = sensibilitat_dades_caracter_personal_documentSimple;
		document.properties["udl:verificacio_integritat_algorisme_documentSimple"] = verificacio_integritat_algorisme_documentSimple;
		document.properties["udl:verificacio_integritat_valor_documentSimple"] = verificacio_integritat_valor_documentSimple;
		document.properties["udl:idioma_1_documentSimple"] = idioma_1_documentSimple;
		document.properties["udl:idioma_2_documentSimple"] = idioma_2_documentSimple;
		document.properties["udl:valoracio_documentSimple"] = valoracio_documentSimple;
		document.properties["udl:tipus_dictamen_1_documentSimple"] = tipus_dictamen_1_documentSimple;
		document.properties["udl:tipus_dictamen_2_documentSimple"] = tipus_dictamen_2_documentSimple;
		document.properties["udl:accio_dictaminada_1_documentSimple"] = accio_dictaminada_1_documentSimple;
		document.properties["udl:accio_dictaminada_2_documentSimple"] = accio_dictaminada_2_documentSimple;
		document.properties["udl:suport_origen_documentSimple"] = suport_origen_documentSimple;
		document.properties["udl:versio_format_documentSimple"] = versio_format_documentSimple;
		document.properties["udl:nom_aplicacio_creacio_documentSimple"] = nom_aplicacio_creacio_documentSimple;
		document.properties["udl:versio_aplicacio_creacio_documentSimple"] = versio_aplicacio_creacio_documentSimple;
		document.properties["udl:registre_formats_documentSimple"] = registre_formats_documentSimple;
		document.properties["udl:resolucio_documentSimple"] = resolucio_documentSimple;
		document.properties["udl:dimensions_fisiques_documentSimple"] = dimensions_fisiques_documentSimple;
		document.properties["udl:unitats_documentSimple"] = unitats_documentSimple;
		document.properties["udl:suport_1_documentSimple"] = suport_1_documentSimple;
		document.properties["udl:localitzacio_1_documentSimple"] = localitzacio_1_documentSimple;
		document.properties["udl:localitzacio_2_documentSimple"] = localitzacio_2_documentSimple;
		document.properties["udl:denominacio_estat_documentSimple"] = denominacio_estat_documentSimple;
		document.properties["udl:tipus_copia_documentSimple"] = tipus_copia_documentSimple;
		document.properties["udl:motiu_documentSimple"] = motiu_documentSimple;
		document.properties["udl:codi_classificacio_1_documentSimple"] = codi_classificacio_1_documentSimple;
		document.properties["udl:codi_classificacio_2_documentSimple"] = codi_classificacio_2_documentSimple;
		document.properties["udl:denominacio_classe_1_documentSimple"] = denominacio_classe_1_documentSimple;						
		document.properties["udl:denominacio_classe_2_documentSimple"] = denominacio_classe_2_documentSimple;						
	}
	
	document.save();
	model.document = document;
}