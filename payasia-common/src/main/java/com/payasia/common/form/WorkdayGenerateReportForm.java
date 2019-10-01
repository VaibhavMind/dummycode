package com.payasia.common.form;

public class WorkdayGenerateReportForm {

	private Long companyId;
	private String batchType;
	private String batchYear;
	private Long batchPeriod;
	private Long workDayReport;
	
	
	public Long getWorkDayReport() {
		return workDayReport;
	}
	public void setWorkDayReport(Long workDayReport) {
		this.workDayReport = workDayReport;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getBatchType() {
		return batchType;
	}
	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}
	public String getBatchYear() {
		return batchYear;
	}
	public void setBatchYear(String batchYear) {
		this.batchYear = batchYear;
	}
	public Long getBatchPeriod() {
		return batchPeriod;
	}
	public void setBatchPeriod(Long batchPeriod) {
		this.batchPeriod = batchPeriod;
	}
	
	
}
