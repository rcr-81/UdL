/**
 * Main entry point for component webscript logic
 *
 * @method main
 */
function main()
{
	var userName = person.properties.userName;
	var action = args.action;
	var nodeRef = args.nodeRef;
	var node = search.findNode(nodeRef);
	// Crida al webscript java backed que s'encarrega d'afegir informació d'auditoria.
	XMLHttpRequest.open("GET", udlProperties.getProperty("url_audit") +"?action=" + action + "&userName=" + userName +"&nodeRef=" + nodeRef+"&type="+node.getTypeShort()+"&site="+node.getSiteShortName(),false, udlProperties.getProperty("admin_username"), udlProperties.getProperty("admin_password"));
    XMLHttpRequest.send("");
	var xmlhttpresult = XMLHttpRequest.getResponseText();
                        
    XMLHttpRequest.close();
}

// Start the webscript
main();