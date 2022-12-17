package com.fss.aeps.test;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.hibernate.engine.jdbc.BlobProxy;
import org.hibernate.engine.jdbc.ClobProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fss.aeps.jpa.WebTrafficAudit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import okhttp3.Connection;
import okhttp3.Handshake;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

@Component
public final class RetroLoggingInterceptor2 implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(RetroLoggingInterceptor2.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	@Autowired
	private EntityManagerFactory emf;

	@Value("${server.http.logbody:#{false}}")
	private boolean logBody;

	@Value("${server.http.logheaders:#{false}}")
	private boolean logHeaders;

	private boolean auditLog;

	private boolean auditLogBody;


	private static final Charset UTF8 = Charset.forName("UTF-8");
	private volatile Level level = Level.BODY;

	public enum Level {
		NONE, BASIC, HEADERS, BODY
	}

	//@formatter:off
	public RetroLoggingInterceptor2(@Value("${server.http.audit.log:#{false}}") boolean auditLog, @Value("${server.http.audit.log.body:#{false}}") boolean auditLogBody) {
		this.auditLog = auditLog;
		this.auditLogBody = this.auditLog && auditLogBody;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		final WebTrafficAudit webTraffic = new WebTrafficAudit();
		if(auditLog) populateRequestInfo(webTraffic, chain);

		final StringBuilder sb = new StringBuilder("\r\n");
		final Request request = chain.request();
		try {
			final boolean logBody    = level == Level.BODY;
			final boolean logHeaders = logBody || level == Level.HEADERS;

			final RequestBody requestBody    = request.body();
			final boolean     hasRequestBody = requestBody != null;

			final Connection connection = chain.connection();
			String requestStartMessage = "--> " + request.method() + ' ' + request.url() + (connection != null ? " " + connection.protocol() : "");
			if (!logHeaders && hasRequestBody) { requestStartMessage += " (" + requestBody.contentLength() + "-byte body)"; }
			sb.append(requestStartMessage).append("\r\n");

			if (logHeaders) {
				if (hasRequestBody) {
					if (requestBody.contentType() != null) { sb.append("--> Content-Type: ").append(requestBody.contentType()).append("\r\n"); }
					if (requestBody.contentLength() != -1) { sb.append("--> Content-Length: ").append(requestBody.contentLength()).append("\r\n"); }
				}

				logHeader(sb, request.headers(), "--> ");

				if (!logBody || !hasRequestBody) {
					sb.append("--> END " + request.method()).append("\r\n");
				} else if (bodyHasUnknownEncoding(request.headers())) {
					sb.append("--> END " + request.method() + " (encoded body omitted)").append("\r\n");
				} else if (requestBody.isDuplex()) {
					sb.append("--> END " + request.method() + " (duplex request body omitted)").append("\r\n");
				} else {
					Buffer buffer = new Buffer();
					requestBody.writeTo(buffer);
					final byte[] requestBytes = buffer.clone().readByteArray();
					if(auditLogBody) webTraffic.setBody(BlobProxy.generateProxy(requestBytes));
					Charset   charset     = UTF8;
					MediaType contentType = requestBody.contentType();
					if (contentType != null) { charset = contentType.charset(UTF8); }

					sb.append("\r\n");
					if (isPlaintext(buffer)) {
						sb.append("--> body : ").append(new String(requestBytes, charset)).append("\r\n");
						sb.append("--> END " + request.method() + " (" + requestBody.contentLength() + "-byte body)").append("\r\n");
					} else {
						sb.append("--> END " + request.method() + " (binary " + requestBody.contentLength() + "-byte body omitted)").append("\r\n");
					}
				}
			}
			logger.info(sb.toString());
			sb.setLength(0);
			sb.append("\r\n");
			long     startNs = System.nanoTime();
			Response response;
			try {
				response = chain.proceed(request);
			} catch (Exception e) {
				sb.append("<-- HTTP FAILED: " + e).append("\r\n");
				logger.info(sb.toString());
				webTraffic.setResponseBody(BlobProxy.generateProxy(e.getMessage().getBytes()));
				webTraffic.setResponseTime(new Date());
				throw e;
			}
			if(auditLog) populateResponseInfo(webTraffic, chain, response);
			long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

			ResponseBody responseBody  = response.body();
			long         contentLength = responseBody.contentLength();
			String       bodySize      = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
			sb.append("<-- " + response.code() + (response.message().isEmpty() ? "" : ' ' + response.message()) + ' ' + response.request().url() + " (" + tookMs
					+ "ms" + (!logHeaders ? ", " + bodySize + " body" : "") + ')').append("\r\n");

			if (logHeaders) {
				final Headers headers = response.headers();
				logHeader(sb, headers, "<-- ");
				if (!logBody || !HttpHeaders.promisesBody(response)) {
					sb.append("<-- END HTTP").append("\r\n");
				} else if (bodyHasUnknownEncoding(response.headers())) {
					sb.append("<-- END HTTP (encoded body omitted)").append("\r\n");
				} else {
					BufferedSource source = responseBody.source();
					source.request(Long.MAX_VALUE);
					Buffer buffer = source.getBuffer();

					Long gzippedLength = null;
					if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
						gzippedLength = buffer.size();
						try(GzipSource gzippedResponseBody = new GzipSource(buffer.clone())) {
							buffer = new Buffer();
							buffer.writeAll(gzippedResponseBody);
						}
					}

					Charset   charset     = UTF8;
					MediaType contentType = responseBody.contentType();
					if (contentType != null) { charset = contentType.charset(UTF8); }
					if (!isPlaintext(buffer)) {
						sb.append("\r\n");
						sb.append("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)").append("\r\n");
						logger.info(sb.toString());
						return response;
					}

					if (contentLength != 0) {
						final byte[] responseBytes = buffer.clone().readByteArray();
						sb.append("\r\n").append("<-- body : ");
						sb.append(new String(responseBytes, charset)).append("\r\n");
						webTraffic.setResponseContentLength(responseBytes.length);
						if(auditLogBody) {
							webTraffic.setResponseHeaders(ClobProxy.generateProxy(mapper.writeValueAsString(headers.toMultimap())));
							webTraffic.setResponseBody(BlobProxy.generateProxy(responseBytes));
							//response = response.newBuilder().body(ResponseBody.create(responseBytes, contentType)).build();
						}
					}

					if (gzippedLength != null) {
						sb.append("<-- END HTTP (" + buffer.size() + "-byte, " + gzippedLength + "-gzipped-byte body)").append("\r\n");
					} else {
						sb.append("<-- END HTTP (" + buffer.size() + "-byte body)").append("\r\n");
					}
				}
			}
			logger.info(sb.toString());
			return response;

		} finally {
			try {
				EntityManager em = emf.createEntityManager();
				em.getTransaction().begin();
				em.persist(webTraffic);
				em.getTransaction().commit();
				em.close();
			} catch (Exception e) {
				logger.error("", e);
			}
		}

	}

	private final void logHeader(final StringBuilder sb, final Headers headers, final String direction) {
		final Map<String, List<String>> map = headers.toMultimap();
		sb.append(direction).append("headers").append(" : ");
		writeHeaders(map, sb);
	}

	private final static StringBuilder writeHeaders(final Map<String, List<String>> map, final StringBuilder sb) {
		if (map.isEmpty()) return sb.append("{}");
		sb.append("{");
		final Set<Entry<String, List<String>>> set   = map.entrySet();
		final int                              size  = set.size();
		int                                    count = 0;
		for (final Entry<String, List<String>> e : set) {
			count++;
			sb.append(e.getKey());
			sb.append('=');
			sb.append(e.getValue());
			if (count != size) sb.append(", ");
		}
		sb.append("}");
		return sb;
	}

	private static final boolean isPlaintext(Buffer buffer) {
		try {
			Buffer prefix    = new Buffer();
			long   byteCount = buffer.size() < 64 ? buffer.size() : 64;
			buffer.copyTo(prefix, 0, byteCount);
			for (int i = 0; i < 16; i++) {
				if (prefix.exhausted()) { break; }
				int codePoint = prefix.readUtf8CodePoint();
				if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) { return false; }
			}
			return true;
		} catch (EOFException e) {
			return false; // Truncated UTF-8 sequence.
		}
	}

	private static final boolean bodyHasUnknownEncoding(Headers headers) {
		String contentEncoding = headers.get("Content-Encoding");
		return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity") && !contentEncoding.equalsIgnoreCase("gzip");
	}

	private final void populateRequestInfo(WebTrafficAudit webTraffic, final Chain chain) {
		try {
	        Request request = chain.request();
			RequestBody requestBody = request.body();
			HttpUrl url = request.url();

			webTraffic.setRequestTime(new Date());
			webTraffic.setRequestUrl(url.url().toString());
			webTraffic.setMethod(request.method());
			webTraffic.setLocale(null);
			webTraffic.setScheme(url.scheme());
			webTraffic.setDispatcherType("OUTBOUND");
			webTraffic.setContentLength(requestBody.contentLength());
			webTraffic.setContentType(requestBody.contentType().toString());
			webTraffic.setContextPath(null);
			webTraffic.setCharacterEncoding(request.headers().get("Content-Encoding"));
			webTraffic.setQueryString(url.query());
			if(auditLogBody) {
				webTraffic.setHeaders(ClobProxy.generateProxy(mapper.writeValueAsString(request.headers().toMultimap())));
			}

		} catch (Exception e) {
			logger.error("populateRequestInfo", e);
		}
	}

	private final void populateResponseInfo(WebTrafficAudit webTraffic, final Chain chain, Response response) throws JsonProcessingException, SocketException, UnknownHostException {
		try {
			Connection connection = chain.connection();
			Handshake handshake = connection.handshake();
			Socket socket = connection.socket();
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

			webTraffic.setResponseCharacterEncoding(response.headers().get("Content-Encoding"));
			webTraffic.setResponseStatus(response.code());
			webTraffic.setResponseContentType(Objects.toString(response.body().contentType()));
			if(webTraffic.getResponseContentLength() == null && response.headers().get("content-length") != null) webTraffic.setResponseContentLength(Integer.parseInt(response.headers().get("content-length")));
			webTraffic.setResponseIsCommited('Y');
			webTraffic.setResponseTime(new Date());
		} catch (Exception e) {
			logger.error("populateResponseInfo", e);
		}
	}
}
