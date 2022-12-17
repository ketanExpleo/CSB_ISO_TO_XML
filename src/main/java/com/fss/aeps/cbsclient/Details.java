package com.fss.aeps.cbsclient;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Details")
public class Details {
	@XmlElement(name = "Aadhaar_Number")
	public String Aadhaar_Number;

	@XmlElement(name = "Txn_Amount")
	public BigDecimal Txn_Amount;

	@XmlElement(name = "Txn_Currency")
	public String Txn_Currency;

	@XmlElement(name = "BC_ID")
	public String BC_ID;

	@XmlElement(name = "Agent_ID")
	public String Agent_ID;

	@XmlElement(name = "Device_ID")
	public String Device_ID;

	@XmlElement(name = "Txn_Timestamp")
	public String Txn_Timestamp;

	@XmlElement(name = "Txn_ID")
	public String Txn_ID;

	@XmlElement(name = "Channel_Type")
	public String Channel_Type;

	@XmlElement(name = "Transaction_Type")
	public String Transaction_Type;

	@XmlElement(name = "Transaction_Code")
	public String Transaction_Code;

	@XmlElement(name = "Network_Type")
	public String Network_Type;

	@XmlElement(name = "Account_Type")
	public String Account_Type;

	@XmlElement(name = "IIN")
	public String IIN;

	@XmlElement(name = "Status")
	public String Status;

	@XmlElement(name = "Customer_Name")
	public String Customer_Name;

	@XmlElement(name = "Error_Code")
	public String Error_Code;

	@XmlElement(name = "Orig_Device_ID") //for reversal transation
	public String Orig_Device_ID;

	@XmlElement(name = "Orig_Txn_Timestamp") //for reversal transation
	public String Orig_Txn_Timestamp;

	@XmlElement(name = "Orig_Txn_ID") //for reversal transation
	public String Orig_Txn_ID ;

	@XmlElement(name = "Auth_ID") //for reversal transation
	public String Auth_ID ;

	@XmlElement(name = "Reversal_resp_code") //for reversal transation
	public String Reversal_resp_code ;

	@XmlElement(name = "Benf_AadhaarNo") //for only fund transfer(005) transation
	public String Benf_AadhaarNo ;

	@XmlElement(name = "Response_XML")
	public ResponseXml Response_XML ;

}

