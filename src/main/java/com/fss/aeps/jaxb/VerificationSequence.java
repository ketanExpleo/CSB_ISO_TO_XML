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
 * <p>Java class for VerificationSequence complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="VerificationSequence"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SendingTimestamp" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Sequence" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="ChkTxnMsgId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="respCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VerificationSequence", propOrder = {
    "sendingTimestamp",
    "sequence",
    "chkTxnMsgId",
    "respCode"
})
public class VerificationSequence {

    @XmlElement(name = "SendingTimestamp", required = true)
    protected String sendingTimestamp;
    @XmlElement(name = "Sequence")
    protected int sequence;
    @XmlElement(name = "ChkTxnMsgId", required = true)
    protected String chkTxnMsgId;
    @XmlElement(required = true)
    protected String respCode;

    /**
     * Gets the value of the sendingTimestamp property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSendingTimestamp() {
        return sendingTimestamp;
    }

    /**
     * Sets the value of the sendingTimestamp property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSendingTimestamp(String value) {
        this.sendingTimestamp = value;
    }

    /**
     * Gets the value of the sequence property.
     *
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * Sets the value of the sequence property.
     *
     */
    public void setSequence(int value) {
        this.sequence = value;
    }

    /**
     * Gets the value of the chkTxnMsgId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getChkTxnMsgId() {
        return chkTxnMsgId;
    }

    /**
     * Sets the value of the chkTxnMsgId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setChkTxnMsgId(String value) {
        this.chkTxnMsgId = value;
    }

    /**
     * Gets the value of the respCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRespCode() {
        return respCode;
    }

    /**
     * Sets the value of the respCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRespCode(String value) {
        this.respCode = value;
    }

}
