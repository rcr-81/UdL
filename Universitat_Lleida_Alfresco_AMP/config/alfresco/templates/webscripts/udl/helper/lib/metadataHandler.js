<import resource="classpath:alfresco/templates/webscripts/udl/helper/lib/constantsUdL.js">

// Retorna un map con los metadatos del nodo. Clave = nombre del metadato, Valor = valor del metadato.
function getMetadatos(node) {

	var map = {};
	
	if(node != null) {
		for(var propName in node.properties) {
			if(node.properties[propName] != null) {
				// Esta validación es necesaria para la consulta de documento
				if(node.properties[propName].isDocument != true) {
					var type = typeof(node.properties[propName]);
					var newPropName = propName.substr(propName.lastIndexOf("}") + 1)
					
					if(type == "string") {
						map[newPropName] = "<![CDATA[" + node.properties[propName] + "]]>";	
				
					}else {
						map[newPropName] = "<![CDATA[" + String(node.properties[propName]) + "]]>";
					}
				}
			}
		}
	}
	
	return map;
}

// Setea en node los argumentos recibidos que perteneces al esquema de metadatos própio de la UdL
function setMetadatos(node, arguments) {
	var auxArgs = arguments;
	
	if(node != null) {
		for(var propName in node.properties) {
			if(node.properties[propName] != null) {
				// Se verifica que el valor del metadato no sea un scriptnode
				if(node.properties[propName].isDocument != true) {

					// Solo se sobreescriben los metadatos propios de la UdL
					if(propName.indexOf(namespaceUDL) != -1) {
						// newPropName no tiene el namespace
						var newPropName = propName.substr(propName.lastIndexOf("}") + 1)
						node.properties[propName] = String(arguments.metadades[newPropName]);
						
						delete auxArgs.metadades[newPropName];
					}
				}
			}
		}
		
		// En este bucle se acaban de setear las propiedades que faltan.
		// Hay que hacer esto porque "node.properties" no retorna todos los metadatos del expediente
		var i=0;

		while(i < auxArgs.metadades.*.length()) {
			if(String(auxArgs.metadades.*[i].name()).indexOf("data_") != -1) {
				node.properties["udl:" + String(auxArgs.metadades.*[i].name())] = dateHelper.parse(auxArgs.metadades.*[i]);

			}else {
				node.properties["udl:" + String(auxArgs.metadades.*[i].name())] = String(auxArgs.metadades.*[i]);	
			}

			i++;
		}
	}
	
	node.save();
}

// Copia los metadatos (udl, descripción y título) desde el nodeDest al nodeOrigen
function copyMetadatos(nodeOrigen, nodeDest) {
	if(nodeOrigen != null && nodeDest != null) {
		for(var propNameUDLRM in nodeOrigen.properties) {
			propNameUDL = propNameUDLRM.replace(namespaceUDLRM, namespaceUDL);
			if(nodeOrigen.properties[propNameUDLRM] != null) {
				// Se verifica que el valor del metadato no sea un scriptnode
				if(nodeOrigen.properties[propNameUDLRM].isDocument != true) {
					// Solo se sobreescriben los metadatos propios de la UdL
					if(propNameUDLRM.indexOf(namespaceUDLRM) != -1) {
						nodeDest.properties[propNameUDL] = nodeOrigen.properties[propNameUDLRM];
					}
				}
			}
		}
		
		if(nodeOrigen.properties["cm:description"] != null) {
			nodeDest.properties["cm:description"] = nodeOrigen.properties["cm:description"];

		}else if(nodeOrigen.properties["cm:title"] != null) {
			nodeDest.properties["cm:title"] = nodeOrigen.properties["cm:title"];
		}
	}
}