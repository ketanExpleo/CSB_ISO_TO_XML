package com.fss.aeps.acquirer.matm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.AcquirerChannel;
import com.fss.aeps.acquirer.core.ReqBalEnqSender;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.RespBalEnq;

@RestController
public class BalanceEnquiry {

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private Corrector corrector;

	@PostMapping(path = "/acquirer/ReqBalEnq/{version}/urn:txnid:{txnid}", produces = MediaType.APPLICATION_XML_VALUE)
	public RespBalEnq balanceEnquiry(@RequestBody ReqBalEnq request) {
		request.context.put(ContextKey.CHANNEL, AcquirerChannel.MICROATM);
		corrector.correctAgentId(request);
		return appConfig.context.getBean(ReqBalEnqSender.class).send(request);
	}

	@GetMapping(path = "/acquirer/test")
	public String balanceEnquiry() {

		return "Welcome";
	}
}