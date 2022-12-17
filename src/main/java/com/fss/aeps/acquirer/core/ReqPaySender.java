package com.fss.aeps.acquirer.core;

import static com.fss.aeps.acquirer.AcquirerTransactionMap.payments;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.IAcquirerTransaction;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.constants.Purpose;
import com.fss.aeps.constants.ResponseCode;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.Ref;
import com.fss.aeps.jaxb.RefType;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jaxb.RespPay.Resp;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jaxb.adapter.TranUtil;
import com.fss.aeps.jpa.acquirer.AcquirerTransaction;
import com.fss.aeps.npciclient.NpciClient;
import com.fss.aeps.services.AcquirerTransactionService;
import com.fss.aeps.test.PatchWork;
import com.fss.aeps.util.BiometricUtil;

import reactor.core.publisher.Mono;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReqPaySender implements IAcquirerTransaction<ReqPay, RespPay>{

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private AcquirerTransactionService transactionService;

	@Autowired
	private NpciClient npciWebClient;

	//private final AcquirerTransaction transaction =  new AcquirerTransaction();
	private final CompletableFuture<RespPay> future = new CompletableFuture<>();

	public RespPay send(ReqPay request) {
		final AcquirerTransaction transaction = (AcquirerTransaction) request.context.get(ContextKey.ACQUIRER_TRANSACTION);
		BiometricUtil.setAUADetails(request, appConfig);
		if(Purpose.FUND_TRANSFER.equals(request.getTxn().getPurpose())) {
			PatchWork.patchFTReqPay(request, appConfig);
		}
		logger.info("[ReqPaySender : send] :: {}", request);
		transactionService.registerTransactionRequest(transaction, request);
		RespPay response = null;
		try {
			final Mono<RespPay> timeoutMono = Mono.fromSupplier(() -> RESPPAY_SUPPLIER.apply(request, "91"));
			final Mono<RespPay> mono = Mono.fromFuture(future)
					.timeout(Duration.ofMillis(appConfig.npciReadTimeout), timeoutMono)
					.doOnSuccess((r) -> future.complete(r));
			payments.put(TranUtil.getTranKey(request.getTxn()), this);
			final Mono<Ack> monoAck = npciWebClient.payment(request);
			logger.info("acquirer request forwarded to npci.");
			final Ack ack = monoAck.block();
			request.context.put(ContextKey.REQUEST_ACK, ack);
			logger.info("acquirer request ack received." + ack);
			if(ack.getErrorMessages().size() > 0) {
				payments.remove(TranUtil.getTranKey(request.getTxn()));
				return (response = RESPPAY_ACK_SUPPLIER.apply(request, ack));
			}
			response = mono.block();
			return response;
		} finally {
			try {
				payments.remove(request.getHead().getMsgId());
				transactionService.registerTransactionResponse(transaction, request, response);
				adjustResponseCode(request, response);
			} catch (Exception e) {
				logger.error("error accured when storing payment transaction in database : ", e);
			}
		}
	}

	private final void adjustResponseCode(ReqPay request, RespPay response) {
		if(response.getResp().getResult() != ResultType.SUCCESS) {
			logger.info("response.getResp().getErrCode() : '"+response.getResp().getErrCode()+"'");
			logger.info("response.getResp().getUidaiError() : '"+response.getResp().getUidaiError()+"'");
			if("UIDAI_ERROR".equalsIgnoreCase(response.getResp().getErrCode())) {
				logger.info("changing response code in case of UIDAI error.");
				response.getResp().setErrCode(response.getResp().getUidaiError());
			}
			else {
				logger.info("changing response code to ref response code.");
				final String purpose = request.getTxn().getPurpose();
				final RefType refType = List.of(Purpose.CASH_WITHDRAWAL , Purpose.PURCHASE).contains(purpose) ? RefType.PAYER : RefType.PAYEE;
				final Ref ref = response.getResp().getRef().stream()
						.filter(f -> f.getType() == refType).findFirst().orElseGet(() -> null);
				if(ref != null && ref.getRespCode() != null) {
					response.getResp().setErrCode(ref.getRespCode());
				}
			}
		}
	}

	@Override
	public void processResponse(RespPay response) {
		boolean isDone = future.isDone();
		if(isDone) {
			logger.info("acquirer timedout before response received.");
		}
		else {
			future.complete(response);
			logger.info("response received by acquirer.");
		}
	}

	public static final BiFunction<ReqPay, String, RespPay> RESPPAY_SUPPLIER = (reqPay, errCode) -> {
		logger.info("timedout!. responding with : "+errCode);
		final RespPay respPay = new RespPay();
		respPay.setHead(reqPay.getHead());
		respPay.setTxn(reqPay.getTxn());
		final Resp resp = new Resp();
		resp.setResult(ResultType.FAILURE);
		resp.setErrCode(errCode);
		respPay.setResp(resp);
		respPay.context.put(ContextKey.IS_STATIC_RESPONSE, Boolean.TRUE);
		return respPay;
	};

	public static final BiFunction<ReqPay, Ack, RespPay> RESPPAY_ACK_SUPPLIER = (reqPay, ack) -> {
		final String errMessage = ack.getErrorMessages().stream()
				.map(a -> a.getErrorCd()+"|"+a.getErrorDtl()).collect(Collectors.joining("||"));
		logger.info("error received in ack : "+errMessage);
		final RespPay respPay = new RespPay();
		respPay.setHead(reqPay.getHead());
		respPay.setTxn(reqPay.getTxn());
		final Resp resp = new Resp();
		resp.setResult(ResultType.FAILURE);
		resp.setErrCode(ResponseCode.INVALID_INPUT);
		resp.setErrDesc(errMessage);
		respPay.setResp(resp);
		respPay.context.put(ContextKey.IS_STATIC_RESPONSE, Boolean.TRUE);
		return respPay;
	};
}
