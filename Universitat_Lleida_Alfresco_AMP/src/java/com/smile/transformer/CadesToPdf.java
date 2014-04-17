package com.smile.transformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.springframework.extensions.webscripts.WebScriptException;

public class CadesToPdf extends AbstractContentTransformer2 {

	@Override
	public boolean isTransformable(String sourceMimetype,
			String targetMimetype, TransformationOptions options) {
		if (sourceMimetype.equals("application/cades")
				&& targetMimetype.equals("application/pdf")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void transformInternal(ContentReader reader,
			ContentWriter writer, TransformationOptions options)
			throws Exception {
		PDDocument pdf = null;
		InputStream is = null;

		try {
			CMSSignedData signedData = new CMSSignedData(getBytes(reader
					.getContentInputStream()));
			CMSProcessable signedContent = signedData.getSignedContent();

			if (signedContent != null) {
				byte[] originalContent = (byte[]) signedContent.getContent();
				writer.putContent(new ByteArrayInputStream(originalContent));
			}
		}

		catch (Exception e1) {
			throw new WebScriptException("Unable get encoded PDF content.");
		}

		finally {
			if (pdf != null) {
				try {
					pdf.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static byte[] getBytes(InputStream is) throws IOException {

		int len;
		int size = 1024;
		byte[] buf;

		if (is instanceof ByteArrayInputStream) {
			size = is.available();
			buf = new byte[size];
			len = is.read(buf, 0, size);
		} else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			buf = new byte[size];
			while ((len = is.read(buf, 0, size)) != -1)
				bos.write(buf, 0, len);
			buf = bos.toByteArray();
		}
		return buf;
	}
}