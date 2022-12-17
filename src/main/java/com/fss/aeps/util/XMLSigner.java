package com.fss.aeps.util;

import java.io.BufferedInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.dom.DOMSignContext;

import org.springframework.core.io.Resource;
import org.w3c.dom.Document;


//@formatter:off
public class XMLSigner {

	private final PublicKey  publicKey;
	private final PrivateKey privateKey;

	public XMLSigner(PublicKey publicKey, PrivateKey privateKey) {
		this.publicKey  = publicKey;
		this.privateKey = privateKey;
	}

	public XMLSigner(final Resource resource, final String password, final String alias){
		PublicKey  publicKey = null;
		PrivateKey privateKey = null;
		try {
			final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			try(final BufferedInputStream fis = new BufferedInputStream(resource.getInputStream())){
				keyStore.load(fis, password.toCharArray());
			}
			publicKey  = keyStore.getCertificate(alias).getPublicKey();
			privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
		} catch (Exception e) {e.printStackTrace();}
		this.publicKey  = publicKey;
		this.privateKey = privateKey;
	}

	public final String generateXMLDigitalSignature(final String xml) throws Exception {
		final Document document       = XMLUtils.stringToDocument(xml);
		final Document signedDocument = generateXMLDigitalSignature(document);
		return XMLUtils.documentToString(signedDocument);
	}

	public final Document generateXMLDigitalSignature(final Document document) {
		try {
			XMLSignature xmlSignature = XMLUtils.getXMLSignature();
			final DOMSignContext domSignContext = new DOMSignContext(privateKey, document.getDocumentElement());
			xmlSignature.sign(domSignContext);
			return document;
		} catch (Exception e) { throw new InvalidXmlSignatureException("error generating xml signature", e); }

	}

	public final boolean isValidDigitalSignature(String xml) throws Exception {
		return XMLUtils.validateXMLDigitalSignature(publicKey, XMLUtils.stringToDocument(xml));
	}
}
