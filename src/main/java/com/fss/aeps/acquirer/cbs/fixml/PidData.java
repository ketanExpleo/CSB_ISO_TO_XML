package com.fss.aeps.acquirer.cbs.fixml;

import com.fss.aeps.jaxb.Data;
import com.fss.aeps.jaxb.Skey;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class PidData {
	@XmlElement(name = "DeviceInfo")
	public DeviceInfo	deviceInfo;

	@XmlElement(name = "Skey")
	public Skey			skey;

	@XmlElement(name = "Hmac")
	public String		hmac;

	@XmlElement(name = "Data")
	public Data			data;

	@Override
	public String toString() {
		return "PidData [deviceInfo=" + deviceInfo + ", skey=" + skey + ", hmac=" + hmac + ", data=" + data + "]";
	}

}
