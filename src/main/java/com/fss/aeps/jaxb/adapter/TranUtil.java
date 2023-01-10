package com.fss.aeps.jaxb.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fss.aeps.Application;
import com.fss.aeps.jaxb.PayTrans;

public class TranUtil {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static final String getTranKey(PayTrans txn) {
		String tranKey = (txn.getType().name()+ txn.getId());
		logger.info("trankey :  "+tranKey);
		return tranKey;
	}

}
