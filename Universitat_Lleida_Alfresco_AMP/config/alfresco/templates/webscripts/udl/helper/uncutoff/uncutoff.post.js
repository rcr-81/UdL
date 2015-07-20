<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">

var username = null;
var nodeRef = null;
var server = null;
var admin_username = null;
var admin_password = null;

for each (field in formdata.fields){
	if (field.name == "username"){
		username = field.value;
	}
	else if (field.name == "nodeRef"){
		nodeRef = field.value;
	}
	else if (field.name == "server"){
		server = field.value;
	}
	else if (field.name == "admin_username"){
		admin_username = field.value;
	}
	else if (field.name == "admin_password"){
		admin_password = field.value;
	}
}	

impersonate.impersonate(username);

var expedient = search.findNode(nodeRef);
if (expedient == undefined || !expedient.isContainer) {
	status.code = 500;
	status.message = "Expedient at '" + pathExpedient + "' not found.";
	status.redirect = true;
}
else {	
	try {
		//before to run this, cutoff action must be added to the category where is placed the folder (expediente) 
		//and all the document contained in the folder must be declared as a record.
		var url = server + "/share/proxy/alfresco/api/rma/actions/ExecutionQueue";
		var params = '{"name":"uncutoff","nodeRef":"'+ nodeRef +'"}';
		XMLHttpRequest.open("POST", url, false, admin_username, admin_password);
		XMLHttpRequest.setRequestHeader("Content-type", "application/json");
		XMLHttpRequest.send(params);	
		XMLHttpRequest.close();
		model.node = expedient;
	}
	catch(e){
		status.code = 500;
		status.message = handleError(e);
		status.redirect = true;
	}		
}