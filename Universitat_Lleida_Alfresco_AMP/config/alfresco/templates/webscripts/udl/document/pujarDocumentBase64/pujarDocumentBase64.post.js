<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">

var docPath = null;
var docContent = null;
var docName = null;
var docType = null;
var document = null;
var estat = "";
var destiTransferencia = "";

var tipus_entitat_documentSimple = null;
var categoria_documentSimple = null;
var secuencial_identificador_documentSimple = null;
var esquema_identificador_documentSimple = null;
var data_inici_documentSimple = null;
var data_fi_documentSimple = null;
var classificacio_acces_documentSimple = null;
var sensibilitat_dades_caracter_personal_documentSimple = null;
var valoracio_documentSimple = null;
var suport_origen_documentSimple = null;
var versio_format_documentSimple = null;
var nom_aplicacio_creacio_documentSimple = null;
var versio_aplicacio_creacio_documentSimple = null;
var registre_formats_documentSimple = null;
var resolucio_documentSimple = null;
var dimensions_fisiques_documentSimple = null;
var unitats_documentSimple = null;
var tipus_documental_documentSimple = null;
var denominacio_estat_documentSimple = null;
var tipus_copia_documentSimple = null;
var motiu_documentSimple = null;
var data_registre_entrada_documentSimple = null;
var data_registre_sortida_documentSimple = null;
var advertencia_seguretat_documentSimple = null;
var categoria_advertencia_seguretat_documentSimple = null;
var verificacio_integritat_algorisme_documentSimple = null;
var verificacio_integritat_valor_documentSimple = null;
var idioma_1_documentSimple = null;
var idioma_2_documentSimple  = null;
var tipus_dictamen_1_documentSimple = null;
var tipus_dictamen_2_documentSimple = null;
var accio_dictaminada_1_documentSimple = null;
var accio_dictaminada_2_documentSimple = null;
var suport_1_documentSimple = null;
var localitzacio_1_documentSimple = null;
var localitzacio_2_documentSimple = null;
var codi_classificacio_1_documentSimple = null;
var codi_classificacio_2_documentSimple = null;
var denominacio_classe_1_documentSimple = null;
var denominacio_classe_2_documentSimple = null;
var username = null;
var advertencia_seguretat_documentSimple = null;
var signatura1_nom = null;
var signatura1_carrec = null;
var signatura1_dni = null;
var signatura1_data = null;
var signatura1_data_fi_certificat = null;
var signatura1_data_caducitat = null;
var signatura2_nom = null;
var signatura2_carrec = null;
var signatura2_dni = null;
var signatura2_data = null;
var signatura2_data_fi_certificat = null;
var signatura2_data_caducitat = null;
var signatura3_nom = null;
var signatura3_carrec = null;
var signatura3_dni = null;
var signatura3_data = null;
var signatura3_data_fi_certificat = null;
var signatura3_data_caducitat = null;
var signatura4_nom = null;
var signatura4_carrec = null;
var signatura4_dni = null;
var signatura4_data = null;
var signatura4_data_fi_certificat = null;
var signatura4_data_caducitat = null;
var signatura5_nom = null;
var signatura5_carrec = null;
var signatura5_dni = null;
var signatura5_data = null;
var signatura5_data_fi_certificat = null;
var signatura5_data_caducitat = null;
var signatura6_nom = null;
var signatura6_carrec = null;
var signatura6_dni = null;
var signatura6_data = null;
var signatura6_data_fi_certificat = null;
var signatura6_data_caducitat = null;
var signatura7_nom = null;
var signatura7_carrec = null;
var signatura7_dni = null;
var signatura7_data = null;
var signatura7_data_fi_certificat = null;
var signatura7_data_caducitat = null;
var signatura8_nom = null;
var signatura8_carrec = null;
var signatura8_dni = null;
var signatura8_data = null;
var signatura8_data_fi_certificat = null;
var signatura8_data_caducitat = null;
var signatura9_nom = null;
var signatura9_carrec = null;
var signatura9_dni = null;
var signatura9_data = null;
var signatura9_data_fi_certificat = null;
var signatura9_data_caducitat = null;
var signatura10_nom = null;
var signatura10_carrec = null;
var signatura10_dni = null;
var signatura10_data = null;
var signatura10_data_fi_certificat = null;
var signatura10_data_caducitat = null;
var signatura11_nom = null;
var signatura11_carrec = null;
var signatura11_dni = null;
var signatura11_data = null;
var signatura11_data_fi_certificat = null;
var signatura11_data_caducitat = null;
var signatura12_nom = null;
var signatura12_carrec = null;
var signatura12_dni = null;
var signatura12_data = null;
var signatura12_data_fi_certificat = null;
var signatura12_data_caducitat = null;
var grup_creador_documentSimple = null;

