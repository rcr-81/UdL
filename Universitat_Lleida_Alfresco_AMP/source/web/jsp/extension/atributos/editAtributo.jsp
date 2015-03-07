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

<h:panelGrid rendered="#{EditVocabularioWizard.errorNodeVocabulario != null}">
	<h:outputText value="#{EditVocabularioWizard.errorNodeVocabulario}" rendered="#{EditVocabularioWizard.errorNodeVocabulario != null}" style="color:red; font-weight:bold;"/>
</h:panelGrid>

<h:panelGrid rendered="#{EditVocabularioWizard.errorNodeVocabulario == null}">
<h:panelGrid columns="1">
	<h:outputText value="#{EditVocabularioWizard.result}" rendered="#{EditVocabularioWizard.error == null}" style="color:blue; font-weight:bold;" />
	<h:outputText value="#{EditVocabularioWizard.error}" rendered="#{EditVocabularioWizard.error != null}" style="color:red; font-weight:bold;"/>
</h:panelGrid>

<h:panelGrid width="100%" columns="1" style="font-weight:bold;background:#A9D0F5;">
	<h:outputLabel value="Propietats vocabulari" style="font-weight:bold;"/>
</h:panelGrid>

<h:panelGrid>
	<h:outputLabel value="Nom vocabulari"/>
	<h:inputText id="nombreVoc" size="35" value="#{EditVocabularioWizard.nombreVoc}"/>
</h:panelGrid>

<h:panelGrid width="100%" columns="1" style="font-weight:bold;background:#A9D0F5;">
	<h:outputLabel value="Propietats atributs" style="font-weight:bold;"/>
</h:panelGrid>

<h:panelGrid columns="2">  
	<h:panelGrid>
	         <h:outputLabel value="Nom"/>
	         <h:inputText id="nombreAtr" size="35" value="#{EditVocabularioWizard.nombreAtr}"/>
	         <h:outputLabel value="Valor"/>
	         <h:inputTextarea id="valor" rows="2" cols="70" value="#{EditVocabularioWizard.valor}" disabled="true"/>
	         <h:commandButton id="clean-btn" value="Neteja concatenació" styleClass="wizardButton" 
	         		actionListener="#{EditVocabularioWizard.netejaConcatenacio}"/>
	         <h:outputLabel value="Tipus"/>
	         <h:selectOneMenu value="#{EditVocabularioWizard.tipus}">
				        <f:selectItem id="item1" itemLabel="Txt" itemValue="Txt" />
				        <f:selectItem id="item2" itemLabel="Data" itemValue="Data" />
				        <f:selectItem id="item3" itemLabel="Número" itemValue="Numero" />
	    	</h:selectOneMenu>
	         <h:outputLabel value="Obligatori"/>
			 <h:selectBooleanCheckbox value="#{EditVocabularioWizard.obligatori}"/>
	</h:panelGrid>
	<h:panelGrid>  
		<h:outputLabel value="Atribut tipus documental"/>
		<h:selectBooleanCheckbox id="checkboxTipusDocumental" value="#{EditVocabularioWizard.checkboxTipusDocumental}"/>
	    <h:selectOneMenu id="tipusDocumental" value="#{EditVocabularioWizard.tipusDocumental}">
			<f:selectItems value="#{EditVocabularioWizard.tipusDocumentals}" />
			<!--  <p:ajax update="atributs" event="change" partialSubmit="true" actionListener="#{EditVocabularioWizard.cargaLlistatAtributs}" />-->
	    </h:selectOneMenu>
	    <h:selectOneMenu id="atributs" value="#{EditVocabularioWizard.atribut}">
			<f:selectItems value="#{EditVocabularioWizard.atributs}" />
	    </h:selectOneMenu>
	    <h:outputLabel value="Constant"/>
	    <h:selectBooleanCheckbox id="checkboxConstant" value="#{EditVocabularioWizard.checkboxConstant}"/>
	    <h:inputText id="constant" size="40" value="#{EditVocabularioWizard.constant}"/>
	    <h:outputLabel value="Sentència de cerca"/>
	    <h:selectBooleanCheckbox id="checkboxCerca" value="#{EditVocabularioWizard.checkboxCerca}"/>
	    <h:inputTextarea rows="5" cols="35" value="#{EditVocabularioWizard.cerca}"/>
	    <h:outputLabel value="Tipus documental a parsejar"/>
	    <h:selectBooleanCheckbox id="checkboxTipusDocumentalParser" value="#{EditVocabularioWizard.checkboxTipusDocumentalParser}"/>
	    <h:selectOneMenu id="tipusDocumentalParser" value="#{EditVocabularioWizard.tipusDocumentalParser}">
			<f:selectItems value="#{EditVocabularioWizard.tipusDocumentals}" />
			<!--  <p:ajax update="atributs" event="change" partialSubmit="true" actionListener="#{EditVocabularioWizard.cargaLlistatAtributs}" />-->
	    </h:selectOneMenu>
	    <h:outputLabel value="En cas d'escollir aquesta última opció s'ha d'escollir també un atribut de tipus documental o bé introduïr una sentència de cerca per tal d'establir la relació per la metadada necessària."/>
	    <h:commandButton id="valida-btn" value="Afegeix i valida" styleClass="wizardButton" 
	         		actionListener="#{EditVocabularioWizard.validar}"/>
	</h:panelGrid>
	 <h:commandButton id="nou-btn" value="Guarda" styleClass="wizardButton" 
	         		actionListener="#{EditVocabularioWizard.nouEditaAtributButton}"/>
</h:panelGrid>

<h:panelGrid width="100%" columns="1" rendered="#{EditVocabularioWizard.error == null}" style="font-weight:bold;background:#A9D0F5;">
	<h:outputLabel value="Llistat atributs" style="font-weight:bold;"/>
</h:panelGrid>

<h:dataTable id="results" border="0" value="#{EditVocabularioWizard.nodesAtr}" var="nodeAtr" styleClass="nodeBrowserTable" width="100%">
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
	<a:actionLink id="edit" value="Edita atribut" actionListener="#{EditVocabularioWizard.editAtributo}" showLink="false" image="/images/icons/edit_properties.gif" >
	 	<f:param name="editAtributo" value="#{nodeAtr.nombreAtr}" />
	 </a:actionLink>
	 </h:outputText>
	</h:column>
	<h:column>
	<h:outputText value="">
	<a:actionLink id="delete" value="Elimina atribut" actionListener="#{EditVocabularioWizard.deleteAtributo}" showLink="false" image="/images/icons/delete.gif" >
	 	<f:param name="nomAtributo" value="#{nodeAtr.nombreAtr}" />
	 </a:actionLink>
	 </h:outputText>
	</h:column>
</h:dataTable>
</h:panelGrid>