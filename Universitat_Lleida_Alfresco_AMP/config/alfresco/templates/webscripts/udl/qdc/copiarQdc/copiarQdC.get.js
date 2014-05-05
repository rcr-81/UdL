<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/metadataHandler.js">

/**
 * Return a DM folder, if the folder exists update the folder / if not exists create the folder.
 *
 *
 */
function createOrUpdateFolder(folderDM, folderRM, type) {

	logger.log("INICIO - createOrUpdateFolder");

	var query;
	var id;

	if(folderRM.hasAspect("udlrm:fons")) {
		id = folderRM.properties["udlrm:secuencial_identificador_fons"];
		query = "TYPE:\"{http://www.smile.com/model/udl/1.0}fons\" AND @udl\\:secuencial_identificador_fons:\"" + id + "\"";

		if(id == "") {
			model.error = "ERROR a l'intentar sincronitzar els quadres de classificaci\u00f3. El fons \"" + folderRM.properties["cm:name"] + "\" NO t\u00E9 informat el camp seqüencial identificador.";
			return null;
		}

	}else if(folderRM.hasAspect("udlrm:serie")) {
		id = folderRM.properties["udlrm:secuencial_identificador_serie"];
		query = "TYPE:\"{http://www.smile.com/model/udl/1.0}serie\" AND @udl\\:secuencial_identificador_serie:\"" + id + "\"";		

		if(id == "") {
			model.error = "ERROR a l'intentar sincronitzar els quadres de classificaci\u00f3. La s\u00E8rie \"" + folderRM.properties["cm:name"] + "\" NO t\u00E9 informat el camp seqüencial identificador.";
			return null;
		}
	}

	var nodes = search.luceneSearch(query);	

	if(nodes.length > 0) {
		logger.log("Update: " + folderRM.properties["cm:name"]);
		nodes[0].properties["cm:name"] = folderRM.properties["cm:name"];
		nodes[0].properties["cm:description"] = folderRM.properties["cm:description"];
		nodes[0].properties["cm:title"] = folderRM.properties["cm:title"];
		
		copyMetadatos(folderRM, nodes[0]);

		//nodes[0].save();
		
		return nodes[0];			

	} else {
		logger.log("Create: " + folderRM.properties["cm:name"]);
		var newFolder;

/*		
		if(existsNode(folderDM, folderRM.properties.name)) {
			model.error = "ERROR a l'intentar sincronitzar els quadres de classificaci\u00f3. El fons \"" + folderRM.properties.name + "\" ja existeix.";
			return null;
		}
*/
		
		if(folderRM.hasAspect(ASPECT_RM_FONS)){
			newFolder = folderDM.createNode(folderRM.properties.name, TYPE_FONS);	

		} else if(folderRM.hasAspect(ASPECT_RM_SERIE)){
			newFolder = folderDM.createNode(folderRM.properties.name, TYPE_SERIE);
		}

		copyMetadatos(folderRM, newFolder);
		//newFolder.save();
	}
	
	logger.log("FIN - createOrUpdateFolder");
	
	return newFolder;
}

/**
 * Si el node existeix es retorna error i es finalitza la sincronització.
 * 
 */
function existsNode(folderDM, name) {
	var node = roothome.childByNamePath(folderDM.displayPath + "/" + folderDM.name + "/" + name);
	
	if(node != null) {
		return true

	}else {
		return false;
	}
}

/**
 * Main entry point for component webscript logic
 *
 * @method main
 */
function main()
{
	logger.log("INICIO - Sincronización QdCs");
	impersonate.impersonate('admin');
	
	// PRODUCTION
	//var dm = "Company Home/Sites/dm/documentLibrary";
    //var rm = "Company Home/Sites/rm/documentLibrary";
	
	// DEVELOPMENT
	var dm = "Espacio de empresa/Sitios/dm/documentLibrary";
	var rm = "Espacio de empresa/Sitios/rm/documentLibrary";

	var folderDM = roothome.childByNamePath(dm);
	var folderRM = roothome.childByNamePath(rm);
	
	for(var i = 0; i < folderRM.children.length; i++) {
		var fons = folderRM.children[i];
		
		if(fons.hasAspect("udlrm:fons")) {
			var fonsDM = createOrUpdateFolder(folderDM, fons, 'fons');
			fonsDM.save();
			
			if(fonsDM != null) {
				for(var j = 0; j < fons.children.length; j++) {
					var serie = fons.children[j];

					if(serie.hasAspect("udlrm:serie")) {
						var serieDM = createOrUpdateFolder(fonsDM, serie, 'serie');
						
						if(serieDM != null) {
							serieDM.properties["udl:rm_uuid_serie"] = serie.properties["sys:node-uuid"];
							serieDM.save();
						}
					}
				}
			}
		}
	}

	logger.log("FIN - Sincronización QdCs");
}

// Start the webscript
main();