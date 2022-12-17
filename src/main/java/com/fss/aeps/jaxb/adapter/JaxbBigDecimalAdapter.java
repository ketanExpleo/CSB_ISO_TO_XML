package com.fss.aeps.jaxb.adapter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

//@XmlJavaTypeAdapter(JaxbBigDecimalAdapter.class)
public final class JaxbBigDecimalAdapter extends XmlAdapter<String, BigDecimal>{

	private static final ThreadLocal<DecimalFormat> decimalFormat = ThreadLocal.withInitial(() -> new DecimalFormat("#0.00"));

	@Override
	public final BigDecimal unmarshal(final String decimal) throws Exception {
		if(decimal == null) return new BigDecimal(0);
		return new BigDecimal(decimal);
	}

	@Override
	public final String marshal(final BigDecimal decimal) throws Exception {
		if(decimal == null) return null;
		return decimalFormat.get().format(decimal);
	}
}