for each (field in formdata.fields){
    if (field.name == "docPath"){
        docPath = field.value;
  
    }else if (field.name == "docContent"){
	    docContent = field.value;

    }else if (field.name == "docName"){
	    docName = field.value;

    }else if (field.name == "docType"){
	    docType = field.value;
  
    }else if (field.name == "estat"){
	    estat = field.value;
  
	}else if (field.name == "tipus_entitat_documentSimple"){	
		  tipus_entitat_documentSimple = field.value;
	
	}else if (field.name == "categoria_documentSimple"){  	
		  categoria_documentSimple = field.value;
	
	}else if (field.name == "secuencial_identificador_documentSimple"){
		  secuencial_identificador_documentSimple = field.value;
	
	}else if (field.name == "esquema_identificador_documentSimple"){
		  esquema_identificador_documentSimple = field.value;
	
	}else if (field.name == "data_inici_documentSimple"){
		  data_inici_documentSimple = field.value;
	
	}else if (field.name == "data_fi_documentSimple"){
		  data_fi_documentSimple = field.value;
	
	}else if (field.name == "classificacio_acces_documentSimple"){
		  classificacio_acces_documentSimple = field.value;
	
	}else if (field.name == "sensibilitat_dades_caracter_personal_documentSimple"){
		  sensibilitat_dades_caracter_personal_documentSimple = field.value;

	}else if (field.name == "valoracio_documentSimple"){
		  valoracio_documentSimple = field.value;
	
	}else if (field.name == "suport_origen_documentSimple"){
		  suport_origen_documentSimple = field.value;
	
	}else if (field.name == "versio_format_documentSimple"){
		  versio_format_documentSimple = field.value;
	
	}else if (field.name == "nom_aplicacio_creacio_documentSimple"){
		  nom_aplicacio_creacio_documentSimple = field.value;
	
	}else if (field.name == "versio_aplicacio_creacio_documentSimple"){
		  versio_aplicacio_creacio_documentSimple = field.value;
	
	}else if (field.name == "registre_formats_documentSimple"){
		registre_formats_documentSimple = field.value;
	
	}else if (field.name == "resolucio_documentSimple"){
		resolucio_documentSimple = field.value;
	
	}else if (field.name == "dimensions_fisiques_documentSimple"){
		dimensions_fisiques_documentSimple = field.value;
	
	}else if (field.name == "unitats_documentSimple"){
		unitats_documentSimple = field.value;
	
	}else if (field.name == "tipus_documental_documentSimple"){
		tipus_documental_documentSimple = field.value;
	
	}else if (field.name == "denominacio_estat_documentSimple"){
		denominacio_estat_documentSimple = field.value;
	
	}else if (field.name == "tipus_copia_documentSimple"){
		tipus_copia_documentSimple = field.value;
	
	}else if (field.name == "motiu_documentSimple"){
		motiu_documentSimple = field.value;
	
	}else if(field.name == "data_registre_entrada_documentSimple") {
		data_registre_entrada_documentSimple = field.value;

	}else if(field.name == "data_registre_sortida_documentSimple") {
		data_registre_sortida_documentSimple = field.value;

	}else if(field.name == "advertencia_seguretat_documentSimple") {
		advertencia_seguretat_documentSimple = field.value;

	}else if(field.name == "categoria_advertencia_seguretat_documentSimple") {
		categoria_advertencia_seguretat_documentSimple = field.value;

	}else if(field.name == "verificacio_integritat_algorisme_documentSimple") {
		verificacio_integritat_algorisme_documentSimple = field.value;

	}else if(field.name == "verificacio_integritat_valor_documentSimple") {
		verificacio_integritat_valor_documentSimple = field.value;

	}else if(field.name == "idioma_1_documentSimple") {
		idioma_1_documentSimple = field.value;

	}else if(field.name == "idioma_2_documentSimple") {
		idioma_2_documentSimple  = field.value;

	}else if(field.name == "tipus_dictamen_1_documentSimple") {
		tipus_dictamen_1_documentSimple = field.value;

	}else if(field.name == "tipus_dictamen_2_documentSimple") {
		tipus_dictamen_2_documentSimple = field.value;

	}else if(field.name == "accio_dictaminada_1_documentSimple") {
		accio_dictaminada_1_documentSimple = field.value;

	}else if(field.name == "accio_dictaminada_2_documentSimple") {
		accio_dictaminada_2_documentSimple = field.value;

	}else if(field.name == "suport_1_documentSimple") {
		suport_1_documentSimple = field.value;

	}else if(field.name == "localitzacio_1_documentSimple") {
		localitzacio_1_documentSimple = field.value;

	}else if(field.name == "localitzacio_2_documentSimple") {
		localitzacio_2_documentSimple = field.value;

	}else if(field.name == "codi_classificacio_1_documentSimple") {
		codi_classificacio_1_documentSimple = field.value;

	}else if(field.name == "codi_classificacio_2_documentSimple") {
		codi_classificacio_2_documentSimple = field.value;

	}else if(field.name == "denominacio_classe_1_documentSimple") {
		denominacio_classe_1_documentSimple = field.value;

	}else if(field.name == "denominacio_classe_2_documentSimple") {
		denominacio_classe_2_documentSimple = field.value;

	}else if (field.name == "username"){
		username = field.value;

	}else if (field.name == "advertencia_seguretat_documentSimple"){
		advertencia_seguretat_documentSimple = field.value;

	}else if (field.name == "signatura1_nom"){
		signatura1_nom = field.value;
	
	}else if (field.name == "signatura1_carrec"){
		signatura1_carrec = field.value;
	
	}else if (field.name == "signatura1_dni"){
		signatura1_dni = field.value;
	
	}else if (field.name == "signatura1_data"){
		signatura1_data = field.value;
	
	}else if (field.name == "signatura1_data_fi_certificat"){
		signatura1_data_fi_certificat = field.value;

	}else if (field.name == "signatura1_data_caducitat"){
		signatura1_data_caducitat = field.value;

	}else if (field.name == "signatura2_nom"){
		signatura2_nom = field.value;
	
	}else if (field.name == "signatura2_carrec"){
		signatura2_carrec = field.value;
	
	}else if (field.name == "signatura2_dni"){
		signatura2_dni = field.value;
	
	}else if (field.name == "signatura2_data"){
		signatura2_data = field.value;
	
	}else if (field.name == "signatura2_data_fi_certificat"){
		signatura2_data_fi_certificat = field.value;

	}else if (field.name == "signatura2_data_caducitat"){
		signatura2_data_caducitat = field.value;

	}else if (field.name == "signatura3_nom"){
		signatura3_nom = field.value;
	
	}else if (field.name == "signatura3_carrec"){
		signatura3_carrec = field.value;
	
	}else if (field.name == "signatura3_dni"){
		signatura3_dni = field.value;
	
	}else if (field.name == "signatura3_data"){
		signatura3_data = field.value;
	
	}else if (field.name == "signatura3_data_fi_certificat"){
		signatura3_data_fi_certificat = field.value;

	}else if (field.name == "signatura3_data_caducitat"){
		signatura3_data_caducitat = field.value;

	}else if (field.name == "signatura4_nom"){
		signatura4_nom = field.value;
	
	}else if (field.name == "signatura4_carrec"){
		signatura4_carrec = field.value;
	
	}else if (field.name == "signatura4_dni"){
		signatura4_dni = field.value;
	
	}else if (field.name == "signatura4_data"){
		signatura4_data = field.value;
	
	}else if (field.name == "signatura4_data_fi_certificat"){
		signatura4_data_fi_certificat = field.value;

	}else if (field.name == "signatura4_data_caducitat"){
		signatura4_data_caducitat = field.value;

	}else if (field.name == "signatura5_nom"){
		signatura5_nom = field.value;
	
	}else if (field.name == "signatura5_carrec"){
		signatura5_carrec = field.value;
	
	}else if (field.name == "signatura5_dni"){
		signatura5_dni = field.value;
	
	}else if (field.name == "signatura5_data"){
		signatura5_data = field.value;
	
	}else if (field.name == "signatura5_data_fi_certificat"){
		signatura5_data_fi_certificat = field.value;

	}else if (field.name == "signatura5_data_caducitat"){
		signatura5_data_caducitat = field.value;

	}else if (field.name == "signatura6_nom"){
		signatura6_nom = field.value;
	
	}else if (field.name == "signatura6_carrec"){
		signatura6_carrec = field.value;
	
	}else if (field.name == "signatura6_dni"){
		signatura6_dni = field.value;
	
	}else if (field.name == "signatura6_data"){
		signatura6_data = field.value;
	
	}else if (field.name == "signatura6_data_fi_certificat"){
		signatura6_data_fi_certificat = field.value;

	}else if (field.name == "signatura6_data_caducitat"){
		signatura6_data_caducitat = field.value;

	}else if (field.name == "signatura7_nom"){
		signatura7_nom = field.value;
	
	}else if (field.name == "signatura7_carrec"){
		signatura7_carrec = field.value;
	
	}else if (field.name == "signatura7_dni"){
		signatura7_dni = field.value;
	
	}else if (field.name == "signatura7_data"){
		signatura7_data = field.value;
	
	}else if (field.name == "signatura7_data_fi_certificat"){
		signatura7_data_fi_certificat = field.value;

	}else if (field.name == "signatura7_data_caducitat"){
		signatura7_data_caducitat = field.value;

	}else if (field.name == "signatura8_nom"){
		signatura8_nom = field.value;
	
	}else if (field.name == "signatura8_carrec"){
		signatura8_carrec = field.value;
	
	}else if (field.name == "signatura8_dni"){
		signatura8_dni = field.value;
	
	}else if (field.name == "signatura8_data"){
		signatura8_data = field.value;
	
	}else if (field.name == "signatura8_data_fi_certificat"){
		signatura8_data_fi_certificat = field.value;

	}else if (field.name == "signatura8_data_caducitat"){
		signatura8_data_caducitat = field.value;

	}else if (field.name == "signatura9_nom"){
		signatura9_nom = field.value;
	
	}else if (field.name == "signatura9_carrec"){
		signatura9_carrec = field.value;
	
	}else if (field.name == "signatura9_dni"){
		signatura9_dni = field.value;
	
	}else if (field.name == "signatura9_data"){
		signatura9_data = field.value;
	
	}else if (field.name == "signatura9_data_fi_certificat"){
		signatura9_data_fi_certificat = field.value;

	}else if (field.name == "signatura9_data_caducitat"){
		signatura9_data_caducitat = field.value;

	}else if (field.name == "signatura10_nom"){
		signatura10_nom = field.value;
	
	}else if (field.name == "signatura10_carrec"){
		signatura10_carrec = field.value;
	
	}else if (field.name == "signatura10_dni"){
		signatura10_dni = field.value;
	
	}else if (field.name == "signatura10_data"){
		signatura10_data = field.value;
	
	}else if (field.name == "signatura10_data_fi_certificat"){
		signatura10_data_fi_certificat = field.value;

	}else if (field.name == "signatura10_data_caducitat"){
		signatura10_data_caducitat = field.value;

	}else if (field.name == "signatura11_nom"){
		signatura11_nom = field.value;
	
	}else if (field.name == "signatura11_carrec"){
		signatura11_carrec = field.value;
	
	}else if (field.name == "signatura11_dni"){
		signatura11_dni = field.value;
	
	}else if (field.name == "signatura11_data"){
		signatura11_data = field.value;
	
	}else if (field.name == "signatura11_data_fi_certificat"){
		signatura11_data_fi_certificat = field.value;

	}else if (field.name == "signatura11_data_caducitat"){
		signatura11_data_caducitat = field.value;

	}else if (field.name == "signatura12_nom"){
		signatura12_nom = field.value;
	
	}else if (field.name == "signatura12_carrec"){
		signatura12_carrec = field.value;
	
	}else if (field.name == "signatura12_dni"){
		signatura12_dni = field.value;
	
	}else if (field.name == "signatura12_data"){
		signatura12_data = field.value;
	
	}else if (field.name == "signatura12_data_fi_certificat"){
		signatura12_data_fi_certificat = field.value;

	}else if (field.name == "signatura12_data_caducitat"){
		signatura12_data_caducitat = field.value;

	}else if (field.name == "grup_creador_documentSimple"){
		grup_creador_documentSimple = field.value;
	}
}

