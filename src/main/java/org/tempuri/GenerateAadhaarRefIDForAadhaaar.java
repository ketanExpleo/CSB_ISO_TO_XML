
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
 *         &lt;element name="Encryptedaadhaar" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="channel" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
    "encryptedaadhaar",
    "channel"
})
@XmlRootElement(name = "GenerateAadhaarRefIDForAadhaaar")
public class GenerateAadhaarRefIDForAadhaaar {

    @XmlElementRef(name = "Encryptedaadhaar", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> encryptedaadhaar;
    protected Integer channel;

    /**
     * Gets the value of the encryptedaadhaar property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getEncryptedaadhaar() {
        return encryptedaadhaar;
    }

    /**
     * Sets the value of the encryptedaadhaar property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setEncryptedaadhaar(JAXBElement<String> value) {
        this.encryptedaadhaar = value;
    }

    /**
     * Gets the value of the channel property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getChannel() {
        return channel;
    }

    /**
     * Sets the value of the channel property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setChannel(Integer value) {
        this.channel = value;
    }

}
