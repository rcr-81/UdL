package com.smile.webscripts.document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.service.namespace.QName;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.Metadada;
import com.smile.webscripts.helper.UdlProperties;

public class DocumentUtils implements ConstantsUdL {

	/**
	 * Call custom web-script to upload a document (content file should be encoded in Base64 otherwise HTTP can corrupt binary content).
	 * @param req
	 * @return The nodeRef of the created node or the error message.
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String pujarDocumentBase64(WebScriptRequest req, String content, String filename, String path, String type, List<Metadada> metadades) throws Exception {
		return pujarOactualitzarDocumentBase64(req, content, filename, path, null, type, metadades, URL_WEBSCRIPT_PUJAR_DOCUMENT_BASE64);
	}

	/**
	 * @param args
	 * @return
	 */
	private static List<Metadada> obtenirMetadades(Element args){
		NodeList nodesMetadades = args.getElementsByTagName(FORM_PARAM_METADADES).item(0).getChildNodes();
		return obtenirMetadades(nodesMetadades);
	}

	/**
	 * @param nodesMetadades
	 * @return
	 */
	private static List<Metadada> obtenirMetadades(NodeList nodesMetadades){
		List<Metadada> metadades = new ArrayList<Metadada>();
		for (int i = 0; i < nodesMetadades.getLength(); i++) {
			Node nodeMetadada = nodesMetadades.item(i);
			Metadada metadada = new Metadada(nodeMetadada);
			metadades.add(metadada);			
		}		
		return metadades;
	}

	/**
	 * Call custom web-script to upload a document (content file should be encoded in Base64 otherwise HTTP can corrupt binary content).
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected static String pujarDocumentBase64(WebScriptRequest req, Element args, String username) throws Exception {
		String content = args.getElementsByTagName(FORM_PARAM_DOC_CONTENT).item(0).getFirstChild().getNodeValue();
		String path = args.getElementsByTagName(FORM_PARAM_DOC_PATH).item(0).getFirstChild().getNodeValue();
		String filename = args.getElementsByTagName(FORM_PARAM_DOC_NAME).item(0).getFirstChild().getNodeValue();
		String type = args.getElementsByTagName(FORM_PARAM_DOC_TYPE).item(0).getFirstChild().getNodeValue();
		List<Metadada> metadades = obtenirMetadades(args);	
		metadades.add(new Metadada(FORM_PARAM_USERNAME, username));
		return pujarOactualitzarDocumentBase64(req, content, filename, path, null, type, metadades, URL_WEBSCRIPT_PUJAR_DOCUMENT_BASE64);
	}

	/**
	 * Call custom web-script to upload a document (content file should be encoded in Base64 otherwise HTTP can corrupt binary content).
	 * @param req
	 * @param path
	 * @return
	 * @throws Exception
	 */
	protected static String pujarSignaturaBase64(WebScriptRequest req, Element args, String path) throws Exception {
		String content = args.getElementsByTagName(FORM_PARAM_SIG_CONTENT).item(0).getFirstChild().getNodeValue();				
		String filename = args.getElementsByTagName(FORM_PARAM_SIG_NAME).item(0).getFirstChild().getNodeValue();
		List<Metadada> metadades = obtenirMetadades(args);
		return pujarOactualitzarDocumentBase64(req, content, filename, path, null, "udl:signaturaDettached", metadades, URL_WEBSCRIPT_PUJAR_DOCUMENT_BASE64);
	}

	/**
	 * Call custom web-script to update a document (content file should be encoded in Base64 otherwise HTTP can corrupt binary content).
	 * @param req
	 * @return The nodeRef of the created node or the error message.
	 * @throws HttpException
	 * @throws IOException
	 */
	protected static String actualitzarDocumentBase64(WebScriptRequest req, String content, String filename, String path, List<Metadada> metadades) throws Exception {
		return pujarOactualitzarDocumentBase64(req, content, filename, path, null, null, metadades, URL_WEBSCRIPT_ACTUALITZAR_DOCUMENT_BASE64);
	}

	/**
	 * Call custom web-script to update a document (content file should be encoded in Base64 otherwise HTTP can corrupt binary content).
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected static String actualitzarDocumentBase64(WebScriptRequest req, Element args) throws Exception {
		String content = args.getElementsByTagName(FORM_PARAM_DOC_CONTENT).item(0).getFirstChild().getNodeValue(); 
		String filename = args.getElementsByTagName(FORM_PARAM_DOC_NAME).item(0).getFirstChild().getNodeValue();
		String id = args.getElementsByTagName(FORM_PARAM_DOC_ID).item(0).getFirstChild().getNodeValue();
		List<Metadada> metadades = obtenirMetadades(args);		
		return pujarOactualitzarDocumentBase64(req, content, filename, null, id, null, metadades, URL_WEBSCRIPT_ACTUALITZAR_DOCUMENT_BASE64);
	}

	/**
	 * Call custom web-script to upload or update a document (content file should be encoded in Base64 otherwise HTTP can corrupt binary content).
	 * @param req
	 * @param content
	 * @param filename
	 * @param path
	 * @param id
	 * @param metadades
	 * @param webscriptUrl
	 * @return
	 * @throws Exception
	 */
	protected static String pujarOactualitzarDocumentBase64(WebScriptRequest req, String content, String filename, String path, String id, String type, List<Metadada> metadades, String webscriptUrl) throws Exception {

		//create HttpClient object
		HttpClient client = new HttpClient();

		//pre-authenticate with alfresco server
		client.getParams().setAuthenticationPreemptive(true);
		UdlProperties props = new UdlProperties();
		Credentials loginCreds = new UsernamePasswordCredentials(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD));

