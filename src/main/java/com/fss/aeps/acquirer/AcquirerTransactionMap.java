package com.fss.aeps.acquirer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.ReqBioAuth;
import com.fss.aeps.jaxb.ReqChkTxn;
import com.fss.aeps.jaxb.ReqHbt;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespBalEnq;
import com.fss.aeps.jaxb.RespBioAuth;
import com.fss.aeps.jaxb.RespChkTxn;
import com.fss.aeps.jaxb.RespHbt;
import com.fss.aeps.jaxb.RespPay;

public final class AcquirerTransactionMap {

	public static final Map<String, IAcquirerTransaction<ReqHbt, RespHbt>> heartbeats = new ConcurrentHashMap<>();
	public static final Map<String, IAcquirerTransaction<ReqPay, RespPay>> payments = new ConcurrentHashMap<>();
	public static final Map<String, IAcquirerTransaction<ReqChkTxn, RespChkTxn>> advices = new ConcurrentHashMap<>();
	public static final Map<String, IAcquirerTransaction<ReqBalEnq, RespBalEnq>> metas = new ConcurrentHashMap<>();
	public static final Map<String, IAcquirerTransaction<ReqBioAuth, RespBioAuth>> authentications = new ConcurrentHashMap<>();

}
