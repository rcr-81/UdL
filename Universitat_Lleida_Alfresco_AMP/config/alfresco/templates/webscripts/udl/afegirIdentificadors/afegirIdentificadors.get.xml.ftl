<?xml version="1.0" encoding="UTF-8"?>

<response>
	<service>afegirIdentificadors</service>
	<#if error?exists>
		<error>${error}</error>
	<#else>
		<success>		
			<fons>${successFons}</fons>
			<serie>${successSerie}</serie>	
			<expedient>${successExpedient}</expedient>	
			<agregacio>${successAgregacio}</agregacio>	
			<documentSimple>${successDocumentSimple}</documentSimple>
		</success>	
	</#if>
</response>