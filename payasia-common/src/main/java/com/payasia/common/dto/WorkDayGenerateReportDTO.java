package com.payasia.common.dto;

public class WorkDayGenerateReportDTO {

	private String entiryName;
	private byte[] workBookByteArr;
	private String fileName;
	private String fileDatePeriod;
	private String payGroup;
	
	public String getEntiryName() {
		return entiryName;
	}
	public void setEntiryName(String entiryName) {
		this.entiryName = entiryName;
	}
	public String getPayGroup() {
		return payGroup;
	}
	public void setPayGroup(String payGroup) {
		this.payGroup = payGroup;
	}
	public byte[] getWorkBookByteArr() {
		return workBookByteArr;
	}
	public void setWorkBookByteArr(byte[] workBookByteArr) {
		this.workBookByteArr = workBookByteArr;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileDatePeriod() {
		return fileDatePeriod;
	}
	public void setFileDatePeriod(String fileDatePeriod) {
		this.fileDatePeriod = fileDatePeriod;
	}
	
	
	
}
