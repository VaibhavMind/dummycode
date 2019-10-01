/**
 * @author vivekjain
 *
 */
package com.payasia.common.dto;

import java.io.Serializable;

/**
 * The Class SsoConfigurationDTO.
 */
public class SsoConfigurationDTO implements Serializable {

/**
	 * 
	 */
	private static final long serialVersionUID = -3626964433853522840L;

	/** The company id. */
	private Long companyId;

	/** The idpSsoUrl. */
	private String idpSsoUrl;
	
	/** The idpIssuer. */
	private String idpIssuer;

	/** The metaData. */
	private String metaData;
	
	/** The metadataUrl. */
	private String metadataUrl;

	private Boolean isEnableSso = false;
	
	private String samlSsoUrl;
	
	private String samlSpEntityId;
	
	
	public String getSamlSsoUrl() {
		return samlSsoUrl;
	}

	public void setSamlSsoUrl(String samlSsoUrl) {
		this.samlSsoUrl = samlSsoUrl;
	}

	public String getSamlSpEntityId() {
		return samlSpEntityId;
	}

	public void setSamlSpEntityId(String samlSpEntityId) {
		this.samlSpEntityId = samlSpEntityId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getIdpSsoUrl() {
		return idpSsoUrl;
	}

	public void setIdpSsoUrl(String idpSsoUrl) {
		this.idpSsoUrl = idpSsoUrl;
	}

	public String getIdpIssuer() {
		return idpIssuer;
	}

	public void setIdpIssuer(String idpIssuer) {
		this.idpIssuer = idpIssuer;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public String getMetadataUrl() {
		return metadataUrl;
	}

	public void setMetadataUrl(String metadataUrl) {
		this.metadataUrl = metadataUrl;
	}

	public Boolean getIsEnableSso() {
		return isEnableSso;
	}

	public void setIsEnableSso(Boolean isEnableSso) {
		this.isEnableSso = isEnableSso;
	}
	
	
}
