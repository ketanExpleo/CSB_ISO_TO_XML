package com.fss.aeps.acquirer.merchant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantResponse {


	@JsonProperty("ACCOUNT_BAL")
	public String accountBal = "*";

	@JsonProperty("Auth_ID")
	public String authId = "*";

	@JsonProperty("MER_AVAIL_BAL")
	public String merAvailBal = "*";

	@JsonProperty("MERCHANT_AMNT_DETS")
	public String merchantDets = "";





	@JsonProperty("IIN")
	public String iin;

	@JsonProperty("PAN")
	public String pan;

	@JsonProperty("RRN")
	public String rrn;

	@JsonProperty("STAN")
	public String stan;

	@JsonProperty("TID")
	public String tid;


	@JsonProperty("Amount")
	public String amount;

	@JsonProperty("Curr_code")
	public String currCode;

	@JsonProperty("Loc_date")
	public String locDate;

	@JsonProperty("Loc_Time")
	public String locTime;

	@JsonProperty("Merchant_ID")
	public String merchantId;

	@JsonProperty("MSG-TYP")
	public String msgType;

	@JsonProperty("Proc_Code")
	public String procCode;

	@JsonProperty("Response_Code")
	public String responseCode;

	@JsonProperty("RSP_DESC")
	public String respDesc;

	@JsonProperty("UID_TOKEN")
	public String uidToken = "*";

	@JsonProperty("CUSTOMER_NAME")
	public String customerName = "*";

	@JsonProperty("CUSTOMER_ACNO")
	public String customerAcno = "*";

	@JsonProperty("Auth_CODE")
	public String authCode = "2";

	@JsonProperty("LEDGER_BAL")
	public String ledgerBal = "*";


	public MerchantResponse() {
	}

	public MerchantResponse(MerchantRequest request) {
		this.iin = request.iin;
		this.pan = request.pan;
		this.rrn = request.rrn;
		this.stan = request.stan;
		this.tid = request.tid;
		this.amount = request.amount;
		this.currCode = request.currCode;
		this.locTime = request.locTime;
		this.locDate = request.locDate;
		this.merchantId = request.mid;
		this.procCode = request.procCode;

		/*
		 * this.acqurierInstId = request.acqurierInstId; this.merchantId =
		 * request.merchantID; this.merchantDets = request.merchantDets; this.mcc =
		 * request.mcc;
		 */
	}



}