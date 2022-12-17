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
import jakarta.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for ratingType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ratingType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *       &lt;attribute name="verifiedAddress" type="{http://npci.org/upi/schema/}whiteListedConstant" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ratingType", propOrder = {
    "value"
})
public class RatingType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "verifiedAddress")
    protected WhiteListedConstant verifiedAddress;

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the verifiedAddress property.
     *
     * @return
     *     possible object is
     *     {@link WhiteListedConstant }
     *
     */
    public WhiteListedConstant getVerifiedAddress() {
        return verifiedAddress;
    }

    /**
     * Sets the value of the verifiedAddress property.
     *
     * @param value
     *     allowed object is
     *     {@link WhiteListedConstant }
     *
     */
    public void setVerifiedAddress(WhiteListedConstant value) {
        this.verifiedAddress = value;
    }

}
