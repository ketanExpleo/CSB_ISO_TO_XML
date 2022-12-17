package com.fss.aeps.acquirer.matm;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.core.ReqPaySender;
import com.fss.aeps.cbsclient.AcquirerCbsClient;
import com.fss.aeps.cbsclient.CBSResponse;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jpa.acquirer.AcquirerTransaction;
import com.fss.aeps.repository.AcquirerRepositories.AcquirerTransactionRepository;
import com.fss.aeps.util.ExceptionUtil;

@Component
public class CWTransaction {

	private static final Logger logger = LoggerFactory.getLogger(CWTransaction.class);

	@Autowired
	private AcquirerTransactionRepository repository;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	@Qualifier(value = "threadpool")
	private ThreadPoolExecutor executor;

	@Autowired
	private AcquirerCbsClient cbsClient;

	@Autowired
	private Corrector corrector;

	public RespPay process(ReqPay request) {
		final AcquirerTransaction transaction = new AcquirerTransaction();
		request.context.put(ContextKey.ACQUIRER_TRANSACTION, transaction);
		try {
			corrector.correctData(request);
			corrector.correctAgentId(request);
			final RespPay response = appConfig.context.getBean(ReqPaySender.class).send(request);
			if (response.getResp().getResult() == ResultType.SUCCESS) {
				final CBSResponse accountingResponse = cbsClient.accountingCW(transaction).block();
				if(accountingResponse != null) {
					transaction.setCbsTranDetails(accountingResponse.tranDetails);
					transaction.setCbsResponseCode(accountingResponse.responseCode);
					transaction.setCbsAuthCode(accountingResponse.authCode);
				} else {
					//retry and check for 913
					transaction.setCbsResponseCode("91");
					logger.info("accounting response not received for txnId : " + request.getTxn().getId());
				}
				if(accountingResponse == null || "911".equalsIgnoreCase(accountingResponse.responseCode) || "91".equalsIgnoreCase(accountingResponse.responseCode)) {
					cbsClient.accountingReversal(transaction);
					response.getResp().setResult(ResultType.FAILURE);
					response.getResp().setErrCode("91");
					request.context.put(ContextKey.ORG_RESP_CODE, "22");
					executor.execute(appConfig.context.getBean(ReversalTransaction.class, request));
				}
				else if(!"000".equals(accountingResponse.responseCode)) {
					response.getResp().setResult(ResultType.FAILURE);
					response.getResp().setErrCode("91");
					executor.execute(appConfig.context.getBean(ReversalTransaction.class, request));
				}
			} else if ("91".equals(response.getResp().getErrCode())
					&& ContextKey.toBoolean(response.context.get(ContextKey.IS_STATIC_RESPONSE))) {
				logger.info("generating reversal for cash withdrawal. TxnId : " + request.getTxn().getId());
				executor.execute(appConfig.context.getBean(ReversalTransaction.class, request));
			}
			return response;
		} catch (Exception e) {
			transaction.setException(ExceptionUtil.appendBlob(transaction.getException(), e));
			logger.error("error in cash withdrawal ", e);
		} finally {
			try {
				repository.save(transaction);
			} catch (Exception e) {
				logger.error("error while saving cash withdrawal.", e);
			}
		}
		return null;
	}


}
