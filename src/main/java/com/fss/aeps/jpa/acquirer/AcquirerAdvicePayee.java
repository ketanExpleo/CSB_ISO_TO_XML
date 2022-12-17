package com.fss.aeps.jpa.acquirer;

import java.io.Serializable;
import java.math.BigDecimal;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
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

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "ACQUIRER_ADVICE_PAYEE")
public class AcquirerAdvicePayee implements Serializable, Persistable<AcquirerAdvicePayeeId> {

	private static final long serialVersionUID = 1L;

	@Transient
	private boolean isNew = true;

	private AcquirerAdvicePayeeId id;
	private String addr;
	private String type;
	private String code;
	private String acAddrtype;
	private String acIin;
	private String acUidnumVid;
	private BigDecimal amount;
	private String currency;

	public AcquirerAdvicePayee() {
	}

	public AcquirerAdvicePayee(AcquirerAdvicePayeeId id) {
		this.id = id;
	}


	@Override
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "msgId", column = @Column(name = "MSG_ID", nullable = false, length = 36)),
			@AttributeOverride(name = "seqnum", column = @Column(name = "SEQNUM", nullable = false, length = 3)) })
	public AcquirerAdvicePayeeId getId() {
		return this.id;
	}

	public void setId(AcquirerAdvicePayeeId id) {
		this.id = id;
	}

	@Column(name = "ADDR", length = 100)
	public String getAddr() {
		return this.addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	@Column(name = "TYPE", length = 30)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "CODE", length = 6)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "AC_ADDRTYPE", length = 50)
	public String getAcAddrtype() {
		return this.acAddrtype;
	}

	public void setAcAddrtype(String acAddrtype) {
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
