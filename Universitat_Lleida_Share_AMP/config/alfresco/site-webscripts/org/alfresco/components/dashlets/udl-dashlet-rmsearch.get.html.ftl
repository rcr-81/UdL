<#macro doclibUrl doc>
   <#if doc.type == "register.destroy" || doc.type == "register.transfer" >
   	   <a href="${url.context}/page/site/rm/document-details?nodeRef=${doc.nodeRef}" class="theme-color-1">${doc.displayName?html}</a>
   <#else>	      	
   	   <a href="${url.context}/page/site/rm/record-folder-details?nodeRef=${doc.nodeRef}" class="theme-color-1">${doc.displayName?html}</a>
   </#if> 
</#macro>
<script type="text/javascript">//<![CDATA[
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>
<div class="dashlet">
   <div class="title">${msg("header.docSummary")}</div>
   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
   
      <#if docs.items?size == 0>
      <div class="detail-list-item first-item last-item">
         <span>${msg("label.noItems")}</span>
      </div>
      <#else>
         <#list docs.items as doc>
      <div class="detail-list-item <#if doc_index = 0>first-item<#elseif !doc_has_next>last-item</#if>">
         <div>
            <div class="icon">
               <img src="${url.context}/res/components/images/generic-file-32.png" alt="${doc.displayName?html}" />
            </div>
            <div class="details">
               <h4><@doclibUrl doc /></h4>
               <div>
                  ${msg(""+doc.type, doc.dateEvent)} 
               </div>
            </div>
         </div>
      </div>
         </#list>
      </#if>
   </div>
</div>