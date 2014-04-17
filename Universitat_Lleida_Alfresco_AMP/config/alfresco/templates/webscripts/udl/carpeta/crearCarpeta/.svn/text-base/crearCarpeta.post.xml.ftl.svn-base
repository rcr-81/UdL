<?xml version="1.0" encoding="UTF-8"?>

<response>
	<service>crearCarpeta</service>
	<username>${username}</username>
	<success>${success}</success>
	<#if error?exists>
		<error>${error}</error>
	</#if>
	<#if carpeta?exists>
		<carpeta>
			<nom>${carpeta.name}</nom>
			<id>${carpeta.id}</id>			
			<path>${carpeta.displayPath}</path>
			<tipus>${carpeta.type}</tipus>
			<metadades>
				<#list mapMetadatos?keys as nombreMetadato>
					<${nombreMetadato}>${mapMetadatos[nombreMetadato]}</${nombreMetadato}>						
				</#list>
			</metadades>
		</carpeta>
	</#if>
</response>