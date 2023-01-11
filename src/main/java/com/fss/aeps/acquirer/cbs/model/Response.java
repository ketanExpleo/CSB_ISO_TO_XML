package com.fss.aeps.acquirer.cbs.model;

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

	@XmlElement(name = "FROM_ACCOUNT_NO") public String fromAccountNo;
	@XmlElement(name = "MINISTMT") public String        miniStatement;
	@XmlElement(name = "EMV_CHIP_DATA") public String   emvChipData;
	
	//added by ketan k
	@XmlElement(name = "MSG_TYPE") public String   msgType;
	@XmlElement(name = "RESP_DESC") public String   respDesc;
	@XmlElement(name = "MERCHANT_DETS") public String   merchantDets;
	@XmlElement(name = "MERAVAILBAL") public String   merAvailBal;
	@XmlElement(name = "ACCOUNT_BAL") public String   accountBal;
	@XmlElement(name = "AUTH_ID") public String   authId;
	@XmlElement(name = "LEDGER_BAL") public String ledgerBal;
	@XmlElement(name = "CARD_NO") public String cardNo;
	@XmlElement(name = "TXN_CODE") public String txnCode;
	
	
	
	
	@XmlElement(name = "TXN_AMOUNT") public String         txnAmount;
	@XmlElement(name = "TXN_DATE_TIME") public String      txnDateTime;
	@XmlElement(name = "STAN") public String               stan;
	@XmlElement(name = "TIME") public String               time;
	@XmlElement(name = "DATE") public String               date;
	@XmlElement(name = "SRC_TYPE") public String           srcType;
	@XmlElement(name = "MERCHANT_TYPE") public String      merchantType;
	@XmlElement(name = "ENTRY_MODE") public String         entryMode;
	@XmlElement(name = "SERVICE_CONDITION") public String  serviceCondition;
	@XmlElement(name = "ACQURIER_INST_ID") public String   acqurierInstId;
	@XmlElement(name = "TERMINAL_ID") public String        terminalID;
	@XmlElement(name = "CARD_ACPT_ID") public String       cardAcptID;
	@XmlElement(name = "CARD_ACPT_NAME_LOC") public String cardAcptNameLOC;
	@XmlElement(name = "MERCHANT_PASS_CODE") public String merchantPassCode;
	@XmlElement(name = "CURRENCY_CODE") public String      currencyCode;
	@XmlElement(name = "UID_VID_NO") public String         uidVidNo;
	@XmlElement(name = "AUTH_FACTOR") public String        authFactor;
	@XmlElement(name = "MC_DATA") public String            mcData;
	@XmlElement(name = "AUTH_INDICATOR") public String     authIndicator;
	@XmlElement(name = "AGENT_ID") public String           agentID;
	@XmlElement(name = "KEY_DATA") public String           keyData;
	@XmlElement(name = "FINGER_DATA") public String        fingerData;
	@XmlElement(name = "POSTAL_CODE") public String        postalCode;

	@XmlElement(name = "TO_ACCOUNT_NO") public String          toAccountNo;
	@XmlElement(name = "TRACK2_DATA") public String            track2Data;
	@XmlElement(name = "TRACK1_DATA") public String            track1Data;
	@XmlElement(name = "PIN_DATA") public String               pinData;
	@XmlElement(name = "CARD_SEQUENCE_NUMBER") public String   cardSeqNo;
	@XmlElement(name = "SERVICE_CONDITION_CODE") public String serviceCode;
	@XmlElement(name = "CARD_EXP_DATE") public String          cardExpiryYYMM;
	@XmlElement(name = "POS_DATACODE") public String           posDataCode;
	@XmlElement(name = "REVERSAL_RESPONSE_CODE") public String reversalRespCode;
	@XmlElement(name = "ORIGINAL_DATA") public String          originalData;
	@XmlElement(name = "BENEFICIARY_DATA") public String       beneficiaryData;
	
}