//retrieve arguments
var sArguments = null
for each (field in formdata.fields){
	  if (field.name == "arguments"){
	    sArguments = field.value;
	  }
}
var arguments = new XML(sArguments);

//impersonate
impersonate.impersonate(arguments.username);
model.username = String(arguments.username);