		//Use AuthScope.ANY since the HttpClient connects to just one URL
		client.getState().setCredentials(AuthScope.ANY, loginCreds);
		PostMethod post = new PostMethod(req.getServerPath() + req.getContextPath() + webscriptUrl);
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		//Alfresco only reads the POST parameters if they are in the body of the request, so we need to create a RequestEntity to populate the body.		
		Part[] parts = {new StringPart(FORM_PARAM_DOC_CONTENT, content, UTF8), 
				new StringPart(FORM_PARAM_DOC_NAME, filename, UTF8)};

		if(path != null){
			parts = (Part[])ArrayUtils.add(parts, new  StringPart(FORM_PARAM_DOC_PATH, path, UTF8));		
		}

		if(id != null){
			parts = (Part[])ArrayUtils.add(parts, new  StringPart(FORM_PARAM_DOC_ID, id, UTF8));
		}
		
		
		if(type != null){
			parts = (Part[])ArrayUtils.add(parts, new  StringPart(FORM_PARAM_DOC_TYPE, type, UTF8));
		}

		if(metadades != null){
			for (Metadada metadada : metadades) {
				parts = (Part[])ArrayUtils.add(parts, new StringPart(metadada.getNom(), metadada.getValor(), UTF8));
			}
		}

		MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		post.setRequestEntity(entity);

		//submit the request and get the response code
		int statusCode = client.executeMethod(post);
		InputStream responseBody = post.getResponseBodyAsStream();
		String nodeRef = convertStreamToString(responseBody);

