<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">

var success = false;
var expedient = search.findNode("workspace://SpacesStore/" + arguments.expID);
if (expedient == undefined || !expedient.isContainer) {
	model.error = "Expedient with id '" + arguments.expID + "' not found.";
}
else {
	try {
		var estat = expedient.properties["udl:estat"];
		if(estat == 'tancat'){
			expedient.properties["udl:estat"] = "obert";
		}
		else if (estat == 'arxivat'){
			expedient.properties["udl:estat"] = "obert";
			//cal moure de false semi-activa a fase activa: comentar amb udl si cal automatitzar aquesta tasca
		}
		expedient.save();
		success = true;
	}
	catch(e){
		model.error = handleError(e);
	}
}

model.expedient = expedient;
model.success = String(success);