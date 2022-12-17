//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2022.07.10 at 04:24:59 PM IST
//


package com.fss.aeps.jaxb;

import java.util.Date;

import com.fss.aeps.jaxb.adapter.JaxbDateAdapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for headType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="headType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iso8583Element" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="iso8583Payload" use="required" type="{http://www.w3.org/2001/XMLSchema}base64Binary" /&gt;
 *                 &lt;attribute name="port" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="trackingId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ver" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="ts" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="orgId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="msgId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="prodType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="destinationOrgId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="aepsOld" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "headType", propOrder = {
    "iso8583Element"
})
public class HeadType {

    protected HeadType.Iso8583Element iso8583Element;
    @XmlAttribute(name = "ver", required = true)
    protected String ver;
    @XmlJavaTypeAdapter(JaxbDateAdapter.class)
    @XmlAttribute(name = "ts", required = true)
    protected Date ts;
    @XmlAttribute(name = "orgId", required = true)
    protected String orgId;
    @XmlAttribute(name = "msgId", required = true)
    protected String msgId;
    @XmlAttribute(name = "prodType", required = true)
    protected ProdType prodType;
    @XmlAttribute(name = "destinationOrgId")
    protected String destinationOrgId;
    @XmlAttribute(name = "aepsOld")
    protected Boolean aepsOld;
    @XmlAttribute(name = "callbackEndpointIP")
    protected String callbackEndpointIP;

    /**
     * Gets the value of the iso8583Element property.
     *
     * @return
     *     possible object is
     *     {@link HeadType.Iso8583Element }
     *
     */
    public HeadType.Iso8583Element getIso8583Element() {
        return iso8583Element;
    }

    /**
     * Sets the value of the iso8583Element property.
     *
     * @param value
     *     allowed object is
     *     {@link HeadType.Iso8583Element }
     *
     */
    public void setIso8583Element(HeadType.Iso8583Element value) {
        this.iso8583Element = value;
    }

    /**
     * Gets the value of the ver property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVer() {
        return ver;
    }

    /**
     * Sets the value of the ver property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVer(String value) {
        this.ver = value;
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
     * Gets the value of the orgId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * Sets the value of the orgId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrgId(String value) {
        this.orgId = value;
    }

    /**
     * Gets the value of the msgId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * Sets the value of the msgId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMsgId(String value) {
        this.msgId = value;
    }

    /**
     * Gets the value of the prodType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public ProdType getProdType() {
        return prodType;
    }

    /**
     * Sets the value of the prodType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProdType(ProdType value) {
        this.prodType = value;
    }

    /**
     * Gets the value of the destinationOrgId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDestinationOrgId() {
        return destinationOrgId;
    }

    /**
     * Sets the value of the destinationOrgId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDestinationOrgId(String value) {
        this.destinationOrgId = value;
    }

    /**
     * Gets the value of the aepsOld property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isAepsOld() {
        return aepsOld;
    }

    /**
     * Sets the value of the aepsOld property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setAepsOld(Boolean value) {
        this.aepsOld = value;
    }


    public String getCallbackEndpointIP() {
		return callbackEndpointIP;
	}

	public void setCallbackEndpointIP(String callbackEndpointIP) {
		this.callbackEndpointIP = callbackEndpointIP;
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
     *       &lt;attribute name="iso8583Payload" use="required" type="{http://www.w3.org/2001/XMLSchema}base64Binary" /&gt;
     *       &lt;attribute name="port" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="trackingId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Iso8583Element {

        @XmlAttribute(name = "iso8583Payload", required = true)
        protected byte[] iso8583Payload;
        @XmlAttribute(name = "port", required = true)
        protected String port;
        @XmlAttribute(name = "trackingId")
        protected String trackingId;

        /**
         * Gets the value of the iso8583Payload property.
         *
         * @return
         *     possible object is
         *     byte[]
         */
        public byte[] getIso8583Payload() {
            return iso8583Payload;
        }

        /**
         * Sets the value of the iso8583Payload property.
         *
         * @param value
         *     allowed object is
         *     byte[]
         */
        public void setIso8583Payload(byte[] value) {
            this.iso8583Payload = value;
        }

        /**
         * Gets the value of the port property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getPort() {
            return port;
        }

        /**
         * Sets the value of the port property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setPort(String value) {
            this.port = value;
        }

        /**
         * Gets the value of the trackingId property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getTrackingId() {
            return trackingId;
        }

        /**
         * Sets the value of the trackingId property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setTrackingId(String value) {
            this.trackingId = value;
        }




    }

}
