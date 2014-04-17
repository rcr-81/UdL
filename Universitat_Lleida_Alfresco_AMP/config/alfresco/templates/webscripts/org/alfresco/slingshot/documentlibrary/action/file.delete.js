<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/action/action.lib.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/audit/callAudit.js">

/**
 * Delete file action
 * @method DELETE
 * @param uri {string} /{siteId}/{containerId}/{filepath}
 */

/**
 * Entrypoint required by action.lib.js
 *
 * @method runAction
 * @param p_params {object} standard action parameters: nodeRef, siteId, containerId, path
 * @return {object|null} object representation of action result
 */
function runAction(p_params)
{
   var results;
   
   try
   {
      var assetNode = p_params.destNode,
         resultId = assetNode.name,
         resultNodeRef = assetNode.nodeRef.toString();
      var node = search.findNode(resultNodeRef);
      var type = node.getTypeShort();
      var site = node.getSiteShortName();
      var isFolder = node.isSubType("cm:folder");

      // Delete the asset
      if (!assetNode.remove())
      {
         status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Could not delete.");
         return;
      }

      // Construct the result object
      results = [
      {
         id: resultId,
         nodeRef: resultNodeRef,
         action: "deleteFile",
         success: true
      }];
      
      // *********** SMILE **************************/
      // Generacion de registros de auditoria para la eliminacion de documentos      	
      if(type == "udl:documentSimple") {
	    audit("url_audit_delete_document_simple", resultNodeRef, type, site, null);			
	  }
      else if(type == "udl:expedient") {
	    audit("url_audit_delete_expedient", resultNodeRef, type, site, null);	
	  }
      else if(type == "udl:agregacio") {
	    audit("url_audit_delete_agregacio", resultNodeRef, type, site, null);	
	  }
      else if(type == "udl:serie") {
	    audit("url_audit_delete_serie", resultNodeRef, type, site, null);	
	  }
      else if(type == "udl:fons") {
		audit("url_audit_delete_fons", resultNodeRef, type, site, null);	
	  }
      else if(type == "udl:grupDeFons") {		
		audit("url_audit_delete_grup_de_fons", resultNodeRef, type, site, null);
	  }
   }
   catch(e){
      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, e.toString());
      return;
   }
   
   return results;
}

/* Bootstrap action script */
main();