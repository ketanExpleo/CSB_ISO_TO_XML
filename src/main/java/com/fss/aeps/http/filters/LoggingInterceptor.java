package com.fss.aeps.http.filters;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.hibernate.engine.jdbc.BlobProxy;
import org.hibernate.engine.jdbc.ClobProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fss.aeps.jpa.ClientTrafficAudit;
import com.fss.aeps.repository.ClientTrafficAuditRepository;

import okhttp3.Connection;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.GzipSource;
import okio.Source;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public final class LoggingInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

	@Autowired
	private ClientTrafficAuditRepository repository;

	private boolean logBody;
	private boolean auditLog;
	private boolean auditLogBody;

	public LoggingInterceptor(@Value("${client.http.logbody:#{false}}") boolean logBody,
			@Value("${client.http.audit.log:#{false}}") boolean auditLog,
			@Value("${client.http.audit.log.body:#{false}}") boolean auditLogBody) {
		this.logBody = logBody;
		this.auditLog = auditLog;
		this.auditLogBody = auditLog && auditLogBody;
	}

	// @formatter:off
	@Override
	public Response intercept(final Chain chain) throws IOException {
		final ClientTrafficAudit clientTrafficAudit = new ClientTrafficAudit();
		Request request = chain.request();
		if(auditLog) populateRequestInfo(clientTrafficAudit, request);
		final RequestBody requestBody = request.body();
		byte[] requestBytes = null;
		StringBuilder sb =  null;
		if(requestBody != null) {
			final Buffer buffer = new Buffer();
			requestBody.writeTo(buffer);
			final MediaType mediaType = requestBody.contentType();
			requestBytes = buffer.readByteArray();
			sb = new StringBuilder(requestBytes.length);
			final RequestBody body    = RequestBody.create(requestBytes, mediaType);
			request = request.newBuilder().method(request.method(), body).build();
		}
		if(sb == null) sb = new StringBuilder(0);
		sb.append("\r\n--> ").append(request.method()).append(" ").append(request.url().toString())
		.append(" ").append(chain.connection().protocol()).append("\r\n");
		if(requestBody != null) {
			sb.append("--> Content-Type: ").append(requestBody.contentType()).append("\r\n");
			sb.append("--> Content-Length: ").append(requestBody.contentLength()).append("\r\n");
		}
		if(logBody || auditLogBody) {
			final String headers = request.headers().toMultimap().toString();
			if(logBody) sb.append("--> headers : ").append(headers).append("\r\n");
			if(auditLogBody) {
				clientTrafficAudit.setHeaders(ClobProxy.generateProxy(headers));
				if(requestBytes != null) clientTrafficAudit.setBody(BlobProxy.generateProxy(requestBytes));
			}
		}
		if(requestBytes != null && logBody) sb.append("--> body : ").append(new String(requestBytes)).append("\r\n");
		try {
			logger.info(sb.toString());
			sb.setLength(0);
			long startNs = System.nanoTime();
			Response response = chain.proceed(request);
			long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
			if(auditLog) populateResponseInfo(clientTrafficAudit, chain, response);
			final ResponseBody responseBody = response.body();
			final long contentLength = responseBody.contentLength();
			clientTrafficAudit.setResponseContentLength((int) contentLength);
			final String contentEncoding = response.headers().get("Content-Encoding");
			sb.append("\r\n<-- ").append(response.code()).append(" ").append(response.message().isEmpty() ? "" : response.message())
			.append(" ").append(response.request().url()).append(" (").append(tookMs).append(")\r\n");
			if(logBody || auditLogBody) {
				final String headers = response.headers().toMultimap().toString();
				if(logBody) sb.append("<-- headers : ").append(headers).append("\r\n");
				if(auditLogBody) {
					clientTrafficAudit.setResponseHeaders(ClobProxy.generateProxy(headers));
				}
			}
			if(contentLength != 0) {
				final Source source = "gzip".equalsIgnoreCase(contentEncoding) ? new GzipSource(responseBody.source()) : responseBody.source();
				final Buffer buffer = new Buffer();
				buffer.writeAll(source);
				final byte[] responseBytes = buffer.clone().readByteArray();
				clientTrafficAudit.setResponseContentLength(responseBytes.length);
				sb.append("<-- body : ").append(new String(responseBytes)).append("\r\n");
				sb.append("<-- END HTTP (" + responseBytes.length + "-byte body)").append("\r\n");
				if(auditLogBody) clientTrafficAudit.setResponseBody(BlobProxy.generateProxy(responseBytes));
				buffer.close();
				final ResponseBody newBody = ResponseBody.create(responseBytes, responseBody.contentType());
				response = response.newBuilder().body(newBody).build();
			}
			logger.info(sb.toString());
			return response;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				repository.save(clientTrafficAudit);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}


	private void populateRequestInfo(ClientTrafficAudit webTraffic, Request request) {
		try {
			final RequestBody requestBody = request.body();
			final HttpUrl url = request.url();
			webTraffic.setRequestTime(new Date());
			webTraffic.setRequestUrl(url.url().toString());
			webTraffic.setMethod(request.method());
			webTraffic.setScheme(url.scheme());
			webTraffic.setDispatcherType("OUTBOUND");
			webTraffic.setCharacterEncoding(request.headers().get("Content-Encoding"));
			webTraffic.setQueryString(url.query());
			if(requestBody != null) {
				webTraffic.setContentLength(requestBody.contentLength());
				webTraffic.setContentType(requestBody.contentType().toString());
			}
		} catch (Exception e) {
			logger.error("populateRequestInfo", e);
		}
	}

	private final void populateResponseInfo(ClientTrafficAudit webTraffic, Chain chain, Response response) throws JsonProcessingException, SocketException, UnknownHostException {
		try {
			final Connection connection = chain.connection();
			final Handshake handshake = connection.handshake();
			final Socket socket = connection.socket();
			webTraffic.setProtocol(connection.protocol().name());
			InetSocketAddress remoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
			InetSocketAddress localSocketAddress = (InetSocketAddress) socket.getLocalSocketAddress();
			InetAddress remoteAddress = remoteSocketAddress.getAddress();
			InetAddress locaAddress = localSocketAddress.getAddress();
			webTraffic.setRemoteAddr(remoteAddress.getHostAddress());
			webTraffic.setRemoteHost(remoteAddress.getHostName());
			webTraffic.setRemotePort(remoteSocketAddress.getPort());
			webTraffic.setServerName(remoteAddress.getHostAddress());
			webTraffic.setServerPort(remoteSocketAddress.getPort());
			webTraffic.setLocalAddr(locaAddress.getHostAddress());
			webTraffic.setLocalName(locaAddress.getHostName());
			webTraffic.setLocalPort(localSocketAddress.getPort());
			webTraffic.setCipherUsed(handshake.cipherSuite().javaName());
			webTraffic.setTlsProtocol(handshake.tlsVersion().javaName());
			webTraffic.setResponseIsCommited('Y');
			webTraffic.setResponseTime(new Date());
			if(response != null) {
				webTraffic.setResponseCharacterEncoding(response.headers().get("Content-Encoding"));
				webTraffic.setResponseStatus(response.code());
				webTraffic.setResponseContentType(Objects.toString(response.body().contentType()));
				if(webTraffic.getResponseContentLength() == null && response.headers().get("content-length") != null) webTraffic.setResponseContentLength(Integer.parseInt(response.headers().get("content-length")));
			}
		} catch (Exception e) {
			logger.error("populateResponseInfo", e);
		}
	}
}