		//Check for code 200		
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception(nodeRef);
		}

		return nodeRef;		
	}


	/**
	 * Creates or retrives nodeRef of a signatures folder.
	 * @param req
	 * @param username
	 * @param path
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static String obtenirFolderSignatures(WebScriptRequest req, String username, String path, String name) throws Exception {
		//create HttpClient object
		HttpClient client = new HttpClient();

		//pre-authenticate with alfresco server
		client.getParams().setAuthenticationPreemptive(true);
		UdlProperties props = new UdlProperties();
		Credentials loginCreds = new UsernamePasswordCredentials(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD));

		//Use AuthScope.ANY since the HttpClient connects to just one URL
		client.getState().setCredentials(AuthScope.ANY, loginCreds);
		PostMethod post = new PostMethod(req.getServerPath() + req.getContextPath() + URL_WEBSCRIPT_OBTENIR_FOLDER_SIGNATURES);
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		//Alfresco only reads the POST parameters if they are in the body of the request, so we need to create a RequestEntity to populate the body.				
		Part[] parts = {new StringPart(FORM_PARAM_DOC_NAME, name, UTF8),
				new  StringPart(FORM_PARAM_DOC_PATH, path),
				new  StringPart(FORM_PARAM_USERNAME, username)};
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		post.setRequestEntity(entity);

		//submit the request and get the response code
		int statusCode = client.executeMethod(post);
		InputStream responseBody = post.getResponseBodyAsStream();
		String nodeRef = convertStreamToString(responseBody);

		//Check for code 200		
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception(nodeRef);
		}

		return nodeRef;		
	}

	/**
	 * Helper method to read InputStreams.
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String convertStreamToString(InputStream is)
			throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, UTF8));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {       
			return "";
		}
	}

	/**
	 * Check incoming documents data and invoke node creation web-script.
	 * @param req
	 * @return
	 */
	protected static List<DocumentUploadInfo> pujarDocumentsBase64(WebScriptRequest req, Element args) {
		return pujarDocumentsOSignaturesBase64(req, args, null);
	}

	/**
	 * Check incoming documents data and invoke node creation web-script. 
	 * @param req
	 * @return The nodeRef list of the created nodes.
	 * @throws HttpException
	 * @throws IOException
	 */
	protected static List<DocumentUploadInfo> pujarDocumentsOSignaturesBase64(WebScriptRequest req, Element args, String pathSignatures) {
		List<DocumentUploadInfo> uploadInfoList = new ArrayList<DocumentUploadInfo>();
		if(pathSignatures == null){
			Node documents = args.getElementsByTagName(FORM_PARAM_DOCUMENTS).item(0);
			NodeList documentsList = documents.getChildNodes();
			for (int i = 0; i < documentsList.getLength(); i++) {				
				Node document = documentsList.item(i);
				NodeList dadesDocument = document.getChildNodes();
				String content = "";
				String filename = "";
				String path = "";
				String type = "";
				List<Metadada> metadades = null;

				for (int j = 0; j < dadesDocument.getLength(); j++) {
					Node dadaDocument = dadesDocument.item(j);
					if (dadaDocument.getNodeName().equals(FORM_PARAM_DOC_CONTENT)){
						content = dadaDocument.getFirstChild().getNodeValue();
					}
					else if (dadaDocument.getNodeName().equals(FORM_PARAM_DOC_PATH)){
						path = dadaDocument.getFirstChild().getNodeValue();
					}
					else if (dadaDocument.getNodeName().equals(FORM_PARAM_DOC_NAME)){
						filename = dadaDocument.getFirstChild().getNodeValue();
					}
					else if (dadaDocument.getNodeName().equals(FORM_PARAM_METADADES)){
						metadades = obtenirMetadades(dadaDocument.getChildNodes());
					}
					else if (dadaDocument.getNodeName().equals(FORM_PARAM_DOC_TYPE)){
						type = dadaDocument.getFirstChild().getNodeValue();
					}
				}
				DocumentUploadInfo uploadInfo = obtenirInfoPujarDocumentBase64(req, content, filename, path, type, metadades);	
				uploadInfoList.add(uploadInfo);
			}
		}

		else {
			Node signatures = args.getElementsByTagName(FORM_PARAM_SIGNATURES).item(0);
			NodeList signaturesList = signatures.getChildNodes();
			for (int i = 0; i < signaturesList.getLength(); i++) {				
				Node signatura = signaturesList.item(i);
				NodeList dadesSignatura = signatura.getChildNodes();
				String content = "";
				String filename = "";
				List<Metadada> metadades = null;

				for (int j = 0; j < dadesSignatura.getLength(); j++) {
					Node dadaSignatura = dadesSignatura.item(j);
					if (dadaSignatura.getNodeName().equals(FORM_PARAM_SIG_CONTENT)){
						content = dadaSignatura.getFirstChild().getNodeValue();
					}					
					else if (dadaSignatura.getNodeName().equals(FORM_PARAM_SIG_NAME)){
						filename = dadaSignatura.getFirstChild().getNodeValue();
					}
					else if (dadaSignatura.getNodeName().equals(FORM_PARAM_METADADES)){
						metadades = obtenirMetadades(dadaSignatura.getChildNodes());
					}
				}

				DocumentUploadInfo uploadInfo = obtenirInfoPujarDocumentBase64(req, content, filename, pathSignatures, "udl:signaturaDettached", metadades);	
				uploadInfoList.add(uploadInfo);
			}				
		}	

		return uploadInfoList;		
	}


	/**
	 * Gestiona errors i encapsula informacio un cop crida la pujada d'un document al repositori.
	 * @param req
	 * @param content
	 * @param filename
	 * @param path
	 * @return
	 */
	protected static DocumentUploadInfo obtenirInfoPujarDocumentBase64(WebScriptRequest req, String content, String filename, String path, String type, List<Metadada> metadades){		
		String nodeRef = "";
		String error = null;
		boolean success = false;		
		try {
			nodeRef = DocumentUtils.pujarDocumentBase64(req, content, filename, path, type, metadades);
			success = true;
		}
		catch (Exception e) {
			error = e.getMessage();
		}
		DocumentUploadInfo uploadInfo = new DocumentUploadInfo(nodeRef, error, success);		
		return uploadInfo;
	}

	/**
	 * Crea un node del tipus indicat al repositori.
	 * @param path
	 * @param name
	 * @param type
	 * @param req
	 * @return nodeRef del node creat o missatge d'error.
	 * @throws Exception
	 */
	public static String crearNode(String path, String name, String type, WebScriptRequest req) throws Exception{		

		//create HttpClient object
		HttpClient client = new HttpClient();

		//pre-authenticate with alfresco server
		client.getParams().setAuthenticationPreemptive(true);
		UdlProperties props = new UdlProperties();
		Credentials loginCreds = new UsernamePasswordCredentials(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD));

		//Use AuthScope.ANY since the HttpClient connects to just one URL
		client.getState().setCredentials(AuthScope.ANY, loginCreds);
		PostMethod post = new PostMethod(req.getServerPath() + req.getContextPath() + URL_WEBSCRIPT_CREAR_NODE);
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		//Alfresco only reads the POST parameters if they are in the body of the request, so we need to create a RequestEntity to populate the body.

		Part[] parts = {new StringPart(FORM_PARAM_NODE_NAME, name, UTF8),
				new  StringPart(FORM_PARAM_NODE_PATH, path),
				new  StringPart(FORM_PARAM_NODE_TYPE, type),};
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		post.setRequestEntity(entity);

		//submit the request and get the response code
		int statusCode = client.executeMethod(post);
		InputStream responseBody = post.getResponseBodyAsStream();
		String nodeRef = DocumentUtils.convertStreamToString(responseBody);

		//Check for code 200		
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception(nodeRef);
		}

		return nodeRef;
	}
}
