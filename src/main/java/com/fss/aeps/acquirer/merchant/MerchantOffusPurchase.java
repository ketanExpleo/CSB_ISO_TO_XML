package com.fss.aeps.acquirer.merchant;

import java.math.BigDecimal;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.AcquirerChannel;
import com.fss.aeps.acquirer.core.ReqPaySender;
import com.fss.aeps.acquirer.core.Templates;
import com.fss.aeps.acquirer.matm.ReversalTransaction;
import com.fss.aeps.cbsclient.AcquirerCbsClient;
import com.fss.aeps.cbsclient.CBSResponse;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.constants.Purpose;
import com.fss.aeps.jaxb.AccountDetailType;
import com.fss.aeps.jaxb.AccountType;
import com.fss.aeps.jaxb.AccountType.Detail;
import com.fss.aeps.jaxb.AddressType;
import com.fss.aeps.jaxb.AmountType;
import com.fss.aeps.jaxb.Auth;
import com.fss.aeps.jaxb.CredSubType;
import com.fss.aeps.jaxb.CredType;
import com.fss.aeps.jaxb.CredsType;
import com.fss.aeps.jaxb.Data;
import com.fss.aeps.jaxb.DeviceTagNameType;
import com.fss.aeps.jaxb.DeviceType;
import com.fss.aeps.jaxb.DeviceType.Tag;
import com.fss.aeps.jaxb.Hmac;
import com.fss.aeps.jaxb.IdentityConstant;
import com.fss.aeps.jaxb.IdentityType;
import com.fss.aeps.jaxb.InfoType;
import com.fss.aeps.jaxb.Meta;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayeeType;
import com.fss.aeps.jaxb.PayeesType;
import com.fss.aeps.jaxb.PayerConstant;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.RatingType;
import com.fss.aeps.jaxb.RefType;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespPay;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jaxb.Skey;
import com.fss.aeps.jaxb.TxnSubType;
import com.fss.aeps.jaxb.Uses;
import com.fss.aeps.jaxb.WhiteListedConstant;
import com.fss.aeps.jpa.acquirer.AcquirerTransaction;
import com.fss.aeps.repository.AcquirerRepositories.AcquirerTransactionRepository;
import com.fss.aeps.util.ExceptionUtil;
import com.fss.aeps.util.Generator;
import com.fss.aeps.util.Mapper;
import com.fss.aeps.util.Tlv;

@Component
public class MerchantOffusPurchase {

	private static final Logger logger = LoggerFactory.getLogger(MerchantTransaction.class);

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private AcquirerTransactionRepository repository;

	@Autowired
	@Qualifier("npciResponseDescMapper")
	private Mapper npciResponseDescMapper;

	@Autowired
	private AcquirerCbsClient cbsClient;

	@Autowired
	@Qualifier(value = "threadpool")
	private ThreadPoolExecutor executor;

