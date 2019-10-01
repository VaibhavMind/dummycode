package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Company_External_Link database table.
 * 
 */
@Entity
@Table(name = "Email")
public class Email implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Email_ID")
	private long emailId;

	@Column(name = "Email_From")
	private String emailFrom;

	@Column(name = "Email_To")
	private String emailTo;

	@Column(name = "Email_CC")
	private String emailCC;

	@Column(name = "Email_BCC")
	private String emailBCC;

	@Column(name = "Subject")
	private String subject;

	@Column(name = "Body")
	private String body;

	@Column(name = "Created_Date")
	private Timestamp createdDate;

	@Column(name = "Sent_Date")
	private Timestamp sentDate;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@OneToMany(mappedBy = "email")
	private Set<EmailAttachment> emailAttachments;

	public Email() {
	}

	public long getEmailId() {
		return emailId;
	}

	public void setEmailId(long emailId) {
		this.emailId = emailId;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getSentDate() {
		return sentDate;
	}

	public void setSentDate(Timestamp sentDate) {
		this.sentDate = sentDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<EmailAttachment> getEmailAttachments() {
		return emailAttachments;
	}

	public void setEmailAttachments(Set<EmailAttachment> emailAttachments) {
		this.emailAttachments = emailAttachments;
	}

	public String getEmailBCC() {
		return emailBCC;
	}

	public void setEmailBCC(String emailBCC) {
		this.emailBCC = emailBCC;
	}

}