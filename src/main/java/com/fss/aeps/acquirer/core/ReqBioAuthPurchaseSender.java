package com.fss.aeps.acquirer.core;

import static com.fss.aeps.acquirer.AcquirerTransactionMap.authentications;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.AcquirerChannel;
import com.fss.aeps.acquirer.IAcquirerTransaction;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.constants.ResponseCode;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.ReqBioAuth;
import com.fss.aeps.jaxb.RespBioAuth;
import com.fss.aeps.jaxb.RespType;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jaxb.adapter.TranUtil;
import com.fss.aeps.jpa.acquirer.AcquirerBioAuthPurchase;
import com.fss.aeps.npciclient.NpciClient;
import com.fss.aeps.services.AcquirerBioAuthPurchaseService;

import reactor.core.publisher.Mono;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReqBioAuthPurchaseSender implements IAcquirerTransaction<ReqBioAuth, RespBioAuth>{

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private NpciClient npciWebClient;

	@Autowired
	private AcquirerBioAuthPurchaseService bioAuthService;

	//private final AcquirerBioAuthPurchase transaction = new AcquirerBioAuthPurchase();
	private final CompletableFuture<RespBioAuth> future = new CompletableFuture<>();

	public RespBioAuth send(ReqBioAuth request) {
		AcquirerChannel channel = (AcquirerChannel) request.context.get(ContextKey.CHANNEL);
		final AcquirerBioAuthPurchase transaction = (AcquirerBioAuthPurchase) request.context.get(ContextKey.ACQUIRER_TRANSACTION);
		if(channel == null) throw new RuntimeException("acquirer channel not specified.");
		RespBioAuth response = null;
		try {
			request.setHead(appConfig.getHead());
			bioAuthService.registerTransactionRequest(transaction, request);
			final Mono<RespBioAuth> timeoutMono = Mono.fromSupplier(() -> TSUPPLIER.apply(request));
			final Mono<RespBioAuth> mono = Mono.fromFuture(future)
					.timeout(Duration.ofMillis(appConfig.npciReadTimeout), timeoutMono)
					.doOnSuccess((r) -> future.complete(r));
			authentications.put(TranUtil.getTranKey(request.getTxn()), this);
			final Mono<Ack> monoAck = npciWebClient.bioAuth(request);
			final Ack ack = monoAck.block();
			request.context.put(ContextKey.REQUEST_ACK, ack);
			if(ack.getErrorMessages().size() > 0) {
				return (response = ESUPPLIER.apply(request, ack));
			}
			return (response = mono.block());
		} finally {
			try {
				authentications.remove(request.getHead().getMsgId());
				bioAuthService.registerTransactionResponse(transaction, response);
				adjustResponseCode(request, response);
			} catch (Exception e) {
				logger.error("error accured when storing balance enquiry in database : ", e);
			}
		}
	}

	@Override
	public void processResponse(RespBioAuth response) {
		boolean isDone = future.isDone();
		if(isDone) {
			logger.info("acquirer timedout before response received.");
		}
		else {
			future.complete(response);
			logger.info("response received by acquirer.");
		}
	}

	private final void adjustResponseCode(ReqBioAuth request, RespBioAuth response) {
		if(response.getResp().getResult() != ResultType.SUCCESS) {
			logger.info("response.getResp().getErrCode() : '"+response.getResp().getErrCode()+"'");
			if("UIDAI_ERROR".equalsIgnoreCase(response.getResp().getErrCode())) {
				response.getResp().setErrCode(response.getUidaiData().getErr());
			}
		}
	}

	public static final Function<ReqBioAuth, RespBioAuth> TSUPPLIER = (reqPay) -> {
		logger.info("timedout!. responding with "+ResponseCode.TIMEOUT);
		final RespBioAuth respPay = new RespBioAuth();
		respPay.setHead(reqPay.getHead());
		respPay.setTxn(reqPay.getTxn());
		final RespType resp = new RespType();
		resp.setResult(ResultType.FAILURE);
		resp.setErrCode(ResponseCode.TIMEOUT);
		respPay.setResp(resp);
		respPay.context.put(ContextKey.IS_STATIC_RESPONSE, Boolean.TRUE);
		return respPay;
	};

	private static final BiFunction<ReqBioAuth, Ack, RespBioAuth> ESUPPLIER = (reqPay, ack) -> {
		final String errMessage = ack.getErrorMessages().stream()
				.map(a -> a.getErrorCd()+"|"+a.getErrorDtl()).collect(Collectors.joining("||"));
		logger.info("error received in ack : "+errMessage);
		final RespBioAuth respPay = new RespBioAuth();
		respPay.setHead(reqPay.getHead());
		respPay.setTxn(reqPay.getTxn());
		final RespType resp = new RespType();
		resp.setResult(ResultType.FAILURE);
		resp.setErrCode(ResponseCode.INVALID_INPUT);
		resp.setErrDesc(errMessage);
		respPay.setResp(resp);
		respPay.context.put(ContextKey.IS_STATIC_RESPONSE, Boolean.TRUE);
		return respPay;
	};

}
