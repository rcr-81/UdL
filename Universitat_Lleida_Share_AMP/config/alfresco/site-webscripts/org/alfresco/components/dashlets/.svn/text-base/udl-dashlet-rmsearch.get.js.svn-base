
// Get the Documents Modified data for this site
var url = "/udl/rmsearch-events";

logger.log("Entrando en dill-dashlet");

var json = remote.call(url);
if (json.status == 200)
{	

   // Create the model
   var docs = eval('(' + json + ')');
 
   logger.log(docs);
   
   model.docs = docs;
}
else
{
   model.docs = {message: json.message};
}

