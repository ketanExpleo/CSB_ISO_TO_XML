
package org.tempuri;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="SMSProcessResult" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
    "smsProcessResult"
})
@XmlRootElement(name = "SMSProcessResponse")
public class SMSProcessResponse {

    @XmlElement(name = "SMSProcessResult")
    protected Integer smsProcessResult;

    /**
     * Gets the value of the smsProcessResult property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getSMSProcessResult() {
        return smsProcessResult;
    }

    /**
     * Sets the value of the smsProcessResult property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setSMSProcessResult(Integer value) {
        this.smsProcessResult = value;
    }

}
