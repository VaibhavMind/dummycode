package com.payasia.common.dto;

public class EmployeeLoginHistoryReportDTO {

	private static final long serialVersionUID = 1L;
	private String employeeNumber;
	private String employeeName;
	private String companyName;
	private String loggedInDate;
	private String IPAddress;
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getLoggedInDate() {
		return loggedInDate;
	}
	public void setLoggedInDate(String loggedInDate) {
		this.loggedInDate = loggedInDate;
	}
	public String getIPAddress() {
		return IPAddress;
	}
	public void setIPAddress(String iPAddress) {
		IPAddress = iPAddress;
	}
	
	
}
