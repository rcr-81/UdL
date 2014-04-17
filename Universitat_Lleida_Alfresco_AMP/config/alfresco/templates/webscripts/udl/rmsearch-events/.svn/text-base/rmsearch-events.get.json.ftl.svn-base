<#macro dateFormat date>${date?string("dd/MM/yyyy")}</#macro>
<#escape x as jsonUtils.encodeJSONString(x)>
{
   "items":
   [<#list itemList.items as item>
      { "nodeRef": "${item.nodeRef}", 
        "displayName": "${item.displayName}", 
        "type": "${item.type}" , 
        "dateEvent": "<@dateFormat item.dateEvent />"}<#if (item_has_next)>,</#if> 
   </#list>]
}
</#escape>