package com.fss.aeps.cbsclient;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.util.iso8583.ISO8583Loggging;
import org.util.iso8583.ISO8583Message;
import org.util.iso8583.api.ISOFormat;

import com.fss.aeps.AppConfig;
import com.fss.aeps.constants.ContextKey;
import com.fss.aeps.jaxb.DeviceTagNameType;
import com.fss.aeps.jaxb.ReqBioAuth;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jaxb.RespBioAuth;
import com.fss.aeps.jpa.acquirer.AcquirerTransaction;
import com.fss.aeps.util.AadharAccount;
import com.fss.aeps.util.AadharAccountCollector;
import com.fss.aeps.util.Converter;
import com.fss.aeps.util.DeviceTagMap;
import com.fss.aeps.util.ExceptionUtil;
import com.fss.aeps.util.Generator;

import reactor.core.publisher.Mono;

@Component
public class AcquirerFinacleClient implements AcquirerCbsClient {

	private static final Logger logger 						= LoggerFactory.getLogger(AcquirerFinacleClient.class);
	private static final BigDecimal hundred 				= new BigDecimal(100.00);
	private static final ISOFormat isoFormat 				= FinacleFormat.getInstance();
	private static final DateTimeFormatter dateFormatter 	= DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final DateTimeFormatter datTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private ISO8583MessageSender sender;

