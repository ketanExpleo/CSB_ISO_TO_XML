package com.fss.aeps.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.fss.aeps.AppConfig;
import com.fss.aeps.acquirer.AcquirerChannel;
import com.fss.aeps.acquirer.core.ReqBioAuthSender;
import com.fss.aeps.acquirer.merchant.MerchantRequest;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.constants.Purpose;
import com.fss.aeps.jaxb.AccountDetailType;
import com.fss.aeps.jaxb.AccountType;
import com.fss.aeps.jaxb.AccountType.Detail;
import com.fss.aeps.jaxb.AddressType;
import com.fss.aeps.jaxb.AmountType;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.PayTrans;
import com.fss.aeps.jaxb.PayeeType;
import com.fss.aeps.jaxb.PayeesType;
import com.fss.aeps.jaxb.PayerConstant;
import com.fss.aeps.jaxb.PayerType;
import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.ReqBioAuth;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespBalEnq;
import com.fss.aeps.jaxb.RespBioAuth;
import com.fss.aeps.util.Generator;
import com.fss.aeps.util.ThreadManagement;

public class PatchWork {

	public static final Logger logger = LoggerFactory.getLogger(PatchWork.class);

	public static final void convertCWToCD(ReqPay reqPay, AppConfig appConfig) {
		reqPay.getTxn().setPurpose("23");
		reqPay.getPayer().getInfo().getIdentity().setId(reqPay.getTxn().getId());
		final PayeesType payees = new PayeesType();
		final PayeeType payee = new PayeeType();
		payees.getPayee().add(payee);
		reqPay.setPayees(payees);
		reqPay.getPayer().setType(PayerConstant.PERSON);
		payee.setAddr(reqPay.getPayer().getAddr());
		payee.setCode("0000");
		payee.setSeqNum("0");
		payee.setName(reqPay.getPayer().getName());
		payee.setType(PayerConstant.ENTITY);
		payee.setAmount(reqPay.getPayer().getAmount());
		payee.setAc(reqPay.getPayer().getAc());
		reqPay.getPayer().setAc(new AccountType());
		reqPay.getPayer().getAc().setAddrType(AddressType.AADHAAR);
		reqPay.getPayer().getAc().getDetail().add(new Detail(AccountDetailType.IIN, "608314"));
		payee.getAc().getDetail().stream()
		.filter(f -> f.getName() == AccountDetailType.UIDNUM || f.getName() == AccountDetailType.VID).
		forEach(b -> reqPay.getPayer().getAc().getDetail().add(b));
		payee.setCreds(reqPay.getPayer().getCreds());
		reqPay.getPayer().setCreds(null);

	}

