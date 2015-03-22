<?xml version="1.0" encoding="UTF-8"?>

<response>
	<service>test</service>
	<#if error?exists>
		<error>${error}</error>
	<#else>
		<success>${success}</success>	
	</#if>
</response>