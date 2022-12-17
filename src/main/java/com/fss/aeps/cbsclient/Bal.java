package com.fss.aeps.cbsclient;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Bal", propOrder = {
    "Amt",
    "DrCr"
})
public class Bal {

	@XmlElement(name="Amt")
	public BigDecimal Amt;

	@XmlElement(name="DrCr")
	public String DrCr;
}
