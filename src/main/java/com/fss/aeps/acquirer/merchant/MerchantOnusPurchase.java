package com.fss.aeps.acquirer.merchant;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.AcquirerChannel;
import com.fss.aeps.acquirer.core.ReqBioAuthPurchaseSender;
import com.fss.aeps.acquirer.core.ReqBioAuthSender;
import com.fss.aeps.acquirer.core.Templates;
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
import com.fss.aeps.jaxb.Meta;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayerConstant;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.RefType;
import com.fss.aeps.jaxb.ReqBioAuth;
import com.fss.aeps.jaxb.RespBioAuth;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jaxb.Skey;
import com.fss.aeps.jaxb.Uses;
import com.fss.aeps.jpa.acquirer.AcquirerBioAuthPurchase;
import com.fss.aeps.repository.AcquirerRepositories.AcquirerBioAuthPurchaseRepository;
import com.fss.aeps.test.PatchWork;
import com.fss.aeps.util.ExceptionUtil;
import com.fss.aeps.util.Generator;
import com.fss.aeps.util.Mapper;
import com.fss.aeps.util.Tlv;

@Component
public class MerchantOnusPurchase {

	private static final Logger logger = LoggerFactory.getLogger(MerchantTransaction.class);

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private AcquirerCbsClient cbsClient;

	@Autowired
	@Qualifier("npciResponseDescMapper")
	private Mapper npciResponseDescMapper;

	@Autowired
	@Qualifier("cbsToNpciResponseMapper")
	private Mapper finacleToNpciResponseMapper;

	@Autowired
	private AcquirerBioAuthPurchaseRepository repository;

