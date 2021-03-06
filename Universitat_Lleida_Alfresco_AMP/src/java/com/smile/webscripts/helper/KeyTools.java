package com.smile.webscripts.helper;

/* 
 * ################################################################
 * 
 * ProActive: The Java(TM) library for Parallel, Distributed, 
 *            Concurrent computing with Security and Mobility
 * 
 * Copyright (C) 1997-2NRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *  
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s): 
 * 
 * ################################################################
 */ 

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;


public class KeyTools {
	static Logger log = Logger.getLogger(KeyTools.class);

	private KeyTools() {
	}

	public static KeyPair genKeys(int keysize)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		log.debug(">genKeys()");

		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA", "BC");
		keygen.initialize(keysize);

		KeyPair rsaKeys = keygen.generateKeyPair();

		log.debug("Generated " + rsaKeys.getPublic().getAlgorithm() +
				" keys with length " +
				((RSAPrivateKey) rsaKeys.getPrivate()).getPrivateExponent()
				.bitLength());

		log.debug("<genKeys()");

		return rsaKeys;
	} // genKeys

	public static KeyStore createP12(String alias, PrivateKey privKey,
			X509Certificate cert, X509Certificate cacert) throws Exception {
		Certificate[] chain;

		if (cacert == null) {
			chain = null;
		} else {
			chain = new Certificate[1];
			chain[0] = cacert;
		}

		return createP12(alias, privKey, cert, chain);
	} // createP12

	static public KeyStore createP12(String alias, PrivateKey privKey,
			X509Certificate cert, Collection<Certificate> cacerts) throws Exception {
		Certificate[] chain;
		if (cacerts == null) {
			chain = null;
		} else {
			chain = new Certificate[cacerts.size()];
			chain = (Certificate[]) cacerts.toArray(chain);
		}
		return createP12(alias, privKey, cert, chain);
	} // createP12

	public static KeyStore createP12(String alias, PrivateKey privKey,
			X509Certificate cert, Certificate[] cachain) throws Exception {
		log.debug(">createP12: alias=" + alias + ", privKey, cert=" +
				CertTools.getSubjectDN(cert) + ", cachain.length=" +
				((cachain == null) ? 0 : cachain.length));

		// Certificate chain
		if (cert == null) {
			throw new IllegalArgumentException("Parameter cert cannot be null.");
		}
		int len = 1;
		if (cachain != null) {
			len += cachain.length;
		}
		Certificate[] chain = new Certificate[len];

		// To not get a ClassCastException we need to genereate a real new certificate with BC
		CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
		chain[0] = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(
				cert.getEncoded()));

		if (cachain != null) {
			for (int i = 0; i < cachain.length; i++) {
				X509Certificate tmpcert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(
						cachain[i].getEncoded()));
				chain[i + 1] = tmpcert;
			}
		}
		if (chain.length > 1) {
			for (int i = 1; i < chain.length; i++) {
				X509Certificate cacert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(
						chain[i].getEncoded()));

				// Set attributes on CA-cert
				PKCS12BagAttributeCarrier caBagAttr = (PKCS12BagAttributeCarrier) chain[i];

				// We constuct a friendly name for the CA, and try with some parts from the DN if they exist.
				String cafriendly = CertTools.getPartFromDN(CertTools.getSubjectDN(
						cacert), "CN");

				// On the ones below we +i to make it unique, O might not be otherwise
				if (cafriendly == null) {
					cafriendly = CertTools.getPartFromDN(CertTools.getSubjectDN(
							cacert), "O") + i;
				}
				if (cafriendly == null) {
					cafriendly = CertTools.getPartFromDN(CertTools.getSubjectDN(
							cacert), "OU" + i);
				}
				if (cafriendly == null) {
					cafriendly = "CA_unknown" + i;
				}
				caBagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
						new DERBMPString(cafriendly));
			}
		}

		// Set attributes on user-cert
		PKCS12BagAttributeCarrier certBagAttr = (PKCS12BagAttributeCarrier) chain[0];
		certBagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
				new DERBMPString(alias));
		// in this case we just set the local key id to that of the public key
		certBagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
				createSubjectKeyId(chain[0].getPublicKey()));
		// "Clean" private key, i.e. remove any old attributes
		KeyFactory keyfact = KeyFactory.getInstance(privKey.getAlgorithm(), "BC");
		PrivateKey pk = keyfact.generatePrivate(new PKCS8EncodedKeySpec(
				privKey.getEncoded()));

		// Set attributes for private key
		PKCS12BagAttributeCarrier keyBagAttr = (PKCS12BagAttributeCarrier) pk;

		// in this case we just set the local key id to that of the public key
		keyBagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
				new DERBMPString(alias));
		keyBagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
				createSubjectKeyId(chain[0].getPublicKey()));
		// store the key and the certificate chain
		KeyStore store = KeyStore.getInstance("PKCS12", "BC");
		store.load(null, null);
		store.setKeyEntry(alias, pk, null, chain);
		log.debug("<createP12: alias=" + alias + ", privKey, cert=" +
				CertTools.getSubjectDN(cert) + ", cachain.length=" +
				((cachain == null) ? 0 : cachain.length));

		return store;
	} // createP12

	public static KeyStore createJKS(String alias, PrivateKey privKey,
			String password, X509Certificate cert, Certificate[] cachain)
					throws Exception {
		log.debug(">createJKS: alias=" + alias + ", privKey, cert=" +
				CertTools.getSubjectDN(cert) + ", cachain.length=" +
				((cachain == null) ? 0 : cachain.length));

		String caAlias = "cacert";

		// Certificate chain
		if (cert == null) {
			throw new IllegalArgumentException("Parameter cert cannot be null.");
		}
		int len = 1;
		if (cachain != null) {
			len += cachain.length;
		}
		Certificate[] chain = new Certificate[len];
		chain[0] = cert;
		if (cachain != null) {
			for (int i = 0; i < cachain.length; i++) {
				chain[i + 1] = cachain[i];
			}
		}

		// store the key and the certificate chain
		KeyStore store = KeyStore.getInstance("JKS");
		store.load(null, null);

		// First load the key entry
		X509Certificate[] usercert = new X509Certificate[1];
		usercert[0] = cert;
		store.setKeyEntry(alias, privKey, password.toCharArray(), usercert);

		// Add the root cert as trusted
		if (cachain != null) {
			if (!CertTools.isSelfSigned(
					(X509Certificate) cachain[cachain.length - 1])) {
				throw new IllegalArgumentException(
						"Root cert is not self-signed.");
			}
			store.setCertificateEntry(caAlias, cachain[cachain.length - 1]);
		}

		// Set the complete chain
		log.debug("Storing cert chain of length " + chain.length);
		store.setKeyEntry(alias, privKey, password.toCharArray(), chain);
		log.debug("<createJKS: alias=" + alias + ", privKey, cert=" +
				CertTools.getSubjectDN(cert) + ", cachain.length=" +
				((cachain == null) ? 0 : cachain.length));

		return store;
	} // createJKS

	public static Certificate[] getCertChain(KeyStore keyStore,
			String privateKeyAlias) throws KeyStoreException {
		System.out.println(">getCertChain: alias='" + privateKeyAlias + "'");

		Certificate[] certchain = keyStore.getCertificateChain(privateKeyAlias);
		System.out.println("Certchain retrieved from alias '" +
				privateKeyAlias + "' has length " + certchain.length);

		if (certchain.length < 1) {
			log.error("Cannot load certificate chain with alias '" +
					privateKeyAlias + "' from keystore.");
			System.out.println("<getCertChain: alias='" + privateKeyAlias +
					"', retlength=" + certchain.length);

			return certchain;
		} else if (certchain.length > 0) {
			if (CertTools.isSelfSigned(
					(X509Certificate) certchain[certchain.length - 1])) {
				System.out.println("Issuer='" +
						CertTools.getIssuerDN(
								(X509Certificate) certchain[certchain.length - 1]) +
								"'.");
				System.out.println("Subject='" +
						CertTools.getSubjectDN(
								(X509Certificate) certchain[certchain.length - 1]) +
								"'.");
				System.out.println("<getCertChain: alias='" + privateKeyAlias +
						"', retlength=" + certchain.length);

				return certchain;
			}
		}

		// If we came here, we have a cert which is not root cert in 'cert'
		ArrayList<Certificate> array = new ArrayList<Certificate>();

		for (int i = 0; i < certchain.length; i++) {
			array.add(certchain[i]);
		}

		boolean stop = false;

		while (!stop) {
			X509Certificate cert = (X509Certificate) array.get(array.size() -
					1);
			String ialias = CertTools.getPartFromDN(CertTools.getIssuerDN(cert),
					"CN");
			Certificate[] chain1 = keyStore.getCertificateChain(ialias);

			if (chain1 == null) {
				stop = true;
			} else {
				System.out.println("Loaded certificate chain with length " +
						chain1.length + " with alias '" + ialias + "'.");

				if (chain1.length == 0) {
					log.error("No RootCA certificate found!");
					stop = true;
				}

				for (int j = 0; j < chain1.length; j++) {
					array.add(chain1[j]);

					// If one cert is slefsigned, we have found a root certificate, we don't need to go on anymore
					if (CertTools.isSelfSigned((X509Certificate) chain1[j])) {
						stop = true;
					}
				}
			}
		}

		Certificate[] ret = new Certificate[array.size()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = (X509Certificate) array.get(i);
			System.out.println("Issuer='" +
					CertTools.getIssuerDN((X509Certificate) ret[i]) + "'.");
			System.out.println("Subject='" +
					CertTools.getSubjectDN((X509Certificate) ret[i]) + "'.");
		}

		System.out.println("<getCertChain: alias='" + privateKeyAlias +
				"', retlength=" + ret.length);

		return ret;
	} // getCertChain

	public static SubjectKeyIdentifier createSubjectKeyId(PublicKey pubKey) {
		try {
			ByteArrayInputStream bIn = new ByteArrayInputStream(pubKey.getEncoded());
			SubjectPublicKeyInfo info = new SubjectPublicKeyInfo((ASN1Sequence) new ASN1InputStream(
					bIn).readObject());

			return new SubjectKeyIdentifier(info);
		} catch (Exception e) {
			throw new RuntimeException("error creating key");
		}
	} // createSubjectKeyId
} // KeyTools