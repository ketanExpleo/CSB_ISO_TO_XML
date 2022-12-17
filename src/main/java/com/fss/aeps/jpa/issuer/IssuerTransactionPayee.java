package com.fss.aeps.jpa.issuer;

import java.io.Serializable;
import java.math.BigDecimal;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fss.aeps.jaxb.AddressType;
import com.fss.aeps.jaxb.CredSubType;
import com.fss.aeps.jaxb.CredType;
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
@Table(name = "ISSUER_TRANSACTION_PAYEE")
public class IssuerTransactionPayee implements Serializable {

	private static final long serialVersionUID = 1L;

	@Transient
	private boolean isNew = true;

	private IssuerTransactionPayeeId id;
	private IssuerTransaction transaction;
	private PayerConstant type;
	private String addr;
	private String code;
	private AddressType acAddrtype;
	private String acIin;
	private String acUidnumVid;
	private BigDecimal amount;
	private String currency;
	private CredType credType;
	private CredSubType credSubType;
	private String credData;

	public IssuerTransactionPayee() {
	}



	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "msgId", column = @Column(name = "MSG_ID", nullable = false, length = 36)),
			@AttributeOverride(name = "seqnum", column = @Column(name = "SEQNUM", nullable = false, length = 3)) })
	public IssuerTransactionPayeeId getId() {
		return this.id;
	}

	public void setId(IssuerTransactionPayeeId id) {
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

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE", nullable = false, length = 30)
	public PayerConstant getType() {
		return this.type;
	}

	public void setType(PayerConstant type) {
		this.type = type;
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

	@Enumerated(EnumType.STRING)
	@Column(name = "AC_ADDRTYPE", length = 50)
	public AddressType getAcAddrtype() {
		return this.acAddrtype;
	}

	public void setAcAddrtype(AddressType acAddrtype) {
		this.acAddrtype = acAddrtype;
	}

	@Column(name = "AC_IIN", length = 6)
	public String getAcIin() {
		return this.acIin;
	}

	public void setAcIin(String acIin) {
		this.acIin = acIin;
	}

	@Column(name = "AC_UIDNUM_VID", length = 16)
	public String getAcUidnumVid() {
		return this.acUidnumVid;
	}

	public void setAcUidnumVid(String acUidnumVid) {
		this.acUidnumVid = acUidnumVid;
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

	@Enumerated(EnumType.STRING)
	@Column(name = "CRED_TYPE", length = 20)
	public CredType getCredType() {
		return this.credType;
	}

	public void setCredType(CredType credType) {
		this.credType = credType;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "CRED_SUB_TYPE", length = 20)
	public CredSubType getCredSubType() {
		return this.credSubType;
	}

	public void setCredSubType(CredSubType credSubType) {
		this.credSubType = credSubType;
	}

	@Column(name = "CRED_DATA", length = 2048)
	public String getCredData() {
		return this.credData;
	}

	public void setCredData(String credData) {
		this.credData = credData;
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
