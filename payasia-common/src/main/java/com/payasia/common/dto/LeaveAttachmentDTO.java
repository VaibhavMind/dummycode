package com.payasia.common.dto;

import java.io.Serializable;

public class LeaveAttachmentDTO implements Serializable {
	
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4366499714392451916L;
	private Integer fileId;
	private String fileURL;
	public String getFileURL() {
		return fileURL;
	}
	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}
	public Integer getFileId() {
		return fileId;
	}
	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}	
	
	

}