	public MerchantResponse process(Socket socket, MerchantRequest merchantRequest) {
		final AcquirerTransaction transaction = new AcquirerTransaction();
		try {
			final ReqPay request = new ReqPay();
			final PayTrans txn = new PayTrans();
			final PayerType payer = new PayerType();
			final PayeesType payees = new PayeesType();
			final PayeeType payee = new PayeeType();

			request.setHead(appConfig.getHead());
			request.setTxn(txn);
			request.setPayer(payer);
			request.setPayees(payees);
			payees.getPayee().add(payee);

			txn.setId(Generator.newRandomTxnId(appConfig.participationCode));
			txn.setNote("AEPS Transaction");
			txn.setRefId(merchantRequest.stan);
			txn.setRefUrl("https://www.npci.org.in");
			txn.setTs(new Date());
			txn.setType(PayConstant.PAY);
			txn.setCustRef(merchantRequest.rrn);
			txn.setInitiationMode("00");
			txn.setSubType(TxnSubType.PAY);
			txn.setPurpose(Purpose.PURCHASE);

			payer.setAddr(merchantRequest.iin+"@"+appConfig.participationCode);
			payer.setCode(merchantRequest.mcc);
			payer.setName(merchantRequest.merLoc);
			payer.setType(PayerConstant.ENTITY);
			payer.setSeqNum("0");

			final InfoType info = new InfoType();
			final IdentityType identity = new IdentityType();
			identity.setId("BANK");
			identity.setType(IdentityConstant.BANK);
			identity.setVerifiedName(merchantRequest.merLoc);
			final RatingType rating = new RatingType();
			rating.setVerifiedAddress(WhiteListedConstant.TRUE);
			info.setIdentity(identity);
			info.setRating(rating);

			final Tlv tlv = Tlv.parse(merchantRequest.authFactor);

			final DeviceType device = Templates.getDeviceType();
			device.getTag().add(new Tag(DeviceTagNameType.CARD_ACC_ID_CODE, appConfig.participationCode+String.format("%12s", merchantRequest.tid).replaceAll(" ", "0"))); //**
			device.getTag().add(new Tag(DeviceTagNameType.LOCATION, merchantRequest.merLoc));
			device.getTag().add(new Tag(DeviceTagNameType.PIN_CODE, tlv.get("006")));
			device.getTag().add(new Tag(DeviceTagNameType.AGENT_ID, merchantRequest.merchantID));

			final AccountType payerAc = new AccountType();
			payerAc.setAddrType(AddressType.AADHAAR);
			payerAc.getDetail().add(new Detail(AccountDetailType.IIN, merchantRequest.iin));
			if(merchantRequest.pan.length() == 12) payerAc.getDetail().add(new Detail(AccountDetailType.UIDNUM, merchantRequest.pan));
			else payerAc.getDetail().add(new Detail(AccountDetailType.VID, merchantRequest.pan));
			final CredsType payerCreds = new CredsType();
			final CredsType.Cred payerCred = new CredsType.Cred();
			payerCred.setType(CredType.AADHAAR);
			payerCred.setSubType(CredSubType.AADHAAR_BIO_FP);
			final Auth auth = new Auth();
			auth.setUid(merchantRequest.pan);

			final Uses uses = Templates.getUses(tlv.get("001"));
			final Meta meta = new Meta();
			meta.setDc(tlv.get("013"));
			meta.setDpId(tlv.get("010"));
			meta.setMc(merchantRequest.mc);
			meta.setMi(tlv.get("014"));
			meta.setRdsId(tlv.get("011"));
			meta.setRdsVer(tlv.get("012"));
			meta.setUdc(tlv.get("009"));
			final Skey skey = new Skey();
			skey.setCi(merchantRequest.ci);
			skey.setValue(merchantRequest.skey);
			final Hmac hmac = new Hmac();
			hmac.setValue(merchantRequest.hmac);
			final Data data = new Data();
			data.setType(tlv.get("008"));
			data.setValue(merchantRequest.pid);
			payerCreds.getCred().add(payerCred);
			payerCred.setAuth(auth);
			auth.setUses(uses);
			auth.setMeta(meta);
			auth.setSkey(skey);
			auth.setHmac(hmac);
			auth.setData(data);
			final AmountType amount = new AmountType();
			amount.setCurr("INR");
			amount.setValue(new BigDecimal(merchantRequest.amount).divide(new BigDecimal(100.0)));

			payer.setInfo(info);
			payer.setDevice(device);
			payer.setAc(payerAc);
			payer.setCreds(payerCreds);
			payer.setAmount(amount);

			final CredsType payeeCreds = new CredsType();
			final CredsType.Cred payeeCred = new CredsType.Cred();
			payeeCreds.getCred().add(payeeCred);
			payeeCred.setType(CredType.POST_CREDIT);
			payeeCred.setSubType(CredSubType.NA);
			payee.setAmount(amount);
			payee.setCreds(payeeCreds);

			payee.setAddr(merchantRequest.pan+"@"+appConfig.iin+".iin.npci");
			payee.setCode("0000");
			payee.setSeqNum("0");
			payee.setType(PayerConstant.PERSON);

			request.context.put(ContextKey.CHANNEL, AcquirerChannel.MERCHANT);
			request.context.put(ContextKey.AGENT_DETAILS, merchantRequest.merchantDets);
			request.context.put(ContextKey.RECON_INDICATOR, merchantRequest.reconIndicator);
			request.context.put(ContextKey.ACQUIRER_TRANSACTION, transaction);

			final RespPay response = appConfig.context.getBean(ReqPaySender.class).send(request); //, purchaseAccounting.andThen(purchaseReversal)
			if (response.getResp().getResult() == ResultType.SUCCESS) {
				final CBSResponse accountingResponse = cbsClient.accountingPT(request).block();
				if(accountingResponse != null) {
					transaction.setCbsTranDetails(accountingResponse.tranDetails);
					transaction.setCbsResponseCode(accountingResponse.responseCode);
					transaction.setCbsAuthCode(accountingResponse.authCode);
				} else {
					//retry and check for 913
					transaction.setCbsResponseCode("91");
					logger.info("accounting response not received for txnId : " + request.getTxn().getId());
				}

				if(accountingResponse == null || "911".equalsIgnoreCase(accountingResponse.responseCode) || "91".equalsIgnoreCase(accountingResponse.responseCode)) {
					cbsClient.accountingReversal(transaction);
					response.getResp().setResult(ResultType.FAILURE);
					response.getResp().setErrCode("91");
					request.context.put(ContextKey.ORG_RESP_CODE, "22");
					executor.execute(appConfig.context.getBean(ReversalTransaction.class, request));
				}
				else if(!"000".equals(accountingResponse.responseCode)) {
					response.getResp().setResult(ResultType.FAILURE);
					response.getResp().setErrCode("91");
					request.context.put(ContextKey.ORG_RESP_CODE, "22");
					executor.execute(appConfig.context.getBean(ReversalTransaction.class, request));
				}
			} else if ("91".equals(response.getResp().getErrCode())
					&& ContextKey.toBoolean(response.context.get(ContextKey.IS_STATIC_RESPONSE))) {
				logger.info("generating reversal for cash withdrawal. TxnId : " + request.getTxn().getId());
				executor.execute(appConfig.context.getBean(ReversalTransaction.class, request));
			}
			return processResponse(request, response, merchantRequest);
		} catch (Exception e) {
			transaction.setException(ExceptionUtil.appendBlob(transaction.getException(), e));
			logger.error("error in cash withdrawal ", e);
		} finally {
			try {
				repository.save(transaction);
			} catch (Exception e) {
				logger.error("error while saving cash withdrawal.", e);
			}
		}
		return null;

	}

