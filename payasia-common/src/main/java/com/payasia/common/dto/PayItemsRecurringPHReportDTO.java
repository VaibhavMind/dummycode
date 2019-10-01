package com.payasia.common.dto;

public class PayItemsRecurringPHReportDTO {
	
	private String employeeID;
	private String employeeName;
	private String payCode;
	private String payDescription;
	private String recurringAmount;
	private String startYear;
	private String startMonth;
	private String endYear;
	private String endMonth;
	private String endCounter;
	private String suspendRecurring;
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
	public String getPayCode() {
		return payCode;
	}
	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}
	public String getPayDescription() {
		return payDescription;
	}
	public void setPayDescription(String payDescription) {
		this.payDescription = payDescription;
	}
	public String getRecurringAmount() {
		return recurringAmount;
	}
	public void setRecurringAmount(String recurringAmount) {
		this.recurringAmount = recurringAmount;
	}
	public String getStartYear() {
		return startYear;
	}
	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}
	public String getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}
	public String getEndYear() {
		return endYear;
	}
	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}
	public String getEndMonth() {
		return endMonth;
	}
	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getEndCounter() {
		return endCounter;
	}
	public void setEndCounter(String endCounter) {
		this.endCounter = endCounter;
	}
	public String getSuspendRecurring() {
		return suspendRecurring;
	}
	public void setSuspendRecurring(String suspendRecurring) {
		this.suspendRecurring = suspendRecurring;
	}
	
	

}
