package com.payasia.common.dto;

import java.io.Serializable;
import java.util.Arrays;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveApplicationAttachmentDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5995777111647602325L;
	private Long leaveApplicationId;
	private Long leaveApplicationAttachmentId;
	private String fileType;
	private String fileName;
	private String uploadedDate;
	private CommonsMultipartFile attachment;
	private byte[] attachmentBytes;
	private String attachmentPath;
	
	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}
	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getUploadedDate() {
		return uploadedDate;
	}
	public void setUploadedDate(String uploadedDate) {
		this.uploadedDate = uploadedDate;
	}
	public CommonsMultipartFile getAttachment() {
		return attachment;
	}
	public void setAttachment(CommonsMultipartFile attachment) {
		this.attachment = attachment;
	}
	public byte[] getAttachmentBytes() {
		return attachmentBytes;
	}
	public void setAttachmentBytes(byte[] attachmentBytes) {
		if (attachmentBytes != null) {
			this.attachmentBytes = Arrays.copyOf(attachmentBytes, attachmentBytes.length);
		}
	}
	public Long getLeaveApplicationAttachmentId() {
		return leaveApplicationAttachmentId;
	}
	public void setLeaveApplicationAttachmentId(
			Long leaveApplicationAttachmentId) {
		this.leaveApplicationAttachmentId = leaveApplicationAttachmentId;
	}
	public String getAttachmentPath() {
		return attachmentPath;
	}
	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}
	
}
