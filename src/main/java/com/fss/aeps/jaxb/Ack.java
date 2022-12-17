//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2022.07.10 at 04:24:59 PM IST
//


package com.fss.aeps.jaxb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fss.aeps.jaxb.adapter.JaxbDateAdapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="errorMessages" type="{http://npci.org/upi/schema/}errorMessage" maxOccurs="100" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="api" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="reqMsgId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="err" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="ts" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="crn" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "errorMessages"
})
@XmlRootElement(name = "Ack")
public class Ack {

    protected List<ErrorMessage> errorMessages;
    @XmlAttribute(name = "api")
    protected String api;
    @XmlAttribute(name = "reqMsgId")
    protected String reqMsgId;
    @XmlAttribute(name = "err")
    protected String err;
    @XmlJavaTypeAdapter(JaxbDateAdapter.class)
    @XmlAttribute(name = "ts")
    protected Date ts;
    @XmlAttribute(name = "crn")
    protected String crn;

    /**
     * Gets the value of the errorMessages property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the errorMessages property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getErrorMessages().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ErrorMessage }
     *
     *
     */
    public List<ErrorMessage> getErrorMessages() {
        if (errorMessages == null) {
            errorMessages = new ArrayList<>();
        }
        return this.errorMessages;
    }

    /**
     * Gets the value of the api property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getApi() {
        return api;
    }

    /**
     * Sets the value of the api property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setApi(String value) {
        this.api = value;
    }

    /**
     * Gets the value of the reqMsgId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getReqMsgId() {
        return reqMsgId;
    }

    /**
     * Sets the value of the reqMsgId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReqMsgId(String value) {
        this.reqMsgId = value;
    }

    /**
     * Gets the value of the err property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getErr() {
        return err;
    }

    /**
     * Sets the value of the err property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setErr(String value) {
        this.err = value;
    }

    /**
     * Gets the value of the ts property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Date getTs() {
        return ts;
    }

    /**
     * Sets the value of the ts property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTs(Date value) {
        this.ts = value;
    }

    /**
     * Gets the value of the crn property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCrn() {
        return crn;
    }

    /**
     * Sets the value of the crn property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCrn(String value) {
        this.crn = value;
    }

}
