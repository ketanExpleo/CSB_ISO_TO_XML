package com.fss.aeps.jpa;

import java.sql.Blob;
import java.sql.Clob;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "CLIENT_TRAFFIC_AUDIT")
public class ClientTrafficAudit implements TrafficAudit {

	private static final long serialVersionUID = 1L;

	@Transient
	private boolean isNew = true;

	private long id;
	private String requestUrl;
	private String method;
	private String protocol;
	private String scheme;
	private String dispatcherType;
	private Long contentLength;
	private String contentType;
	private String characterEncoding;
	private String queryString;
	private String remoteAddr;
	private String remoteHost;
	private Integer remotePort;
	private String localAddr;
	private String localName;
	private Integer localPort;
	private String serverName;
	private Integer serverPort;
	private String cipherUsed;
	private String tlsProtocol;
	private String requestedTlsProtocols;
	private Integer responseStatus;
	private Integer responseContentLength;
	private String responseContentType;
	private String responseCharacterEncoding;
	private Character responseIsCommited;
	private Date requestTime;
	private Date responseTime;
	private Clob headers;
	private Blob body;
	private Clob responseHeaders;
	private Blob responseBody;

	public ClientTrafficAudit() {
	}

	public ClientTrafficAudit(long id) {
		this.id = id;
	}

