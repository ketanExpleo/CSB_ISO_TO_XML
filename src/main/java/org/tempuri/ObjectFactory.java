
package org.tempuri;

import javax.xml.namespace.QName;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.tempuri package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GenerateAadhaarRefIDForAadhaaarEncryptedaadhaar_QNAME = new QName("http://tempuri.org/", "Encryptedaadhaar");
    private final static QName _SMSProcessEventid_QNAME = new QName("http://tempuri.org/", "eventid");
    private final static QName _SMSProcessCountyCallCode_QNAME = new QName("http://tempuri.org/", "countyCallCode");
    private final static QName _SMSProcessMessageXml_QNAME = new QName("http://tempuri.org/", "message_xml");
    private final static QName _SMSProcessMobileno_QNAME = new QName("http://tempuri.org/", "mobileno");
    private final static QName _SMSProcessChannel_QNAME = new QName("http://tempuri.org/", "channel");
    private final static QName _SMSProcessRequestDate_QNAME = new QName("http://tempuri.org/", "request_date");
    private final static QName _GenerateAadhaarRefIDForAadhaaarResponseGenerateAadhaarRefIDForAadhaaarResult_QNAME = new QName("http://tempuri.org/", "GenerateAadhaarRefIDForAadhaaarResult");
    private final static QName _AEPSAadhaarTransactionResponseAEPSAadhaarTransactionResult_QNAME = new QName("http://tempuri.org/", "AEPS_Aadhaar_TransactionResult");
    private final static QName _AEPSATMCardTransactionResponseAEPSATMCardTransactionResult_QNAME = new QName("http://tempuri.org/", "AEPS_ATMCard_TransactionResult");
    private final static QName _GenerateAadhaarRefIDResponseGenerateAadhaarRefIDResult_QNAME = new QName("http://tempuri.org/", "GenerateAadhaarRefIDResult");
    private final static QName _AEPSAadhaarTransactionInputDetails_QNAME = new QName("http://tempuri.org/", "inputDetails");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tempuri
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AEPSATMCardTransactionResponse }
     *
     */
    public AEPSATMCardTransactionResponse createAEPSATMCardTransactionResponse() {
        return new AEPSATMCardTransactionResponse();
    }

    /**
     * Create an instance of {@link AEPSAadhaarTransaction }
     *
     */
    public AEPSAadhaarTransaction createAEPSAadhaarTransaction() {
        return new AEPSAadhaarTransaction();
    }

    /**
     * Create an instance of {@link GenerateAadhaarRefID }
     *
     */
    public GenerateAadhaarRefID createGenerateAadhaarRefID() {
        return new GenerateAadhaarRefID();
    }

    /**
     * Create an instance of {@link GenerateAadhaarRefIDForAadhaaarResponse }
     *
     */
    public GenerateAadhaarRefIDForAadhaaarResponse createGenerateAadhaarRefIDForAadhaaarResponse() {
        return new GenerateAadhaarRefIDForAadhaaarResponse();
    }

    /**
     * Create an instance of {@link AEPSATMCardTransaction }
     *
     */
    public AEPSATMCardTransaction createAEPSATMCardTransaction() {
        return new AEPSATMCardTransaction();
    }

    /**
     * Create an instance of {@link GenerateAadhaarRefIDResponse }
     *
     */
    public GenerateAadhaarRefIDResponse createGenerateAadhaarRefIDResponse() {
        return new GenerateAadhaarRefIDResponse();
    }

    /**
     * Create an instance of {@link GenerateAadhaarRefIDForAadhaaar }
     *
     */
    public GenerateAadhaarRefIDForAadhaaar createGenerateAadhaarRefIDForAadhaaar() {
        return new GenerateAadhaarRefIDForAadhaaar();
    }

    /**
     * Create an instance of {@link SMSProcessResponse }
     *
     */
    public SMSProcessResponse createSMSProcessResponse() {
        return new SMSProcessResponse();
    }

    /**
     * Create an instance of {@link SMSProcess }
     *
     */
    public SMSProcess createSMSProcess() {
        return new SMSProcess();
    }

    /**
     * Create an instance of {@link AEPSAadhaarTransactionResponse }
     *
     */
    public AEPSAadhaarTransactionResponse createAEPSAadhaarTransactionResponse() {
        return new AEPSAadhaarTransactionResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "Encryptedaadhaar", scope = GenerateAadhaarRefIDForAadhaaar.class)
    public JAXBElement<String> createGenerateAadhaarRefIDForAadhaaarEncryptedaadhaar(String value) {
        return new JAXBElement<>(_GenerateAadhaarRefIDForAadhaaarEncryptedaadhaar_QNAME, String.class, GenerateAadhaarRefIDForAadhaaar.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "eventid", scope = SMSProcess.class)
    public JAXBElement<String> createSMSProcessEventid(String value) {
        return new JAXBElement<>(_SMSProcessEventid_QNAME, String.class, SMSProcess.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "countyCallCode", scope = SMSProcess.class)
    public JAXBElement<String> createSMSProcessCountyCallCode(String value) {
        return new JAXBElement<>(_SMSProcessCountyCallCode_QNAME, String.class, SMSProcess.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "message_xml", scope = SMSProcess.class)
    public JAXBElement<String> createSMSProcessMessageXml(String value) {
        return new JAXBElement<>(_SMSProcessMessageXml_QNAME, String.class, SMSProcess.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "mobileno", scope = SMSProcess.class)
    public JAXBElement<String> createSMSProcessMobileno(String value) {
        return new JAXBElement<>(_SMSProcessMobileno_QNAME, String.class, SMSProcess.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "channel", scope = SMSProcess.class)
    public JAXBElement<String> createSMSProcessChannel(String value) {
        return new JAXBElement<>(_SMSProcessChannel_QNAME, String.class, SMSProcess.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "request_date", scope = SMSProcess.class)
    public JAXBElement<String> createSMSProcessRequestDate(String value) {
        return new JAXBElement<>(_SMSProcessRequestDate_QNAME, String.class, SMSProcess.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "Encryptedaadhaar", scope = GenerateAadhaarRefID.class)
    public JAXBElement<String> createGenerateAadhaarRefIDEncryptedaadhaar(String value) {
        return new JAXBElement<>(_GenerateAadhaarRefIDForAadhaaarEncryptedaadhaar_QNAME, String.class, GenerateAadhaarRefID.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GenerateAadhaarRefIDForAadhaaarResult", scope = GenerateAadhaarRefIDForAadhaaarResponse.class)
    public JAXBElement<String> createGenerateAadhaarRefIDForAadhaaarResponseGenerateAadhaarRefIDForAadhaaarResult(String value) {
        return new JAXBElement<>(_GenerateAadhaarRefIDForAadhaaarResponseGenerateAadhaarRefIDForAadhaaarResult_QNAME, String.class, GenerateAadhaarRefIDForAadhaaarResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "AEPS_Aadhaar_TransactionResult", scope = AEPSAadhaarTransactionResponse.class)
    public JAXBElement<String> createAEPSAadhaarTransactionResponseAEPSAadhaarTransactionResult(String value) {
        return new JAXBElement<>(_AEPSAadhaarTransactionResponseAEPSAadhaarTransactionResult_QNAME, String.class, AEPSAadhaarTransactionResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "AEPS_ATMCard_TransactionResult", scope = AEPSATMCardTransactionResponse.class)
    public JAXBElement<String> createAEPSATMCardTransactionResponseAEPSATMCardTransactionResult(String value) {
        return new JAXBElement<>(_AEPSATMCardTransactionResponseAEPSATMCardTransactionResult_QNAME, String.class, AEPSATMCardTransactionResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GenerateAadhaarRefIDResult", scope = GenerateAadhaarRefIDResponse.class)
    public JAXBElement<String> createGenerateAadhaarRefIDResponseGenerateAadhaarRefIDResult(String value) {
        return new JAXBElement<>(_GenerateAadhaarRefIDResponseGenerateAadhaarRefIDResult_QNAME, String.class, GenerateAadhaarRefIDResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "inputDetails", scope = AEPSAadhaarTransaction.class)
    public JAXBElement<String> createAEPSAadhaarTransactionInputDetails(String value) {
        return new JAXBElement<>(_AEPSAadhaarTransactionInputDetails_QNAME, String.class, AEPSAadhaarTransaction.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "inputDetails", scope = AEPSATMCardTransaction.class)
    public JAXBElement<String> createAEPSATMCardTransactionInputDetails(String value) {
        return new JAXBElement<>(_AEPSAadhaarTransactionInputDetails_QNAME, String.class, AEPSATMCardTransaction.class, value);
    }

}
