package com.payasia.common.dto;

public class WorkdayReportMasterDTO {
	
	private long workdayReportId;
	private String reportName;
	private String reportType;
	
	
	
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public long getWorkdayReportId() {
		return workdayReportId;
	}
	public void setWorkdayReportId(long workdayReportId) {
		this.workdayReportId = workdayReportId;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	

}
