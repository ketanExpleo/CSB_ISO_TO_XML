package com.fss.aeps.util;

import static java.util.Collections.singletonList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUtils {

	public static final DocumentBuilderFactory dbf = XMLUtils.newNSInstance();

	//@formatter:off
	public static final XMLSignature getXMLSignature() throws Exception {
		final XMLSignatureFactory    xmlSignatureFactory    = XMLSignatureFactory.getInstance("DOM");
		final DigestMethod           digestMethod           = xmlSignatureFactory.newDigestMethod(DigestMethod.SHA256, null);
		final List<Transform>        transforms             = singletonList(xmlSignatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
		final Reference              reference              = xmlSignatureFactory.newReference("", digestMethod, transforms, null, null);
		final CanonicalizationMethod canonicalizationMethod = xmlSignatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
		final SignatureMethod        signatureMethod        = xmlSignatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA256, (SignatureMethodParameterSpec) null);
		final SignedInfo             signedInfo             = xmlSignatureFactory.newSignedInfo(canonicalizationMethod, signatureMethod, singletonList(reference));
		final XMLSignature           xmlSignature           = xmlSignatureFactory.newXMLSignature(signedInfo, null);
		return xmlSignature;
	}

	//@formatter:on
	public static final ByteArrayInputStream documentToInputStream(final Document document) {
		try {
			final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			final Result outputTarget = new StreamResult(outputStream);
			final Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(new DOMSource(document), outputTarget);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (Exception e) {
			throw new InvalidXmlSignatureException("error converting xml document to bytestream", e);
		}
	}

	public static final String documentToString(final Document document) {
		try {
			final DOMSource domSource = new DOMSource(document);
			final StringWriter writer = new StringWriter();
			final TransformerFactory factory = TransformerFactory.newInstance();
			final Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(domSource, new StreamResult(writer));
			return writer.toString();
		} catch (Exception e) {
			throw new InvalidXmlSignatureException("exception while converting document to string.", e);
		}

	}

	public static final String documentToFormattedString(final Document document) {
		try {
			final DOMSource domSource = new DOMSource(document);
			final StringWriter writer = new StringWriter();
			final TransformerFactory factory = TransformerFactory.newInstance();
			final Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(domSource, new StreamResult(writer));
			return writer.toString();
		} catch (Exception e) {
			throw new InvalidXmlSignatureException("exception while converting document to string.", e);
		}

	}

	public static final byte[] documentToByteArray(final Document document) {
		try {
			final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			final StreamResult outputTarget = new StreamResult(outputStream);
			final Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.transform(new DOMSource(document), outputTarget);
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new InvalidXmlSignatureException("exception while validating xml signature.", e);
		}
	}

	public static final Document bytesToDocument(final byte[] xml) {
		try {
			return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
		} catch (Exception e) {
			throw new InvalidXmlSignatureException("exception while converting byte array to document xml signature.", e);
		}
	}

	public static final Document stringToDocument(final String xml){
		try {
			return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static final Document inputstreamToDocument(final InputStream xml) throws SAXException, IOException, ParserConfigurationException {
		return dbf.newDocumentBuilder().parse(xml);
	}

	public static final Document generateXMLDigitalSignature(final PublicKey pubKey, final PrivateKey privKey, final Document document) throws Exception {
		final XMLSignature xmlSignature = getXMLSignature();
		final DOMSignContext domSignContext = new DOMSignContext(privKey, document.getDocumentElement());
		xmlSignature.sign(domSignContext);
		return document;
	}

	public static final boolean validateXMLDigitalSignature(final Key pubKey, final Document document) {
		try {
			final NodeList nodeList = document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			if (nodeList.getLength() == 0)
				throw new XMLSignatureException("No XML Digital Signature Found, document is discarded");
			final DOMValidateContext domValidateContext = new DOMValidateContext(pubKey, nodeList.item(0));
			final XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
			final XMLSignature xmlSignature = xmlSignatureFactory.unmarshalXMLSignature(domValidateContext);
			final boolean result = xmlSignature.validate(domValidateContext);
			return result;
		} catch (Exception e) {
			throw new InvalidXmlSignatureException("exception while validating xml signature.", e);
		}
	}

	private static final DocumentBuilderFactory newNSInstance() {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		return factory;
	}
}
