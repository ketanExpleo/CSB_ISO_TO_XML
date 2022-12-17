package com.fss.aeps.cbsclient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.util.iso8583.ISO8583Loggging;
import org.util.iso8583.ISO8583Message;
import org.util.iso8583.api.ISO8583Exception;
import org.util.iso8583.api.ISO8583ExceptionCause;
import org.util.iso8583.api.ISOFormat;

import com.fss.aeps.AppConfig;
import com.fss.aeps.constants.Purpose;
import com.fss.aeps.jaxb.CredsType.Cred;
import com.fss.aeps.jaxb.DeviceTagNameType;
import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.ReqChkTxn;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jpa.issuer.IssuerTransaction;
import com.fss.aeps.jpa.issuer.IssuerTransactionPayee;
import com.fss.aeps.util.AadharAccount;
import com.fss.aeps.util.AadharAccountCollector;
import com.fss.aeps.util.Converter;
import com.fss.aeps.util.DeviceTagMap;
import com.fss.aeps.util.Generator;
import com.fss.aeps.util.Mapper;
import com.fss.aeps.util.UIDAIAuthCode;

import reactor.core.publisher.Mono;

public class IssuerFinacleClient implements IssuerCbsClient {

	private static final Logger logger 						= LoggerFactory.getLogger(IssuerFinacleClient.class);

	private static final BigDecimal hundred 				= new BigDecimal(100.00);
	private static final ISOFormat isoFormat 				= FinacleFormat.getInstance();
	private static final DateTimeFormatter dateFormatter 	= DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final DateTimeFormatter datTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	@Autowired
	@Qualifier("cbsToNpciResponseMapper")
	private Mapper finacleToNpciResponseMapper;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private ISO8583MessageSender sender;


