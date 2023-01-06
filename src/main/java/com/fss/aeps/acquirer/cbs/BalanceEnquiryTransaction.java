package com.fss.aeps.acquirer.cbs;

import java.math.BigDecimal;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.AcquirerChannel;
import com.fss.aeps.acquirer.core.ReqBalEnqSender;
import com.fss.aeps.acquirer.core.Templates;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.AccountDetailType;
import com.fss.aeps.jaxb.AccountType;
import com.fss.aeps.jaxb.AccountType.Detail;
import com.fss.aeps.jaxb.AddressType;
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
import com.fss.aeps.jaxb.PayerConstant;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.RatingType;
import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.RespBalEnq;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jaxb.Skey;
import com.fss.aeps.jaxb.Uses;
import com.fss.aeps.jaxb.WhiteListedConstant;
import com.fss.aeps.util.Generator;
import com.fss.aeps.util.Mapper;
import com.fss.aeps.util.Tlv;
import com.sil.fssswitch.model.BalanceRequest;
import com.sil.fssswitch.model.BalanceResponse;

@Component
public class BalanceEnquiryTransaction {

	private static final Logger logger = LoggerFactory.getLogger(BalanceEnquiryTransaction.class);

	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	@Qualifier("npciResponseDescMapper")
	private Mapper npciResponseDescMapper;

	public BalanceResponse process(BalanceRequest balanceRequest) {
		
		
		if(balanceRequest.cardNo.charAt(6) == '0') balanceRequest.uidVidNo = balanceRequest.cardNo.substring(7);
		
		// final BalanceRequest body = new BalanceRequest();
		// logger.info("body : " + body);
		final ReqBalEnq reqBalEnq = new ReqBalEnq();
		final PayTrans txn = new PayTrans();
		final PayerType payer = new PayerType();

		reqBalEnq.setTxn(txn);
		reqBalEnq.setPayer(payer);

		txn.setId(Generator.newRandomTxnId(appConfig.participationCode));
		txn.setNote("AEPS Transaction");
		txn.setRefId(balanceRequest.stan);
		txn.setRefUrl("https://www.npci.org.in");
		txn.setTs(new Date());
		txn.setType(PayConstant.BAL_ENQ);
		txn.setCustRef(balanceRequest.rrn);
		txn.setInitiationMode("00");

		payer.setAddr(appConfig.orgId + "@" + appConfig.participationCode);
		payer.setCode(balanceRequest.merchantType);
		payer.setName(balanceRequest.cardAcptNameLOC); // **
		payer.setType(PayerConstant.ENTITY);
		payer.setSeqNum("0");

		final InfoType info = new InfoType();
		final IdentityType identity = new IdentityType();
		identity.setId("BANK");
		identity.setType(IdentityConstant.BANK);
		identity.setVerifiedName(balanceRequest.cardAcptNameLOC);// **
		final RatingType rating = new RatingType();
		rating.setVerifiedAddress(WhiteListedConstant.TRUE);
		info.setIdentity(identity);
		info.setRating(rating);

		
		
		
		final Tlv tlv = Tlv.parse(balanceRequest.authFactor);
		final Tlv tlvKeydata = Tlv.parse(balanceRequest.keyData);

		final DeviceType device = Templates.getDeviceType();
		device.getTag().add(new Tag(DeviceTagNameType.CARD_ACC_ID_CODE,
				appConfig.participationCode + String.format("%12s", balanceRequest.terminalID).replaceAll(" ", "0"))); // **
		device.getTag().add(new Tag(DeviceTagNameType.LOCATION, balanceRequest.cardAcptNameLOC));//
		device.getTag().add(new Tag(DeviceTagNameType.PIN_CODE, balanceRequest.postalCode));// **
		device.getTag().add(new Tag(DeviceTagNameType.AGENT_ID, balanceRequest.agentID));

		final AccountType payerAc = new AccountType();
		payerAc.setAddrType(AddressType.AADHAAR);
		payerAc.getDetail().add(new Detail(AccountDetailType.IIN, balanceRequest.cardNo.substring(0, 6)));
		logger.info("@@@ bank IIN :: {}", balanceRequest.cardNo.substring(0, 6)); //IIN number
		String uid = balanceRequest.uidVidNo;
		if (uid.length() == 12)
			payerAc.getDetail().add(new Detail(AccountDetailType.UIDNUM, uid));
		else
			payerAc.getDetail().add(new Detail(AccountDetailType.VID, uid));
		final CredsType payerCreds = new CredsType();
		final CredsType.Cred payerCred = new CredsType.Cred();
		payerCred.setType(CredType.AADHAAR);
		payerCred.setSubType(CredSubType.AADHAAR_BIO_FP);
		final Auth auth = new Auth();
		auth.setUid(uid);

		final Uses uses = Templates.getUses(tlv.get("001"));
		uses.setBt("FMR,FIR");
		final Meta meta = new Meta();
		meta.setDc(tlv.get("013"));
		meta.setDpId(tlv.get("010"));
		meta.setLot("P");
		meta.setLov(balanceRequest.postalCode);
		meta.setMc(balanceRequest.mcData.substring(7));
		meta.setMi(tlv.get("014"));
		meta.setRdsId(tlv.get("011"));
		meta.setRdsVer(tlv.get("012"));
		meta.setUdc(tlv.get("009"));
		final Skey skey = new Skey();
		skey.setCi(tlvKeydata.get("002"));
		skey.setValue(tlvKeydata.get("001"));
		final Hmac hmac = new Hmac();
		hmac.setValue(tlvKeydata.get("003"));
		final Data data = new Data();
		data.setType(tlv.get("008"));
		data.setValue(balanceRequest.fingerData.substring(balanceRequest.fingerData.indexOf('M')));
		payerCreds.getCred().add(payerCred);
		payerCred.setAuth(auth);
		auth.setUses(uses);
		auth.setMeta(meta);
		auth.setSkey(skey);
		auth.setHmac(hmac);
		auth.setData(data);

		payer.setInfo(info);
		payer.setDevice(device);
		payer.setAc(payerAc);
		payer.setCreds(payerCreds);
		reqBalEnq.context.put(ContextKey.CHANNEL, AcquirerChannel.CBS);
		logger.info("@@@ request send to NPCI :: {}", reqBalEnq);
		final RespBalEnq respBalEnq = appConfig.context.getBean(ReqBalEnqSender.class).send(reqBalEnq);
		logger.info("response received : " + respBalEnq.getResp().getResult());
		return prepareResponse(reqBalEnq, respBalEnq, balanceRequest);
	}

