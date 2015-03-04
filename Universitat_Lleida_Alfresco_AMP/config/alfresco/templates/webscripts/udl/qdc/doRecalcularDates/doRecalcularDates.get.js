function main()
{
	impersonate.impersonate('admin');
	dateHelper.runRecalcularTotesDates();
}

main();