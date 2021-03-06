package com.smile.webscripts;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.w3c.dom.Document;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfPKCS7;
import com.itextpdf.text.pdf.PdfReader;
import com.smile.webscripts.helper.CertTools;
import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Signatura;
import com.smile.webscripts.helper.UdlProperties;

import es.mityc.firmaJava.libreria.xades.DatosFirma;
import es.mityc.firmaJava.libreria.xades.ResultadoValidacion;
import es.mityc.firmaJava.libreria.xades.ValidarFirmaXML;

public class SignaturesList extends AbstractWebScript implements ConstantsUdL {

	private static Log logger = LogFactory.getLog(SignaturesList.class);
	private final static String MIMETYPE_PDF = "application/pdf";
	private final static String GIVENNAME = "GIVENNAME";
	private final static String SURNAME = "SURNAME";
	private final static String SERIALNUMBER_ABREUJAT = "SN";
	private final static String SERIALNUMBER = "SERIALNUMBER";
	private final static String COMMONNAME = "CN";
	private final static String CARREC = "T";

	private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		JSONObject jsonObj = new JSONObject();
		List<Signatura> signaturesList = new ArrayList<Signatura>();
		ContentReader reader = null;
		NodeRef nodeRef = getNodeRef(req);
		String mimeType = "";
		InputStream is = null;
		List<Signatura> dettachedSignaturesList = new ArrayList<Signatura>();

