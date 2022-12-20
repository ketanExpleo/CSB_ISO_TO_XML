//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2022.07.10 at 04:24:59 PM IST
//


package com.fss.aeps.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for errorMessage complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="errorMessage"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="errorCd" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="errorDtl" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "errorMessage", propOrder = {
    "errorCd",
    "errorDtl"
})
public class ErrorMessage {

    @XmlElement(required = true)
    protected String errorCd;
    @XmlElement(required = true)
    protected String errorDtl;



    public ErrorMessage() {}

	public ErrorMessage(String errorCd, String errorDtl) {
		this.errorCd = errorCd;
		this.errorDtl = errorDtl;
	}


	/**
     * Gets the value of the errorCd property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getErrorCd() {
        return errorCd;
    }

    /**
     * Sets the value of the errorCd property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setErrorCd(String value) {
        this.errorCd = value;
    }

    /**
     * Gets the value of the errorDtl property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getErrorDtl() {
        return errorDtl;
    }

    /**
     * Sets the value of the errorDtl property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setErrorDtl(String value) {
        this.errorDtl = value;
    }

}