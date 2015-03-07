<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<h:outputText id="t-txt" value="#{WizardManager.bean.tipus}" />

<h:dataTable id="results" border="1" value="#{ConsultaGeneralWizard.nodes}" var="node" styleClass="nodeBrowserTable">
<h:column>
<f:facet name="header">
<h:outputText value="#{msg.name}"/>
</f:facet>
	<h:outputText value="#{node.name}"/>
</h:column>
<h:column>
<f:facet name="header">
<h:outputText value="#{msg.expedient}"/>
</f:facet>
	<h:outputText value="#{node.exp}"/>
</h:column>
<h:column>
<f:facet name="header">
<h:outputText value="#{msg.document}"/>
</f:facet>
	<h:outputText value="#{node.doc}"/>
</h:column>
</h:dataTable>