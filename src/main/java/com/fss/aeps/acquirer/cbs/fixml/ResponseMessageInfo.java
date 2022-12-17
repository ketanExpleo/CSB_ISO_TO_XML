package com.fss.aeps.acquirer.cbs.fixml;

import jakarta.xml.bind.annotation.XmlElement;

public class ResponseMessageInfo {
	@XmlElement(name = "BankId")
	public String BankId;
	@XmlElement(name = "TimeZone")
	public Object TimeZone;
}

