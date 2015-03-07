<%--
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
--%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<h:panelGrid columns="1">
<h:outputText value="#{VocabulariWizard.result}" rendered="#{VocabulariWizard.error == null && VocabulariWizard.errorAtr == null}" style="color:blue; font-weight:bold;" />
<h:outputText value="#{VocabulariWizard.error}" rendered="#{VocabulariWizard.error != null}" style="color:red; font-weight:bold;"/>
<h:outputText value="#{VocabulariWizard.errorAtr}" rendered="#{VocabulariWizard.errorAtr != null}" style="color:red; font-weight:bold;"/>
</h:panelGrid>

<h:panelGrid width="100%" columns="1" rendered="#{VocabulariWizard.error == null}" style="font-weight:bold;background:#A9D0F5;">
	<h:outputLabel value="Propietats atributs" style="font-weight:bold;"/>
</h:panelGrid>

<h:panelGrid columns="2" rendered="#{VocabulariWizard.error == null}">  
	<h:panelGrid>
	         <h:outputLabel value="Nom"/>
	         <h:inputText id="nombreAtr" size="35" value="#{VocabulariWizard.nombreAtr}"/>
	         <h:outputLabel value="Valor"/>
	         <h:inputTextarea id="valor" rows="2" cols="70" value="#{VocabulariWizard.valor}" disabled="true"/>
	         <h:commandButton id="clean-btn" value="Neteja concatenació" styleClass="wizardButton" 
	         		actionListener="#{VocabulariWizard.netejaConcatenacio}"/>
	         <h:outputLabel value="Tipus"/>
	         <h:selectOneMenu id="tipus" value="#{VocabulariWizard.tipus}">
				        <f:selectItem id="item1" itemLabel="Txt" itemValue="Txt" />
				        <f:selectItem id="item2" itemLabel="Data" itemValue="Data" />
				        <f:selectItem id="item3" itemLabel="Número" itemValue="Numero" />
	    	</h:selectOneMenu>
	         <h:outputLabel value="Obligatori"/>
			 <h:selectBooleanCheckbox value="#{VocabulariWizard.obligatori}"/>
	</h:panelGrid>
	<h:panelGrid>
		<h:outputLabel value="Atribut tipus documental"/>
		<h:selectBooleanCheckbox id="checkboxTipusDocumental" value="#{VocabulariWizard.checkboxTipusDocumental}"/>
        <h:selectOneMenu id="tipusDocumental" value="#{VocabulariWizard.tipusDocumental}">
			<f:selectItems value="#{VocabulariWizard.tipusDocumentals}" />
			<p:ajax update="atributs" event="change" partialSubmit="false" actionListener="#{VocabulariWizard.cargaLlistatAtributs}" />
	    </h:selectOneMenu>
	    <h:selectOneMenu id="atributs" value="#{VocabulariWizard.atribut}">
			<f:selectItems value="#{VocabulariWizard.atributs}" />
	    </h:selectOneMenu>
	    <h:outputLabel value="Constant"/>
	    <h:selectBooleanCheckbox id="checkboxConstant" value="#{VocabulariWizard.checkboxConstant}"/>
	    <h:inputText id="constant" size="40" value="#{VocabulariWizard.constant}"/>
	    <h:outputLabel value="Sentència de cerca"/>
	    <h:selectBooleanCheckbox id="checkboxCerca" value="#{VocabulariWizard.checkboxCerca}"/>
	    <h:inputTextarea rows="2" cols="35" value="#{VocabulariWizard.cerca}"/>
	    <h:outputLabel value="Tipus documental a parsejar"/>
	    <h:selectBooleanCheckbox id="checkboxTipusDocumentalParser" value="#{VocabulariWizard.checkboxTipusDocumentalParser}"/>
	    <h:selectOneMenu id="tipusDocumentalParser" value="#{VocabulariWizard.tipusDocumentalParser}">
			<f:selectItems value="#{VocabulariWizard.tipusDocumentals}" />
			<!--  <p:ajax update="atributs" event="change" partialSubmit="true" actionListener="#{EditVocabularioWizard.cargaLlistatAtributs}" />-->
	    </h:selectOneMenu>
	    <h:outputLabel value="En cas d'escollir aquesta última opció s'ha d'escollir també un atribut de tipus documental o bé introduïr una sentència de cerca per tal d'establir la relació per la metadada necessària."/>
	    <h:commandButton id="valida-btn" value="Afegeix i valida" styleClass="wizardButton" 
	         		actionListener="#{VocabulariWizard.validar}"/>
	</h:panelGrid>
	 <h:commandButton id="nou-btn" value="Guarda" styleClass="wizardButton" 
	         		actionListener="#{VocabulariWizard.nouEditAtribut}" rendered="#{VocabulariWizard.error == null}"/>
</h:panelGrid>

<h:panelGrid width="100%" columns="1" rendered="#{VocabulariWizard.error == null}" style="font-weight:bold;background:#A9D0F5;">
	<h:outputLabel value="Llistat atributs" style="font-weight:bold;"/>
</h:panelGrid>

<h:dataTable id="results" border="0" value="#{VocabulariWizard.nodesAtr}" var="nodeAtr" styleClass="nodeBrowserTable" width="100%" rendered="#{VocabulariWizard.error == null }">
	<h:column>
	<f:facet name="header">
	<h:outputText value="Nom"/>
	</f:facet>
		<h:outputText value="#{nodeAtr.nombreAtr}"/>
	</h:column>
	<h:column>
	<f:facet name="header">
	<h:outputText value="Valor"/>
	</f:facet>
		<h:outputText value="#{nodeAtr.valor}"/>
	</h:column>
	<h:column>
	<f:facet name="header">
	<h:outputText value="Obligatori"/>
	</f:facet>
		<h:outputText value="#{nodeAtr.obligatori}"/>
	</h:column>
	<h:column>
	<f:facet name="header">
	<h:outputText value="Tipus"/>
	</f:facet>
		<h:outputText value="#{nodeAtr.tipus}"/>
	</h:column>
	<h:column>
	<h:outputText value="">
	<a:actionLink id="edit" value="Edita atribut" actionListener="#{VocabulariWizard.editAtributo}" showLink="false" image="/images/icons/edit_properties.gif" >
	 	<f:param name="editAtributo" value="#{nodeAtr.nombreAtr}" />
	 </a:actionLink>
	 </h:outputText>
	</h:column>
	<h:column>
	<h:outputText value="">
	<a:actionLink id="delete" value="Elimina atribut" actionListener="#{VocabulariWizard.deleteAtributo}" showLink="false" image="/images/icons/delete.gif" >
	 	<f:param name="nomAtributo" value="#{nodeAtr.nombreAtr}" />
	 </a:actionLink>
	 </h:outputText>
	</h:column>
</h:dataTable>