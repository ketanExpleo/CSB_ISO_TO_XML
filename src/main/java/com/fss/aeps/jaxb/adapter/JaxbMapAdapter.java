package com.fss.aeps.jaxb.adapter;

import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

//@XmlJavaTypeAdapter(JaxbDateAdapter.class)
public final class JaxbMapAdapter extends XmlAdapter<String, Map<String, Object>>{

	@Override
	public final Map<String, Object> unmarshal(final String data) throws Exception {
		if(data == null) return null;
		return new HashMap<>();
	}

	@Override
	public final String marshal(final Map<String, Object> data) throws Exception {
		if(data == null) return null;
		return data.toString();
	}
}
