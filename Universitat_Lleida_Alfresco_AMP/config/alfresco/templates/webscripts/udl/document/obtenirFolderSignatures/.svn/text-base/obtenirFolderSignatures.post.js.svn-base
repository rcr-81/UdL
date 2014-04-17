<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">

var username = null;
var path = null;
var folderName = null;

for each (field in formdata.fields){
  if (field.name == "username"){
    username = field.value;
  }
  else if (field.name == "docPath"){
    path = field.value;
  }	
  else if (field.name == "docName"){
	folderName = field.value;
  }	
}	

impersonate.impersonate(username);

var folder = roothome.childByNamePath(path + "/" + folderName);
if (folder == undefined || !folder.isContainer) {
	//folder not found, create
	var parentFolder = roothome.childByNamePath(path);
	if (parentFolder == undefined || !parentFolder.isContainer) {
		status.code = 500;
		status.message = "Folder " + parentFolder + " not found.";
		status.redirect = true;
	}
	
	try {
		folder = parentFolder.createFolder(folderName);	
		//falta crear les metadades y associar els aspectes que siguin necessaris
	}
	catch(e){
		status.code = 500;
		status.message = handleError(e);
		status.redirect = true;
	}
}

model.folder = folder;