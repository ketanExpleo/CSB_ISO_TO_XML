package com.fss.aeps.jpa;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Date;

import org.springframework.data.domain.Persistable;

public interface TrafficAudit extends Persistable<Long>, Serializable {

	@Override
	Long getId();

	void setId(long id);

	String getRequestUrl();

	void setRequestUrl(String requestUrl);

	String getMethod();

	void setMethod(String method);

	String getProtocol();

	void setProtocol(String protocol);

	String getLocale();

	void setLocale(String locale);

	String getScheme();

	void setScheme(String scheme);

	String getDispatcherType();

	void setDispatcherType(String dispatcherType);

	Long getContentLength();

	void setContentLength(Long contentLength);

	String getContentType();

	void setContentType(String contentType);

	String getContextPath();

	void setContextPath(String contextPath);

	String getCharacterEncoding();

	void setCharacterEncoding(String characterEncoding);

	String getQueryString();

	void setQueryString(String queryString);

	String getRemoteAddr();

	void setRemoteAddr(String remoteAddr);

	String getRemoteHost();

	void setRemoteHost(String remoteHost);

	Integer getRemotePort();

	void setRemotePort(Integer remotePort);

	String getLocalAddr();

	void setLocalAddr(String localAddr);

	String getLocalName();

	void setLocalName(String localName);

	Integer getLocalPort();

	void setLocalPort(Integer localPort);

	String getServerName();

	void setServerName(String serverName);

	Integer getServerPort();

	void setServerPort(Integer serverPort);

	String getCipherUsed();

	void setCipherUsed(String cipherUsed);

	String getTlsProtocol();

	void setTlsProtocol(String tlsProtocol);

	String getRequestedTlsProtocols();

	void setRequestedTlsProtocols(String requestedTlsProtocols);

	Integer getResponseStatus();

	void setResponseStatus(Integer responseStatus);

	Integer getResponseContentLength();

	void setResponseContentLength(Integer responseContentLength);

	String getResponseContentType();

	void setResponseContentType(String responseContentType);

	String getResponseCharacterEncoding();

	void setResponseCharacterEncoding(String responseCharacterEncoding);

	Character getResponseIsCommited();

	void setResponseIsCommited(Character responseIsCommited);

	Date getRequestTime();

	void setRequestTime(Date requestTime);

	Date getResponseTime();

	void setResponseTime(Date responseTime);

	Clob getHeaders();

	void setHeaders(Clob headers);

	Blob getBody();

	void setBody(Blob body);

	Clob getResponseHeaders();

	void setResponseHeaders(Clob responseHeaders);

	Blob getResponseBody();

	void setResponseBody(Blob responseBody);

	@Override
	boolean isNew();

}