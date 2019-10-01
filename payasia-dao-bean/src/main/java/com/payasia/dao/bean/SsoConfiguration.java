package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the SSO_Configuration database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "SSO_Configuration")
public class SsoConfiguration extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SSO_Integration_ID")
	private long ssoIntegrationId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@Column(name = "IDP_Issuer")
	private String idpIssuer;

	@Column(name = "IDP_SSO_URL")
	private String idpssoUrl;

	@Column(name = "IDP_Metadata")
	private String idpMetadata;

	@Column(name = "IDP_Metadata_Url")
	private String idpmetadataUrl;

	@Column(name = "Is_Enable_SSO")
	private Boolean isEnableSso;

	public long getSsoIntegrationId() {
		return ssoIntegrationId;
	}

	public void setSsoIntegrationId(long ssoIntegrationId) {
		this.ssoIntegrationId = ssoIntegrationId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getIdpIssuer() {
		return idpIssuer;
	}

	public void setIdpIssuer(String idpIssuer) {
		this.idpIssuer = idpIssuer;
	}

	public String getIdpssoUrl() {
		return idpssoUrl;
	}

	public void setIdpssoUrl(String idpssoUrl) {
		this.idpssoUrl = idpssoUrl;
	}

	public String getIdpMetadata() {
		return idpMetadata;
	}

	public void setIdpMetadata(String idpMetadata) {
		this.idpMetadata = idpMetadata;
	}

	public String getIdpmetadataUrl() {
		return idpmetadataUrl;
	}

	public void setIdpmetadataUrl(String idpmetadataUrl) {
		this.idpmetadataUrl = idpmetadataUrl;
	}

	public Boolean getIsEnableSso() {
		return isEnableSso;
	}

	public void setIsEnableSso(Boolean isEnableSso) {
		this.isEnableSso = isEnableSso;
	}

}