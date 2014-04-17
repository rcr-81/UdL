<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">

var success = false;
var datalistFolder = null;
var datalist = roothome.childByNamePath(arguments.datalistPath);
if (datalist == undefined || !datalist.isContainer) {
	model.error = "Datalist '" + arguments.datalistName + "' not found.";
}
else {
	try {				
		for each(xmlElem in arguments.elems.elem){
			var el = datalist.createNode(null, arguments.datalistType);
			xmlElem = new XML(xmlElem);
			for each(md in xmlElem.metadata){	
				el.properties[md.name] = String(md.value);			
			}		
			el.save();			
		}			
		success = true;
	}
	catch(e){
		model.error = handleError(e);
	}
}

model.datalist = datalist;
model.success = String(success);