<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/arguments.js">
<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/errorHandler.js">

var success = false;
var nodeID = String(arguments.nodeID);
var nodeName = String(arguments.nodeName);
var nodeContent = String(arguments.nodeContent);
var query = "";
var hasPrevious = false;

if(nodeID != ""){
	nodeID = "workspace://SpacesStore/" + nodeID;
	query = 'ID:"' + nodeID + '"';
	hasPrevious = true;
}

if(nodeName != ""){
	if(hasPrevious == true){
		query = query + " AND ";
	}
	if(nodeName.indexOf(".") > 0){
		nodeName = nodeName.split(".")[0];
	}
	query = query + '@cm\\:name:"' + nodeName + '*"';
	hasPrevious = true;
}

if(nodeContent != ""){
	if(hasPrevious == true){
		query = query + " AND ";
	}	
	query = query + 'TEXT:"' + nodeContent + '"';
	hasPrevious = true;
}

for each(metadada in arguments.metadades.metadada){	
	if(hasPrevious == true){
		query = query + " AND ";
	}
	query = query + "@udl\\:" + metadada.name + ":" + metadada.value;
	hasPrevious = true;
}

try {
	var nodes = search.luceneSearch(query);
	success = true;
}
catch(e){
	model.error = handleError(e);
}

model.success = String(success);
if(nodes != undefined && nodes.length > 0){
	model.nodes = nodes;
}