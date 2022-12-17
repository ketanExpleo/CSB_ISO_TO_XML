package com.fss.aeps.acquirer.cbs.fixml;

import java.util.Objects;

import jakarta.xml.bind.annotation.XmlElement;

public class RequestBody {

	@XmlElement(name = "Identifier")
	public String		Identifier;
	@XmlElement(name = "ReplaceIdentifer")
	public String		ReplaceIdentifer;
	@XmlElement(name = "PinCode")
	public String		PinCode;
	@XmlElement(name = "CardAcceptor")
	public String		CardAcceptor;
	@XmlElement(name = "IIN")
	public String		IIN;
	@XmlElement(name = "UID")
	public String		UID;
	@XmlElement(name = "TXN_CODE")
	public String		TXN_CODE;
	@XmlElement(name = "TXN_AMOUNT")
	public String		TXN_AMOUNT;
	@XmlElement(name = "TXN_DATE_TIME")
	public String		TXN_DATE_TIME;
	@XmlElement(name = "STAN")
	public String		STAN;
	@XmlElement(name = "TIME")
	public String		TIME;
	@XmlElement(name = "DATE")
	public String		DATE;
	@XmlElement(name = "MERCHANT_TYPE")
	public String		MERCHANT_TYPE;
	@XmlElement(name = "ENTRY_MODE")
	public String		ENTRY_MODE;
	@XmlElement(name = "SERVICE_CONDITION")
	public String		SERVICE_CONDITION;
	@XmlElement(name = "ACQURIER_INST_ID")
	public String		ACQURIER_INST_ID;
	@XmlElement(name = "RRN")
	public String		RRN;
	@XmlElement(name = "TERMINAL_ID")
	public String		TERMINAL_ID;
	@XmlElement(name = "CARD_ACPT_ID")
	public String		CARD_ACPT_ID;
	@XmlElement(name = "CARD_ACPT_NAME_LOC")
	public String		CARD_ACPT_NAME_LOC;
	@XmlElement(name = "CURRENCY_CODE")
	public String		CURRENCY_CODE;
	@XmlElement(name = "MERCHANT_PASS_CODE")
	public String		MERCHANT_PASS_CODE;
	@XmlElement(name = "AGENT_DETS")
	public String		AGENT_DETS;
	@XmlElement(name = "BENEFICIARY_DATA")
	public String		BENEFICIARY_DATA;

	@XmlElement(name = "DEPOSIT_ID")
	public String		DEPOSIT_ID;

	@XmlElement(name = "PidData")
	public PidWrapper	pidWrapper;

	@XmlElement(name = "RECON_INDICATOR")
	public String	reconIndicator;

	@XmlElement(name = "ORIGINAL_DATA")
	public String	originalData;

	@XmlElement(name = "REVERSAL_RESPONSE_CODE")
	public String	REVERSAL_RESPONSE_CODE;

	@XmlElement(name = "BT")
	public String	BT;

	public RequestBody() {
	}

	public RequestBody(RequestBody body) {
		super();
		Identifier = body.Identifier;
		ReplaceIdentifer = body.ReplaceIdentifer;
		PinCode = body.PinCode;
		CardAcceptor = body.CardAcceptor;
		IIN = body.IIN;
		UID = body.UID;
		TXN_CODE = body.TXN_CODE;
		TXN_AMOUNT = body.TXN_AMOUNT;
		TXN_DATE_TIME = body.TXN_DATE_TIME;
		STAN = body.STAN;
		TIME = body.TIME;
		DATE = body.DATE;
		MERCHANT_TYPE = body.MERCHANT_TYPE;
		ENTRY_MODE = body.ENTRY_MODE;
		SERVICE_CONDITION = body.SERVICE_CONDITION;
		ACQURIER_INST_ID = body.ACQURIER_INST_ID;
		RRN = body.RRN;
		TERMINAL_ID = body.TERMINAL_ID;
		CARD_ACPT_ID = body.CARD_ACPT_ID;
		CARD_ACPT_NAME_LOC = body.CARD_ACPT_NAME_LOC;
		CURRENCY_CODE = body.CURRENCY_CODE;
		MERCHANT_PASS_CODE = body.MERCHANT_PASS_CODE;
		AGENT_DETS = body.AGENT_DETS;
		BENEFICIARY_DATA = body.BENEFICIARY_DATA;
		DEPOSIT_ID = body.DEPOSIT_ID;
		reconIndicator = body.reconIndicator;
	}



	@Override
	public String toString() {
		return Objects.toString(pidWrapper);
	}

	public static class PidWrapper {
		@XmlElement(name = "PidData")
		public PidData pidData;

		@Override
		public String toString() {
			return "PidWrapper [pidData=" + pidData + "]";
		}

	}

}
