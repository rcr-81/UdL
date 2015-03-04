<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/console/rm-console.lib.js">

function main()
{
	var meta = [];
	var conn = remote.connect("alfresco");
	var hasAccess = hasCapability(conn, "AccessAudit");

	model.hasAccess = hasAccess;
	
	if (hasAccess) {
		var result = remote.call("/udl/qdc/copiarQdC");
		
		if (result.status == 200) {
			model.successMsg = result;

		}else if (result.status == 408) {
			model.successMsg = 'Temps d\'espera superat, el procés continuarà executant-se en un segon pla.';

		}else {
			model.errorMsg = 'ERROR a l\'intentar sincronitzar els quadres de classificació.';
		}
	}
}

main();