	@Override
	public Mono<CBSResponse> debitFT(ReqPay reqPay, RespBioAuth respBioAuth) {
		try {
			final String info = respBioAuth.getUidaiData().getInfo();
			final String uidaiToken = info.substring(info.indexOf("{")+1, info.indexOf(","));
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
			String accId = deviceTagMap.get(DeviceTagNameType.CARD_ACC_ID_CODE);
			String trId = accId.substring(accId.length()-8);
			debitRequest.put(41, trId);
			debitRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			debitRequest.put(49, "INR");
			debitRequest.put(63, "AEPS");
			debitRequest.put(102, "013        0000    110000002901");
			debitRequest.put(103, "  013        0000    110000002901");
			debitRequest.put(123, "AEP");
			String reconIndicator = (String) reqPay.context.get(ContextKey.RECON_INDICATOR);
			String agentDetails = (String) reqPay.context.get(ContextKey.AGENT_DETAILS);
			if(agentDetails == null) throw new RuntimeException("agentDetails is null in accountingCW");
			if(reconIndicator == null) throw new RuntimeException("reconIndicator is null in accountingCW");
			debitRequest.put(125, agentDetails);
			debitRequest.put(126, reconIndicator);
			debitRequest.put(127, "U~"+uidaiToken);
			logger.info(ISO8583Loggging.log(debitRequest).toString());
			final ISO8583Message debitResponse = sender.send("ACQUIRER", "Fund Transfer", debitRequest, isoFormat, appConfig.cbsReadTimeout);
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
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> debitFTReversal(ReqPay request, RespBioAuth respBioAuth) {
		try {
			final String info = respBioAuth.getUidaiData().getInfo();
			final String uidaiToken = info.substring(info.indexOf("{")+1, info.indexOf(","));
			final ISO8583Message reversal = new ISO8583Message();
			final LocalDateTime currentTime = Converter.dateToLocalDateTime(request.getTxn().getTs());
			final Map<DeviceTagNameType, String> deviceTagMap = DeviceTagMap.toMap(request.getPayer().getDevice().getTag());
			final AadharAccount account = request.getPayer().getAc().getDetail().stream()
										.collect(AadharAccountCollector.getInstance());

			final BigDecimal amount = request.getPayer().getAmount().getValue();
			final String formattedAmount = Generator.amountToFormattedString16(amount.multiply(hundred).toBigInteger());
			reversal.put(0, "1420");
			reversal.put(2, account.uidVid);
			reversal.put(3, "400000");
			reversal.put(4, formattedAmount);
			reversal.put(11, request.getTxn().getCustRef());
			reversal.put(12, currentTime.format(datTimeFormatter));
			reversal.put(17, currentTime.format(dateFormatter));
			reversal.put(24, "400");
			reversal.put(32, appConfig.orgId);
			reversal.put(37, request.getTxn().getCustRef());
			String accId = deviceTagMap.get(DeviceTagNameType.CARD_ACC_ID_CODE);
			String trId = accId.substring(accId.length()-8);
			reversal.put(41, trId);
			reversal.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			reversal.put(49, "INR");
			reversal.put(56, "1200" + request.getTxn().getCustRef() + new SimpleDateFormat("yyyyMMddHHmmss").format(request.getTxn().getTs()) + "06" + appConfig.orgId);
			reversal.put(63, "AEPS");
			reversal.put(102, "013        0000    110000002901");
			reversal.put(103, "  013        0000    110000002901");
			reversal.put(123, "AEP");
			String reconIndicator = (String) request.context.get(ContextKey.RECON_INDICATOR);
			String agentDetails = (String) request.context.get(ContextKey.AGENT_DETAILS);
			reversal.put(125, agentDetails);
			reversal.put(126, reconIndicator);
			reversal.put(127, "U~"+uidaiToken);
			if(agentDetails == null) throw new RuntimeException("agentDetails is null in accountingCW");
			if(reconIndicator == null) throw new RuntimeException("reconIndicator is null in accountingCW");
			reversal.put(126, reconIndicator+"_RVSL");
			reversal.put(127, agentDetails);
			logger.info(ISO8583Loggging.log(reversal).toString());
			final ISO8583Message debitResponse = sender.send("ACQUIRER", "REV", reversal, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(debitResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.tranDetails = debitResponse.get(127);
			response.authCode = debitResponse.get(38);
			response.responseCode = debitResponse.get(39);
			if("000".equals(debitResponse.get(39))) {
				response.responseMessage = "SUCCESS";
			}
			else {
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> accountingCW(ReqPay reqPay) {
		try {
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
			String accId = deviceTagMap.get(DeviceTagNameType.CARD_ACC_ID_CODE);
			String trId = accId.substring(accId.length()-8);
			debitRequest.put(41, trId);
			debitRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			debitRequest.put(49, "INR");
			debitRequest.put(63, "AEPS");
			debitRequest.put(102, "013        0000    110000002901");
			debitRequest.put(103, "  013        0000    110000002901");
			debitRequest.put(123, "AEP");
			debitRequest.put(125, "002CW004MATM003ACQ004AEPS");
			String reconIndicator = (String) reqPay.context.get(ContextKey.RECON_INDICATOR);
			String agentDetails = (String) reqPay.context.get(ContextKey.AGENT_DETAILS);
			if(agentDetails == null) throw new RuntimeException("agentDetails is null in accountingCW");
			if(reconIndicator == null) throw new RuntimeException("reconIndicator is null in accountingCW");
			debitRequest.put(126, reconIndicator);
			debitRequest.put(127, agentDetails);
			logger.info(ISO8583Loggging.log(debitRequest).toString());
			final ISO8583Message debitResponse = sender.send("ACQUIRER", "Cash Withdrawal", debitRequest, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(debitResponse).toString());
			final CBSResponse response = new CBSResponse();
			if("000".equals(debitResponse.get(39))) {
				response.tranDetails = debitResponse.get(127);
				response.responseCode = debitResponse.get(39);
				response.responseMessage = "SUCCESS";
				String balances = debitResponse.get(48).substring(0, 34);
				String ledger = balances.substring(5, 17);
				String available = balances.substring(22, 34);
				response.balance = "0001356" + (balances.charAt(0) == '+' ? "C" : "D") + ledger;
				response.balance += "0001356" + (balances.charAt(17) == '+' ? "C" : "D") + available;
			}
			else {
				response.responseCode = debitResponse.get(39);
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (Exception e) {
			logger.error("error processing cash withdrawal accounting at CBS.", e);
		}
		return Mono.empty();

	}

	@Override
	public Mono<CBSResponse> accountingCW(AcquirerTransaction transaction) {
		try {
			final ISO8583Message debitRequest = new ISO8583Message();
			final LocalDateTime currentTime = Converter.dateToLocalDateTime(transaction.getTxnTs());
			final Map<DeviceTagNameType, String> deviceTagMap = DeviceTagMap.toMap(transaction.getPayerDeviceDetails());

			final BigDecimal amount = transaction.getPayerAmount();
			final String formattedAmount = Generator.amountToFormattedString16(amount.multiply(hundred).toBigInteger());
			debitRequest.put(0, "1200");
			debitRequest.put(2, transaction.getPayerAcUidnumVid());
			debitRequest.put(3, "400000");
			debitRequest.put(4, formattedAmount);
			debitRequest.put(11, transaction.getCustRef());
			debitRequest.put(12, currentTime.format(datTimeFormatter));
			debitRequest.put(17, currentTime.format(dateFormatter));
			debitRequest.put(24, "200");
			debitRequest.put(32, appConfig.orgId);
			debitRequest.put(37, transaction.getCustRef());
			String accId = deviceTagMap.get(DeviceTagNameType.CARD_ACC_ID_CODE);
			String trId = accId.substring(accId.length()-8);
			debitRequest.put(41, trId);
			debitRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			debitRequest.put(49, "INR");
			debitRequest.put(63, "AEPS");
			debitRequest.put(102, "013        0000    110000002901");
			debitRequest.put(103, "  013        0000    110000002901");
			debitRequest.put(123, "AEP");
			debitRequest.put(125, "002CW004MATM003ACQ004AEPS");
			String reconIndicator = transaction.getReconIndicator();
			String agentDetails = transaction.getAgentDetails();
			if(agentDetails == null) throw new RuntimeException("agentDetails is null in accountingCW");
			if(reconIndicator == null) throw new RuntimeException("reconIndicator is null in accountingCW");
			debitRequest.put(126, reconIndicator);
			debitRequest.put(127, agentDetails);
			logger.info(ISO8583Loggging.log(debitRequest).toString());
			final ISO8583Message debitResponse = sender.send("ACQUIRER", "CW", debitRequest, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(debitResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.authCode = debitResponse.get(38);
			response.tranDetails = debitResponse.get(127);
			response.responseCode = debitResponse.get(39);
			if("000".equals(debitResponse.get(39))) {
				response.responseMessage = "SUCCESS";
				String balances = debitResponse.get(48).substring(0, 34);
				String ledger = balances.substring(5, 17);
				String available = balances.substring(22, 34);
				response.balance = "0001356" + (balances.charAt(0) == '+' ? "C" : "D") + ledger;
				response.balance += "0001356" + (balances.charAt(17) == '+' ? "C" : "D") + available;
			}
			else {
				response.responseCode = debitResponse.get(39);
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (Exception e) {
			transaction.setException(ExceptionUtil.appendBlob(transaction.getException(), e));
			logger.error("error processing cash withdrawal accounting at CBS.", e);
		}
		return Mono.empty();

	}


	@Override
	public Mono<CBSResponse> accountingPT(ReqPay reqPay) {
		try {
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
			String accId = deviceTagMap.get(DeviceTagNameType.CARD_ACC_ID_CODE);
			String trId = accId.substring(accId.length()-8);
			debitRequest.put(41, trId);
			debitRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			debitRequest.put(49, "INR");
			debitRequest.put(63, "AEPS");
			debitRequest.put(102, "013        0000    110000002901");
			debitRequest.put(103, "  013        0000    110000002901");
			debitRequest.put(123, "AEP");
			debitRequest.put(125, "002CW004MATM003ACQ004AEPS");
			String reconIndicator = (String) reqPay.context.get(ContextKey.RECON_INDICATOR);
			String agentDetails = (String) reqPay.context.get(ContextKey.AGENT_DETAILS);
			if(agentDetails == null) throw new RuntimeException("agentDetails is null in accountingCW");
			if(reconIndicator == null) throw new RuntimeException("reconIndicator is null in accountingCW");
			debitRequest.put(126, reconIndicator);
			debitRequest.put(127, agentDetails);
			logger.info(ISO8583Loggging.log(debitRequest).toString());
			final ISO8583Message debitResponse = sender.send("ACQUIRER", "PT", debitRequest, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(debitResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.tranDetails = debitResponse.get(127);
			response.responseCode = debitResponse.get(39);
			response.authCode = debitResponse.get(38);
			if("000".equals(debitResponse.get(39))) {
				reqPay.context.put("MERCHANT_AMNT_DETS", debitResponse.get(127));
				response.responseMessage = "SUCCESS";
				String balances = debitResponse.get(48).substring(0, 34);
				String ledger = balances.substring(5, 17);
				String available = balances.substring(22, 34);
				response.balance = "0001356" + (balances.charAt(0) == '+' ? "C" : "D") + ledger;
				reqPay.context.put("MER_AVAIL_BAL", (balances.charAt(17) == '+' ? "C" : "D") + available);
				response.balance += "0001356" + (balances.charAt(17) == '+' ? "C" : "D") + available;

			}
			else {
				response.responseCode = debitResponse.get(39);
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;

	}


	@Override
	public Mono<CBSResponse> accountingCD(AcquirerTransaction transaction) {
		try {
			final ISO8583Message debitRequest = new ISO8583Message();
			final LocalDateTime currentTime = Converter.dateToLocalDateTime(transaction.getTxnTs());
			final Map<DeviceTagNameType, String> deviceTagMap = DeviceTagMap.toMap(DeviceTagMap.toTagList(transaction.getPayerDeviceDetails()));
			final BigDecimal amount = transaction.getPayerAmount();
			final String formattedAmount = Generator.amountToFormattedString16(amount.multiply(hundred).toBigInteger());
			debitRequest.put(0, "1200");
			debitRequest.put(2, transaction.getPayerAcUidnumVid());
			debitRequest.put(3, "400000");
			debitRequest.put(4, formattedAmount);
			debitRequest.put(11, transaction.getCustRef());
			debitRequest.put(12, currentTime.format(datTimeFormatter));
			debitRequest.put(17, currentTime.format(dateFormatter));
			debitRequest.put(24, "200");
			debitRequest.put(32, appConfig.orgId);
			debitRequest.put(37, transaction.getCustRef());
			String accId = deviceTagMap.get(DeviceTagNameType.CARD_ACC_ID_CODE);
			String trId = accId.substring(accId.length()-8);
			debitRequest.put(41, trId);
			debitRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			debitRequest.put(49, "INR");
			debitRequest.put(63, "AEPS");
			debitRequest.put(102, "013        0450    06520192025166");
			debitRequest.put(103, "  013        0450    06520192025166");
			debitRequest.put(123, "AEP");
			debitRequest.put(125, "002CD004MATM003ACQ004AEPS");
			debitRequest.put(126, transaction.getReconIndicator()); //AEPS_OFFUS_CBS_BEN_CDP_PMT
			debitRequest.put(127, transaction.getAgentDetails());
			logger.info(ISO8583Loggging.log(debitRequest).toString());
			final ISO8583Message debitResponse = sender.send("ACQUIRER", "CD", debitRequest, isoFormat, appConfig.cbsReadTimeout);
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
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;

	}

	@Override
	public Mono<CBSResponse> debitCreditOnus(ReqBioAuth reqBioAuth, RespBioAuth respBioAuth) {
		try {
			final String info = respBioAuth.getUidaiData().getInfo();
			final String uidaiAuthCode = info.substring(info.indexOf("{")+1, info.indexOf(","));
			final ISO8583Message debitRequest = new ISO8583Message();
			final LocalDateTime currentTime = Converter.dateToLocalDateTime(reqBioAuth.getTxn().getTs());
			final Map<DeviceTagNameType, String> deviceTagMap = DeviceTagMap.toMap(reqBioAuth.getPayer().getDevice().getTag());
			final AadharAccount account = reqBioAuth.getPayer().getAc().getDetail().stream()
										.collect(AadharAccountCollector.getInstance());

			final BigDecimal amount = reqBioAuth.getPayer().getAmount().getValue();
			final String formattedAmount = Generator.amountToFormattedString16(amount.multiply(hundred).toBigInteger());
			debitRequest.put(0, "1200");
			debitRequest.put(2, account.uidVid);
			debitRequest.put(3, "400000");
			debitRequest.put(4, formattedAmount);
			debitRequest.put(11, reqBioAuth.getTxn().getCustRef());
			debitRequest.put(12, currentTime.format(datTimeFormatter));
			debitRequest.put(17, currentTime.format(dateFormatter));
			debitRequest.put(24, "200");
			debitRequest.put(32, appConfig.orgId);
			debitRequest.put(37, reqBioAuth.getTxn().getCustRef());
			String accId = deviceTagMap.get(DeviceTagNameType.CARD_ACC_ID_CODE);
			String trId = accId.substring(accId.length()-8);
			debitRequest.put(41, trId);
			debitRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			debitRequest.put(49, "INR");
			debitRequest.put(63, "AEPS");
			debitRequest.put(102, "013        0000    110000002901");
			debitRequest.put(103, "  013        0000    110000002901");
			debitRequest.put(123, "AEP");
			String reconIndicator = (String) reqBioAuth.context.get(ContextKey.RECON_INDICATOR);
			String agentDetails = (String) reqBioAuth.context.get(ContextKey.AGENT_DETAILS);
			if(agentDetails == null) throw new RuntimeException("agentDetails is null in accountingCW");
			if(reconIndicator == null) throw new RuntimeException("reconIndicator is null in accountingCW");
			debitRequest.put(125, "U~"+uidaiAuthCode);
			debitRequest.put(126, reconIndicator);
			debitRequest.put(127, agentDetails);
			logger.info(ISO8583Loggging.log(debitRequest).toString());
			final ISO8583Message debitResponse = sender.send("ONUS", "PT_ONUS", debitRequest, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(debitResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.responseCode = debitResponse.get(39);
			response.tranDetails = debitResponse.get(127);
			response.authCode = debitResponse.get(38);
			if("000".equals(debitResponse.get(39))) {
				reqBioAuth.context.put("MERCHANT_AMNT_DETS", debitResponse.get(127));
				response.responseMessage = "SUCCESS";
				String balances = debitResponse.get(48).substring(0, 34);
				String ledger = balances.substring(5, 17);
				String available = balances.substring(22, 34);
				response.balance = "0001356" + (balances.charAt(0) == '+' ? "C" : "D") + ledger;
				reqBioAuth.context.put("MER_AVAIL_BAL", (balances.charAt(17) == '+' ? "C" : "D") + available);
				response.balance += "0001356" + (balances.charAt(17) == '+' ? "C" : "D") + available;
			}
			else {
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return Mono.just(new CBSResponse("91", "Error processing at CBS"));
	}

	@Override
	public Mono<CBSResponse> debitCreditOnusReversal(ReqBioAuth reqBioAuth, RespBioAuth respBioAuth) {
		try {
			final String info = respBioAuth.getUidaiData().getInfo();
			final String uidaiAuthCode = info.substring(info.indexOf("{")+1, info.indexOf(","));
			final ISO8583Message debitRequest = new ISO8583Message();
			final LocalDateTime currentTime = Converter.dateToLocalDateTime(reqBioAuth.getTxn().getTs());
			final Map<DeviceTagNameType, String> deviceTagMap = DeviceTagMap.toMap(reqBioAuth.getPayer().getDevice().getTag());
			final AadharAccount account = reqBioAuth.getPayer().getAc().getDetail().stream()
										.collect(AadharAccountCollector.getInstance());

			final BigDecimal amount = reqBioAuth.getPayer().getAmount().getValue();
			final String formattedAmount = Generator.amountToFormattedString16(amount.multiply(hundred).toBigInteger());
			debitRequest.put(0, "1420");
			debitRequest.put(2, account.uidVid);
			debitRequest.put(3, "400000");
			debitRequest.put(4, formattedAmount);
			debitRequest.put(11, reqBioAuth.getTxn().getCustRef());
			debitRequest.put(12, currentTime.format(datTimeFormatter));
			debitRequest.put(17, currentTime.format(dateFormatter));
			debitRequest.put(24, "400");
			debitRequest.put(32, appConfig.orgId);
			debitRequest.put(37, reqBioAuth.getTxn().getCustRef());
			String accId = deviceTagMap.get(DeviceTagNameType.CARD_ACC_ID_CODE);
			String trId = accId.substring(accId.length()-8);
			debitRequest.put(41, trId);
			debitRequest.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			debitRequest.put(49, "INR");
			debitRequest.put(56, "1200" + reqBioAuth.getTxn().getCustRef() + new SimpleDateFormat("yyyyMMddHHmmss").format(reqBioAuth.getTxn().getTs()) + "06" + appConfig.orgId);
			debitRequest.put(63, "AEPS");
			debitRequest.put(102, "013        0000    110000002901");
			debitRequest.put(103, "  013        0000    110000002901");
			debitRequest.put(123, "AEP");
			String reconIndicator = (String) reqBioAuth.context.get(ContextKey.RECON_INDICATOR);
			String agentDetails = (String) reqBioAuth.context.get(ContextKey.AGENT_DETAILS);
			if(agentDetails == null) throw new RuntimeException("agentDetails is null in accountingCW");
			if(reconIndicator == null) throw new RuntimeException("reconIndicator is null in accountingCW");
			debitRequest.put(125, "U~"+uidaiAuthCode);
			debitRequest.put(126, reconIndicator+"_RVSL");
			debitRequest.put(127, agentDetails);
			logger.info(ISO8583Loggging.log(debitRequest).toString());
			final ISO8583Message debitResponse = sender.send("ONUS", "PT_ONUS_REV", debitRequest, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(debitResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.tranDetails = debitResponse.get(127);
			response.authCode = debitResponse.get(38);
			response.responseCode = debitResponse.get(39);
			if("000".equals(debitResponse.get(39))) {
				//reqBioAuth.context.put("MERCHANT_AMNT_DETS", debitResponse.get(127));
				response.responseMessage = "SUCCESS";
			}
			else {
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return Mono.just(new CBSResponse("91", "Error processing at CBS"));
	}

	@Override
	public Mono<CBSResponse> accountingReversal(AcquirerTransaction transaction) {
		try {
			final ISO8583Message reversal = new ISO8583Message();
			final LocalDateTime currentTime = Converter.dateToLocalDateTime(transaction.getTxnTs());
			final Map<DeviceTagNameType, String> deviceTagMap = DeviceTagMap.toMap(transaction.getPayerDeviceDetails());
			final BigDecimal amount = transaction.getPayerAmount();
			final String formattedAmount = Generator.amountToFormattedString16(amount.multiply(hundred).toBigInteger());
			reversal.put(0, "1420");
			reversal.put(2, transaction.getPayerAcUidnumVid());
			reversal.put(3, "400000");
			reversal.put(4, formattedAmount);
			reversal.put(11, transaction.getCustRef());
			reversal.put(12, currentTime.format(datTimeFormatter));
			reversal.put(17, currentTime.format(dateFormatter));
			reversal.put(24, "400");
			reversal.put(32, appConfig.orgId);
			reversal.put(37, transaction.getCustRef());
			String accId = deviceTagMap.get(DeviceTagNameType.CARD_ACC_ID_CODE);
			String trId = accId.substring(accId.length()-8);
			reversal.put(41, trId);
			reversal.put(43, deviceTagMap.get(DeviceTagNameType.LOCATION));
			reversal.put(49, "INR");
			reversal.put(56, "1200" + transaction.getCustRef() + new SimpleDateFormat("yyyyMMddHHmmss").format(transaction.getTxnTs()) + "06" + appConfig.orgId);
			reversal.put(63, "AEPS");
			reversal.put(102, "013        0000    110000002901");
			reversal.put(103, "  013        0000    110000002901");
			reversal.put(123, "AEP");
			reversal.put(125, "002CW004MATM003ACQ004AEPS");
			String reconIndicator = transaction.getReconIndicator();
			String agentDetails = transaction.getAgentDetails();
			if(agentDetails == null) throw new RuntimeException("agentDetails is null in accountingCW");
			if(reconIndicator == null) throw new RuntimeException("reconIndicator is null in accountingCW");
			reversal.put(126, reconIndicator+"_RVSL");
			reversal.put(127, agentDetails);
			logger.info(ISO8583Loggging.log(reversal).toString());
			final ISO8583Message debitResponse = sender.send("ACQUIRER", "REV", reversal, isoFormat, appConfig.cbsReadTimeout);
			logger.info(ISO8583Loggging.log(debitResponse).toString());
			final CBSResponse response = new CBSResponse();
			response.authCode = debitResponse.get(38);
			response.tranDetails = debitResponse.get(127);
			response.responseCode = debitResponse.get(39);
			if("000".equals(debitResponse.get(39))) {
				response.responseMessage = "SUCCESS";
			}
			else {
				response.responseMessage = "FAILURE";
			}
			return Mono.just(response);
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;
	}

}
