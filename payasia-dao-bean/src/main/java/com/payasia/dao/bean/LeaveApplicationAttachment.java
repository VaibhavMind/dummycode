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
 * The persistent class for the Leave_Application_Attachment database table.
 * 
 */
@Entity
@Table(name = "Leave_Application_Attachment")
public class LeaveApplicationAttachment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Application_Attachment_ID")
	private long leaveApplicationAttachmentId;

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
	@JoinColumn(name = "Leave_Application_ID")
	private LeaveApplication leaveApplication;

	public LeaveApplicationAttachment() {
	}

	public long getLeaveApplicationAttachmentId() {
		return this.leaveApplicationAttachmentId;
	}

	public void setLeaveApplicationAttachmentId(
			long leaveApplicationAttachmentId) {
		this.leaveApplicationAttachmentId = leaveApplicationAttachmentId;
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

	public LeaveApplication getLeaveApplication() {
		return this.leaveApplication;
	}

	public void setLeaveApplication(LeaveApplication leaveApplication) {
		this.leaveApplication = leaveApplication;
	}

}