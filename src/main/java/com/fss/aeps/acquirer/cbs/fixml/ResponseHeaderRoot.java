package com.fss.aeps.acquirer.cbs.fixml;

import jakarta.xml.bind.annotation.XmlElement;

public class ResponseHeaderRoot {
	@XmlElement(name = "ResponseHeader")
	public ResponseHeader ResponseHeader;
}

