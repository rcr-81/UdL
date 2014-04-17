<?xml version="1.0" encoding="UTF-8"?>

<result>
	<success>${success}</success>
	
	<#if carpeta.properties["udlrm:secuencial_identificador_expedient"]?exists>
		<id>${carpeta.properties["udlrm:secuencial_identificador_expedient"]}</id>
	</#if>
</result>