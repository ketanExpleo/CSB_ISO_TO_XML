package com.fss.aeps.test;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Connection;
import okhttp3.Headers;
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

public final class RetroLoggingInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(RetroLoggingInterceptor.class);

	private static final Charset UTF8 = Charset.forName("UTF-8");
	private volatile Level level = Level.BODY;

	public enum Level {
		NONE, BASIC, HEADERS, BODY
	}

	//@formatter:off
	public RetroLoggingInterceptor() {}

	public RetroLoggingInterceptor(final Level level) {
		this.level = level;
	}

	private volatile Set<String> headersToRedact = Collections.emptySet();

	public final void redactHeader(String name) {
		Set<String> newHeadersToRedact = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		newHeadersToRedact.addAll(headersToRedact);
		newHeadersToRedact.add(name);
		headersToRedact = newHeadersToRedact;
	}

	public RetroLoggingInterceptor setLevel(Level level) {
		if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
		this.level = level;
		return this;
	}

	public final Level getLevel() {
		return level;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		final StringBuilder sb = new StringBuilder("\r\n");
		Request request = chain.request();
		if (level == Level.NONE) {
			logger.info("retrofit logging skipped because level set to none.");
			return chain.proceed(request);
		}

		final boolean logBody    = level == Level.BODY;
		final boolean logHeaders = logBody || level == Level.HEADERS;

		final RequestBody requestBody    = request.body();
		final boolean     hasRequestBody = requestBody != null;

		Connection connection = chain.connection();
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

				Charset   charset     = UTF8;
				MediaType contentType = requestBody.contentType();
				if (contentType != null) { charset = contentType.charset(UTF8); }

				sb.append("\r\n");
				if (isPlaintext(buffer)) {
					sb.append("--> body : ").append(buffer.readString(charset)).append("\r\n");
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
			throw e;
		}
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
					sb.append("\r\n").append("<-- body : ");
					sb.append(buffer.clone().readString(charset)).append("\r\n");
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
}
