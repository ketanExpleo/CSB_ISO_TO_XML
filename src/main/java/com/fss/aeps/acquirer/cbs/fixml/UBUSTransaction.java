package com.fss.aeps.acquirer.cbs.fixml;

import jakarta.xml.bind.annotation.XmlElement;

public class UBUSTransaction {
	@XmlElement(name = "Id")
	public Object Id;
	@XmlElement(name = "Status")
	public String Status;
}

