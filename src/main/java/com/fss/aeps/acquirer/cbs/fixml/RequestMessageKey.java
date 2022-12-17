package com.fss.aeps.acquirer.cbs.fixml;

import jakarta.xml.bind.annotation.XmlElement;

public class RequestMessageKey {

	@XmlElement(name = "RequestUUID")
	public String	RequestUUID;
	@XmlElement(name = "ServiceRequestId")
	public String	ServiceRequestId;
	@XmlElement(name = "ServiceRequestVersion")
	public String	ServiceRequestVersion;
	@XmlElement(name = "ChannelId")
	public String	ChannelId;
}
