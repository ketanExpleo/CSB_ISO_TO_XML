package com.fss.aeps.acquirer.merchant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantRequest {

	@JsonProperty("Entry_Mode")
	public String entryMode;
	@JsonProperty("RRN")
	public String rrn;
	@JsonProperty("Con_Code")
	public String conCode;
	@JsonProperty("Hmac")
	public String hmac;
	@JsonProperty("Acqurier_inst_id")
	public String acqurierInstId;
	@JsonProperty("RECON_INDICATOR")
	public String reconIndicator;
	@JsonProperty("Loc_Time")
	public String locTime;
	@JsonProperty("CI")
	public String ci;
	@JsonProperty("TSP_ID")
	public String tspId;
	@JsonProperty("STAN")
	public String stan;
	@JsonProperty("PASS_CODE")
	public String passCode;
	@JsonProperty("Loc_date")
	public String locDate;
	@JsonProperty("TID")
	public String tid;
	@JsonProperty("MID")
	public String mid;
	@JsonProperty("PAN")
	public String pan;
	@JsonProperty("Proc_Code")
	public String procCode;
	@JsonProperty("Skey")
	public String skey;
	@JsonProperty("AUTH_FACTOR")
	public String authFactor;
	@JsonProperty("Mer_Loc")
	public String merLoc;
	@JsonProperty("MSG-TYP")
	public String msgType;
	@JsonProperty("IIN")
	public String iin;
	@JsonProperty("Auth_indicator")
	public String authIndicator;
	@JsonProperty("Amount")
	public String amount;
	@JsonProperty("PID")
	public String pid;
	@JsonProperty("MCC")
	public String mcc;
	@JsonProperty("Merchant_ID")
	public String merchantID;
	@JsonProperty("MC")
	public String mc;
	@JsonProperty("MERCHANT_DETS")
	public String merchantDets;
	@JsonProperty("Curr_code")
	public String currCode;
	@JsonProperty("Txn_Ind")
	public String txnInd;
	@JsonProperty("Original_Loc_date")
	public String orgLocDate;
	@JsonProperty("Original_Loc_Time")
	public String orgLocTime;
	@JsonProperty("Original_Stan")
	public String orgStan;
	@JsonProperty("Agent_Details")
	public String agentDetails;
	@JsonProperty("Response_Code")
	public String responseCode;
	@JsonProperty("Orig_Msg_Type")
	public String Orig_Msg_Type;

}