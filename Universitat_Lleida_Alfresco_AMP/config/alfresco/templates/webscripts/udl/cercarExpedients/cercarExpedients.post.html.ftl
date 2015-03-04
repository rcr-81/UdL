<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<link rel="stylesheet" type="text/css" href="/share/css/jquery.dataTables.css">
  		<link type="text/css" href="/share/css/osx.css" rel="stylesheet" media="screen" />
  		
		<script type="text/javascript" charset="utf8" src="/share/js/jquery-1.10.2.min.js"></script>
		<script type="text/javascript" charset="utf8" src="/share/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="/share/js/jquery.simplemodal.js"></script>
		<script type="text/javascript" src="/share/js/date-eu.js"></script>
		<script type="text/javascript" src="/share/js/osx.js"></script>		
	
		<script type="text/javascript">	
		
			function getNewSubmitForm(action){
				 var submitForm = document.createElement("form");
				 submitForm.style.display = "none";
				 submitForm.method = "POST";
				 submitForm.action = action;
				 submitForm.enctype="multipart/form-data";				 
				 document.body.appendChild(submitForm);
				 return submitForm;
			}

			function createNewFormElement(inputForm, elementName, elementValue){
				 var newElement = document.createElement("input");
				 newElement.setAttribute("type", "text");
				 newElement.setAttribute("name", elementName);
				 newElement.setAttribute("value", elementValue);
				 inputForm.appendChild(newElement);				 
				 return newElement;
			}
			
			function cercarExpedients(){
				 var filtro = document.getElementById("filtro").value;
				 //var submitForm = getNewSubmitForm("http://localhost:8080/alfresco/service/cercarExpedients/cercarExpedients?filtro=" + filtro);
				 var submitForm = getNewSubmitForm(window.location.protocol + "//" + window.location.host + "/alfresco/service/cercarExpedients/cercarExpedients?filtro=" + filtro);				 
				 submitForm.submit();
			}

			function foco(elemento) {
				elemento.className = "textMarcado";
			}

			function no_foco(elemento) {
				elemento.className = "text";
			}	
			
			function initFiltro(filtro) {
				document.getElementById("filtro").value = filtro;
			}
			
			// Captura tecla INTRO i F5
			document.onkeypress = function(event) {
				var tecla = (document.all)? window.event.keyCode : event.which;
				
				if (tecla == 13){
					cercarExpedients();
				}
			} 
				
		</script>
	
		<style>
			input.boton
			{
				vertical-align: top;
				background-color: #830051;
				background-image: -moz-linear-gradient(center top , #830051, #830051);
				border: 1px solid #830051;
				color: #FFFFFF !important;
				-moz-user-select: none;
				border-radius: 2px 2px 2px 2px;
				cursor: default !important;
				display: inline-block;
				font-weight: bold;
				height: 28px;
				line-height: 29px;
				min-width: 54px;
				padding: 0 8px;
				text-align: center;
				text-decoration: none !important;
			}	
	
			img.help
			{
				cursor: pointer;
				vertical-align: text-bottom;
			}
	
			div.titulo
			{
				text-align: center;
				margin-bottom: 3em;
			}
	
			div.descripcion
			{
				text-align: center;
				margin-bottom: 1em;
			}
	
			div.buscador
			{
				text-align: center;
				margin-bottom: 3em;
			}
	
			input.text
			{
				height: 24px;
				width: 550px;
				border: 1px solid #C0C4C6;
				margin-right: 10px;
			}
	
			input.textMarcado
			{
				height: 24px;
				width: 550px;
				border: 1px solid #830051;
				margin-right: 10px;
			}
		
			div.results
			{
				margin-left: 20px;
				font-size: 14px;
			}
			
			div.rowEven
			{
				height: 100px;
				margin-left: 17px;
    			margin-right: 17px;				
				font-size: 10px;
			}		

			div.rowOdd
			{
				height: 100px;
				margin-left: 17px;
    			margin-right: 17px;				
				font-size: 10px;
				background: #FCF3F9;
			}		

			div.headerRow
			{
			    font-size: 12px;
			    font-weight: bold;
			    height: 50px;
		        margin-left: 17px;
    			margin-right: 17px;
			    background: #F9E8F4;
			}
			
			div.columns
			{
				padding: 15px;		
			}

			div.headerColumns
			{
				padding: 12px;
				margin-left: 43px;	
			}

			div.image
			{
				float: left;
				margin-right: 15px;
				margin-left: 7px;
				margin-top: 10px;
			}
			
			div.column
			{
				float: left;
				margin-right: 7px;
				width: 170px;
			}

			div.columnOrgan
			{
				float: left;
				margin-right: 7px;
				width: 135px;
			}	

			div.columnCodi
			{
				float: left;
				margin-right: 7px;
				width: 50px;
			}
			
			div.columnId
			{
				float: left;
				width: 154px;
				margin-right: 7px;				
			}

			div.columnDate
			{
				float: left;
				width: 70px;
			}	

			div.columnLoc
			{
				float: left;
				width: 70px;
			}	

		</style>
	</head>
	
	<body onload="initFiltro('${filtro}');">
		<div class="titulo">
			<img src="/share/res/images/udl.png" />
		</div>
		<div class="descripcion">
			Arxiu UdL: cercador d'expedients
			&nbsp;&nbsp;<img class="help" id="helpIcon" src="/share/images/help.gif" title="Ajuda" />
		</div>
		<div class="buscador">
			<input type="text" id="filtro" name="filtro" class="text" onfocus="foco(this)" onblur="no_foco(this)"/>			
			<input type="button" value="Cercar" class="boton" onclick="javascript:cercarExpedients();" />
		</div>

		<br><br><br><br>
				
        <table id="table_id" class="display">
		    <thead>
		        <tr>
                	<th>Identificador</th>
                	<th>Grup creador</th>
                    <th>Data inici</th>
                    <th>Data fi</th>
                    <th>Nom natural</th>
                    <th>Òrgan</th>
                    <th>Institució</th>
                    <th>Codi class.</th>
                    <th>Localitzaci&#243;</th>
		        </tr>
		    </thead>
		</table>

		<!-- modal content -->
		<div id="osx-modal-content">
			<div id="osx-modal-title">Arxiu UdL: cercador d'expedients</div>
			<div class="close"><a href="#" class="simplemodal-close">x</a></div>
			<div id="osx-modal-data">
				<h2>Ajuda</h2>
				<p>Introduir els criteris de cerca i prémer sobre el botó "Cercar".</p>
				<p>El cercador permet utilitzar l'asterisc (*) com a comodí.</p>
				<p>Per a fer cerques per dates hi ha 2 opcions:
					<ul>
						<li>Cerca per data exacta: introduir una data amb el format "dd/mm/aaaa" o bé "dd-mm-aaaa". Exemple: 05/02/2014</li>
						<li>Cerca per rang de dates: introduir 2 dates entre claudàtors separades per la paraula "TO". Exemple: [01/05/2014 TO 31/05/2014]</li>
						(La cerca per dates s'aplica a la data d'inici de l'expedient.)
					</ul>
				</p>
				<p>IMPORTANT: per a què la cerca sigui més ràpida i retorni els expedients desitjats cal intentar sempre acotar el màxim possible i evitar fer cerques molt genèriques.</p>
			</div>
		</div>

		<script>
			$(document).ready( function () {
			    $('#table_id').DataTable( {
			    	deferRender: true,
			    	data: ${expedients},
					columns: [
						{ data: 'numExp' },
						{ data: 'grupCreador' },
						{ data: 'dataInici' },
						{ data: 'dataFi' },												
				        { data: 'nomNatural' },
				        { data: 'nomNaturalOrgan' },
						{ data: 'nomNaturalInstitucio' },
				        { data: 'codiClass' },
				        { data: 'localitzacio' }
					],
			    	columnDefs: [
					    {
					      data: ${expedients},
					      defaultContent: "",
					      targets: "_all"
					    },
					    {
					      type: 'date-eu',
					      targets: [2, 3]
					    }
					],
					language: {
					    sProcessing:   "Processant...",
					    sLengthMenu:   "Mostra _MENU_ resultats",
					    sZeroRecords:  "No s'han trobat resultats.",
					    sInfo:         "Mostrant de _START_ a _END_ de _TOTAL_ resultats",
					    sInfoEmpty:    "Mostrant de 0 a 0 de 0 resultats",
					    sInfoFiltered: "(filtrat de _MAX_ total resultats)",
					    sInfoPostFix:  "",
					    sSearch:       "Filtrar:",
					    sUrl:          "",
					    oPaginate: {
					        "sFirst":    "Primer",
					        "sPrevious": "Anterior",
					        "sNext":     "Següent",
					        "sLast":     "Últim"
					    }
					}
			    } );
			    
				var OSX = {
					container: null,
					init: function () {
						$("#helpIcon").click(function (e) {
							e.preventDefault();	
				
							$("#osx-modal-content").modal({
								overlayId: 'osx-overlay',
								containerId: 'osx-container',
								closeHTML: null,
								minHeight: 80,
								opacity: 65, 
								position: ['0',],
								overlayClose: true,
								onOpen: OSX.open,
								onClose: OSX.close
							});
						});
					},
					open: function (d) {
						var self = this;
						self.container = d.container[0];
						d.overlay.fadeIn('slow', function () {
							$("#osx-modal-content", self.container).show();
							var title = $("#osx-modal-title", self.container);
							title.show();
							d.container.slideDown('slow', function () {
								setTimeout(function () {
									var h = $("#osx-modal-data", self.container).height()
										+ title.height()
										+ 20; // padding
									d.container.animate(
										{height: h}, 
										200,
										function () {
											$("div.close", self.container).show();
											$("#osx-modal-data", self.container).show();
										}
									);
								}, 300);
							});
						})
					},
					close: function (d) {
						var self = this; // this = SimpleModal object
						d.container.animate(
							{top:"-" + (d.container.height() + 20)},
							500,
							function () {
								self.close(); // or $.modal.close();
							}
						);
					}
				};
				
				OSX.init();
			} );
		</script>
	</body>
</html>