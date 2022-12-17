package com.fss.aeps.http.filters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.xml.crypto.dsig.XMLSignature;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.tomcat.util.net.SSLSupport;
import org.hibernate.engine.jdbc.BlobProxy;
import org.hibernate.engine.jdbc.ClobProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fss.aeps.AppConfig;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jpa.TrafficAudit;
import com.fss.aeps.jpa.UnknownTrafficAudit;
import com.fss.aeps.jpa.WebTrafficAudit;
import com.fss.aeps.jpa.acquirer.AcqTrafficAudit;
import com.fss.aeps.util.InvalidXmlSignatureException;
import com.fss.aeps.util.ServletUtils;
import com.fss.aeps.util.XMLUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SignatureAndLoggingFilter extends HttpFilter implements OrderedFilter {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(SignatureAndLoggingFilter.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private EntityManagerFactory emf;

	@Value("${server.http.logbody:#{false}}")
	private boolean logBody;

	private boolean auditLog;
	private boolean auditLogBody;

	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	@Autowired
	@Qualifier("npciSignerPublicKey")
	private PublicKey npciSignerPublicKey;

	public SignatureAndLoggingFilter(@Value("${server.http.audit.log:#{false}}") boolean auditLog,
			@Value("${server.http.audit.log.body:#{false}}") boolean auditLogBody) {
		this.auditLog = auditLog;
		this.auditLogBody = this.auditLog && auditLogBody;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		logger.info("****** SignatureFilter initialized");
	}

	// @formatter:off
	@Override
	public final void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		TrafficAudit traffic = new UnknownTrafficAudit();
		try {
			final String contextPath = request.getRequestURI();
			final String method = request.getMethod();
			if(!("GET".equalsIgnoreCase(method) || "POST".equalsIgnoreCase(method))) {
				logger.error("request rejected for method : "+method);
				if(auditLog) populateRequestInfo(request, traffic);
				response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
				return;
			}
			if("GET".equalsIgnoreCase(method)) {
				logger.info("skipping signature filter for GET request.");
				if(auditLog) populateRequestInfo(request, traffic);
				chain.doFilter(request, response);
				return;
			}

			final int port = request.getLocalPort();
			if(port == appConfig.npciListenPort && contextPath.startsWith("/aeps/")) {
				logger.info("request from npci");
				traffic = new WebTrafficAudit();
				if(auditLog) populateRequestInfo(request, traffic);
				response = signatureFilter(request, response, chain, traffic);
			}
			else if(port != appConfig.npciListenPort && !contextPath.startsWith("/aeps/")){
				logger.info("request from acquirer");
				traffic = new AcqTrafficAudit();
				if(auditLog) populateRequestInfo(request, traffic);
				response = loggingFilter(request, response, chain, traffic);
			}
			else {
				if(auditLog) populateRequestInfo(request, traffic);
				if(auditLogBody) traffic.setBody(BlobProxy.generateProxy(request.getInputStream().readAllBytes()));
				logger.error("request rejected as port is : "+port+" and context path is '"+contextPath+"'");
				response.sendError(HttpStatus.FORBIDDEN.value());
				return;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if(auditLog) {
					populateResponseInfo(response, traffic);
					EntityManager em = emf.createEntityManager();
					em.getTransaction().begin();
					em.persist(traffic);
					em.getTransaction().commit();
					em.close();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void destroy() {}

	private final HttpServletResponse signatureFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain, TrafficAudit traffic) throws IOException, ServletException {
		final Map<String, Object> context = new HashMap<>();
		request.setAttribute(ContextKey.HTTP_CONTEXT, context);
		final String url = request.getRequestURL().toString();
		final boolean isReq = url.split("aeps/")[1].startsWith("Req");
		if(isReq && appConfig.isShutdowned) {
			response.sendError(HttpStatus.SERVICE_UNAVAILABLE.value());
			return response;
		}
		final CompletableFuture<Object> future = new CompletableFuture<>();
		context.put(ContextKey.FUTURE, future);
		final Map<String, Collection<String>> headers = new HashMap<>();
		final StringBuilder sb = new StringBuilder();
		final byte[] requestBytes = request.getInputStream().readAllBytes();
		if(!isReq) {
			context.put(ContextKey.RAW_RESPONSE_BODY, requestBytes);
			context.put(ContextKey.RESPONSE_URL, request.getRequestURL());
			context.put(ContextKey.BROADCASTED_RESPONSE, request.getHeader("BROADCASTED_RESPONSE"));
		}
		if(auditLogBody) traffic.setBody(BlobProxy.generateProxy(requestBytes));
		sb.append("\r\n<-- npci ").append((isReq ? "request" : "response")).append(" received.").append("\r\n");
		sb.append("<-- ").append(request.getMethod()).append(" ").append(url).append("\r\n");
		sb.append("<-- Content-Length : ").append(request.getContentLength()).append("\r\n");
		if(logBody || auditLogBody) {
			request.getHeaderNames().asIterator().forEachRemaining(n -> headers.put(n, Collections.list(request.getHeaders(n))));
			if(logBody) sb.append("<-- Headers : ").append(headers).append("\r\n");
			if(auditLogBody) traffic.setHeaders(ClobProxy.generateProxy(mapper.writeValueAsString(headers)));
		}
		final HttpServletRequest wrappedRequest = ServletUtils.getWrappedRequest(request, requestBytes);
		try {
			final Document requestDocument = XMLUtils.bytesToDocument(requestBytes);
			final boolean isValidSignature = XMLUtils.validateXMLDigitalSignature(npciSignerPublicKey, requestDocument);
			final NodeList signatureNodes = requestDocument.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			if(signatureNodes.getLength() > 0) {
				signatureNodes.item(0).getParentNode().removeChild(signatureNodes.item(0));
			}
			if(logBody) sb.append("<-- Body : ").append(XMLUtils.documentToFormattedString(requestDocument)).append("\r\n");
			sb.append("<-- isValidSignature : ").append(isValidSignature).append("\r\n");
			logger.info(sb.toString());
			sb.setLength(0);
			if (!isValidSignature) {
				final Element head = (Element) requestDocument.getElementsByTagName("Head").item(0);
				throw new InvalidXmlSignatureException("invalid xml signature for msgId : "+head.getAttribute("msgId"));
			}
			//logger.info("signature verified.");
		} catch (InvalidXmlSignatureException e) {
			logger.error(e.getMessage(), e);
			response.sendError(HttpStatus.UNAUTHORIZED.value());
			return response;
		}

		final ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
				//ServletUtils.getWrappedResponse(response, responseOutputStream);
		chain.doFilter(wrappedRequest, wrappedResponse);
		final byte[] responseBytes = wrappedResponse.getContentAsByteArray();
		//final Document signeddocument = signer.generateXMLDigitalSignature(responseDocument);
		//final byte[] signedxml = XMLUtils.documentToByteArray(signeddocument);
		response.getOutputStream().write(responseBytes);
		traffic.setResponseContentLength(responseBytes.length);
		traffic.setResponseTime(new Date());
		sb.append("\r\n--> ").append(wrappedResponse.getStatus()).append(" ").append(url).append("\r\n");
		sb.append("--> Content-Length : ").append(responseBytes.length).append("\r\n");
		if(logBody || auditLogBody) {
			headers.clear();
			response.getHeaderNames().forEach(name -> System.out.println(name));
			wrappedResponse.getHeaderNames().forEach(n -> headers.put(n, response.getHeaders(n)));
			if(logBody) sb.append("--> Headers : ").append(headers).append("\r\n");
			if(auditLogBody) traffic.setResponseHeaders(ClobProxy.generateProxy(mapper.writeValueAsString(headers)));
		}
		if(logBody) sb.append("--> Body : ").append(new String(responseBytes)).append("\r\n");
		if(auditLogBody) traffic.setResponseBody(BlobProxy.generateProxy(responseBytes));
		sb.append("--> npci ack delivered.");
		logger.info(sb.toString());
		future.complete("completed");
		return wrappedResponse;
	}

	private final HttpServletResponse loggingFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain, TrafficAudit traffic) throws IOException, ServletException {
		byte[] requestBytes = new byte[0];
		HttpServletRequest wrappedRequest = request;
		try {
			if(request.getContentLength() > 0 || "chunked".equalsIgnoreCase(request.getHeader("transfer-encoding"))) {
				logger.info("request.getContentLength() is greater than 0");
				requestBytes = request.getInputStream().readAllBytes();
				wrappedRequest = ServletUtils.getWrappedRequest(request, requestBytes);
				logger.info("requestBytes.length : "+requestBytes.length);
			}
		} catch (ClientAbortException e) {
			logger.info("no data can be read from client because : "+e.getMessage());
		}
		if(auditLogBody) traffic.setBody(BlobProxy.generateProxy(requestBytes));
		final Map<String, Collection<String>> headers = new HashMap<>();
		final StringBuilder sb = new StringBuilder();
		sb.append("\r\n<<- acquirer request received.").append("\r\n");
		sb.append("<<- ").append(request.getMethod()).append(" ").append(request.getRequestURL().toString()).append("\r\n");
		sb.append("<<- Content-Length : ").append(request.getContentLength()).append("\r\n");
		if(logBody || auditLogBody) {
			request.getHeaderNames().asIterator().forEachRemaining(n -> headers.put(n, Collections.list(request.getHeaders(n))));
			if(logBody) sb.append("<<- Headers : ").append(headers).append("\r\n");
			if(auditLogBody) traffic.setHeaders(ClobProxy.generateProxy(mapper.writeValueAsString(headers)));
		}
		if(logBody) sb.append("<<- \r\n"+new String(requestBytes));
		logger.info(sb.toString());
		sb.setLength(0);
		final ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
		chain.doFilter(wrappedRequest, wrappedResponse);
		final byte[] responseBytes = wrappedResponse.getContentAsByteArray();
		response.getOutputStream().write(responseBytes);
		traffic.setResponseContentLength(responseBytes.length);
		traffic.setResponseTime(new Date());
		if(auditLogBody) traffic.setResponseBody(BlobProxy.generateProxy(responseBytes));
		sb.append("\r\n->> ").append(wrappedResponse.getStatus()).append(" ").append(request.getRequestURL().toString()).append("\r\n");
		sb.append("->> Content-Length : ").append(responseBytes.length).append("\r\n");
		if(logBody || auditLogBody) {
			headers.clear();
			wrappedResponse.getHeaderNames().forEach(n -> headers.put(n, wrappedResponse.getHeaders(n)));
			if(logBody) sb.append("->> Headers : ").append(headers).append("\r\n");
			if(auditLogBody) traffic.setResponseHeaders(ClobProxy.generateProxy(mapper.writeValueAsString(headers)));
		}
		if(logBody) sb.append("->> Body : \r\n").append(new String(responseBytes)).append("\r\n");
		sb.append("->> acquirer response delivered.");
		logger.info(sb.toString());
		return wrappedResponse;
	}

	private static final void populateRequestInfo(HttpServletRequest request, TrafficAudit traffic) {
		try {
			traffic.setRequestTime(new Date());
			traffic.setRequestUrl(request.getRequestURL().toString());
			traffic.setMethod(request.getMethod());
			traffic.setProtocol(request.getProtocol());
			traffic.setLocale(Objects.toString(request.getLocale()));
			traffic.setScheme(request.getScheme());
			traffic.setDispatcherType(Objects.toString(request.getDispatcherType()));
			traffic.setContentLength(request.getContentLengthLong());
			traffic.setContentType(request.getContentType());
			traffic.setContextPath(request.getContextPath());
			traffic.setCharacterEncoding(request.getCharacterEncoding());
			traffic.setQueryString(request.getQueryString());
			traffic.setRemoteAddr(request.getRemoteAddr());
			traffic.setRemoteHost(request.getRemoteHost());
			traffic.setRemotePort(request.getRemotePort());
			traffic.setLocalAddr(request.getLocalAddr());
			traffic.setLocalName(request.getLocalName());
			traffic.setLocalPort(request.getLocalPort());
			traffic.setServerName(request.getServerName());
			traffic.setServerPort(request.getServerPort());
			traffic.setCipherUsed((String) request.getAttribute(SSLSupport.CIPHER_SUITE_KEY));
			traffic.setTlsProtocol((String) request.getAttribute(SSLSupport.PROTOCOL_VERSION_KEY));
			traffic.setRequestedTlsProtocols((String) request.getAttribute(SSLSupport.REQUESTED_PROTOCOL_VERSIONS_KEY));
		} catch (Exception e) {
			logger.error("populateRequestInfo", e);
		}
	}

	private final void populateResponseInfo(HttpServletResponse response, TrafficAudit traffic) {
		try {
			traffic.setResponseIsCommited(response.isCommitted()? 'Y' : 'N');
			traffic.setResponseContentType(response.getContentType());
			traffic.setResponseCharacterEncoding(response.getCharacterEncoding());
			if(traffic.getResponseStatus() == null) traffic.setResponseStatus(response.getStatus());
			if(traffic.getResponseTime() == null) traffic.setResponseTime(new Date());
			if(traffic.getResponseHeaders() == null && auditLogBody) {
				final Map<String, Collection<String>> headers = new HashMap<>();
				response.getHeaderNames().forEach(n -> headers.put(n, response.getHeaders(n)));
				traffic.setResponseHeaders(ClobProxy.generateProxy(mapper.writeValueAsString(headers)));
			}
		} catch (Exception e) {
			logger.error("populateResponseInfo", e);
		}
	}

	@Override
	public int getOrder() {
		return HIGHEST_PRECEDENCE;
	}

	public static String getEncoding(HttpServletRequest request) {
		return request.getCharacterEncoding() == null ? StandardCharsets.US_ASCII.name() : request.getCharacterEncoding();
	}

}