	public MerchantResponse process(Socket socket, MerchantRequest merchantRequest) throws IOException {
		final AcquirerBioAuthPurchase transaction = new AcquirerBioAuthPurchase();
		try {
			final ReqBioAuth request = new ReqBioAuth();
			final PayTrans txn = new PayTrans();
			final PayerType payer = new PayerType();

			request.setHead(appConfig.getHead());
			request.setTxn(txn);
			request.setPayer(payer);

			txn.setId(Generator.newRandomTxnId(appConfig.participationCode));
			txn.setNote("AEPS Transaction");
			txn.setRefId(merchantRequest.stan);
			txn.setRefUrl("https://www.npci.org.in");
			txn.setTs(new Date());
			txn.setType(PayConstant.BIO_AUTH);
			txn.setCustRef(merchantRequest.rrn);
			txn.setInitiationMode("00");
			txn.setPurpose(Purpose.PURCHASE);

			payer.setAddr(appConfig.orgId + "@" + appConfig.participationCode);
			payer.setCode(merchantRequest.mcc);
			payer.setName(merchantRequest.merLoc);
			payer.setType(PayerConstant.ENTITY);
			payer.setSeqNum("0");

			final Tlv tlv = Tlv.parse(merchantRequest.authFactor);

			final DeviceType device = Templates.getDeviceType();
			device.getTag().add(new Tag(DeviceTagNameType.CARD_ACC_ID_CODE,	appConfig.participationCode + String.format("%12s", merchantRequest.tid).replaceAll(" ", "0"))); // **
			device.getTag().add(new Tag(DeviceTagNameType.LOCATION, merchantRequest.merLoc));
			device.getTag().add(new Tag(DeviceTagNameType.PIN_CODE, tlv.get("006")));// **
			device.getTag().add(new Tag(DeviceTagNameType.AGENT_ID, merchantRequest.merchantID));

			String orgIin = PatchWork.patchOnusPurchaseIin(merchantRequest);

			final AccountType payerAc = new AccountType();
			payerAc.setAddrType(AddressType.AADHAAR);
			payerAc.getDetail().add(new Detail(AccountDetailType.IIN, merchantRequest.iin));
			if (merchantRequest.pan.length() == 12) payerAc.getDetail().add(new Detail(AccountDetailType.UIDNUM, merchantRequest.pan));
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
			meta.setLot("P");
			meta.setLov(tlv.get("006"));
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

			payer.setDevice(device);
			payer.setAc(payerAc);
			payer.setCreds(payerCreds);
			payer.setAmount(amount);

			final CredsType payeeCreds = new CredsType();
			final CredsType.Cred payeeCred = new CredsType.Cred();
			payeeCreds.getCred().add(payeeCred);
			payeeCred.setType(CredType.POST_CREDIT);
			payeeCred.setSubType(CredSubType.NA);

			merchantRequest.iin = orgIin;

			request.context.put(ContextKey.CHANNEL, AcquirerChannel.MERCHANT);
			request.context.put(ContextKey.AGENT_DETAILS, merchantRequest.merchantDets);
			request.context.put(ContextKey.RECON_INDICATOR, merchantRequest.reconIndicator);
			request.context.put(ContextKey.ACQUIRER_TRANSACTION, transaction);
			final RespBioAuth response = appConfig.context.getBean(ReqBioAuthPurchaseSender.class).send(request);
			if(response.getResp().getResult() == ResultType.SUCCESS) {
				CBSResponse cbsResponse = cbsClient.debitCreditOnus(request, response).block();
				transaction.setCbsTranDetails(cbsResponse.tranDetails);
				transaction.setCbsResponseCode(cbsResponse.responseCode);
				transaction.setCbsAuthCode(cbsResponse.authCode);
				if("911".equals(cbsResponse.responseCode) || "91".equals(cbsResponse.responseCode)) {
					final CBSResponse revResponse = cbsClient.debitCreditOnusReversal(request, response).block();
					transaction.setIsReversed("Y");
					transaction.setRevCbsTranDetails(revResponse.tranDetails);
					transaction.setRevCbsResponseCode(revResponse.responseCode);
					transaction.setRevCbsAuthCode(revResponse.authCode);
					transaction.setRevReconIndicator(merchantRequest.reconIndicator+"_RVSL");
					final ReqBioAuth advice = getAdvice(request, finacleToNpciResponseMapper.map(cbsResponse.responseCode));
					final RespBioAuth adviceResponse = appConfig.context.getBean(ReqBioAuthSender.class).send(advice);
					logger.info("advice sent for failure at CBS : "+adviceResponse.getResp().getResult());
				}
				return processResponse(request, response, merchantRequest, cbsResponse);
			}
			else {
				if(ContextKey.toBoolean(response.context.get(ContextKey.IS_STATIC_RESPONSE))) {
					final ReqBioAuth advice = getAdvice(request, "91");
					final RespBioAuth adviceResponse = appConfig.context.getBean(ReqBioAuthSender.class).send(advice);
					logger.info("advice sent for failure at CBS : "+adviceResponse.getResp().getResult());
				}
				return processResponse(request, response, merchantRequest, null);
			}
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


	public MerchantResponse processResponse(ReqBioAuth request, RespBioAuth response, MerchantRequest merchantRequest, CBSResponse debitCbsResponse) {
		try {
			logger.info("response received : "+response);
			if(response.getResp().getResult() == ResultType.SUCCESS && debitCbsResponse != null && "000".equals(debitCbsResponse.responseCode)) {
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
			else if(debitCbsResponse != null) {
				MerchantResponse merchantResponse = new MerchantResponse(merchantRequest);
				merchantResponse.msgType = "0210";
				merchantResponse.responseCode = finacleToNpciResponseMapper.map(debitCbsResponse.responseCode);
				merchantResponse.respDesc = npciResponseDescMapper.mapOrDefault(merchantResponse.responseCode, "Transaction Failed.");
				return merchantResponse;
			}
			else {
				MerchantResponse merchantResponse = new MerchantResponse(merchantRequest);
				merchantResponse.msgType = "0210";
				if(response.getResp().getErrCode() != null) {
					merchantResponse.responseCode = response.getResp().getErrCode();
					merchantResponse.respDesc = npciResponseDescMapper.mapOrDefault(response.getResp().getErrCode(), "Transaction Failed.");
				}
				else {
					merchantResponse.responseCode = "91";
					merchantResponse.respDesc = npciResponseDescMapper.mapOrDefault(response.getResp().getErrCode(), "Transaction Failed.");
				}
				return merchantResponse;
			}
		} catch (Exception e) {
			logger.error("error processing response", e);
		}
		return null;
	}

	private final ReqBioAuth getAdvice(final ReqBioAuth request, final String errCode) {
		final ReqBioAuth advice = new ReqBioAuth();
		advice.setHead(appConfig.getHead());
		advice.setTxn(request.getTxn());
		advice.setPayer(request.getPayer());
		advice.getPayer().setCreds(null);
		advice.getPayer().setAc(null);
		advice.getTxn().setType(PayConstant.ADVICE);
		advice.getTxn().setOrgTxnId(advice.getTxn().getId());
		advice.getTxn().setId(Generator.newRandomTxnId(appConfig.participationCode));
		advice.getTxn().setPurpose("00");
		advice.getTxn().setOrgRespCode(errCode);
		advice.getTxn().setNote(advice.getTxn().getOrgTxnId());
		advice.context.put(ContextKey.CHANNEL, AcquirerChannel.MERCHANT);
		return advice;
	}
}
