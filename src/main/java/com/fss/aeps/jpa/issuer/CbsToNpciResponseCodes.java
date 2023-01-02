package com.fss.aeps.jpa.issuer;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "CBS_TO_NPCI_RESPONSE_CODES")
public class CbsToNpciResponseCodes implements Serializable {

	private static final long serialVersionUID = 1L;

	private String cbsCode;
	private String npciCode;
	private String description;

	public CbsToNpciResponseCodes() {
	}

	public CbsToNpciResponseCodes(String cbsCode, String npciCode) {
		this.cbsCode = cbsCode;
		this.npciCode = npciCode;
	}

	public CbsToNpciResponseCodes(String cbsCode, String npciCode, String description) {
		this.cbsCode = cbsCode;
		this.npciCode = npciCode;
		this.description = description;
	}

	@Id
	@Column(name = "CBS_CODE", unique = true, nullable = false, length = 10)
	public String getCbsCode() {
		return this.cbsCode;
	}

	public void setCbsCode(String cbsCode) {
		this.cbsCode = cbsCode;
	}

	@Column(name = "NPCI_CODE", nullable = false, length = 3)
	public String getNpciCode() {
		return this.npciCode;
	}

	public void setNpciCode(String npciCode) {
		this.npciCode = npciCode;
	}

	@Column(name = "DESCRIPTION", length = 1024)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
