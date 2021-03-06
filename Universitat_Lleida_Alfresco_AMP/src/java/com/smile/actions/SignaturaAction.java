package com.smile.actions;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.smile.webscripts.SignaturesList;
import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Signatura;
import com.smile.webscripts.helper.UdlProperties;

public class SignaturaAction extends ActionExecuterAbstractBase implements ConstantsUdL {
	
	private static Log logger = LogFactory.getLog(SignaturaAction.class);
	private final static String MIMETYPE_PDF = "application/pdf";
	private final static QName ASPECT_SIGNATURA = QName.createQName("http://www.smile.com/model/udl/1.0", "signatura");
	private ServiceRegistry serviceRegistry;
	
	@Override
	protected void executeImpl(Action action, NodeRef nodeRef) {
		ContentReader reader = null;
		String mimeType = "";
		List<Signatura> signatures = new ArrayList<Signatura>();
		SignaturesList signUtils = new SignaturesList();
		InputStream is = null;
		
		try {
			UdlProperties props = new UdlProperties();
			serviceRegistry.getAuthenticationService().authenticate(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD).toCharArray());
			if (serviceRegistry.getNodeService().exists(nodeRef) &&	
					!serviceRegistry.getNodeService().hasAspect(nodeRef, QName.createQName("http://www.alfresco.org/model/content/1.0", "workingcopy"))){
				reader = serviceRegistry.getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);
				if (reader != null) {
					reader.setEncoding(ConstantsUdL.UTF8);
					mimeType = reader.getContentData().getMimetype();
					is = reader.getContentInputStream();
					if (MIMETYPE_PDF.equals(mimeType)) {
						signatures = signUtils.getSignaturaPDF(is, UTF8, nodeRef, serviceRegistry);
					} 
					else if (signUtils.isXadesSignature(mimeType)) {
						signatures = signUtils.getSignaturaXades(is, UTF8, nodeRef, serviceRegistry);
					} 
					else if (signUtils.isCadesSignature(mimeType)) {
						signatures = signUtils.getSignaturaCades(is, UTF8, nodeRef, serviceRegistry);
					}									
				}

				//Load associated signatures data in the signatures metadata (add signatures data to List<Signatura> signatures)
				List<Signatura> dettachedSignaturesList = new ArrayList<Signatura>();
				if (serviceRegistry.getNodeService().getType(nodeRef).equals(QName.createQName(UDL_URI, "documentSimple"))){
					dettachedSignaturesList = signUtils.loadDettachedSignaturesDM(nodeRef, serviceRegistry, false, UTF8);
				}
				else {
					dettachedSignaturesList = signUtils.loadDettachedSignaturesRM(nodeRef, serviceRegistry, false, UTF8);
				}				
				signatures.addAll(dettachedSignaturesList);				
				setSignatures(signatures, nodeRef);
			}		
		} 
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error obtenint les signatures del document.", e);
		}
		finally {
			try {
				if(reader != null){
					is.close();
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Setea los metadatos de firma del documento.
	 * 
	 * @param signatures
	 * @param nodeRef
	 * @throws Exception
	 */
	private void setSignatures(List<Signatura> signatures, NodeRef nodeRef) throws Exception {
		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
		Iterator<Signatura> iter = signatures.iterator();
		int i = 1;		
		while (iter.hasNext()) {
			Signatura signatura = (Signatura) iter.next();			
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_tipus"), signatura.getTipusAttached());
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_format"), signatura.getFormatAttached());
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_nom"), signatura.getNom());
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_dni"), signatura.getDni());
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_data"), signatura.getData());
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_carrec"), signatura.getCarrec());				
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_data_fi_certificat"), signatura.getDataFiCert());
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_data_caducitat"), signatura.getDataCaducitat());
			i++;
			if (i>12){
				break;
			}
		}					
		//clear old metadata values
		i = signatures.size() + 1;
		while(i <= 12){
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_tipus"), "");
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_format"), "");
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_nom"), "");
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_dni"), "");
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_data"), null);
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_carrec"), "");				
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_data_fi_certificat"), null);
			props.put(QName.createQName(UDL_URI, "signatura" + i + "_data_caducitat"), null);	
			i++;
		}
		if(signatures.size() > 0 && serviceRegistry.getFileFolderService().getFileInfo(nodeRef).isFolder() == false) {
			serviceRegistry.getNodeService().addAspect(nodeRef, ASPECT_SIGNATURA, props);			
		}
		else {
			serviceRegistry.getNodeService().removeAspect(nodeRef, ASPECT_SIGNATURA);
		}
	}	
	
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> arg0) {}
	
	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
}