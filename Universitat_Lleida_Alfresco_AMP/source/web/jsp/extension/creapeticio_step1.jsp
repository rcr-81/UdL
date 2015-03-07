<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>
     
<h:outputText value="#{msg.send_request}: " rendered="#{CreaPeticioIArxiuWizard.error == null}"/>                         
<h:outputText value="#{CreaPeticioIArxiuWizard.node.name}" rendered="#{CreaPeticioIArxiuWizard.error == null}" />

<h:outputText value="#{CreaPeticioIArxiuWizard.error}" rendered="#{CreaPeticioIArxiuWizard.error != null}" style="color:red"/>