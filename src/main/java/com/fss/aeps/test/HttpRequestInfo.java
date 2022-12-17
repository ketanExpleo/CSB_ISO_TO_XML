package com.fss.aeps.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tomcat.util.net.SSLSupport;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;

public class HttpRequestInfo {

	public String charEncoding;
	public String authType;
	public String contentType;
	public String contextPath;
	public String localAddr;
	public String localName;
	public String method;
	public String pathTranslated;
	public String protocol;
	public String queryString;
	public String remoteAddr;
	public String remoteHost;
	public String remoteUser;
	public String requestedSessionId;
	public String scheme;
	public String serverName;
	public long contentLengthLong;
	public DispatcherType dispatcherType;
	public Map<String, List<String>> headerMap = new HashMap<>();
	public Locale locale;
	public int localPort;
	public Map<String, String[]> parameterMap;
	public int remotePort;
	public String requestURL;
	public int serverPort;
	public String cipherUsed;
	public String tlsProtocol;
	public String requestedProtocols;
	public Integer keySize;

	public HttpRequestInfo(HttpServletRequest request) {
		charEncoding = request.getCharacterEncoding();
		authType = request.getAuthType();
		contentType = request.getContentType();
		contextPath = request.getContextPath();
		localAddr = request.getLocalAddr();
		localName = request.getLocalName();
		method = request.getMethod();
		protocol = request.getProtocol();
		queryString = request.getQueryString();
		remoteAddr = request.getRemoteAddr();
		remoteHost = request.getRemoteHost();
		remoteUser = request.getRemoteUser();
		requestedSessionId = request.getRequestedSessionId();
		scheme = request.getScheme();
		serverName = request.getServerName();
		contentLengthLong = request.getContentLengthLong();
		dispatcherType = request.getDispatcherType();
		List<String> headerNames = Collections.list(request.getHeaderNames());
		headerNames.forEach(name -> headerMap.put(name, Collections.list(request.getHeaders(name))));
		locale = request.getLocale();
		localPort = request.getLocalPort();
		parameterMap = request.getParameterMap();
		remotePort = request.getRemotePort();
		requestURL = request.getRequestURL().toString();
		serverPort = request.getServerPort();
		cipherUsed = (String) request.getAttribute(SSLSupport.CIPHER_SUITE_KEY);
		tlsProtocol = (String) request.getAttribute(SSLSupport.PROTOCOL_VERSION_KEY);
		requestedProtocols = (String) request.getAttribute(SSLSupport.REQUESTED_PROTOCOL_VERSIONS_KEY);
		keySize = (Integer) request.getAttribute(SSLSupport.KEY_SIZE_KEY);
	}


}
