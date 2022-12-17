package com.fss.aeps.cbsclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.tempuri.IService1;
import org.tempuri.Service1;

import com.fss.aeps.jaxb.ReqBalEnq;
import com.fss.aeps.jaxb.ReqChkTxn;
import com.fss.aeps.jaxb.ReqPay;
import com.fss.aeps.jpa.issuer.IssuerTransaction;
import com.fss.aeps.util.Generator;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import reactor.core.publisher.Mono;

@Component
public class CSBClient implements IssuerCbsClient {

	private static final Logger logger = LoggerFactory.getLogger(CSBClient.class);
	private static final JAXBContext CONTEXT = getJaxbContext();
	private static final BigDecimal hundred = new BigDecimal(100.00);

	private Service1 getService() {
		try {
			return new Service1(new ClassPathResource("ini/csb.wsdl").getURL());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static JAXBContext getJaxbContext() {
		try {
			return JAXBContext.newInstance(Details.class);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> balance(ReqBalEnq reqBalEnq) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Service1 service1 = getService();
			Details details = new Details();

			details.Aadhaar_Number = "dbab683e-056d-461c-8c23-72975958e832";
			details.Account_Type = "1";
			details.Agent_ID = "20020002";
			details.BC_ID = "103";
			details.Channel_Type = "1";
			details.Device_ID = "UY185633";
			details.IIN = "607082";
			details.Network_Type = "1";
			details.Transaction_Code = "1";
			details.Transaction_Type = "003";
			details.Txn_Amount = new BigDecimal(0.00);
			details.Txn_Currency = "INR";
			details.Txn_Timestamp = sdf.format(reqBalEnq.getTxn().getTs());
			details.Txn_ID = reqBalEnq.getTxn().getCustRef();
			StringWriter writer = new StringWriter();
			CONTEXT.createMarshaller().marshal(details, writer);
			IService1 iService1 = service1.getBasicHttpBindingIService1();
			String responseString = iService1.aepsAadhaarTransaction(writer.toString());
			logger.info("@@@@ Response String :: {}", responseString);
			Details response = (Details) CONTEXT.createUnmarshaller()
					.unmarshal(new ByteArrayInputStream(responseString.getBytes()));
			logger.info("@@@@ Response :: {}", response);
			final CBSResponse cbsResponse = new CBSResponse();
			cbsResponse.authCode = response.Auth_ID;
			cbsResponse.customerName = response.Customer_Name;
			logger.info("@@@@@ Error_Code :: {}", response.Error_Code);
			if ("021".equals(response.Error_Code.substring(3))) {
				cbsResponse.responseCode = "52";
			} else {
				cbsResponse.responseCode = "000";
			}
			logger.info("@@@@@ response code :: {}", cbsResponse.responseCode);
			if ("ERR000".equals(response.Error_Code)) {
				logger.info("@@@@@ before conversion Balance AMT :: {}", response.Response_XML.Bal.Amt);
				BigDecimal bal = response.Response_XML.Bal.Amt;
				String amount = Generator.amountToFormattedString12(bal.toBigInteger());
				if (amount != null) {
					cbsResponse.balance = "0001356" + (Integer.parseInt(amount) < 0 ? "D" : "C") + amount;
					logger.info("@@@@@ after conversion Balance AMT :: {}", cbsResponse.balance);
				}
				cbsResponse.responseMessage = "SUCCESS";
			} else {
				cbsResponse.responseMessage = "FAILURE";
			}
			return Mono.just(cbsResponse);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("error processing balance enquiry request at CBS. {}", e.getMessage());
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> miniStatement(ReqBalEnq reqBalEnq) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Service1 service1 = getService();
			Details details = new Details();

			details.Aadhaar_Number = "dbab683e-056d-461c-8c23-72975958e832";
			details.Account_Type = "1";
			details.Agent_ID = "20020002";
			details.BC_ID = "103";
			details.Channel_Type = "1";
			details.Device_ID = "UY185633";
			details.IIN = "607082";
			details.Network_Type = "1";
			details.Transaction_Code = "1";
			details.Transaction_Type = "004";
			details.Txn_Amount = new BigDecimal(0.00);
			details.Txn_Currency = "INR";
			details.Txn_Timestamp = sdf.format(reqBalEnq.getTxn().getTs());
			details.Txn_ID = reqBalEnq.getTxn().getCustRef();
			StringWriter writer = new StringWriter();
			CONTEXT.createMarshaller().marshal(details, writer);
			IService1 iService1 = service1.getBasicHttpBindingIService1();
			String responseString = iService1.aepsAadhaarTransaction(writer.toString());
			logger.info("@@@@ Response String :: {}", responseString);
			Details response = (Details) CONTEXT.createUnmarshaller()
					.unmarshal(new ByteArrayInputStream(responseString.getBytes()));
			logger.info("@@@@ Response :: {}", response);
			List<Row> rowList = response.Response_XML.MiniStmt.row;
			List<String> statementList = getStatementList(rowList);
			final CBSResponse cbsResponse = new CBSResponse();
			cbsResponse.authCode = response.Auth_ID;
			cbsResponse.customerName = response.Customer_Name;
			cbsResponse.responseCode = response.Error_Code.substring(3);
			logger.info("@@@@@ response code :: {}", cbsResponse.responseCode);
			logger.info("@@@@@ Error_Code :: {}", response.Error_Code);
			if ("ERR000".equals(response.Error_Code)) {
				logger.info("@@@@@ before conversion Balance AMT :: {}", response.Response_XML.MiniStmt.Bal.Amt);
				BigDecimal bal = response.Response_XML.MiniStmt.Bal.Amt;
				String amount = Generator.amountToFormattedString12(bal.toBigInteger());
				if (amount != null) {
					cbsResponse.balance = "0001356" + (Integer.parseInt(amount) < 0 ? "D" : "C") + amount;
					logger.info("@@@@@ after conversion Balance AMT :: {}", cbsResponse.balance);
				}
				// for CZ tool testing
				/*
				 * List<String> statementList = new ArrayList<String>();
				 * statementList.add("02/06 POS/D/5017000018 C 100.00");
				 * statementList.add("02/06 POS/D/5017000018 C 100.00");
				 * statementList.add("02/06 POS/D/5017000018 C 100.00");
				 * statementList.add("02/06 POS/D/5017000018 C 100.00");
				 * statementList.add("02/06 POS/D/5017000018 C 100.00");
				 * statementList.add("02/06 POS/D/5017000018 C 100.00");
				 * statementList.add("02/06 POS/D/5017000018 C 100.00");
				 * statementList.add("02/06 POS/D/5017000018 C 100.00");
				 * statementList.add("02/06 POS/D/5017000018 C 100.00");
				 * statementList.add("02/06 POS/D/5017000018 C 100.00");
				 */
				cbsResponse.statement = statementList;

				cbsResponse.responseMessage = "SUCCESS";
			} else {
				cbsResponse.responseMessage = "FAILURE";
			}
			return Mono.just(cbsResponse);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("error processing mini statement request at CBS.{}", e.getMessage());
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
				logger.info("@@@@@ statementList :: {}", stmtList);
				return stmtList;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> debit(ReqPay reqPay) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Service1 service1 = getService();
			Details details = new Details();

			details.Aadhaar_Number = "dbab683e-056d-461c-8c23-72975958e832";
			details.Account_Type = "1";
			details.Agent_ID = "20020002";
			details.BC_ID = "103";
			details.Channel_Type = "1";
			details.Device_ID = "UY185633";
			details.IIN = "607082";
			details.Network_Type = "1";
			details.Transaction_Code = "1";
			details.Transaction_Type = "002";
			details.Txn_Amount = reqPay.getPayer().getAmount().getValue();
			details.Txn_Currency = "INR";
			details.Txn_Timestamp = sdf.format(reqPay.getTxn().getTs());
			details.Txn_ID = reqPay.getTxn().getCustRef();
			StringWriter writer = new StringWriter();
			CONTEXT.createMarshaller().marshal(details, writer);
			IService1 iService1 = service1.getBasicHttpBindingIService1();
			String responseString = iService1.aepsAadhaarTransaction(writer.toString());
			logger.info("@@@@ Response String :: {}", responseString);
			Details response = (Details) CONTEXT.createUnmarshaller()
					.unmarshal(new ByteArrayInputStream(responseString.getBytes()));
			logger.info("@@@@ Response :: {}", response);
			final CBSResponse cbsResponse = new CBSResponse();
			cbsResponse.authCode = response.Auth_ID;
			cbsResponse.customerName = response.Customer_Name;
			cbsResponse.responseMessage = "SUCCESS";
			// cbsResponse.responseCode = response.Error_Code.substring(3);
			logger.info("@@@@@ Error_Code :: {}", response.Error_Code);
			// logger.info("@@@@@ response code :: {}",cbsResponse.responseCode);
			if ("ERR000".equals(response.Error_Code)) {
				//cbsResponse.responseCode = "57";
				cbsResponse.responseCode = response.Error_Code.substring(3);
				logger.info("@@@@@ response code :: {}", cbsResponse.responseCode);
				logger.info("@@@@@ before conversion Balance AMT :: {}", response.Response_XML.Bal.Amt);
				BigDecimal bal = response.Response_XML.Bal.Amt;
				String amount = Generator.amountToFormattedString12(bal.toBigInteger());
				if (amount != null) {
					cbsResponse.balance = "0001356" + (Integer.parseInt(amount) < 0 ? "D" : "C") + amount;
					logger.info("@@@@@ after conversion Balance AMT :: {}", cbsResponse.balance);
				}
			}else if ("ERR021".equals(response.Error_Code)) {
				cbsResponse.responseCode = "52";
				logger.info("@@@@@ response code :: {}", cbsResponse.responseCode);
				logger.info("@@@@@ before conversion Balance AMT :: {}", response.Response_XML.Bal.Amt);
				BigDecimal bal = response.Response_XML.Bal.Amt;
				String amount = Generator.amountToFormattedString12(bal.toBigInteger());
				if (amount != null) {
					cbsResponse.balance = "0001356" + (Integer.parseInt(amount) < 0 ? "D" : "C") + amount;
					logger.info("@@@@@ after conversion Balance AMT :: {}", cbsResponse.balance);
				}
			}else if ("ERR053".equals(response.Error_Code)) {
				cbsResponse.responseCode = "51";
				logger.info("@@@@@ response code :: {}", cbsResponse.responseCode);
				logger.info("@@@@@ before conversion Balance AMT :: {}", response.Response_XML.Bal.Amt);
				BigDecimal bal = response.Response_XML.Bal.Amt;
				String amount = Generator.amountToFormattedString12(bal.toBigInteger());
				if (amount != null) {
					cbsResponse.balance = "0001356" + (Integer.parseInt(amount) < 0 ? "D" : "C") + amount;
					logger.info("@@@@@ after conversion Balance AMT :: {}", cbsResponse.balance);
				}
			}else if ("ERR050".equals(response.Error_Code)) {
				cbsResponse.responseCode = "91";
				logger.info("@@@@@ response code :: {}", cbsResponse.responseCode);
			}else if ("ERR064".equals(response.Error_Code)) {
				cbsResponse.responseCode = "UW";
				logger.info("@@@@@ response code :: {}", cbsResponse.responseCode);
			}
			else {
				cbsResponse.responseMessage = "FAILURE";
			}
			return Mono.just(cbsResponse);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("error processing cash withdraw request at CBS.{}", e.getMessage());
		}
		return null;
	}

	@Override
	public Mono<CBSResponse> creditFundTransfer(ReqPay reqPay) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<CBSResponse> deposit(ReqPay reqPay) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<CBSResponse> depositAdvice(ReqChkTxn reqChkTxn, IssuerTransaction original) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<CBSResponse> depositRepeatAdvice(ReqChkTxn reqChkTxn, IssuerTransaction original) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<CBSResponse> debitReversal(ReqPay request, IssuerTransaction original) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) throws IOException, JAXBException {
		String s = "\r\n" + "<Details>\r\n" + "	<Aadhaar_Number/>\r\n" + "	<Txn_Amount/>\r\n" + "	<Txn_Currency/>\r\n"
				+ "	<BC_ID/>\r\n" + "	<Agent_ID/>\r\n" + "	<Device_ID/>\r\n" + "	<Txn_Timestamp/>\r\n"
				+ "	<Txn_ID/>\r\n" + "	<Channel_Type/>\r\n" + "	<Transaction_Type/>\r\n"
				+ "	<Transaction_Code/>\r\n" + "	<Orig_Device_ID/>\r\n" + "	<Orig_Txn_Timestamp/>\r\n"
				+ "	<Orig_Txn_ID/>\r\n" + "	<Reversal_resp_code/>\r\n" + "	<Network_Type/>\r\n"
				+ "	<Benf_AadhaarNo/>\r\n" + "	<Account_Type/>\r\n" + "	<Customer_Name/>\r\n" + "	<IIN/>\r\n"
				+ "	<Status>2</Status>\r\n" + "	<Auth_ID/>\r\n" + "	<Error_Code>ERR001</Error_Code>\r\n"
				+ "	<Response_XML/>\r\n" + "</Details>";
		System.err.println(CONTEXT.createUnmarshaller().unmarshal(new ByteArrayInputStream(s.getBytes())));
	}
}
