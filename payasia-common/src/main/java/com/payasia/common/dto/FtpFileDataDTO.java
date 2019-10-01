package com.payasia.common.dto;

import java.io.Serializable;

public class FtpFileDataDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] data;
	private String fileName;
	private boolean isEmployeeData;
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public boolean isEmployeeData() {
		return isEmployeeData;
	}
	public void setEmployeeData(boolean isEmployeeData) {
		this.isEmployeeData = isEmployeeData;
	}

}
