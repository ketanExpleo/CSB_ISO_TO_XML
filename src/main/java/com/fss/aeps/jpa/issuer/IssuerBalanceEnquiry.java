package com.fss.aeps.jpa.issuer;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import com.fss.aeps.jaxb.AddressType;
import com.fss.aeps.jaxb.CredSubType;
import com.fss.aeps.jaxb.CredType;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.PayerConstant;
import com.fss.aeps.jaxb.ProdType;
import com.fss.aeps.jaxb.ResultType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "ISSUER_BALANCE_ENQUIRY")
public class IssuerBalanceEnquiry implements Serializable, Persistable<String> {

	private static final long serialVersionUID = 1L;

	@Transient
	private boolean isNew = true;

	private String msgId;
	private Date msgTs;
	private ProdType prodType;
	private String orgId;
	private String msgVer;
	private String callbackEndpointIp;
	private String txnId;
	private PayConstant txnType;
	private String custRef;
	private String refId;
	private String refUrl;
	private String note;
	private Date txnTs;
	private String payerAddr;
	private String payerName;
	private String payerSeqNum;
	private String payerCode;
	private PayerConstant payerType;
	private String payerDeviceDetails;
	private AddressType payerAcAddrType;
	private String payerAcIin;
	private String payerAcUidnumVid;
	private CredType payerCredType;
	private CredSubType payerCredSubType;
	private String payerCredData;
	private Date reqAckTs;
	private String reqAckErr;
	private String reqAckErrCdDtl;
	private String respMsgId;
	private Date respMsgTs;
	private String respOrgId;
	private ResultType respResult;
	private String respErrCode;
	private String respAuthCode;
	private Date respAckTs;
	private String respAckErr;
	private String respAckErrCdDtl;
	private String cbsRespCode;
	private String cbsAuthCode;
	private String cbsTranDetails;
	private Date reqTime;
	private Date respTime;

	public IssuerBalanceEnquiry() {
	}

