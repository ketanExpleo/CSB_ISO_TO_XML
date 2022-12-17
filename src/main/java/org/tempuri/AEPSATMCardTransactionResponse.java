
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
 *         &lt;element name="AEPS_ATMCard_TransactionResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "aepsatmCardTransactionResult"
})
@XmlRootElement(name = "AEPS_ATMCard_TransactionResponse")
public class AEPSATMCardTransactionResponse {

    @XmlElementRef(name = "AEPS_ATMCard_TransactionResult", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> aepsatmCardTransactionResult;

    /**
     * Gets the value of the aepsatmCardTransactionResult property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<String> getAEPSATMCardTransactionResult() {
        return aepsatmCardTransactionResult;
    }

    /**
     * Sets the value of the aepsatmCardTransactionResult property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setAEPSATMCardTransactionResult(JAXBElement<String> value) {
        this.aepsatmCardTransactionResult = value;
    }

}
