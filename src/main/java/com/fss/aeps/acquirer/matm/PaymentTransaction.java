package com.fss.aeps.acquirer.matm;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.AcquirerChannel;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.constants.Purpose;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RestController
public class PaymentTransaction {

	private static final Logger logger = LoggerFactory.getLogger(PaymentTransaction.class);

	@Autowired
	private ApplicationContext context;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	@Qualifier(value = "threadpool")
	private ThreadPoolExecutor executor;

	@PostMapping(path = "/acquirer/ReqPay/{version}/urn:txnid:{txnid}", produces = MediaType.APPLICATION_XML_VALUE)
	public RespPay payment(@RequestBody ReqPay request) {
		request.setHead(appConfig.getHead());
		request.context.put(ContextKey.CHANNEL, AcquirerChannel.MICROATM);

		if(request.extras != null) {
			request.extras.stream().filter(k -> "109".equals(k.getKey())).findFirst().ifPresent(kv ->{
				request.context.put(ContextKey.AGENT_DETAILS, kv.getValue());
			});

			request.extras.stream().filter(k -> "indicator".equals(k.getKey())).findFirst().ifPresent(kv ->{
				request.context.put(ContextKey.RECON_INDICATOR, kv.getValue());
			});
		}
		request.extras = null;

		if(Purpose.CASH_WITHDRAWAL.equals(request.getTxn().getPurpose())) {
			logger.info("processing transaction : " + "CASH_WITHDRAWAL");
			RespPay respPay = appConfig.context.getBean(CWTransaction.class).process(request);
			//if(0 == 0) throw new RuntimeException("manually generated error");
			return respPay;
		} else if(Purpose.FUND_TRANSFER.equals(request.getTxn().getPurpose())) {
			logger.info("transaction type : "+"FUND_TRANSFER");
			request.context.put(ContextKey.RECON_INDICATOR, "AEPS_OFFUS_MATM_REM_FT_PMT");
			return context.getBean(FTTransaction.class).processFundTransfer(request);
		} else {
			throw new RuntimeException("Invalid Transaction at CBS with purpose "+request.getTxn().getPurpose());
		}
	}

}