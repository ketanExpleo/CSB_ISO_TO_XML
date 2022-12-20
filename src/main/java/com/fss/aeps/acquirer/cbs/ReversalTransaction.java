package com.fss.aeps.acquirer.cbs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.core.RevPaySender;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jaxb.TxnSubType;
import com.fss.aeps.jpa.acquirer.AcquirerReversal;
import com.fss.aeps.util.Generator;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class ReversalTransaction implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ReversalTransaction.class);

	@Autowired
	private AppConfig appConfig;

	private ReqPay payRequest;

	public ReversalTransaction(ReqPay payRequest) {
		this.payRequest = payRequest;
	}

	@Override
	public void run() {
		String orgRespCode = (String) payRequest.context.get(ContextKey.ORG_RESP_CODE);
		final ReqPay reversal = new ReqPay();
		reversal.setTxn(payRequest.getTxn().clone());
		reversal.getTxn().setOrgTxnId(payRequest.getTxn().getId());
		reversal.getTxn().setOrgTxnDate(payRequest.getTxn().getTs());
		reversal.getTxn().setOrgRespCode("68");
		if(orgRespCode != null) reversal.getTxn().setOrgRespCode(orgRespCode);
		reversal.getTxn().setType(PayConstant.REVERSAL);
		reversal.getTxn().setSubType(TxnSubType.DEBIT);
		reversal.getTxn().setNote("REVERSAL");
		reversal.setPayer(payRequest.getPayer());
		reversal.getPayer().setCreds(null);
		//reversal.getPayer().setDevice(null);
		reversal.setPayees(payRequest.getPayees());
		reversal.getPayees().getPayee().stream().forEach(p -> {
			p.setCreds(null);
		});
		
		reversal.context.put(ContextKey.CHANNEL, payRequest.context.get(ContextKey.CHANNEL));
		reversal.context.put(ContextKey.AGENT_DETAILS, payRequest.context.get(ContextKey.AGENT_DETAILS));
		reversal.context.put(ContextKey.ORG_TXN_MSG_ID, payRequest.getHead().getMsgId());
		for (int i = 0; i < 4; i++) {
			reversal.getTxn().setId(Generator.newRandomTxnId(appConfig.participationCode));
			reversal.setHead(appConfig.getHead());
			if(i > 0) reversal.getTxn().setNote("REPEAT_REVERSAL");
			final AcquirerReversal acquirerReversal =  new AcquirerReversal();
			reversal.context.put(ContextKey.ACQUIRER_REVERSAL, acquirerReversal);
			final RespPay respReversal = appConfig.context.getBean(RevPaySender.class).send(reversal);
			logger.info("reversal response received : " + respReversal);
			if(respReversal.getResp().getResult() == ResultType.FAILURE && "91".equals(respReversal.getResp().getErrCode())) {
				continue;
			}
			else break;
		}
	}
}