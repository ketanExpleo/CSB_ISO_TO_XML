package com.fss.aeps.json;

import java.math.BigDecimal;
import java.util.Date;

public class ReqPayJson {

	protected String id;
	protected String note;
	protected String refId;
	protected String refUrl;
	protected Date ts;
	protected String type;
	protected String custRef;
	protected String initiationMode;
	protected String subType;
	protected String purpose;
	
	protected String addr;
	protected String code;
	protected String name;
	protected String value;
	protected String seqNum;
	protected String verifiedName;
	protected String verifiedAddress;
	protected String addrType;
	
	//Uses
	protected String pi;
    protected String pa;
    protected String pfa;
    protected String bio;
    protected String bt;
    protected String pin;
    protected String otp;
	
    //Meta
    protected String udc;
    protected String lot;
    protected String lov;
    protected String rdsId;
    protected String rdsVer;
    protected String dpId;
    protected String dc;
    protected String mi;
    protected String mc;
    
    //Skey
    protected String skeyValue;
    protected String ci;
    
    //Hmac
    protected String hmacValue;
	
    //Data
    protected String dataValue;
    protected String dataType;
    
    //cred
    protected String credType;
    protected String credSubType;
    
    //AmountType
    protected String curr;
    protected BigDecimal amountTypeValue;
	
}
