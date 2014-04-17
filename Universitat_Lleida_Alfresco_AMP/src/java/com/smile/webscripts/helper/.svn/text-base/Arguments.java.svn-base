package com.smile.webscripts.helper;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class Arguments implements ConstantsUdL {
	
	/**
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public static Element getArguments(WebScriptRequest req) throws Exception {
		String arguments = req.getParameter(FORM_PARAM_ARGS);		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document argsDoc = db.parse(new InputSource(new ByteArrayInputStream(arguments.getBytes(UTF8))));
		Element args = argsDoc.getDocumentElement();
		return args;
	}
}
