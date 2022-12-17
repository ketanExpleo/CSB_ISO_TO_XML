package com.fss.aeps.acquirer.core;

import static com.fss.aeps.acquirer.AcquirerTransactionMap.heartbeats;

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
import com.fss.aeps.acquirer.IAcquirerTransaction;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.constants.ResponseCode;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.ReqHbt;
import com.fss.aeps.jaxb.RespHbt;
import com.fss.aeps.jaxb.RespType;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jaxb.adapter.TranUtil;
import com.fss.aeps.npciclient.NpciClient;

import reactor.core.publisher.Mono;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReqHbtSender implements IAcquirerTransaction<ReqHbt, RespHbt>{

	@Autowired
	private NpciClient npciWebClient;

	@Autowired
	private AppConfig appConfig;

	private CompletableFuture<RespHbt> future = new CompletableFuture<>();

	public RespHbt send(ReqHbt request) {
		final Mono<RespHbt> timeoutMono = Mono.fromSupplier(() -> TSUPPLIER.apply(request));
		final Mono<RespHbt> mono = Mono.fromFuture(future)
				.timeout(Duration.ofMillis(appConfig.npciReadTimeout), timeoutMono);
		heartbeats.put(TranUtil.getTranKey(request.getTxn()), this);
		final Mono<Ack> monoAck = npciWebClient.heartbeat(request);
		final Ack ack = monoAck.block();
		request.context.put(ContextKey.REQUEST_ACK, ack);
		if(ack.getErrorMessages().size() > 0) {
			heartbeats.remove(request.getHead().getMsgId());
			return ESUPPLIER.apply(request, ack);
		}
		return mono.block();
	}

	@Override
	public void processResponse(RespHbt response) {
		future.complete(response);
		logger.info("RespHbt received.");
	}

	private static final BiFunction<ReqHbt, Ack, RespHbt> ESUPPLIER = (reqPay, ack) -> {
		final String errMessage = ack.getErrorMessages().stream()
				.map(a -> a.getErrorCd()+"|"+a.getErrorDtl()).collect(Collectors.joining("||"));
		logger.info("error received in ack : "+errMessage);
		final RespHbt respPay = new RespHbt();
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

	private static final Function<ReqHbt, RespHbt> TSUPPLIER = (reqPay) -> {
		logger.info("timedout!. responding with "+ResponseCode.TIMEOUT);
		final RespHbt respPay = new RespHbt();
		respPay.setHead(reqPay.getHead());
		respPay.setTxn(reqPay.getTxn());
		final RespType resp = new RespType();
		resp.setResult(ResultType.FAILURE);
		resp.setErrCode(ResponseCode.TIMEOUT);
		respPay.setResp(resp);
		respPay.context.put(ContextKey.IS_STATIC_RESPONSE, Boolean.TRUE);
		return respPay;
	};

}
