<?xml version="1.0" encoding="UTF-8"?>

<response>
	<service>esborrarDocuments</service>
	<username>${username}</username>	
	<#if removeInfo?exists>
		<#list removeInfo as info>				
			<document>				
				<success>${info.success}</success>
				<#if info.error?exists>   
					<error>${info.error}</error>
				</#if>
				<#if info.id?exists>   
					<name>${info.name}</name>
					<id>${info.id}</id>					
					<path>${info.displayPath}</path>
					<tipus>${info.type}</tipus>
				</#if>				
			</document>
		</#list>
	</#if>
</response>