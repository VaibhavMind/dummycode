package com.payasia.common.dto;

public class WorkdayPaygroupBatchDTO {

	private Long workdayPaygroupBatchId;
	private String workdayStartDate;
	private String workdayEndDate;
	private String year;
	
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getWorkdayStartDate() {
		return workdayStartDate;
	}
	public Long getWorkdayPaygroupBatchId() {
		return workdayPaygroupBatchId;
	}
	public void setWorkdayPaygroupBatchId(Long workdayPaygroupBatchId) {
		this.workdayPaygroupBatchId = workdayPaygroupBatchId;
	}
	public void setWorkdayStartDate(String workdayStartDate) {
		this.workdayStartDate = workdayStartDate;
	}
	public String getWorkdayEndDate() {
		return workdayEndDate;
	}
	public void setWorkdayEndDate(String workdayEndDate) {
		this.workdayEndDate = workdayEndDate;
	}
	
	
	
}
