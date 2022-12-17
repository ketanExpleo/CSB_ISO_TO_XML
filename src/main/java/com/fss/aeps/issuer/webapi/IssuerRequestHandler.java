package com.fss.aeps.issuer.webapi;

import static com.fss.aeps.acquirer.AcquirerTransactionMap.advices;
import static com.fss.aeps.acquirer.AcquirerTransactionMap.authentications;
import static com.fss.aeps.acquirer.AcquirerTransactionMap.heartbeats;
import static com.fss.aeps.acquirer.AcquirerTransactionMap.metas;
import static com.fss.aeps.acquirer.AcquirerTransactionMap.payments;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.IAcquirerTransaction;
import com.fss.aeps.broadcast.BroadcastClient;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.constants.Purpose;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.ReqBioAuth;
import com.fss.aeps.jaxb.ReqChkTxn;
import com.fss.aeps.jaxb.ReqHbt;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespBalEnq;
import com.fss.aeps.jaxb.RespBioAuth;
import com.fss.aeps.jaxb.RespChkTxn;
import com.fss.aeps.jaxb.RespHbt;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jaxb.RespType;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jaxb.TxnSubType;
import com.fss.aeps.jaxb.adapter.TranUtil;
import com.fss.aeps.npciclient.NpciClient;

import reactor.core.publisher.Mono;

