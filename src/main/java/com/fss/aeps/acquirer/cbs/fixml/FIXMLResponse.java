package com.fss.aeps.acquirer.cbs.fixml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FIXML")
public class FIXMLResponse {

	@XmlElement(name = "Header")
	public ResponseHeaderRoot header;

	@XmlElement(name = "Body")
	public ResponseBody body;

	@Override
	public String toString() {
		return "FIXML [body=" + body + "]";
	}



}