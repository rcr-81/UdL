//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//********** DEPRECATED: all transfer is done with TransferirExpedient.java! ***********
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************
//**************************************************************************************

<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">

var username = null;
var path = null;
var name = null;
var nodeRef = null;
var assocsMap = new Array();
var documents = new Array();
var signaturesMap = new Array();
var foliacio = "<indexTransferencia>\n";
var exp = null;

for each (field in formdata.fields){
	if (field.name == "username"){
		username = field.value;

	} else if (field.name == "expPath"){
		path = field.value;

	} else if (field.name == "expName"){
		name = field.value;
	}
}	

function main() {
	impersonate.impersonate(username);	
	var pathExpedient = path + "/" + name;
	var expedient = roothome.childByNamePath(pathExpedient);
	
	try {
		model.node = transferirNode(expedient, null, true);		
		/*
		if(documents.length != 0){
			for(var i=0; i<documents.length; i++){
				var assocsList = assocsMap[documents[i].id];
				if(assocsList != null){
					for(var j=0; j<assocsList.length; j++){
						var oldSignatura = assocsList[j];
						var newSignatura = signaturesMap[oldSignatura.id];
						var doc = documents[i];
						doc.createAssociation(newSignatura, "cm:signaturesDocumentRm");					
					}
				}
			}
		}
		*/
		//expedient.remove();

	} catch(e){
		status.code = 500;
		status.message = handleError(e);
		status.redirect = true;
	}		
}

