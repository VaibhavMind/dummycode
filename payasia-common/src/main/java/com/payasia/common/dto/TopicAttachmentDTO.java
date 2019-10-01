package com.payasia.common.dto;

import java.io.Serializable;
import java.util.Arrays;

import org.springframework.web.multipart.commons.CommonsMultipartFile;


public class TopicAttachmentDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6822609474670643729L;

	private String emailAttachmentId;

	private CommonsMultipartFile attachment;
	
	private String attachmentName;
	
	private byte[] attachmentBytes;
	
	private String attachmentPath;
	
	public byte[] getAttachmentBytes() {
		return attachmentBytes;
	}

	public void setAttachmentBytes(byte[] attachmentBytes) {
		if (attachmentBytes != null) {
			this.attachmentBytes = Arrays.copyOf(attachmentBytes, attachmentBytes.length);
		}
	}

	public String getEmailAttachmentId() {
		return emailAttachmentId;
	}

	public void setEmailAttachmentId(String emailAttachmentId) {
		this.emailAttachmentId = emailAttachmentId;
	}

	public CommonsMultipartFile getAttachment() {
		return attachment;
	}

	public void setAttachment(CommonsMultipartFile attachment) {
		this.attachment = attachment;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	public String getAttachmentPath() {
		return attachmentPath;
	}

	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}
	
}