	private BalanceResponse prepareResponse(ReqBalEnq reqBalEnq, RespBalEnq respBalEnq,
			BalanceRequest balanceRequest) {
		try {
			logger.info("response received : " + respBalEnq);
			if (respBalEnq.getResp().getResult() == ResultType.SUCCESS) {
				BalanceResponse balanceResponse = new BalanceResponse();
				balanceResponse.msgType = "0210";
				balanceResponse.responseCode = "00";
				balanceResponse.responseDesc = "Transaction Successfull";
				String bal = respBalEnq.getPayer().getBal().getData().getValue();
				logger.info("@@@ Bal :: {}", bal);
				if (bal != null) {
					if (bal.length() >= 20) {
						String ledger = bal.substring(0, 20);
						BigDecimal ledgerBD = new BigDecimal(ledger.substring(ledger.length() - 12));
						if (ledger.charAt(7) == 'D')
							ledgerBD = ledgerBD.multiply(new BigDecimal(-1));
						balanceResponse.merAvailBal = Generator.amountToDecimalString(ledgerBD);
					}
					if (bal.length() == 40) {
						String available = bal.substring(20);
						BigDecimal avail = new BigDecimal(available.substring(available.length() - 12));
						if (available.charAt(7) == 'D')
							avail = avail.multiply(new BigDecimal(-1));
						balanceResponse.merAvailBal = Generator.amountToDecimalString(avail);
					}
				} else {
					balanceResponse.responseCode = respBalEnq.getResp().getErrCode();
					balanceResponse.responseDesc = "UIDAI_ERROR";
				}
				return balanceResponse;
			}else {
				BalanceResponse balanceResponse = new BalanceResponse();
				balanceResponse.msgType = "0210";
				balanceResponse.responseCode = respBalEnq.getResp().getErrCode();
				balanceResponse.respDesc = npciResponseDescMapper.mapOrDefault(respBalEnq.getResp().getErrCode(), "Transaction Failed.");
				return balanceResponse;
			}
		} catch (Exception e) {
			logger.error("error processing response", e);
		}
		return null;
	}

}
