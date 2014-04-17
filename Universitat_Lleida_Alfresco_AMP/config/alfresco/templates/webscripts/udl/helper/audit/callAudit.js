function audit(url,noderef,type,site, username){
	if(username == null)username = person.properties.userName;
	
    XMLHttpRequest.open("GET", udlProperties.getProperty(url) +"&nodeRef=" + noderef+"&userName="+username+"&type="+type+"&site="+site,false,"","");
    XMLHttpRequest.send("");
	var xmlhttpresult = XMLHttpRequest.getResponseText();                
    XMLHttpRequest.close();
}