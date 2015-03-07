<?xml version="1.0" encoding="UTF-8"?>

<result>
	<success>${success}</success>
	
	<#if expedient.properties["udlrm:secuencial_identificador_expedient"]?exists>
		<localitzacio_1_expedient>${expedient.properties["udlrm:localitzacio_1_expedient"]}</localitzacio_1_expedient>
		<localitzacio_2_expedient>${expedient.properties["udlrm:localitzacio_2_expedient"]}</localitzacio_2_expedient>
	</#if>
</result>