	public MerchantResponse processResponse(ReqPay request, RespPay response, MerchantRequest merchantRequest) {
		try {
			logger.info("response received : "+response);
			if(response.getResp().getResult() == ResultType.SUCCESS) {
				MerchantResponse merchantResponse = new MerchantResponse(merchantRequest);
				merchantResponse.msgType = "0210";
				merchantResponse.responseCode = "00";
				merchantResponse.respDesc = "Transaction Successfull.";
				merchantResponse.merchantDets = (String) request.context.get("MERCHANT_AMNT_DETS");
				merchantResponse.merAvailBal = (String) request.context.get("MER_AVAIL_BAL");
				response.getResp().getRef().stream().filter(ref -> ref.getType() == RefType.PAYER).
				findFirst().ifPresent(ref -> {
					if(ref.getApprovalNum() != null) merchantResponse.authId = ref.getApprovalNum();
					merchantResponse.accountBal = ref.getBalAmt().substring(7, 20);
					if(ref.getBalAmt().length() > 20) merchantResponse.ledgerBal = ref.getBalAmt().substring(27);
				});
				return merchantResponse;
			}
			else {
				MerchantResponse merchantResponse = new MerchantResponse(merchantRequest);
				merchantResponse.msgType = "0210";
				merchantResponse.responseCode = response.getResp().getErrCode();
				merchantResponse.respDesc = npciResponseDescMapper.mapOrDefault(response.getResp().getErrCode(), "Transaction Failed.");
				return merchantResponse;
			}
		} catch (Exception e) {
			logger.error("error processing response", e);
		}
		return null;
	}
}
