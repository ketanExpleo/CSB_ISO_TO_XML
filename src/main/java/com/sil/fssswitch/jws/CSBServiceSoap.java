
package com.sil.fssswitch.jws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;

@WebService(name = "CSB_ServiceSoap", targetNamespace = "http://aeps.Csbemvonline.in/")
@XmlSeeAlso({ ObjectFactory.class })
public interface CSBServiceSoap {

	/**
	 * 
	 * @param requestData
	 * @return
	 *     returns java.lang.String
	 */
	@WebMethod(operationName = "TransactionRequest", action = "http://aeps.Csbemvonline.in/TransactionRequest")
	@WebResult(name = "TransactionRequestResult", targetNamespace = "http://aeps.Csbemvonline.in/")
	@RequestWrapper(localName = "TransactionRequest", targetNamespace = "http://aeps.Csbemvonline.in/", className = "com.sil.fssswitch.jws.TransactionRequest")
	@ResponseWrapper(localName = "TransactionRequestResponse", targetNamespace = "http://aeps.Csbemvonline.in/", className = "com.sil.fssswitch.jws.TransactionRequestResponse")
	public String transactionRequest(@WebParam(name = "requestData", targetNamespace = "http://aeps.Csbemvonline.in/") String requestData);

}
