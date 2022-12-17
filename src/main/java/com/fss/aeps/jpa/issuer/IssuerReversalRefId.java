package com.fss.aeps.jpa.issuer;

import java.io.Serializable;
import java.util.Objects;

import com.fss.aeps.jaxb.RefType;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class IssuerReversalRefId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String msgId;
	private RefType type;
	private String seqnum;

	public IssuerReversalRefId() {
	}


	public IssuerReversalRefId(String msgId, RefType type, String seqnum) {
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
	public int hashCode() {
		return Objects.hash(msgId, seqnum, type);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		IssuerReversalRefId other = (IssuerReversalRefId) obj;
		return Objects.equals(msgId, other.msgId) && Objects.equals(seqnum, other.seqnum) && type == other.type;
	}



}
