package com.fss.aeps.acquirer.core;

import static com.fss.aeps.acquirer.AcquirerTransactionMap.advices;

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
import com.fss.aeps.jaxb.ReqChkTxn;
import com.fss.aeps.jaxb.RespChkTxn;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jaxb.adapter.TranUtil;
import com.fss.aeps.jpa.acquirer.AcquirerAdvice;
import com.fss.aeps.npciclient.NpciClient;
import com.fss.aeps.services.AcquirerAdviceService;

import reactor.core.publisher.Mono;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReqChkTxnSender implements IAcquirerTransaction<ReqChkTxn, RespChkTxn>{

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private NpciClient npciWebClient;

	@Autowired
	private AcquirerAdviceService adviceService;


	private final AcquirerAdvice advice =  new AcquirerAdvice();
	private final CompletableFuture<RespChkTxn> future = new CompletableFuture<>();

	public RespChkTxn send(ReqChkTxn request) {
		AcquirerChannel channel = (AcquirerChannel) request.context.get(ContextKey.CHANNEL);
		if(channel == null) throw new RuntimeException("acquirer channel not specified.");
		RespChkTxn response = null;
		try {
			request.setHead(appConfig.getHead());
			adviceService.populateRequestData(advice, request);
			final Mono<RespChkTxn> timeoutMono = Mono.fromSupplier(() -> TSUPPLIER.apply(request));
			final Mono<RespChkTxn> mono = Mono.fromFuture(future)
					.timeout(Duration.ofMillis(appConfig.npciReadTimeout), timeoutMono)
					.doOnSuccess((r) -> future.complete(r));
			advices.put(TranUtil.getTranKey(request.getTxn()), this);
			final Mono<Ack> monoAck = npciWebClient.advice(request);
			final Ack ack = monoAck.block();
			request.context.put(ContextKey.REQUEST_ACK, ack);
			if(ack.getErrorMessages().size() > 0) {
				return (response = ESUPPLIER.apply(request, ack));
			}
			return (response = mono.block());
		} finally {
			try {
				advices.remove(request.getHead().getMsgId());
				adviceService.registerTransactionResponse(advice, response);
			} catch (Exception e) {
				logger.error("error accured when storing balance enquiry in database : ", e);
			}
		}
	}

	@Override
	public void processResponse(RespChkTxn response) {
		boolean isDone = future.isDone();
		if(isDone) {
			logger.info("acquirer timedout before response received.");
		}
		else {
			future.complete(response);
			logger.info("response received by acquirer.");
		}
	}

	public static final Function<ReqChkTxn, RespChkTxn> TSUPPLIER = (reqPay) -> {
		logger.info("timedout!. responding with "+ResponseCode.TIMEOUT);
		final RespChkTxn respPay = new RespChkTxn();
		respPay.setHead(reqPay.getHead());
		respPay.setTxn(reqPay.getTxn());
		final RespChkTxn.Resp resp = new RespChkTxn.Resp();
		resp.setResult(ResultType.FAILURE);
		resp.setErrCode("91");
		respPay.setResp(resp);
		respPay.context.put(ContextKey.IS_STATIC_RESPONSE, Boolean.TRUE);
		return respPay;
	};

	private static final BiFunction<ReqChkTxn, Ack, RespChkTxn> ESUPPLIER = (reqPay, ack) -> {
		final String errMessage = ack.getErrorMessages().stream()
				.map(a -> a.getErrorCd()+"|"+a.getErrorDtl()).collect(Collectors.joining("||"));
		logger.info("error received in ack : "+errMessage);
		final RespChkTxn respPay = new RespChkTxn();
		respPay.setHead(reqPay.getHead());
		respPay.setTxn(reqPay.getTxn());
		final RespChkTxn.Resp resp = new RespChkTxn.Resp();
		resp.setResult(ResultType.FAILURE);
		resp.setErrCode(ResponseCode.INVALID_INPUT);
		resp.setErrDesc(errMessage);
		respPay.setResp(resp);
		respPay.context.put(ContextKey.IS_STATIC_RESPONSE, Boolean.TRUE);
		return respPay;
	};

}
