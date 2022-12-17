package com.fss.aeps.jaxb.adapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import com.fss.aeps.acquirer.cbs.fixml.FIXMLRequest;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

//@XmlJavaTypeAdapter(JaxbDateAdapter.class)
public final class JaxbFixmlRequestAdapter extends XmlAdapter<String, FIXMLRequest>{

	private static final JAXBContext CONTEXT = getContext();

	@Override
	public final FIXMLRequest unmarshal(final String request) throws Exception {
		try {
			if(request == null) return null;
			FIXMLRequest fixml = (FIXMLRequest) CONTEXT.createUnmarshaller().unmarshal(new ByteArrayInputStream(Base64.getDecoder().decode(request)));
			return fixml;
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}

	@Override
	public final String marshal(final FIXMLRequest request) throws Exception {
		if(request == null) return null;
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CONTEXT.createMarshaller().marshal(request, baos);
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

	private static final JAXBContext getContext() {
		try {
			return JAXBContext.newInstance(FIXMLRequest.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
}