	public static final RespBalEnq convertBalToBioAuthAndPerform(ReqBalEnq request, AppConfig appConfig) {
		final ReqBioAuth bioAuth = new ReqBioAuth();
		bioAuth.setHead(appConfig.getHead());
		final PayTrans txn = new PayTrans();
		bioAuth.setTxn(txn);
		txn.setId(request.getTxn().getId());
		txn.setNote(request.getTxn().getNote());
		txn.setRefId(request.getTxn().getRefId());
		txn.setRefUrl(request.getTxn().getRefUrl());
		txn.setTs(request.getTxn().getTs());
		txn.setType(PayConstant.BIO_AUTH);
		txn.setCustRef(request.getTxn().getCustRef());
		txn.setPurpose(Purpose.PURCHASE);

		final PayerType payer = new PayerType();
		bioAuth.setPayer(payer);
		payer.setDevice(request.getPayer().getDevice());
		payer.setAc(request.getPayer().getAc());
		payer.setCreds(request.getPayer().getCreds());
		payer.setAddr(request.getPayer().getAddr());
		payer.setName(request.getPayer().getName());
		payer.setSeqNum(request.getPayer().getSeqNum());
		payer.setCode(request.getPayer().getCode());
		payer.setType(request.getPayer().getType());

		final AmountType amount = new AmountType();
		payer.setAmount(amount);
		amount.setCurr("INR");
		amount.setValue(new BigDecimal(100.00));

		bioAuth.getPayer().setAddr(String.format("%10s", bioAuth.getPayer().getAddr()).replaceAll(" ", "0"));
		bioAuth.getPayer().getAc().getDetail().stream().filter(f -> f.getName() == AccountDetailType.IIN)
		.forEach(f -> {
			f.setValue(String.format("%6s", f.getValue()).replaceAll(" ", "0"));
		});
		bioAuth.context.put(ContextKey.CHANNEL, AcquirerChannel.MICROATM);
		final RespBioAuth respBioAuth = appConfig.context.getBean(ReqBioAuthSender.class).send(bioAuth);
		String waitResponse = ThreadManagement.waitOtherForCompletion(respBioAuth);
		logger.info("waiting for thread completion : "+waitResponse);

		final ReqBioAuth advice = new ReqBioAuth();
		advice.setHead(appConfig.getHead());
		advice.setTxn(bioAuth.getTxn());
		advice.setPayer(bioAuth.getPayer());
		advice.getPayer().setCreds(null);
		advice.getPayer().setAc(null);
		advice.getTxn().setType(PayConstant.ADVICE);
		advice.getTxn().setOrgTxnId(advice.getTxn().getId());
		advice.getTxn().setId(Generator.newRandomTxnId(appConfig.participationCode));
		advice.getTxn().setPurpose("00");
		advice.getTxn().setOrgRespCode("00");
		advice.getTxn().setNote(advice.getTxn().getOrgTxnId());
		advice.context.put(ContextKey.CHANNEL, AcquirerChannel.MICROATM);
		final RespBioAuth adviceResponse = appConfig.context.getBean(ReqBioAuthSender.class).send(advice);
		return null;

	}

	public static final void convertCWToPurchase(ReqPay reqPay, AppConfig appConfig) {
		reqPay.getTxn().setPurpose(Purpose.PURCHASE);
	}

	public static final void convertFTofUIDToVID(ReqPay request, AppConfig appConfig) {
		request.getPayer().getAc().getDetail().stream()
		.filter(f -> f.getName() == AccountDetailType.UIDNUM || f.getName()==AccountDetailType.VID).forEach(f -> {
			f.setName(AccountDetailType.VID);
			f.setValue("9168899401036922");
		});
		request.getPayer().getCreds().getCred().stream().forEach(c ->{
			c.getAuth().setUid("9168899401036922");
		});

		request.getPayees().getPayee().stream().forEach(p -> {
			p.getAc().getDetail().stream()
			.filter(f -> f.getName() == AccountDetailType.UIDNUM || f.getName()==AccountDetailType.VID)
			.forEach(f -> {
				f.setName(AccountDetailType.UIDNUM);
				f.setValue("891607617819");
			});
		});
	}

	public static void patchFTBioAuth(String payeeIin, ReqBioAuth reqBioAuth, AppConfig appConfig) {
		logger.info("patching ReqBioAuth for test with IIN : "+payeeIin);
		reqBioAuth.getPayer().getAc().getDetail().stream()
		.filter(f -> f.getName() == AccountDetailType.IIN).findFirst().get().setValue(payeeIin);
	}

	public static void patchFTReqPay(ReqPay request, AppConfig appConfig) {
		String payeeIin = request.getPayees().getPayee().get(0).getAc().getDetail().stream()
				.filter(f -> f.getName() == AccountDetailType.IIN).findFirst().get().getValue();
		logger.info("patching ReqPay for FundTransfer with IIN : "+payeeIin);
		request.getPayer().getAc().getDetail().stream()
		.filter(f -> f.getName() == AccountDetailType.IIN).findFirst().get().setValue(payeeIin);
		request.getPayees().getPayee().get(0).getAc().getDetail().stream()
		.filter(f -> f.getName() == AccountDetailType.IIN).findFirst().get().setValue("100070");
	}

	public static String patchOnusPurchaseIin(MerchantRequest merchantRequest) throws IOException {
		final Resource resource = new ClassPathResource("/application.properties");
		final Properties properties = PropertiesLoaderUtils.loadProperties(resource);
		String orgIin = merchantRequest.iin;
		merchantRequest.iin = properties.getProperty("PURCHASE_IIN");
		return orgIin;
	}

	public static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
