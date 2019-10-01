package com.payasia.common.dto;

public class WorkDaySummrizedHoursDTO extends WorkDayReportDTO{
	
	private String employeeID;
	private String type;
	private String noOfHours;
	private String date;
	
	public String getEmployeeID() {
		return employeeID;
	}
	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNoOfHours() {
		return noOfHours;
	}
	public void setNoOfHours(String noOfHours) {
		this.noOfHours = noOfHours;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
	

}
