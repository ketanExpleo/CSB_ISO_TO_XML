package com.fss.aeps.jaxb;

import java.util.HashMap;
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
 *         &lt;element name="HbtMsg"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="type" type="{http://npci.org/upi/schema/}hbtMsgType" /&gt;
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
    "hbtMsg"
})
@XmlRootElement(name = "ReqHbt")
public class ReqHbt {

	@XmlTransient
	public final Map<String, Object>  context = new HashMap<>();

    @XmlElement(name = "Head", required = true)
    protected HeadType head;
    @XmlElement(name = "Txn", required = true)
    protected PayTrans txn;
    @XmlElement(name = "HbtMsg", required = true)
    protected ReqHbt.HbtMsg hbtMsg;

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
     * Gets the value of the hbtMsg property.
     *
     * @return
     *     possible object is
     *     {@link ReqHbt.HbtMsg }
     *
     */
    public ReqHbt.HbtMsg getHbtMsg() {
        return hbtMsg;
    }

    /**
     * Sets the value of the hbtMsg property.
     *
     * @param value
     *     allowed object is
     *     {@link ReqHbt.HbtMsg }
     *
     */
    public void setHbtMsg(ReqHbt.HbtMsg value) {
        this.hbtMsg = value;
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
     *       &lt;attribute name="type" type="{http://npci.org/upi/schema/}hbtMsgType" /&gt;
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class HbtMsg {

        @XmlAttribute(name = "type")
        protected HbtMsgType type;
        @XmlAttribute(name = "value")
        protected String value;

        /**
         * Gets the value of the type property.
         *
         * @return
         *     possible object is
         *     {@link HbtMsgType }
         *
         */
        public HbtMsgType getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         *
         * @param value
         *     allowed object is
         *     {@link HbtMsgType }
         *
         */
        public void setType(HbtMsgType value) {
            this.type = value;
        }

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

    }

}
