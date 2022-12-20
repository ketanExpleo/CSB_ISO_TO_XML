package com.fss.aeps.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fss.aeps.AppConfig;
import com.fss.aeps.constants.Purpose;
import com.fss.aeps.jaxb.AccountDetailType;
import com.fss.aeps.jaxb.AccountType;
import com.fss.aeps.jaxb.AccountType.Detail;
import com.fss.aeps.jaxb.AddressType;
import com.fss.aeps.jaxb.PayeeType;
import com.fss.aeps.jaxb.PayeesType;
import com.fss.aeps.jaxb.PayerConstant;
import com.fss.aeps.jaxb.ReqBioAuth;
import com.fss.aeps.jaxb.ReqPay;

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

	public static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
