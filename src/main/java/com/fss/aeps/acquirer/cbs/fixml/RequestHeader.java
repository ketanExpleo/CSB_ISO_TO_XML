package com.fss.aeps.acquirer.cbs.fixml;

import jakarta.xml.bind.annotation.XmlElement;

public class RequestHeader {
	@XmlElement(name = "MessageKey")
	public MessageKey MessageKey;

	@XmlElement(name = "RequestMessageInfo")
	public RequestMessageInfo RequestMessageInfo;
}
