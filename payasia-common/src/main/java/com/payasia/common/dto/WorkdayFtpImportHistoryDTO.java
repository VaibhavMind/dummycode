package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class WorkdayFtpImportHistoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String importFileName;
	private String importType;
	private String importStatus;
	private Timestamp fromDate;
	private Timestamp toDate;
	private String fromDateString;
	private String toDateString;
	
	public String getImportFileName() {
		return importFileName;
	}
	public void setImportFileName(String importFileName) {
		this.importFileName = importFileName;
	}
	
	public String getImportType() {
		return importType;
	}
	public void setImportType(String importType) {
		this.importType = importType;
	}
	public String getImportStatus() {
		return importStatus;
	}
	public void setImportStatus(String importStatus) {
		this.importStatus = importStatus;
	}
	public Timestamp getFromDate() {
		return fromDate;
	}
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	public Timestamp getToDate() {
		return toDate;
	}
	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}
	public String getFromDateString() {
		return fromDateString;
	}
	public void setFromDateString(String fromDateString) {
		this.fromDateString = fromDateString;
	}
	public String getToDateString() {
		return toDateString;
	}
	public void setToDateString(String toDateString) {
		this.toDateString = toDateString;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