var parentFolder = roothome.childByNamePath(docPath);

if (parentFolder == undefined || !parentFolder.isContainer) {
	status.code = 500;
	status.message = "Folder '" + docPath + "' not found.";
	status.redirect = true;	

} else {	
	try {
		document = parentFolder.createNode(docName, docType);
		document.content = docContent;
		//document.setOwner(username);
				
		if(docType == 'udl:documentSimple') {
			document.properties["udl:tipus_documental_documentSimple"] = tipus_documental_documentSimple;
			document.properties["udl:tipus_entitat_documentSimple"] = tipus_entitat_documentSimple;
			document.properties["udl:categoria_documentSimple"] = categoria_documentSimple;
			document.properties["udl:secuencial_identificador_documentSimple"] = secuencial_identificador_documentSimple;
			document.properties["udl:esquema_identificador_documentSimple"] = esquema_identificador_documentSimple;
			if(data_inici_documentSimple != null) {
				document.properties["udl:data_inici_documentSimple"] = dateHelper.parse(data_inici_documentSimple);				
			}
			if(data_fi_documentSimple != null) {
				document.properties["udl:data_fi_documentSimple"] = dateHelper.parse(data_fi_documentSimple);
			}
			if(data_registre_entrada_documentSimple != null) {
				document.properties["udl:data_registre_entrada_documentSimple"] = dateHelper.parse(data_registre_entrada_documentSimple);
			}
			if(data_registre_sortida_documentSimple != null) {
				document.properties["udl:data_registre_sortida_documentSimple"] = dateHelper.parse(data_registre_sortida_documentSimple);
			}
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
			document.properties["udl:accio_dictaminada_1_documentSimple"] = accio_dictaminada_1_documentSimple;
			document.properties["udl:tipus_dictamen_2_documentSimple"] = tipus_dictamen_2_documentSimple;
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
			document.properties["udl:denominacio_classe_1_documentSimple"] = denominacio_classe_1_documentSimple;
			document.properties["udl:codi_classificacio_2_documentSimple"] = codi_classificacio_2_documentSimple;
			document.properties["udl:denominacio_classe_2_documentSimple"] = denominacio_classe_2_documentSimple;

			if(signatura1_nom != null) {
				document.properties["udl:signatura1_nom"] = signatura1_nom;
			}
			if(signatura1_carrec != null) {
				document.properties["udl:signatura1_carrec"] = signatura1_carrec;
			}
			if(signatura1_dni != null) {
				document.properties["udl:signatura1_dni"] = signatura1_dni;
			}
			if(signatura1_data != null) {
				document.properties["udl:signatura1_data"] = dateHelper.parse(signatura1_data);
			}
			if(signatura1_data_fi_certificat != null) {
				document.properties["udl:signatura1_data_fi_certificat"] = dateHelper.parse(signatura1_data_fi_certificat);
			}
			if(signatura1_data_caducitat != null) {
				document.properties["udl:signatura1_data_caducitat"] = dateHelper.parse(signatura1_data_caducitat);
			}

			if(signatura2_nom != null) {
				document.properties["udl:signatura2_nom"] = signatura2_nom;
			}
			if(signatura2_carrec != null) {
				document.properties["udl:signatura2_carrec"] = signatura2_carrec;
			}
			if(signatura2_dni != null) {
				document.properties["udl:signatura2_dni"] = signatura2_dni;
			}
			if(signatura2_data != null) {
				document.properties["udl:signatura2_data"] = dateHelper.parse(signatura2_data);
			}
			if(signatura2_data_fi_certificat != null) {
				document.properties["udl:signatura2_data_fi_certificat"] = dateHelper.parse(signatura2_data_fi_certificat);
			}
			if(signatura2_data_caducitat != null) {
				document.properties["udl:signatura2_data_caducitat"] = dateHelper.parse(signatura2_data_caducitat);
			}
			
			if(signatura3_nom != null) {
				document.properties["udl:signatura3_nom"] = signatura3_nom;
			}
			if(signatura3_carrec != null) {
				document.properties["udl:signatura3_carrec"] = signatura3_carrec;
			}
			if(signatura3_dni != null) {
				document.properties["udl:signatura3_dni"] = signatura3_dni;
			}
			if(signatura3_data != null) {
				document.properties["udl:signatura3_data"] = dateHelper.parse(signatura3_data);
			}
			if(signatura3_data_fi_certificat != null) {
				document.properties["udl:signatura3_data_fi_certificat"] = dateHelper.parse(signatura3_data_fi_certificat);
			}
			if(signatura3_data_caducitat != null) {
				document.properties["udl:signatura3_data_caducitat"] = dateHelper.parse(signatura3_data_caducitat);
			}
			
			if(signatura4_nom != null) {
				document.properties["udl:signatura4_nom"] = signatura4_nom;
			}
			if(signatura4_carrec != null) {
				document.properties["udl:signatura4_carrec"] = signatura4_carrec;
			}
			if(signatura4_dni != null) {
				document.properties["udl:signatura4_dni"] = signatura4_dni;
			}
			if(signatura4_data != null) {
				document.properties["udl:signatura4_data"] = dateHelper.parse(signatura4_data);
			}
			if(signatura4_data_fi_certificat != null) {
				document.properties["udl:signatura4_data_fi_certificat"] = dateHelper.parse(signatura4_data_fi_certificat);
			}
			if(signatura4_data_caducitat != null) {
				document.properties["udl:signatura4_data_caducitat"] = dateHelper.parse(signatura4_data_caducitat);
			}
			
			if(signatura5_nom != null) {
				document.properties["udl:signatura5_nom"] = signatura5_nom;
			}
			if(signatura5_carrec != null) {
				document.properties["udl:signatura5_carrec"] = signatura5_carrec;
			}
			if(signatura5_dni != null) {
				document.properties["udl:signatura5_dni"] = signatura5_dni;
			}
			if(signatura5_data != null) {
				document.properties["udl:signatura5_data"] = dateHelper.parse(signatura5_data);
			}
			if(signatura5_data_fi_certificat != null) {
				document.properties["udl:signatura5_data_fi_certificat"] = dateHelper.parse(signatura5_data_fi_certificat);
			}
			if(signatura5_data_caducitat != null) {
				document.properties["udl:signatura5_data_caducitat"] = dateHelper.parse(signatura5_data_caducitat);
			}
			
			if(signatura6_nom != null) {
				document.properties["udl:signatura6_nom"] = signatura6_nom;
			}
			if(signatura6_carrec != null) {
				document.properties["udl:signatura6_carrec"] = signatura6_carrec;
			}
			if(signatura6_dni != null) {
				document.properties["udl:signatura6_dni"] = signatura6_dni;
			}
			if(signatura6_data != null) {
				document.properties["udl:signatura6_data"] = dateHelper.parse(signatura6_data);
			}
			if(signatura6_data_fi_certificat != null) {
				document.properties["udl:signatura6_data_fi_certificat"] = dateHelper.parse(signatura6_data_fi_certificat);
			}
			if(signatura6_data_caducitat != null) {
				document.properties["udl:signatura6_data_caducitat"] = dateHelper.parse(signatura6_data_caducitat);
			}
			
			if(signatura7_nom != null) {
				document.properties["udl:signatura7_nom"] = signatura7_nom;
			}
			if(signatura7_carrec != null) {
				document.properties["udl:signatura7_carrec"] = signatura7_carrec;
			}
			if(signatura7_dni != null) {
				document.properties["udl:signatura7_dni"] = signatura7_dni;
			}
			if(signatura7_data != null) {
				document.properties["udl:signatura7_data"] = dateHelper.parse(signatura7_data);
			}
			if(signatura7_data_fi_certificat != null) {
				document.properties["udl:signatura7_data_fi_certificat"] = dateHelper.parse(signatura7_data_fi_certificat);
			}
			if(signatura7_data_caducitat != null) {
				document.properties["udl:signatura7_data_caducitat"] = dateHelper.parse(signatura7_data_caducitat);
			}

			if(signatura8_nom != null) {
				document.properties["udl:signatura8_nom"] = signatura8_nom;
			}
			if(signatura8_carrec != null) {
				document.properties["udl:signatura8_carrec"] = signatura8_carrec;
			}
			if(signatura8_dni != null) {
				document.properties["udl:signatura8_dni"] = signatura8_dni;
			}
			if(signatura8_data != null) {
				document.properties["udl:signatura8_data"] = dateHelper.parse(signatura8_data);
			}
			if(signatura8_data_fi_certificat != null) {
				document.properties["udl:signatura8_data_fi_certificat"] = dateHelper.parse(signatura8_data_fi_certificat);
			}
			if(signatura8_data_caducitat != null) {
				document.properties["udl:signatura8_data_caducitat"] = dateHelper.parse(signatura8_data_caducitat);
			}
			
			if(signatura9_nom != null) {
				document.properties["udl:signatura9_nom"] = signatura9_nom;
			}
			if(signatura9_carrec != null) {
				document.properties["udl:signatura9_carrec"] = signatura9_carrec;
			}
			if(signatura9_dni != null) {
				document.properties["udl:signatura9_dni"] = signatura9_dni;
			}
			if(signatura9_data != null) {
				document.properties["udl:signatura9_data"] = dateHelper.parse(signatura9_data);
			}
			if(signatura9_data_fi_certificat != null) {
				document.properties["udl:signatura9_data_fi_certificat"] = dateHelper.parse(signatura9_data_fi_certificat);
			}
			if(signatura9_data_caducitat != null) {
				document.properties["udl:signatura9_data_caducitat"] = dateHelper.parse(signatura9_data_caducitat);
			}
			
			if(signatura10_nom != null) {
				document.properties["udl:signatura10_nom"] = signatura10_nom;
			}
			if(signatura10_carrec != null) {
				document.properties["udl:signatura10_carrec"] = signatura10_carrec;
			}
			if(signatura10_dni != null) {
				document.properties["udl:signatura10_dni"] = signatura10_dni;
			}
			if(signatura10_data != null) {
				document.properties["udl:signatura10_data"] = dateHelper.parse(signatura10_data);
			}
			if(signatura10_data_fi_certificat != null) {
				document.properties["udl:signatura10_data_fi_certificat"] = dateHelper.parse(signatura10_data_fi_certificat);
			}
			if(signatura10_data_caducitat != null) {
				document.properties["udl:signatura10_data_caducitat"] = dateHelper.parse(signatura10_data_caducitat);
			}
			
			if(signatura11_nom != null) {
				document.properties["udl:signatura11_nom"] = signatura11_nom;
			}
			if(signatura11_carrec != null) {
				document.properties["udl:signatura11_carrec"] = signatura11_carrec;
			}
			if(signatura11_dni != null) {
				document.properties["udl:signatura11_dni"] = signatura11_dni;
			}
			if(signatura11_data != null) {
				document.properties["udl:signatura11_data"] = dateHelper.parse(signatura11_data);
			}
			if(signatura11_data_fi_certificat != null) {
				document.properties["udl:signatura11_data_fi_certificat"] = dateHelper.parse(signatura11_data_fi_certificat);
			}
			if(signatura11_data_caducitat != null) {
				document.properties["udl:signatura11_data_caducitat"] = dateHelper.parse(signatura11_data_caducitat);
			}
			
			if(signatura12_nom != null) {
				document.properties["udl:signatura12_nom"] = signatura12_nom;
			}
			if(signatura12_carrec != null) {
				document.properties["udl:signatura12_carrec"] = signatura12_carrec;
			}
			if(signatura12_dni != null) {
				document.properties["udl:signatura12_dni"] = signatura12_dni;
			}
			if(signatura12_data != null) {
				document.properties["udl:signatura12_data"] = dateHelper.parse(signatura12_data);
			}
			if(signatura12_data_fi_certificat != null) {
				document.properties["udl:signatura12_data_fi_certificat"] = dateHelper.parse(signatura12_data_fi_certificat);
			}
			if(signatura12_data_caducitat != null) {
				document.properties["udl:signatura12_data_caducitat"] = dateHelper.parse(signatura12_data_caducitat);
			}
			
			if(grup_creador_documentSimple != null) {
				document.properties["udl:grup_creador_documentSimple"] = grup_creador_documentSimple;
			}
		}
		
		document.save();

	}catch(e){
	   status.code = 500;
	   status.message = handleError(e);
	   status.redirect = true;
	}
	
	model.document = document;
}