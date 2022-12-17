
package org.tempuri;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 *
 */
@WebService(name = "IService1", targetNamespace = "http://tempuri.org/")
@XmlSeeAlso({
    com.microsoft.schemas._2003._10.serialization.ObjectFactory.class,
    org.tempuri.ObjectFactory.class
})
public interface IService1 {


    /**
     *
     * @param inputDetails
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "AEPS_Aadhaar_Transaction", action = "http://tempuri.org/IService1/AEPS_Aadhaar_Transaction")
    @WebResult(name = "AEPS_Aadhaar_TransactionResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "AEPS_Aadhaar_Transaction", targetNamespace = "http://tempuri.org/", className = "org.tempuri.AEPSAadhaarTransaction")
    @ResponseWrapper(localName = "AEPS_Aadhaar_TransactionResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.AEPSAadhaarTransactionResponse")
    public String aepsAadhaarTransaction(
        @WebParam(name = "inputDetails", targetNamespace = "http://tempuri.org/")
        String inputDetails);

    /**
     *
     * @param inputDetails
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "AEPS_ATMCard_Transaction", action = "http://tempuri.org/IService1/AEPS_ATMCard_Transaction")
    @WebResult(name = "AEPS_ATMCard_TransactionResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "AEPS_ATMCard_Transaction", targetNamespace = "http://tempuri.org/", className = "org.tempuri.AEPSATMCardTransaction")
    @ResponseWrapper(localName = "AEPS_ATMCard_TransactionResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.AEPSATMCardTransactionResponse")
    public String aepsATMCardTransaction(
        @WebParam(name = "inputDetails", targetNamespace = "http://tempuri.org/")
        String inputDetails);

    /**
     *
     * @param encryptedaadhaar
     * @param channel
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GenerateAadhaarRefID", action = "http://tempuri.org/IService1/GenerateAadhaarRefID")
    @WebResult(name = "GenerateAadhaarRefIDResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GenerateAadhaarRefID", targetNamespace = "http://tempuri.org/", className = "org.tempuri.GenerateAadhaarRefID")
    @ResponseWrapper(localName = "GenerateAadhaarRefIDResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.GenerateAadhaarRefIDResponse")
    public String generateAadhaarRefID(
        @WebParam(name = "Encryptedaadhaar", targetNamespace = "http://tempuri.org/")
        String encryptedaadhaar,
        @WebParam(name = "channel", targetNamespace = "http://tempuri.org/")
        Integer channel);

    /**
     *
     * @param encryptedaadhaar
     * @param channel
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GenerateAadhaarRefIDForAadhaaar", action = "http://tempuri.org/IService1/GenerateAadhaarRefIDForAadhaaar")
    @WebResult(name = "GenerateAadhaarRefIDForAadhaaarResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GenerateAadhaarRefIDForAadhaaar", targetNamespace = "http://tempuri.org/", className = "org.tempuri.GenerateAadhaarRefIDForAadhaaar")
    @ResponseWrapper(localName = "GenerateAadhaarRefIDForAadhaaarResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.GenerateAadhaarRefIDForAadhaaarResponse")
    public String generateAadhaarRefIDForAadhaaar(
        @WebParam(name = "Encryptedaadhaar", targetNamespace = "http://tempuri.org/")
        String encryptedaadhaar,
        @WebParam(name = "channel", targetNamespace = "http://tempuri.org/")
        Integer channel);

    /**
     *
     * @param eventid
     * @param countyCallCode
     * @param channel
     * @param requestDate
     * @param messageXml
     * @param mobileno
     * @return
     *     returns java.lang.Integer
     */
    @WebMethod(operationName = "SMSProcess", action = "http://tempuri.org/IService1/SMSProcess")
    @WebResult(name = "SMSProcessResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SMSProcess", targetNamespace = "http://tempuri.org/", className = "org.tempuri.SMSProcess")
    @ResponseWrapper(localName = "SMSProcessResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.SMSProcessResponse")
    public Integer smsProcess(
        @WebParam(name = "countyCallCode", targetNamespace = "http://tempuri.org/")
        String countyCallCode,
        @WebParam(name = "mobileno", targetNamespace = "http://tempuri.org/")
        String mobileno,
        @WebParam(name = "channel", targetNamespace = "http://tempuri.org/")
        String channel,
        @WebParam(name = "message_xml", targetNamespace = "http://tempuri.org/")
        String messageXml,
        @WebParam(name = "eventid", targetNamespace = "http://tempuri.org/")
        String eventid,
        @WebParam(name = "request_date", targetNamespace = "http://tempuri.org/")
        String requestDate);

}
