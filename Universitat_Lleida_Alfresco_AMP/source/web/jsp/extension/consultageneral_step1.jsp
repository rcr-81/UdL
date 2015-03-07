<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>
              
<table cellpadding="2" cellspacing="2" border="0" width="100%">

<tr><td colspan="2" class="paddingRow"></td></tr>
<tr>
<td><h:outputText value="Tipus Petició: "/></td>
<td>
<h:selectOneMenu id="language" value="#{ConsultaGeneralWizard.tipus}">
	<f:selectItem itemLabel="Tots" itemValue="Tots"/>
	<f:selectItem itemLabel="Pendent" itemValue="Pendent"/>
	<f:selectItem itemLabel="En tramitació" itemValue="En tramitacio"/>
	<f:selectItem itemLabel="Transferit" itemValue="Transferit"/>
	<f:selectItem itemLabel="Error" itemValue="Error"/>
</h:selectOneMenu>
</td>
</tr>


</table>
                              
