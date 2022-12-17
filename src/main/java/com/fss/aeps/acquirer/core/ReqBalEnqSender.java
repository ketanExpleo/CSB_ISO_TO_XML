package com.fss.aeps.acquirer.core;

import static com.fss.aeps.acquirer.AcquirerTransactionMap.metas;

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
import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.RespBalEnq;
import com.fss.aeps.jaxb.RespBalEnq.Payer;
import com.fss.aeps.jaxb.RespType;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jaxb.adapter.TranUtil;
import com.fss.aeps.jpa.acquirer.AcquirerBalanceEnquiry;
import com.fss.aeps.npciclient.NpciClient;
import com.fss.aeps.services.AcquirerBalanceService;

import reactor.core.publisher.Mono;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReqBalEnqSender implements IAcquirerTransaction<ReqBalEnq, RespBalEnq>{

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private NpciClient npciWebClient;

	@Autowired
	private AcquirerBalanceService balanceService;

	private final AcquirerBalanceEnquiry transaction = new AcquirerBalanceEnquiry();
	private final CompletableFuture<RespBalEnq> future = new CompletableFuture<>();

	public RespBalEnq send(ReqBalEnq request) {
		logger.info("@@@ Inside [ReqBalEnqSender : send]");
		AcquirerChannel channel = (AcquirerChannel) request.context.get(ContextKey.CHANNEL);
		if(channel == null) throw new RuntimeException("acquirer channel not specified.");
		RespBalEnq response = null;
		try {
			request.setHead(appConfig.getHead());
			balanceService.populateRequestData(transaction, request);
			final Mono<RespBalEnq> timeoutMono = Mono.fromSupplier(() -> TSUPPLIER.apply(request));
			final Mono<RespBalEnq> mono = Mono.fromFuture(future)
					.timeout(Duration.ofMillis(appConfig.npciReadTimeout), timeoutMono)
					.doOnSuccess((r) -> future.complete(r));
			metas.put(TranUtil.getTranKey(request.getTxn()), this);
			final Mono<Ack> monoAck = npciWebClient.balanceEnquiry(request);
			final Ack ack = monoAck.block();
			request.context.put(ContextKey.REQUEST_ACK, ack);
			if(ack.getErrorMessages().size() > 0) {
				return (response = ESUPPLIER.apply(request, ack));
			}
			return (response = mono.block());
		} finally {
			try {
				metas.remove(request.getHead().getMsgId());
				balanceService.registerTransactionResponse(transaction, response);
			} catch (Exception e) {
				logger.error("error accured when storing balance enquiry in database : ", e);
			}
		}
	}

	@Override
	public void processResponse(RespBalEnq response) {
		boolean isDone = future.isDone();
		if(isDone) {
			logger.info("acquirer timedout before response delivered : "+response.getTxn().getId());
		}
		else {
			future.complete(response);
			logger.info("response received by acquirer.");
		}
	}

	public static final Function<ReqBalEnq, RespBalEnq> TSUPPLIER = (reqPay) -> {
		logger.info("timedout!. responding with "+ResponseCode.TIMEOUT);
		final RespBalEnq respPay = new RespBalEnq();
		respPay.setHead(reqPay.getHead());
		respPay.setTxn(reqPay.getTxn());
		final Payer payer = new Payer();
		payer.setAddr(reqPay.getPayer().getAddr());
		payer.setCode(reqPay.getPayer().getCode());
		payer.setName(reqPay.getPayer().getName());
		payer.setSeqNum(reqPay.getPayer().getSeqNum());
		payer.setType(reqPay.getPayer().getType());
		respPay.setPayer(payer);
		final RespType resp = new RespType();
		resp.setResult(ResultType.FAILURE);
		resp.setErrCode(ResponseCode.TIMEOUT);
		respPay.setResp(resp);
		respPay.context.put(ContextKey.IS_STATIC_RESPONSE, Boolean.TRUE);
		return respPay;
	};

	private static final BiFunction<ReqBalEnq, Ack, RespBalEnq> ESUPPLIER = (reqPay, ack) -> {
		final String errMessage = ack.getErrorMessages().stream()
				.map(a -> a.getErrorCd()+"|"+a.getErrorDtl()).collect(Collectors.joining("||"));
		logger.info("error received in ack : "+errMessage);
		final RespBalEnq respPay = new RespBalEnq();
		respPay.setHead(reqPay.getHead());
		respPay.setTxn(reqPay.getTxn());
		final RespType resp = new RespType();
		resp.setResult(ResultType.FAILURE);
		final Payer payer = new Payer();
		payer.setAddr(reqPay.getPayer().getAddr());
		payer.setCode(reqPay.getPayer().getCode());
		payer.setName(reqPay.getPayer().getName());
		payer.setSeqNum(reqPay.getPayer().getSeqNum());
		payer.setType(reqPay.getPayer().getType());
		respPay.setPayer(payer);
		resp.setErrCode(ResponseCode.INVALID_INPUT);
		resp.setErrDesc(errMessage);
		respPay.setResp(resp);
		respPay.context.put(ContextKey.IS_STATIC_RESPONSE, Boolean.TRUE);
		return respPay;
	};

}
