package com.fss.aeps.test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

public class HttpResponseInfo {

	public final String charEncoding;
	public final String contentType;
	public final int bufferSize;
	public final Map<String, Collection<String>> headers = new HashMap<>();
	public final int status;
	public final boolean isCommited;

	public HttpResponseInfo(HttpServletResponse response) throws IOException, ServletException {
		charEncoding = response.getCharacterEncoding();
		contentType = response.getContentType();
		bufferSize = response.getBufferSize();
		response.getHeaderNames().forEach(name -> headers.put(name, response.getHeaders(name)));
		status = response.getStatus();
		isCommited = response.isCommitted();

	}

}
