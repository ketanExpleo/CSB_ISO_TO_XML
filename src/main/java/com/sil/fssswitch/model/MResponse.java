package com.sil.fssswitch.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MRESPONSE")
public class MResponse {

	@XmlElement(name = "SID")
	public Object sid;
	@XmlElement(name = "TYPE")
	public int    type;
	@XmlElement(name = "DATA")
	public String data;
}
