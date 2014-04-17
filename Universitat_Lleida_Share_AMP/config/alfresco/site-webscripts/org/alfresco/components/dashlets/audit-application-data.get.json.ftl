 <#assign username = "/udl/audit/userName/value">
 <#assign noderef = "/udl/audit/noderef/value">
 <#assign action = "/udl/audit/action/value">
 <#assign site = "/udl/audit/site/value">
 <#assign type = "/udl/audit/type/value">
 <#assign noderef = "/udl/audit/noderef/value">
{
    "count": "${auditresponse.count}",
    "entries":
    [
    <#list auditresponse.entries as e>
	{
	    "id": "${e.id}",
	    "application": "${e.application}",
	    "user": "${e.user}",
	    "time": "${e.time}",
	    "values":
	    {
		  "UserName":"${e.values[username]}","Action":"${e.values[action]}","DocumentType":"${e.values[type]}","Site":"${e.values[site]}","NodeRef":"${e.values[noderef]}"
	    }
	}<#if e_has_next>,</#if>
    </#list>
    ]
}

