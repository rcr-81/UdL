package com.smile.transformer;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.springframework.extensions.webscripts.WebScriptException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import es.mityc.firmaJava.libreria.utilidades.Base64;

public class CdocxToDocx extends AbstractContentTransformer2 {

	@Override
	public boolean isTransformable(String sourceMimetype,
			String targetMimetype, TransformationOptions options) {
		if (sourceMimetype.equals("application/cdocx")
				&& targetMimetype
						.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void transformInternal(ContentReader reader,
			ContentWriter writer, TransformationOptions options)
			throws Exception {
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(reader
					.getContentInputStream()));
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("/Documento/Contenido/text()");
			Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
			String contentEncoded = node.getNodeValue();
			byte[] contentDecoded = Base64.decode(contentEncoded);
			writer.putContent(new ByteArrayInputStream(contentDecoded));
		}

		catch (Exception e) {
			throw new WebScriptException("Unable get encoded PDF content.");
		}
	}
}