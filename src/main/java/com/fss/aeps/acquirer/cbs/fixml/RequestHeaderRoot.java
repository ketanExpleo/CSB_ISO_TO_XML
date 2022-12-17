package com.fss.aeps.acquirer.cbs.fixml;

import jakarta.xml.bind.annotation.XmlElement;

public class RequestHeaderRoot {
	@XmlElement(name = "RequestHeader")
	public RequestHeader RequestHeader;
}