	@Id
	@Column(name = "MSG_ID", unique = true, nullable = false, length = 35)
	public String getMsgId() {
		return this.msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MSG_TS", length = 11)
	public Date getMsgTs() {
		return this.msgTs;
	}

	public void setMsgTs(Date msgTs) {
		this.msgTs = msgTs;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "PROD_TYPE", length = 10)
	public ProdType getProdType() {
		return this.prodType;
	}

	public void setProdType(ProdType prodType) {
		this.prodType = prodType;
	}

	@Column(name = "ORG_ID", length = 20)
	public String getOrgId() {
		return this.orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	@Column(name = "MSG_VER", length = 5)
	public String getMsgVer() {
		return this.msgVer;
	}

	public void setMsgVer(String msgVer) {
		this.msgVer = msgVer;
	}

	@Column(name = "CALLBACK_ENDPOINT_IP", length = 20)
	public String getCallbackEndpointIp() {
		return this.callbackEndpointIp;
	}

	public void setCallbackEndpointIp(String callbackEndpointIp) {
		this.callbackEndpointIp = callbackEndpointIp;
	}

	@Column(name = "TXN_ID", nullable = false, length = 35)
	public String getTxnId() {
		return this.txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "TXN_TYPE", nullable = false, length = 20)
	public PayConstant getTxnType() {
		return this.txnType;
	}

	public void setTxnType(PayConstant txnType) {
		this.txnType = txnType;
	}

	@Column(name = "CUST_REF", length = 12)
	public String getCustRef() {
		return this.custRef;
	}

	public void setCustRef(String custRef) {
		this.custRef = custRef;
	}

	@Column(name = "REF_ID", length = 35)
	public String getRefId() {
		return this.refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	@Column(name = "REF_URL", length = 35)
	public String getRefUrl() {
		return this.refUrl;
	}

	public void setRefUrl(String refUrl) {
		this.refUrl = refUrl;
	}

	@Column(name = "NOTE", length = 50)
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TXN_TS", length = 11)
	public Date getTxnTs() {
		return this.txnTs;
	}

	public void setTxnTs(Date txnTs) {
		this.txnTs = txnTs;
	}

	@Column(name = "PAYER_ADDR")
	public String getPayerAddr() {
		return this.payerAddr;
	}

	public void setPayerAddr(String payerAddr) {
		this.payerAddr = payerAddr;
	}

	@Column(name = "PAYER_NAME", length = 99)
	public String getPayerName() {
		return this.payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	@Column(name = "PAYER_SEQ_NUM", length = 3)
	public String getPayerSeqNum() {
		return this.payerSeqNum;
	}

	public void setPayerSeqNum(String payerSeqNum) {
		this.payerSeqNum = payerSeqNum;
	}

	@Column(name = "PAYER_CODE", length = 4)
	public String getPayerCode() {
		return this.payerCode;
	}

	public void setPayerCode(String payerCode) {
		this.payerCode = payerCode;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "PAYER_TYPE", length = 10)
	public PayerConstant getPayerType() {
		return this.payerType;
	}

	public void setPayerType(PayerConstant payerType) {
		this.payerType = payerType;
	}

	@Column(name = "PAYER_DEVICE_DETAILS", length = 2048)
	public String getPayerDeviceDetails() {
		return this.payerDeviceDetails;
	}

	public void setPayerDeviceDetails(String payerDeviceDetails) {
		this.payerDeviceDetails = payerDeviceDetails;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "PAYER_AC_ADDR_TYPE", length = 20)
	public AddressType getPayerAcAddrType() {
		return this.payerAcAddrType;
	}

	public void setPayerAcAddrType(AddressType payerAcAddrType) {
		this.payerAcAddrType = payerAcAddrType;
	}

	@Column(name = "PAYER_AC_IIN", length = 6)
	public String getPayerAcIin() {
		return this.payerAcIin;
	}

	public void setPayerAcIin(String payerAcIin) {
		this.payerAcIin = payerAcIin;
	}

	@Column(name = "PAYER_AC_UIDNUM_VID", length = 16)
	public String getPayerAcUidnumVid() {
		return this.payerAcUidnumVid;
	}

	public void setPayerAcUidnumVid(String payerAcUidnumVid) {
		this.payerAcUidnumVid = payerAcUidnumVid;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "PAYER_CRED_TYPE", length = 20)
	public CredType getPayerCredType() {
		return this.payerCredType;
	}

	public void setPayerCredType(CredType credType) {
		this.payerCredType = credType;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "PAYER_CRED_SUB_TYPE", length = 20)
	public CredSubType getPayerCredSubType() {
		return this.payerCredSubType;
	}

	public void setPayerCredSubType(CredSubType payerCredSubType) {
		this.payerCredSubType = payerCredSubType;
	}

	@Column(name = "PAYER_CRED_DATA", length = 2048)
	public String getPayerCredData() {
		return this.payerCredData;
	}

	public void setPayerCredData(String payerCredData) {
		this.payerCredData = payerCredData;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQ_ACK_TS", length = 11)
	public Date getReqAckTs() {
		return this.reqAckTs;
	}

	public void setReqAckTs(Date reqAckTs) {
		this.reqAckTs = reqAckTs;
	}

	@Column(name = "REQ_ACK_ERR", length = 100)
	public String getReqAckErr() {
		return this.reqAckErr;
	}

	public void setReqAckErr(String reqAckErr) {
		this.reqAckErr = reqAckErr;
	}

	@Column(name = "REQ_ACK_ERR_CD_DTL", length = 4000)
	public String getReqAckErrCdDtl() {
		return reqAckErrCdDtl;
	}

	public void setReqAckErrCdDtl(String reqAckErrCdDtl) {
		this.reqAckErrCdDtl = reqAckErrCdDtl;
	}

	@Column(name = "RESP_MSG_ID", length = 35)
	public String getRespMsgId() {
		return this.respMsgId;
	}

	public void setRespMsgId(String respMsgId) {
		this.respMsgId = respMsgId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RESP_MSG_TS", length = 11)
	public Date getRespMsgTs() {
		return this.respMsgTs;
	}

	public void setRespMsgTs(Date respMsgTs) {
		this.respMsgTs = respMsgTs;
	}

	@Column(name = "RESP_ORG_ID", length = 20)
	public String getRespOrgId() {
		return this.respOrgId;
	}

	public void setRespOrgId(String respOrgId) {
		this.respOrgId = respOrgId;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "RESP_RESULT", length = 20)
	public ResultType getRespResult() {
		return this.respResult;
	}

	public void setRespResult(ResultType respResult) {
		this.respResult = respResult;
	}

	@Column(name = "RESP_ERR_CODE", length = 20)
	public String getRespErrCode() {
		return this.respErrCode;
	}

	public void setRespErrCode(String respErrCode) {
		this.respErrCode = respErrCode;
	}

	@Column(name = "RESP_AUTH_CODE", length = 32)
	public String getRespAuthCode() {
		return this.respAuthCode;
	}

	public void setRespAuthCode(String respAuthCode) {
		this.respAuthCode = respAuthCode;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RESP_ACK_TS", length = 11)
	public Date getRespAckTs() {
		return this.respAckTs;
	}

	public void setRespAckTs(Date respAckTs) {
		this.respAckTs = respAckTs;
	}

	@Column(name = "RESP_ACK_ERR", length = 100)
	public String getRespAckErr() {
		return this.respAckErr;
	}

	public void setRespAckErr(String respAckErr) {
		this.respAckErr = respAckErr;
	}

	@Column(name = "RESP_ACK_ERR_CD_DTL", length = 4000)
	public String getRespAckErrCdDtl() {
		return respAckErrCdDtl;
	}

	public void setRespAckErrCdDtl(String respAckErrCdDtl) {
		this.respAckErrCdDtl = respAckErrCdDtl;
	}

	@Column(name = "CBS_RESP_CODE", length = 5)
	public String getCbsRespCode() {
		return cbsRespCode;
	}

	public void setCbsRespCode(String cbsRespCode) {
		this.cbsRespCode = cbsRespCode;
	}

	@Column(name = "CBS_AUTH_CODE", length = 6)
	public String getCbsAuthCode() {
		return cbsAuthCode;
	}

	public void setCbsAuthCode(String cbsAuthCode) {
		this.cbsAuthCode = cbsAuthCode;
	}

	@Column(name = "CBS_TRAN_DETAILS", length = 1024)
	public String getCbsTranDetails() {
		return cbsTranDetails;
	}

	public void setCbsTranDetails(String cbsTranDetails) {
		this.cbsTranDetails = cbsTranDetails;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQ_TIME", length = 11, updatable = false)
	public Date getReqTime() {
		return reqTime;
	}

	public void setReqTime(Date reqTime) {
		this.reqTime = reqTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RESP_TIME", length = 11)
	public Date getRespTime() {
		return respTime;
	}

	public void setRespTime(Date respTime) {
		this.respTime = respTime;
	}

	@Override
	@Transient
	public String getId() {
		return msgId;
	}

	@Override
	@Transient
	public boolean isNew() {
		return isNew;
	}

	@PrePersist
	@PostLoad
	void markNotNew() {
		this.isNew = false;
	}

}
