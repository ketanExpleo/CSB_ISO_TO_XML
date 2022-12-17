package com.fss.aeps.jaxb.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

//@XmlJavaTypeAdapter(JaxbDateAdapter.class)
public final class JaxbDateAdapter extends XmlAdapter<String, Date>{

	private static final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

	@Override
	public final Date unmarshal(final String date) throws Exception {
		if(date == null) return null;
		return sdf.get().parse(date);
	}

	@Override
	public final String marshal(final Date date) throws Exception {
		if(date == null) return null;
		return sdf.get().format(date);
	}
}
