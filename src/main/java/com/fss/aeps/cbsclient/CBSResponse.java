package com.fss.aeps.cbsclient;

import java.util.ArrayList;
import java.util.List;

public class CBSResponse {

	public String responseCode;
	public String responseMessage;
	public String balance;
	public List<String> statement = new ArrayList<>();
	public String operatedAccount;
	public String customerName;
	public String authCode;
	public String tranDetails;

	public CBSResponse() {
	}

	public CBSResponse(String responseCode, String responseMessage) {
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}

	@Override
	public String toString() {
		return "CBSResponse [responseCode=" + responseCode + ", responseMessage=" + responseMessage + ", balance="
				+ balance + ", statement=" + statement + ", operatedAccount=" + operatedAccount + ", customerName="
				+ customerName + "]";
	}



}