	@Override
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "ID", unique = true, nullable = false, precision = 18, scale = 0)
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	@Column(name = "REQUEST_URL", length = 2048)
	public String getRequestUrl() {
		return this.requestUrl;
	}

	@Override
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	@Override
	@Column(name = "METHOD", length = 30)
	public String getMethod() {
		return this.method;
	}

	@Override
	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	@Column(name = "PROTOCOL", length = 10)
	public String getProtocol() {
		return this.protocol;
	}

	@Override
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	@Column(name = "SCHEME", length = 10)
	public String getScheme() {
		return this.scheme;
	}

	@Override
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	@Override
	@Column(name = "DISPATCHER_TYPE", length = 30)
	public String getDispatcherType() {
		return this.dispatcherType;
	}

	@Override
	public void setDispatcherType(String dispatcherType) {
		this.dispatcherType = dispatcherType;
	}

	@Override
	@Column(name = "CONTENT_LENGTH", precision = 10, scale = 0)
	public Long getContentLength() {
		return this.contentLength;
	}

	@Override
	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}

	@Override
	@Column(name = "CONTENT_TYPE", length = 250)
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	@Column(name = "CHARACTER_ENCODING", length = 30)
	public String getCharacterEncoding() {
		return this.characterEncoding;
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	@Override
	@Column(name = "QUERY_STRING", length = 2048)
	public String getQueryString() {
		return this.queryString;
	}

	@Override
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	@Override
	@Column(name = "REMOTE_ADDR")
	public String getRemoteAddr() {
		return this.remoteAddr;
	}

	@Override
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	@Override
	@Column(name = "REMOTE_HOST")
	public String getRemoteHost() {
		return this.remoteHost;
	}

	@Override
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	@Override
	@Column(name = "REMOTE_PORT", precision = 5, scale = 0)
	public Integer getRemotePort() {
		return this.remotePort;
	}

	@Override
	public void setRemotePort(Integer remotePort) {
		this.remotePort = remotePort;
	}

	@Override
	@Column(name = "LOCAL_ADDR")
	public String getLocalAddr() {
		return this.localAddr;
	}

	@Override
	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}

	@Override
	@Column(name = "LOCAL_NAME")
	public String getLocalName() {
		return this.localName;
	}

	@Override
	public void setLocalName(String localName) {
		this.localName = localName;
	}

	@Override
	@Column(name = "LOCAL_PORT", precision = 5, scale = 0)
	public Integer getLocalPort() {
		return this.localPort;
	}

	@Override
	public void setLocalPort(Integer localPort) {
		this.localPort = localPort;
	}

	@Override
	@Column(name = "SERVER_NAME")
	public String getServerName() {
		return this.serverName;
	}

	@Override
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Override
	@Column(name = "SERVER_PORT", precision = 5, scale = 0)
	public Integer getServerPort() {
		return this.serverPort;
	}

	@Override
	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}

	@Override
	@Column(name = "CIPHER_USED")
	public String getCipherUsed() {
		return this.cipherUsed;
	}

	@Override
	public void setCipherUsed(String cipherUsed) {
		this.cipherUsed = cipherUsed;
	}

	@Override
	@Column(name = "TLS_PROTOCOL", length = 20)
	public String getTlsProtocol() {
		return this.tlsProtocol;
	}

	@Override
	public void setTlsProtocol(String tlsProtocol) {
		this.tlsProtocol = tlsProtocol;
	}

	@Override
	@Column(name = "REQUESTED_TLS_PROTOCOLS", length = 100)
	public String getRequestedTlsProtocols() {
		return this.requestedTlsProtocols;
	}

	@Override
	public void setRequestedTlsProtocols(String requestedTlsProtocols) {
		this.requestedTlsProtocols = requestedTlsProtocols;
	}

	@Override
	@Column(name = "RESPONSE_STATUS", precision = 5, scale = 0)
	public Integer getResponseStatus() {
		return responseStatus;
	}

	@Override
	public void setResponseStatus(Integer responseStatus) {
		this.responseStatus = responseStatus;
	}

	@Override
	@Column(name = "RESPONSE_CONTENT_LENGTH", precision = 5, scale = 0)
	public Integer getResponseContentLength() {
		return this.responseContentLength;
	}

	@Override
	public void setResponseContentLength(Integer responseContentLength) {
		this.responseContentLength = responseContentLength;
	}

	@Override
	@Column(name = "RESPONSE_CONTENT_TYPE", length = 250)
	public String getResponseContentType() {
		return this.responseContentType;
	}

	@Override
	public void setResponseContentType(String responseContentType) {
		this.responseContentType = responseContentType;
	}

	@Override
	@Column(name = "RESPONSE_CHARACTER_ENCODING", length = 30)
	public String getResponseCharacterEncoding() {
		return this.responseCharacterEncoding;
	}

	@Override
	public void setResponseCharacterEncoding(String responseCharacterEncoding) {
		this.responseCharacterEncoding = responseCharacterEncoding;
	}

	@Override
	@Column(name = "RESPONSE_IS_COMMITED", length = 1)
	public Character getResponseIsCommited() {
		return this.responseIsCommited;
	}

	@Override
	public void setResponseIsCommited(Character responseIsCommited) {
		this.responseIsCommited = responseIsCommited;
	}

	@Override
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUEST_TIME", length = 11)
	public Date getRequestTime() {
		return this.requestTime;
	}

	@Override
	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	@Override
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RESPONSE_TIME", length = 11)
	public Date getResponseTime() {
		return this.responseTime;
	}

	@Override
	public void setResponseTime(Date responseTime) {
		this.responseTime = responseTime;
	}

	@Override
	@Column(name = "HEADERS")
	public Clob getHeaders() {
		return this.headers;
	}

	@Override
	public void setHeaders(Clob headers) {
		this.headers = headers;
	}

	@Override
	@Column(name = "BODY")
	public Blob getBody() {
		return this.body;
	}

	@Override
	public void setBody(Blob body) {
		this.body = body;
	}

	@Override
	@Column(name = "RESPONSE_HEADERS")
	public Clob getResponseHeaders() {
		return this.responseHeaders;
	}

	@Override
	public void setResponseHeaders(Clob responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	@Override
	@Column(name = "RESPONSE_BODY")
	public Blob getResponseBody() {
		return this.responseBody;
	}

	@Override
	public void setResponseBody(Blob responseBody) {
		this.responseBody = responseBody;
	}

	@Override
	@Transient
	public String getLocale() {
		return null;
	}

	@Override
	public void setLocale(String locale) {

	}

	@Override
	@Transient
	public String getContextPath() {
		return null;
	}

	@Override
	@Transient
	public void setContextPath(String contextPath) {}


	@Override
	@Transient
	public boolean isNew() {
		return isNew;
	}

	@Transient
	@PrePersist
	@PostLoad
	void markNotNew() {
		this.isNew = false;
	}
}
