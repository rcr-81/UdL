<?xml version="1.0" encoding="UTF-8"?>

<result>
	<#if nodes?exists>
		<exists>true</exists>
		<#if (nodes?size > 0)>
			<success>true</success>
			<#list nodes as node>				
				<nodePath>${node.displayPath}/${node.name}</nodePath>
			</#list>
		<#else>
			<success>false</success>
		</#if>
	<#else>
		<exists>false</exists>
		<success>false</success>
	</#if>
</result>