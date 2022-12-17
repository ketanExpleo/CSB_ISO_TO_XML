package com.sil.fssswitch.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RESPONSE")
public class Response {

	@XmlElement(name = "RRN") public String                rrn;
	@XmlElement(name = "RESPONSE_CODE") public String      responseCode;
	@XmlElement(name = "RESPONSE_DESC") public String      responseDesc;
	@XmlElement(name = "CUSTOMER_NAME") public String      customerName;
	@XmlElement(name = "ACC_LED_BALANCE") public double    accLEDBalance;
	@XmlElement(name = "ACC_AVALIB_BALANCE") public double accAvalibBalance;
	@XmlElement(name = "TXN_AUTH_CODE") public String      txnAuthCode;
	@XmlElement(name = "UIDAI_AUTH_CODE") public String    uidaiAuthCode;

	@XmlElement(name = "FROM_ACCOUNT_NO") public String fromAccount;
	@XmlElement(name = "MINISTMT") public String        miniStatement;
	@XmlElement(name = "EMV_CHIP_DATA") public String   emvChipData;
	
	//added by ketan k
	@XmlElement(name = "MSG_TYPE") public String   msgType;
	@XmlElement(name = "RESP_DESC") public String   respDesc;
	@XmlElement(name = "MERCHANT_DETS") public String   merchantDets;
	@XmlElement(name = "MERAVAILBAL") public String   merAvailBal;
	@XmlElement(name = "ACCOUNT_BAL") public String   accountBal;
	@XmlElement(name = "AUTH_ID") public String   authId;
	@XmlElement(name = "LEDGER_BAL") public String   ledgerBal;
}