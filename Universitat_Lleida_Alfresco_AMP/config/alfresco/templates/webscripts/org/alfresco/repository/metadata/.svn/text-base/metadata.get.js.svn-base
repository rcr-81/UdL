<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

/**
 * Node Metadata Retrieval Service GET method
 */
function main()
{
   var json = "{}";
   
   // allow for content to be loaded from id
   if (args["nodeRef"] != null)
   {
   	var nodeRef = args["nodeRef"];
   	node = search.findNode(nodeRef);
   	
   	if (node != null)
   	{
   	   // if the node was found get JSON representation
   		if (args["shortQNames"] != null)
   		{
   			json = node.toJSON(true);
   		}
   		else
   		{
   			json = node.toJSON();
   		}

   		// AUDIT
   		var type = node.getTypeShort();
   		var site = node.getSiteShortName();   		
		if(type == "udl:expedient") {
		    audit("url_audit_view_expedient", nodeRef, type, site, null);	
		}
		else if(type == "udl:agregacio") {
		    audit("url_audit_view_agregacio", nodeRef, type, site, null);	
		}
		else if(type == "udl:serie") {
		    audit("url_audit_view_serie", nodeRef, type, site, null);	
		}
		else if(type == "udl:fons") {
		    audit("url_audit_view_fons", nodeRef, type, site, null);	
		}
		else if(type == "udl:grupDeFons") {		
		    audit("url_audit_view_grup_de_fons", nodeRef, type, site, null);
		}
		if(type == "udl:documentSimple") {
		    audit("url_audit_view_document_simple", nodeRef, type, site, null);			
		}			
     }   	
  }
   
  // store node onto model
  model.json = json;
}

main();