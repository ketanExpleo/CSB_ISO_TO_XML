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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Auth complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Auth"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Uses" type="{http://npci.org/upi/schema/}Uses"/&gt;
 *         &lt;element name="Meta" type="{http://npci.org/upi/schema/}Meta"/&gt;
 *         &lt;element name="Skey" type="{http://npci.org/upi/schema/}Skey"/&gt;
 *         &lt;element name="Hmac" type="{http://npci.org/upi/schema/}Hmac"/&gt;
 *         &lt;element name="Data" type="{http://npci.org/upi/schema/}Data"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="lk" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="ac" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="sa" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="uid" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="ver" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="tid" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="txn" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="rc" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Auth", propOrder = {
    "uses",
    "meta",
    "skey",
    "hmac",
    "data"
})
public class Auth {

    @XmlElement(name = "Uses", required = true)
    protected Uses uses;
    @XmlElement(name = "Meta", required = true)
    protected Meta meta;
    @XmlElement(name = "Skey", required = true)
    protected Skey skey;
    @XmlElement(name = "Hmac", required = true)
    protected Hmac hmac;
    @XmlElement(name = "Data", required = true)
    protected Data data;
    @XmlAttribute(name = "lk")
    protected String lk;
    @XmlAttribute(name = "ac")
    protected String ac;
    @XmlAttribute(name = "sa")
    protected String sa;
    @XmlAttribute(name = "uid")
    protected String uid;
    @XmlAttribute(name = "ver")
    protected String ver;
    @XmlAttribute(name = "tid")
    protected String tid;
    @XmlAttribute(name = "txn")
    protected String txn;
    @XmlAttribute(name = "rc")
    protected String rc;

    /**
     * Gets the value of the uses property.
     *
     * @return
     *     possible object is
     *     {@link Uses }
     *
     */
    public Uses getUses() {
        return uses;
    }

    /**
     * Sets the value of the uses property.
     *
     * @param value
     *     allowed object is
     *     {@link Uses }
     *
     */
    public void setUses(Uses value) {
        this.uses = value;
    }

    /**
     * Gets the value of the meta property.
     *
     * @return
     *     possible object is
     *     {@link Meta }
     *
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Sets the value of the meta property.
     *
     * @param value
     *     allowed object is
     *     {@link Meta }
     *
     */
    public void setMeta(Meta value) {
        this.meta = value;
    }

    /**
     * Gets the value of the skey property.
     *
     * @return
     *     possible object is
     *     {@link Skey }
     *
     */
    public Skey getSkey() {
        return skey;
    }

    /**
     * Sets the value of the skey property.
     *
     * @param value
     *     allowed object is
     *     {@link Skey }
     *
     */
    public void setSkey(Skey value) {
        this.skey = value;
    }

    /**
     * Gets the value of the hmac property.
     *
     * @return
     *     possible object is
     *     {@link Hmac }
     *
     */
    public Hmac getHmac() {
        return hmac;
    }

    /**
     * Sets the value of the hmac property.
     *
     * @param value
     *     allowed object is
     *     {@link Hmac }
     *
     */
    public void setHmac(Hmac value) {
        this.hmac = value;
    }

    /**
     * Gets the value of the data property.
     *
     * @return
     *     possible object is
     *     {@link Data }
     *
     */
    public Data getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     *
     * @param value
     *     allowed object is
     *     {@link Data }
     *
     */
    public void setData(Data value) {
        this.data = value;
    }

    /**
     * Gets the value of the lk property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLk() {
        return lk;
    }

    /**
     * Sets the value of the lk property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLk(String value) {
        this.lk = value;
    }

    /**
     * Gets the value of the ac property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAc() {
        return ac;
    }

    /**
     * Sets the value of the ac property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAc(String value) {
        this.ac = value;
    }

    /**
     * Gets the value of the sa property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSa() {
        return sa;
    }

    /**
     * Sets the value of the sa property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSa(String value) {
        this.sa = value;
    }

    /**
     * Gets the value of the uid property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the value of the uid property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUid(String value) {
        this.uid = value;
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
     * Gets the value of the tid property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTid() {
        return tid;
    }

    /**
     * Sets the value of the tid property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTid(String value) {
        this.tid = value;
    }

    /**
     * Gets the value of the txn property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTxn() {
        return txn;
    }

    /**
     * Sets the value of the txn property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTxn(String value) {
        this.txn = value;
    }

    /**
     * Gets the value of the rc property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRc() {
        return rc;
    }

    /**
     * Sets the value of the rc property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRc(String value) {
        this.rc = value;
    }

}
