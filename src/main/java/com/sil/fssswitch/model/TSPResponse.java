package com.sil.fssswitch.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TSPRESPONSE")
public class TSPResponse {

	@XmlElement(name = "RESPONSE_CODE")
	public String responseCode;
	
	@XmlElement(name = "RESPONSE_DESC")
	public String responseDesc;
	
	
}
