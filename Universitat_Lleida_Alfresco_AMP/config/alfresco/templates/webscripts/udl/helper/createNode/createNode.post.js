<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">

var node = null;
var name = null;
var path = null;
var type = null;

for each (field in formdata.fields){
  if (field.name == "nodePath"){
    path = field.value;
  }
  else if (field.name == "nodeName"){
    name = field.value;
  }  
  else if (field.name == "nodeType"){
	type = field.value;
  }  
}

var parentFolder = roothome.childByNamePath(path);
if (parentFolder == undefined || !parentFolder.isContainer) {
	status.code = 500;
	status.message = "Folder '" + path + "' not found.";
	status.redirect = true;	
}
else {	
	try {
		node = parentFolder.createNode(name, type);
		
	}
	catch(e){
	   status.code = 500;
	   status.message = handleError(e);
	   status.redirect = true;
	}
	
	model.node = node;
}