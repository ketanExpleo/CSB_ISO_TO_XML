package com.fss.aeps.acquirer.cbs;

import java.io.StringWriter;

import com.fss.aeps.acquirer.cbs.fixml.PidData;
import com.sil.fssswitch.model.BalanceRequest;
import com.sil.fssswitch.model.BalanceResponse;
import com.sil.fssswitch.model.MResponse;
import com.sil.fssswitch.model.MiniStatementRequest;
import com.sil.fssswitch.model.MiniStatementResponse;
import com.sil.fssswitch.model.Response;
import com.sil.fssswitch.model.TSPResponse;
import com.sil.fssswitch.model.WithdrawalRequest;
import com.sil.fssswitch.model.WithdrawalResponse;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

public class Test {

	public static void main(String[] args) throws JAXBException {
		JAXBContext context = getContext();
		MResponse response = new MResponse();
		response.sid="FSS111";
		response.type=1;
		final BalanceResponse balResponse = new BalanceResponse();
		balResponse.rrn = "233209415327";
		balResponse.responseCode = "00";
		//balResponse.responseDesc = "Transaction Completed Successfully";
		//balResponse.customerName = "*";
		//balResponse.accAvalibBalance = 00000000000000000;
		//balResponse.txnAuthCode = "000001";
		//balResponse.uidaiAuthCode = "20c70e4af70747de913167bb3d7c3cee";
		//balResponse.fromAccount = "*";
		StringWriter writer = new StringWriter();
		context.createMarshaller().marshal(balResponse, writer);
		response.data=writer.toString();
		System.out.println("response xml : "+response.data);
		
		/*
		 * JAXBContext context = getContext(); TransactionRequestResponse
		 * requestResponse = new TransactionRequestResponse(); MResponse response = new
		 * MResponse(); response.sid="FSS111"; response.type=1; BalanceResponse
		 * balResponse = new BalanceResponse(); balResponse.rrn = "233209415327";
		 * balResponse.responseCode = "00"; balResponse.responseDesc =
		 * "Transaction Completed Successfully"; balResponse.customerName = "*";
		 * balResponse.accAvalibBalance = 00000000000000000; balResponse.txnAuthCode =
		 * "000001"; balResponse.uidaiAuthCode = "20c70e4af70747de913167bb3d7c3cee";
		 * balResponse.fromAccount = "*";
		 * 
		 * 
		 * ByteArrayOutputStream barrOut = new ByteArrayOutputStream();
		 * context.createMarshaller().marshal(balResponse, barrOut); response.data=
		 * Base64.getEncoder().encodeToString(barrOut.toByteArray());
		 * 
		 * 
		 * ByteArrayOutputStream barrOut1 = new ByteArrayOutputStream();
		 * context.createMarshaller().marshal(response, barrOut1);
		 * requestResponse.transactionRequestResult = barrOut1.toString();
		 * System.out.println("Resp : "+requestResponse.transactionRequestResult);
		 */
		
		


	}
	
	
	  private static JAXBContext getContext() {
	  
	  try { return JAXBContext.newInstance(WithdrawalRequest.class,
	  WithdrawalResponse.class, BalanceRequest.class, BalanceResponse.class,
	  MiniStatementRequest.class, MiniStatementResponse.class,MResponse.class,
	  TransactionRequestResponse.class, TSPResponse.class,
	  PidData.class,Response.class); } catch (JAXBException e) {
	  e.printStackTrace(); } return null; }
	 
}
