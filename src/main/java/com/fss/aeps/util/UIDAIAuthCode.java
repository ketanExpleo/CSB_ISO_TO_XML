package com.fss.aeps.util;

import java.util.Base64;

public final class UIDAIAuthCode {

	public final String responseCode;
	public final String authCode;
	public final String authToken;

	public UIDAIAuthCode(String responseCode, String authCode, String authToken) {
		this.responseCode = responseCode;
		this.authCode = authCode;
		this.authToken = authToken;
	}

	public UIDAIAuthCode(String base64String) {
		String[] data = new String(Base64.getDecoder().decode(base64String)).split("\\|");
		this.responseCode = data[0];
		this.authCode = data[1];
		this.authToken = data[2];
	}

}
