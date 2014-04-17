<?xml version="1.0" encoding="UTF-8"?>

<response>
	<service>crearDocument</service>
	<username>${username}</username>	
	<success>${success}</success>	
	<#if error?exists>   
		<error>${error}</error>
	</#if>
	<#if document?exists>
		<document>		   
			<name>${document.name}</name>
			<id>${document.id}</id>
			<path>${document.displayPath}</path>
			<tipus>${document.type}</tipus>			
			<#--
			<metadades>
				<estat>${document.properties['udl:estat']}</estat>									
				<destiTransferencia>${document.properties['udl:desti_transferencia']}</destiTransferencia>
			</metadades>
			-->
		</document>
	</#if>	
</response>