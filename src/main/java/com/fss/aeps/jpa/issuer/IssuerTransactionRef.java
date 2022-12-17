package com.fss.aeps.jpa.issuer;

import java.io.Serializable;
import java.math.BigDecimal;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "ISSUER_TRANSACTION_REF")
public class IssuerTransactionRef implements Serializable {

	private static final long serialVersionUID = 1L;

	@Transient
	private boolean isNew = true;

	private IssuerTransactionRefId id;
	private IssuerTransaction transaction;
	private String addr;
	private BigDecimal settAmount;
	private String settCurrency;
	private String approvalNo;
	private String respCode;
	private String regName;
	private BigDecimal orgAmount;
	private String code;

	public IssuerTransactionRef() {
	}


	@EmbeddedId

	@AttributeOverrides({
			@AttributeOverride(name = "msgId", column = @Column(name = "MSG_ID", nullable = false, length = 35)),
			@AttributeOverride(name = "type", column = @Column(name = "TYPE", nullable = false, length = 30)),
			@AttributeOverride(name = "seqnum", column = @Column(name = "SEQNUM", nullable = false, length = 3)) })
	public IssuerTransactionRefId getId() {
		return this.id;
	}

	public void setId(IssuerTransactionRefId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MSG_ID", nullable = false, insertable = false, updatable = false)
	public IssuerTransaction getTransaction() {
		return this.transaction;
	}

	public void setTransaction(IssuerTransaction transaction) {
		this.transaction = transaction;
	}

	@Column(name = "ADDR", length = 100)
	public String getAddr() {
		return this.addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	@Column(name = "SETT_AMOUNT", precision = 14)
	public BigDecimal getSettAmount() {
		return this.settAmount;
	}

	public void setSettAmount(BigDecimal settAmount) {
		this.settAmount = settAmount;
	}

	@Column(name = "SETT_CURRENCY", length = 5)
	public String getSettCurrency() {
		return this.settCurrency;
	}

	public void setSettCurrency(String settCurrency) {
		this.settCurrency = settCurrency;
	}

	@Column(name = "APPROVAL_NO", length = 12)
	public String getApprovalNo() {
		return this.approvalNo;
	}

	public void setApprovalNo(String approvalNo) {
		this.approvalNo = approvalNo;
	}

	@Column(name = "RESP_CODE", length = 3)
	public String getRespCode() {
		return this.respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	@Column(name = "REG_NAME", length = 100)
	public String getRegName() {
		return this.regName;
	}

	public void setRegName(String regName) {
		this.regName = regName;
	}

	@Column(name = "ORG_AMOUNT", precision = 14)
	public BigDecimal getOrgAmount() {
		return this.orgAmount;
	}

	public void setOrgAmount(BigDecimal orgAmount) {
		this.orgAmount = orgAmount;
	}

	@Column(name = "CODE", length = 6)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

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
