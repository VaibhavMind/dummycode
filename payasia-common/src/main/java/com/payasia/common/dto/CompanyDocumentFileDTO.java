package com.payasia.common.dto;

import java.io.Serializable;


public class CompanyDocumentFileDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1243116247728466490L;
	private java.io.InputStream inputStream;
	private String fileName;
	private String tempFileName;
	private java.util.zip.ZipEntry zipEntry;
	
	
	
	
	public java.util.zip.ZipEntry getZipEntry() {
		return zipEntry;
	}
	public void setZipEntry(java.util.zip.ZipEntry zipEntry) {
		this.zipEntry = zipEntry;
	}
	public String getTempFileName() {
		return tempFileName;
	}
	public void setTempFileName(String tempFileName) {
		this.tempFileName = tempFileName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public java.io.InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(java.io.InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	
	
	
	

}
