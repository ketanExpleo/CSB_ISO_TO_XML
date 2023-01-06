package com.fss.aeps.cbsclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.tempuri.IService1;
import org.tempuri.Service1;

import com.fss.aeps.AppConfig;
import com.fss.aeps.http.filters.LoggingHandler;
import com.fss.aeps.jaxb.AccountDetailType;
import com.fss.aeps.jaxb.DeviceTagNameType;
import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jpa.acquirer.AcquirerTransaction;
import com.fss.aeps.jpa.issuer.IssuerTransaction;
import com.fss.aeps.util.DeviceTagMap;
import com.fss.aeps.util.Generator;
import com.fss.aeps.util.Mapper;
import com.fss.aeps.util.UIDAIAuthCode;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import reactor.core.publisher.Mono;

@Component
public class CSBCbsClient implements CbsClient {

	private static final Logger logger = LoggerFactory.getLogger(CSBCbsClient.class);

	private static final JAXBContext CONTEXT = getJaxbContext();

	@Autowired
	@Qualifier("cbsToNpciResponseMapper")
	protected Mapper cbsToNpciResponseMapper;
	
	@Autowired
	protected AppConfig appConfig;
	
	private static IService1 getService() {
		try {
			final Service1     service  = new Service1(new ClassPathResource("ini/csb.wsdl").getURL());
			final IService1 serviceSoap = service.getBasicHttpBindingIService1();
			final BindingProvider bindingProvider = (BindingProvider)serviceSoap;
			final Binding binding = bindingProvider.getBinding();
			final Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put("com.sun.xml.ws.request.timeout", 10000); // Timeout in millis
			requestContext.put("com.sun.xml.ws.connect.timeout", 100000); // Timeout in millis
			binding.setHandlerChain(List.of(new LoggingHandler()));
			return serviceSoap;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static JAXBContext getJaxbContext() {
		try {
			return JAXBContext.newInstance(Details.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> issuerBE(ReqBalEnq reqBalEnq) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			IService1 service = getService();
			Details details = new Details();
			logger.info("authToken : "+reqBalEnq.getPayer().getCreds().getCred().get(0).getData().getValue());
			String uidToken = new UIDAIAuthCode(reqBalEnq.getPayer().getCreds().getCred().get(0).getData().getValue()).authToken;
			String cardAccId = reqBalEnq.getPayer().getDevice().getTag().stream()
					.filter(d -> d.getName() == DeviceTagNameType.AGENT_ID).findFirst().get().getValue();
			//details.Aadhaar_Number = "dbab683e-056d-461c-8c23-72975958e832";
			details.Aadhaar_Number = getBenRefId(uidToken);
			details.Account_Type = "1";
			//details.Agent_ID = "20020002";
			details.Agent_ID = cardAccId;
			details.BC_ID = "103";
			details.Channel_Type = "1";
			details.Device_ID = "register";
			details.IIN = "607082";
			details.Network_Type = "3";
			details.Transaction_Code = "1";
			details.Transaction_Type = "003";
			details.Txn_Amount = new BigDecimal(0.00);
			details.Txn_Currency = "INR";
			details.Txn_Timestamp = sdf.format(reqBalEnq.getTxn().getTs());
			details.Txn_ID = reqBalEnq.getTxn().getCustRef();
			StringWriter writer = new StringWriter();
			CONTEXT.createMarshaller().marshal(details, writer);
			String responseString = service.aepsAadhaarTransaction(writer.toString());
			Details response = (Details) CONTEXT.createUnmarshaller()
					.unmarshal(new ByteArrayInputStream(responseString.getBytes()));
			final CBSResponse cbsResponse = new CBSResponse();
			cbsResponse.authCode = response.Auth_ID;
			cbsResponse.customerName = response.Customer_Name;
			logger.info("Error_Code :: {}", response.Error_Code);
			cbsResponse.responseCode = cbsToNpciResponseMapper.map(response.Error_Code);
			if(cbsResponse.responseCode.length() > 2) cbsResponse.responseCode = "KH";
			if ("ERR000".equals(response.Error_Code)) {
				logger.info("before conversion Balance AMT :: {}", response.Response_XML.Bal.Amt);
				BigDecimal bal = response.Response_XML.Bal.Amt;
				String amount = Generator.amountToFormattedString12(bal.toBigInteger());
				if (amount != null) {
					cbsResponse.balance = "0001356" + (Integer.parseInt(amount) < 0 ? "D" : "C") + amount;
				}
				cbsResponse.responseMessage = "SUCCESS";
			} else {
				cbsResponse.responseMessage = "FAILURE";
			}
			return Mono.just(cbsResponse);
		} catch (Exception e) {
			logger.error("error processing balance enquiry request at CBS.", e);
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> issuerMS(ReqBalEnq reqBalEnq) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			IService1 service = getService();
			Details details = new Details();
			String uidToken = new UIDAIAuthCode(reqBalEnq.getPayer().getCreds().getCred().get(0).getData().getValue()).authToken;
			String cardAccId = reqBalEnq.getPayer().getDevice().getTag().stream()
					.filter(d -> d.getName() == DeviceTagNameType.AGENT_ID).findFirst().get().getValue();
			details.Aadhaar_Number = getBenRefId(uidToken);
			details.Account_Type = "1";
			details.Agent_ID = cardAccId;
			details.BC_ID = "103";
			details.Channel_Type = "1";
			details.Device_ID = "register";
			details.IIN = "607082";
			details.Network_Type = "3";
			details.Transaction_Code = "1";
			details.Transaction_Type = "004";
			details.Txn_Amount = new BigDecimal(0.00);
			details.Txn_Currency = "INR";
			details.Txn_Timestamp = sdf.format(reqBalEnq.getTxn().getTs());
			details.Txn_ID = reqBalEnq.getTxn().getCustRef();
			StringWriter writer = new StringWriter();
			CONTEXT.createMarshaller().marshal(details, writer);
			String responseString = service.aepsAadhaarTransaction(writer.toString());
			Details response = (Details) CONTEXT.createUnmarshaller()
					.unmarshal(new ByteArrayInputStream(responseString.getBytes()));
			final CBSResponse cbsResponse = new CBSResponse();
			cbsResponse.authCode = response.Auth_ID;
			cbsResponse.customerName = response.Customer_Name;
			cbsResponse.responseCode = cbsToNpciResponseMapper.map(response.Error_Code);
			if(cbsResponse.responseCode.length() > 2) cbsResponse.responseCode = "KH";
			if ("ERR000".equals(response.Error_Code)) {
				logger.info("before conversion Balance AMT :: {}", response.Response_XML.MiniStmt.Bal.Amt);
				BigDecimal bal = response.Response_XML.MiniStmt.Bal.Amt;
				String amount = Generator.amountToFormattedString12(bal.toBigInteger());
				if (amount != null) {
					cbsResponse.balance = "0001356" + (Integer.parseInt(amount) < 0 ? "D" : "C") + amount;
					logger.info("after conversion Balance AMT :: {}", cbsResponse.balance);
				}
				List<Row> rowList = response.Response_XML.MiniStmt.row;
				List<String> statementList = getStatementList(rowList);
				cbsResponse.statement = statementList;
				cbsResponse.responseMessage = "SUCCESS";
			} else {
				cbsResponse.responseMessage = "FAILURE";
			}
			return Mono.just(cbsResponse);
		} catch (Exception e) {
			logger.error("error processing mini statement request at CBS.", e);
		}
		return null;
	}

	private List<String> getStatementList(List<Row> rowList) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM");
		List<String> stmtList = new ArrayList<>();
		for (Row row : rowList) {
			String date = row.Dt;
			Date newDate;
			try {
				newDate = new SimpleDateFormat("dd-MMM-yyyy").parse(date);
				String finalDate = sdf1.format(newDate);
				String narr = row.Narr.substring(0, 17);
				String data = finalDate + " " + narr + " " + row.DrCr.charAt(0) + " " + row.Amt;
				stmtList.add(data);
				logger.info("statementList :: {}", stmtList);
				return stmtList;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> issuerDebit(ReqPay reqPay) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			IService1 service = getService();
			Details details = new Details();

			String uidToken = new UIDAIAuthCode(reqPay.getPayer().getCreds().getCred().get(0).getData().getValue()).authToken;
			String cardAccId = reqPay.getPayer().getDevice().getTag().stream()
					.filter(d -> d.getName() == DeviceTagNameType.AGENT_ID).findFirst().get().getValue();
			details.Aadhaar_Number = getBenRefId(uidToken);
			details.Account_Type = "1";
			details.Agent_ID = cardAccId;
			details.BC_ID = "103";
			details.Channel_Type = "1";
			details.Device_ID = "register";
			details.IIN = "607082";
			details.Network_Type = "3";
			details.Transaction_Code = "1";
			details.Transaction_Type = "002";
			details.Txn_Amount = reqPay.getPayer().getAmount().getValue();
			details.Txn_Currency = "INR";
			details.Txn_Timestamp = sdf.format(reqPay.getTxn().getTs());
			details.Txn_ID = reqPay.getTxn().getCustRef();
			StringWriter writer = new StringWriter();
			CONTEXT.createMarshaller().marshal(details, writer);
			String responseString = service.aepsAadhaarTransaction(writer.toString());
			Details response = (Details) CONTEXT.createUnmarshaller()
					.unmarshal(new ByteArrayInputStream(responseString.getBytes()));
			final CBSResponse cbsResponse = new CBSResponse();
			cbsResponse.authCode = response.Auth_ID;
			cbsResponse.customerName = response.Customer_Name;
			cbsResponse.responseMessage = "SUCCESS";
			// cbsResponse.responseCode = response.Error_Code.substring(3);
			cbsResponse.responseCode = cbsToNpciResponseMapper.map(response.Error_Code);
			if(cbsResponse.responseCode.length() > 2) cbsResponse.responseCode = "KH";
			if ("ERR000".equals(response.Error_Code)) {
				// cbsResponse.responseCode = "57";
				logger.info("response code :: {}", cbsResponse.responseCode);
				logger.info("before conversion Balance AMT :: {}", response.Response_XML.Bal.Amt);
				BigDecimal bal = response.Response_XML.Bal.Amt;
				String amount = Generator.amountToFormattedString12(bal.toBigInteger());
				if (amount != null) {
					cbsResponse.balance = "0001356" + (Integer.parseInt(amount) < 0 ? "D" : "C") + amount;
					logger.info("after conversion Balance AMT :: {}", cbsResponse.balance);
				}
			} else {
				cbsResponse.responseMessage = "FAILURE";
			}
			return Mono.just(cbsResponse);
		} catch (Exception e) {
			logger.error("error processing CW request at CBS.", e);
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> issuerDebitReversal(ReqPay request, IssuerTransaction original) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			IService1 service = getService();
			Details details = new Details();

			details.Aadhaar_Number = "*";
			details.Account_Type = "1";
			// details.Agent_ID = "";
			// details.BC_ID = "";
			details.Channel_Type = "1";
			details.Device_ID = "register";
			details.IIN = "607082";
			details.Network_Type = "3";
			details.Transaction_Code = "2";
			details.Transaction_Type = "002";
			details.Txn_Amount = original.getPayerAmount();
			details.Txn_Currency = "INR";
			details.Txn_Timestamp = sdf.format(original.getTxnTs());
			details.Txn_ID = "REV" + original.getCustRef();

			details.Orig_Device_ID = "register";
			details.Orig_Txn_Timestamp = sdf.format(original.getTxnTs());
			details.Orig_Txn_ID = original.getCustRef();
			details.Auth_ID = "*";
			details.Reversal_resp_code = request.getTxn().getOrgRespCode();

			StringWriter writer = new StringWriter();
			CONTEXT.createMarshaller().marshal(details, writer);
			String responseString = service.aepsAadhaarTransaction(writer.toString());
			Details response = (Details) CONTEXT.createUnmarshaller()
					.unmarshal(new ByteArrayInputStream(responseString.getBytes()));
			final CBSResponse cbsResponse = new CBSResponse();
			cbsResponse.authCode = response.Auth_ID;
			cbsResponse.customerName = response.Customer_Name;
			cbsResponse.responseCode = cbsToNpciResponseMapper.map(response.Error_Code);
			if(cbsResponse.responseCode.length() > 2) cbsResponse.responseCode = "KH";
			if ("ERR000".equals(response.Error_Code)) {
				cbsResponse.responseMessage = "SUCCESS";
				if (response.Response_XML.Bal != null && response.Response_XML.Bal.Amt != null) {
					logger.info("before conversion Balance AMT :: {}", response.Response_XML.Bal.Amt);
					BigDecimal bal = response.Response_XML.Bal.Amt;
					String amount = Generator.amountToFormattedString12(bal.toBigInteger());
					cbsResponse.balance = "0001356" + (Integer.parseInt(amount) < 0 ? "D" : "C") + amount;
					logger.info("after conversion Balance AMT :: {}", cbsResponse.balance);
				}
			}  else {
				cbsResponse.responseMessage = "FAILURE";
			}
			return Mono.just(cbsResponse);
		} catch (Exception e) {
			logger.error("error processing CW reversal at CBS.", e);
		}
		return null;

	}

	@Override
	public Mono<CBSResponse> acqAccountingCW(final ReqPay reqPay) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			final IService1 service = getService();
			final Details details = new Details();
			details.Aadhaar_Number = getRefId(reqPay.getPayer().getAc().getDetail().stream()
					.filter(f -> f.getName() == AccountDetailType.UIDNUM || f.getName() == AccountDetailType.VID)
					.findFirst().get().getValue());
			details.Agent_ID = reqPay.getPayer().getDevice().getTag().stream()
					.filter(f -> f.getName() == DeviceTagNameType.AGENT_ID).findFirst().get().getValue();
			details.Device_ID = reqPay.getPayer().getDevice().getTag().stream()
					.filter(f -> f.getName() == DeviceTagNameType.CARD_ACC_ID_CODE).findFirst().get().getValue().substring(7);
			details.IIN = appConfig.iin;
			
			details.Account_Type = "1";
			details.BC_ID = "103";
			details.Channel_Type = "1";
			details.Network_Type = "2";
			details.Transaction_Code = "1";
			details.Transaction_Type = "002";
			details.Txn_Amount = reqPay.getPayer().getAmount().getValue();
			details.Txn_Currency = "INR";
			details.Txn_Timestamp = sdf.format(reqPay.getTxn().getTs());
			details.Txn_ID = reqPay.getTxn().getCustRef();
			final StringWriter writer = new StringWriter();
			CONTEXT.createMarshaller().marshal(details, writer);
			final String responseString = service.aepsAadhaarTransaction(writer.toString());
			final Details response = (Details) CONTEXT.createUnmarshaller()
					.unmarshal((InputStream) new ByteArrayInputStream(responseString.getBytes()));
			final CBSResponse cbsResponse = new CBSResponse();
			cbsResponse.authCode = response.Auth_ID;
			cbsResponse.customerName = response.Customer_Name;
			cbsResponse.responseCode = cbsToNpciResponseMapper.map(response.Error_Code);
			if(cbsResponse.responseCode.length() > 2) cbsResponse.responseCode = "KH";
			if ("ERR000".equals(response.Error_Code)) {
				cbsResponse.responseMessage = "SUCCESS";
				final BigDecimal bal = response.Response_XML.Bal.Amt;
				final String amount = Generator.amountToFormattedString12(bal.toBigInteger());
				if (amount != null) {
					cbsResponse.balance = "0001356" + ((Integer.parseInt(amount) < 0) ? "D" : "C") + amount;
					logger.info("after conversion Balance AMT :: {}", cbsResponse.balance);
				}
			} else {
				cbsResponse.responseMessage = "FAILURE";
			}
			return Mono.just(cbsResponse);
		} catch (Exception e) {
			logger.error("error processing accountingCW at CBS.", e);
			return Mono.empty();
		}
	}

	@Override
	public Mono<CBSResponse> acqAccountingCWReversal(final AcquirerTransaction transaction) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			final IService1 service = getService();
			final Details details = new Details();
			details.Aadhaar_Number = "*";
			details.Account_Type = "1";
			Map<DeviceTagNameType, String> deviceTags = DeviceTagMap.toMap(transaction.getPayerDeviceDetails());
			details.Agent_ID = deviceTags.get(DeviceTagNameType.AGENT_ID);
			details.BC_ID = "103";
			details.Channel_Type = "1";
			details.Device_ID = deviceTags.get(DeviceTagNameType.CARD_ACC_ID_CODE).substring(7);
			details.IIN = appConfig.iin;
			details.Network_Type = "2";
			details.Transaction_Code = "1";
			details.Transaction_Type = "002";
			details.Txn_Amount = transaction.getPayerAmount();
			details.Txn_Currency = "INR";
			details.Txn_Timestamp = sdf.format(transaction.getTxnTs());
			details.Txn_ID = transaction.getCustRef();
			final StringWriter writer = new StringWriter();
			CONTEXT.createMarshaller().marshal(details, (Writer) writer);
			final String responseString = service.aepsAadhaarTransaction(writer.toString());
			final Details response = (Details) CONTEXT.createUnmarshaller()
					.unmarshal((InputStream) new ByteArrayInputStream(responseString.getBytes()));
			final CBSResponse cbsResponse = new CBSResponse();
			cbsResponse.authCode = response.Auth_ID;
			cbsResponse.customerName = response.Customer_Name;
			cbsResponse.responseCode = cbsToNpciResponseMapper.map(response.Error_Code);
			if(cbsResponse.responseCode.length() > 2) cbsResponse.responseCode = "KH";
			if ("ERR000".equals(response.Error_Code)) {
				cbsResponse.responseMessage = "SUCCESS";
			} else {
				cbsResponse.responseMessage = "FAILURE";
			}
			return Mono.just(cbsResponse);
		} catch (Exception e) {
			logger.error("error processing accountingReversal at CBS.", e);
			return Mono.empty();
		}
	}

	public static String getBenRefId(String uidToken) throws Exception {
		IService1 service = getService();
		String refId = service.generateAadhaarRefID(uidToken, Integer.valueOf(4));
		return refId;
	}
	
	public static String getRefId(String uidToken) throws Exception {
		IService1 service = getService();
		String refId = service.generateAadhaarRefIDForAadhaaar(uidToken, Integer.valueOf(4));
		return refId;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getRefId("ACBD"));;
	}
}
