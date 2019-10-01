package com.payasia.common.dto;

public class WorkdayTimeSheetDTO extends WorkDayReportDTO{
	
	private String employeeName;
	private String employeeID;
	private String externalID;
	private String locationId;
	private String payCategoryId;
	private String units;
	private String rate;
	
	public String getExternalID() {
		return externalID;
	}
	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getEmployeeID() {
		return employeeID;
	}
	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getPayCategoryId() {
		return payCategoryId;
	}
	public void setPayCategoryId(String payCategoryId) {
		this.payCategoryId = payCategoryId;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	
	
}
