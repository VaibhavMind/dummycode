package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the Integration_Master database table.
 * 
 */
@Entity
@Table(name = "Integration_Master")
public class IntegrationMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Integration_ID")
	private long integrationId;

	@Column(name = "Integration_Name")
	private String integrationName;

	@Column(name = "Username")
	private String username;

	@Column(name = "Password")
	private String password;

	@Column(name = "Base_URL")
	private String baseURL;

	@Column(name = "API_Key")
	private String apiKey;

	@Column(name = "Company_ID")
	private Long companyId;

	@Column(name = "External_Company_ID")
	private Long externalCompanyId;

	@Column(name = "Active")
	private boolean active;

	public IntegrationMaster() {
	}

	public long getIntegrationId() {
		return integrationId;
	}

	public void setIntegrationId(long integrationId) {
		this.integrationId = integrationId;
	}

	public String getIntegrationName() {
		return integrationName;
	}

	public void setIntegrationName(String integrationName) {
		this.integrationName = integrationName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getExternalCompanyId() {
		return externalCompanyId;
	}

	public void setExternalCompanyId(Long externalCompanyId) {
		this.externalCompanyId = externalCompanyId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}