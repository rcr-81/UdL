package com.smile.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.UdlPropertiesShare;

public class Utils implements ConstantsUdL {

	/**
	 * Hace una llamada a un webscript de Alfresco, retorna un String con la
	 * respuesta del webscript.
	 * 
	 * @param url
	 * @param xmlArgs
	 * @return
	 * @throws Exception
	 */
	public static String callAlfrescoWebScript(String url, String xmlArgs) throws Exception {

		// create HttpClient object
		HttpClient client = new HttpClient();

		// pre-authenticate with alfresco server
		client.getParams().setAuthenticationPreemptive(true);
		UdlPropertiesShare props = new UdlPropertiesShare();
		Credentials loginCreds = new UsernamePasswordCredentials(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD));

		// Use AuthScope.ANY since the HttpClient connects to just one URL
		client.getState().setCredentials(AuthScope.ANY, loginCreds);
		PostMethod post = new PostMethod(LOCALHOST_ALFRESCO + url);
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		xmlArgs = xmlArgs.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		Part[] parts = { new StringPart(XML_ARGUMENTS, xmlArgs, UTF8) };
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		post.setRequestEntity(entity);

		// submit the request and get the response code
		int statusCode = client.executeMethod(post);
		InputStream responseBody = post.getResponseBodyAsStream();
		String result = convertStreamToString(responseBody);

		// Check for code 200
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception(result);
		}

		return result;
	}

	/**
	 * Retorna un string a partir de un objeto InputStream.
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String convertStreamToString(InputStream is) throws IOException {
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
}