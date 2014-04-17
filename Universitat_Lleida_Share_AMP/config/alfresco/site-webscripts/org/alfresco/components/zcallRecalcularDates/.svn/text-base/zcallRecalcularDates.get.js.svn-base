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
		}
		else {
			model.errorMsg = "ERROR al recalcular les dates.";	
		}
	}
}

main();