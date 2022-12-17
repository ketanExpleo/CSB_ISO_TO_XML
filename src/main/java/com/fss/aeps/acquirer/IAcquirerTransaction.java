package com.fss.aeps.acquirer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IAcquirerTransaction<R, S> extends Runnable {

	static final Logger logger = LoggerFactory.getLogger(IAcquirerTransaction.class);

	public void processResponse(S response);

	@Override
	public default void run() {
		logger.warn("AcquirerTransaction run method called.");
	}

}
