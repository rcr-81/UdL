<?xml version="1.0" encoding="UTF-8"?>

<response>
	<service>esborrarCarpeta</service>
	<username>${username}</username>
	<success>${success}</success>
	<#if error?exists>   
		<error>${error}</error>
	</#if>  
	<carpeta>
		<#if carpeta?exists>   
			<name>${carpeta.name}</name>
			<id>${carpeta.id}</id>			
			<path>${carpeta.displayPath}</path>
			<tipus>${carpeta.type}</tipus>
		</#if>
	</carpeta>	 
</response>