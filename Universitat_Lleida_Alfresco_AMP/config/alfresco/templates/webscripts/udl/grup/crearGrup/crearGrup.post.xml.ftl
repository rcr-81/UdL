<?xml version="1.0" encoding="UTF-8"?>

<response>
	<service>crearGrup</service>
	<username>${username}</username>
	<success>${success}</success>
	<#if error?exists>   
		<error>${error}</error>
	</#if>
	<#if grup?exists>   
		<grup>
			<name>${grup.name}</name>
			<id>${grup.id}</id>			
		</grup>			
	</#if>
</response>