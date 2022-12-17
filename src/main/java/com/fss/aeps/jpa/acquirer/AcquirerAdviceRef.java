package com.fss.aeps.jpa.acquirer;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "ACQUIRER_ADVICE_REF")
public class AcquirerAdviceRef implements Serializable, Persistable<AcquirerAdviceRefId> {

	private static final long serialVersionUID = 1L;

	@Transient
	private boolean isNew = true;

	private AcquirerAdviceRefId id;
	private String addr;
	private String code;
	private BigDecimal orgAmount;
	private BigDecimal settAmount;
	private String settCurrency;
	private String approvalNo;
	private String respCode;
	private String regName;

	public AcquirerAdviceRef() {
	}

	public AcquirerAdviceRef(AcquirerAdviceRefId id) {
		this.id = id;
	}


	@Override
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "msgId", column = @Column(name = "MSG_ID", nullable = false, length = 35)),
			@AttributeOverride(name = "seqnum", column = @Column(name = "SEQNUM", nullable = false, length = 3)),
			@AttributeOverride(name = "type", column = @Column(name = "TYPE", nullable = false, length = 30)) })
	public AcquirerAdviceRefId getId() {
		return this.id;
	}

	public void setId(AcquirerAdviceRefId id) {
		this.id = id;
	}

	@Column(name = "ADDR", length = 100)
	public String getAddr() {
		return this.addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	@Column(name = "CODE", length = 6)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "ORG_AMOUNT", precision = 14)
	public BigDecimal getOrgAmount() {
		return this.orgAmount;
	}

	public void setOrgAmount(BigDecimal orgAmount) {
		this.orgAmount = orgAmount;
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
