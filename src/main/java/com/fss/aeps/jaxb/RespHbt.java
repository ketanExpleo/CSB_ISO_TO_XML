package com.fss.aeps.jaxb;

import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="Resp" type="{http://npci.org/upi/schema/}respType"/&gt;
 *         &lt;element name="Txn" type="{http://npci.org/upi/schema/}payTrans"/&gt;
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
    "resp",
    "txn"
})
@XmlRootElement(name = "RespHbt")
public class RespHbt {

	@XmlTransient
	public final Map<String, Object> context = new HashMap<>();

    @XmlElement(name = "Head", required = true)
    protected HeadType head;
    @XmlElement(name = "Resp", required = true)
    protected RespType resp;
    @XmlElement(name = "Txn", required = true)
    protected PayTrans txn;

    /**
     * Gets the value of the head property.
     *
     * @return
     *     possible object is
     *     {@link HeadType }
     *
     */
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
    public void setHead(HeadType value) {
        this.head = value;
    }

    /**
     * Gets the value of the resp property.
     *
     * @return
     *     possible object is
     *     {@link RespType }
     *
     */
    public RespType getResp() {
        return resp;
    }

    /**
     * Sets the value of the resp property.
     *
     * @param value
     *     allowed object is
     *     {@link RespType }
     *
     */
    public void setResp(RespType value) {
        this.resp = value;
    }

    /**
     * Gets the value of the txn property.
     *
     * @return
     *     possible object is
     *     {@link PayTrans }
     *
     */
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
    public void setTxn(PayTrans value) {
        this.txn = value;
    }

}
