package com.fss.aeps.jpa.issuer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import com.fss.aeps.jaxb.AddressType;
import com.fss.aeps.jaxb.IdentityConstant;
import com.fss.aeps.jaxb.PayConstant;
import com.fss.aeps.jaxb.PayerConstant;
import com.fss.aeps.jaxb.ProdType;
import com.fss.aeps.jaxb.ResultType;
import com.fss.aeps.jaxb.TxnSubType;
import com.fss.aeps.jaxb.WhiteListedConstant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "ISSUER_REVERSAL")
public class IssuerReversal implements Serializable, Persistable<String> {

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
	private TxnSubType txnSubType;
	private String purpose;
	private String custRef;
	private String refId;
	private String refUrl;
	private String note;
	private Date txnTs;
	private String initiationMode;
	private String depositId;
	private String payerAddr;
	private String payerName;
	private String payerSeqNum;
	private String payerCode;
	private PayerConstant payerType;
	private IdentityConstant payerIdentityType;
	private String payerIdentityVerifiedName;
	private WhiteListedConstant payerRatingVerifiedAddress;
	private AddressType payerAcAddrType;
	private String payerAcIin;
	private String payerAcUidnumVid;
	private BigDecimal payerAmount;
	private String payerCurrency;
	private Date reqAckTs;
	private String reqAckErr;
	private String reqAckErrCdDtl;
	private String respMsgId;
	private Date respMsgTs;
	private String respOrgId;
	private ResultType respResult;
	private String respErrCode;
	private Date respAckTs;
	private String respAckErr;
	private String respAckErrCdDtl;
	private String cbsResponseCode;
	private String cbsAuthCode;
	private String cbsTranDetails;
	private Date reqTime;
	private Date respTime;

	private String orgTxnMsgId;

	private Set<IssuerReversalRef> refs = new HashSet<>(0);

	public IssuerReversal() {
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

	@Column(name = "TXN_ID", length = 35)
	public String getTxnId() {
		return this.txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "TXN_TYPE", length = 20)
	public PayConstant getTxnType() {
		return this.txnType;
	}

	public void setTxnType(PayConstant txnType) {
		this.txnType = txnType;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "TXN_SUB_TYPE", length = 30)
	public TxnSubType getTxnSubType() {
		return this.txnSubType;
	}

	public void setTxnSubType(TxnSubType txnSubType) {
		this.txnSubType = txnSubType;
	}

	@Column(name = "PURPOSE", length = 2)
	public String getPurpose() {
		return this.purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
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

	@Column(name = "INITIATION_MODE", length = 2)
	public String getInitiationMode() {
		return this.initiationMode;
	}

	public void setInitiationMode(String initiationMode) {
		this.initiationMode = initiationMode;
	}

	@Column(name = "DEPOSIT_ID", length = 20)
	public String getDepositId() {
		return this.depositId;
	}

	public void setDepositId(String depositId) {
		this.depositId = depositId;
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

	@Enumerated(EnumType.STRING)
	@Column(name = "PAYER_IDENTITY_TYPE", length = 10)
	public IdentityConstant getPayerIdentityType() {
		return this.payerIdentityType;
	}

	public void setPayerIdentityType(IdentityConstant payerIdentityType) {
		this.payerIdentityType = payerIdentityType;
	}

	@Column(name = "PAYER_IDENTITY_VERIFIED_NAME", length = 99)
	public String getPayerIdentityVerifiedName() {
		return this.payerIdentityVerifiedName;
	}

	public void setPayerIdentityVerifiedName(String payerIdentityVerifiedName) {
		this.payerIdentityVerifiedName = payerIdentityVerifiedName;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "PAYER_RATING_VERIFIED_ADDRESS", length = 5)
	public WhiteListedConstant getPayerRatingVerifiedAddress() {
		return this.payerRatingVerifiedAddress;
	}

	public void setPayerRatingVerifiedAddress(WhiteListedConstant payerRatingVerifiedAddress) {
		this.payerRatingVerifiedAddress = payerRatingVerifiedAddress;
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

	@Column(name = "PAYER_AMOUNT", precision = 14)
	public BigDecimal getPayerAmount() {
		return this.payerAmount;
	}

	public void setPayerAmount(BigDecimal payerAmount) {
		this.payerAmount = payerAmount;
	}

	@Column(name = "PAYER_CURRENCY", length = 5)
	public String getPayerCurrency() {
		return this.payerCurrency;
	}

	public void setPayerCurrency(String payerCurrency) {
		this.payerCurrency = payerCurrency;
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

	@Column(name = "CBS_RESPONSE_CODE", length = 10)
	public String getCbsResponseCode() {
		return this.cbsResponseCode;
	}

	public void setCbsResponseCode(String cbsResponseCode) {
		this.cbsResponseCode = cbsResponseCode;
	}

	@Column(name = "CBS_AUTH_CODE", length = 10)
	public String getCbsAuthCode() {
		return this.cbsAuthCode;
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
	@Column(name = "REQ_TIME", length = 11)
	public Date getReqTime() {
		return this.reqTime;
	}

	public void setReqTime(Date reqTime) {
		this.reqTime = reqTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RESP_TIME", length = 11)
	public Date getRespTime() {
		return this.respTime;
	}

	public void setRespTime(Date respTime) {
		this.respTime = respTime;
	}

	@Column(name = "ORG_TXN_MSG_ID", unique = true, nullable = false, length = 35)
	public String getOrgTxnMsgId() {
		return orgTxnMsgId;
	}

	public void setOrgTxnMsgId(String orgTxnMsgId) {
		this.orgTxnMsgId = orgTxnMsgId;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "reversal", cascade = CascadeType.PERSIST)
	public Set<IssuerReversalRef> getRefs() {
		return this.refs;
	}

	public void setRefs(Set<IssuerReversalRef> refs) {
		this.refs = refs;
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

	@Transient
	@PrePersist
	@PostLoad
	void markNotNew() {
		this.isNew = false;
	}
}
