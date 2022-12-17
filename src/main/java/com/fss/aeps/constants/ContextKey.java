package com.fss.aeps.constants;

public class ContextKey {

	public static final String FUTURE = "FUTURE";
	public static final String CHANNEL = "CHANNEL";
	public static final String REQUEST_ACK = "REQUEST_ACK";
	public static final String RESPONSE_ACK = "RESPONSE_ACK";
	public static final String AGENT_DETAILS = "AGENT_DETAILS";
	public static final String RECON_INDICATOR = "RECON_INDICATOR";
	public static final String IS_STATIC_RESPONSE = "IS_STATIC_RESPONSE";
	public static final String ACQUIRER_REVERSAL = "ACQUIRER_REVERSAL";
	public static final String ACQUIRER_TRANSACTION = "ACQUIRER_TRANSACTION";
	public static final String ORG_TXN_MSG_ID = "ORG_TXN_MSG_ID";
	public static final String HTTP_CONTEXT = "HTTP_CONTEXT";
	public static final String RAW_RESPONSE_BODY = "RAW_RESPONSE_BODY";
	public static final String RESPONSE_URL = "RESPONSE_URL";
	public static final String BROADCASTED_RESPONSE = "BROADCASTED_RESPONSE";
	public static final String ORG_RESP_CODE = "ORG_RESP_CODE";


	public static final boolean toBoolean(Object object) {
		if (object == null) return false;
		else if (object instanceof Boolean b) return b;
		else return Boolean.parseBoolean(object.toString());
	}

}
