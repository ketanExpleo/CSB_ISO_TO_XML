package com.fss.aeps.jpa;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "AEPS_CONFIG")
public class AepsConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	private String	param;
	private String	value;
	private String	description;

	public AepsConfig() {
	}

	public AepsConfig(String param, String value) {
		this.param = param;
		this.value = value;
	}

	public AepsConfig(String param, String value, String description) {
		this.param = param;
		this.value = value;
		this.description = description;
	}

	@Id
	@Column(name = "PARAM", unique = true, nullable = false, length = 256)
	public String getParam() {
		return this.param;
	}

	public void setParam(String param) {
		this.param = param;
	}


	@org.hibernate.annotations.ColumnTransformer
	@Column(name = "VALUE", nullable = false, length = 2048)
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Column(name = "DESCRIPTION", length = 2048)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
