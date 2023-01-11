package com.fss.aeps.acquirer.cbs;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.cbs.model.ReversalRequest;
import com.fss.aeps.acquirer.cbs.model.ReversalResponse;
import com.fss.aeps.acquirer.core.RevPaySender;
import com.fss.aeps.cbsclient.CBSResponse;
import com.fss.aeps.cbsclient.CSBCbsClient;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.AccountDetailType;
import com.fss.aeps.jaxb.AccountType;
import com.fss.aeps.jaxb.AccountType.Detail;
import com.fss.aeps.jaxb.AmountType;
import com.fss.aeps.jaxb.DeviceType;
import com.fss.aeps.jaxb.IdentityType;
import com.fss.aeps.jaxb.InfoType;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayeeType;
import com.fss.aeps.jaxb.PayeesType;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.RatingType;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jaxb.TxnSubType;
import com.fss.aeps.jpa.acquirer.AcquirerReversal;
import com.fss.aeps.jpa.acquirer.AcquirerTransaction;
import com.fss.aeps.jpa.acquirer.AcquirerTransactionPayee;
import com.fss.aeps.services.AcquirerTransactionService;
import com.fss.aeps.util.DeviceTagMap;
import com.fss.aeps.util.Generator;

@RestController
public class TFReversalTransaction {

	private static final Logger logger = LoggerFactory.getLogger(TFReversalTransaction.class);

	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private CSBCbsClient cbsClient;

	@Autowired
	private AcquirerTransactionService transactionService;

	@PostMapping(path = "/acquirer/TFReversal/{txnId}", produces = MediaType.APPLICATION_XML_VALUE)
	public ReversalResponse reversal(ReversalRequest reversalRequest) {
		ReversalResponse response = new ReversalResponse(reversalRequest);
		response.responseCode = "91";
		response.responseDesc = "Original Txn Not Found";
		logger.info("initiating terminal failure reversal : "+reversalRequest);
		final AcquirerTransaction acquirerTransaction = transactionService.findAcquirerTransactiontByTxnId(reversalRequest.rrn, reversalRequest.uidVidNo);
		if(acquirerTransaction == null) {
			logger.error("acquirer transaction is null for TxnID : "+reversalRequest.rrn+":"+reversalRequest.uidVidNo);
			response.responseDesc = "Original Txn Not Found";
			return response;
		}
		final ReqPay reversal = new ReqPay();
		final PayTrans txn = new PayTrans();
		final PayerType payer = new PayerType();
		final InfoType info = new InfoType();
		final IdentityType identityType = new IdentityType();
		final RatingType rating = new RatingType();
		final DeviceType device = new DeviceType();
		final AccountType account = new AccountType();
		final AmountType amount = new AmountType();
		final PayeesType payees = new PayeesType();
		reversal.setHead(appConfig.getHead());
		reversal.setTxn(txn);
		reversal.setPayer(payer);
		payer.setInfo(info);
		info.setIdentity(identityType);
		info.setRating(rating);
		payer.setDevice(device);
		payer.setAc(account);
		payer.setAmount(amount);
		reversal.setPayees(payees);

		txn.setId(Generator.newRandomTxnId(appConfig.participationCode));
		txn.setNote("REVERSAL");
		txn.setRefId(acquirerTransaction.getRefId());
		txn.setRefUrl("https://www.npci.org.in");
		txn.setTs(acquirerTransaction.getTxnTs());
		txn.setOrgTxnDate(acquirerTransaction.getTxnTs());
		txn.setType(PayConstant.REVERSAL);
		txn.setOrgTxnId(acquirerTransaction.getTxnId());
		txn.setOrgRespCode("22");
		txn.setCustRef(acquirerTransaction.getCustRef());
		txn.setInitiationMode(acquirerTransaction.getInitiationMode());
		txn.setSubType(TxnSubType.DEBIT);
		txn.setPurpose(acquirerTransaction.getPurpose());

		payer.setAddr(acquirerTransaction.getPayerAddr());
		payer.setCode(acquirerTransaction.getPayerCode());
		payer.setName(acquirerTransaction.getPayerName());
		payer.setSeqNum(acquirerTransaction.getPayerSeqNum());
		payer.setType(acquirerTransaction.getPayerType());

		identityType.setId(acquirerTransaction.getPayerInfoIdentityId());
		identityType.setType(acquirerTransaction.getPayerIdentityType());
		identityType.setVerifiedName(acquirerTransaction.getPayerIdentityVerifiedName());
		rating.setVerifiedAddress(acquirerTransaction.getPayerRatingVerifiedAddress());

		device.getTag().addAll(DeviceTagMap.toTagList(acquirerTransaction.getPayerDeviceDetails()));

		account.setAddrType(acquirerTransaction.getPayerAcAddrType());
		account.getDetail().add(new Detail(AccountDetailType.IIN, acquirerTransaction.getPayerAcIin()));
		Detail detail = new Detail(AccountDetailType.UIDNUM, acquirerTransaction.getPayerAcUidnumVid());
		account.getDetail().add(detail);
		if(acquirerTransaction.getPayerAcUidnumVid().length() == 16) detail.setName(AccountDetailType.VID);

		amount.setCurr(acquirerTransaction.getPayerCurrency());
		amount.setValue(acquirerTransaction.getPayerAmount());
		Set<AcquirerTransactionPayee> payeeSet = acquirerTransaction.getPayees();
		payeeSet.forEach(p -> {
			final PayeeType payee = new PayeeType();
			payees.getPayee().add(payee);
			payee.setAddr(p.getAddr());
			payee.setSeqNum(p.getId().getSeqnum());
			payee.setType(p.getType());
			payee.setCode(p.getCode());
			payee.setAmount(amount);
		});
		final AcquirerReversal acquirerReversal =  new AcquirerReversal();
		reversal.context.put(ContextKey.CHANNEL, acquirerTransaction.getChannel());
		reversal.context.put(ContextKey.AGENT_DETAILS, acquirerTransaction.getAgentDetails());
		reversal.context.put(ContextKey.ORG_TXN_MSG_ID, acquirerTransaction.getMsgId());
		reversal.context.put(ContextKey.ACQUIRER_REVERSAL, acquirerReversal);
		final RespPay respPay = appConfig.context.getBean(RevPaySender.class).send(reversal);
		logger.info("terminal failure reversal request forwarded to npci : "+respPay.getTxn().getId());
		final CBSResponse cbsResponse =  cbsClient.acqAccountingCWReversal(acquirerTransaction, respPay).block();
		if(cbsResponse != null) logger.info("Terminal Failure Response fro TxnID : "+reversal.getTxn().getId()+" Response Code : "+cbsResponse.responseCode);
		if(cbsResponse != null) {
			response.responseCode = cbsResponse.responseCode;
			response.respDesc = cbsResponse.responseMessage;
		}
		
		return response;
	}
}