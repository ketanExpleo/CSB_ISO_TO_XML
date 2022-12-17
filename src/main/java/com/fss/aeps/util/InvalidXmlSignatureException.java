package com.fss.aeps.util;

public final class InvalidXmlSignatureException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidXmlSignatureException(final String message) {
		super(message);
	}

	public InvalidXmlSignatureException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidXmlSignatureException(Throwable cause) {
		super(cause);
	}
}