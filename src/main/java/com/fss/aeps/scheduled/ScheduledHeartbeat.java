package com.fss.aeps.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.core.ReqHbtSender;
import com.fss.aeps.jaxb.HbtMsgType;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.ReqHbt;
import com.fss.aeps.jaxb.ReqHbt.HbtMsg;
import com.fss.aeps.jaxb.RespHbt;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.npciclient.NpciWebClient;
import com.fss.aeps.util.Generator;

@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "heartbeat.enabled", havingValue = "true")
public class ScheduledHeartbeat {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledHeartbeat.class);

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private NpciWebClient client;

	@Scheduled(initialDelayString = "${hearbeat.initialDelay}", fixedDelayString = "${hearbeat.fixedDelay}")
	public void heartbeat() {
		try {
			final String refUrl = "http://npci.org/upi/schema/";

			final ReqHbt reqHbt = new ReqHbt();
			final HeadType headType = appConfig.getHead();

			final PayTrans payTrans = new PayTrans();
			payTrans.setId(Generator.newRandomTxnId(appConfig.participationCode));
			payTrans.setNote("Heart beat");
			payTrans.setRefUrl(refUrl);
			payTrans.setType(PayConstant.HBT);
			payTrans.setTs(headType.getTs());

			final HbtMsg hbtMsg = new HbtMsg();
			hbtMsg.setType(HbtMsgType.ALIVE);
			hbtMsg.setValue("NA");

			reqHbt.setHead(headType);
			reqHbt.setTxn(payTrans);
			reqHbt.setHbtMsg(hbtMsg);

			final RespHbt respHbt = appConfig.context.getBean(ReqHbtSender.class).send(reqHbt);

			if(respHbt.getResp().getResult() == ResultType.FAILURE) {
				logger.info("heartbeat failed. changing npci client ip.");
				client.changeClientToNextIp();
			}
		} catch (Exception e) {logger.error("error accured on heartbeat", e);}
	}


}
