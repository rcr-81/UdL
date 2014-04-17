<?xml version="1.0" encoding="UTF-8"?>

<response>
	<service>consultarQdc</service>
	<username>${username}</username>
	<success>${success}</success>
	<#if error?exists>   
		<error>${error}</error>
	</#if>	
	<#if folder?exists>
		<qdc>
			<classes>
				<#list folder.children as child>	
					<classe>
						<id>${child.id}</id>
						<nom>${child.properties.name}</nom>
						<categories>
							<#list child.children as categoria>
								<categoria>
									<id>${categoria.id}</id>
									<nom>${categoria.properties.name}</nom>
									<carpetes>	
										<#list categoria.children as carpeta>
										    <#if carpeta.properties.description?exists>   
												<carpeta>
													<id>${carpeta.id}</id>
													<nom>${carpeta.properties.name}</nom>
													<path>${carpeta.displayPath}</path>		
													<tipus>${carpeta.type}</tipus>
													<documents>																		
														<#list carpeta.children as document>
															<document>
																<id>${document.id}</id>
																<nom>${document.properties.name}</nom>
																<path>${document.displayPath}</path>
																<tipus>${document.type}</tipus>
															</document>
														</#list>	
													</documents>									
												</carpeta>
											</#if>		
										</#list>	
									</carpetes>		
								</categoria>
							</#list>					
						</categories>
					</classe>
				</#list>
			</classes>	
		</qdc>
	</#if>
</response>