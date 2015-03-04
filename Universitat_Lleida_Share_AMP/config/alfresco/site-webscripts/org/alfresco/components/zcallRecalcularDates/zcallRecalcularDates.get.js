<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/console/rm-console.lib.js">

function main()
{
	var meta = [];
	var conn = remote.connect("alfresco");
	var hasAccess = hasCapability(conn, "AccessAudit");

	model.hasAccess = hasAccess;
	
	if (hasAccess) {
		var result = remote.call("http://localhost:8080/alfresco/service/udl/qdc/doRecalcularDates");
		
		if (result.status == 200) {
			model.successMsg = "Dates recalculades correctament.";		

		}else if (result.status == 408) {
			model.errorMsg = "Temps d\'espera superat, el procés continuarà executant-se en un segon pla.";

		}else {
			model.errorMsg = "ERROR al recalcular les dates.";
		}
	}
}

main();