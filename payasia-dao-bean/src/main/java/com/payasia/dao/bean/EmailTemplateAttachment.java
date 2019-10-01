package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Email_Template_Attachment database table.
 * 
 */
@Entity
@Table(name = "Email_Template_Attachment")
public class EmailTemplateAttachment extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Email_Attachment_ID")
	private long emailAttachmentId;

	@Lob()
	@Column(name = "Attachment")
	private byte[] attachment;

	@Column(name = "File_Name")
	private String fileName;

	 
	@ManyToOne
	@JoinColumn(name = "Email_Template_ID")
	private EmailTemplate emailTemplate;

	public EmailTemplateAttachment() {
	}

	public long getEmailAttachmentId() {
		return this.emailAttachmentId;
	}

	public void setEmailAttachmentId(long emailAttachmentId) {
		this.emailAttachmentId = emailAttachmentId;
	}

	public byte[] getAttachment() {
		return this.attachment;
	}

	public void setAttachment(byte[] attachment) {
		if (attachment != null) {
			this.attachment = Arrays.copyOf(attachment, attachment.length);
		}
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public EmailTemplate getEmailTemplate() {
		return this.emailTemplate;
	}

	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

}