<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>
                              
<table cellpadding="2" cellspacing="2" border="0" width="100%">

<tr>
<td><h:outputText value="#{msg.result_peticio_desc} #{ConsultaEstatPeticioWizard.peticio}: "/></td>
<td>
<h:outputText id="peticio" value="#{ConsultaEstatPeticioWizard.result}" rendered="#{ConsultaEstatPeticioWizard.error == null}" />
<h:outputText id="error" value="#{ConsultaEstatPeticioWizard.error}" rendered="#{ConsultaEstatPeticioWizard.error != null}" style="color:red;"/>
</td>
</tr>

</table>