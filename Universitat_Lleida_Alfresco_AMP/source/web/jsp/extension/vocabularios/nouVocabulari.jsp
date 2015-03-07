<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<h:panelGrid columns="1">
<h:outputText value="#{VocabulariWizard.result}" rendered="#{VocabulariWizard.result != null}" style="color:blue; font-weight:bold;" />
</h:panelGrid>

<h:panelGrid width="100%" columns="1" style="font-weight:bold;background:#A9D0F5;">
<h:outputLabel value="Propietats vocabulari" style="font-weight:bold;"/>
</h:panelGrid>

<h:panelGrid columns="1">
<h:outputText value="Nom vocabulari: "/>
<h:inputText id="nombreVoc" value="#{VocabulariWizard.nombreVoc}" size="35" maxlength="1024" />
</h:panelGrid>

<h:panelGrid width="100%" columns="1" style="font-weight:bold;background:#A9D0F5;">
<h:outputLabel value="Llistat vocabularis" style="font-weight:bold;"/>
</h:panelGrid>

<h:dataTable id="results" border="0" value="#{VocabulariWizard.nodes}" var="node" styleClass="nodeBrowserTable" width="100%">
<h:column>
<f:facet name="header">
<h:outputText value="Nom vocabulari"/>
</f:facet>
	<h:outputText value="#{node.nombreVoc}"/>
</h:column>
<h:column>
	<h:outputText value="">
	<a:actionLink id="delete" value="Elimina vocabulari" actionListener="#{VocabulariWizard.deleteVocabulario}" showLink="false" image="/images/icons/delete.gif" >
	 	<f:param name="nomVocabulario" value="#{node.nombreVoc}" />
	 </a:actionLink>
	 </h:outputText>
	</h:column>
</h:dataTable>