function transferirNode(nodeCm, rmParent, root){	
	if(nodeCm != null){			
		var nodeRma = null;
		if(root){
			var codiClassificacio = nodeCm.parent.properties["udl:codi_classificacio_serie"];
			
			//buscar serie a RM amb el mateix codi de classificació i assignar-lo com a nodeRma
			var nodeRma = search.luceneSearch("TYPE:\"{http://www.alfresco.org/model/dod5015/1.0}recordCategory\" AND ASPECT:\"{http://www.smile.com/model/udlrm/1.0}serie\" AND @udlrm\\:codi_classificacio_serie:\"" + codiClassificacio + "\"");

			nodeRma = nodeRma[0];
			if(nodeRma == undefined || !nodeRma.isContainer){
				status.code = 500;
				status.message = 'Unable to transfer node "' + nodeCm.name + '". Records management location with the classification code = "' + codiClassificacio + '" not found in RM [urgrm:serie] node type instances.';
				status.redirect = true;	
			}		
		} 
		else {
			nodeRma = rmParent;
		}
		
		if(nodeRma != null){
			if(nodeCm.isContainer){
				var newNodeRma = null;

				if(nodeCm.getTypeShort() == "udl:expedient"){					
					newNodeRma = nodeRma.createNode(nodeCm.name, "rma:recordFolder");
					newNodeRma.addAspect("udlrm:expedient");
					newNodeRma.properties["rma:identifier"] = newNodeRma.id;
					newNodeRma.properties["rma:creator"] = username;
					newNodeRma.properties["udlrm:tipus_entitat_expedient"] = nodeCm.properties["udl:tipus_entitat_expedient"];
					newNodeRma.properties["udlrm:categoria_expedient"] = nodeCm.properties["udl:categoria_expedient"];
					newNodeRma.properties["udlrm:descripcio_expedient"] = nodeCm.properties["udl:descripcio_expedient"];
					newNodeRma.properties["udlrm:idioma_expedient"] = nodeCm.properties["udl:idioma_expedient"];				
					newNodeRma.properties["udlrm:secuencial_identificador_expedient"] = nodeCm.properties["udl:secuencial_identificador_expedient"];
					newNodeRma.properties["udlrm:esquema_identificador_expedient"] = nodeCm.properties["udl:esquema_identificador_expedient"];
					newNodeRma.properties["udlrm:nom_natural_expedient"] = nodeCm.properties["udl:nom_natural_expedient"];
					newNodeRma.properties["udlrm:data_inici_expedient"] = nodeCm.properties["udl:data_inici_expedient"];
					newNodeRma.properties["udlrm:data_fi_expedient"] = nodeCm.properties["udl:data_fi_expedient"];					
					newNodeRma.properties["udlrm:classificacio_acces_expedient"] = nodeCm.properties["udl:classificacio_acces_expedient"];
					newNodeRma.properties["udlrm:codi_politica_control_acces_expedient"] = nodeCm.properties["udl:codi_politica_control_acces_expedient"];
					newNodeRma.properties["udlrm:sensibilitat_dades_caracter_personal_expedient"] = nodeCm.properties["udl:sensibilitat_dades_caracter_personal_expedient"];
					newNodeRma.properties["udlrm:tipus_acces_expedient"] = nodeCm.properties["udl:tipus_acces_expedient"];
					newNodeRma.properties["udlrm:valoracio_expedient"] = nodeCm.properties["udl:valoracio_expedient"];															
					newNodeRma.properties["udlrm:tipus_dictamen_expedient"] = nodeCm.properties["udl:tipus_dictamen_expedient"];
					newNodeRma.properties["udlrm:accio_dictaminada_expedient"] = nodeCm.properties["udl:accio_dictaminada_expedient"];
					newNodeRma.properties["udlrm:suport_origen_expedient"] = nodeCm.properties["udl:suport_origen_expedient"];
					newNodeRma.properties["udlrm:nom_format_expedient"] = nodeCm.properties["udl:nom_format_expedient"];
					newNodeRma.properties["udlrm:versio_format_expedient"] = nodeCm.properties["udl:versio_format_expedient"];										
					newNodeRma.properties["udlrm:nom_aplicacio_creacio_expedient"] = nodeCm.properties["udl:nom_aplicacio_creacio_expedient"];
					newNodeRma.properties["udlrm:versio_aplicacio_creacio_expedient"] = nodeCm.properties["udl:versio_aplicacio_creacio_expedient"];
					newNodeRma.properties["udlrm:registre_formats_expedient"] = nodeCm.properties["udl:registre_formats_expedient"];
					newNodeRma.properties["udlrm:dimensions_fisiques_expedient"] = nodeCm.properties["udl:dimensions_fisiques_expedient"];
					newNodeRma.properties["udlrm:tamany_logic_expedient"] = nodeCm.properties["udl:tamany_logic_expedient"];
					newNodeRma.properties["udlrm:quantitat_expedient"] = nodeCm.properties["udl:quantitat_expedient"];
					newNodeRma.properties["udlrm:unitats_expedient"] = nodeCm.properties["udl:unitats_expedient"];
					newNodeRma.properties["udlrm:suport_expedient"] = nodeCm.properties["udl:suport_expedient"];
					newNodeRma.properties["udlrm:localitzacio_expedient"] = nodeCm.properties["udl:localitzacio_expedient"];
					newNodeRma.properties["udlrm:algoritme_expedient"] = nodeCm.properties["udl:algoritme_expedient"];
					newNodeRma.properties["udlrm:valor_expedient"] = nodeCm.properties["udl:valor_expedient"];
					newNodeRma.properties["udlrm:tipus_signatura_expedient"] = nodeCm.properties["udl:tipus_signatura_expedient"];
					newNodeRma.properties["udlrm:format_signatura_expedient"] = nodeCm.properties["udl:format_signatura_expedient"];
					newNodeRma.properties["udlrm:rol_signatura_expedient"] = nodeCm.properties["udl:rol_signatura_expedient"];
					newNodeRma.properties["udlrm:denominacio_estat_expedient"] = nodeCm.properties["udl:denominacio_estat_expedient"];
					newNodeRma.properties["udlrm:accio_expedient"] = nodeCm.properties["udl:accio_expedient"];
					newNodeRma.properties["udlrm:motiu_reglat_expedient"] = nodeCm.properties["udl:motiu_reglat_expedient"];
					newNodeRma.properties["udlrm:usuari_accio_expedient"] = nodeCm.properties["udl:usuari_accio_expedient"];
					newNodeRma.properties["udlrm:modificacio_metadades_expedient"] = nodeCm.properties["udl:modificacio_metadades_expedient"];																																																																	
					newNodeRma.properties["udlrm:codi_classificacio_expedient"] = nodeCm.properties["udl:codi_classificacio_expedient"];
					newNodeRma.properties["udlrm:denominacio_classe_expedient"] = nodeCm.properties["udl:denominacio_classe_expedient"];
					newNodeRma.save();
					exp = newNodeRma;
					
					foliacio = foliacio + 
					"<expedient>\n<nom>" + 
					newNodeRma.properties["cm:name"] +
					"</nom>\n<id>" + 
					newNodeRma.id + 
					"</id>\n" +				
					"<creador>" + 
					newNodeRma.properties["cm:creator"] +
					"</creador>\n" +
					"<dataCreacio>" + 
					newNodeRma.properties["cm:created"] +
					"</dataCreacio>\n" +
					"<modificador>" + 
					newNodeRma.properties["cm:modifier"] +
					"</modificador>\n" +					
					"<dataModificacio>" + 
					newNodeRma.properties["cm:modified"] +
					"</dataModificacio>\n" +
					"</expedient>\n";					
				}
				else if(nodeCm.getTypeShort() == "udl:agregacio"){					
					newNodeRma = nodeRma.createNode(nodeCm.name, "rma:recordFolder");	
					newNodeRma.addAspect("udlrm:agregacio");
					newNodeRma.properties["rma:identifier"] = newNodeRma.id;
					newNodeRma.properties["rma:creator"] = username;
					newNodeRma.properties["udlrm:tipus_entitat_agregacio"] = nodeCm.properties["udl:tipus_entitat_agregacio"];
					newNodeRma.properties["udlrm:categoria_agregacio"] = nodeCm.properties["udl:categoria_agregacio"];
					newNodeRma.properties["udlrm:descripcio_agregacio"] = nodeCm.properties["udl:descripcio_agregacio"];
					newNodeRma.properties["udlrm:idioma_agregacio"] = nodeCm.properties["udl:idioma_agregacio"];				
					newNodeRma.properties["udlrm:secuencial_identificador_agregacio"] = nodeCm.properties["udl:secuencial_identificador_agregacio"];
					newNodeRma.properties["udlrm:esquema_identificador_agregacio"] = nodeCm.properties["udl:esquema_identificador_agregacio"];
					newNodeRma.properties["udlrm:nom_natural_agregacio"] = nodeCm.properties["udl:nom_natural_agregacio"];
					newNodeRma.properties["udlrm:data_inici_agregacio"] = nodeCm.properties["udl:data_inici_agregacio"];
					newNodeRma.properties["udlrm:data_fi_agregacio"] = nodeCm.properties["udl:data_fi_agregacio"];					
					newNodeRma.properties["udlrm:classificacio_acces_agregacio"] = nodeCm.properties["udl:classificacio_acces_agregacio"];
					newNodeRma.properties["udlrm:codi_politica_control_acces_agregacio"] = nodeCm.properties["udl:codi_politica_control_acces_agregacio"];
					newNodeRma.properties["udlrm:sensibilitat_dades_caracter_personal_agregacio"] = nodeCm.properties["udl:sensibilitat_dades_caracter_personal_agregacio"];
					newNodeRma.properties["udlrm:tipus_acces_agregacio"] = nodeCm.properties["udl:tipus_acces_agregacio"];
					newNodeRma.properties["udlrm:valoracio_agregacio"] = nodeCm.properties["udl:valoracio_agregacio"];															
					newNodeRma.properties["udlrm:tipus_dictamen_agregacio"] = nodeCm.properties["udl:tipus_dictamen_agregacio"];
					newNodeRma.properties["udlrm:accio_dictaminada_agregacio"] = nodeCm.properties["udl:accio_dictaminada_agregacio"];
					newNodeRma.properties["udlrm:suport_origen_agregacio"] = nodeCm.properties["udl:suport_origen_agregacio"];
					newNodeRma.properties["udlrm:nom_format_agregacio"] = nodeCm.properties["udl:nom_format_agregacio"];
					newNodeRma.properties["udlrm:versio_format_agregacio"] = nodeCm.properties["udl:versio_format_agregacio"];										
					newNodeRma.properties["udlrm:nom_aplicacio_creacio_agregacio"] = nodeCm.properties["udl:nom_aplicacio_creacio_agregacio"];
					newNodeRma.properties["udlrm:versio_aplicacio_creacio_agregacio"] = nodeCm.properties["udl:versio_aplicacio_creacio_agregacio"];
					newNodeRma.properties["udlrm:registre_formats_agregacio"] = nodeCm.properties["udl:registre_formats_agregacio"];
					newNodeRma.properties["udlrm:dimensions_fisiques_agregacio"] = nodeCm.properties["udl:dimensions_fisiques_agregacio"];
					newNodeRma.properties["udlrm:tamany_logic_agregacio"] = nodeCm.properties["udl:tamany_logic_agregacio"];
					newNodeRma.properties["udlrm:quantitat_agregacio"] = nodeCm.properties["udl:quantitat_agregacio"];
					newNodeRma.properties["udlrm:unitats_agregacio"] = nodeCm.properties["udl:unitats_agregacio"];
					newNodeRma.properties["udlrm:suport_agregacio"] = nodeCm.properties["udl:suport_agregacio"];
					newNodeRma.properties["udlrm:localitzacio_agregacio"] = nodeCm.properties["udl:localitzacio_agregacio"];
					newNodeRma.properties["udlrm:algoritme_agregacio"] = nodeCm.properties["udl:algoritme_agregacio"];
					newNodeRma.properties["udlrm:valor_agregacio"] = nodeCm.properties["udl:valor_agregacio"];
					newNodeRma.properties["udlrm:tipus_signatura_agregacio"] = nodeCm.properties["udl:tipus_signatura_agregacio"];
					newNodeRma.properties["udlrm:format_signatura_agregacio"] = nodeCm.properties["udl:format_signatura_agregacio"];
					newNodeRma.properties["udlrm:rol_signatura_agregacio"] = nodeCm.properties["udl:rol_signatura_agregacio"];
					newNodeRma.properties["udlrm:denominacio_estat_agregacio"] = nodeCm.properties["udl:denominacio_estat_agregacio"];
					newNodeRma.properties["udlrm:accio_agregacio"] = nodeCm.properties["udl:accio_agregacio"];
					newNodeRma.properties["udlrm:motiu_reglat_agregacio"] = nodeCm.properties["udl:motiu_reglat_agregacio"];
					newNodeRma.properties["udlrm:usuari_accio_agregacio"] = nodeCm.properties["udl:usuari_accio_agregacio"];
					newNodeRma.properties["udlrm:modificacio_metadades_agregacio"] = nodeCm.properties["udl:modificacio_metadades_agregacio"];																																																																	
					newNodeRma.properties["udlrm:codi_classificacio_agregacio"] = nodeCm.properties["udl:codi_classificacio_agregacio"];
					newNodeRma.properties["udlrm:denominacio_classe_agregacio"] = nodeCm.properties["udl:denominacio_classe_agregacio"];
					newNodeRma.save();
					exp = newNodeRma;
					
					foliacio = foliacio + 
					"<agregacio>\n<nom>" + 
					newNodeRma.properties["cm:name"] +
					"</nom>\n<id>" + 
					newNodeRma.id + 
					"</id>\n" +				
					"<creador>" + 
					newNodeRma.properties["cm:creator"] +
					"</creador>\n" +
					"<dataCreacio>" + 
					newNodeRma.properties["cm:created"] +
					"</dataCreacio>\n" +
					"<modificador>" + 
					newNodeRma.properties["cm:modifier"] +
					"</modificador>\n" +					
					"<dataModificacio>" + 
					newNodeRma.properties["cm:modified"] +
					"</dataModificacio>\n" +
					"</agregacio>\n";					
				}
				
				var nodesCm = nodeCm.children;				
				for(var i=0; i<nodesCm.length;i++){
					transferirNode(nodesCm[i], newNodeRma, false);
				}
			}
			else {
				var newNodeRma = nodeRma.createNode(nodeCm.name, "cm:content");
				newNodeRma.content = nodeCm.content;
				newNodeRma.properties["cm:title"] = nodeCm.properties["cm:name"];
				newNodeRma.properties["cm:description"] = nodeCm.properties["cm:description"];
				newNodeRma.properties["rma:originator"] = "originator";
				newNodeRma.properties["rma:originatingOrganization"] = "originating organization";
				newNodeRma.properties["rma:publicationDate"] = dateHelper.getNow();
				newNodeRma.properties["rma:identifier"] = newNodeRma.id; 
				newNodeRma.properties["rma:dateFiled"] = dateHelper.getNow();
				newNodeRma.properties["rma:creator"] = username;
				//newNodeRma.properties["cm:created"] = nodeCm.properties["cm:created"];
				//newNodeRma.properties["cm:modified"] = nodeCm.properties["cm:modified"];
				newNodeRma.save();
						
				if(nodeCm.getTypeShort() == "udl:documentSimple") {
					addAspects(nodeCm, newNodeRma);
					newNodeRma.addAspect("udlrm:documentSimple");
					newNodeRma.properties["udlrm:tipus_entitat_documentSimple"] = nodeCm.properties["udl:tipus_entitat_documentSimple"];
					newNodeRma.properties["udlrm:categoria_documentSimple"] = nodeCm.properties["udl:categoria_documentSimple"];													
					newNodeRma.properties["udlrm:secuencial_identificador_documentSimple"] = nodeCm.properties["udl:secuencial_identificador_documentSimple"];
					newNodeRma.properties["udlrm:esquema_identificador_documentSimple"] = nodeCm.properties["udl:esquema_identificador_documentSimple"];
					newNodeRma.properties["udlrm:nom_natural_documentSimple"] = nodeCm.properties["udl:nom_natural_documentSimple"];
					newNodeRma.properties["udlrm:data_inici_documentSimple"] = nodeCm.properties["udl:data_inici_documentSimple"];
					newNodeRma.properties["udlrm:data_fi_documentSimple"] = nodeCm.properties["udl:data_fi_documentSimple"];					
					newNodeRma.properties["udlrm:descripcio_documentSimple"] = nodeCm.properties["udl:descripcio_documentSimple"];														
					newNodeRma.properties["udlrm:classificacio_acces_documentSimple"] = nodeCm.properties["udl:classificacio_acces_documentSimple"];
					newNodeRma.properties["udlrm:codi_politica_control_acces_documentSimple"] = nodeCm.properties["udl:codi_politica_control_acces_documentSimple"];
					newNodeRma.properties["udlrm:sensibilitat_dades_caracter_personal_documentSimple"] = nodeCm.properties["udl:sensibilitat_dades_caracter_personal_documentSimple"];
					newNodeRma.properties["udlrm:tipus_acces_documentSimple"] = nodeCm.properties["udl:tipus_acces_documentSimple"];
					newNodeRma.properties["udlrm:idioma_documentSimple"] = nodeCm.properties["udl:idioma_documentSimple"];
					newNodeRma.properties["udlrm:valoracio_documentSimple"] = nodeCm.properties["udl:valoracio_documentSimple"];													
					newNodeRma.properties["udlrm:tipus_dictamen_documentSimple"] = nodeCm.properties["udl:tipus_dictamen_documentSimple"];
					newNodeRma.properties["udlrm:accio_dictaminada_documentSimple"] = nodeCm.properties["udl:accio_dictaminada_documentSimple"];
					newNodeRma.properties["udlrm:suport_origen_documentSimple"] = nodeCm.properties["udl:suport_origen_documentSimple"];
					newNodeRma.properties["udlrm:nom_format_documentSimple"] = nodeCm.properties["udl:nom_format_documentSimple"];
					newNodeRma.properties["udlrm:versio_format_documentSimple"] = nodeCm.properties["udl:versio_format_documentSimple"];										
					newNodeRma.properties["udlrm:nom_aplicacio_creacio_documentSimple"] = nodeCm.properties["udl:nom_aplicacio_creacio_documentSimple"];
					newNodeRma.properties["udlrm:versio_aplicacio_creacio_documentSimple"] = nodeCm.properties["udl:versio_aplicacio_creacio_documentSimple"];
					newNodeRma.properties["udlrm:registre_formats_documentSimple"] = nodeCm.properties["udl:registre_formats_documentSimple"];
					newNodeRma.properties["udlrm:resolucio_documentSimple"] = nodeCm.properties["udl:resolucio_documentSimple"];
					newNodeRma.properties["udlrm:dimensions_fisiques_documentSimple"] = nodeCm.properties["udl:dimensions_fisiques_documentSimple"];
					newNodeRma.properties["udlrm:tamany_logic_documentSimple"] = nodeCm.properties["udl:tamany_logic_documentSimple"];
					newNodeRma.properties["udlrm:quantitat_documentSimple"] = nodeCm.properties["udl:quantitat_documentSimple"];
					newNodeRma.properties["udlrm:unitats_documentSimple"] = nodeCm.properties["udl:unitats_documentSimple"];
					newNodeRma.properties["udlrm:suport_documentSimple"] = nodeCm.properties["udl:suport_documentSimple"];
					newNodeRma.properties["udlrm:localitzacio_documentSimple"] = nodeCm.properties["udl:localitzacio_documentSimple"];
					newNodeRma.properties["udlrm:algoritme_documentSimple"] = nodeCm.properties["udl:algoritme_documentSimple"];
					newNodeRma.properties["udlrm:valor_documentSimple"] = nodeCm.properties["udl:valor_documentSimple"];
					newNodeRma.properties["udlrm:tipus_signatura_documentSimple"] = nodeCm.properties["udl:tipus_signatura_documentSimple"];
					newNodeRma.properties["udlrm:format_signatura_documentSimple"] = nodeCm.properties["udl:format_signatura_documentSimple"];
					newNodeRma.properties["udlrm:rol_signatura_documentSimple"] = nodeCm.properties["udl:rol_signatura_documentSimple"];
					newNodeRma.properties["udlrm:tipus_documental_documentSimple"] = nodeCm.properties["udl:tipus_documental_documentSimple"];
					newNodeRma.properties["udlrm:denominacio_estat_documentSimple"] = nodeCm.properties["udl:denominacio_estat_documentSimple"];
					newNodeRma.properties["udlrm:tipus_copia_documentSimple"] = nodeCm.properties["udl:tipus_copia_documentSimple"];
					newNodeRma.properties["udlrm:motiu_documentSimple"] = nodeCm.properties["udl:motiu_documentSimple"];
					newNodeRma.properties["udlrm:accio_documentSimple"] = nodeCm.properties["udl:accio_documentSimple"];
					newNodeRma.properties["udlrm:motiu_reglat_documentSimple"] = nodeCm.properties["udl:motiu_reglat_documentSimple"];
					newNodeRma.properties["udlrm:usuari_accio_documentSimple"] = nodeCm.properties["udl:usuari_accio_documentSimple"];
					newNodeRma.properties["udlrm:modificacio_metadades_documentSimple"] = nodeCm.properties["udl:modificacio_metadades_documentSimple"];																																																																
					newNodeRma.properties["udlrm:codi_classificacio_documentSimple"] = nodeCm.properties["udl:codi_classificacio_documentSimple"];
					newNodeRma.properties["udlrm:denominacio_classe_documentSimple"] = nodeCm.properties["udl:denominacio_classe_documentSimple"];

					foliacio = foliacio + 
					"<documentSimple>\n<nom>" + 
					newNodeRma.properties["cm:name"] + 
					"</nom>\n<id>"+ 
					newNodeRma.id + 
					"</id>\n<md5>" + 
					cript.md5(nodeCm.nodeRef) + 
					"</md5>\n" +					
					"<creador>" + 
					newNodeRma.properties["cm:creator"] +
					"</creador>\n" +
					"<dataCreacio>" + 
					newNodeRma.properties["cm:created"] +
					"</dataCreacio>\n" +
					"<modificador>" + 
					newNodeRma.properties["cm:modifier"] +
					"</modificador>\n" +					
					"<dataModificacio>" + 
					newNodeRma.properties["cm:modified"] +
					"</dataModificacio>\n" +
					"<tamany>" + 
					newNodeRma.size +
					"</tamany>\n" +
					"<tipusMIME>" + 
					newNodeRma.mimetype +
					"</tipusMIME>\n</documentSimple>\n";
					
					/*
					documents.push(newNodeRma);
					var assocs = nodeCm.assocs["udl:signaturesDocument"];		
					if(assocs != null){
						for(var i=0; i<assocs.length; i++){											
							var signatura = assocs[i];								
							if(signatura.getTypeShort() == "udl:signaturaDettached"){
								if(assocsMap[newNodeRma.id] == null){
									assocsMap[newNodeRma.id] = new Array();
								}								
								assocsMap[newNodeRma.id].push(signatura);
							}
						}
					}
					*/
					
					newNodeRma.save();
				}
				/*
				else if(nodeCm.getTypeShort() == "udl:signaturaDettached") {					
					newNodeRma.addAspect("udlrm:signaturaDettached");
					newNodeRma.properties["udlrm:tipus_signatura"] = nodeCm.properties["udl:tipus_signatura"];
					newNodeRma.properties["udlrm:id_document_signat"] = nodeCm.properties["udl:id_document_signat"];
					newNodeRma.properties["udlrm:data_signatura"] = nodeCm.properties["udl:data_signatura"];
					newNodeRma.properties["udlrm:data_ini_validacio_signatura"] = nodeCm.properties["udl:data_ini_validacio_signatura"];
					newNodeRma.properties["udlrm:data_fi_validacio_signatura"] = nodeCm.properties["udl:data_fi_validacio_signatura"];
					newNodeRma.properties["udlrm:validacio_signatura"] = nodeCm.properties["udl:validacio_signatura"];
					newNodeRma.properties["udlrm:nom_signatari_signatura"] = nodeCm.properties["udl:nom_signatari_signatura"];
					newNodeRma.properties["udlrm:identificador_signatari_signatura"] = nodeCm.properties["udl:identificador_signatari_signatura"];
					newNodeRma.properties["udlrm:organitzacio_signatura"] = nodeCm.properties["udl:organitzacio_signatura"];
					newNodeRma.properties["udlrm:unitat_organica_signatura"] = nodeCm.properties["udl:unitat_organica_signatura"];
					newNodeRma.properties["udlrm:politica_signatura"] = nodeCm.properties["udl:politica_signatura"];
					newNodeRma.properties["udlrm:proveidor_certificat_signatura"] = nodeCm.properties["udl:proveidor_certificat_signatura"];
					newNodeRma.properties["udlrm:tipus_certificat_signatura"] = nodeCm.properties["udl:tipus_certificat_signatura"];
					
					foliacio = foliacio + 
					"<signaturaDettached>\n<nomSignatari>" + 
					newNodeRma.properties["udlrm:nom_signatari_signatura"] + 
					"</nomSignatari>\n"+ 
					newNodeRma.id + 
					"</id>\n<md5>" + 
					cript.md5(nodeCm.nodeRef) + 
					"</md5>\n" +					
					"<creador>" + 
					newNodeRma.properties["cm:creator"] +
					"</creador>\n" +
					"<dataCreacio>" + 
					newNodeRma.properties["cm:created"] +
					"</dataCreacio>\n" +
					"<modificador>" + 
					newNodeRma.properties["cm:modifier"] +
					"</modificador>\n" +					
					"<dataModificacio>" + 
					newNodeRma.properties["cm:modified"] +
					"</dataModificacio>\n" +
					"<tamany>" + 
					newNodeRma.size +
					"</tamany>\n" +
					"<tipusMIME>" + 
					newNodeRma.mimetype +
					"</tipusMIME>\n</signaturaDettached>\n";
										
					signaturesMap[nodeCm.id] = newNodeRma;
					newNodeRma.save();					
				}
				*/
			}		
		}	
	}
	
	if(root == true){
		foliacio = foliacio + "</indexTransferencia>"
		var filename = "index.xml";
		var index = exp.createNode(filename, "cm:content");
		index.content = foliacio;
		index.properties.content.setEncoding("UTF-8");
		index.properties.content.guessMimetype(filename);		
		index.properties["cm:title"] = filename;
		index.properties["rma:originator"] = "originator";
		index.properties["rma:originatingOrganization"] = "originating organization";
		index.properties["rma:publicationDate"] = dateHelper.getNow();
		index.properties["rma:identifier"] = newNodeRma.id; 
		index.properties["rma:dateFiled"] = dateHelper.getNow();
		index.setInheritsPermissions(false);
		index.setPermission("Consumer");
		index.setOwner("admin");
		index.save();
		return newNodeRma;			
	}	
}

