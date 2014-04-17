
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->
<#-- DEPRECATED -->

<#if formUI == "true">
   <@formLib.renderFormsRuntime formId=formId />
</#if>
    
<div id="${formId}-container" class="form-container">
 
   <#if form.showCaption?exists && form.showCaption>
      <div id="${formId}-caption" class="caption"><span class="mandatory-indicator">*</span>${msg("form.required.fields")}</div>
   </#if>
    
   <#if form.mode != "view">
      <form id="${formId}" method="${form.method}" accept-charset="utf-8" enctype="${form.enctype}" action="${form.submissionUrl}">
   </#if>
   
  
   <div id="${formId}-fields" class="form-fields">
      <#--
      <div class="set-bordered-panel">
         <div class="set-bordered-panel-heading">${msg("label.set.idStatus")}</div>
         <div class="set-bordered-panel-body">
            <@formLib.renderField field=form.fields["prop_cm_name"] />
            <@formLib.renderField field=form.fields["prop_rma_identifier"] />
            <#if form.mode == "view">
               <@formLib.renderField field=form.fields["prop_rmCategoryIdentifier"] />
            </#if>           
            <@formLib.renderField field=form.fields["prop_cm_title"] />
            <@formLib.renderField field=form.fields["prop_cm_description"] />
            <#if form.fields["prop_cm_owner"]?? && form.mode == "view">
               <@formLib.renderField field=form.fields["prop_cm_owner"] />
            </#if>
            <#if form.fields["prop_rmDeclared"]?? && form.mode == "view">
               <@formLib.renderField field=form.fields["prop_rmDeclared"] />
            </#if>
            <#if form.fields["prop_rma_declaredAt"]?? && form.mode == "view">
               <@formLib.renderField field=form.fields["prop_rma_declaredAt"] />
            </#if>
            <#if form.fields["prop_rma_declaredBy"]?? && form.mode == "view">
               <@formLib.renderField field=form.fields["prop_rma_declaredBy"] />
            </#if>
            <#if form.fields["prop_cm_author"]??>
               <@formLib.renderField field=form.fields["prop_cm_author"] />
            </#if>
         </div>
      </div>
      -->
      <#--
      <#if form.fields["prop_mimetype"]?? || form.mode == "view">
      <div class="set-bordered-panel">
         <div class="set-bordered-panel-heading">${msg("label.set.general")}</div>
         <div class="set-bordered-panel-body">
            <#if form.mode == "view">
               <@formLib.renderField field=form.fields["prop_cm_creator"] />               
               <@formLib.renderField field=form.fields["prop_cm_modifier"] />
               <@formLib.renderField field=form.fields["prop_cm_modified"] />
               <#if form.fields["prop_size"]??>
                  <@formLib.renderField field=form.fields["prop_size"] />
               </#if>
            </#if>
            <#if form.fields["prop_mimetype"]??>
               <@formLib.renderField field=form.fields["prop_mimetype"] />
            </#if>
         </div>
      </div>
      </#if>
      -->
      <#--
      <div class="set-bordered-panel">
         <div class="set-bordered-panel-heading">${msg("label.set.record")}</div>
         <div class="set-bordered-panel-body">
            <#if form.fields["prop_rmRecordType"]?? && form.mode == "view">
               <@formLib.renderField field=form.fields["prop_rmRecordType"] />
            </#if>
            <@formLib.renderField field=form.fields["prop_rma_originator"] />
            <@formLib.renderField field=form.fields["prop_rma_originatingOrganization"] />
            <#if form.mode == "view">
               <@formLib.renderField field=form.fields["prop_rma_dateFiled"] />
            </#if>
            <@formLib.renderField field=form.fields["prop_rma_publicationDate"] />
            <@formLib.renderField field=form.fields["prop_rma_location"] />
            <@formLib.renderField field=form.fields["prop_rma_mediaType"] />
            <@formLib.renderField field=form.fields["prop_rma_format"] />
            <!-- Scanned Record Fields 
            <#if form.fields["prop_dod_scannedFormatVersion"]??>
               <@formLib.renderField field=form.fields["prop_dod_scannedFormatVersion"] />
               <@formLib.renderField field=form.fields["prop_dod_resolutionX"] />
               <@formLib.renderField field=form.fields["prop_dod_resolutionY"] />
               <@formLib.renderField field=form.fields["prop_dod_scannedBitDepth"] />
            </#if>
            <!-- PDF Record Fields 
            <#if form.fields["prop_dod_producingApplication"]??>
               <@formLib.renderField field=form.fields["prop_dod_producingApplication"] />
               <@formLib.renderField field=form.fields["prop_dod_producingApplicationVersion"] />
               <@formLib.renderField field=form.fields["prop_dod_pdfVersion"] />
               <@formLib.renderField field=form.fields["prop_dod_creatingApplication"] />
               <@formLib.renderField field=form.fields["prop_dod_documentSecuritySettings"] />
            </#if>
            <!-- Digital Photograph Record Fields 
            <#if form.fields["prop_dod_caption"]??>
               <@formLib.renderField field=form.fields["prop_dod_caption"] />
               <@formLib.renderField field=form.fields["prop_dod_photographer"] />
               <@formLib.renderField field=form.fields["prop_dod_copyright"] />
               <@formLib.renderField field=form.fields["prop_dod_bitDepth"] />
               <@formLib.renderField field=form.fields["prop_dod_imageSizeX"] />
               <@formLib.renderField field=form.fields["prop_dod_imageSizeY"] />
               <@formLib.renderField field=form.fields["prop_dod_imageSource"] />
               <@formLib.renderField field=form.fields["prop_dod_compression"] />
               <@formLib.renderField field=form.fields["prop_dod_iccIcmProfile"] />
               <@formLib.renderField field=form.fields["prop_dod_exifInformation"] />
            </#if>
            <!-- Web Record Fields 
            <#if form.fields["prop_dod_webFileName"]??>
               <@formLib.renderField field=form.fields["prop_dod_webFileName"] />
               <@formLib.renderField field=form.fields["prop_dod_webPlatform"] />
               <@formLib.renderField field=form.fields["prop_dod_webSiteName"] />
               <@formLib.renderField field=form.fields["prop_dod_webSiteURL"] />
               <@formLib.renderField field=form.fields["prop_dod_captureMethod"] />
               <@formLib.renderField field=form.fields["prop_dod_captureDate"] />
               <@formLib.renderField field=form.fields["prop_dod_contact"] />
               <@formLib.renderField field=form.fields["prop_dod_contentManagementSystem"] />
            </#if>
         </div>
      </div>
      -->
      <#--
      <div class="set-bordered-panel">
         <div class="set-bordered-panel-heading">${msg("label.set.correspondence")}</div>
         <div class="set-bordered-panel-body">
            <@formLib.renderField field=form.fields["prop_rma_dateReceived"] />
            <@formLib.renderField field=form.fields["prop_rma_address"] />
            <@formLib.renderField field=form.fields["prop_rma_otherAddress"] />
         </div>
      </div>
      -->      
      <#--
      <div class="set-bordered-panel">
         <div class="set-bordered-panel-heading">${msg("label.set.security")}</div>
         <div class="set-bordered-panel-body">
            <@formLib.renderField field=form.fields["prop_rmc_supplementalMarkingList"] />
         </div>
      </div>
	  -->
      <#--
      <#if form.fields["prop_rma_vitalRecordIndicator"]?? || form.fields["prop_rma_reviewPeriod"]?? ||
           (form.fields["prop_rma_reviewAsOf"]?? && form.mode == "view")>
         <div class="set-bordered-panel">
            <div class="set-bordered-panel-heading">${msg("label.set.vitalRecord")}</div>
            <div class="set-bordered-panel-body">
               <#if form.fields["prop_rma_vitalRecordIndicator"]??>
                  <@formLib.renderField field=form.fields["prop_rma_vitalRecordIndicator"] />
               </#if>
               <#if form.fields["prop_rma_reviewPeriod"]??>
                  <@formLib.renderField field=form.fields["prop_rma_reviewPeriod"] />
               </#if>
               <#if form.fields["prop_rma_reviewAsOf"]?? && form.mode == "view">
                  <@formLib.renderField field=form.fields["prop_rma_reviewAsOf"] />
               </#if>
            </div>
         </div>
      </#if>
      -->
	  <#--
      <#if form.mode == "view">
         <div class="set-bordered-panel">
            <div class="set-bordered-panel-heading">${msg("label.set.disposition")}</div>
            <div class="set-bordered-panel-body">
               <@formLib.renderField field=form.fields["prop_rmDispositionInstructions"] />
               <#if form.fields["prop_rma_recordSearchDispositionActionAsOf"]??>
                  <@formLib.renderField field=form.fields["prop_rma_recordSearchDispositionActionAsOf"] />
               </#if>
               <#if form.fields["prop_rma_cutOffDate"]??>
                  <@formLib.renderField field=form.fields["prop_rma_cutOffDate"] />
               </#if>
            </div>
         </div>
      </#if>
      -->
      <#list form.structure as item>
         <#if item.kind == "set" && item.id == "rm-custom">
            <@formLib.renderSet set=item />
            <#break>
         </#if>
      </#list>

      
	<!-- ############################### -->      
    <!-- ## Custom properties for UdL ## -->
	<!-- ##      DOCUMENT SIMPLE      ## -->    
	<!-- ############################### -->    
	
	<#if form.fields["prop_udlrm_grup_creador_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: Creació</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_grup_creador_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_grup_creador_documentSimple"] />
		        </#if>
		    	<#if form.fields["prop_cm_creator"]??>
		        	<@formLib.renderField field=form.fields["prop_cm_creator"] />
		        </#if>		        
		    	<#if form.fields["prop_cm_modifier"]??>
		        	<@formLib.renderField field=form.fields["prop_cm_modifier"] />
		        </#if>
			</div>
		</div>
	</#if>
	
	<#if form.fields["prop_udlrm_tipus_documental_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple</div>
		    <div class="set-bordered-panel-body">
		        <#--
		        <#if form.fields["prop_udlrm_descripcio_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_descripcio_documentSimple"] />
		        </#if>
		       -->		       
		        <#--
		        <#if form.fields["prop_udlrm_prioritat_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_prioritat_documentSimple"] />
		        </#if>
		        -->
		    	<#if form.fields["prop_udlrm_tipus_entitat_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_tipus_entitat_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_categoria_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_categoria_documentSimple"] />
		        </#if>	
		        <#if form.fields["prop_cm_signaturesDocumentRm"]??>
		        	<@formLib.renderField field=form.fields["prop_cm_signaturesDocumentRm"] />
		        </#if>	       
			</div>
		</div>
	</#if>
	
	<#if form.fields["prop_udlrm_secuencial_identificador_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 2 - Identificador</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_secuencial_identificador_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_secuencial_identificador_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_esquema_identificador_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_esquema_identificador_documentSimple"] />
		        </#if>
			</div>
		</div>
	</#if>
		
	<#if form.fields["prop_udlrm_nom_natural_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 3 - Nom</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_cm_name"]??>
					<@formLib.renderField field=form.fields["prop_cm_name"] />
		        </#if>		        
			</div>
		</div>
	</#if>
		
	<#if form.fields["prop_udlrm_data_inici_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 4 - Dates</div>
		    <div class="set-bordered-panel-body">		    	
		    	<#if form.fields["prop_cm_created"]??>
		    		<@formLib.renderField field=form.fields["prop_cm_created"] />
		    	</#if>
		    	<#if form.fields["prop_cm_modified"]??>
		    		<@formLib.renderField field=form.fields["prop_cm_modified"] />
		    	</#if>
		    	<#if form.fields["prop_udlrm_data_inici_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_data_inici_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_data_fi_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_data_fi_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_data_registre_entrada_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_data_registre_entrada_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_data_registre_sortida_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_data_registre_sortida_documentSimple"] />
		        </#if>	        
			</div>
		</div>
	</#if>	
	
	<#if form.fields["prop_udlrm_sensibilitat_dades_caracter_personal_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 8.1 - Seguretat - Classificació</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_classificacio_acces_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_classificacio_acces_documentSimple"] />
		        </#if>
			</div>
		</div>
	</#if>
	
	<#if form.fields["prop_udlrm_classificacio_acces_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 8 - Seguretat</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_advertencia_seguretat_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_advertencia_seguretat_documentSimple"] />
		        </#if>
		    	<#if form.fields["prop_udlrm_categoria_advertencia_seguretat_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_categoria_advertencia_seguretat_documentSimple"] />
		        </#if>	
		    	<#if form.fields["prop_udlrm_sensibilitat_dades_caracter_personal_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_sensibilitat_dades_caracter_personal_documentSimple"] />
		        </#if>
		        <#--
		    	<#if form.fields["prop_udlrm_classificacio_ENS_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_classificacio_ENS_documentSimple"] />
		        </#if>
		        -->    	
		        <#--
		        <#if form.fields["prop_udlrm_codi_politica_control_acces_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_codi_politica_control_acces_documentSimple"] />
		        </#if>
		        -->
			</div>
		</div>
	</#if>
		
	<#--	
	<#if form.fields["prop_udlrm_tipus_acces_1_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: Drets accés, us i reutilització</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_condicions_acces_1_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_condicions_acces_1_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_condicions_acces_2_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_condicions_acces_2_documentSimple"] />
		        </#if>
		    	<#if form.fields["prop_udlrm_tipus_acces_1_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_tipus_acces_1_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_tipus_acces_2_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_tipus_acces_2_documentSimple"] />
		        </#if>
			</div>
		</div>
	</#if>
	-->
	
	<#if form.fields["prop_udlrm_idioma_1_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 11 - Idioma</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_idioma_1_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_idioma_1_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_idioma_2_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_idioma_2_documentSimple"] />
		        </#if>
		        <#--
		        <#if form.fields["prop_udlrm_idioma_3_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_idioma_3_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_idioma_4_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_idioma_4_documentSimple"] />
		        </#if>
		        -->
			</div>
		</div>
	</#if>
		
	<#if form.fields["prop_udlrm_valoracio_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 13 - Avaluació</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_valoracio_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_valoracio_documentSimple"] />
		        </#if>
			</div>
		</div>
	</#if>

	<#if form.fields["prop_udlrm_tipus_dictamen_1_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 13.2 - Avaluació - Dictamen</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_tipus_dictamen_1_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_tipus_dictamen_1_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_accio_dictaminada_1_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_accio_dictaminada_1_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_tipus_dictamen_2_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_tipus_dictamen_2_documentSimple"] />
		        </#if>		        
		        <#if form.fields["prop_udlrm_accio_dictaminada_2_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_accio_dictaminada_2_documentSimple"] />
		        </#if>
			</div>
		</div>
	</#if>

	<#if form.fields["prop_udlrm_suport_origen_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 14 - Característiques tècniques</div>
		    <div class="set-bordered-panel-body">		    	
		    	<#if form.fields["prop_udlrm_suport_origen_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_suport_origen_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_mimetype"]??>
	               <@formLib.renderField field=form.fields["prop_mimetype"] />
	            </#if>
		        <#--
		        <#if form.fields["prop_udlrm_nom_format_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_nom_format_documentSimple"] />
		        </#if>
		        -->
		        <#if form.fields["prop_udlrm_versio_format_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_versio_format_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_nom_aplicacio_creacio_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_nom_aplicacio_creacio_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_versio_aplicacio_creacio_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_versio_aplicacio_creacio_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_registre_formats_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_registre_formats_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_resolucio_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_resolucio_documentSimple"] />
		        </#if>
			</div>
		</div>
	</#if>

	<#if form.fields["prop_udlrm_dimensions_fisiques_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 14.8 - Característiques tècniques - Tamany</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_dimensions_fisiques_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_dimensions_fisiques_documentSimple"] />
		        </#if>
		        <#--
		        <#if form.fields["prop_udlrm_tamany_logic_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_tamany_logic_documentSimple"] />
		        </#if>
		        -->
		    	<#--
		    	<#if form.fields["prop_udlrm_quantitat_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_quantitat_documentSimple"] />
		        </#if>
		        -->
		    	<#if form.fields["prop_udlrm_unitats_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_unitats_documentSimple"] />
		        </#if>

			</div>
		</div>
	</#if>

	<#if form.fields["prop_udlrm_suport_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 15 - Ubicació</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_suport_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_suport_documentSimple"] />
		        </#if>
		        <#--
		        <#if form.fields["prop_udlrm_suport_2_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_suport_2_documentSimple"] />
		        </#if>
		        -->
		        <#if form.fields["prop_udlrm_localitzacio_1_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_localitzacio_1_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_localitzacio_2_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_localitzacio_2_documentSimple"] />
		        </#if>
			</div>
		</div>
	</#if>

	<#if form.fields["prop_udlrm_verificacio_integritat_algorisme_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 16 - Verificació integritat</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_verificacio_integritat_algorisme_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_verificacio_integritat_algorisme_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_verificacio_integritat_valor_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_verificacio_integritat_valor_documentSimple"] />
		        </#if>		    	
		    	<#--
		    	<#if form.fields["prop_udlrm_algoritme_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_algoritme_documentSimple"] />
		        </#if>
		        -->
		        <!--
		        <#if form.fields["prop_udlrm_valor_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_valor_documentSimple"] />
		        </#if>
		        -->
			</div>
		</div>
	</#if>
	
	<#if form.fields["prop_udlrm_denominacio_estat_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 18 - Tipus documental</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_tipus_documental_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_tipus_documental_documentSimple"] />
		        </#if>
			</div>
		</div>
	</#if>
	
	<#if form.fields["prop_udlrm_denominacio_estat_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 20 - Estat elaboració</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_denominacio_estat_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_denominacio_estat_documentSimple"] />
		        </#if>
			</div>
		</div>
	</#if>

	<#if form.fields["prop_udlrm_tipus_copia_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 20.2 - Estat elaboració - Característiques còpia</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_tipus_copia_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_tipus_copia_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_motiu_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_motiu_documentSimple"] />
		        </#if>
			</div>
		</div>
	</#if>
	
	<#if form.fields["prop_udlrm_codi_classificacio_1_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: 22 - Classificació</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_codi_classificacio_1_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_codi_classificacio_1_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_denominacio_classe_1_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_denominacio_classe_1_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_codi_classificacio_2_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_codi_classificacio_2_documentSimple"] />
		        </#if>		        
		        <#if form.fields["prop_udlrm_denominacio_classe_2_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_denominacio_classe_2_documentSimple"] />
		        </#if>
			</div>
		</div>
	</#if>
	
	<#--	
	<#if form.fields["prop_udlrm_tipus_signatura_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: Signatura</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_tipus_signatura_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_tipus_signatura_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_format_signatura_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_format_signatura_documentSimple"] />
		        </#if>
		    	<#if form.fields["prop_udlrm_rol_signatura_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_rol_signatura_documentSimple"] />
		        </#if>
		         <#if form.fields["prop_cm_signaturesDocumentRm"]??>
		        	<@formLib.renderField field=form.fields["prop_cm_signaturesDocumentRm"] />
		        </#if>	
			</div>
		</div>
	</#if>

	<#if form.fields["prop_udlrm_accio_documentSimple"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Document simple: Traçabilitat</div>
		    <div class="set-bordered-panel-body">
		    	<#if form.fields["prop_udlrm_accio_documentSimple"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_accio_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_motiu_reglat_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_motiu_reglat_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_usuari_accio_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_usuari_accio_documentSimple"] />
		        </#if>
		        <#if form.fields["prop_udlrm_modificacio_metadades_documentSimple"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_modificacio_metadades_documentSimple"] />
		        </#if>
			</div>
		</div>
	</#if>
	-->
	
	<!-- ############################### -->      
    <!-- ## Custom properties for UdL ## -->
	<!-- ##     SIGNATURA DETTACHED   ## -->    
	<!-- ############################### -->    
	<#if form.fields["prop_udlrm_tipus_signatura"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Signatura dettached</div>
		    <div class="set-bordered-panel-body">
		        <#if form.fields["prop_udlrm_tipus_signatura"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_tipus_signatura"] />
		        </#if>
		        <#if form.fields["prop_udlrm_id_document_signat"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_id_document_signat"] />
		        </#if>
		    	<#if form.fields["prop_udlrm_data_signatura"]??>
		        	<@formLib.renderField field=form.fields["prop_udlrm_data_signatura"] />
		        </#if>
		        <#if form.fields["prop_udlrm_data_ini_validacio_signatura"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_data_ini_validacio_signatura"] />
		        </#if>
		        <#if form.fields["prop_udlrm_data_fi_validacio_signatura"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_data_fi_validacio_signatura"] />
		        </#if>
		        <#if form.fields["prop_udlrm_validacio_signatura"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_validacio_signatura"] />
		        </#if>
		        <#if form.fields["prop_udlrm_nom_signatari_signatura"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_nom_signatari_signatura"] />
		        </#if>
		        <#if form.fields["prop_udlrm_identificador_signatari_signatura"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_identificador_signatari_signatura"] />
		        </#if>
		        <#if form.fields["prop_udlrm_organitzacio_signatura"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_organitzacio_signatura"] />
		        </#if>
		        <#if form.fields["prop_udlrm_unitat_organica_signatura"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_unitat_organica_signatura"] />
		        </#if>
		        <#if form.fields["prop_udlrm_politica_signatura"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_politica_signatura"] />
		        </#if>
		        <#if form.fields["prop_udlrm_proveidor_certificat_signatura"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_proveidor_certificat_signatura"] />
		        </#if>
		        <#if form.fields["prop_udlrm_tipus_certificat_signatura"]??>
					<@formLib.renderField field=form.fields["prop_udlrm_tipus_certificat_signatura"] />
		        </#if>
			</div>
		</div>
	</#if>

	<!-- ############################### -->      
    <!-- ## Custom properties for UdL ## -->
	<!-- ##         REGULACIO         ## -->    
	<!-- ############################### -->    
	<#if form.fields["prop_udl_nom_natural_regulacio"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Regulació</div>
		    <div class="set-bordered-panel-body">
		        <#if form.fields["prop_udl_nom_natural_regulacio"]??>
					<@formLib.renderField field=form.fields["prop_udl_nom_natural_regulacio"] />
		        </#if>
			</div>
		</div>
	</#if>

	<!-- ############################### -->      
    <!-- ## Custom properties for UdL ## -->
	<!-- ##        INSTITUCIÓ         ## -->    
	<!-- ############################### -->    
	<#if form.fields["prop_udl_nom_natural_institucio"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Institucio</div>
		    <div class="set-bordered-panel-body">
		        <#if form.fields["prop_udl_nom_natural_institucio"]??>
					<@formLib.renderField field=form.fields["prop_udl_nom_natural_institucio"] />
		        </#if>
		    </div>
		</div>
	</#if>

	<!-- ############################### -->      
    <!-- ## Custom properties for UdL ## -->
	<!-- ##           ÒRGAN           ## -->    
	<!-- ############################### -->    
	<#if form.fields["prop_udl_nom_natural_organ"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Òrgan</div>
		    <div class="set-bordered-panel-body">
		        <#if form.fields["prop_udl_nom_natural_organ"]??>
					<@formLib.renderField field=form.fields["prop_udl_nom_natural_organ"] />
		        </#if>
			</div>
		</div>
	</#if>
	
	<!-- ############################### -->      
    <!-- ## Custom properties for UdL ## -->
	<!-- ##         DISPOSITIU        ## -->    
	<!-- ############################### -->    
	<#if form.fields["prop_udl_nom_dispositiu"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Dispositiu</div>
		    <div class="set-bordered-panel-body">
		        <#if form.fields["prop_udl_nom_dispositiu"]??>
					<@formLib.renderField field=form.fields["prop_udl_nom_dispositiu"] />
		        </#if>
			</div>
		</div>
	</#if>

	<!-- ############################### -->      
    <!-- ## Custom properties for UdL ## -->
	<!-- ##         PERSONA           ## -->    
	<!-- ############################### -->    
	<#if form.fields["prop_udl_secuencial_identificador_persona"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Persona</div>
		    <div class="set-bordered-panel-body">
		        <#if form.fields["prop_udl_secuencial_identificador_persona"]??>
					<@formLib.renderField field=form.fields["prop_udl_secuencial_identificador_persona"] />
		        </#if>
		        <#if form.fields["prop_udl_esquema_identificador_persona"]??>
					<@formLib.renderField field=form.fields["prop_udl_esquema_identificador_persona"] />
		        </#if>
		        <#if form.fields["prop_udl_nom_natural_persona"]??>
					<@formLib.renderField field=form.fields["prop_udl_nom_natural_persona"] />
		        </#if>
		        <#if form.fields["prop_udl_esquema_nom_persona"]??>
					<@formLib.renderField field=form.fields["prop_udl_esquema_nom_persona"] />
		        </#if>
		    	<#if form.fields["prop_udl_data_inici_persona"]??>
		        	<@formLib.renderField field=form.fields["prop_udl_data_inici_persona"] />
		        </#if>
		        <#if form.fields["prop_udl_data_fi_persona"]??>
					<@formLib.renderField field=form.fields["prop_udl_data_fi_persona"] />
		        </#if>
		        <#if form.fields["prop_udl_tipus_contacte_persona"]??>
					<@formLib.renderField field=form.fields["prop_udl_tipus_contacte_persona"] />
		        </#if>
		        <#if form.fields["prop_udl_dada_contacte_persona"]??>
					<@formLib.renderField field=form.fields["prop_udl_dada_contacte_persona"] />
		        </#if>
		        <#if form.fields["prop_udl_ocupacio_persona"]??>
					<@formLib.renderField field=form.fields["prop_udl_ocupacio_persona"] />
		        </#if>
			</div>
		</div>
	</#if>

	<!-- ############################### -->      
    <!-- ## Custom properties for UdL ## -->
	<!-- ##          IARXIU           ## -->    
	<!-- ############################### -->    
	<#if form.fields["prop_udl_id_ref_PIA"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">iArxiu</div>
		    <div class="set-bordered-panel-body">
		        <#if form.fields["prop_udl_id_ref_PIA"]??>
					<@formLib.renderField field=form.fields["prop_udl_id_ref_PIA"] />
		        </#if>
		        <#if form.fields["prop_udl_id_peticio"]??>
					<@formLib.renderField field=form.fields["prop_udl_id_peticio"] />
		        </#if>
			</div>
		</div>
	</#if>

	<!-- ############################### -->      
    <!-- ## Custom properties for UdL ## -->
	<!-- ##        SIGNATURES         ## -->    
	<!-- ############################### -->    
	<#if form.fields["prop_udl_signatura1_nom"]??>
		<div class="set-bordered-panel">
			<div class="set-bordered-panel-heading">Signatures</div>
		    <div class="set-bordered-panel-body">
		        <#if form.fields["prop_udl_signatura1_nom"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura1_nom"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura1_carrec"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura1_carrec"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura1_dni"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura1_dni"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura1_data"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura1_data"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura1_data_fi_certificat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura1_data_fi_certificat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura1_data_caducitat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura1_data_caducitat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura2_nom"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura2_nom"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura2_carrec"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura2_carrec"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura2_dni"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura2_dni"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura2_data"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura2_data"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura2_data_fi_certificat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura2_data_fi_certificat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura2_data_caducitat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura2_data_caducitat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura3_nom"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura3_nom"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura3_carrec"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura3_carrec"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura3_dni"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura3_dni"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura3_data"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura3_data"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura3_data_fi_certificat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura3_data_fi_certificat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura3_data_caducitat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura3_data_caducitat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura4_nom"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura4_nom"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura4_carrec"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura4_carrec"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura4_dni"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura4_dni"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura4_data"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura4_data"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura4_data_fi_certificat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura4_data_fi_certificat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura4_data_caducitat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura4_data_caducitat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura5_nom"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura5_nom"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura5_carrec"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura5_carrec"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura5_dni"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura5_dni"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura5_data"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura5_data"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura5_data_fi_certificat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura5_data_fi_certificat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura5_data_caducitat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura5_data_caducitat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura6_nom"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura6_nom"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura6_carrec"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura6_carrec"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura6_dni"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura6_dni"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura6_data"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura6_data"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura6_data_fi_certificat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura6_data_fi_certificat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura6_data_caducitat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura6_data_caducitat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura7_nom"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura7_nom"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura7_carrec"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura7_carrec"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura7_dni"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura7_dni"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura7_data"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura7_data"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura7_data_fi_certificat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura7_data_fi_certificat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura7_data_caducitat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura7_data_caducitat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura8_nom"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura8_nom"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura8_carrec"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura8_carrec"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura8_dni"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura8_dni"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura8_data"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura8_data"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura8_data_fi_certificat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura8_data_fi_certificat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura8_data_caducitat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura8_data_caducitat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura9_nom"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura9_nom"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura9_carrec"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura9_carrec"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura9_dni"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura9_dni"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura9_data"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura9_data"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura9_data_fi_certificat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura9_data_fi_certificat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura9_data_caducitat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura9_data_caducitat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura10_nom"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura10_nom"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura10_carrec"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura10_carrec"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura10_dni"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura10_dni"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura10_data"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura10_data"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura10_data_fi_certificat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura10_data_fi_certificat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura10_data_caducitat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura10_data_caducitat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura11_nom"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura11_nom"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura11_carrec"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura11_carrec"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura11_dni"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura11_dni"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura11_data"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura11_data"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura11_data_fi_certificat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura11_data_fi_certificat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura11_data_caducitat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura11_data_caducitat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura12_nom"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura12_nom"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura12_carrec"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura12_carrec"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura12_dni"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura12_dni"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura12_data"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura12_data"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura12_data_fi_certificat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura12_data_fi_certificat"] />
		        </#if>
		        <#if form.fields["prop_udl_signatura12_data_caducitat"]??>
					<@formLib.renderField field=form.fields["prop_udl_signatura12_data_caducitat"] />
		        </#if>
			</div>
		</div>
	</#if>

    <#if form.mode != "view">
       <@formLib.renderFormButtons formId=formId />
       </form>
    </#if>
</div>