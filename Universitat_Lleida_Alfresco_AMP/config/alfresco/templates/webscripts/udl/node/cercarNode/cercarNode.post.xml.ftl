<?xml version="1.0" encoding="UTF-8"?>

<response>
	<service>cercarNode</service>
	<username>${username}</username>
	<success>${success}</success>
	<#if error?exists>   
		<error>${error}</error>
	</#if>
	<nodes>
	    <#if nodes?exists>
			<#list nodes as node>				
				<node>				
					<name>${node.name}</name>
					<id>${node.id}</id>					
					<path>${node.displayPath}</path>
					<tipus>${node.type}</tipus>								
				</node>
			</#list>
		<#else>
			No s'ha trobat cap node amb els criteris de cerca indicats.
		</#if>
	</nodes>
</response>