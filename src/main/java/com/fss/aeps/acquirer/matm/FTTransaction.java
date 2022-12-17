package com.fss.aeps.acquirer.matm;

import java.util.Base64;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.AcquirerChannel;
import com.fss.aeps.acquirer.core.ReqBioAuthSender;
import com.fss.aeps.acquirer.core.ReqPaySender;
import com.fss.aeps.cbsclient.AcquirerCbsClient;
import com.fss.aeps.cbsclient.CBSResponse;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.AccountDetailType;
import com.fss.aeps.jaxb.CredSubType;
import com.fss.aeps.jaxb.CredType;
import com.fss.aeps.jaxb.CredsType;
import com.fss.aeps.jaxb.CredsType.Cred;
import com.fss.aeps.jaxb.CredsType.Cred.Data;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.ReqBioAuth;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespBioAuth;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jaxb.RespPay.Resp;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jpa.acquirer.AcquirerTransaction;
import com.fss.aeps.test.PatchWork;
import com.fss.aeps.util.Mapper;
import com.fss.aeps.util.ThreadManagement;

import reactor.core.publisher.Mono;

@Component
public class FTTransaction {

	private static final Logger logger = LoggerFactory.getLogger(FTTransaction.class);

	@Autowired
	private AppConfig appConfig;

	@Autowired
	@Qualifier(value = "threadpool")
	private ThreadPoolExecutor executor;

	@Autowired
	private AcquirerCbsClient cbsClient;

	@Autowired
	@Qualifier("cbsToNpciResponseMapper")
	private Mapper finacleToNpciResponseMapper;

	@Autowired
	private Corrector corrector;

	public RespPay processFundTransfer(ReqPay request) {
		corrector.correctAgentId(request);
		final RespBioAuth respBioAuth = sendAuthentication(request, appConfig);
		String waitResponse = ThreadManagement.waitOtherForCompletion(respBioAuth);
		logger.info("waiting for thread completion : " + waitResponse);
		logger.info("bio auth response received.");

		if (respBioAuth.getResp().getResult() == ResultType.SUCCESS) {
			final Mono<CBSResponse> cbsResponseMono = cbsClient.debitFT(request, respBioAuth);
			final CBSResponse cbsResponse = cbsResponseMono != null ? cbsResponseMono.block() : null;
			if (cbsResponse != null && "000".equals(cbsResponse.responseCode)) {
				CredsType creds = new CredsType();
				Cred cred = new Cred();
				cred.setSubType(CredSubType.NA);
				cred.setType(CredType.PRE_APPROVED);
				cred.setData(new Data(Base64.getEncoder().encodeToString(("00|approved").getBytes())));
				creds.getCred().add(cred);
				request.getPayer().setCreds(creds);
				logger.info("sending fund transfer.");
				final ReqPay fundTransfer = new ReqPay();
				fundTransfer.setHead(appConfig.getHead());
				fundTransfer.setTxn(request.getTxn());
				fundTransfer.setPayer(request.getPayer());
				fundTransfer.setPayees(request.getPayees());
				fundTransfer.context.put(ContextKey.CHANNEL, AcquirerChannel.MICROATM);
				final AcquirerTransaction transaction = new AcquirerTransaction();
				fundTransfer.context.put(ContextKey.ACQUIRER_TRANSACTION, transaction);
				final RespPay respPay = appConfig.context.getBean(ReqPaySender.class).send(fundTransfer);
				if(respPay.getResp().getResult() == ResultType.FAILURE && "!91".equals(respPay.getResp().getErrCode())) {
					cbsClient.debitFTReversal(request, respBioAuth);
				}
				return respPay;
			} else if(cbsResponse != null) {
				if("911".equalsIgnoreCase(cbsResponse.responseCode)) cbsClient.debitFTReversal(request, respBioAuth);
				final RespPay response = RESPONSE_SUPPLIER.apply(request, finacleToNpciResponseMapper.map(cbsResponse.responseCode));
				return response;
			} else {
				cbsClient.debitFTReversal(request, respBioAuth);
				final RespPay response = RESPONSE_SUPPLIER.apply(request, "91");
				return response;
			}

		} else {
			final RespPay response = RESPONSE_SUPPLIER.apply(request, "91");
			response.getResp().setErrCode(respBioAuth.getResp().getErrCode());
			return response;
		}
	}

	public final RespBioAuth sendAuthentication(ReqPay request, AppConfig appConfig) {
		final ReqBioAuth reqBioAuth = new ReqBioAuth();
		reqBioAuth.setHead(appConfig.getHead());
		reqBioAuth.setTxn(request.getTxn().clone());
		reqBioAuth.getTxn().setType(PayConstant.BIO_AUTH);
		reqBioAuth.getTxn().setSubType(null);
		reqBioAuth.setPayer(request.getPayer());
		String payeeIin = request.getPayees().getPayee().get(0).getAc().getDetail().stream()
				.filter(f -> f.getName() == AccountDetailType.IIN).findFirst().map(d -> d.getValue()).get();
		PatchWork.patchFTBioAuth(payeeIin, reqBioAuth, appConfig);
		reqBioAuth.context.put(ContextKey.CHANNEL, AcquirerChannel.MICROATM);
		final RespBioAuth respBioAuth = appConfig.context.getBean(ReqBioAuthSender.class).send(reqBioAuth);
		return respBioAuth;
	}

	public static final BiFunction<ReqPay, String, RespPay> RESPONSE_SUPPLIER = (reqPay, responceCode) -> {
		final RespPay respPay = new RespPay();
		respPay.setHead(reqPay.getHead());
		respPay.setTxn(reqPay.getTxn());
		final Resp resp = new Resp();
		resp.setResult(ResultType.FAILURE);
		resp.setErrCode(responceCode);
		respPay.setResp(resp);
		respPay.context.put(ContextKey.IS_STATIC_RESPONSE, Boolean.TRUE);
		return respPay;
	};
}
