package com.fss.aeps;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fss.aeps.util.InvalidXmlSignatureException;

@RestControllerAdvice
public class WebExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { InvalidXmlSignatureException.class })
	protected ResponseEntity<Object> handleSignatureException(InvalidXmlSignatureException exception, WebRequest request) {
		String bodyOfResponse = "xml signature verification failed.";
		return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
	}
}
