<?xml version="1.0" encoding="UTF-8"?>

<response>
	<service>checkinDocument</service>
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
			<downloadUrl>${document.downloadUrl}</downloadUrl>
			<tipus>${document.type}</tipus>
			<metadades>				
				<#list mapMetadatos?keys as nombreMetadato>
					<${nombreMetadato}>${mapMetadatos[nombreMetadato]}</${nombreMetadato}>						
				</#list>
			</metadades>
		</document>			
	</#if>   
</response>