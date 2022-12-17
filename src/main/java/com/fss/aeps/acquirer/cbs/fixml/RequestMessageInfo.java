package com.fss.aeps.acquirer.cbs.fixml;

import jakarta.xml.bind.annotation.XmlElement;

public class RequestMessageInfo {
	@XmlElement(name = "BankId")
	public String BankId;
	@XmlElement(name = "TimeZone")
	public Object TimeZone;
	@XmlElement(name = "EntityId")
	public Object EntityId;
	@XmlElement(name = "EntityType")
	public Object EntityType;
	@XmlElement(name = "ArmCorrelationId")
	public Object ArmCorrelationId;
	@XmlElement(name = "MessageDateTime")
	public String MessageDateTime;
}
