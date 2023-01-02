package com.fss.aeps.issuer.webapi;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.cbsclient.CBSResponse;
import com.fss.aeps.cbsclient.CbsClient;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.Ack;
import com.fss.aeps.jaxb.CredsType.Cred;
import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.RespBalEnq;
import com.fss.aeps.jaxb.RespBalEnq.Payer;
import com.fss.aeps.jaxb.RespBalEnq.Payer.Bal;
import com.fss.aeps.jaxb.RespBalEnq.Payer.Bal.Data;
import com.fss.aeps.jaxb.RespBalEnq.Payer.MiniStatement;
import com.fss.aeps.jaxb.RespBalEnq.Payer.MiniStatement.StmtData;
import com.fss.aeps.jaxb.RespType;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jpa.issuer.IssuerBalanceEnquiry;
import com.fss.aeps.npciclient.NpciClient;
import com.fss.aeps.services.IssuerBalanceService;
import com.fss.aeps.util.UIDAIAuthCode;

import reactor.core.publisher.Mono;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MSTransaction extends IIssuerTransaction<ReqBalEnq, RespBalEnq> {

	private static final Logger logger = LoggerFactory.getLogger(MSTransaction.class);

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private NpciClient npciWebClient;

	@Autowired
	private IssuerBalanceService balanceService;

	@Autowired
	private CbsClient cbsClient;

	private final IssuerBalanceEnquiry miniStatement = new IssuerBalanceEnquiry();


	public MSTransaction(ReqBalEnq request, RespBalEnq response) {
		super(request, response);
	}

	@Override
	public final void run() {
		try {
			logger.info("******* executing " + request.getTxn().getId());
			miniStatement.setReqTime(new Date());
			response.setHead(appConfig.getHead());
			response.setTxn(request.getTxn());

			final RespType resp = new RespType();
			resp.setReqMsgId(request.getHead().getMsgId());
			response.setResp(resp);

			final Mono<CBSResponse> responseMono = cbsClient.issuerMS(request);
			if(responseMono != null) responseMono.subscribe(cbsResponse -> {
				miniStatement.setCbsRespCode(cbsResponse.responseCode);
				miniStatement.setCbsAuthCode(cbsResponse.authCode);
				miniStatement.setCbsTranDetails(cbsResponse.tranDetails);
				if(cbsResponse.responseCode.equals("00")) {
					final Cred cred = request.getPayer().getCreds().getCred().get(0);
					final UIDAIAuthCode uidaiAuthCode = new UIDAIAuthCode(cred.getData().getValue());
					resp.setAuthCode(uidaiAuthCode.authCode);
					resp.setResult(ResultType.SUCCESS);

					final Payer payer = new Payer();
					payer.setAddr(request.getPayer().getAddr());
					payer.setCode(request.getPayer().getCode());
					payer.setName(request.getPayer().getName());
					payer.setSeqNum(request.getPayer().getSeqNum());
					payer.setType(request.getPayer().getType());
					MiniStatement statement = new MiniStatement();
					statement.setBeneMas("XXX");

					for (int i = 0; i < cbsResponse.statement.size(); i++) {
						StmtData stmtData = new StmtData(Integer.toString(i+1), cbsResponse.statement.get(i));
						statement.getStmtData().add(stmtData);
					}
					payer.setMiniStatement(statement);
					statement.setStmtLineCount(statement.getStmtData().size());
					Bal balance = new Bal();
					balance.setData(new Data(cbsResponse.balance));
					payer.setBal(balance);
					response.setPayer(payer);
				}
				else {
					final Cred cred = request.getPayer().getCreds().getCred().get(0);
					final UIDAIAuthCode uidaiAuthCode = new UIDAIAuthCode(cred.getData().getValue());
					resp.setAuthCode(uidaiAuthCode.authCode);
					resp.setResult(ResultType.FAILURE);
					resp.setErrCode(cbsToNpciResponseMapper.map(cbsResponse.responseCode));
					//do negative processing here
				}
				logger.info("sending response of mini statement.");
				final Mono<Ack> monoResponseAck = npciWebClient.balanceEnquiryResponse(response);
				miniStatement.setRespTime(new Date());
				final Ack responseAck = monoResponseAck.block();
				response.context.put(ContextKey.RESPONSE_ACK, responseAck);
			});

		} catch (Exception e) {
			logger.error("error accured in mini statement transaction : " + request.getTxn().getId(), e);
		} finally {
			try {
				balanceService.registerRequestResponse(miniStatement, request, response);
			} catch (Exception e) {
				logger.error("error accured when storing balance enquiry in database : ", e);
			}
		}
	}
}
