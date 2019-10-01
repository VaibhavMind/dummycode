package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Email_Template database table.
 * 
 */
@Entity
@Table(name = "Email_Template")
public class EmailTemplate extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Email_Template_ID")
	private long emailTemplateId;

	@Column(name = "Body")
	private String body;

	@Column(name = "Name")
	private String name;

	@Column(name = "Subject")
	private String subject;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@OneToMany(mappedBy = "emailTemplate")
	private Set<EmailTemplateAttachment> emailTemplateAttachments;

	 
	@ManyToOne
	@JoinColumn(name = "Email_Template_Sub_Category_ID")
	private EmailTemplateSubCategoryMaster emailTemplateSubCategoryMaster;

	 
	@OneToMany(mappedBy = "sendMailBeforeMailTemplate")
	private Set<ReminderEventConfig> sendMailBeforeReminderEventConfigs;

	 
	@OneToMany(mappedBy = "sendMailOnEventMailTemplate")
	private Set<ReminderEventConfig> sendMailOnEventReminderEventConfigs;

	 
	@OneToMany(mappedBy = "sendMailAfterMailTemplate")
	private Set<ReminderEventConfig> sendMailAfterReminderEventConfigs;

	public EmailTemplate() {
	}

	public long getEmailTemplateId() {
		return this.emailTemplateId;
	}

	public void setEmailTemplateId(long emailTemplateId) {
		this.emailTemplateId = emailTemplateId;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<EmailTemplateAttachment> getEmailTemplateAttachments() {
		return this.emailTemplateAttachments;
	}

	public void setEmailTemplateAttachments(
			Set<EmailTemplateAttachment> emailTemplateAttachments) {
		this.emailTemplateAttachments = emailTemplateAttachments;
	}

	public EmailTemplateSubCategoryMaster getEmailTemplateSubCategoryMaster() {
		return this.emailTemplateSubCategoryMaster;
	}

	public void setEmailTemplateSubCategoryMaster(
			EmailTemplateSubCategoryMaster emailTemplateSubCategoryMaster) {
		this.emailTemplateSubCategoryMaster = emailTemplateSubCategoryMaster;
	}

	public Set<ReminderEventConfig> getSendMailBeforeReminderEventConfigs() {
		return sendMailBeforeReminderEventConfigs;
	}

	public void setSendMailBeforeReminderEventConfigs(
			Set<ReminderEventConfig> sendMailBeforeReminderEventConfigs) {
		this.sendMailBeforeReminderEventConfigs = sendMailBeforeReminderEventConfigs;
	}

	public Set<ReminderEventConfig> getSendMailOnEventReminderEventConfigs() {
		return sendMailOnEventReminderEventConfigs;
	}

	public void setSendMailOnEventReminderEventConfigs(
			Set<ReminderEventConfig> sendMailOnEventReminderEventConfigs) {
		this.sendMailOnEventReminderEventConfigs = sendMailOnEventReminderEventConfigs;
	}

	public Set<ReminderEventConfig> getSendMailAfterReminderEventConfigs() {
		return sendMailAfterReminderEventConfigs;
	}

	public void setSendMailAfterReminderEventConfigs(
			Set<ReminderEventConfig> sendMailAfterReminderEventConfigs) {
		this.sendMailAfterReminderEventConfigs = sendMailAfterReminderEventConfigs;
	}

}