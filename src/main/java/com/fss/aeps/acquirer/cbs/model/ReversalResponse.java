package com.fss.aeps.acquirer.cbs.model;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "REVERSALRESPONSE")
public class ReversalResponse extends Response{
	
	public ReversalResponse() {
		super();
	}

	public ReversalResponse(ReversalRequest request) {
		this.rrn = request.rrn;
		this.fromAccountNo = request.fromAccountNo;
		this.msgType = "0430";
		this.cardNo = request.cardNo;
		this.txnCode = request.txnCode;
		this.txnAmount = request.txnAmount;
		this.txnDateTime = request.txnDateTime;
		this.stan = request.stan;
		this.time = request.time;
		this.date = request.date;
		this.srcType = request.srcType;
		this.merchantType = request.merchantType;
		this.entryMode = request.entryMode;
		this.serviceCondition = request.serviceCondition;
		this.acqurierInstId = request.acqurierInstId;
		this.terminalID = request.terminalID;
		this.cardAcptID = request.cardAcptID;
		this.cardAcptNameLOC = request.cardAcptNameLOC;
		this.merchantPassCode = request.merchantPassCode;
		this.currencyCode = request.currencyCode;
		this.uidVidNo = request.uidVidNo;
		this.authIndicator = request.authIndicator;
		this.agentID = request.agentID;
		this.postalCode = request.postalCode;
		this.toAccountNo = request.toAccountNo;
		this.pinData = request.pinData;
		this.reversalRespCode = request.reversalRespCode;
		this.originalData = request.originalData;
		this.beneficiaryData = request.beneficiaryData;
	}
	
}