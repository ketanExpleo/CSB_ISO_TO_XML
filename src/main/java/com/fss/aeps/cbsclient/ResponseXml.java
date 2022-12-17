package com.fss.aeps.cbsclient;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseXml", propOrder = {
    "Bal",
    "MiniStmt"
})
public class ResponseXml {

	@XmlElement(name="Bal")
	public Bal Bal;

	@XmlElement(name="MiniStmt")
	public MiniStmt MiniStmt;
}
