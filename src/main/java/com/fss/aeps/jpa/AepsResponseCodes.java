package com.fss.aeps.jpa;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "AEPS_RESPONSE_CODES")
public class AepsResponseCodes implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String onlineRespCode;
	private String offlineRespCode;
	private String uidaiErrorCode;
	private String description;
	private String type;

	public AepsResponseCodes() {
	}

	@Id
	@Column(name = "ONLINE_RESP_CODE", unique = true, nullable = false, length = 3)
	public String getOnlineRespCode() {
		return this.onlineRespCode;
	}

	public void setOnlineRespCode(String onlineRespCode) {
		this.onlineRespCode = onlineRespCode;
	}

	@Column(name = "OFFLINE_RESP_CODE", length = 3)
	public String getOfflineRespCode() {
		return this.offlineRespCode;
	}

	public void setOfflineRespCode(String offlineRespCode) {
		this.offlineRespCode = offlineRespCode;
	}

	@Column(name = "UIDAI_ERROR_CODE", length = 10)
	public String getUidaiErrorCode() {
		return this.uidaiErrorCode;
	}

	public void setUidaiErrorCode(String uidaiErrorCode) {
		this.uidaiErrorCode = uidaiErrorCode;
	}

	@Column(name = "DESCRIPTION", length = 2048)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "TYPE", length = 20)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
