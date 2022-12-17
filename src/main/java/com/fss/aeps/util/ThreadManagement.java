package com.fss.aeps.util;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.RespBioAuth;
import com.fss.aeps.jaxb.RespPay;

public final class ThreadManagement {

	private static final Logger logger = LoggerFactory.getLogger(ThreadManagement.class);

	public static final String waitOtherForCompletion(final RespPay response) {
		try {
			final CompletableFuture<?> future = (CompletableFuture<?>) response.context.get(ContextKey.FUTURE);
			final Date startFuture = new Date();
			final Object object = future.get();
			final Date endFuture = new Date();
			logger.info("the future returned '"+object+"'"+" in "+(endFuture.getTime()-startFuture.getTime())+"ms");
			return object.toString();
		} catch (Exception e) {
			logger.error("error processing future.", e);
		}
		return null;
	}

	public static final String waitOtherForCompletion(final RespBioAuth response) {
		try {
			final CompletableFuture<?> future = (CompletableFuture<?>) response.context.get(ContextKey.FUTURE);
			if(future != null) {
				final Date startFuture = new Date();
				final Object object = future.get();
				final Date endFuture = new Date();
				logger.info("the future returned '"+object+"'"+" in "+(endFuture.getTime()-startFuture.getTime())+"ms");
				return object.toString();
			}
			else logger.error("the future is null. no wait is required.");
		} catch (Exception e) {
			logger.error("error processing future.", e);
		}
		return null;
	}

}
