 <#assign username = "/udl/audit/userName/value">
 <#assign noderef = "/udl/audit/noderef/value">
 <#assign action = "/udl/audit/action/value">
 <#assign type = "/udl/audit/type/value">
 <#assign site = "/udl/audit/site/value">
   Resultados:${count?c}
   [
      <#list entries as entry>
         <#if entry.values??>
  	UserName=${entry.values[username]};Action=${entry.values[action]};DocumentType=${entry.values[type]};Site=${entry.values[site]};NodeRef=${entry.values[noderef]};Hora=${entry.time?string("dd-MM-yyyy HH:mm:ss")}
  		 </#if>
      </#list>
   ]

