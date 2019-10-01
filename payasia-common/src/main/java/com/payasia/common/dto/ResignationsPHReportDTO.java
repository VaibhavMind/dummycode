package com.payasia.common.dto;

public class ResignationsPHReportDTO {
	
	private String employeeID;
	private String employeeName;
	private String cessationReason;
	private String cessationDate;
	private String remarks;
	
	public String getEmployeeID() {
		return employeeID;
	}
	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getCessationReason() {
		return cessationReason;
	}
	public void setCessationReason(String cessationReason) {
		this.cessationReason = cessationReason;
	}
	public String getCessationDate() {
		return cessationDate;
	}
	public void setCessationDate(String cessationDate) {
		this.cessationDate = cessationDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}																															

	

}
