//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2022.07.10 at 04:24:59 PM IST
//


package com.fss.aeps.jaxb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fss.aeps.jaxb.adapter.IPayTrans;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;


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
 *         &lt;element name="Head" type="{http://npci.org/upi/schema/}headType"/&gt;
 *         &lt;element name="Meta"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Tag" maxOccurs="2" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;attribute name="name" use="required" type="{http://npci.org/upi/schema/}metaTagNameType" /&gt;
 *                           &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Txn" type="{http://npci.org/upi/schema/}payTrans"/&gt;
 *         &lt;element name="Payer" type="{http://npci.org/upi/schema/}payerType"/&gt;
 *         &lt;element name="Payees" type="{http://npci.org/upi/schema/}payeesType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "head",
    "txn",
    "payer",
    "payees",
    "extras"
})
@XmlRootElement(name = "ReqPay")
public class ReqPay implements IPayTrans {

	@XmlTransient
	public final Map<String, Object>  context = new HashMap<>();

	@XmlElementWrapper(name = "extras", required = false)
	@XmlElement(name = "KeyValue", required = true)
	public List<KeyValue> extras;

    @XmlElement(name = "Head", required = true)
    protected HeadType head;
    @XmlElement(name = "Txn", required = true)
    protected PayTrans txn;
    @XmlElement(name = "Payer", required = true)
    protected PayerType payer;
    @XmlElement(name = "Payees", required = true)
    protected PayeesType payees;

    /**
     * Gets the value of the head property.
     *
     * @return
     *     possible object is
     *     {@link HeadType }
     *
     */
    @Override
	public HeadType getHead() {
        return head;
    }

    /**
     * Sets the value of the head property.
     *
     * @param value
     *     allowed object is
     *     {@link HeadType }
     *
     */
    @Override
	public void setHead(HeadType value) {
        this.head = value;
    }


    /**
     * Gets the value of the txn property.
     *
     * @return
     *     possible object is
     *     {@link PayTrans }
     *
     */
    @Override
	public PayTrans getTxn() {
        return txn;
    }

    /**
     * Sets the value of the txn property.
     *
     * @param value
     *     allowed object is
     *     {@link PayTrans }
     *
     */
    @Override
	public void setTxn(PayTrans value) {
        this.txn = value;
    }

    /**
     * Gets the value of the payer property.
     *
     * @return
     *     possible object is
     *     {@link PayerType }
     *
     */
    @Override
	public PayerType getPayer() {
        return payer;
    }

    /**
     * Sets the value of the payer property.
     *
     * @param value
     *     allowed object is
     *     {@link PayerType }
     *
     */
    @Override
	public void setPayer(PayerType value) {
        this.payer = value;
    }

    /**
     * Gets the value of the payees property.
     *
     * @return
     *     possible object is
     *     {@link PayeesType }
     *
     */
    public PayeesType getPayees() {
        return payees;
    }

    /**
     * Sets the value of the payees property.
     *
     * @param value
     *     allowed object is
     *     {@link PayeesType }
     *
     */
    public void setPayees(PayeesType value) {
        this.payees = value;
    }



}