		try {
			UdlProperties props = new UdlProperties();
			serviceRegistry.getAuthenticationService().authenticate(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD).toCharArray());
			reader = serviceRegistry.getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);
			if (reader != null) {
				reader.setEncoding(ConstantsUdL.UTF8);
				mimeType = reader.getContentData().getMimetype();
				is = reader.getContentInputStream();
				//load document signature
				signaturesList = loadSignatura(is, mimeType, signaturesList, nodeRef, serviceRegistry, ISO8859);

				//load associated signatures (dettached)				
				if (serviceRegistry.getNodeService().getType(nodeRef).equals(QName.createQName(UDL_URI, "documentSimple"))){
					dettachedSignaturesList = loadDettachedSignaturesDM(nodeRef, serviceRegistry, false, ISO8859);
				}
				else {
					dettachedSignaturesList = loadDettachedSignaturesRM(nodeRef, serviceRegistry, false, ISO8859);
				}
			}
			signaturesList.addAll(dettachedSignaturesList);

		} 
		catch (Exception e) {			
			logger.error("Error obtenint les signatures del document", e);			
			//String error = "{\"signatures\":[{\"error\":\"Error obtenint les signatures del document:"+e.getMessage()+"\"}]}";			
			//res.getWriter().write(error);
		} 		
		finally {
			try {
				//Collections.sort(signaturesList);
				String json = jsonObj.put(SIGNATURES, listSignaturesToJsonArray(signaturesList)).toString();
				res.getWriter().write(json);
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
	 * @param is
	 * @param mimeType
	 * @param signaturesList
	 * @param nodeRef
	 * @param serviceRegistry
	 * @param encoding
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 * @throws Exception
	 */
	private List<Signatura> loadSignatura(InputStream is, String mimeType, List<Signatura> signaturesList, 
			NodeRef nodeRef, ServiceRegistry serviceRegistry, String encoding) throws IOException, DocumentException, Exception{
		if (MIMETYPE_PDF.equals(mimeType)) {
			signaturesList = getSignaturaPDF(is, encoding, nodeRef, serviceRegistry);
		} 
		else if ("application/octet-stream".equals(mimeType)){
			signaturesList = getSignaturaPKCS7(is, encoding, nodeRef, serviceRegistry);
		}
		else if (isXadesSignature(mimeType)) {
			signaturesList = getSignaturaXades(is, encoding, nodeRef, serviceRegistry);
		} 
		else if (isCadesSignature(mimeType)) {
			signaturesList = getSignaturaCades(is, encoding, nodeRef, serviceRegistry);
		}
		return signaturesList;
	}


	/**
	 * Dettached signatures evaluation (peer-associations)
	 *    1) Retrieve associated signatures
	 *    2) Load associated signatures data in the signatures list table
	 * @param nodeRef
	 * @param serviceRegistry
	 * @param updateSignaturaMetadata
	 * @param encoding
	 * @return
	 */
	public List<Signatura> loadDettachedSignaturesRM(NodeRef nodeRef, ServiceRegistry serviceRegistry, boolean updateSignaturaMetadata, String encoding){
		List<Signatura> dettachedSignaturesList = new ArrayList<Signatura>();
		List<AssociationRef> assocs = serviceRegistry.getNodeService().getTargetAssocs(nodeRef, QName.createQName(CM_URI, "signaturesDocumentRm"));		
		List<Signatura> signatures = new ArrayList<Signatura>();
		if(assocs != null){
			Iterator<AssociationRef> it = assocs.iterator();
			InputStream is = null;
			while(it.hasNext()){
				try {				
					AssociationRef assoc = (AssociationRef)it.next();				
					NodeRef signaturaRef = assoc.getTargetRef();
					if(serviceRegistry.getNodeService().hasAspect(signaturaRef, QName.createQName(UDLRM_URI, "signaturaDettached"))){
						ContentReader reader = serviceRegistry.getContentService().getReader(signaturaRef, ContentModel.PROP_CONTENT);						
						String mimeType = reader.getContentData().getMimetype();
						reader.setEncoding(ConstantsUdL.UTF8);			
						is = reader.getContentInputStream();
						signatures = loadSignatura(is, mimeType, signatures, nodeRef, serviceRegistry, encoding);
						//serviceRegistry.getNodeService().setProperty(signaturaRef, QName.createQName(UDLRM_URI, "id_document_signat"), assoc.getSourceRef().toString());
						//serviceRegistry.getNodeService().setProperty(signaturaRef, QName.createQName(UDLRM_URI, "id_document_signat"), serviceRegistry.getNodeService().getProperty(nodeRef, QName.createQName(UDLRM_URI, "secuencial_identificador_documentSimple")));
						dettachedSignaturesList.addAll(signatures);
					}
				}
				catch (Exception e){
					e.printStackTrace();
				}
				finally {
					if(is != null) {
						try {
							is.close();
						} catch (IOException e) {						
							e.printStackTrace();
						}
					}	
				}			
			}
		}
		return dettachedSignaturesList;
	}

	/**
	 * Dettached signatures evaluation (peer-associations)
	 *    1) Retrieve associated signatures
	 *    2) Load associated signatures data in the signatures list table
	 * @param nodeRef
	 * @param serviceRegistry
	 * @param updateSignaturaMetadata
	 * @param encoding
	 * @return
	 */
	public List<Signatura> loadDettachedSignaturesDM(NodeRef nodeRef, ServiceRegistry serviceRegistry, boolean updateSignaturaMetadata, String encoding){
		List<Signatura> dettachedSignaturesList = new ArrayList<Signatura>();
		List<AssociationRef> assocs = serviceRegistry.getNodeService().getTargetAssocs(nodeRef, QName.createQName(UDL_URI, "signaturesDocument"));
		List<Signatura> signatures = new ArrayList<Signatura>();
		if(assocs != null){
			Iterator<AssociationRef> it = assocs.iterator();
			InputStream is = null;
			try {		
				while(it.hasNext()){
					AssociationRef assoc = (AssociationRef)it.next();				
					NodeRef signaturaRef = assoc.getTargetRef();
					if(serviceRegistry.getNodeService().getType(signaturaRef).equals(QName.createQName(UDL_URI, "signaturaDettached"))){
						ContentReader reader = serviceRegistry.getContentService().getReader(signaturaRef, ContentModel.PROP_CONTENT);						
						String mimeType = reader.getContentData().getMimetype();
						reader.setEncoding(ConstantsUdL.UTF8);			
						is = reader.getContentInputStream();						
						signatures = loadSignatura(is, mimeType, signatures, nodeRef, serviceRegistry, encoding);
						//serviceRegistry.getNodeService().setProperty(signaturaRef, QName.createQName(UDL_URI, "id_document_signat"), assoc.getSourceRef().toString());
						serviceRegistry.getNodeService().setProperty(signaturaRef, QName.createQName(UDL_URI, "id_document_signat"), serviceRegistry.getNodeService().getProperty(nodeRef, QName.createQName(UDL_URI, "secuencial_identificador_documentSimple")));
						dettachedSignaturesList.addAll(signatures);
					}
				}							
			}
			catch (Exception e){
				e.printStackTrace();
			}
			finally {
				if(is != null) {
					try {
						is.close();
					} catch (IOException e) {						
						e.printStackTrace();
					}
				}	
			}
		}
		return dettachedSignaturesList;
	}

	/**
	 * Retorna un objeto NodeRef con la referencia del nodo recibido como parametro.
	 * @param req
	 * @return
	 */
	private NodeRef getNodeRef(WebScriptRequest req) {
		NodeRef nodeRef = new NodeRef(req.getParameter("nodeRef"));
		return nodeRef;
	}

	/**
	 * Returns a json structure with the fields of signatures. For XADES signatures.
	 * @param is
	 * @param encoding
	 * @param nodeRef
	 * @param serviceRegistry
	 * @return
	 * @throws Exception
	 */
	public List<Signatura> getSignaturaXades(InputStream is, String encoding, NodeRef nodeRef, ServiceRegistry serviceRegistry) throws Exception {
		ResultadoValidacion result = null;
		ArrayList<ResultadoValidacion> results = null;
		List<Signatura> signaturesList = new ArrayList<Signatura>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true); // Para que no se altere el orden de los atributos de nodo invalidando la firma 
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(is);
		ValidarFirmaXML vXml = new ValidarFirmaXML();

		if (doc != null) {
			// Se instancia el validador y se realiza la validación
			results = vXml.validar(doc, "./", null) ;
			Iterator<ResultadoValidacion> it = results.iterator();

			while (it.hasNext()) {
				result = it.next();
				DatosFirma datosFirma = result.getDatosFirma();
				if (datosFirma.getCadenaFirma() != null) {
					X509Certificate cert = ((X509Certificate) datosFirma.getCadenaFirma().getCertificates().get(0));
					Signatura signatura = new Signatura();										
					signatura = loadCertificateInfo(cert, encoding);

					signatura.setValidacio(result.getNivelValido());					
					signatura.setData(datosFirma.getFechaFirma());									
					if(datosFirma.getTipoFirma() != null){
						signatura.setTipus(datosFirma.getTipoFirma().getTipoXAdES().name());
					}
					signatura.setTipusAttached(ConstantsUdL.SIGNATURA_ATTACHED);
					signatura.setFormatAttached(ConstantsUdL.SIGNATURA_XADES);
					signaturesList.add(signatura);
					uploadDettachedSignaturaMetadata(signatura, nodeRef, serviceRegistry);					
				}
			}
		}
		return signaturesList;
	}

	/**
	 * Returns a json structure with the fields of signatures. For CADES Signatures.
	 * @param is
	 * @param encoding
	 * @param nodeRef
	 * @param serviceRegistry
	 * @return
	 * @throws Exception
	 */
	public List<Signatura> getSignaturaCades(InputStream is, String encoding, NodeRef nodeRef, ServiceRegistry serviceRegistry) throws Exception {
		List<Signatura> signaturesList = new ArrayList<Signatura>();
		CMSSignedData s = new CMSSignedData(is);
		CertStore certStore = s.getCertificatesAndCRLs("Collection", "BC");
		SignerInformationStore signers = s.getSignerInfos();
		for (Iterator<?> i = signers.getSigners().iterator(); i.hasNext();) {
			SignerInformation signer = (SignerInformation) i.next();
			certStore.getCertificates(signer.getSID());
			if (!certStore.getCertificates(signer.getSID()).isEmpty()) {
				for (Iterator<?> j = certStore.getCertificates(signer.getSID()).iterator(); j.hasNext();) {
					X509Certificate cert = (X509Certificate) j.next();
					Signatura signatura = loadCertificateInfo(cert, encoding);
					signatura.setTipusAttached(ConstantsUdL.SIGNATURA_ATTACHED);
					signatura.setFormatAttached(ConstantsUdL.SIGNATURA_CADES);
					signaturesList.add(signatura);
					uploadDettachedSignaturaMetadata(signatura, nodeRef, serviceRegistry);
				}
			}
		}
		return signaturesList;
	}

	/**
	 * @param is
	 * @param encoding
	 * @param serviceRegistry
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 * @throws Exception
	 */
	public List<Signatura> getSignaturaPKCS7(InputStream is, String encoding, NodeRef nodeRef, ServiceRegistry serviceRegistry) throws IOException, DocumentException, Exception {
		List<Signatura> signaturesList = new ArrayList<Signatura>();
		CMSSignedData s = new CMSSignedData(is);
		CertStore certStore = s.getCertificatesAndCRLs("Collection", "BC");
		SignerInformationStore signers = s.getSignerInfos();
		for (Iterator<?> i = signers.getSigners().iterator(); i.hasNext();) {
			SignerInformation signer = (SignerInformation) i.next();
			certStore.getCertificates(signer.getSID());
			if (!certStore.getCertificates(signer.getSID()).isEmpty()) {
				for (Iterator<?> j = certStore.getCertificates(signer.getSID()).iterator(); j.hasNext();) {
					X509Certificate cert = (X509Certificate) j.next();
					Signatura signatura = loadCertificateInfo(cert, encoding);
					signatura.setTipusAttached(ConstantsUdL.SIGNATURA_ATTACHED);
					signatura.setFormatAttached(ConstantsUdL.SIGNATURA_CADES);
					signaturesList.add(signatura);
					uploadDettachedSignaturaMetadata(signatura, nodeRef, serviceRegistry);
				}
			}
		}
		return signaturesList;
	}

	/**
	 * Returns a json structure with the fields of signatures. For PDF signed.
	 * @param is
	 * @param encoding
	 * @param nodeRef
	 * @param serviceRegistry
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 * @throws Exception
	 */
	public List<Signatura> getSignaturaPDF(InputStream is, String encoding, NodeRef nodeRef, ServiceRegistry serviceRegistry) throws IOException, DocumentException, Exception {
		List<Signatura> signaturesList = new ArrayList<Signatura>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		PdfReader pdfReader = new PdfReader(is);
		AcroFields af = pdfReader.getAcroFields();
		ArrayList<String> names = af.getSignatureNames();
		for (String name : names) {
			Signatura signatura = new Signatura();
			PdfPKCS7 pk = af.verifySignature(name);
			signatura = loadCertificateInfo(pk.getSigningCertificate(), encoding);
			Calendar cal = pk.getSignDate();
			signatura.setStringDataFiCert(sdf.format(pk.getSigningCertificate().getNotAfter()));
			signatura.setDataFiCert(pk.getSigningCertificate().getNotAfter());
			signatura.setStringData(sdf.format(cal.getTime()));
			signatura.setData(cal.getTime());
			if(af.signatureCoversWholeDocument(name)) {
				signatura.setTipusAttached(ConstantsUdL.SIGNATURA_ENVELOPING);
			}else {
				signatura.setTipusAttached(ConstantsUdL.SIGNATURA_ATTACHED);
			}
			signatura.setFormatAttached(ConstantsUdL.SIGNATURA_PADES);
			signaturesList.add(signatura);
			uploadDettachedSignaturaMetadata(signatura, nodeRef, serviceRegistry);
		}
		return signaturesList;
	}

	/**
	 * @param signatura
	 * @param nodeRef
	 * @param serviceRegistry
	 */
	private void uploadDettachedSignaturaMetadata(Signatura signatura, NodeRef nodeRef, ServiceRegistry serviceRegistry){
		NodeService nodeService = serviceRegistry.getNodeService();
		if(nodeService.getType(nodeRef).equals(QName.createQName(UDL_URI, "signaturaDettached"))){
			nodeService.setProperty(nodeRef, QName.createQName(UDL_URI, "nom_signatari_signatura"), signatura.getNom());
			nodeService.setProperty(nodeRef, QName.createQName(UDL_URI, "data_signatura"), signatura.getData());
			nodeService.setProperty(nodeRef, QName.createQName(UDL_URI, "data_ini_validacio_signatura"), signatura.getDataIniCert());
			nodeService.setProperty(nodeRef, QName.createQName(UDL_URI, "data_fi_validacio_signatura"), signatura.getDataFiCert());
			nodeService.setProperty(nodeRef, QName.createQName(UDL_URI, "politica_signatura"), signatura.getPolitiquesCertificat());
			nodeService.setProperty(nodeRef, QName.createQName(UDL_URI, "proveidor_certificat_signatura"), signatura.getProveidorCerficat());
			nodeService.setProperty(nodeRef, QName.createQName(UDL_URI, "tipus_certificat_signatura"), signatura.getTipusCertificat());
			nodeService.setProperty(nodeRef, QName.createQName(UDL_URI, "unitat_organica_signatura"), signatura.getDepartamentSubjecte());
			nodeService.setProperty(nodeRef, QName.createQName(UDL_URI, "identificador_signatari_signatura"), signatura.getNumSerieSubjecte());							
			nodeService.setProperty(nodeRef, QName.createQName(UDL_URI, "organitzacio_signatura"), signatura.getOrganitzacioSubjecte());
			nodeService.setProperty(nodeRef, QName.createQName(UDL_URI, "tipus_signatura"), signatura.getTipus());
			nodeService.setProperty(nodeRef, QName.createQName(UDL_URI, "evidencia_validacio_signatura"), signatura.getValidacio());						
		}
	}

	/**
	 * Get info of the signing certificate.
	 * @param cert
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public Signatura loadCertificateInfo(X509Certificate cert, String encoding) throws Exception {
		Signatura signatura = new Signatura();
		LdapName ldapDN;
		String dn;
		String cn = "";
		String givenName = "";
		String surname = "";		
		dn = cert.getSubjectDN().getName();
		ldapDN = new LdapName(dn);
		String politiquesCertificat = "";
		try {
			//politiquesCertificat = CertTools.getCertificatePolicyId(cert,0);	
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		String subjecte = CertTools.getSubjectDN(cert);
		Date dataValidDesde = cert.getNotAfter();
		Date dataFiValid = cert.getNotBefore();
		String proveidorCertificat = CertTools.getIssuerDN(cert);		
		String tipusCertificat = cert.getType();		
		String departamentSubjecte = CertTools.getPartFromDN(subjecte,"OU"); 
		String numSerieSubjecte = CertTools.getPartFromDN(subjecte,"SERIALNUMBER");
		String versioCertificat = String.valueOf(cert.getVersion());
		String organitzacioSubjecte = CertTools.getPartFromDN(subjecte,"O");
		String cnView = "";		
		String givenNameView = "";
		String surnameView = "";

		for (Rdn rdn : ldapDN.getRdns()) {
			if (COMMONNAME.equalsIgnoreCase(rdn.getType())) {
				cn = new String(((String) rdn.getValue()).getBytes(), encoding);
				cnView = new String(((String) rdn.getValue()).getBytes(), ISO8859);
			} 
			else if (GIVENNAME.equalsIgnoreCase(rdn.getType())) {
				givenName = new String(((String) rdn.getValue()).getBytes(), encoding);
				givenNameView = new String(((String) rdn.getValue()).getBytes(), ISO8859);				
			} 
			else if (SURNAME.equalsIgnoreCase(rdn.getType())) {
				surname = new String(((String) rdn.getValue()).getBytes(), encoding);
				surnameView = new String(((String) rdn.getValue()).getBytes(), ISO8859);
			} 
			else if (SERIALNUMBER_ABREUJAT.equalsIgnoreCase(rdn.getType()) || SERIALNUMBER.equalsIgnoreCase(rdn.getType())) {
				signatura.setDni(new String(((String) rdn.getValue()).getBytes(), encoding));

			} 
			else if(CARREC.equalsIgnoreCase(rdn.getType())) {
				signatura.setCarrec((String) rdn.getValue());
				signatura.setCarrecView(new String(((String) rdn.getValue()).getBytes(), ISO8859));				
			}
		}
		signatura.setNom(givenName + " " + surname);
		signatura.setNomView(givenNameView + " " + surnameView);
		if(" ".equals(signatura.getNom())) {
			signatura.setNom(cn);
			signatura.setNomView(cnView);
		}
		signatura.setPolitiquesCertificat(politiquesCertificat);		
		signatura.setDataFiCert(dataFiValid);
		signatura.setDataIniCert(dataValidDesde);		
		signatura.setProveidorCerficat(proveidorCertificat);
		signatura.setTipusCertificat(tipusCertificat);
		signatura.setDepartamentSubjecte(departamentSubjecte);
		signatura.setNumSerieSubjecte(numSerieSubjecte);
		signatura.setVersioCertificat(versioCertificat);
		signatura.setOrganitzacioSubjecte(organitzacioSubjecte);
		return signatura;
	}

	/**
	 * Return true if the mimetype corresponds to a document with xades attached
	 * signature / false in other case.
	 * @param mimeType
	 * @return
	 * @throws Exception
	 */
	public boolean isXadesSignature(String mimeType) throws Exception {
		boolean result = false;
		if (MIMETYPE_SIGNATURE_XADES.equalsIgnoreCase(mimeType)
				|| MIMETYPE_SIGNATURE_XADES_DOC.equalsIgnoreCase(mimeType)
				|| MIMETYPE_SIGNATURE_XADES_DOCX.equalsIgnoreCase(mimeType)
				|| MIMETYPE_SIGNATURE_XADES_ODT.equalsIgnoreCase(mimeType)
				|| MIMETYPE_SIGNATURE_XADES_PDF.equalsIgnoreCase(mimeType)) {
			result = true;
		}
		return result;
	}

	/**
	 * Return true if the mimetype corresponds to a document with cades attached
	 * signature / false in other case.
	 * @param mimeType
	 * @return
	 * @throws Exception
	 */
	public boolean isCadesSignature(String mimeType) throws Exception {
		boolean result = false;
		if (MIMETYPE_SIGNATURE_CADES.equalsIgnoreCase(mimeType)
				|| MIMETYPE_SIGNATURE_CADES_DOC.equalsIgnoreCase(mimeType)
				|| MIMETYPE_SIGNATURE_CADES_DOCX.equalsIgnoreCase(mimeType)
				|| MIMETYPE_SIGNATURE_CADES_ODT.equalsIgnoreCase(mimeType)
				|| MIMETYPE_SIGNATURE_CADES_PDF.equalsIgnoreCase(mimeType)) {
			result = true;
		}
		return result;
	}

	/**
	 * Return true if the mimetype corresponds to a document with detached
	 * signature / false in other case.
	 * @param mimeType
	 * @return
	 * @throws Exception
	 */
	/*
	private boolean isDetachedSignature(String mimeType) throws Exception {
		boolean result = false;
		if (MIMETYPE_SIGNATURE_DETACHED_DOC.equalsIgnoreCase(mimeType)
				|| MIMETYPE_SIGNATURE_DETACHED_DOCX.equalsIgnoreCase(mimeType)
				|| MIMETYPE_SIGNATURE_DETACHED_ODT.equalsIgnoreCase(mimeType)
				|| MIMETYPE_SIGNATURE_DETACHED_PDF.equalsIgnoreCase(mimeType)) {
			result = true;
		}
		return result;
	}
	 */
	
	/**
	 * Convert a list of signatures to json string.
	 * 
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public static JSONArray listSignaturesToJsonArray(List<Signatura> list)
			throws Exception {
		JSONArray array = new JSONArray();
		Iterator<Signatura> iter = list.iterator();

		while (iter.hasNext()) {
			Signatura signatura = (Signatura) iter.next();
			JSONObject obj = new JSONObject();

			obj.put(SIGNATURA_NOM, signatura.getNom());
			obj.put(SIGNATURA_NOM_VIEW, signatura.getNomView());
			obj.put(SIGNATURA_CARREC, signatura.getCarrecView());			
			obj.put(SIGNATURA_DNI, signatura.getDni());
			obj.put(SIGNATURA_DATA, signatura.getStringData());
			obj.put(SIGNATURA_DATA_FI_VALIDESA_CERTIFICAT, signatura.getStringDataFiCert());
			
			array.put(obj);
		}

		return array;
	}
}