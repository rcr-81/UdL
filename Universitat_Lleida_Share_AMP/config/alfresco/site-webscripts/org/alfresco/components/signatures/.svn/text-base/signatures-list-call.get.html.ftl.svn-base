<#if signatures?exists>
	<#if signatures.signatures?has_content>
		<div style="border-right: 1px solid #CCCCCC; border-bottom: 1px solid #CCCCCC; border-top:0px; border-left:0px; padding:20px;">
			<table>
				<tr>
					<th style="width: 180px" class="set-panel-heading">Nom</th>
					<th style="width: 90px" class="set-panel-heading">Càrrec</th>
			    	<th style="width: 65px" class="set-panel-heading">DNI</th>					
					<th style="width: 95px" class="set-panel-heading">Data signatura</th>
					<th class="set-panel-heading">Data fi certificat</th>				
				</tr>
				<#list signatures.signatures as s>
					<tr style="font-size: 10px">
						<td style="width: 180px"><#if s.nomView != "">${s.nomView}<#else>-</#if></td>
						<td style="width: 90px"><#if s.carrec != "">${s.carrec}<#else>-</#if></td>
						<td style="width: 65px"><#if s.dni != "">${s.dni}<#else>-</#if></td>
						<td style="width: 95px"><#if s.data != "">${s.data}<#else>-</#if></td>
						<td><#if s.dataFiCert != "">${s.dataFiCert}<#else>-</#if></td>					
					</tr>
				</#list>
			</table>
		</div>
	</#if>
</#if>