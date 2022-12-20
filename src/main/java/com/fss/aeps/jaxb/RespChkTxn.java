//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2022.07.10 at 04:24:59 PM IST
//


package com.fss.aeps.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="Txn" type="{http://npci.org/upi/schema/}payTrans"/&gt;
 *         &lt;element name="Resp"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Ref" type="{http://npci.org/upi/schema/}ref" maxOccurs="50" minOccurs="0"/&gt;
 *                   &lt;element name="Consent" type="{http://npci.org/upi/schema/}consentType" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name="reqMsgId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="result" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="errCode" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="opType" type="{http://npci.org/upi/schema/}payConstant" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
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
    "resp"
})
@XmlRootElement(name = "RespChkTxn")
public class RespChkTxn {

	@XmlTransient
	public final Map<String, Object>  context = new HashMap<>();

    @XmlElement(name = "Head", required = true)
    protected HeadType head;
    @XmlElement(name = "Txn", required = true)
    protected PayTrans txn;
    @XmlElement(name = "Resp", required = true)
    protected RespChkTxn.Resp resp;

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

    /**
     * Gets the value of the resp property.
     *
     * @return
     *     possible object is
     *     {@link RespChkTxn.Resp }
     *
     */
    public RespChkTxn.Resp getResp() {
        return resp;
    }

    /**
     * Sets the value of the resp property.
     *
     * @param value
     *     allowed object is
     *     {@link RespChkTxn.Resp }
     *
     */
    public void setResp(RespChkTxn.Resp value) {
        this.resp = value;
    }


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
     *         &lt;element name="Ref" type="{http://npci.org/upi/schema/}ref" maxOccurs="50" minOccurs="0"/&gt;
     *         &lt;element name="Consent" type="{http://npci.org/upi/schema/}consentType" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="reqMsgId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="result" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="errCode" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="opType" type="{http://npci.org/upi/schema/}payConstant" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "ref"
    })
    public static class Resp {

        @XmlElement(name = "Ref")
        protected List<Ref> ref;
        @XmlAttribute(name = "reqMsgId")
        protected String reqMsgId;
        @XmlAttribute(name = "result")
        protected ResultType result;
        @XmlAttribute(name = "errCode")
        protected String errCode;
        @XmlTransient
        protected String errDesc;
        @XmlAttribute(name = "opType")
        protected PayConstant opType;

        /**
         * Gets the value of the ref property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a <CODE>set</CODE> method for the ref property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRef().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Ref }
         *
         *
         */
        public List<Ref> getRef() {
            if (ref == null) {
                ref = new ArrayList<>();
            }
            return this.ref;
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
         * Gets the value of the result property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public ResultType getResult() {
            return result;
        }

        /**
         * Sets the value of the result property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setResult(ResultType value) {
            this.result = value;
        }

        /**
         * Gets the value of the errCode property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getErrCode() {
            return errCode;
        }

        /**
         * Sets the value of the errCode property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setErrCode(String value) {
            this.errCode = value;
        }

        /**
         * Gets the value of the errDesc property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        @XmlTransient
        public String getErrDesc() {
            return errDesc;
        }

        /**
         * Sets the value of the errDesc property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setErrDesc(String value) {
            this.errDesc = value;
        }

        /**
         * Gets the value of the opType property.
         *
         * @return
         *     possible object is
         *     {@link PayConstant }
         *
         */
        public PayConstant getOpType() {
            return opType;
        }

        /**
         * Sets the value of the opType property.
         *
         * @param value
         *     allowed object is
         *     {@link PayConstant }
         *
         */
        public void setOpType(PayConstant value) {
            this.opType = value;
        }

    }

}