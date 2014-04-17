<?xml version="1.0" encoding="UTF-8"?>

<response>
	<service>esborrarDocument</service>
	<username>${username}</username>
	<success>${success}</success>
	<#if error?exists>   
		<error>${error}</error>
	</#if>  
	<document>
		<#if doc?exists>   
			<name>${doc.name}</name>
			<id>${doc.id}</id>			
			<path>${doc.displayPath}</path>
			<tipus>${doc.type}</tipus>
		</#if>
	</document>	 
</response>