package com.fss.aeps.acquirer.matm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.jaxb.DeviceTagNameType;
import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.ReqPay;

@Component
public class Corrector {

	private static final Logger logger = LoggerFactory.getLogger(Corrector.class);

	@Autowired
	private AppConfig appConfig;

	public void correctData(ReqPay request) {
		try {
			request.getPayer().setAddr(appConfig.orgId+"@"+appConfig.participationCode);
			if (request.getPayer().getInfo().getIdentity().getVerifiedName() != null)
				request.getPayer().setName(request.getPayer().getInfo().getIdentity().getVerifiedName());
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void correctAgentId(ReqPay request) {
		final String tid = request.getPayer().getDevice().getTag().stream().filter(d -> d.getName() == DeviceTagNameType.AGENT_ID).findFirst().get().getValue();
		String agentId = request.getPayer().getDevice().getTag().stream().filter(d -> d.getName() == DeviceTagNameType.CARD_ACC_ID_CODE).findFirst().get().getValue();
		request.getPayer().getDevice().getTag().stream().filter(d -> d.getName() == DeviceTagNameType.CARD_ACC_ID_CODE).forEach(d -> {
			d.setValue(appConfig.participationCode + String.format("%12s", tid).replaceAll(" ", "0"));
		});
		request.getPayer().getDevice().getTag().stream().filter(d -> d.getName() == DeviceTagNameType.AGENT_ID).forEach(d -> {
			d.setValue(agentId);
		});
	}

	public void correctAgentId(ReqBalEnq request) {
		final String tid = request.getPayer().getDevice().getTag().stream().filter(d -> d.getName() == DeviceTagNameType.AGENT_ID).findFirst().get().getValue();
		String agentId = request.getPayer().getDevice().getTag().stream().filter(d -> d.getName() == DeviceTagNameType.CARD_ACC_ID_CODE).findFirst().get().getValue();
		request.getPayer().getDevice().getTag().stream().filter(d -> d.getName() == DeviceTagNameType.CARD_ACC_ID_CODE).forEach(d -> {
			d.setValue(appConfig.participationCode + String.format("%12s", tid).replaceAll(" ", "0"));
		});
		request.getPayer().getDevice().getTag().stream().filter(d -> d.getName() == DeviceTagNameType.AGENT_ID).forEach(d -> {
			d.setValue(agentId);
		});
	}
}
