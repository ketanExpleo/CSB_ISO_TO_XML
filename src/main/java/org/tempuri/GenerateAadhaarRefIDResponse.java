
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
 *         &lt;element name="GenerateAadhaarRefIDResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "generateAadhaarRefIDResult"
})
@XmlRootElement(name = "GenerateAadhaarRefIDResponse")
public class GenerateAadhaarRefIDResponse {

    @XmlElementRef(name = "GenerateAadhaarRefIDResult", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> generateAadhaarRefIDResult;

    /**
     * Gets the value of the generateAadhaarRefIDResult property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getGenerateAadhaarRefIDResult() {
        return generateAadhaarRefIDResult;
    }

    /**
     * Sets the value of the generateAadhaarRefIDResult property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setGenerateAadhaarRefIDResult(JAXBElement<String> value) {
        this.generateAadhaarRefIDResult = value;
    }

}
