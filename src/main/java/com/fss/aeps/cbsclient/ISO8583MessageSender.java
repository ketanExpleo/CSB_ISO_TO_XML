package com.fss.aeps.cbsclient;

import static org.util.iso8583.api.ISO8583ExceptionCause.SOCKET_CONNECT_ERROR;
import static org.util.iso8583.api.ISO8583ExceptionCause.SOCKET_WRITE_ERROR;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import org.hibernate.engine.jdbc.BlobProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.util.iso8583.EncoderDecoder;
import org.util.iso8583.ISO8583Message;
import org.util.iso8583.api.ISO8583Exception;
import org.util.iso8583.api.ISOFormat;
import org.util.iso8583.util.ByteHexUtil;

import com.fss.aeps.AppConfig;
import com.fss.aeps.jpa.CbsTransaction;
import com.fss.aeps.repository.CbsTransactionRepository;

@Component
public class ISO8583MessageSender {

	private static final Logger logger = LoggerFactory.getLogger(ISO8583MessageSender.class);

	private final InetSocketAddress cbsAddress;

	@Autowired
	private CbsTransactionRepository repository;

	public ISO8583MessageSender(@Autowired AppConfig appConfig) {
		String[] ipPort = appConfig.cbsBaseUrl.split(":");
		cbsAddress = InetSocketAddress.createUnresolved(ipPort[0], Integer.parseInt(ipPort[1]));
	}

	public final ISO8583Message send(final String channel, final String txnType, final ISO8583Message request, final ISOFormat format, final int timeoutInMs) {
		final CbsTransaction cbsTransaction = new CbsTransaction();
		if("ACQUIRER".equalsIgnoreCase(channel)) cbsTransaction.setIsAcquirer("A");
		else if("ISSUER".equalsIgnoreCase(channel)) cbsTransaction.setIsAcquirer("I");
		else cbsTransaction.setIsAcquirer("O");
		cbsTransaction.setTranType(txnType);
		populateISO8583Request(cbsTransaction, request);
		final byte[] bytes = EncoderDecoder.encode(format, request);
		cbsTransaction.setRequestBody(BlobProxy.generateProxy(bytes));
		try(final Socket socket = getSocket(cbsAddress.getHostName(), cbsAddress.getPort(), timeoutInMs);
			final InputStream in = socket.getInputStream();
			final OutputStream out = socket.getOutputStream()) {
			populateSocketInfo(cbsTransaction, socket);
			logger.debug("request bytes : "+ByteHexUtil.byteToHex(bytes));
			try {
				out.write(bytes);
				out.flush();
			} catch (Exception e) {
				throw new ISO8583Exception(SOCKET_WRITE_ERROR, "socket write error.", e);
			}
			final byte[] responseBytes = EncoderDecoder.readNextMessageBytes(format, in);
			cbsTransaction.setResponseBody(BlobProxy.generateProxy(responseBytes));
			logger.debug("response bytes : "+ByteHexUtil.byteToHex(responseBytes));
			final ISO8583Message response = EncoderDecoder.decode(format, responseBytes);
			populateISO8583Response(cbsTransaction, response);
			return response;
		} catch (IOException e) {
			throw new ISO8583Exception(SOCKET_CONNECT_ERROR, "socket connect error.", e);
		} finally {
			try {
				repository.save(cbsTransaction);
			} catch (Exception e) {logger.error("error while saving CbsTransaction", e);}
		}
	}


	private static final Socket getSocket(final String host, final int port, final int timeoutInMs) throws UnknownHostException, IOException {
		final Socket socket = new Socket(host, port);
		socket.setSoTimeout(timeoutInMs);
		return socket;
	}

	private void populateISO8583Request(CbsTransaction cbsTransaction, ISO8583Message request) {
		cbsTransaction.setMti(request.get(0));
		cbsTransaction.setPan(request.get(2));
		cbsTransaction.setPcode(request.get(3));
		cbsTransaction.setTime(request.get(12));
		cbsTransaction.setRrn(request.get(37));
		cbsTransaction.setReconIndicator(request.get(126));
		cbsTransaction.setRequestTime(new Date());
	}

	private void populateISO8583Response(CbsTransaction cbsTransaction, ISO8583Message response) {
		cbsTransaction.setResponseTime(new Date());
		if(response != null) {
			cbsTransaction.setAuthCode(response.get(38));
			cbsTransaction.setResponseCode(response.get(39));
			cbsTransaction.setTranDetails(response.get(127));
		}
	}

	private void populateSocketInfo(CbsTransaction cbsTransaction, Socket socket) {
		try {
			InetSocketAddress remoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
			InetSocketAddress localSocketAddress = (InetSocketAddress) socket.getLocalSocketAddress();
			cbsTransaction.setRemoteAddr(remoteSocketAddress.getAddress().getHostAddress());
			cbsTransaction.setRemotePort(remoteSocketAddress.getPort());
			cbsTransaction.setLocalAddr(localSocketAddress.getAddress().getHostAddress());
			cbsTransaction.setLocalPort(localSocketAddress.getPort());
		} catch (Exception e) {logger.trace("populateSocketInfo", e);}
	}

}
