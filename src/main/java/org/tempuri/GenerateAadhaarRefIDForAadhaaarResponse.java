
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
 *         &lt;element name="GenerateAadhaarRefIDForAadhaaarResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "generateAadhaarRefIDForAadhaaarResult"
})
@XmlRootElement(name = "GenerateAadhaarRefIDForAadhaaarResponse")
public class GenerateAadhaarRefIDForAadhaaarResponse {

    @XmlElementRef(name = "GenerateAadhaarRefIDForAadhaaarResult", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> generateAadhaarRefIDForAadhaaarResult;

    /**
     * Gets the value of the generateAadhaarRefIDForAadhaaarResult property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getGenerateAadhaarRefIDForAadhaaarResult() {
        return generateAadhaarRefIDForAadhaaarResult;
    }

    /**
     * Sets the value of the generateAadhaarRefIDForAadhaaarResult property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setGenerateAadhaarRefIDForAadhaaarResult(JAXBElement<String> value) {
        this.generateAadhaarRefIDForAadhaaarResult = value;
    }

}
