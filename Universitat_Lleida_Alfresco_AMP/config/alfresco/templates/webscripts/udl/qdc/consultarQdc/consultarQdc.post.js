<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">

//user must be member of the "Records Management Administrator" group.
var success = false;
var folderQdC = "Company Home/Sites/rm/documentLibrary";
var folder = roothome.childByNamePath(folderQdC);
if (folder == undefined || !folder.isContainer){
   model.error = "Folder '" + folderQdC + "' not found.";
}
else {
	success = true;
}

model.folder = folder;
model.success = String(success);