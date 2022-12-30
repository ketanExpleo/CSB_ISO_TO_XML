package com.fss.aeps.http.filters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;

public final class LoggingHandler implements SOAPHandler<SOAPMessageContext> {

	private static final Logger logger = LoggerFactory.getLogger(LoggingHandler.class);
	
	@Override
	public boolean handleMessage(final SOAPMessageContext context) {
		log(context);
		return true;
	}

	@Override
	public boolean handleFault(final SOAPMessageContext context) {
		log(context);
		return true;
	}

	@Override
	public void close(MessageContext context) {

	}

	@Override
	public Set<QName> getHeaders() {
		return new HashSet<QName>();
	}

	private static final void log(final SOAPMessageContext context) {
		final Boolean     isOutBound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		final SOAPMessage soapMessage    = context.getMessage();
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (isOutBound) {
				logger.info("--> " +(String) context.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
			} else {
				logger.info("<-- inbound message");
			}
			soapMessage.writeTo(baos);
			logger.info(new String(baos.toByteArray()));
		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
