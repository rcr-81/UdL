<?xml version="1.0" encoding="UTF-8"?>

<response>
	<service>transferirExpedient</service>
	<username>${username}</username>
	<success>${success}</success>
	<#if error?exists>
		<error>${error}</error>
	</#if>
	<#if expedient?exists>
		<expedient>
			<nom>${expedient.name}</nom>
			<id>${expedient.id}</id>			
			<path>${expedient.displayPath}</path>		
			<tipus>${expedient.type}</tipus>	
		</expedient>
	</#if>
</response>