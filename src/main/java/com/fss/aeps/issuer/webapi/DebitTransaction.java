package com.fss.aeps.issuer.webapi;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.cbsclient.CBSResponse;
import com.fss.aeps.cbsclient.CbsClient;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.CredsType.Cred;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.Ref;
import com.fss.aeps.jaxb.RefType;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jpa.issuer.IssuerTransaction;
import com.fss.aeps.npciclient.NpciClient;
import com.fss.aeps.services.IssuerTransactionService;
import com.fss.aeps.util.UIDAIAuthCode;

import reactor.core.publisher.Mono;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DebitTransaction extends IIssuerTransaction<ReqPay, RespPay> {

	private static final Logger logger = LoggerFactory.getLogger(DebitTransaction.class);

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private IssuerTransactionService transactionService;

	@Autowired
	private NpciClient npciWebClient;

	@Autowired
	private CbsClient cbsClient;

	public DebitTransaction(ReqPay request, RespPay response) {
		super(request, response);
	}

	IssuerTransaction transaction = new IssuerTransaction();

	@Override
	public final void run() {
		try {
			logger.info("******* executing " + request.getTxn().getId());
			transaction.setReqTime(new Date());
			transactionService.populateRequestData(transaction, request);
			final HeadType head = appConfig.getHead();
			final PayTrans txn = request.getTxn();
			final PayerType payer = request.getPayer();

			final RespPay.Resp resp = new RespPay.Resp();
			resp.setReqMsgId(request.getHead().getMsgId());

			final Ref ref = new Ref();
			ref.setType(RefType.PAYER);
			ref.setAddr(payer.getAddr());
			ref.setCode(payer.getCode());
			ref.setOrgAmount(payer.getAmount().getValue());
			ref.setSeqNum(payer.getSeqNum());
			ref.setSettCurrency(payer.getAmount().getCurr());
			ref.setSettAmount(new BigDecimal(0));
			resp.getRef().add(ref);

			response.setHead(head);
			response.setTxn(txn);
			response.setResp(resp);
			txn.setRiskScores(null);

			final Mono<CBSResponse> responseMono = cbsClient.issuerDebit(request);
			if(responseMono != null) responseMono.subscribe(cbsResponse -> {
				transaction.setCbsResponseCode(cbsResponse.responseCode);
				transaction.setCbsAuthCode(cbsResponse.authCode);
				transaction.setCbsTranDetails(cbsResponse.tranDetails);
				if(cbsResponse.responseCode.equals("00")) {
					final Cred cred = request.getPayer().getCreds().getCred().get(0);
					final UIDAIAuthCode uidaiAuthCode = new UIDAIAuthCode(cred.getData().getValue());
					resp.setAuthCode(uidaiAuthCode.authCode);
					resp.setResult(ResultType.SUCCESS);
					ref.setRespCode("00");
					ref.setSettAmount(payer.getAmount().getValue());
					ref.setBalAmt(cbsResponse.balance);
					ref.setApprovalNum(cbsResponse.authCode);
				}
				else {
					final Cred cred = request.getPayer().getCreds().getCred().get(0);
					final UIDAIAuthCode uidaiAuthCode = new UIDAIAuthCode(cred.getData().getValue());
					resp.setAuthCode(uidaiAuthCode.authCode);
					resp.setResult(ResultType.FAILURE);
					resp.setErrCode(cbsResponse.responseCode);
					ref.setRespCode(resp.getErrCode());

					//do negative processing here
				}
				final Mono<Ack> monoAck = npciWebClient.paymentResponse(response);
				transaction.setRespTime(new Date());
				final Ack respAck = monoAck.block();
				response.context.put(ContextKey.RESPONSE_ACK, respAck);
			});
		} catch (Exception e) {
			logger.error("error accured in debit : " + request.getTxn().getId(), e);
		} finally {
			try {
				transactionService.registerResponse(transaction, response);
			} catch (Exception e) {
				logger.error("error accured when storing balance enquiry in database : ", e);
			}
		}
	}
}
