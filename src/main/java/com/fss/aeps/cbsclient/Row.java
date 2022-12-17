package com.fss.aeps.cbsclient;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Row", propOrder = {
    "S1",
    "Dt",
    "DrCr",
    "Narr",
    "Amt"
})
public class Row {

	@XmlElement(name="S1")
	public String S1;

	@XmlElement(name="Dt")
	public String Dt;

	@XmlElement(name="DrCr")
	public String DrCr;

	@XmlElement(name="Narr")
	public String Narr;

	@XmlElement(name="Amt")
	public String Amt;
}
