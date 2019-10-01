package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
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
 * The persistent class for the Claim_Application_Item_Attachment database
 * table.
 * 
 */
@Entity
@Table(name = "Claim_Application_Item_Attachment")
public class ClaimApplicationItemAttachment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Claim_Application_Item_Attachment_ID")
	private long claimApplicationItemAttachmentId;

	@Lob()
	@Column(name = "Attachment")
	private byte[] attachment;

	@Column(name = "File_Name")
	private String fileName;

	@Column(name = "File_Type")
	private String fileType;

	@Column(name = "Uploaded_Date")
	private Timestamp uploadedDate;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Application_Item_ID")
	private ClaimApplicationItem claimApplicationItem;

	public ClaimApplicationItemAttachment() {
	}

	public long getClaimApplicationItemAttachmentId() {
		return this.claimApplicationItemAttachmentId;
	}

	public void setClaimApplicationItemAttachmentId(
			long claimApplicationItemAttachmentId) {
		this.claimApplicationItemAttachmentId = claimApplicationItemAttachmentId;
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

	public String getFileType() {
		return this.fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Timestamp getUploadedDate() {
		return this.uploadedDate;
	}

	public void setUploadedDate(Timestamp uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public ClaimApplicationItem getClaimApplicationItem() {
		return this.claimApplicationItem;
	}

	public void setClaimApplicationItem(
			ClaimApplicationItem claimApplicationItem) {
		this.claimApplicationItem = claimApplicationItem;
	}

}