<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
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
				
				if (tecla == 13 || tecla == 116){
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
		</div>
		<div class="buscador">
			<input type="text" id="filtro" name="filtro" class="text" onfocus="foco(this)" onblur="no_foco(this)"/>			
			<input type="button" value="Cercar" class="boton" onclick="javascript:cercarExpedients();" />
		</div>

		<#if (size > 0)>
                <#if (size = 1)>
                        <div class="results">${size} resultat.</div>
                <#else>
                        <div class="results">${size} resultats.</div>
                </#if>
                <div class="headerRow">
                <div class="headerColumns">
                        <div class="columnId">Identificador</div>
                        <div class="columnDate">Data inici</div>
                        <div class="columnDate">Data fi</div>
                        <div class="column">Nom natural</div>
                        <div class="columnOrgan">Òrgan</div>
                        <div class="columnOrgan">Institució</div>
                        <div class="columnCodi">Codi class.</div>
                        <div class="columnLoc">Localitzaci&#243;</div>                        
                    </div>
            </div>
        <#else>   
                <div class="results">No s'han trobat resultats.</div>
        </#if>
		 	
		<#list expedients as exp>
                <#if ((exp_index + 1) % 2) = 0>
                        <div class="rowEven">
                <#else>
                        <div class="rowOdd">
                </#if>
                      <div class="image"><img src="/share/res/components/documentlibrary/images/record-folder-32.png"/></div>
                      <div class="columns">
                              <div class="columnId"><#if exp.numExp != "">${exp.numExp}<#else>-</#if></div>
                              <div class="columnDate"><#if exp.dataInici??>${exp.dataInici?datetime}<#else>-</#if></div>
                              <div class="columnDate"><#if exp.dataFi??>${exp.dataFi?datetime}<#else>-</#if></div>
                              <div class="column"><#if exp.nomNatural != "">${exp.nomNatural}<#else>-</#if></div>
                              <div class="columnOrgan"><#if exp.nomNaturalOrgan??>${exp.nomNaturalOrgan}<#else>-</#if></div>
                              <div class="columnOrgan"><#if exp.nomNaturalInstitucio??>${exp.nomNaturalInstitucio}<#else>-</#if></div>
                              <div class="columnCodi"><#if exp.codiClass != "">${exp.codiClass}<#else>-</#if></div>
                              <div class="columnLoc"><#if exp.localitzacio != "">${exp.localitzacio}<#else>-</#if></div>                              
                          </div>
                </div>
        </#list>
	</body>
</html>