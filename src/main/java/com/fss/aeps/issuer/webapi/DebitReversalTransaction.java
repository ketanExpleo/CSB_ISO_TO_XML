package com.fss.aeps.issuer.webapi;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fss.aeps.cbsclient.CBSResponse;
import com.fss.aeps.cbsclient.CbsClient;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.constants.ResponseCode;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.Ref;
import com.fss.aeps.jaxb.RefType;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jpa.issuer.IssuerReversal;
import com.fss.aeps.jpa.issuer.IssuerTransaction;
import com.fss.aeps.npciclient.NpciClient;
import com.fss.aeps.services.IssuerReversalService;
import com.fss.aeps.services.IssuerTransactionService;

import reactor.core.publisher.Mono;
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DebitReversalTransaction  extends IIssuerTransaction<ReqPay, RespPay> {

	private static final Logger logger = LoggerFactory.getLogger(DebitTransaction.class);

	@Autowired
	private IssuerReversalService reversalService;

	@Autowired
	private IssuerTransactionService transactionService;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private NpciClient npciWebClient;

	@Autowired
	private CbsClient cbsClient;

	public DebitReversalTransaction(ReqPay request, RespPay response) {
		super(request, response);
	}

	private IssuerReversal reversal = new IssuerReversal();

	@Override
	public final void run() {
		try {
			logger.info("******* executing " + request.getTxn().getId());
			reversal.setReqTime(new Date());
			reversal = reversalService.populateRequestData(reversal, request);
			final PayerType payer = request.getPayer();
			final PayTrans txn = request.getTxn();
			final HeadType head = context.getBean(HeadType.class);


			final RespPay.Resp resp = new RespPay.Resp();
			resp.setReqMsgId(request.getHead().getMsgId());
			resp.setResult(ResultType.FAILURE);

			final Ref ref = new Ref();
			ref.setType(RefType.PAYER);
			ref.setAddr(payer.getAddr());
			ref.setCode(payer.getCode());
			ref.setOrgAmount(payer.getAmount().getValue());
			ref.setSettCurrency(payer.getAmount().getCurr());
			ref.setSeqNum(payer.getSeqNum());
			ref.setSettAmount(new BigDecimal(0));
			resp.getRef().add(ref);

			response.setHead(head);
			response.setTxn(request.getTxn());
			response.setResp(resp);

			final IssuerTransaction original = transactionService.findIssuerTransaction(txn.getOrgTxnId(), txn.getSubType(), txn.getPurpose());
			if(original == null) {
				resp.setErrCode(ResponseCode.REVERSAL_BY_NPCI_NOT_ACKNOWLEDGED_BY_ISSUER);
				ref.setRespCode(resp.getErrCode());
			}
			else {
				reversal.setOrgTxnMsgId(original.getMsgId());
				final Mono<CBSResponse> responseMono = cbsClient.issuerDebitReversal(request, original);
				if(responseMono != null) responseMono.subscribe(cbsResponse -> {
					reversal.setCbsResponseCode(cbsResponse.responseCode);
					reversal.setCbsAuthCode(cbsResponse.authCode);
					reversal.setCbsTranDetails(cbsResponse.tranDetails);
					if("000".equals(cbsResponse.responseCode) || "913".equals(cbsResponse.responseCode)) {
						resp.setResult(ResultType.SUCCESS);
						ref.setBalAmt(cbsResponse.balance);
						ref.setRespCode("00");
						ref.setSettAmount(payer.getAmount().getValue());
						ref.setApprovalNum(cbsResponse.authCode);
					}
					else {
						resp.setErrCode(cbsToNpciResponseMapper.map(cbsResponse.responseCode));
						ref.setRespCode(resp.getErrCode());
					}
				});
			}

			final Mono<Ack> monoAck = npciWebClient.paymentResponse(response);
			reversal.setRespTime(new Date());
			final Ack respAck = monoAck.block();
			response.context.put(ContextKey.RESPONSE_ACK, respAck);
		} catch (Exception e) {
			logger.error("error accured in reversal : " + request.getTxn().getId(), e);
		} finally {
			try {
				reversalService.registerResponse(reversal, response);
			} catch (Exception e) {
				logger.error("error accured when storing reversal in database : ", e);
			}
		}
	}

	public static void main(String[] args) {
		System.out.println(new String(Base64.getDecoder().decode("MDAwfGU4ODQ0YWJjNDc5OTQyMzBhNmFkYWJkMjdiNmZiNWRlfDAxMDAwNDY2Qm9IVVZpbmRscUN0MGR5ZUhCRkUxaWlmeWhxQ1cyVmg0Qmgxa2EzSGNQRUJicUZVaDdMWS9yMU9NNjkxcjNzNQ==")));
	}
}
