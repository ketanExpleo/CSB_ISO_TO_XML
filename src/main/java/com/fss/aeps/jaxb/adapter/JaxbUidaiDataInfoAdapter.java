package com.fss.aeps.jaxb.adapter;

import com.fss.aeps.jaxb.UidaiDataInfo;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

//@XmlJavaTypeAdapter(JaxbUidaiDataInfoAdapter.class)
public final class JaxbUidaiDataInfoAdapter extends XmlAdapter<String, UidaiDataInfo>{

	@Override
	public final UidaiDataInfo unmarshal(final String data) throws Exception {
		if(data == null) return null;
		return UidaiDataInfo.parse(data);
	}

	@Override
	public final String marshal(final UidaiDataInfo data) throws Exception {
		if(data == null) return null;
		return data.toString();
	}
}
