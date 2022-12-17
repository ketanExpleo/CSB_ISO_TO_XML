package com.fss.aeps.jpa;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "CBS_TRANSACTION")
public class CbsTransaction implements Serializable, Persistable<Long> {

	private static final long serialVersionUID = 1L;

	@Transient
	private boolean isNew = true;

	private long id;
	private String isAcquirer;
	private String mti;
	private String pan;
	private String pcode;
	private String tranType;
	private String time;
	private Date dateTime;
	private String rrn;
	private String reconIndicator;
	private String authCode;
	private String responseCode;
	private String tranDetails;
	private Blob requestBody;
	private Blob responseBody;
	private String remoteAddr;
	private Integer remotePort;
	private String localAddr;
	private Integer localPort;
	private Date requestTime;
	private Date responseTime;
	private String exceptionMessage;

	public CbsTransaction() {
	}

	public CbsTransaction(long id) {
		this.id = id;
	}

	@Override
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "ID", unique = true, nullable = false, precision = 18, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "IS_ACQUIRER", length = 1)
	public String getIsAcquirer() {
		return this.isAcquirer;
	}

	public void setIsAcquirer(String isAcquirer) {
		this.isAcquirer = isAcquirer;
	}

	@Column(name = "MTI", length = 4)
	public String getMti() {
		return this.mti;
	}

	public void setMti(String mti) {
		this.mti = mti;
	}

	@Column(name = "PAN", length = 29)
	public String getPan() {
		return this.pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	@Column(name = "PCODE", length = 6)
	public String getPcode() {
		return this.pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

	@Column(name = "TRAN_TYPE", length = 100)
	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	@Column(name = "TIME", length = 20)
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_TIME", length = 11, insertable = false, updatable = false)
	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	@Column(name = "RRN", length = 12)
	public String getRrn() {
		return this.rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	@Column(name = "RECON_INDICATOR", length = 100)
	public String getReconIndicator() {
		return reconIndicator;
	}

	public void setReconIndicator(String reconIndicator) {
		this.reconIndicator = reconIndicator;
	}

	@Column(name = "AUTH_CODE", length = 12)
	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	@Column(name = "RESPONSE_CODE", length = 3)
	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	@Column(name = "TRAN_DETAILS", length = 1024)
	public String getTranDetails() {
		return tranDetails;
	}

	public void setTranDetails(String tranDetails) {
		this.tranDetails = tranDetails;
	}

	@Column(name = "REQUEST_BODY")
	public Blob getRequestBody() {
		return this.requestBody;
	}

	public void setRequestBody(Blob requestBody) {
		this.requestBody = requestBody;
	}

	@Column(name = "RESPONSE_BODY")
	public Blob getResponseBody() {
		return this.responseBody;
	}

	public void setResponseBody(Blob responseBody) {
		this.responseBody = responseBody;
	}

	@Column(name = "REMOTE_ADDR")
	public String getRemoteAddr() {
		return this.remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	@Column(name = "REMOTE_PORT", precision = 5, scale = 0)
	public Integer getRemotePort() {
		return this.remotePort;
	}

	public void setRemotePort(Integer remotePort) {
		this.remotePort = remotePort;
	}

	@Column(name = "LOCAL_ADDR")
	public String getLocalAddr() {
		return this.localAddr;
	}

	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}

	@Column(name = "LOCAL_PORT", precision = 5, scale = 0)
	public Integer getLocalPort() {
		return this.localPort;
	}

	public void setLocalPort(Integer localPort) {
		this.localPort = localPort;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUEST_TIME", length = 11)
	public Date getRequestTime() {
		return this.requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RESPONSE_TIME", length = 11)
	public Date getResponseTime() {
		return this.responseTime;
	}

	public void setResponseTime(Date responseTime) {
		this.responseTime = responseTime;
	}

	@Column(name = "EXCEPTION_MESSAGE", length = 1024)
	public String getExceptionMessage() {
		return this.exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
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
