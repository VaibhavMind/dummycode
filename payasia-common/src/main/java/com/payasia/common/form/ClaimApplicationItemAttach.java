package com.payasia.common.form;

import java.io.Serializable;
import java.util.Arrays;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ClaimApplicationItemAttach implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9064742798477141339L;
	private Long claimApplicationItemAttachementId;
	private Long claimApplicationItemId;
	private String fileType;
	private String fileName;
	private String uploadedDate;
	private CommonsMultipartFile Attachment;
	private byte[] attachmentBytes;
	
	
	public byte[] getAttachmentBytes() {
		return attachmentBytes;
	}
	public void setAttachmentBytes(byte[] attachmentBytes) {
		if (attachmentBytes != null) {
			this.attachmentBytes = Arrays.copyOf(attachmentBytes, attachmentBytes.length);
		}
	}
	public Long getClaimApplicationItemAttachementId() {
		return claimApplicationItemAttachementId;
	}
	public void setClaimApplicationItemAttachementId(
			Long claimApplicationItemAttachementId) {
		this.claimApplicationItemAttachementId = claimApplicationItemAttachementId;
	}
	public Long getClaimApplicationItemId() {
		return claimApplicationItemId;
	}
	public void setClaimApplicationItemId(Long claimApplicationItemId) {
		this.claimApplicationItemId = claimApplicationItemId;
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
		return Attachment;
	}
	public void setAttachment(CommonsMultipartFile attachment) {
		Attachment = attachment;
	}
	
	
}
