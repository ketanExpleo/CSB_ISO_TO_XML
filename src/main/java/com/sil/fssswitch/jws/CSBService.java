
package com.sil.fssswitch.jws;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceFeature;


@WebServiceClient(name = "CSB_Service", targetNamespace = "http://aeps.Csbemvonline.in/", wsdlLocation = "https://aepstest.fssnet.co.in/CSB_EMV_Services_SIT/Services/CSB_Service.asmx?wsdl")
public class CSBService extends Service {

	private final static URL                 CSBSERVICE_WSDL_LOCATION;
	private final static WebServiceException CSBSERVICE_EXCEPTION;
	private final static QName               CSBSERVICE_QNAME = new QName("http://aeps.Csbemvonline.in/", "CSB_Service");

	static {
		URL                 url = null;
		WebServiceException e   = null;
		try {
			url = new URL("https://aepstest.fssnet.co.in/CSB_EMV_Services_SIT/Services/CSB_Service.asmx?wsdl");
		} catch (MalformedURLException ex) {
			e = new WebServiceException(ex);
		}
		CSBSERVICE_WSDL_LOCATION = url;
		CSBSERVICE_EXCEPTION     = e;
	}

	public CSBService() {
		super(__getWsdlLocation(), CSBSERVICE_QNAME);
	}

	public CSBService(WebServiceFeature... features) {
		super(__getWsdlLocation(), CSBSERVICE_QNAME, features);
	}

	public CSBService(URL wsdlLocation) {
		super(wsdlLocation, CSBSERVICE_QNAME);
	}

	public CSBService(URL wsdlLocation, WebServiceFeature... features) {
		super(wsdlLocation, CSBSERVICE_QNAME, features);
	}

	public CSBService(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public CSBService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
		super(wsdlLocation, serviceName, features);
	}

	/**
	 * 
	 * @return
	 *     returns CSBServiceSoap
	 */
	@WebEndpoint(name = "CSB_ServiceSoap")
	public CSBServiceSoap getCSBServiceSoap() {
		return super.getPort(new QName("http://aeps.Csbemvonline.in/", "CSB_ServiceSoap"), CSBServiceSoap.class);
	}

	/**
	 * 
	 * @param features
	 *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
	 * @return
	 *     returns CSBServiceSoap
	 */
	@WebEndpoint(name = "CSB_ServiceSoap")
	public CSBServiceSoap getCSBServiceSoap(WebServiceFeature... features) {
		return super.getPort(new QName("http://aeps.Csbemvonline.in/", "CSB_ServiceSoap"), CSBServiceSoap.class, features);
	}

	/**
	 * 
	 * @return
	 *     returns CSBServiceSoap
	 */
	@WebEndpoint(name = "CSB_ServiceSoap12")
	public CSBServiceSoap getCSBServiceSoap12() {
		return super.getPort(new QName("http://aeps.Csbemvonline.in/", "CSB_ServiceSoap12"), CSBServiceSoap.class);
	}

	/**
	 * 
	 * @param features
	 *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
	 * @return
	 *     returns CSBServiceSoap
	 */
	@WebEndpoint(name = "CSB_ServiceSoap12")
	public CSBServiceSoap getCSBServiceSoap12(WebServiceFeature... features) {
		return super.getPort(new QName("http://aeps.Csbemvonline.in/", "CSB_ServiceSoap12"), CSBServiceSoap.class, features);
	}

	private static URL __getWsdlLocation() {
		if (CSBSERVICE_EXCEPTION != null) { throw CSBSERVICE_EXCEPTION; }
		return CSBSERVICE_WSDL_LOCATION;
	}

}
