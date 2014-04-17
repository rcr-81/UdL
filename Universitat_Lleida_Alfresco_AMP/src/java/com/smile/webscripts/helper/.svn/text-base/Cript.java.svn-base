package com.smile.webscripts.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.ServiceRegistry;

public class Cript extends BaseScopableProcessorExtension {	

	private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public static String sha512(String arg) throws NoSuchAlgorithmException {
		MessageDigest md;
		String out = "";
		try {
			md = MessageDigest.getInstance("SHA-512");
			md.update(arg.getBytes());
			byte[] mb = md.digest();

			for (int i = 0; i < mb.length; i++) {
				byte temp = mb[i];
				String s = Integer.toHexString(new Byte(temp));
				while (s.length() < 2) {
					s = "0" + s;
				}
				s = s.substring(s.length() - 2);
				out += s;
			}
		} 
		catch (NoSuchAlgorithmException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		return out;
	}
}