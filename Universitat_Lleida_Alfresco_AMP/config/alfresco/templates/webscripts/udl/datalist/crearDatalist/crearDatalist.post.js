<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">

var success = false;
var datalistFolder = null;
var siteFolder = roothome.childByNamePath(arguments.sitePath);
if (siteFolder == undefined || !siteFolder.isContainer) {
	model.error = "Site path '" + arguments.sitePath + "' not found.";
}
else {
	try {
		var datalistFolder = siteFolder.childByNamePath("dataLists");
		if (!datalistFolder){
			var datalistFolder = siteFolder.createNode("dataLists", "cm:folder");
			var dataListProps = new Array(1);
			dataListProps["st:componentId"] = "dataLists";
			datalistFolder.addAspect("st:siteContainer", dataListProps);
			datalistFolder.save();
		}

		var datalist = datalistFolder.childByNamePath(arguments.datalistName);
		if (!datalist){
			var datalist = datalistFolder.createNode(arguments.datalistName, "dl:dataList");
			// tells Share which type of items to create
			datalist.properties["dl:dataListItemType"] = String(arguments.datalistType);
			datalist.save();

			var datalistProps = [];
			datalistProps["cm:title"] = String(arguments.datalistTitle);
			datalistProps["cm:description"] = String(arguments.datalistDescription);
			datalist.addAspect("cm:titled", datalistProps);
		}		
		success = true;
	}
	catch(e){
		model.error = handleError(e);
	}
}

model.datalist = datalist;
model.success = String(success);