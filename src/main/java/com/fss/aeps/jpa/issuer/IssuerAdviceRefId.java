package com.fss.aeps.jpa.issuer;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fss.aeps.jaxb.RefType;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@DynamicUpdate
@DynamicInsert
@Embeddable
public class IssuerAdviceRefId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String msgId;
	private RefType type;
	private String seqnum;

	public IssuerAdviceRefId() {
	}

	public IssuerAdviceRefId(String msgId, RefType type, String seqnum) {
		this.msgId = msgId;
		this.type = type;
		this.seqnum = seqnum;
	}

	@Column(name = "MSG_ID", nullable = false, length = 35)
	public String getMsgId() {
		return this.msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE", nullable = false, length = 30)
	public RefType getType() {
		return this.type;
	}

	public void setType(RefType type) {
		this.type = type;
	}

	@Column(name = "SEQNUM", nullable = false, length = 3)
	public String getSeqnum() {
		return this.seqnum;
	}

	public void setSeqnum(String seqnum) {
		this.seqnum = seqnum;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null) || !(other instanceof IssuerAdviceRefId))
			return false;
		IssuerAdviceRefId castOther = (IssuerAdviceRefId) other;

		return ((this.getMsgId() == castOther.getMsgId()) || (this.getMsgId() != null && castOther.getMsgId() != null
				&& this.getMsgId().equals(castOther.getMsgId())))
				&& ((this.getType() == castOther.getType()) || (this.getType() != null && castOther.getType() != null
						&& this.getType().equals(castOther.getType())))
				&& ((this.getSeqnum() == castOther.getSeqnum()) || (this.getSeqnum() != null
						&& castOther.getSeqnum() != null && this.getSeqnum().equals(castOther.getSeqnum())));
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + (getMsgId() == null ? 0 : this.getMsgId().hashCode());
		result = 37 * result + (getType() == null ? 0 : this.getType().hashCode());
		result = 37 * result + (getSeqnum() == null ? 0 : this.getSeqnum().hashCode());
		return result;
	}

}
