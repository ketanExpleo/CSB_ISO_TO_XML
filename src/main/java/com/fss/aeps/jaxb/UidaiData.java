//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2022.07.10 at 04:24:59 PM IST
//

package com.fss.aeps.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for uidaiData complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="uidaiData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="KycRes" type="{http://npci.org/upi/schema/}kycRes"/&gt;
 *         &lt;element name="TokenizeRes" type="{http://npci.org/upi/schema/}tokenizeRes"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="info" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="err" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="txn" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uidaiData")
public class UidaiData {

	@XmlAttribute(name = "info")
	protected String info;
	@XmlAttribute(name = "err")
	protected String err;
	@XmlAttribute(name = "txn")
	protected String txn;

	/**
	 * Gets the value of the info property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * Sets the value of the info property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setInfo(String value) {
		this.info = value;
	}

	/**
	 * Gets the value of the err property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getErr() {
		return err;
	}

	/**
	 * Sets the value of the err property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setErr(String value) {
		this.err = value;
	}

	/**
	 * Gets the value of the txn property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getTxn() {
		return txn;
	}

	/**
	 * Sets the value of the txn property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setTxn(String value) {
		this.txn = value;
	}

}
