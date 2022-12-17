package com.fss.aeps.jpa.acquirer;

import java.io.Serializable;
import java.math.BigDecimal;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import com.fss.aeps.jaxb.PayerConstant;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "ACQUIRER_REVERSAL_PAYEE")
public class AcquirerReversalPayee implements Serializable, Persistable<AcquirerReversalPayeeId> {

	private static final long serialVersionUID = 1L;

	@Transient
	private boolean isNew = true;

	private AcquirerReversalPayeeId id;
	private AcquirerReversal reversal;
	private String addr;
	private PayerConstant type;
	private String code;
	private BigDecimal amount;
	private String currency;

	public AcquirerReversalPayee() {
	}

	@Override
	@EmbeddedId

	@AttributeOverrides({
			@AttributeOverride(name = "msgId", column = @Column(name = "MSG_ID", nullable = false, length = 36)),
			@AttributeOverride(name = "seqnum", column = @Column(name = "SEQNUM", nullable = false, length = 3)) })
	public AcquirerReversalPayeeId getId() {
		return this.id;
	}

	public void setId(AcquirerReversalPayeeId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MSG_ID", nullable = false, insertable = false, updatable = false)
	public AcquirerReversal getReversal() {
		return reversal;
	}

	public void setReversal(AcquirerReversal reversal) {
		this.reversal = reversal;
	}

	@Column(name = "ADDR", length = 100)
	public String getAddr() {
		return this.addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE", length = 30)
	public PayerConstant getType() {
		return this.type;
	}

	public void setType(PayerConstant type) {
		this.type = type;
	}

	@Column(name = "CODE", length = 6)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "AMOUNT", precision = 14)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "CURRENCY", length = 10)
	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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
