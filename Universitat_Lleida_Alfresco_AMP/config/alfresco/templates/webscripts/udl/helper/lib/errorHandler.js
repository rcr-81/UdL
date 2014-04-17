//error handler
function handleError(e, ws){
	var errorDescription = "";
	if(e.javaException != undefined){
		errorDescription = e.javaException.getMessage();
	}
	if(errorDescription == undefined || errorDescription == ''){
		errorDescription  = String(e);
	}	
	logger.log(ws + ": " + errorDescription);
	return errorDescription;
}