@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@RestController
@RequestMapping("aeps")
public class IssuerRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(IssuerRequestHandler.class);

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private NpciClient npciWebClient;

	@Autowired
	@Qualifier(value = "threadpool")
	private ThreadPoolExecutor executor;

	@Autowired
	private BroadcastClient broadcaster;

	@PostMapping(path = "/ReqHbt/{version}/urn:txnid:{txnid}", produces = MediaType.APPLICATION_XML_VALUE)
	public Ack heartbeat(@RequestBody ReqHbt request, @PathVariable("version") String version, @PathVariable("txnid") String txnid) {
		final Ack ack = new Ack();
		ack.setReqMsgId(request.getHead().getMsgId());
		ack.setTs(new Date());
		ack.setApi(request.getClass().getSimpleName());
		executor.execute(() -> {
			try {
				final RespHbt response = new RespHbt();
				final RespType resp = new RespType();
				resp.setReqMsgId(request.getHead().getMsgId());
				resp.setResult(ResultType.SUCCESS);

				response.setHead(appConfig.getHead());
				response.setResp(resp);
				response.setTxn(request.getTxn());

				final Mono<Ack> mono = npciWebClient.heartbeatResponse(response);
				mono.subscribe(a -> logger.info("response of beneficiary transaction sent." + a));
			} catch (Exception e) {logger.error(e.getMessage(), e);}
		});
		return ack;
	}

	@PostMapping(path = "/ReqBalEnq/{version}/urn:txnid:{txnid}", produces = MediaType.APPLICATION_XML_VALUE)
	public Ack balanceEnquiry(@RequestBody ReqBalEnq request, @RequestAttribute(ContextKey.HTTP_CONTEXT) Map<String, Object> httpContext) {
		final Ack ack = new Ack();
		ack.setReqMsgId(request.getHead().getMsgId());
		ack.setTs(new Date());
		ack.setApi(request.getClass().getSimpleName());
		request.context.put(ContextKey.FUTURE, httpContext.get(ContextKey.FUTURE));
		request.context.put(ContextKey.REQUEST_ACK, ack);
		if(request.getTxn().getType() == PayConstant.BAL_ENQ) {
			executor.execute(context.getBean(BETransaction.class, request, new RespBalEnq()));
		}
		else if(request.getTxn().getType() == PayConstant.MINI_STMT) {
			executor.execute(context.getBean(MSTransaction.class, request, new RespBalEnq()));
		}
		else throw new RuntimeException("unknown transaction type : "+request.getTxn().getType()+
				" subType : "+request.getTxn().getSubType());
		return ack;
	}

	@PostMapping(path = "/ReqPay/{version}/urn:txnid:{txnid}", produces = MediaType.APPLICATION_XML_VALUE)
	public Ack pay(@RequestBody ReqPay request, @RequestAttribute(ContextKey.HTTP_CONTEXT) Map<String, Object> httpContext) {
		final Ack ack = new Ack();
		ack.setReqMsgId(request.getHead().getMsgId());
		ack.setTs(new Date());
		ack.setApi(request.getClass().getSimpleName());
		request.context.put(ContextKey.FUTURE, httpContext.get(ContextKey.FUTURE));
		request.context.put(ContextKey.REQUEST_ACK, ack);
		if(request.getTxn().getType() == PayConstant.DEBIT && request.getTxn().getSubType() == TxnSubType.PAY) {
			executor.execute(context.getBean(DebitTransaction.class, request, new RespPay()));
		}
		else if(request.getTxn().getType() == PayConstant.CREDIT && request.getTxn().getSubType() == TxnSubType.PAY) {
			if(Purpose.DEPOSIT.equals(request.getTxn().getPurpose())) {
				executor.execute(context.getBean(DepositTransaction.class, request, new RespPay()));
			} else if(Purpose.FUND_TRANSFER.equals(request.getTxn().getPurpose())) {
				executor.execute(context.getBean(CreditTransaction.class, request, new RespPay()));
			} else {
				executor.execute(context.getBean(CreditTransaction.class, request, new RespPay()));
			}
		}
		else if(request.getTxn().getType() == PayConstant.REVERSAL && request.getTxn().getSubType() == TxnSubType.DEBIT) {
			executor.execute(context.getBean(DebitReversalTransaction.class, request, new RespPay()));
		}
		else throw new RuntimeException("unknown transaction type : "+request.getTxn().getType()+
				" subType : "+request.getTxn().getSubType());
		return ack;
	}

	@PostMapping(path = "/ReqChkTxn/{version}/urn:txnid:{txnid}", produces = MediaType.APPLICATION_XML_VALUE)
	public Ack check(@RequestBody ReqChkTxn request, @RequestAttribute(ContextKey.HTTP_CONTEXT) Map<String, Object> httpContext) {
		final Ack ack = new Ack();
		ack.setReqMsgId(request.getHead().getMsgId());
		ack.setTs(new Date());
		ack.setApi("ReqChkTxn");
		request.context.put(ContextKey.FUTURE, httpContext.get(ContextKey.FUTURE));
		request.context.put(ContextKey.REQUEST_ACK, ack);
		if(request.getTxn().getType() == PayConstant.ADVICE && request.getTxn().getSubType() == TxnSubType.PAY) {
			executor.execute(context.getBean(DepositAdviceTransaction.class, request, new RespChkTxn()));
		}
		return ack;
	}

	@PostMapping(path = "/RespHbt/{version}/urn:txnid:{txnid}", produces = MediaType.APPLICATION_XML_VALUE)
	public Ack heartbeatResponse(@RequestBody RespHbt response, @RequestAttribute(ContextKey.HTTP_CONTEXT) Map<String, Object> httpContext) {
		final boolean isBroadCastedResponse = Boolean.parseBoolean(Objects.toString(httpContext.get(ContextKey.BROADCASTED_RESPONSE)).trim());
		logger.info(response.getTxn().getId()+" isBroadCastedResponse : "+isBroadCastedResponse);
		final Ack ack = new Ack();
		ack.setReqMsgId(response.getHead().getMsgId());
		ack.setTs(new Date());
		ack.setApi(response.getClass().getSimpleName());
		final IAcquirerTransaction<ReqHbt, RespHbt> transaction = heartbeats.remove(TranUtil.getTranKey(response.getTxn()));
		if(transaction == null && !isBroadCastedResponse) {
			final byte[] responseBytes = (byte[]) httpContext.get(ContextKey.RAW_RESPONSE_BODY);
			executor.execute(() -> broadcaster.broadcast(response, responseBytes));
		}
		else {
			response.context.put(ContextKey.FUTURE, httpContext.get(ContextKey.FUTURE));
			response.context.put(ContextKey.RESPONSE_ACK, ack);
			executor.execute(() -> transaction.processResponse(response));
		}
		return ack;
	}

	@PostMapping(path = "/RespPay/{version}/urn:txnid:{txnid}", produces = MediaType.APPLICATION_XML_VALUE)
	public Ack pay(@RequestBody RespPay response, @RequestAttribute(ContextKey.HTTP_CONTEXT) Map<String, Object> httpContext) {
		final boolean isBroadCastedResponse = Boolean.parseBoolean(Objects.toString(httpContext.get(ContextKey.BROADCASTED_RESPONSE)).trim());
		logger.info(response.getTxn().getId()+" isBroadCastedResponse : "+isBroadCastedResponse);
		final Ack ack = new Ack();
		ack.setReqMsgId(response.getHead().getMsgId());
		ack.setTs(new Date());
		ack.setApi(response.getClass().getSimpleName());
		final IAcquirerTransaction<ReqPay, RespPay> transaction = payments.remove(TranUtil.getTranKey(response.getTxn()));
		if(transaction == null && !isBroadCastedResponse) {
			final byte[] responseBytes = (byte[]) httpContext.get(ContextKey.RAW_RESPONSE_BODY);
			executor.execute(() -> broadcaster.broadcast(response, responseBytes));
		}
		else {
			response.context.put(ContextKey.FUTURE, httpContext.get(ContextKey.FUTURE));
			response.context.put(ContextKey.RESPONSE_ACK, ack);
			executor.execute(() -> transaction.processResponse(response));
		}
		return ack;
	}

	@PostMapping(path = "/RespChkTxn/{version}/urn:txnid:{txnid}", produces = MediaType.APPLICATION_XML_VALUE)
	public Ack adviceResponse(@RequestBody RespChkTxn response, @RequestAttribute(ContextKey.HTTP_CONTEXT) Map<String, Object> httpContext) {
		final boolean isBroadCastedResponse = Boolean.parseBoolean(Objects.toString(httpContext.get(ContextKey.BROADCASTED_RESPONSE)).trim());
		logger.info(response.getTxn().getId()+" isBroadCastedResponse : "+isBroadCastedResponse);
		final Ack ack = new Ack();
		ack.setReqMsgId(response.getHead().getMsgId());
		ack.setTs(new Date());
		ack.setApi(response.getClass().getSimpleName());
		final IAcquirerTransaction<ReqChkTxn, RespChkTxn> transaction = advices.remove(TranUtil.getTranKey(response.getTxn()));
		if(transaction == null && !isBroadCastedResponse) {
			final byte[] responseBytes = (byte[]) httpContext.get(ContextKey.RAW_RESPONSE_BODY);
			executor.execute(() -> broadcaster.broadcast(response, responseBytes));
		}
		else {
			response.context.put(ContextKey.FUTURE, httpContext.get(ContextKey.FUTURE));
			response.context.put(ContextKey.RESPONSE_ACK, ack);
			executor.execute(() -> transaction.processResponse(response));
		}
		return ack;
	}

	@PostMapping(path = "/RespBalEnq/{version}/urn:txnid:{txnid}", produces = MediaType.APPLICATION_XML_VALUE)
	public Ack balanceResponse(@RequestBody RespBalEnq response, @RequestAttribute(ContextKey.HTTP_CONTEXT) Map<String, Object> httpContext) {
		final boolean isBroadCastedResponse = Boolean.parseBoolean(Objects.toString(httpContext.get(ContextKey.BROADCASTED_RESPONSE)).trim());
		logger.info(response.getTxn().getId()+" isBroadCastedResponse : "+isBroadCastedResponse);
		final Ack ack = new Ack();
		ack.setReqMsgId(response.getHead().getMsgId());
		ack.setTs(new Date());
		ack.setApi(response.getClass().getSimpleName());
		final IAcquirerTransaction<ReqBalEnq, RespBalEnq> transaction = metas.remove(TranUtil.getTranKey(response.getTxn()));
		if(transaction == null && !isBroadCastedResponse) {
			final byte[] responseBytes = (byte[]) httpContext.get(ContextKey.RAW_RESPONSE_BODY);
			executor.execute(() -> broadcaster.broadcast(response, responseBytes));
		}
		else {
			response.context.put(ContextKey.FUTURE, httpContext.get(ContextKey.FUTURE));
			response.context.put(ContextKey.RESPONSE_ACK, ack);
			executor.execute(() -> transaction.processResponse(response));
		}
		return ack;
	}

	@PostMapping(path = "/RespBioAuth/{version}/urn:txnid:{txnid}", produces = MediaType.APPLICATION_XML_VALUE)
	public Ack bioAuthResponse(@RequestBody RespBioAuth response, @RequestAttribute(ContextKey.HTTP_CONTEXT) Map<String, Object> httpContext) {
		final boolean isBroadCastedResponse = Boolean.parseBoolean(Objects.toString(httpContext.get(ContextKey.BROADCASTED_RESPONSE)).trim());
		logger.info(response.getTxn().getId()+" isBroadCastedResponse : "+isBroadCastedResponse);
		final Ack ack = new Ack();
		ack.setReqMsgId(response.getHead().getMsgId());
		ack.setTs(new Date());
		ack.setApi(response.getClass().getSimpleName());
		final IAcquirerTransaction<ReqBioAuth, RespBioAuth> transaction = authentications.remove(TranUtil.getTranKey(response.getTxn()));
		if(transaction == null && !isBroadCastedResponse) {
			final byte[] responseBytes = (byte[]) httpContext.get(ContextKey.RAW_RESPONSE_BODY);
			executor.execute(() -> broadcaster.broadcast(response, responseBytes));
		}
		else {
			response.context.put(ContextKey.FUTURE, httpContext.get(ContextKey.FUTURE));
			response.context.put(ContextKey.RESPONSE_ACK, ack);
			executor.execute(() -> transaction.processResponse(response));
		}
		return ack;
	}

}
