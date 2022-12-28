package com.fss.aeps.acquirer.cbs;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.cbs.fixml.PidData;
import com.sil.fssswitch.model.BalanceRequest;
import com.sil.fssswitch.model.BalanceResponse;
import com.sil.fssswitch.model.MResponse;
import com.sil.fssswitch.model.MiniStatementRequest;
import com.sil.fssswitch.model.MiniStatementResponse;
import com.sil.fssswitch.model.Response;
import com.sil.fssswitch.model.TSPResponse;
import com.sil.fssswitch.model.WithdrawalRequest;
import com.sil.fssswitch.model.WithdrawalResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;


//@formatter:off
@Endpoint
public class CSBService {

	private static final Logger logger = LoggerFactory.getLogger(CSBService.class);
	private static final JAXBContext context = getContext();
	
	@Autowired
	private AppConfig appConfig;
	
	//@formatter:on
	@PayloadRoot(namespace = "http://aeps.Csbemvonline.in/", localPart = "TransactionRequest")
	@ResponsePayload
	public TransactionRequestResponse transactionRequest(@RequestPayload TransactionRequest transactionRequest) throws Exception {
		TransactionRequestResponse requestResponse  = new TransactionRequestResponse();
		MResponse response = new MResponse();
		try {
			
			response.sid="FSS111";
			response.type=1;
			Object object = context.createUnmarshaller().unmarshal(new ByteArrayInputStream(Base64.getDecoder().decode(transactionRequest.requestData)));
			logger.info("@@@@ object :: {}", object.toString());
			if(object instanceof WithdrawalRequest withdrawalRequest) {
				logger.info("[CSBService : transactionRequest] Withdrawal request ::{}",withdrawalRequest);
				final WithdrawalResponse withdrawalResponse = appConfig.context.getBean(WithdrawalTransaction.class).process(withdrawalRequest);
				logger.info("[CSBService : transactionRequest] withdrawal response ::{}",withdrawalResponse);
				StringWriter writer = new StringWriter();
				context.createMarshaller().marshal(withdrawalResponse, writer);
				response.data= Base64.getEncoder().encodeToString(writer.toString().getBytes());
			}else if(object instanceof BalanceRequest balanceRequest) {
				logger.info("[CSBService : transactionRequest] Balance request ::{}",balanceRequest);
				final BalanceResponse balanceResponse = appConfig.context.getBean(BalanceEnquiryTransaction.class).process(balanceRequest);
				logger.info("[CSBService : transactionRequest] Balance response ::{}",balanceResponse);
				StringWriter writer = new StringWriter();
				context.createMarshaller().marshal(balanceResponse, writer);
				response.data= Base64.getEncoder().encodeToString(writer.toString().getBytes());
			}else if(object instanceof MiniStatementRequest miniStatementRequest) {
				logger.info("[CSBService : transactionRequest] Ministatement request ::{}",miniStatementRequest);
				final MiniStatementResponse miniStatementResponse = appConfig.context.getBean(MiniStatementTransaction.class).process(miniStatementRequest);
				logger.info("[CSBService : transactionRequest] Ministatement response ::{}",miniStatementResponse);
				StringWriter writer = new StringWriter();
				context.createMarshaller().marshal(miniStatementResponse, writer);
				response.data= Base64.getEncoder().encodeToString(writer.toString().getBytes());
			}
			
			logger.info("response xml : "+response.data);
			StringWriter writer = new StringWriter();
			context.createMarshaller().marshal(response, writer);
			requestResponse.transactionRequestResult = writer.toString();
			logger.info("Final response xml : "+writer.toString());
			
		} catch (Exception e) {
			logger.error("error in cbs transaction processing.", e);
			throw e;
		}
		return requestResponse;
	}

	private static JAXBContext getContext() {
		
		try {
			return JAXBContext.newInstance(WithdrawalRequest.class, WithdrawalResponse.class, BalanceRequest.class, BalanceResponse.class, MiniStatementRequest.class, MiniStatementResponse.class,MResponse.class, TransactionRequestResponse.class, TSPResponse.class, PidData.class,Response.class, BalanceEnquiryTransaction.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

}
