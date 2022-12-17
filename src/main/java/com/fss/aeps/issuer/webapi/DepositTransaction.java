package com.fss.aeps.issuer.webapi;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fss.aeps.cbsclient.CBSResponse;
import com.fss.aeps.cbsclient.IssuerCbsClient;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.CredsType.Cred;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayeeType;
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
public class DepositTransaction extends IIssuerTransaction<ReqPay, RespPay> {

	private static final Logger logger = LoggerFactory.getLogger(DepositTransaction.class);

	@Autowired
	private IssuerTransactionService transactionService;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private NpciClient npciWebClient;

	@Autowired
	private IssuerCbsClient cbsClient;

	public DepositTransaction(ReqPay request, RespPay response) {
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
			final HeadType head = context.getBean(HeadType.class);
			final RespPay.Resp resp = new RespPay.Resp();
			resp.setReqMsgId(request.getHead().getMsgId());

			response.setHead(head);
			response.setTxn(request.getTxn());
			response.setResp(resp);

			final Mono<CBSResponse> responseMono = cbsClient.deposit(request);
			if(responseMono != null) responseMono.subscribe(cbsResponse -> {
				transaction.setCbsResponseCode(cbsResponse.responseCode);
				transaction.setCbsAuthCode(cbsResponse.authCode);
				if(cbsResponse.responseCode.equals("000")) {
					transaction.setCbsResponseCode(cbsResponse.responseCode);
					transaction.setCbsAuthCode(cbsResponse.authCode);
					transaction.setCbsTranDetails(cbsResponse.tranDetails);
					final Cred cred = request.getPayees().getPayee().get(0).getCreds().getCred().get(0);
					final UIDAIAuthCode uidaiAuthCode = new UIDAIAuthCode(cred.getData().getValue());
					resp.setAuthCode(uidaiAuthCode.authCode);
					resp.setResult(ResultType.SUCCESS);
					payees.forEach(payee -> {
						Ref ref = new Ref();
						ref.setRegName(cbsResponse.customerName);
						//ref.setAcNum(cbsResponse.operatedAccount);
						ref.setType(RefType.PAYEE);
						ref.setSettAmount(payee.getAmount().getValue());
						ref.setSettCurrency(payee.getAmount().getCurr());
						ref.setSeqNum(payee.getSeqNum());
						ref.setRespCode("00");
						ref.setOrgAmount(payee.getAmount().getValue());
						ref.setCode(payee.getCode());
						ref.setApprovalNum(String.format("%06d", new Random().nextInt(999999)));
						resp.getRef().add(ref);
					});
				}
				else {
					final Cred cred = request.getPayees().getPayee().get(0).getCreds().getCred().get(0);
					final UIDAIAuthCode uidaiAuthCode = new UIDAIAuthCode(cred.getData().getValue());
					resp.setAuthCode(uidaiAuthCode.authCode);
					resp.setResult(ResultType.FAILURE);
					resp.setErrCode(cbsToNpciResponseMapper.map(cbsResponse.responseCode));

					payees.forEach(payee -> {
						Ref ref = new Ref();
						ref.setType(RefType.PAYEE);
						ref.setRegName(cbsResponse.customerName+" ");
						ref.setSettAmount(new BigDecimal(0));
						ref.setSettCurrency(payee.getAmount().getCurr());
						ref.setSeqNum(payee.getSeqNum());
						ref.setRespCode(resp.getErrCode());
						ref.setOrgAmount(payee.getAmount().getValue());
						ref.setCode(payee.getCode());
						resp.getRef().add(ref);
					});
					//do negative processing here
				}
				logger.info("sending response of deposit.");
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


	public static void main(String[] args) {
		System.out.println(new String(Base64.getDecoder().decode("MDAwfGU4ODQ0YWJjNDc5OTQyMzBhNmFkYWJkMjdiNmZiNWRlfDAxMDAwNDY2Qm9IVVZpbmRscUN0MGR5ZUhCRkUxaWlmeWhxQ1cyVmg0Qmgxa2EzSGNQRUJicUZVaDdMWS9yMU9NNjkxcjNzNQ==")));
	}
}
