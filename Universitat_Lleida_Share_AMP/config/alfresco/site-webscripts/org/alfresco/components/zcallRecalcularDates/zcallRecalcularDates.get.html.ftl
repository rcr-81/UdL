<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
	<br/>
	<h2>Recalcular dates</h2>
	<br/>
	<#if successMsg??>
		${successMsg}
	<#else>
		${errorMsg}
	</#if>
</#if>	