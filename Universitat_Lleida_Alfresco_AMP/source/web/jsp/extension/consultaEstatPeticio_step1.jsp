<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<table cellpadding="2" cellspacing="2" border="0" width="100%">

<tr>
<td><h:outputText value="#{msg.id_peticio}: "/></td>
<td>
<h:inputText id="peticio" value="#{ConsultaEstatPeticioWizard.peticio}" size="35" maxlength="1024" />
</td>
</tr>

</table>