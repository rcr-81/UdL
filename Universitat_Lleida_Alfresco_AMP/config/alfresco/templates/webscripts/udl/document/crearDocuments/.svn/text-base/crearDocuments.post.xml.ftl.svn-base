<?xml version="1.0" encoding="UTF-8"?>

<response>
	<service>crearDocuments</service>
	<username>${username}</username>
	<#if error?exists>   
		<error>${error}</error>
	</#if>		
	<#if infoList?exists>  				
		<#list infoList as info>	
			<document>				
				<success>${info.success}</success>	
				<#if info.error?exists>   
					<error>${info.error}</error>
				</#if>
				<#if info.document?exists>   
					<id>${info.document.id}</id>
					<name>${info.document.name}</name>
					<path>${info.document.displayPath}</path>		
					<tipus>${info.document.type}</tipus>			
					<metadades>										
						<estat>${info.document.properties['udl:estat']}</estat>									
						<destiTransferencia>${info.document.properties['udl:desti_transferencia']}</destiTransferencia>
					</metadades>					
				</#if>					
			</document>
		</#list>
	</#if>
</response>