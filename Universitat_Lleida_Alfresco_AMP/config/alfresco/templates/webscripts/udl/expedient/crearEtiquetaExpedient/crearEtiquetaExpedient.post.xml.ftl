<?xml version="1.0" encoding="UTF-8"?>

<#if descGrupCreador?exists>${descGrupCreador}</#if>&nbsp;(<#if idGrupCreador?exists>${idGrupCreador}</#if>)

<br>
<br> 
<span style="margin-left:350px;"> 
	ANY: <b>${any}</b>
</span>
<br>
<span style="margin-left:350px;">
	VOLUM: <#if quantitat?exists><b>${quantitat}</b></#if>
</span>

<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>

<div style="width:450px;margin-left:25px;">
	<#if codiClassificacio1?exists && codiClassificacio1 != ''>${codiClassificacio1}</#if>
	<#if denominacioClasse1?exists && denominacioClasse1 != ''>- ${denominacioClasse1}</#if>
</div>

<div style="width:450px;margin-left:25px;">
	<#if nomNaturalAgent?exists>${nomNaturalAgent}</#if>
</div>

<div style="width:450px;margin-left:25px;"> 
	<#if dataInici?exists>${dataInici}</#if>
	<#if dataFi?exists>	- ${dataFi}</#if>
</div>

<div style="width:450px;margin-left:25px;">
	<#if nomNatural?exists>${nomNatural}</#if>
</div>

<div style="width:450px;margin-left:25px;">
	<#if codiClassificacio2?exists && codiClassificacio2 != ''>${codiClassificacio2}</#if>
	<#if denominacioClasse2?exists && denominacioClasse2 != ''>- ${denominacioClasse2}</#if>
</div>

<br>
<br> 
<br>
<br>

<div style="width:425px;margin-left:50px;">Núm. expedient:  
	<#if id?exists>${id}</#if>
</div>

<div style="width:425px;margin-left:50px;">Unitats instal·lació: 
	<#if localitzacio1?exists && localitzacio1 != ''>${localitzacio1}</#if>
	<#if localitzacio2?exists && localitzacio2 != ''>, ${localitzacio2}</#if>
</div>

<div style="width:425px;margin-left:50px;">
	<#if essencial?exists && essencial == true><b>ESSENCIAL</b></#if>
</div>