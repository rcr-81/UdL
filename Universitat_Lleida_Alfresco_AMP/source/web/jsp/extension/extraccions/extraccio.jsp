<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<h:panelGrid columns="1">
<h:outputText value="#{ExtraccioWizard.result}" rendered="#{ExtraccioWizard.error == null}" style="color:blue; font-weight:bold;" />
<h:outputText value="#{ExtraccioWizard.error}" rendered="#{ExtraccioWizard.error != null}" style="color:red; font-weight:bold;"/>
</h:panelGrid>

<h:panelGrid width="100%" columns="1" style="font-weight:bold;background:#A9D0F5;">
<h:outputLabel value="Propietats extracció" style="font-weight:bold;"/>
</h:panelGrid>

<h:panelGrid columns="1">
	<h:outputText value="Nom extracció: "/>
	<h:inputText value="#{ExtraccioWizard.nombreExtraccio}" size="35" maxlength="1024" />
</h:panelGrid>

<h:panelGrid width="100%" columns="1" style="font-weight:bold;background:#A9D0F5;">
<h:outputLabel value="Propietats tipus documentals" style="font-weight:bold;"/>
</h:panelGrid>

<h:panelGrid columns="3">
	<h:panelGrid>
		<h:outputLabel value="Expedients" style="font-weight:bold;"/>
		<h:selectBooleanCheckbox value="#{ExtraccioWizard.checkboxExpedient}"/>
	      <h:outputLabel value="Tipus documental"/>
	      <h:selectOneMenu value="#{ExtraccioWizard.tipusDocumentalExp}">
   				<f:selectItems value="#{ExtraccioWizard.tipusDocumentals}" />
		</h:selectOneMenu>
	    <h:outputLabel value="Criteri selecció"/>
	    <h:inputTextarea rows="5" cols="35" value="#{ExtraccioWizard.selExp}"/>
	    <h:commandButton value="Valida" styleClass="wizardButton" 
	         	actionListener="#{ExtraccioWizard.validarExp}" />
	    <h:outputLabel value="Plantilla"/>
	     <h:selectOneMenu value="#{ExtraccioWizard.plantillaExp}">
   				<f:selectItems value="#{ExtraccioWizard.listPlantilla}" />
		</h:selectOneMenu>
		</h:panelGrid>
		<h:panelGrid>
		<h:outputLabel value="Documents" style="font-weight:bold;"/>
		<h:selectBooleanCheckbox value="#{ExtraccioWizard.checkboxDocument}"/>
	      <h:outputLabel value="Tipus documental"/>
	      <h:selectOneMenu value="#{ExtraccioWizard.tipusDocumentalDoc}">
   				<f:selectItems value="#{ExtraccioWizard.tipusDocumentals}" />
		</h:selectOneMenu>
	    <h:outputLabel value="Criteri selecció"/>
	    <h:inputTextarea rows="5" cols="35" value="#{ExtraccioWizard.selDoc}"/>
	    <h:commandButton value="Valida" styleClass="wizardButton" 
	         	actionListener="#{ExtraccioWizard.validarDoc}" />
	    <h:outputLabel value="Plantilla"/>
	     <h:selectOneMenu value="#{ExtraccioWizard.plantillaDoc}">
   				<f:selectItems value="#{ExtraccioWizard.listPlantillaDoc}" />
		</h:selectOneMenu>
		</h:panelGrid>
		<h:panelGrid>

		<h:outputLabel value="Signatures" style="font-weight:bold;"/>
	      <h:outputLabel value="Tipus documental"/>
	      <h:selectOneMenu value="#{ExtraccioWizard.tipusDocumentalSign}">
   				<f:selectItems value="#{ExtraccioWizard.tipusDocumentals}" />
		</h:selectOneMenu>
	    <h:outputLabel value="Criteri selecció"/>
	    <h:inputTextarea rows="5" cols="35" value="#{ExtraccioWizard.selSign}"/>
	    <h:commandButton value="Valida" styleClass="wizardButton" 
	         	actionListener="#{ExtraccioWizard.validarSign}" />
	    <h:outputLabel value="Plantilla"/>
	     <h:selectOneMenu value="#{ExtraccioWizard.plantillaSign}">
   				<f:selectItems value="#{ExtraccioWizard.listPlantillaSign}" />
		</h:selectOneMenu>
		</h:panelGrid>
		<h:commandButton id="nou-btn" value="Guarda" styleClass="wizardButton" 
	         		actionListener="#{ExtraccioWizard.nouEditaExtraccioButton}"/>
</h:panelGrid>

<h:panelGrid width="100%" columns="1" style="font-weight:bold;background:#A9D0F5;">
<h:outputLabel value="Llistat extraccions" style="font-weight:bold;"/>
</h:panelGrid>

<h:dataTable id="resultsPlantillas" border="0" value="#{ExtraccioWizard.nodes}" var="node" styleClass="nodeBrowserTable" width="100%">
	<h:column>
	<f:facet name="header">
	<h:outputText value="Nom extracció"/>
	</f:facet>
		<h:outputText value="#{node.nombreExtraccio}"/>
	</h:column>
	<h:column>
	<f:facet name="header">
	<h:outputText value="Expedient"/>
	</f:facet>
		<h:outputText value="#{node.expedient}"/>
	</h:column>
	<h:column>
	<f:facet name="header">
	<h:outputText value="Document"/>
	</f:facet>
		<h:outputText value="#{node.document}"/>
	</h:column>
		<h:column>
	<f:facet name="header">
	<h:outputText value="Signatura"/>
	</f:facet>
		<h:outputText value="#{node.signatura}"/>
	</h:column>
	<h:column>
	<h:outputText value="">
	<a:actionLink id="edit" actionListener="#{ExtraccioWizard.editExtraccio}" value="Edita extracció" showLink="false" image="/images/icons/edit_properties.gif" >
	 	<f:param name="editExtraccio" value="#{node.nombreExtraccio}" />
	 </a:actionLink>
	 </h:outputText>
	</h:column>
	<h:column>
	<h:outputText value="">
	<a:actionLink id="delete" value="Elimina extracció" actionListener="#{ExtraccioWizard.deleteExtraccions}" showLink="false" image="/images/icons/delete.gif" >
	 	<f:param name="nomExtraccio" value="#{node.nombreExtraccio}" />
	 </a:actionLink>
	 </h:outputText>
	</h:column>
</h:dataTable>