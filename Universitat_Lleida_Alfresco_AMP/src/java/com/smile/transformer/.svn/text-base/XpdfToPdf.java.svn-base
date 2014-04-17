package com.smile.transformer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.extensions.webscripts.WebScriptException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import es.mityc.firmaJava.libreria.utilidades.Base64;

public class XpdfToPdf extends AbstractContentTransformer2{

	@Override
	public boolean isTransformable(String sourceMimetype, String targetMimetype,
			TransformationOptions options) {
		if(sourceMimetype.equals("application/xpdf") && targetMimetype.equals("application/pdf")){
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	protected void transformInternal(ContentReader reader, ContentWriter writer,
			TransformationOptions options) throws Exception {
		PDDocument pdf = null;
		InputStream is = null;

		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			NamespaceContext ctx = new NamespaceContext() {			
				public String getNamespaceURI(String prefix) {			
					String uri;			
					if (prefix.equals("ds")){			
						uri = "http://www.w3.org/2000/09/xmldsig#";		
					}
					else{		
						uri = null;	
					}
					return uri;
				}
				public Iterator getPrefixes(String val) {return null;}		
				public String getPrefix(String uri) {return null;}			
			};
			xpath.setNamespaceContext(ctx);
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			InputSource inputSource = new InputSource(reader.getContentInputStream());
			Document doc = builder.parse(inputSource);
			String xmlEncoded = xpath.evaluate("//ds:Object[@Id='Object-1']", doc);	
			byte[] xmlDecoded = Base64.decode(xmlEncoded);
			writer.putContent(new ByteArrayInputStream(xmlDecoded));
		}

		catch (Exception e1) {
			throw new WebScriptException("Unable get encoded PDF content.");			
		}

		finally {
			if (pdf != null){
				try { pdf.close(); } catch (Throwable e) {e.printStackTrace(); }
			}
			if (is != null){
				try { is.close(); } catch (Throwable e) {e.printStackTrace(); }
			}
		} 
	}
}