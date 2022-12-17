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
import com.fss.aeps.cbsclient.IssuerCbsClient;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayeeType;
import com.fss.aeps.jaxb.Ref;
import com.fss.aeps.jaxb.RefType;
import com.fss.aeps.jaxb.ReqChkTxn;
import com.fss.aeps.jaxb.RespChkTxn;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jpa.issuer.IssuerAdvice;
import com.fss.aeps.jpa.issuer.IssuerTransaction;
import com.fss.aeps.npciclient.NpciClient;
import com.fss.aeps.services.IssuerAdviceService;
import com.fss.aeps.services.IssuerTransactionService;

import reactor.core.publisher.Mono;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DepositAdviceTransaction extends IIssuerTransaction<ReqChkTxn, RespChkTxn> {

	private static final Logger logger = LoggerFactory.getLogger(DepositAdviceTransaction.class);

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private NpciClient npciWebClient;

	@Autowired
	private IssuerTransactionService transactionService;

	@Autowired
	private IssuerAdviceService adviceService;

	@Autowired
	private IssuerCbsClient cbsClient;

	IssuerAdvice advice = new IssuerAdvice();

	public DepositAdviceTransaction(ReqChkTxn request, RespChkTxn response) {
		super(request, response);
	}

	@Override
	public final void run() {
		try {
			logger.info("******* executing " + request.getTxn().getId());
			advice.setReqTime(new Date());
			adviceService.populateRequestData(advice, request);

			final HeadType head = appConfig.getHead();
			final PayeeType payee = request.getPayee();
			final PayTrans txn = request.getTxn();

			final RespChkTxn.Resp resp = new RespChkTxn.Resp();
			resp.setReqMsgId(request.getHead().getMsgId());

			response.setHead(head);
			response.setTxn(txn);
			response.setResp(resp);

			IssuerAdvice oldAdvice = adviceService.findFirstByOrgTxnId(request.getTxn().getOrgTxnId());
			if(oldAdvice == null){
				Thread.sleep(5000);
				oldAdvice = adviceService.findFirstByOrgTxnId(request.getTxn().getOrgTxnId());
			}
			if(oldAdvice != null) throw new RuntimeException("repeat request recieved.");

			logger.info("oldAdvice : "+oldAdvice);
			IssuerTransaction original = transactionService.findDepositTransaction(txn.getOrgTxnId());
			for (int i = 0; i < 2 && original == null; i++) {
				logger.info((i+1)+"st attempt to find deposit transaction for OrgTxnId : "+txn.getOrgTxnId());
				original = transactionService.findDepositTransaction(txn.getOrgTxnId());
				Thread.sleep(1000);
			}
			final Mono<CBSResponse> responseMono = oldAdvice == null ? cbsClient.depositAdvice(request, original) : cbsClient.depositRepeatAdvice(request, original);
			//final Mono<CBSResponse> responseMono = cbsClient.depositAdvice(request, original);
			if(responseMono != null) responseMono.subscribe(cbsResponse -> {
				advice.setCbsResponseCode(cbsResponse.responseCode);
				advice.setCbsAuthCode(cbsResponse.authCode);
				advice.setCbsTranDetails(cbsResponse.tranDetails);
				if(cbsResponse.responseCode.equals("000") || cbsResponse.responseCode.equals("913")) {
					resp.setResult(ResultType.SUCCESS);
					Ref ref = new Ref();
					ref.setType(RefType.PAYEE);
					ref.setSettAmount(payee.getAmount().getValue());
					ref.setSettCurrency(payee.getAmount().getCurr());
					ref.setSeqNum(payee.getSeqNum());
					ref.setAddr(payee.getAddr());
					ref.setCode(payee.getCode());
					ref.setOrgAmount(payee.getAmount().getValue());
					ref.setApprovalNum(cbsResponse.authCode);
					ref.setRespCode("00");
					ref.setRegName(cbsResponse.customerName);
					ref.setAcNum(cbsResponse.operatedAccount);
					resp.getRef().add(ref);
				}
				else {
					resp.setResult(ResultType.FAILURE);
					resp.setErrCode(cbsToNpciResponseMapper.map(cbsResponse.responseCode));
					Ref ref = new Ref();
					ref.setType(RefType.PAYEE);
					ref.setSeqNum(payee.getSeqNum());
					ref.setAddr(payee.getAddr());
					ref.setCode(payee.getCode());
					ref.setOrgAmount(payee.getAmount().getValue());
					ref.setRespCode(resp.getErrCode());
					ref.setSettAmount(new BigDecimal(0));
					ref.setSettCurrency(payee.getAmount().getCurr());
					resp.getRef().add(ref);
					//do negative processing here
				}
				logger.info("sending response of deposit.");
				final Mono<Ack> monoAck = npciWebClient.verificationResponse(response);
				advice.setRespTime(new Date());
				final Ack respAck = monoAck.block();
				response.context.put(ContextKey.RESPONSE_ACK, respAck);
			});
		} catch (Exception e) {
			logger.error("error accured in credit : "+request.getTxn().getId(), e);
		} finally {
			try {
				adviceService.registerResponse(advice, response);
			} catch (Exception e) {
				logger.error("error accured when storing balance enquiry in database : ", e);
			}
		}
	}
}
