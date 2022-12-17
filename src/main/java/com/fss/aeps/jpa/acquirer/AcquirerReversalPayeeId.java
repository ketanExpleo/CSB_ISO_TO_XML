package com.fss.aeps.jpa.acquirer;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AcquirerReversalPayeeId implements Serializable {

	private static final long	serialVersionUID	= 1L;

	private String				msgId;
	private String				seqnum;

	public AcquirerReversalPayeeId() {
	}

	public AcquirerReversalPayeeId(String msgId, String seqnum) {
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
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null) || !(other instanceof AcquirerReversalPayeeId))
			return false;
		AcquirerReversalPayeeId castOther = (AcquirerReversalPayeeId) other;

		return ((this.getMsgId() == castOther.getMsgId()) || (this.getMsgId() != null && castOther.getMsgId() != null
				&& this.getMsgId().equals(castOther.getMsgId())))
				&& ((this.getSeqnum() == castOther.getSeqnum()) || (this.getSeqnum() != null
						&& castOther.getSeqnum() != null && this.getSeqnum().equals(castOther.getSeqnum())));
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + (getMsgId() == null ? 0 : this.getMsgId().hashCode());
		result = 37 * result + (getSeqnum() == null ? 0 : this.getSeqnum().hashCode());
		return result;
	}

}
