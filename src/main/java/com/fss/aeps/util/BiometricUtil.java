package com.fss.aeps.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fss.aeps.AppConfig;
import com.fss.aeps.jaxb.Auth;
import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.ReqBioAuth;
import com.fss.aeps.jaxb.ReqChkTxn;
import com.fss.aeps.jaxb.ReqPay;

public final class BiometricUtil {

	private static final Logger logger = LoggerFactory.getLogger(BiometricUtil.class);

	public static final void setAUADetails(ReqPay request, AppConfig appConfig) {
		logger.info("injecting AUA details.");
		if(request.getPayer().getCreds() != null && request.getPayer().getCreds().getCred().size() > 0 && request.getPayer().getCreds().getCred().get(0).getAuth() != null) {
			  Auth auth = request.getPayer().getCreds().getCred().get(0).getAuth();
			  auth.setAc(appConfig.uidaiAUACode);
			  auth.setSa(appConfig.uidaiSubAUACode);
			  auth.setLk(appConfig.uidaiLicenseKey);
			  auth.setRc("Y");
			  auth.setTid(appConfig.uidaiTid);
			  auth.setVer(appConfig.uidaiVersion);
			  auth.setTxn(request.getTxn().getRefId());

		}
		request.getPayees().getPayee().forEach(p -> {
			 if(p.getCreds() != null && p.getCreds().getCred().size() > 0 && p.getCreds().getCred().get(0).getAuth() != null) {
				 Auth auth = p.getCreds().getCred().get(0).getAuth();
				 auth.setAc(appConfig.uidaiAUACode);
				 auth.setSa(appConfig.uidaiSubAUACode);
				 auth.setLk(appConfig.uidaiLicenseKey);
				 auth.setRc("Y"); auth.setTid(appConfig.uidaiTid);
				 auth.setVer(appConfig.uidaiVersion);
				 auth.setTxn(request.getTxn().getRefId());
			 }
		});
	}

	public static final void setAUADetails(ReqBalEnq request, AppConfig appConfig) {
		logger.info("injecting AUA details.");
		if(request.getPayer().getCreds() != null && request.getPayer().getCreds().getCred().size() > 0 && request.getPayer().getCreds().getCred().get(0).getAuth() != null) {
			  Auth auth = request.getPayer().getCreds().getCred().get(0).getAuth();
			  auth.setAc(appConfig.uidaiAUACode);
			  auth.setSa(appConfig.uidaiSubAUACode);
			  auth.setLk(appConfig.uidaiLicenseKey);
			  auth.setRc("Y");
			  auth.setTid(appConfig.uidaiTid);
			  auth.setVer(appConfig.uidaiVersion);
			  auth.setTxn(request.getTxn().getRefId());
		}
	}

	public static final void setAUADetails(ReqBioAuth request, AppConfig appConfig) {
		logger.info("injecting AUA details.");
		if(request.getPayer().getCreds() != null && request.getPayer().getCreds().getCred().size() > 0 && request.getPayer().getCreds().getCred().get(0).getAuth() != null) {
			  Auth auth = request.getPayer().getCreds().getCred().get(0).getAuth();
			  auth.setAc(appConfig.uidaiAUACode);
			  auth.setSa(appConfig.uidaiSubAUACode);
			  auth.setLk(appConfig.uidaiLicenseKey);
			  auth.setRc("Y");
			  auth.setTid(appConfig.uidaiTid);
			  auth.setVer(appConfig.uidaiVersion);
			  auth.setTxn(request.getTxn().getRefId());
		}
	}

	public static void setAUADetails(ReqChkTxn request, AppConfig appConfig) {
		logger.info("injecting AUA details.");
		if(request.getPayer().getCreds() != null && request.getPayer().getCreds().getCred().size() > 0 && request.getPayer().getCreds().getCred().get(0).getAuth() != null) {
			  Auth auth = request.getPayer().getCreds().getCred().get(0).getAuth();
			  auth.setAc(appConfig.uidaiAUACode);
			  auth.setSa(appConfig.uidaiSubAUACode);
			  auth.setLk(appConfig.uidaiLicenseKey);
			  auth.setRc("Y");
			  auth.setTid(appConfig.uidaiTid);
			  auth.setVer(appConfig.uidaiVersion);
			  auth.setTxn(request.getTxn().getRefId());
		}
	}
}

