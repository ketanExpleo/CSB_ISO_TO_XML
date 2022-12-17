package com.fss.aeps.acquirer.merchant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;

import org.hibernate.engine.jdbc.BlobProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fss.aeps.AppConfig;
import com.fss.aeps.jpa.TrafficAudit;
import com.fss.aeps.jpa.acquirer.AcqTrafficAudit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RestController
public class MerchantTransaction implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MerchantTransaction.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private EntityManagerFactory emf;

	@Value("${server.http.audit.log:#{false}}")
	private boolean auditLog;

	@Value("${server.http.audit.log.body:#{false}}")
	private boolean auditLogBody;

	private Socket socket;
	private MerchantRequest merchantRequest;
	private AcqTrafficAudit webTraffic = new AcqTrafficAudit();

	public MerchantTransaction(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			auditLogBody = auditLog && auditLogBody;
			populateRequestInfo();
			merchantRequest = readRequest(webTraffic);
			if("AEPS_OFFUS_MER_PUR_PMT".equals(merchantRequest.reconIndicator)) {
				final MerchantResponse merchantResponse = appConfig.context.getBean(MerchantOffusPurchase.class).process(socket, merchantRequest);
				//if(0 == 0) throw new RuntimeException("manual error");
				writeResponse(merchantResponse);
			}
			else if("AEPS_ONUS_MER_PUR_PMT".equals(merchantRequest.reconIndicator)) {
				final MerchantResponse merchantResponse = appConfig.context.getBean(MerchantOnusPurchase.class).process(socket, merchantRequest);
				writeResponse(merchantResponse);
			}
		} catch (Exception e) {
			logger.error("error processing merchant transaction.", e);
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

	private MerchantRequest readRequest(TrafficAudit webTraffic) throws IOException {
		logger.info("processing socket.");
		InputStream in = socket.getInputStream();
		int b1 = in.read();
		int b2 = in.read();
		if(b1 < 0 || b2 < 0) throw new RuntimeException("invalid length in merchant request.");
		int len = (b1 & 0xFF)* 256 + (b2 & 0xFF);
		logger.info("len : "+len);
		byte[] bytes = new byte[len];
		int readCount = in.read(bytes);
		if(readCount != len) throw new RuntimeException("invalid length in merchant request.");
		logger.info("readcount : "+readCount);
		logger.info("request : \r\n"+new String(bytes));
		if(auditLogBody) webTraffic.setBody(BlobProxy.generateProxy(bytes));
		webTraffic.setContentLength((long) len);
		return mapper.readValue(bytes, MerchantRequest.class);
	}

	private void writeResponse(MerchantResponse merchantResponse) throws IOException {
		try(Socket socket = this.socket) {
			logger.info("processing response");
			final OutputStream os = socket.getOutputStream();
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(new byte[2]);
			mapper.writeValue(baos, merchantResponse);
			final byte[] responseBytes = baos.toByteArray();
			responseBytes[0] = (byte) ((responseBytes.length-2)/256);
			responseBytes[1] = (byte) ((responseBytes.length-2)%256);
			os.write(responseBytes);
			os.flush();
			responseBytes[0] = '\r';
			responseBytes[1] = '\n';
			logger.info("response : \r\n"+new String(responseBytes));
			webTraffic.setResponseContentLength(responseBytes.length);
			if(auditLogBody) webTraffic.setResponseBody(BlobProxy.generateProxy(responseBytes));
			webTraffic.setResponseIsCommited('Y');
			webTraffic.setResponseTime(new Date());
			logger.info("response delivered.");
		}
	}

	private void populateRequestInfo() {
		try {
			webTraffic.setRequestTime(new Date());
			webTraffic.setRequestUrl(socket.getLocalAddress().toString());
			webTraffic.setMethod("SOCKET");
			webTraffic.setProtocol("TCP");
			webTraffic.setLocale(null);
			webTraffic.setScheme("tcp");
			webTraffic.setDispatcherType("SOCKET");
			webTraffic.setContentType("STRING");

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
			webTraffic.setResponseContentType("STRING");

		} catch (Exception e) {
			logger.error("populateResponseInfo", e);
		}
	}
}