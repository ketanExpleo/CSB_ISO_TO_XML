package com.fss.aeps.issuer.webapi;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.cbsclient.CBSResponse;
import com.fss.aeps.cbsclient.IssuerCbsClient;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayeeType;
import com.fss.aeps.jaxb.Ref;
import com.fss.aeps.jaxb.RefType;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jpa.issuer.IssuerTransaction;
import com.fss.aeps.npciclient.NpciClient;
import com.fss.aeps.services.IssuerTransactionService;

import reactor.core.publisher.Mono;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreditTransaction extends IIssuerTransaction<ReqPay, RespPay> {

	private static final Logger logger = LoggerFactory.getLogger(CreditTransaction.class);

	@Autowired
	private IssuerTransactionService transactionService;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private NpciClient npciWebClient;

	@Autowired
	private IssuerCbsClient cbsClient;

	public CreditTransaction(ReqPay request, RespPay response) {
		super(request, response);
	}

	IssuerTransaction transaction = new IssuerTransaction();

	@Override
	public final void run() {
		try {
			logger.info("******* executing " + request.getTxn().getId());
			transaction.setReqTime(new Date());
			transactionService.populateRequestData(transaction, request);
			final List<PayeeType> payees = request.getPayees().getPayee();
			final HeadType head = appConfig.getHead();
			final PayTrans txn = request.getTxn();
			final RespPay.Resp resp = new RespPay.Resp();
			resp.setReqMsgId(request.getHead().getMsgId());

			response.setHead(head);
			response.setTxn(txn);
			response.setResp(resp);
			txn.setRiskScores(null);

			final Mono<CBSResponse> responseMono = cbsClient.creditFundTransfer(request);
			if(responseMono != null) responseMono.subscribe(cbsResponse -> {
				transaction.setCbsResponseCode(cbsResponse.responseCode);
				transaction.setCbsAuthCode(cbsResponse.authCode);
				transaction.setCbsTranDetails(cbsResponse.tranDetails);
				if(cbsResponse.responseCode.equals("000")) {
					resp.setResult(ResultType.SUCCESS);
					payees.forEach(payee -> {
						Ref ref = new Ref();
						ref.setType(RefType.PAYEE);
						ref.setSeqNum(payee.getSeqNum());
						ref.setCode(payee.getCode());
						ref.setRegName(cbsResponse.customerName);
						ref.setAcNum(cbsResponse.operatedAccount);
						ref.setSettAmount(payee.getAmount().getValue());
						ref.setSettCurrency(payee.getAmount().getCurr());
						ref.setRespCode("00");
						ref.setOrgAmount(payee.getAmount().getValue());
						ref.setApprovalNum(cbsResponse.authCode);
						resp.getRef().add(ref);
					});
				}
				else {
					resp.setResult(ResultType.FAILURE);
					resp.setErrCode(cbsToNpciResponseMapper.map(cbsResponse.responseCode));

					payees.forEach(payee -> {
						Ref ref = new Ref();
						ref.setType(RefType.PAYEE);
						ref.setRegName(cbsResponse.customerName+" ");
						ref.setAcNum(cbsResponse.operatedAccount);
						ref.setSettAmount(payee.getAmount().getValue());
						ref.setSettCurrency(payee.getAmount().getCurr());
						ref.setSeqNum(payee.getSeqNum());
						ref.setRespCode(resp.getErrCode());
						ref.setOrgAmount(payee.getAmount().getValue());
						ref.setCode(payee.getCode());
						resp.getRef().add(ref);
					});

					//do negative processing here
				}
				logger.info("sending response of credit.");
				final Mono<Ack> monoAck = npciWebClient.paymentResponse(response);
				transaction.setRespTime(new Date());
				final Ack respAck = monoAck.block();
				response.context.put(ContextKey.RESPONSE_ACK, respAck);
			});
		} catch (Exception e) {
			logger.error("error accured in credit : " + request.getTxn().getId(), e);
		} finally {
			try {
				transactionService.registerResponse(transaction, response);
			} catch (Exception e) {
				logger.error("error accured when storing balance enquiry in database : ", e);
			}
		}
	}
}
