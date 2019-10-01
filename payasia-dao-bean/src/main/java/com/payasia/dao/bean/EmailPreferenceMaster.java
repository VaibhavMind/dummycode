package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Email_Preference_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Email_Preference_Master")
public class EmailPreferenceMaster extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Email_Pref_ID")
	private long emailPrefId;

	@Column(name = "Contact_Email")
	private String contactEmail;

	@Column(name = "Logo_Location")
	private String logoLocation;

	@Column(name = "System_Email")
	private String systemEmail;

	@Column(name = "Company_URL")
	private String companyUrl;

	@Column(name = "System_Email_For_Sending_Emails")
	private String systemEmailForSendingEmails;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	public EmailPreferenceMaster() {
	}

	public long getEmailPrefId() {
		return this.emailPrefId;
	}

	public void setEmailPrefId(long emailPrefId) {
		this.emailPrefId = emailPrefId;
	}

	public String getContactEmail() {
		return this.contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getLogoLocation() {
		return this.logoLocation;
	}

	public void setLogoLocation(String logoLocation) {
		this.logoLocation = logoLocation;
	}

	public String getSystemEmail() {
		return this.systemEmail;
	}

	public void setSystemEmail(String systemEmail) {
		this.systemEmail = systemEmail;
	}

	public String getSystemEmailForSendingEmails() {
		return this.systemEmailForSendingEmails;
	}

	public void setSystemEmailForSendingEmails(
			String systemEmailForSendingEmails) {
		this.systemEmailForSendingEmails = systemEmailForSendingEmails;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getCompanyUrl() {
		return companyUrl;
	}

	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
	}

}