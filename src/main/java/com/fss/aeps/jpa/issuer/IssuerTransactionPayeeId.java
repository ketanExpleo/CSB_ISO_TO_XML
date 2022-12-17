package com.fss.aeps.jpa.issuer;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class IssuerTransactionPayeeId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String msgId;
	private String seqnum;

	public IssuerTransactionPayeeId() {
	}

	public IssuerTransactionPayeeId(String msgId, String seqnum) {
		this.msgId = msgId;
		this.seqnum = seqnum;
	}

	@Column(name = "MSG_ID", nullable = false, length = 36)
	public String getMsgId() {
		return this.msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
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
		return Objects.hash(msgId, seqnum);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;
		IssuerTransactionPayeeId other = (IssuerTransactionPayeeId) obj;
		return Objects.equals(msgId, other.msgId) && Objects.equals(seqnum, other.seqnum);
	}

}
