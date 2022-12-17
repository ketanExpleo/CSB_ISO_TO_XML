package com.fss.aeps;

import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fss.aeps.acquirer.merchant.MerchantTCPServer;
import com.fss.aeps.jpa.issuer.CbsToNpciResponseCodes;
import com.fss.aeps.repository.CbsToNpciResponseCodesRepository;

@RestController
public class ConfigController {

	private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private CbsToNpciResponseCodesRepository cbsToNpciResponseCodesRepository;

	@GetMapping(path = "/config/refreshCbsToNpciResponseCodes")
	public boolean refreshCbsToNpciResponseCodes() {
		try {
			Map<String, String> map = cbsToNpciResponseCodesRepository.findAll().stream().collect(Collectors.toMap(CbsToNpciResponseCodes::getCbsCode, CbsToNpciResponseCodes::getNpciCode));
			return appConfig.getCbsToNpciResponseMapper().reInitialize(map);
		} catch (Exception e) {
			logger.error("error reloading CBS to NPCI response codes.", e);
		}
		return false;
	}

	@GetMapping(path = "/config/shutdownAcquirers")
	public boolean shutdownAcquirerPort() {
		try {
			appConfig.getAcquirerConnector().stop();
			appConfig.getAcquirerConnector().destroy();
			MerchantTCPServer tcpServer = appConfig.context.getBean(MerchantTCPServer.class);
			tcpServer.stop();
			return true;
		} catch (Exception e) {
			logger.error("error stopping acquirer port.", e);
		}
		return false;
	}

}
