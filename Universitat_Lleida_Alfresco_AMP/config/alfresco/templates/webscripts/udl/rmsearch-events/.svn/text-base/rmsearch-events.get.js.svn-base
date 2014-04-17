function customRMSearch(){

   var items = new Array();
   
   //logger.log("Entrando en customRMSearch!");
   
   try{

	  var searchPendingFolders = '+PATH:"/app:company_home/st:sites/cm:rm/cm:documentLibrary/*/*/*/rma:nextDispositionAction"';
	  var searchDestroy = '+(@rma\\:dispositionAction:destroy)';
	  var searchTransfer = '+(@rma\\:dispositionAction:transfer)';
      var searchTime = '+(@rma\\:dispositionAsOf:[MIN TO NOW])';
      
      //Folders to destroy
      var foldersToDestroy = search.luceneSearch(searchPendingFolders + " " + searchDestroy + " " + searchTime);
      for each (item in foldersToDestroy)
      {    	 
	       	var node = item.getParent(); 
	       	
	       	// Se itera sobre la lista de expedientes congelados para determinar si el expediente a destruir está congelado.
	       	// Si NO está congelado se añade a la lista items para que aparezcan en el dashlets de esdeveniments pendents
	       	var holdFolders = search.luceneSearch('@rma\\:recordSearchHoldReason:*');
	       	var addItem = true;
	       	
	       	for each(hold in holdFolders) {
	       		if(hold.properties["sys:node-uuid"] == node.properties["sys:node-uuid"]) {
	       			addItem = false;
	       		}
	       	}
	       	
	       	if(addItem) {
		         items.push(
		         {
		            nodeRef: node.getNodeRef().toString(),
		            displayName: node.name,            
		            type: "folder.destroy",
		            dateEvent: item.properties["rma:dispositionAsOf"]
		         });
	       	}
      }

      //Folders to transfer
      var foldersToTransfer = search.luceneSearch(searchPendingFolders + " " + searchTransfer + " " + searchTime);
      for each (item in foldersToTransfer)
      {
       	 var node = item.getParent(); 
       	
	       	// Se itera sobre la lista de expedientes congelados para determinar si el expediente a transferir está congelado.
	       	// Si NO está congelado se añade a la lista items para que aparezcan en el dashlets de esdeveniments pendents
	       	var holdFolders = search.luceneSearch('@rma\\:recordSearchHoldReason:*');
	       	var addItem = true;
	       	
	       	for each(hold in holdFolders) {
	       		if(hold.properties["sys:node-uuid"] == node.properties["sys:node-uuid"]) {
	       			addItem = false;
	       		}
	       	}
	       	
	       	if(addItem) {
		         items.push(
		         {
		            nodeRef: node.getNodeRef().toString(),
		            displayName: node.name,            
		            type: "folder.transfer",
		            dateEvent: item.properties["rma:dispositionAsOf"]
		         });
	       	}
      }
	   
   }catch(e){
      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, e.toString());
      return;
   }

   items.sort(sortByDate);
   
   return ({
       items: items
   });
	
}

/* Sort the results by case-insensitive name */
function sortByDate(a, b)
{
   return (b.dateEvent > a.dateEvent ? -1 : 1);
}

// locate results

model.itemList = customRMSearch();