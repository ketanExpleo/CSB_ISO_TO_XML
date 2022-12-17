
package org.tempuri;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="countyCallCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mobileno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="channel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="message_xml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eventid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="request_date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "countyCallCode",
    "mobileno",
    "channel",
    "messageXml",
    "eventid",
    "requestDate"
})
@XmlRootElement(name = "SMSProcess")
public class SMSProcess {

    @XmlElementRef(name = "countyCallCode", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> countyCallCode;
    @XmlElementRef(name = "mobileno", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> mobileno;
    @XmlElementRef(name = "channel", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> channel;
    @XmlElementRef(name = "message_xml", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> messageXml;
    @XmlElementRef(name = "eventid", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> eventid;
    @XmlElementRef(name = "request_date", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> requestDate;

    /**
     * Gets the value of the countyCallCode property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getCountyCallCode() {
        return countyCallCode;
    }

    /**
     * Sets the value of the countyCallCode property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setCountyCallCode(JAXBElement<String> value) {
        this.countyCallCode = value;
    }

    /**
     * Gets the value of the mobileno property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getMobileno() {
        return mobileno;
    }

    /**
     * Sets the value of the mobileno property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setMobileno(JAXBElement<String> value) {
        this.mobileno = value;
    }

    /**
     * Gets the value of the channel property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getChannel() {
        return channel;
    }

    /**
     * Sets the value of the channel property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setChannel(JAXBElement<String> value) {
        this.channel = value;
    }

    /**
     * Gets the value of the messageXml property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getMessageXml() {
        return messageXml;
    }

    /**
     * Sets the value of the messageXml property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setMessageXml(JAXBElement<String> value) {
        this.messageXml = value;
    }

    /**
     * Gets the value of the eventid property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getEventid() {
        return eventid;
    }

    /**
     * Sets the value of the eventid property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setEventid(JAXBElement<String> value) {
        this.eventid = value;
    }

    /**
     * Gets the value of the requestDate property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getRequestDate() {
        return requestDate;
    }

    /**
     * Sets the value of the requestDate property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setRequestDate(JAXBElement<String> value) {
        this.requestDate = value;
    }

}
