var json = remote.call("/signatures/signatures-list?nodeRef=" + args.nodeRef);
if (json.status == 200){
	model.signatures = eval('(' + json + ')');
	//cuidado: si viene vacio peta el ftl
}
