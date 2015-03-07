<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<h:panelGrid columns="1">
<h:outputText value="#{PlantillaWizard.result}" rendered="#{PlantillaWizard.error == null}" style="color:blue; font-weight:bold;" />
<h:outputText value="#{PlantillaWizard.error}" rendered="#{PlantillaWizard.error != null}" style="color:red; font-weight:bold;"/>
</h:panelGrid>

<h:panelGrid width="100%" columns="1" style="font-weight:bold;background:#A9D0F5;">
<h:outputLabel value="Propietats plantilla" style="font-weight:bold;"/>
</h:panelGrid>

<h:panelGrid columns="1">
<h:outputText value="Nom plantilla: "/>
<h:inputText id="nombrePlantilla" value="#{PlantillaWizard.nombrePlantilla}" size="35" maxlength="1024" />

<h:outputText value="Tipus documental: "/>
<h:selectOneMenu id="tipusDocumental" value="#{PlantillaWizard.tipusDocumental}">
				        <f:selectItem id="item1" itemLabel="Expedient" itemValue="Expedient" />
				        <f:selectItem id="item2" itemLabel="Document"  itemValue="Document" />
				        <f:selectItem id="item3" itemLabel="Signatura" itemValue="Signatura" />
</h:selectOneMenu>

<h:dataTable id="results" border="0" value="#{PlantillaWizard.items}" var="node" styleClass="nodeBrowserTable" width="100%">
<h:column>
        <h:selectBooleanCheckbox value="#{node.addPlantilla}" />
    </h:column>
<h:column>
<f:facet name="header">
<h:outputText value="Nom vocabulari"/>
</f:facet>
	<h:outputText value="#{node.nombreVoc}"/>
</h:column>
</h:dataTable>
<h:commandButton id="nou-btn" value="Guarda" styleClass="wizardButton" 
	         		actionListener="#{PlantillaWizard.novaEditPlantilla}"/>
</h:panelGrid>

<h:panelGrid width="100%" columns="1" style="font-weight:bold;background:#A9D0F5;">
<h:outputLabel value="Llistat plantilles" style="font-weight:bold;"/>
</h:panelGrid>

<h:dataTable id="resultsPlantillas" border="0" value="#{PlantillaWizard.nodes}" var="nodeAtr" styleClass="nodeBrowserTable" width="100%">
	<h:column>
	<f:facet name="header">
	<h:outputText value="Nom plantilla"/>
	</f:facet>
		<h:outputText value="#{nodeAtr.nombrePlantilla}"/>
	</h:column>
	<h:column>
	<f:facet name="header">
	<h:outputText value="Tipus documental"/>
	</f:facet>
		<h:outputText value="#{nodeAtr.tipoDocumental}"/>
	</h:column>
	<h:column>
	<h:outputText value="">
	<a:actionLink id="edit" value="Edita plantilla" actionListener="#{PlantillaWizard.editPlantilla}" showLink="false" image="/images/icons/edit_properties.gif" >
	 	<f:param name="editPlantilla" value="#{nodeAtr.nombrePlantilla}" />
	 </a:actionLink>
	 </h:outputText>
	</h:column>
	<h:column>
	<h:outputText value="">
	<a:actionLink id="delete" value="Elimina plantilla" actionListener="#{PlantillaWizard.deletePlantilles}" showLink="false" image="/images/icons/delete.gif" >
	 	<f:param name="nomPlantilla" value="#{nodeAtr.nombrePlantilla}" />
	 </a:actionLink>
	 </h:outputText>
	</h:column>
</h:dataTable>