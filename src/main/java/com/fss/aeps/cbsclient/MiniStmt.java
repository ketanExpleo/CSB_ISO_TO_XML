package com.fss.aeps.cbsclient;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MiniStmt", propOrder = {
    "Bal",
    "row"
})
public class MiniStmt {

	@XmlElement(name="Bal")
	public Bal Bal;

	@XmlElement(name="row")
	public List<Row> row;
}
