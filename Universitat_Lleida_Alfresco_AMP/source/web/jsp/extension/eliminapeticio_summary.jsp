<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>
                              
<table cellpadding="2" cellspacing="2" border="0" width="100%">

<tr>
<td><h:outputText value="#{result_pia} #{EliminaPeticioWizard.pia}: "/></td>
<td>
<h:outputText id="eliminacio" value="#{EliminaPeticioWizard.result}" rendered="#{EliminaPeticioWizard.error == null}" />
<h:outputText id="error" value="#{EliminaPeticioWizard.error}" rendered="#{EliminaPeticioWizard.error != null}" style="color:red;"/>
</td>
</tr>

</table>