	@Override
	public Mono<CBSResponse> balance(ReqBalEnq reqBalEnq) {
		try {
			final LocalDateTime currentTime = LocalDateTime.now();
			final Cred cred = reqBalEnq.getPayer().getCreds().getCred().get(0);
			final UIDAIAuthCode uidaiAuthCode = new UIDAIAuthCode(cred.getData().getValue());
			final ISO8583Message balanceRequest = new ISO8583Message();
			final Map<DeviceTagNameType, String> deviceTagMap = DeviceTagMap.toMap(reqBalEnq.getPayer().getDevice().getTag());
			final AadharAccount account = reqBalEnq.getPayer().getAc().getDetail().stream()
													.collect(AadharAccountCollector.getInstance());
			balanceRequest.put(0, "1200");
			balanceRequest.put(2, account.uidVid);
			balanceRequest.put(3, "310000");
			balanceRequest.put(4, "0000000000000000");
			balanceRequest.put(11, reqBalEnq.getTxn().getCustRef());
			balanceRequest.put(12, currentTime.format(datTimeFormatter));
			balanceRequest.put(17, currentTime.format(dateFormatter));
			balanceRequest.put(24, "200");
			balanceRequest.put(32, appConfig.orgId);
			balanceRequest.put(37, reqBalEnq.getTxn().getCustRef());
			balanceRequest.put(41, deviceTagMap.get(DeviceTagNameType.CARD_ACCP_TR_ID));
			balanceRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			balanceRequest.put(49, "INR");
			balanceRequest.put(63, "AEPS");
			balanceRequest.put(102, "013        0000    110000002901");
			balanceRequest.put(123, "AEP");
			balanceRequest.put(127, "U~"+uidaiAuthCode.authToken+"~null");
			logger.info(ISO8583Loggging.log(balanceRequest).toString());
			final ISO8583Message beResponse = sender.send("ISSUER", "BE" ,balanceRequest, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(beResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.tranDetails = beResponse.get(127);
			response.responseCode = beResponse.get(39);
			response.authCode = beResponse.get(38);
			if("000".equals(beResponse.get(39))) {
				response.responseMessage = "SUCCESS";
				String balances = beResponse.get(48).substring(0, 34);
				String ledger = balances.substring(5, 17);
				String available = balances.substring(22, 34);
				response.balance = "0001356" + (balances.charAt(0) == '+' ? "C" : "D") + ledger;
				response.balance += "0001356" + (balances.charAt(17) == '+' ? "C" : "D") + available;
				response.authCode = beResponse.get(38);
			}
			else {
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (ISO8583Exception e) {
			if(e.cause == ISO8583ExceptionCause.SOCKET_CONNECT_ERROR) {
				return Mono.just(new CBSResponse("91", "connect error."));
			}
			logger.error("error connecting to CBS.", e);
		}
		catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;
	}


	@Override
	public Mono<CBSResponse> miniStatement(final ReqBalEnq reqBalEnq) {
		try {
			final Cred cred = reqBalEnq.getPayer().getCreds().getCred().get(0);
			final UIDAIAuthCode uidaiAuthCode = new UIDAIAuthCode(cred.getData().getValue());
			final ISO8583Message msRequest = new ISO8583Message();
			final LocalDateTime currentTime = LocalDateTime.now();
			final Map<DeviceTagNameType, String> deviceTagMap = DeviceTagMap.toMap(reqBalEnq.getPayer().getDevice().getTag());
			final AadharAccount account = reqBalEnq.getPayer().getAc().getDetail().stream()
													.collect(AadharAccountCollector.getInstance());
			msRequest.put(0, "1200");
			msRequest.put(2, account.uidVid);
			msRequest.put(3, "380000");
			msRequest.put(4, "0000000000000000");
			msRequest.put(11, reqBalEnq.getTxn().getCustRef());
			msRequest.put(12, currentTime.format(datTimeFormatter));
			msRequest.put(17, currentTime.format(dateFormatter));
			msRequest.put(24, "200");
			msRequest.put(32, appConfig.orgId);
			msRequest.put(37, reqBalEnq.getTxn().getCustRef());
			msRequest.put(41, deviceTagMap.get(DeviceTagNameType.CARD_ACCP_TR_ID));
			msRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			msRequest.put(49, "INR");
			msRequest.put(63, "AEPS");
			msRequest.put(102, "013        0000    110000002901");
			msRequest.put(123, "AEP");
			msRequest.put(127, "U~"+uidaiAuthCode.authToken+"~null");
			logger.info(ISO8583Loggging.log(msRequest).toString());
			final ISO8583Message msResponse = sender.send("ISSUER", "MS", msRequest, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(msResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.tranDetails = msResponse.get(127);
			response.responseCode = msResponse.get(39);
			response.authCode = msResponse.get(38);
			if("000".equals(msResponse.get(39))) {
				response.responseMessage = "SUCCESS";
				String balances = msResponse.get(48).substring(0, 34);
				String ledger = balances.substring(5, 17);
				String available = balances.substring(22, 34);
				response.balance = "0001356" + (balances.charAt(0) == '+' ? "C" : "D") + ledger;
				response.balance += "0001356" + (balances.charAt(17) == '+' ? "C" : "D") + available;

				final String data = msResponse.get(125);
				int i = 0;
				while(i < data.length()) {
					if(i+87 < data.length()) {
						String record = data.substring(i, i+87);
						response.statement.add(record);
					}
					i = i +87;
				}
			}
			else {
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (ISO8583Exception e) {
			if(e.cause == ISO8583ExceptionCause.SOCKET_CONNECT_ERROR) {
				return Mono.just(new CBSResponse("91", "connect error."));
			}
			logger.error("error connecting to CBS.", e);
		}
		 catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;
	}


	@Override
	public Mono<CBSResponse> creditFundTransfer(ReqPay reqPay) {
		try {
			final ISO8583Message creditRequest = new ISO8583Message();
			final LocalDateTime currentTime = Converter.dateToLocalDateTime(reqPay.getTxn().getTs());
			final Map<DeviceTagNameType, String> deviceTagMap = DeviceTagMap.toMap(reqPay.getPayer().getDevice().getTag());
			final AadharAccount account = reqPay.getPayees().getPayee().get(0).getAc().getDetail().stream()
										.collect(AadharAccountCollector.getInstance());

			final BigDecimal amount = reqPay.getPayees().getPayee().get(0).getAmount().getValue();
			final String formattedAmount = Generator.amountToFormattedString16(amount.multiply(hundred).toBigInteger());
			creditRequest.put(0, "1200");
			creditRequest.put(2, account.uidVid);
			creditRequest.put(3, "400000");
			creditRequest.put(4, formattedAmount);
			creditRequest.put(11, reqPay.getTxn().getCustRef());
			creditRequest.put(12, currentTime.format(datTimeFormatter));
			creditRequest.put(17, currentTime.format(dateFormatter));
			creditRequest.put(24, "200");
			creditRequest.put(32, appConfig.orgId);
			creditRequest.put(37, reqPay.getTxn().getCustRef());
			creditRequest.put(41, deviceTagMap.get(DeviceTagNameType.CARD_ACCP_TR_ID));
			creditRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			creditRequest.put(49, "INR");
			creditRequest.put(102, "013        0000    110000002901");
			creditRequest.put(103, "  013        0000    110000002901");
			creditRequest.put(123, "AEP");
			creditRequest.put(125, "AEP");
			creditRequest.put(126, "AEPS_RONUS_CBS_BEN_TRF_PMT");
			creditRequest.put(127, "A"+"~"+account.uidVid);
			logger.info(ISO8583Loggging.log(creditRequest).toString());
			final ISO8583Message creditResponse = sender.send("ISSUER", "FT", creditRequest, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(creditResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.tranDetails = creditResponse.get(127);
			response.responseCode = creditResponse.get(39);
			response.authCode = creditResponse.get(38);
			if("000".equals(creditResponse.get(39))) {
				response.responseMessage = "SUCCESS";
				String balances = creditResponse.get(48).substring(0, 34);
				String ledger = balances.substring(5, 17);
				String available = balances.substring(22, 34);
				response.balance = "0001356" + (balances.charAt(0) == '+' ? "C" : "D") + ledger;
				response.balance += "0001356" + (balances.charAt(17) == '+' ? "C" : "D") + available;
				String[] accountName = creditResponse.get(126).split("~");
				response.customerName = accountName[1];
				response.operatedAccount = accountName[0];
				response.authCode = creditResponse.get(38);
			}
			else {
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (ISO8583Exception e) {
			if(e.cause == ISO8583ExceptionCause.SOCKET_CONNECT_ERROR) {
				return Mono.just(new CBSResponse("91", "connect error."));
			}
			logger.error("error connecting to CBS.", e);
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> deposit(ReqPay reqPay) {
		try {
			final Cred cred = reqPay.getPayees().getPayee().get(0).getCreds().getCred().get(0);
			final UIDAIAuthCode uidaiAuthCode = new UIDAIAuthCode(cred.getData().getValue());
			final ISO8583Message creditRequest = new ISO8583Message();
			final LocalDateTime currentTime = Converter.dateToLocalDateTime(reqPay.getTxn().getTs());
			final Map<DeviceTagNameType, String> deviceTagMap = DeviceTagMap.toMap(reqPay.getPayer().getDevice().getTag());
			final BigDecimal amount = reqPay.getPayees().getPayee().get(0).getAmount().getValue();
			final String formattedAmount = Generator.amountToFormattedString16(amount.multiply(hundred).toBigInteger());
			creditRequest.put(0, "1200");
			creditRequest.put(2, "BAV");
			creditRequest.put(3, "820000");
			creditRequest.put(4, formattedAmount);
			creditRequest.put(11, reqPay.getTxn().getCustRef());
			creditRequest.put(12, currentTime.format(datTimeFormatter));
			creditRequest.put(17, currentTime.format(dateFormatter));
			creditRequest.put(24, "200");
			creditRequest.put(32, appConfig.orgId);
			creditRequest.put(37, reqPay.getTxn().getCustRef());
			creditRequest.put(41, deviceTagMap.get(DeviceTagNameType.CARD_ACCP_TR_ID));
			creditRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			creditRequest.put(49, "INR");
			creditRequest.put(102, "013        0000    110000002901");
			creditRequest.put(103, "  013        0000    110000002901");
			creditRequest.put(123, "AEP");
			creditRequest.put(125, Generator.amountToDecimalString(amount)+"~"+
			reqPay.getTxn().getCustRef()+"~U~"+uidaiAuthCode.authToken);
			logger.info(ISO8583Loggging.log(creditRequest).toString());
			final ISO8583Message creditResponse = sender.send("ISSUER", "BAV", creditRequest, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(creditResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.tranDetails = creditResponse.get(127);
			response.responseCode = creditResponse.get(39);
			response.authCode = creditResponse.get(38);
			if("000".equals(creditResponse.get(39))) {
				response.responseMessage = "SUCCESS";
				String balances = creditResponse.get(48).substring(0, 34);
				String ledger = balances.substring(5, 17);
				String available = balances.substring(22, 34);
				response.balance = "0001356" + (balances.charAt(0) == '+' ? "C" : "D") + ledger;
				response.balance += "0001356" + (balances.charAt(17) == '+' ? "C" : "D") + available;
				response.operatedAccount = creditResponse.get(103);
				response.customerName = creditResponse.get(126);
			}
			else {
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (ISO8583Exception e) {
			if(e.cause == ISO8583ExceptionCause.SOCKET_CONNECT_ERROR) {
				return Mono.just(new CBSResponse("91", "connect error."));
			}
			logger.error("error connecting to CBS.", e);
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> depositAdvice(ReqChkTxn reqChkTxn, IssuerTransaction original) {
		try {
			List<IssuerTransactionPayee> payees = new ArrayList<>(original.getPayees());
			final UIDAIAuthCode uidaiAuthCode = new UIDAIAuthCode(payees.get(0).getCredData());
			final ISO8583Message creditRequest = new ISO8583Message();
			final LocalDateTime currentTime = Converter.dateToLocalDateTime(original.getTxnTs());
			final Map<DeviceTagNameType, String> deviceTagMap = DeviceTagMap.toMap(reqChkTxn.getPayer().getDevice().getTag());
			final AadharAccount account = reqChkTxn.getPayee().getAc().getDetail().stream()
										.collect(AadharAccountCollector.getInstance());

			final BigDecimal amount = reqChkTxn.getPayee().getAmount().getValue();
			final String formattedAmount = Generator.amountToFormattedString16(amount.multiply(hundred).toBigInteger());
			creditRequest.put(0, "1220");
			creditRequest.put(2, account.uidVid);
			creditRequest.put(3, "400000");
			creditRequest.put(4, formattedAmount);
			creditRequest.put(11, reqChkTxn.getTxn().getOrgRrn());
			creditRequest.put(12, currentTime.format(datTimeFormatter));
			creditRequest.put(17, currentTime.format(dateFormatter));
			creditRequest.put(24, "200");
			creditRequest.put(32, appConfig.orgId);
			creditRequest.put(37, reqChkTxn.getTxn().getOrgRrn());
			creditRequest.put(41, deviceTagMap.get(DeviceTagNameType.CARD_ACCP_TR_ID));
			creditRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			creditRequest.put(49, "INR");
			creditRequest.put(102, "013        0000    110000002901");
			creditRequest.put(103, "  013        0000    110000002901");
			creditRequest.put(123, "AEP");
			creditRequest.put(126, "AEPS_RONUS_CBS_BEN_CDEP_PMT");
			creditRequest.put(127, "U~"+uidaiAuthCode.authToken+"~"+reqChkTxn.getTxn().getOrgRrn());
			logger.info(ISO8583Loggging.log(creditRequest).toString());
			final ISO8583Message creditResponse = sender.send("ISSUER", "CD", creditRequest, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(creditResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.tranDetails = creditResponse.get(127);
			response.responseCode = creditResponse.get(39);
			response.authCode = creditResponse.get(38);
			if("000".equals(creditResponse.get(39))) {
				response.responseMessage = "SUCCESS";
				String balances = creditResponse.get(48).substring(0, 34);
				String ledger = balances.substring(5, 17);
				String available = balances.substring(22, 34);
				response.balance = "0001356" + (balances.charAt(0) == '+' ? "C" : "D") + ledger;
				response.balance += "0001356" + (balances.charAt(17) == '+' ? "C" : "D") + available;
				response.operatedAccount = creditResponse.get(103);
				response.customerName = "NAME";
				response.authCode = creditResponse.get(38);
			}
			else {
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (ISO8583Exception e) {
			if(e.cause == ISO8583ExceptionCause.SOCKET_CONNECT_ERROR) {
				return Mono.just(new CBSResponse("91", "connect error."));
			}
			logger.error("error connecting to CBS.", e);
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> depositRepeatAdvice(ReqChkTxn reqChkTxn, IssuerTransaction original) {
		try {
			List<IssuerTransactionPayee> payees = new ArrayList<>(original.getPayees());
			final UIDAIAuthCode uidaiAuthCode = new UIDAIAuthCode(payees.get(0).getCredData());
			final ISO8583Message creditRequest = new ISO8583Message();
			final LocalDateTime currentTime = Converter.dateToLocalDateTime(original.getTxnTs());
			final Map<DeviceTagNameType, String> deviceTagMap = DeviceTagMap.toMap(reqChkTxn.getPayer().getDevice().getTag());
			final AadharAccount account = reqChkTxn.getPayee().getAc().getDetail().stream()
										.collect(AadharAccountCollector.getInstance());

			final BigDecimal amount = reqChkTxn.getPayee().getAmount().getValue();
			final String formattedAmount = Generator.amountToFormattedString16(amount.multiply(hundred).toBigInteger());
			creditRequest.put(0, "1221");
			creditRequest.put(2, account.uidVid);
			creditRequest.put(3, "400000");
			creditRequest.put(4, formattedAmount);
			creditRequest.put(11, reqChkTxn.getTxn().getOrgRrn());
			creditRequest.put(12, currentTime.format(datTimeFormatter));
			creditRequest.put(17, currentTime.format(dateFormatter));
			creditRequest.put(24, "200");
			creditRequest.put(32, appConfig.orgId);
			creditRequest.put(37, reqChkTxn.getTxn().getOrgRrn());
			creditRequest.put(41, deviceTagMap.get(DeviceTagNameType.CARD_ACCP_TR_ID));
			creditRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			creditRequest.put(49, "INR");
			creditRequest.put(102, "013        0000    110000002901");
			creditRequest.put(103, "  013        0000    110000002901");
			creditRequest.put(123, "AEP");
			creditRequest.put(126, "AEPS_RONUS_CBS_BEN_CDEP_PMT");
			creditRequest.put(127, "U~"+uidaiAuthCode.authToken+"~"+reqChkTxn.getTxn().getOrgRrn());
			logger.info(ISO8583Loggging.log(creditRequest).toString());
			final ISO8583Message creditResponse = sender.send("ISSUER", "CD", creditRequest, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(creditResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.tranDetails = creditResponse.get(127);
			response.responseCode = creditResponse.get(39);
			response.authCode = creditResponse.get(38);
			if("000".equals(creditResponse.get(39))) {
				response.responseMessage = "SUCCESS";
				String balances = creditResponse.get(48).substring(0, 34);
				String ledger = balances.substring(5, 17);
				String available = balances.substring(22, 34);
				response.balance = "0001356" + (balances.charAt(0) == '+' ? "C" : "D") + ledger;
				response.balance += "0001356" + (balances.charAt(17) == '+' ? "C" : "D") + available;
				response.operatedAccount = creditResponse.get(103);
				response.customerName = "NAME";
				response.authCode = creditResponse.get(38);
			}
			else {
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (ISO8583Exception e) {
			if(e.cause == ISO8583ExceptionCause.SOCKET_CONNECT_ERROR) {
				return Mono.just(new CBSResponse("91", "connect error."));
			}
			logger.error("error connecting to CBS.", e);
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;
	}


	@Override
	public Mono<CBSResponse> debit(ReqPay reqPay) {
		try {
			final Cred cred = reqPay.getPayer().getCreds().getCred().get(0);
			final UIDAIAuthCode uidaiAuthCode = new UIDAIAuthCode(cred.getData().getValue());
			final ISO8583Message debitRequest = new ISO8583Message();
			final LocalDateTime currentTime = Converter.dateToLocalDateTime(reqPay.getTxn().getTs());
			final Map<DeviceTagNameType, String> deviceTagMap = DeviceTagMap.toMap(reqPay.getPayer().getDevice().getTag());
			final AadharAccount account = reqPay.getPayer().getAc().getDetail().stream()
										.collect(AadharAccountCollector.getInstance());

			final BigDecimal amount = reqPay.getPayer().getAmount().getValue();
			final String formattedAmount = Generator.amountToFormattedString16(amount.multiply(hundred).toBigInteger());
			debitRequest.put(0, "1200");
			debitRequest.put(2, account.uidVid);
			debitRequest.put(3, "400000");
			debitRequest.put(4, formattedAmount);
			debitRequest.put(11, reqPay.getTxn().getCustRef());
			debitRequest.put(12, currentTime.format(datTimeFormatter));
			debitRequest.put(17, currentTime.format(dateFormatter));
			debitRequest.put(24, "200");
			debitRequest.put(32, appConfig.orgId);
			debitRequest.put(37, reqPay.getTxn().getCustRef());
			debitRequest.put(41, deviceTagMap.get(DeviceTagNameType.CARD_ACCP_TR_ID));
			debitRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			debitRequest.put(49, "INR");
			debitRequest.put(63, "AEPS");
			debitRequest.put(102, "013        0000    110000002901");
			debitRequest.put(103, "  013        0000    110000002901");
			debitRequest.put(123, "AEP");
			String purpose = reqPay.getTxn().getPurpose();
			String txnType = Purpose.CASH_WITHDRAWAL.equals(purpose) ? "CW" : "PT";
			if(Purpose.CASH_WITHDRAWAL.equals(purpose)) {
				debitRequest.put(126, "AEPS_RONUS_CBS_REM_CWD_PMT");
			}
			else if(Purpose.PURCHASE.equals(purpose)) {
				debitRequest.put(126, "AEPS_RONUS_CBS_REM_PUR_PMT");
			}
			else throw new RuntimeException("invalid purpose code : "+reqPay.getTxn().getPurpose()+" for debit.");
			debitRequest.put(127, "U~"+uidaiAuthCode.authToken+"~null");
			logger.info(ISO8583Loggging.log(debitRequest).toString());
			final ISO8583Message debitResponse = sender.send("ISSUER", txnType, debitRequest, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(debitResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.tranDetails = debitResponse.get(127);
			response.responseCode = debitResponse.get(39);
			response.authCode = debitResponse.get(38);
			if("000".equals(debitResponse.get(39))) {
				response.responseMessage = "SUCCESS";
				String balances = debitResponse.get(48).substring(0, 34);
				String ledger = balances.substring(5, 17);
				String available = balances.substring(22, 34);
				response.balance = "0001356" + (balances.charAt(0) == '+' ? "C" : "D") + ledger;
				response.balance += "0001356" + (balances.charAt(17) == '+' ? "C" : "D") + available;
				response.authCode = debitResponse.get(38);
			}
			else {
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (ISO8583Exception e) {
			if(e.cause == ISO8583ExceptionCause.SOCKET_CONNECT_ERROR) {
				return Mono.just(new CBSResponse("91", "connect error."));
			}
			logger.error("error connecting to CBS.", e);
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> debitReversal(final ReqPay reqPay, IssuerTransaction original) {
		try {
			logger.info("original : "+original.getMsgId()+":"+original.getTxnType()+":"+original.getTxnSubType());
			final UIDAIAuthCode uidaiAuthCode = new UIDAIAuthCode(original.getPayerCredData());
			final ISO8583Message debitRequest = new ISO8583Message();
			final LocalDateTime currentTime = Converter.dateToLocalDateTime(original.getTxnTs());
			final LocalDateTime txnTime = Converter.dateToLocalDateTime(reqPay.getTxn().getTs());
			final AadharAccount account = reqPay.getPayer().getAc().getDetail().stream()
										.collect(AadharAccountCollector.getInstance());
			final Map<DeviceTagNameType, String> tagMap =  DeviceTagMap.toMap(original.getPayerDeviceDetails());
			final BigDecimal amount = reqPay.getPayer().getAmount().getValue();
			final String formattedAmount = Generator.amountToFormattedString16(amount.multiply(hundred).toBigInteger());
			debitRequest.put(0, "1420");
			if("REPEAT_REVERSAL".equalsIgnoreCase(reqPay.getTxn().getNote())) debitRequest.put(0, "1421");
			debitRequest.put(2, account.uidVid);
			debitRequest.put(3, "400000");
			debitRequest.put(4, formattedAmount);
			debitRequest.put(11, reqPay.getTxn().getCustRef());
			debitRequest.put(12, currentTime.format(datTimeFormatter));
			debitRequest.put(17, currentTime.format(dateFormatter));
			debitRequest.put(24, "400");
			debitRequest.put(32, appConfig.orgId);
			debitRequest.put(37, reqPay.getTxn().getCustRef());
			debitRequest.put(41, tagMap.get(DeviceTagNameType.CARD_ACCP_TR_ID));
			debitRequest.put(43, tagMap.get(DeviceTagNameType.LOCATION));
			debitRequest.put(49, "INR");
			debitRequest.put(56, "1200" + reqPay.getTxn().getCustRef() + txnTime.format(datTimeFormatter) + "06" + appConfig.orgId);
			debitRequest.put(63, "AEPS");
			debitRequest.put(102, "013        0000    110000002901");
			debitRequest.put(103, "  013        0000    110000002901");
			debitRequest.put(123, "AEP");
			if(Purpose.CASH_WITHDRAWAL.equals(reqPay.getTxn().getPurpose())) debitRequest.put(126, "AEPS_RONUS_CBS_REM_CWD_PMT_RVSL");
			else if(Purpose.PURCHASE.equals(reqPay.getTxn().getPurpose()))  debitRequest.put(126, "AEPS_RONUS_CBS_REM_PUR_PMT_RVSL");
			else throw new RuntimeException("invalid reversal : "+reqPay.getTxn().getPurpose());
			debitRequest.put(127, "U~"+uidaiAuthCode.authToken+"~null");
			logger.info(ISO8583Loggging.log(debitRequest).toString());
			final ISO8583Message debitResponse = sender.send("ISSUER", "REV", debitRequest, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(debitResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.tranDetails = debitResponse.get(127);
			response.responseCode = debitResponse.get(39);
			response.authCode = debitResponse.get(38);
			if("000".equals(debitResponse.get(39))) {
				response.responseMessage = "SUCCESS";
				String balances = debitResponse.get(48).substring(0, 34);
				String ledger = balances.substring(5, 17);
				String available = balances.substring(22, 34);
				response.balance = "0001356" + (balances.charAt(0) == '+' ? "C" : "D") + ledger;
				response.balance += "0001356" + (balances.charAt(17) == '+' ? "C" : "D") + available;
			}
			else {
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (ISO8583Exception e) {
			if(e.cause == ISO8583ExceptionCause.SOCKET_CONNECT_ERROR) {
				return Mono.just(new CBSResponse("91", "connect error."));
			}
			logger.error("error connecting to CBS.", e);
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;

	}

}
