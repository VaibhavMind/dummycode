package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Company_External_Link database table.
 * 
 */
@Entity
@Table(name = "Email_Attachment")
public class EmailAttachment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Email_Attachment_ID")
	private long emailAttachmentId;

	@Column(name = "File_Name")
	private String fileName;

	@Lob()
	@Column(name = "Attachment")
	private byte[] attachment;

	 
	@ManyToOne
	@JoinColumn(name = "Email_ID")
	private Email email;

	public EmailAttachment() {
	}

	public long getEmailAttachmentId() {
		return emailAttachmentId;
	}

	public void setEmailAttachmentId(long emailAttachmentId) {
		this.emailAttachmentId = emailAttachmentId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getAttachment() {
		return attachment;
	}

	public void setAttachment(byte[] attachment) {
		if (attachment != null) {
			this.attachment = Arrays.copyOf(attachment, attachment.length);
		}
	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

}