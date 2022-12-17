package com.fss.aeps.acquirer.cbs.fixml;

import jakarta.xml.bind.annotation.XmlElement;

public class ResponseBody extends RequestBody{

	public ResponseBody() {}

	public ResponseBody(RequestBody body) {
		super(body);
	}

	@XmlElement(name = "StatusFlag")
	public String	StatusFlag;

	@XmlElement(name = "ErrorDesc")
	public String	ErrorDesc;

	@XmlElement(name = "ErrorCode")
	public String	ErrorCode;

	@XmlElement(name = "RESPONSE_CODE")
	public String	RESPONSE_CODE;

	@XmlElement(name = "ErrorDescScreen")
	public String	ErrorDescScreen;

	@XmlElement(name = "UIDAI_AUTH_CODE")
	public String	UIDAI_AUTH_CODE;

	@XmlElement(name = "ACC_AVALIB_BALANCE")
	public String	ACC_AVALIB_BALANCE;

	@XmlElement(name = "ACC_LED_BALANCE")
	public String	ACC_LED_BALANCE;

	@XmlElement(name = "CUSTOMER_NAME")
	public String	CUSTOMER_NAME;

	@XmlElement(name = "TXN_AUTH_CODE")
	public String	TXN_AUTH_CODE;

	@XmlElement(name = "AEPS_TRAN_ID")
	public String	AEPS_TRAN_ID;

	@XmlElement(name = "MINISTMT")
	public String	MINISTMT;

	@XmlElement(name = "BENEFICIARY_NAME")
	public String	BENEFICIARY_NAME;

	@XmlElement(name = "DEPOSIT_ID")
	public String	DEPOSIT_ID;

}
