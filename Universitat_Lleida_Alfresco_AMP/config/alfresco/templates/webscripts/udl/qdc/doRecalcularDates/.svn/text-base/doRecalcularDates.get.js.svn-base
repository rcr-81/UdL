function main()
{
	impersonate.impersonate('admin');
	var expedientsAgregacions = search.luceneSearch('ASPECT:"{http://www.smile.com/model/udlrm/1.0}expedient" OR ASPECT:"{http://www.smile.com/model/udlrm/1.0}agregacio"');
	for (i = 0; i < expedientsAgregacions.length; i++){
		dateHelper.runRecalcularDates(expedientsAgregacions[i].nodeRef);
	}	
}

main();