function addAspects(nodeCm, newNodeRma) {
	if(nodeCm.hasAspect("udl:regulacio")) {
		newNodeRma.addAspect("udl:regulacio");
		newNodeRma.properties["udl:nom_natural_regulacio"] = nodeCm.properties["udl:nom_natural_regulacio"];
		newNodeRma.save();
	}

	if(nodeCm.hasAspect("udl:institucio")) {
		newNodeRma.addAspect("udl:institucio");
		newNodeRma.properties["udl:nom_natural_institucio"] = nodeCm.properties["udl:nom_natural_institucio"];
		newNodeRma.save();
	}
	
	if(nodeCm.hasAspect("udl:organ")) {
		newNodeRma.addAspect("udl:organ");
		newNodeRma.properties["udl:nom_natural_organ"] = nodeCm.properties["udl:nom_natural_organ"];
		newNodeRma.save();
	}
	
	if(nodeCm.hasAspect("udl:persona")) {
		newNodeRma.addAspect("udl:persona");
		newNodeRma.properties["udl:secuencial_identificador_persona"] = nodeCm.properties["udl:secuencial_identificador_persona"];
		newNodeRma.properties["udl:esquema_identificador_persona"] = nodeCm.properties["udl:esquema_identificador_persona"];
		newNodeRma.properties["udl:nom_natural_persona"] = nodeCm.properties["udl:nom_natural_persona"];
		newNodeRma.properties["udl:esquema_nom_persona"] = nodeCm.properties["udl:esquema_nom_persona"];
		newNodeRma.properties["udl:data_inici_persona"] = nodeCm.properties["udl:data_inici_persona"];
		newNodeRma.properties["udl:data_fi_persona"] = nodeCm.properties["udl:data_fi_persona"];
		newNodeRma.properties["udl:tipus_contacte_persona"] = nodeCm.properties["udl:tipus_contacte_persona"];
		newNodeRma.properties["udl:dada_contacte_persona"] = nodeCm.properties["udl:dada_contacte_persona"];		
		newNodeRma.properties["udl:ocupacio_persona"] = nodeCm.properties["udl:ocupacio_persona"];
		newNodeRma.save();
	}
	
	if(nodeCm.hasAspect("udl:signatura")) {
		newNodeRma.addAspect("udl:signatura");
		newNodeRma.properties["udl:signatura1_nom"] = nodeCm.properties["udl:signatura1_nom"];
		newNodeRma.properties["udl:signatura1_dni"] = nodeCm.properties["udl:signatura1_dni"];
		newNodeRma.properties["udl:signatura1_data"] = nodeCm.properties["udl:signatura1_data"];
		newNodeRma.properties["udl:signatura2_nom"] = nodeCm.properties["udl:signatura2_nom"];
		newNodeRma.properties["udl:signatura2_dni"] = nodeCm.properties["udl:signatura2_dni"];
		newNodeRma.properties["udl:signatura2_data"] = nodeCm.properties["udl:signatura2_data"];
		newNodeRma.properties["udl:signatura3_nom"] = nodeCm.properties["udl:signatura3_nom"];
		newNodeRma.properties["udl:signatura3_dni"] = nodeCm.properties["udl:signatura3_dni"];
		newNodeRma.properties["udl:signatura3_data"] = nodeCm.properties["udl:signatura3_data"];
		newNodeRma.save();
	}
}

//Start the webscript
main();