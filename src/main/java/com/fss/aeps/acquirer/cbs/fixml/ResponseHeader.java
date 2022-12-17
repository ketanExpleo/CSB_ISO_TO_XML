package com.fss.aeps.acquirer.cbs.fixml;

import jakarta.xml.bind.annotation.XmlElement;

public class ResponseHeader {
	@XmlElement(name = "RequestMessageKey")
	public RequestMessageKey RequestMessageKey;
	@XmlElement(name = "ResponseMessageInfo")
	public ResponseMessageInfo ResponseMessageInfo;
	@XmlElement(name = "UBUSTransaction")
	public UBUSTransaction UBUSTransaction;
	@XmlElement(name = "HostTransaction")
	public HostTransaction HostTransaction;
}

