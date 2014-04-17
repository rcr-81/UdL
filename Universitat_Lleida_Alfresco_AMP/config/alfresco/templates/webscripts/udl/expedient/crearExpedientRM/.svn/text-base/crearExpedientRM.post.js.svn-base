<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/metadataHandler.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

var success = false;
var carpeta = null;
var type = null;

var parentFolder = roothome.childByNamePath(arguments.carpetaPath);
if (parentFolder == undefined || !parentFolder.isContainer) {
	model.error = "Folder '" + arguments.carpetaPath + "' not found.";
	status.code = 500;
	status.message = "Folder '" + arguments.carpetaPath + "' not found.";
	status.redirect = true;	
}
else {
	try {
		type = arguments.carpetaType;
		carpeta = parentFolder.createNode(arguments.carpetaName, type);			
		carpeta.setOwner(arguments.username);
		
		if(type == "rma:recordFolder") {
			carpeta.properties["udlrm:tipus_entitat_expedient"] = "Document";
			carpeta.properties["udlrm:categoria_expedient"] = "Expedient";
			carpeta.properties["udlrm:secuencial_identificador_expedient"] = String(arguments.metadades.secuencial_identificador_expedient);
			carpeta.properties["udlrm:nom_natural_expedient"] = String(arguments.metadades.nom_natural_expedient);
			carpeta.properties["udlrm:data_inici_expedient"] = dateHelper.parse(arguments.metadades.data_inici_expedient);
			carpeta.properties["udlrm:data_fi_expedient"] = dateHelper.parse(arguments.metadades.data_fi_expedient);
			carpeta.properties["udlrm:descripcio_expedient"] = String(arguments.metadades.descripcio_expedient);
			carpeta.properties["udlrm:tipus_dictamen_1_expedient"] = String(arguments.metadades.tipus_dictamen_1_expedient);
			carpeta.properties["udlrm:tipus_dictamen_2_expedient"] = String(arguments.metadades.tipus_dictamen_2_expedient);
			carpeta.properties["udlrm:dimensions_fisiques_expedient"] = String(arguments.metadades.tamany_logic_expedient);
			carpeta.properties["udlrm:localitzacio_1_expedient"] = String(arguments.metadades.localitzacio_1_expedient);
			if(arguments.metadades.localitzacio_2_expedient != null) {
				carpeta.properties["udlrm:localitzacio_2_expedient"] = String(arguments.metadades.localitzacio_2_expedient);	
			} 
			carpeta.properties["udlrm:codi_classificacio_1_expedient"] = String(arguments.metadades.codi_classificacio_1_expedient);
			carpeta.properties["udlrm:codi_classificacio_2_expedient"] = String(arguments.metadades.codi_classificacio_2_expedient);
			carpeta.properties["udlrm:denominacio_classe_1_expedient"] = String(arguments.metadades.denominacio_classe_1_expedient);
			carpeta.properties["udlrm:denominacio_classe_2_expedient"] = String(arguments.metadades.denominacio_classe_2_expedient);
			carpeta.properties["udlrm:grup_creador_expedient"] = String(arguments.metadades.grup_creador_expedient);
			carpeta.properties["udlrm:suport_origen_expedient"] = String(arguments.metadades.suport_origen_expedient);
			carpeta.properties["udlrm:suport_1_expedient"] = String(arguments.metadades.suport_1_expedient);
			carpeta.properties["udlrm:tipus_dictamen_1_expedient"] = String(arguments.metadades.tipus_dictamen_1_expedient);
			carpeta.properties["udlrm:tipus_dictamen_2_expedient"] = String(arguments.metadades.tipus_dictamen_2_expedient);
			carpeta.properties["cm:created"] = dateHelper.parse(arguments.metadades.created);
			carpeta.properties["cm:modified"] = dateHelper.parse(arguments.metadades.modified);
		}
		
		carpeta.save();
		
		var props;
		
		if(arguments.metadades.nom_natural_persona != "" && arguments.metadades.nom_natural_persona != null) {
			props = new Array(1);
			props["udl:nom_natural_persona"] = String(arguments.metadades.nom_natural_persona);
			carpeta.addAspect("udl:persona", props);
		
		}
		
		if(arguments.metadades.nom_natural_organ != "" && arguments.metadades.nom_natural_organ != null) {
			props = new Array(1);
			props["udl:nom_natural_organ"] = String(arguments.metadades.nom_natural_organ);
			carpeta.addAspect("udl:organ", props);

		}
		
		if(arguments.metadades.nom_natural_institucio != "" && arguments.metadades.nom_natural_institucio != null) {
			props = new Array(1);
			props["udl:nom_natural_institucio"] = String(arguments.metadades.nom_natural_institucio);
			carpeta.addAspect("udl:institucio", props);
		}
		
		carpeta.save();
		success = true;
		
		//audit
		var type = carpeta.getTypeShort();
		var site = carpeta.getSiteShortName();
		var nodeRef = carpeta.getNodeRef();
		
	    audit("url_audit_create_expedient", nodeRef, type, site, arguments.username);	

	}catch(e){
		status.code = 500;
		status.message = handleError(e, "crearExpedientRM.post.js");
		status.redirect = true;	
	}
}

model.carpeta = carpeta;
model.success = String(success);