/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
 
/**
 * Records results common code component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsResults
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * Search constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsResults} The new RecordsSearch instance
    * @constructor
    */
   Alfresco.RecordsResults = function(htmlId)
   {
      /* Mandatory properties */
      this.id = htmlId;
      
      this.sortby = [{"field": "rma:identifier", "order": "asc"}, {"field": "", "order": "asc"}, {"field": "", "order": "asc"}];
      
      return this;
   };
   
   Alfresco.RecordsResults.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * siteId to search in.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Maximum number of results displayed.
          * 
          * @property maxResults
          * @type int
          * @default 500
          */
         maxResults: 500,
         
         /**
          * Custom rm meta fields
          * 
          * @property customFields
          * @type Array
          */
         customFields: [],
         
         /**
          * Saved searches
          * 
          * @property savedSearches
          * @type Array
          */
         savedSearches: []
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},

      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: {},

      /**
       * Number of search results.
       * 
       * @property resultsCount
       * @type number
       */
      resultsCount: 0,
      
      /**
       * Array of NodeRef strings from the search results.
       * 
       * @property resultNodeRefs
       * @type Array
       */
      resultNodeRefs: [],
      
      /**
       * True if there are more results than the ones listed in the table.
       * 
       * @property hasMoreResults
       * @type boolean
       */
      hasMoreResults: false,
      
      /**
       * Array of sort descriptor objects. See constructor above for example object structure.
       * 
       * @property sortby
       * @type Array
       */
      sortby: null,
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.RecordsResults} returns 'this' for method chaining
       */
      setOptions: function RecordsResults_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.RecordsResults} returns 'this' for method chaining
       */
      setMessages: function RecordsResults_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RecordsResults_onReady()
      {
         var me = this;
         
         // Sorting option menus
         this.widgets.sortMenus = [];
         this.widgets.sortOrderMenus = [];
         this._setupSortControls(3);
         
         // Column hide/show meta-data options
         var onMetadataClick = function onMetadataClick(e)
         {
            var el = Event.getTarget(e);
            var columnKey = el.id.substring(el.id.lastIndexOf('-') + 1);
            var col = me.widgets.dataTable.getColumn(columnKey)
            if (col)
            {
               if (col.hidden)
               {
                  me.widgets.dataTable.showColumn(col);
               }
               else
               {
                  me.widgets.dataTable.hideColumn(col);
               }
            }
         };
         
         var elAcceptor = function(el)
         {
            return (el.id.indexOf("-metadata-") != -1);
         };
         
         var elVisitor = function(el)
         {
            Event.on(el, "click", onMetadataClick);
         };
         
         // Apply meta-data field event handlers via element visitor pattern
         Dom.getElementsBy(elAcceptor, "input", Dom.get(this.id + "-metadata"), elVisitor);
         
         var onResultOptionsClick = function onResultOptionsClick(e)
         {
            var elToggle = Dom.get(me.id + "-options-toggle");
            var el = Dom.get(me.id + "-options");
            if (el.style.display === "block")
            {
               el.style.display = "none";
               Dom.setStyle(elToggle, "background-image", "url(" + Alfresco.constants.URL_RESCONTEXT + "components/images/collapsed.png)");
            }
            else
            {
               el.style.display = "block";
               Dom.setStyle(elToggle, "background-image", "url(" + Alfresco.constants.URL_RESCONTEXT + "components/images/expanded.png)");
            }
         };
         
         // Click handler for result options
         Event.on(Dom.get(this.id + "-options-toggle"), "click", onResultOptionsClick);
         // Initial image background css
         Dom.setStyle(this.id + "-options-toggle", "url(" + Alfresco.constants.URL_RESCONTEXT + "components/images/expanded.png)");
         
         // add the well known 'cm', 'rma' and 'dod' namespace fields
         var fields =
         [
            "nodeRef", "type", "name", "title", "description", "modifiedOn", "modifiedByUser", "modifiedBy",
            "createdOn", "createdByUser", "createdBy", "author", "size", "browseUrl", "parentFolder",
            "properties.rma_identifier", "properties.rma_dateFiled", "properties.rma_publicationDate", "properties.rma_dateReceived",
            "properties.rma_originator", "properties.rma_originatingOrganization", "properties.rma_mediaType", "properties.rma_format", "properties.rma_location",
            "properties.rma_address", "properties.rma_otherAddress", "properties.rmc_supplementalMarkingList", "properties.rma_reviewAsOf",
            "properties.rma_recordSearchDispositionEvents", "properties.rma_recordSearchHasDispositionSchedule", 
            "properties.rma_recordSearchDispositionActionName", "properties.rma_recordSearchDispositionActionAsOf",
            "properties.rma_recordSearchDispositionInstructions", "properties.rma_recordSearchDispositionAuthority",
            "properties.rma_recordSearchDispositionPeriod", "properties.rma_recordSearchDispositionEventsEligible",
            "properties.rma_recordSearchVitalRecordReviewPeriod", "properties.rma_recordSearchHoldReason",
            "properties.dod_scannedFormatVersion", "properties.dod_resolutionX", "properties.dod_resolutionY", "properties.dod_scannedBitDepth",
            "properties.dod_producingApplication", "properties.dod_producingApplicationVersion", "properties.dod_pdfVersion", "properties.dod_creatingApplication", 
            "properties.dod_documentSecuritySettings", "properties.dod_caption", "properties.dod_photographer", "properties.dod_copyright", 
            "properties.dod_bitDepth", "properties.dod_imageSizeX", "properties.dod_imageSizeY", "properties.dod_imageSource", 
            "properties.dod_compression", "properties.dod_iccIcmProfile", "properties.dod_exifInformation", "properties.dod_webFileName", 
            "properties.dod_webPlatform", "properties.dod_webSiteName", "properties.dod_webSiteURL", "properties.dod_captureMethod", 
            "properties.dod_captureDate", "properties.dod_contact", "properties.dod_contentManagementSystem",
			"properties.udlrm_tipus_entitat_documentSimple",
			"properties.udlrm_tipus_documental_documentSimple",
			"properties.udlrm_categoria_documentSimple",
			"properties.udlrm_secuencial_identificador_documentSimple",
			"properties.udlrm_esquema_identificador_documentSimple",
			"properties.udlrm_data_inici_documentSimple",
			"properties.udlrm_data_fi_documentSimple",
			"properties.udlrm_data_registre_entrada_documentSimple",
			"properties.udlrm_data_registre_sortida_documentSimple",
			"properties.udlrm_classificacio_acces_documentSimple",
			"properties.udlrm_advertencia_seguretat_documentSimple",
			"properties.udlrm_categoria_advertencia_seguretat_documentSimple",
			"properties.udlrm_sensibilitat_dades_caracter_personal_documentSimple",
			"properties.udlrm_verificacio_integritat_algorisme_documentSimple",
			"properties.udlrm_verificacio_integritat_valor_documentSimple",
			"properties.udlrm_idioma_1_documentSimple",
			"properties.udlrm_idioma_2_documentSimple",
			"properties.udlrm_valoracio_documentSimple",
			"properties.udlrm_tipus_dictamen_1_documentSimple",
			"properties.udlrm_accio_dictaminada_1_documentSimple",
			"properties.udlrm_tipus_dictamen_2_documentSimple",
			"properties.udlrm_accio_dictaminada_2_documentSimple",
			"properties.udlrm_suport_origen_documentSimple",
			"properties.udlrm_versio_format_documentSimple",
			"properties.udlrm_nom_aplicacio_creacio_documentSimple",
			"properties.udlrm_versio_aplicacio_creacio_documentSimple",
			"properties.udlrm_registre_formats_documentSimple",
			"properties.udlrm_resolucio_documentSimple",
			"properties.udlrm_dimensions_fisiques_documentSimple",
			"properties.udlrm_unitats_documentSimple",
			"properties.udlrm_suport_1_documentSimple",
			"properties.udlrm_localitzacio_1_documentSimple",
			"properties.udlrm_localitzacio_2_documentSimple",
			"properties.udlrm_denominacio_estat_documentSimple",
			"properties.udlrm_tipus_copia_documentSimple",
			"properties.udlrm_motiu_documentSimple",
			"properties.udlrm_codi_classificacio_1_documentSimple",
			"properties.udlrm_denominacio_classe_1_documentSimple",
			"properties.udlrm_codi_classificacio_2_documentSimple",
			"properties.udlrm_denominacio_classe_2_documentSimple",
			"properties.udlrm_grup_creador_documentSimple",
			"properties.udlrm_grup_creador_expedient",
			"properties.udlrm_descripcio_expedient",
			"properties.udlrm_tipus_entitat_expedient",
			"properties.udlrm_categoria_expedient",
			"properties.udlrm_secuencial_identificador_expedient",
			"properties.udlrm_esquema_identificador_expedient",
			"properties.udlrm_data_inici_expedient",
			"properties.udlrm_data_fi_expedient",
			"properties.udlrm_classificacio_acces_expedient",
			"properties.udlrm_advertencia_seguretat_expedient",
			"properties.udlrm_categoria_advertencia_seguretat_expedient",
			"properties.udlrm_sensibilitat_dades_caracter_personal_expedient",
			"properties.udlrm_condicions_acces_1_expedient",
			"properties.udlrm_condicions_acces_2_expedient",
			"properties.udlrm_tipus_acces_1_expedient",
			"properties.udlrm_tipus_acces_2_expedient",
			"properties.udlrm_idioma_1_expedient",
			"properties.udlrm_idioma_2_expedient",
			"properties.udlrm_idioma_3_expedient",
			"properties.udlrm_idioma_4_expedient",
			"properties.udlrm_valoracio_expedient",
			"properties.udlrm_tipus_dictamen_1_expedient",
			"properties.udlrm_accio_dictaminada_1_expedient",
			"properties.udlrm_tipus_dictamen_2_expedient",
			"properties.udlrm_accio_dictaminada_2_expedient",
			"properties.udlrm_suport_origen_expedient",
			"properties.udlrm_dimensions_fisiques_expedient",
			"properties.udlrm_quantitat_expedient",
			"properties.udlrm_unitats_expedient",
			"properties.udlrm_localitzacio_1_expedient",
			"properties.udlrm_localitzacio_2_expedient",
			"properties.udlrm_codi_classificacio_1_expedient",
			"properties.udlrm_denominacio_classe_1_expedient",
			"properties.udlrm_codi_classificacio_2_expedient",
			"properties.udlrm_denominacio_classe_2_expedient",
			"properties.udlrm_descripcio_agregacio",
			"properties.udlrm_tipus_entitat_agregacio",
			"properties.udlrm_categoria_agregacio",
			"properties.udlrm_secuencial_identificador_agregacio",
			"properties.udlrm_esquema_identificador_agregacio",
			"properties.udlrm_data_inici_agregacio",
			"properties.udlrm_data_fi_agregacio",
			"properties.udlrm_classificacio_acces_agregacio",
			"properties.udlrm_advertencia_seguretat_agregacio",
			"properties.udlrm_categoria_advertencia_seguretat_agregacio",
			"properties.udlrm_sensibilitat_dades_caracter_personal_agregacio",
			"properties.udlrm_condicions_acces_1_agregacio",
			"properties.udlrm_condicions_acces_2_agregacio",
			"properties.udlrm_tipus_acces_1_agregacio",
			"properties.udlrm_tipus_acces_2_agregacio",
			"properties.udlrm_idioma_1_agregacio",
			"properties.udlrm_idioma_2_agregacio",
			"properties.udlrm_idioma_3_agregacio",
			"properties.udlrm_idioma_4_agregacio",
			"properties.udlrm_valoracio_agregacio",
			"properties.udlrm_tipus_dictamen_1_agregacio",
			"properties.udlrm_accio_dictaminada_1_agregacio",
			"properties.udlrm_tipus_dictamen_2_agregacio",
			"properties.udlrm_accio_dictaminada_2_agregacio",
			"properties.udlrm_suport_origen_agregacio",
			"properties.udlrm_dimensions_fisiques_agregacio",
			"properties.udlrm_unitats_agregacio",
			"properties.udlrm_codi_classificacio_1_agregacio",
			"properties.udlrm_denominacio_classe_1_agregacio",
			"properties.udlrm_codi_classificacio_2_agregacio",
			"properties.udlrm_denominacio_classe_2_agregacio",
			"properties.udlrm_grup_creador_agregacio",
			"properties.udlrm_tipus_entitat_serie",
			"properties.udlrm_categoria_serie",
			"properties.udlrm_secuencial_identificador_serie",
			"properties.udlrm_esquema_identificador_serie",
			"properties.udlrm_data_inici_serie",
			"properties.udlrm_data_fi_serie",
			"properties.udlrm_classificacio_acces_serie",
			"properties.udlrm_advertencia_seguretat_serie",
			"properties.udlrm_categoria_advertencia_seguretat_serie",
			"properties.udlrm_sensibilitat_dades_caracter_personal_serie",
			"properties.udlrm_idioma_1_serie",
			"properties.udlrm_idioma_2_serie",
			"properties.udlrm_idioma_3_serie",
			"properties.udlrm_idioma_4_serie",
			"properties.udlrm_valoracio_serie",
			"properties.udlrm_tipus_dictamen_1_serie",
			"properties.udlrm_accio_dictaminada_1_serie",
			"properties.udlrm_tipus_dictamen_2_serie",
			"properties.udlrm_accio_dictaminada_2_serie",
			"properties.udlrm_suport_origen_serie",
			"properties.udlrm_dimensions_fisiques_serie",
			"properties.udlrm_unitats_serie",
			"properties.udlrm_suport_1_serie",
			"properties.udlrm_localitzacio_1_serie",
			"properties.udlrm_localitzacio_2_serie",
			"properties.udlrm_codi_classificacio_serie",
			"properties.udlrm_denominacio_classe_serie",
			"properties.udlrm_grup_creador_serie",
			"properties.udl_nom_natural_organ",
			"properties.udl_nom_natural_institucio",
			"properties.udl_nom_natural_persona"
         ];
         // add the custom meta fields - 'rmc' namespace
         for (var i=0, j=this.options.customFields.length; i<j; i++)
         {
            fields.push("properties.rmc_" + this.options.customFields[i].id);
         }

         // DataSource definition
         var uriSearchResults = Alfresco.constants.PROXY_URI + "slingshot/rmsearch/" + this.options.siteId + "?";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriSearchResults,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            connXhrMode: "queueRequests",
            responseSchema:
            {
                resultsList: "items",
                fields: fields
            }
         });
         
         // setup of the datatable.
         this._setupDataTable();
         
         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      
      _setupDataTable: function RecordsResults_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.RecordsResults class (via the "me" variable).
          */
         var me = this;
         
         /**
          * Record Icon image custom datacell formatter
          *
          * @method renderCellImage
          */
         var renderCellImage = function RecordsResults_renderCellImage(elCell, oRecord, oColumn, oData)
         {
            oColumn.width = 64;
            oColumn.height = 64;
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "height", oColumn.height + "px");
            Dom.setStyle(elCell.parentNode, "text-align", "center");
            
            var record = oRecord.getData(),
            tags = record.tags;
            
            var url = me._getBrowseUrlForRecord(oRecord);
            var imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/record-32.png';
            switch (oRecord.getData("type"))
            {
               case "dod:recordSeries":
                  imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/record-series-32.png';
                  break;
               case "dod:recordCategory":
                  imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/record-category-32.png';
                  break;
               case "rma:recordFolder":
           		  imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/record-folder-32.png';
                  break;
            }
            
            var name = $html(oRecord.getData("name"));
            elCell.innerHTML = '<span><a href="' + encodeURI(url) + '"><img src="' + imageUrl + '" alt="' + name + '" title="' + name + '" /></a></span>';
         };
         
         /**
          * Vital Record indicator custom datacell formatter
          *
          * @method renderCellVitalRecord
          */
         var renderCellVitalRecord = function RecordsResults_renderCellVitalRecord(elCell, oRecord, oColumn, oData)
         {
            var reviewDate = oRecord.getData("properties.rma_reviewAsOf");
            if (reviewDate)
            {
               // found a vital record - is it due for review?
               var html;
               if (Alfresco.util.fromISO8601(reviewDate) < new Date())
               {
                  var imageUrl = Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/warning-16.png';
                  var review = $html(me._msg("label.dueForReview"));
                  html = '<span>' + $html(me._msg("label.yes")) + '&nbsp;<img src="' + imageUrl + '" alt="' + review + '" title="' + review + '"/></span>';
               }
               else
               {
                  html = '<span>' + $html(me._msg("label.yes")) + '</span>';
               }
               elCell.innerHTML = html;
            }
         };
         
         /**
          * URI custom datacell formatter
          *
          * @method renderCellURI
          */
         var renderCellURI = function RecordsResults_renderCellURI(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "white-space", "nowrap");
            var url = me._getBrowseUrlForRecord(oRecord);
            elCell.innerHTML = '<span><a href="' + encodeURI(url) + '">' + oRecord.getData("properties.rma_identifier") + '</a></span>';
         };
         
         /**
          * Date custom datacell formatter
          *
          * @method renderCellDate
          */
         var renderCellDate = function RecordsResults_renderCellDate(elCell, oRecord, oColumn, oData)
         {
            if (oData)
            {
               elCell.innerHTML = Alfresco.util.formatDate(Alfresco.util.fromISO8601(oData));
            }
         };
         
         /**
          * Generic HTML-safe custom datacell formatter
          */
         var renderCellSafeHTML = function renderCellSafeHTML(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = $html(oData);
         };
         
         /**
          * URI custom datacell sorter
          */
         var sortCellURI = function sortCellURI(a, b, desc)
         {
            // identifier format is: YYYY-NNNNNNNNNN where Y=4 digit year and N=zero padded DBID
            var sa = a.getData("properties.rma_identifier");
            var sb = b.getData("properties.rma_identifier");
            var numA = parseInt(sa.substring(0, 4) + sa.substring(5)),
                numB = parseInt(sb.substring(0, 4) + sb.substring(5));
            
            if (desc)
            {
               return (numA < numB ? 1 : (numA > numB ? -1 : 0));
            }
            return (numA < numB ? -1 : (numA > numB ? 1 : 0));
         };
         
         /**
          * Vital Record custom datacell sorter
          */
         var sortCellVitalRecord = function sortCellVitalRecord(a, b, desc)
         {
            var sa = null;
            var sb = null;
            if (a.getData("properties.rma_reviewAsOf"))
            {
               sa = Alfresco.util.fromISO8601(a.getData("properties.rma_reviewAsOf"));
            }
            if (b.getData("properties.rma_reviewAsOf"))
            {
               sb = Alfresco.util.fromISO8601(b.getData("properties.rma_reviewAsOf"));
            }
            if (sa === null && sb === null) return 0;
            if (desc)
            {
               if (sa === null) return -1;
               if (sb === null) return 1;
               return (sa < sb ? 1 : (sa > sb ? -1 : 0));
            }
            if (sa === null) return 1;
            if (sb === null) return -1;
            return (sa < sb ? -1 : (sa > sb ? 1 : 0));
         };
         
         // DataTable column defintions
         var columnDefinitions =
         [
          	{ key: "image", label: me._msg("label.type"), sortable: false, field: "type", sortable: true, formatter: renderCellImage, width: "64px" },
			{ key: "secuencial_identificador_expedient", label: me._msg("label.secuencial_identificador_expedient"), field: "properties.udlrm_secuencial_identificador_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML },
            { key: "grup_creador_expedient", label: me._msg("label.grup_creador_expedient"), field: "properties.udlrm_grup_creador_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML },
			{ key: "data_inici_expedient", label: me._msg("label.data_inici_expedient"), field: "properties.udlrm_data_inici_expedient", sortable: true, resizeable: true, formatter: renderCellDate },
			{ key: "data_fi_expedient", label: me._msg("label.data_fi_expedient"), field: "properties.udlrm_data_fi_expedient", sortable: true, resizeable: true, formatter: renderCellDate },
            { key: "name", label: me._msg("label.name"), field: "name", sortable: true, resizeable: true, formatter: renderCellSafeHTML },
			{ key: "nom_natural_organ", label: me._msg("label.nom_natural_organ"), field: "properties.udl_nom_natural_organ", sortable: true, resizeable: true, formatter: renderCellSafeHTML },
			{ key: "nom_natural_institucio", label: me._msg("label.nom_natural_institucio"), field: "properties.udl_nom_natural_institucio", sortable: true, resizeable: true, formatter: renderCellSafeHTML },			
			{ key: "codi_classificacio_1_expedient", label: me._msg("label.codi_classificacio_1_expedient"), field: "properties.udlrm_codi_classificacio_1_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML },
            { key: "localitzacio_1_expedient", label: me._msg("label.localitzacio_1_expedient"), field: "properties.udlrm_localitzacio_1_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML },
			{ key: "localitzacio_2_expedient", label: me._msg("label.localitzacio_2_expedient"), field: "properties.udlrm_localitzacio_2_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML },
			
			{ key: "identifier", label: me._msg("label.identifier"), sortable: true, sortOptions: {sortFunction: sortCellURI}, resizeable: true, formatter: renderCellURI, hidden: true },
            
            { key: "title", label: me._msg("label.title"), field: "title", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "description", label: me._msg("label.description"), field: "description", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "parentFolder", label: me._msg("label.parentFolder"), field: "parentFolder", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "creator", label: me._msg("label.creator"), field: "createdBy", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "created", label: me._msg("label.created"), field: "createdOn", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
            { key: "modifier", label: me._msg("label.modifier"), field: "modifiedBy", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "modified", label: me._msg("label.modified"), field: "modifiedOn", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
            { key: "author", label: me._msg("label.author"), field: "author", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            
            { key: "originator", label: me._msg("label.originator"), field: "properties.rma_originator", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "dateFiled", label: me._msg("label.dateFiled"), field: "properties.rma_dateFiled", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
            { key: "publicationDate", label: me._msg("label.publicationDate"), field: "properties.rma_publicationDate", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
            { key: "reviewDate", label: me._msg("label.reviewDate"), field: "properties.rma_reviewAsOf", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
            { key: "vitalRecord", label: me._msg("label.vitalRecord"), sortable: true, sortOptions: {sortFunction: sortCellVitalRecord}, resizeable: false, formatter: renderCellVitalRecord, hidden: true },
            { key: "originatingOrganization", label: me._msg("label.originatingOrganization"), field: "properties.rma_originatingOrganization", sortable: true, resizeable: true, hidden: true },
            { key: "mediaType", label: me._msg("label.mediaType"), field: "properties.rma_mediaType", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "format", label: me._msg("label.format"), field: "properties.rma_format", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "dateReceived", label: me._msg("label.dateReceived"), field: "properties.rma_dateReceived", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
            { key: "location", label: me._msg("label.location"), field: "properties.rma_location", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "address", label: me._msg("label.address"), field: "properties.rma_address", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "otherAddress", label: me._msg("label.otherAddress"), field: "properties.rma_otherAddress", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "supplementalMarkingList", label: me._msg("label.supplementalMarkingList"), field: "properties.rmc_supplementalMarkingList", sortable: true, resizeable: true, hidden: true },
            
            { key: "dispositionEvents", label: me._msg("label.dispositionEvents"), field: "properties.rma_recordSearchDispositionEvents", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "dispositionActionName", label: me._msg("label.dispositionActionName"), field: "properties.rma_recordSearchDispositionActionName", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "dispositionActionAsOf", label: me._msg("label.dispositionActionAsOf"), field: "properties.rma_recordSearchDispositionActionAsOf", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
            { key: "dispositionEventsEligible", label: me._msg("label.dispositionEventsEligible"), field: "properties.rma_recordSearchDispositionEventsEligible", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "dispositionPeriod", label: me._msg("label.dispositionPeriod"), field: "properties.rma_recordSearchDispositionPeriod", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "hasDispositionSchedule", label: me._msg("label.hasDispositionSchedule"), field: "properties.rma_recordSearchHasDispositionSchedule", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "dispositionInstructions", label: me._msg("label.dispositionInstructions"), field: "properties.rma_recordSearchDispositionInstructions", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "dispositionAuthority", label: me._msg("label.dispositionAuthority"), field: "properties.rma_recordSearchDispositionAuthority", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "holdReason", label: me._msg("label.holdReason"), field: "properties.rma_recordSearchHoldReason", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "vitalRecordReviewPeriod", label: me._msg("label.vitalRecordReviewPeriod"), field: "properties.rma_recordSearchVitalRecordReviewPeriod", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            
            { key: "scannedFormatVersion", label: me._msg("label.dod.scannedFormatVersion"), field: "properties.dod_scannedFormatVersion", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "resolutionX", label: me._msg("label.dod.resolutionX"), field: "properties.dod_resolutionX", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "resolutionY", label: me._msg("label.dod.resolutionY"), field: "properties.dod_resolutionY", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "scannedBitDepth", label: me._msg("label.dod.scannedBitDepth"), field: "properties.dod_scannedBitDepth", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "producingApplication", label: me._msg("label.dod.producingApplication"), field: "properties.dod_producingApplication", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "producingApplicationVersion", label: me._msg("label.dod.producingApplicationVersion"), field: "properties.dod_producingApplicationVersion", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "pdfVersion", label: me._msg("label.dod.pdfVersion"), field: "properties.dod_pdfVersion", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "creatingApplication", label: me._msg("label.dod.creatingApplication"), field: "properties.dod_creatingApplication", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "documentSecuritySettings", label: me._msg("label.dod.documentSecuritySettings"), field: "properties.dod_documentSecuritySettings", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "caption", label: me._msg("label.dod.caption"), field: "properties.dod_caption", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "photographer", label: me._msg("label.dod.photographer"), field: "properties.dod_photographer", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "copyright", label: me._msg("label.dod.copyright"), field: "properties.dod_copyright", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "bitDepth", label: me._msg("label.dod.bitDepth"), field: "properties.dod_bitDepth", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "imageSizeX", label: me._msg("label.dod.imageSizeX"), field: "properties.dod_imageSizeX", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "imageSizeY", label: me._msg("label.dod.imageSizeY"), field: "properties.dod_imageSizeY", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "imageSource", label: me._msg("label.dod.imageSource"), field: "properties.dod_imageSource", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "compression", label: me._msg("label.dod.compression"), field: "properties.dod_compression", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "iccIcmProfile", label: me._msg("label.dod.iccIcmProfile"), field: "properties.dod_iccIcmProfile", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "exifInformation", label: me._msg("label.dod.exifInformation"), field: "properties.dod_exifInformation", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "webFileName", label: me._msg("label.dod.webFileName"), field: "properties.dod_webFileName", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "webPlatform", label: me._msg("label.dod.webPlatform"), field: "properties.dod_webPlatform", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "webSiteName", label: me._msg("label.dod.webSiteName"), field: "properties.dod_webSiteName", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "webSiteURL", label: me._msg("label.dod.webSiteURL"), field: "properties.dod_webSiteURL", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "captureMethod", label: me._msg("label.dod.captureMethod"), field: "properties.dod_captureMethod", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "captureDate", label: me._msg("label.dod.captureDate"), field: "properties.dod_captureDate", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "contact", label: me._msg("label.dod.contact"), field: "properties.dod_contact", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
            { key: "contentManagementSystem", label: me._msg("label.dod.contentManagementSystem"), field: "properties.dod_contentManagementSystem", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "tipus_entitat_documentSimple", label: me._msg("label.tipus_entitat_documentSimple"), field: "properties.udlrm_tipus_entitat_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "tipus_documental_documentSimple", label: me._msg("label.tipus_documental_documentSimple"), field: "properties.udlrm_tipus_documental_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "categoria_documentSimple", label: me._msg("label.categoria_documentSimple"), field: "properties.udlrm_categoria_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "secuencial_identificador_documentSimple", label: me._msg("label.secuencial_identificador_documentSimple"), field: "properties.udlrm_secuencial_identificador_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "esquema_identificador_documentSimple", label: me._msg("label.esquema_identificador_documentSimple"), field: "properties.udlrm_esquema_identificador_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "data_inici_documentSimple", label: me._msg("label.data_inici_documentSimple"), field: "properties.udlrm_data_inici_documentSimple", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
        	{ key: "data_fi_documentSimple", label: me._msg("label.data_fi_documentSimple"), field: "properties.udlrm_data_fi_documentSimple", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
        	{ key: "data_registre_entrada_documentSimple", label: me._msg("label.data_registre_entrada_documentSimple"), field: "properties.udlrm_data_registre_entrada_documentSimple", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
        	{ key: "data_registre_sortida_documentSimple", label: me._msg("label.data_registre_sortida_documentSimple"), field: "properties.udlrm_data_registre_sortida_documentSimple", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
        	{ key: "classificacio_acces_documentSimple", label: me._msg("label.classificacio_acces_documentSimple"), field: "properties.udlrm_classificacio_acces_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "advertencia_seguretat_documentSimple", label: me._msg("label.advertencia_seguretat_documentSimple"), field: "properties.udlrm_advertencia_seguretat_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "categoria_advertencia_seguretat_documentSimple", label: me._msg("label.categoria_advertencia_seguretat_documentSimple"), field: "properties.udlrm_categoria_advertencia_seguretat_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "sensibilitat_dades_caracter_personal_documentSimple", label: me._msg("label.sensibilitat_dades_caracter_personal_documentSimple"), field: "properties.udlrm_sensibilitat_dades_caracter_personal_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "verificacio_integritat_algorisme_documentSimple", label: me._msg("label.verificacio_integritat_algorisme_documentSimple"), field: "properties.udlrm_verificacio_integritat_algorisme_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "verificacio_integritat_valor_documentSimple", label: me._msg("label.verificacio_integritat_valor_documentSimple"), field: "properties.udlrm_verificacio_integritat_valor_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "idioma_1_documentSimple", label: me._msg("label.idioma_1_documentSimple"), field: "properties.udlrm_idioma_1_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "idioma_2_documentSimple", label: me._msg("label.idioma_2_documentSimple"), field: "properties.udlrm_idioma_2_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "valoracio_documentSimple", label: me._msg("label.valoracio_documentSimple"), field: "properties.udlrm_valoracio_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "tipus_dictamen_1_documentSimple", label: me._msg("label.tipus_dictamen_1_documentSimple"), field: "properties.udlrm_tipus_dictamen_1_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "accio_dictaminada_1_documentSimple", label: me._msg("label.accio_dictaminada_1_documentSimple"), field: "properties.udlrm_accio_dictaminada_1_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "tipus_dictamen_2_documentSimple", label: me._msg("label.tipus_dictamen_2_documentSimple"), field: "properties.udlrm_tipus_dictamen_2_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "accio_dictaminada_2_documentSimple", label: me._msg("label.accio_dictaminada_2_documentSimple"), field: "properties.udlrm_accio_dictaminada_2_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "suport_origen_documentSimple", label: me._msg("label.suport_origen_documentSimple"), field: "properties.udlrm_suport_origen_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "versio_format_documentSimple", label: me._msg("label.versio_format_documentSimple"), field: "properties.udlrm_versio_format_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "nom_aplicacio_creacio_documentSimple", label: me._msg("label.nom_aplicacio_creacio_documentSimple"), field: "properties.udlrm_nom_aplicacio_creacio_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "versio_aplicacio_creacio_documentSimple", label: me._msg("label.versio_aplicacio_creacio_documentSimple"), field: "properties.udlrm_versio_aplicacio_creacio_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "registre_formats_documentSimple", label: me._msg("label.registre_formats_documentSimple"), field: "properties.udlrm_registre_formats_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "resolucio_documentSimple", label: me._msg("label.resolucio_documentSimple"), field: "properties.udlrm_resolucio_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "dimensions_fisiques_documentSimple", label: me._msg("label.dimensions_fisiques_documentSimple"), field: "properties.udlrm_dimensions_fisiques_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "unitats_documentSimple", label: me._msg("label.unitats_documentSimple"), field: "properties.udlrm_unitats_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "suport_1_documentSimple", label: me._msg("label.suport_1_documentSimple"), field: "properties.udlrm_suport_1_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "localitzacio_1_documentSimple", label: me._msg("label.localitzacio_1_documentSimple"), field: "properties.udlrm_localitzacio_1_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "localitzacio_2_documentSimple", label: me._msg("label.localitzacio_2_documentSimple"), field: "properties.udlrm_localitzacio_2_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "denominacio_estat_documentSimple", label: me._msg("label.denominacio_estat_documentSimple"), field: "properties.udlrm_denominacio_estat_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "tipus_copia_documentSimple", label: me._msg("label.tipus_copia_documentSimple"), field: "properties.udlrm_tipus_copia_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "motiu_documentSimple", label: me._msg("label.motiu_documentSimple"), field: "properties.udlrm_motiu_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "codi_classificacio_1_documentSimple", label: me._msg("label.codi_classificacio_1_documentSimple"), field: "properties.udlrm_codi_classificacio_1_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "denominacio_classe_1_documentSimple", label: me._msg("label.denominacio_classe_1_documentSimple"), field: "properties.udlrm_denominacio_classe_1_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "codi_classificacio_2_documentSimple", label: me._msg("label.codi_classificacio_2_documentSimple"), field: "properties.udlrm_codi_classificacio_2_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "denominacio_classe_2_documentSimple", label: me._msg("label.denominacio_classe_2_documentSimple"), field: "properties.udlrm_denominacio_classe_2_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	{ key: "grup_creador_documentSimple", label: me._msg("label.grup_creador_documentSimple"), field: "properties.udlrm_grup_creador_documentSimple", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
        	
			{ key: "descripcio_expedient", label: me._msg("label.descripcio_expedient"), field: "properties.udlrm_descripcio_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "tipus_entitat_expedient", label: me._msg("label.tipus_entitat_expedient"), field: "properties.udlrm_tipus_entitat_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "categoria_expedient", label: me._msg("label.categoria_expedient"), field: "properties.udlrm_categoria_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "esquema_identificador_expedient", label: me._msg("label.esquema_identificador_expedient"), field: "properties.udlrm_esquema_identificador_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "classificacio_acces_expedient", label: me._msg("label.classificacio_acces_expedient"), field: "properties.udlrm_classificacio_acces_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "advertencia_seguretat_expedient", label: me._msg("label.advertencia_seguretat_expedient"), field: "properties.udlrm_advertencia_seguretat_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "categoria_advertencia_seguretat_expedient", label: me._msg("label.categoria_advertencia_seguretat_expedient"), field: "properties.udlrm_categoria_advertencia_seguretat_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "sensibilitat_dades_caracter_personal_expedient", label: me._msg("label.sensibilitat_dades_caracter_personal_expedient"), field: "properties.udlrm_sensibilitat_dades_caracter_personal_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "condicions_acces_1_expedient", label: me._msg("label.condicions_acces_1_expedient"), field: "properties.udlrm_condicions_acces_1_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "condicions_acces_2_expedient", label: me._msg("label.condicions_acces_2_expedient"), field: "properties.udlrm_condicions_acces_2_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "tipus_acces_1_expedient", label: me._msg("label.tipus_acces_1_expedient"), field: "properties.udlrm_tipus_acces_1_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "tipus_acces_2_expedient", label: me._msg("label.tipus_acces_2_expedient"), field: "properties.udlrm_tipus_acces_2_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "idioma_1_expedient", label: me._msg("label.idioma_1_expedient"), field: "properties.udlrm_idioma_1_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "idioma_2_expedient", label: me._msg("label.idioma_2_expedient"), field: "properties.udlrm_idioma_2_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "idioma_3_expedient", label: me._msg("label.idioma_3_expedient"), field: "properties.udlrm_idioma_3_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "idioma_4_expedient", label: me._msg("label.idioma_4_expedient"), field: "properties.udlrm_idioma_4_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "valoracio_expedient", label: me._msg("label.valoracio_expedient"), field: "properties.udlrm_valoracio_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "tipus_dictamen_1_expedient", label: me._msg("label.tipus_dictamen_1_expedient"), field: "properties.udlrm_tipus_dictamen_1_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "accio_dictaminada_1_expedient", label: me._msg("label.accio_dictaminada_1_expedient"), field: "properties.udlrm_accio_dictaminada_1_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "tipus_dictamen_2_expedient", label: me._msg("label.tipus_dictamen_2_expedient"), field: "properties.udlrm_tipus_dictamen_2_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "accio_dictaminada_2_expedient", label: me._msg("label.accio_dictaminada_2_expedient"), field: "properties.udlrm_accio_dictaminada_2_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "suport_origen_expedient", label: me._msg("label.suport_origen_expedient"), field: "properties.udlrm_suport_origen_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "dimensions_fisiques_expedient", label: me._msg("label.dimensions_fisiques_expedient"), field: "properties.udlrm_dimensions_fisiques_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "quantitat_expedient", label: me._msg("label.quantitat_expedient"), field: "properties.udlrm_quantitat_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "unitats_expedient", label: me._msg("label.unitats_expedient"), field: "properties.udlrm_unitats_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "suport_1_expedient", label: me._msg("label.suport_1_expedient"), field: "properties.udlrm_suport_1_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },			
			{ key: "denominacio_classe_1_expedient", label: me._msg("label.denominacio_classe_1_expedient"), field: "properties.udlrm_denominacio_classe_1_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "codi_classificacio_2_expedient", label: me._msg("label.codi_classificacio_2_expedient"), field: "properties.udlrm_codi_classificacio_2_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "denominacio_classe_2_expedient", label: me._msg("label.denominacio_classe_2_expedient"), field: "properties.udlrm_denominacio_classe_2_expedient", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			
			{ key: "descripcio_agregacio", label: me._msg("label.descripcio_agregacio"), field: "properties.udlrm_descripcio_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "tipus_entitat_agregacio", label: me._msg("label.tipus_entitat_agregacio"), field: "properties.udlrm_tipus_entitat_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "categoria_agregacio", label: me._msg("label.categoria_agregacio"), field: "properties.udlrm_categoria_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "secuencial_identificador_agregacio", label: me._msg("label.secuencial_identificador_agregacio"), field: "properties.udlrm_secuencial_identificador_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "esquema_identificador_agregacio", label: me._msg("label.esquema_identificador_agregacio"), field: "properties.udlrm_esquema_identificador_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "data_inici_agregacio", label: me._msg("label.data_inici_agregacio"), field: "properties.udlrm_data_inici_agregacio", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
			{ key: "data_fi_agregacio", label: me._msg("label.data_fi_agregacio"), field: "properties.udlrm_data_fi_agregacio", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
			{ key: "classificacio_acces_agregacio", label: me._msg("label.classificacio_acces_agregacio"), field: "properties.udlrm_classificacio_acces_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "advertencia_seguretat_agregacio", label: me._msg("label.advertencia_seguretat_agregacio"), field: "properties.udlrm_advertencia_seguretat_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "categoria_advertencia_seguretat_agregacio", label: me._msg("label.categoria_advertencia_seguretat_agregacio"), field: "properties.udlrm_categoria_advertencia_seguretat_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "sensibilitat_dades_caracter_personal_agregacio", label: me._msg("label.sensibilitat_dades_caracter_personal_agregacio"), field: "properties.udlrm_sensibilitat_dades_caracter_personal_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "condicions_acces_1_agregacio", label: me._msg("label.condicions_acces_1_agregacio"), field: "properties.udlrm_condicions_acces_1_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "condicions_acces_2_agregacio", label: me._msg("label.condicions_acces_2_agregacio"), field: "properties.udlrm_condicions_acces_2_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "tipus_acces_1_agregacio", label: me._msg("label.tipus_acces_1_agregacio"), field: "properties.udlrm_tipus_acces_1_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "tipus_acces_2_agregacio", label: me._msg("label.tipus_acces_2_agregacio"), field: "properties.udlrm_tipus_acces_2_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "idioma_1_agregacio", label: me._msg("label.idioma_1_agregacio"), field: "properties.udlrm_idioma_1_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "idioma_2_agregacio", label: me._msg("label.idioma_2_agregacio"), field: "properties.udlrm_idioma_2_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "idioma_3_agregacio", label: me._msg("label.idioma_3_agregacio"), field: "properties.udlrm_idioma_3_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "idioma_4_agregacio", label: me._msg("label.idioma_4_agregacio"), field: "properties.udlrm_idioma_4_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "valoracio_agregacio", label: me._msg("label.valoracio_agregacio"), field: "properties.udlrm_valoracio_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "tipus_dictamen_1_agregacio", label: me._msg("label.tipus_dictamen_1_agregacio"), field: "properties.udlrm_tipus_dictamen_1_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "accio_dictaminada_1_agregacio", label: me._msg("label.accio_dictaminada_1_agregacio"), field: "properties.udlrm_accio_dictaminada_1_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "tipus_dictamen_2_agregacio", label: me._msg("label.tipus_dictamen_2_agregacio"), field: "properties.udlrm_tipus_dictamen_2_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "accio_dictaminada_2_agregacio", label: me._msg("label.accio_dictaminada_2_agregacio"), field: "properties.udlrm_accio_dictaminada_2_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "suport_origen_agregacio", label: me._msg("label.suport_origen_agregacio"), field: "properties.udlrm_suport_origen_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "dimensions_fisiques_agregacio", label: me._msg("label.dimensions_fisiques_agregacio"), field: "properties.udlrm_dimensions_fisiques_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "unitats_agregacio", label: me._msg("label.unitats_agregacio"), field: "properties.udlrm_unitats_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "codi_classificacio_1_agregacio", label: me._msg("label.codi_classificacio_1_agregacio"), field: "properties.udlrm_codi_classificacio_1_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "denominacio_classe_1_agregacio", label: me._msg("label.denominacio_classe_1_agregacio"), field: "properties.udlrm_denominacio_classe_1_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "codi_classificacio_2_agregacio", label: me._msg("label.codi_classificacio_2_agregacio"), field: "properties.udlrm_codi_classificacio_2_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "denominacio_classe_2_agregacio", label: me._msg("label.denominacio_classe_2_agregacio"), field: "properties.udlrm_denominacio_classe_2_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "grup_creador_agregacio", label: me._msg("label.grup_creador_agregacio"), field: "properties.udlrm_grup_creador_agregacio", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			
			{ key: "tipus_entitat_serie", label: me._msg("label.tipus_entitat_serie"), field: "udlrm_tipus_entitat_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "categoria_serie", label: me._msg("label.categoria_serie"), field: "udlrm_categoria_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "secuencial_identificador_serie", label: me._msg("label.secuencial_identificador_serie"), field: "properties.udlrm_secuencial_identificador_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "esquema_identificador_serie", label: me._msg("label.esquema_identificador_serie"), field: "properties.udlrm_esquema_identificador_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "data_inici_serie", label: me._msg("label.data_inici_serie"), field: "properties.udlrm_data_inici_serie", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
			{ key: "data_fi_serie", label: me._msg("label.data_fi_serie"), field: "properties.udlrm_data_fi_serie", sortable: true, resizeable: true, formatter: renderCellDate, hidden: true },
			{ key: "classificacio_acces_serie", label: me._msg("label.classificacio_acces_serie"), field: "properties.udlrm_classificacio_acces_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "advertencia_seguretat_serie", label: me._msg("label.advertencia_seguretat_serie"), field: "properties.udlrm_advertencia_seguretat_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "categoria_advertencia_seguretat_serie", label: me._msg("label.categoria_advertencia_seguretat_serie"), field: "properties.udlrm_categoria_advertencia_seguretat_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "sensibilitat_dades_caracter_personal_serie", label: me._msg("label.sensibilitat_dades_caracter_personal_serie"), field: "properties.udlrm_sensibilitat_dades_caracter_personal_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "idioma_1_serie", label: me._msg("label.idioma_1_serie"), field: "properties.udlrm_idioma_1_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "idioma_2_serie", label: me._msg("label.idioma_2_serie"), field: "properties.udlrm_idioma_2_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "idioma_3_serie", label: me._msg("label.idioma_3_serie"), field: "properties.udlrm_idioma_3_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "idioma_4_serie", label: me._msg("label.idioma_4_serie"), field: "properties.udlrm_idioma_4_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "valoracio_serie", label: me._msg("label.valoracio_serie"), field: "properties.udlrm_valoracio_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "tipus_dictamen_1_serie", label: me._msg("label.tipus_dictamen_1_serie"), field: "properties.udlrm_tipus_dictamen_1_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "accio_dictaminada_1_serie", label: me._msg("label.accio_dictaminada_1_serie"), field: "properties.udlrm_accio_dictaminada_1_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "tipus_dictamen_2_serie", label: me._msg("label.tipus_dictamen_2_serie"), field: "properties.udlrm_tipus_dictamen_2_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "accio_dictaminada_2_serie", label: me._msg("label.accio_dictaminada_2_serie"), field: "properties.udlrm_accio_dictaminada_2_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "suport_origen_serie", label: me._msg("label.suport_origen_serie"), field: "properties.udlrm_suport_origen_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "dimensions_fisiques_serie", label: me._msg("label.dimensions_fisiques_serie"), field: "properties.udlrm_dimensions_fisiques_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "unitats_serie", label: me._msg("label.unitats_serie"), field: "properties.udlrm_unitats_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "suport_1_serie", label: me._msg("label.suport_1_serie"), field: "properties.udlrm_suport_1_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "localitzacio_1_serie", label: me._msg("label.localitzacio_1_serie"), field: "properties.udlrm_localitzacio_1_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "localitzacio_2_serie", label: me._msg("label.localitzacio_2_serie"), field: "properties.udlrm_localitzacio_2_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "codi_classificacio_serie", label: me._msg("label.codi_classificacio_serie"), field: "properties.udlrm_codi_classificacio_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "denominacio_classe_serie", label: me._msg("label.denominacio_classe_serie"), field: "properties.udlrm_denominacio_classe_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			{ key: "grup_creador_serie", label: me._msg("label.grup_creador_serie"), field: "properties.udlrm_grup_creador_serie", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true },
			
			{ key: "nom_natural_persona", label: me._msg("label.nom_natural_persona"), field: "properties.udl_nom_natural_persona", sortable: true, resizeable: true, formatter: renderCellSafeHTML, hidden: true }
         ];
         
         // Add the custom metadata columns
         for (var i=0, j=this.options.customFields.length; i<j; i++)
         {
            var prop = this.options.customFields[i];
            columnDefinitions.push(
               { key: prop.id, label: prop.title, field: "properties.rmc_" + prop.id, sortable: true, resizeable: true, hidden: true }
            );
         }
         
         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            draggableColumns: true,
            initialLoad: false,
            MSG_EMPTY: me._msg("message.empty")
         });
         
         // show initial message
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         this.widgets.dataTable.render();
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function RecordsResults_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  // update message area if any
                  var el = Dom.get(me.id + "-itemcount");
                  if (el)
                  {
                     el.innerHTML = me._msg("message.error");
                  }
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  me.widgets.dataTable.set("MSG_ERROR", response.message);
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors(me.widgets.dataTable);
               }
            }
            else if (oResponse.results)
            {
               // update the results count
               me.hasMoreResults = (oResponse.results.length > me.options.maxResults);
               if (me.hasMoreResults)
               {
                  oResponse.results = oResponse.results.slice(0, me.options.maxResults);
               }
               me.resultsCount = oResponse.results.length;
               me.renderLoopSize = 32;
               
               // collect noderefs (for export if required)
               me.resultNodeRefs = new Array(me.resultsCount);
               for (var i=0, j=me.resultsCount; i<j; i++)
               {
                  me.resultNodeRefs[i] = oResponse.results[i].nodeRef;
               }
               
               // update message area if any
               var el = Dom.get(me.id + "-itemcount");
               if (el)
               {
                  if (me.resultsCount === 0)
                  {
                     el.innerHTML = me._msg("message.empty");
                     me.widgets.dataTable.set("MSG_EMPTY", "");
                  }
                  else
                  {
                     el.innerHTML = me._msg("message.foundresults", me.resultsCount);
                  }
               }
               
               YAHOO.Bubbling.fire("searchComplete", {count: me.resultsCount});
            }
            
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         };
      },

      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function RecordsResults__setDefaultDataTableErrors(dataTable)
      {
         dataTable.set("MSG_EMPTY", Alfresco.util.message("message.empty", "Alfresco.RecordsResults"));
         dataTable.set("MSG_ERROR", Alfresco.util.message("message.error", "Alfresco.RecordsResults"));
      },
      
      /**
       * Initialises a number of paired sort order asc/des menu controls.
       * 
       * @method _setupSortControls
       * @param count {string} Number of control pairs to initialise
       */
      _setupSortControls: function RecordResults__setupSortControls(count)
      {
         var me = this;
         for (var i=0; i<count; i++)
         {
            this.widgets.sortMenus[i] = new YAHOO.widget.Button(this.id + "-sort" + (i+1),
            {
               type: "menu",
               menu: this.id + "-sort" + (i+1) + "-menu",
               lazyloadmenu: false
            });
            this.widgets.sortMenus[i].getMenu().subscribe("click", function(p_sType, p_aArgs, index)
            {
               var menuItem = p_aArgs[1];
               if (menuItem)
               {
                  me.widgets.sortMenus[index].set("label", menuItem.cfg.getProperty("text"));
                  me.sortby[index].field = menuItem.value;
               }
            }, i);
            this.widgets.sortOrderMenus[i] = new YAHOO.widget.Button(this.id + "-sort" + (i+1) + "-order",
            {
               type: "menu",
               menu: this.id + "-sort" + (i+1) + "-order-menu",
               lazyloadmenu: false
            });
            this.widgets.sortOrderMenus[i].getMenu().subscribe("click", function(p_sType, p_aArgs, index)
            {
               var menuItem = p_aArgs[1];
               if (menuItem)
               {
                  me.widgets.sortOrderMenus[index].set("label", menuItem.cfg.getProperty("text"));
                  me.sortby[index].order = menuItem.value;
               }
            }, i);
         }
      },
      
      /**
       * Clears results before a search is performed
       *
       * @method _clearSearchResults
       */
      _clearSearchResults: function RecordsResults__clearSearchResults()
      {
         // empty results table
         this.widgets.dataTable.set("MSG_EMPTY", this._msg("message.loading"));
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
      },

      /**
       * Updates document list by calling data webscript with current site and path
       * 
       * @method _performSearch
       * @param query {string} Query to execute
       */
      _performSearch: function RecordsResults__performSearch(query)
      {
         // empty results table
         this._clearSearchResults();
         
         // update the ui to show that a search is on-going
         this.widgets.dataTable.render();
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }
         
         function failureHandler(sRequest, oResponse)
         {
            if (oResponse.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload();
            }
            else
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  this.widgets.dataTable.set("MSG_ERROR", $html(response.message));
                  this.widgets.dataTable.showTableMessage($html(response.message), YAHOO.widget.DataTable.CLASS_ERROR);
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors(this.widgets.dataTable);
                  this.widgets.dataTable.render();
               }
            }
         }
         
         this.widgets.dataSource.sendRequest(this._buildSearchParams(query),
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
      },
      
      /**
       * Build URI sort parameter string for search.
       * 
       * @method _buildSortParam
       * @return {string} The sort parameter string for search.
       */
      _buildSortParam: function RecordsResults__buildSortParam()
      {
         // encode and pack the sort fields as "property/dir" i.e. "cm:name/asc"
         // comma separated between each property and direction pair
         var sort = "";
         for (var i in this.sortby)
         {
            var field = this.sortby[i].field;
            if (field && field.length !== 0)
            {
               if (sort.length !== 0)
               {
                  sort += ",";
               }
               sort += field + '/' + this.sortby[i].order;
            }
         }
         return sort;
      },

      /**
       * Build URI parameter string for search JSON data webscript.
       * 
       * @method _buildSearchParams
       * @param query {string} Query to execute
       * @return {string} The parameters string for search.
       */
      _buildSearchParams: function RecordsResults__buildSearchParams(query)
      {
         // build the parameter string and encode each value
         var params = YAHOO.lang.substitute("site={site}&query={query}&sortby={sortby}&maxResults={maxResults}",
         {
            site: encodeURIComponent(this.options.siteId),
            query : query !== null ? encodeURIComponent(query) : "",
            sortby : encodeURIComponent(this._buildSortParam()),
            maxResults : this.options.maxResults + 1 // to be able to know whether we got more results
         });
         
         return params;
      },
      
      /**
       * Constructs the browse url for a given record.
       */
      _getBrowseUrlForRecord: function _getBrowseUrlForRecord(oRecord)
      {
         var url = "#";
         if (oRecord.getData("browseUrl") !== undefined)
         {
            url = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/" + oRecord.getData("browseUrl");
         }
         return url;
      }
   };
})();