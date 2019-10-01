package com.payasia.common.dto;

import java.io.Serializable;

public class EmployeeLeaveDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5923142755084654082L;
	private String employeeName;
	private String employeeNumber;
	private String leaveTypeName;
	private String fromDate;
	private String toDate;
	private Float noOfDays;
	private String leaveStatus;
	
	
	
	
	
	
	
	public String getLeaveStatus() {
		return leaveStatus;
	}
	public void setLeaveStatus(String leaveStatus) {
		this.leaveStatus = leaveStatus;
	}
	public Float getNoOfDays() {
		return noOfDays;
	}
	public void setNoOfDays(Float noOfDays) {
		this.noOfDays = noOfDays;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getLeaveTypeName() {
		return leaveTypeName;
	}
	public void setLeaveTypeName(String leaveTypeName) {
		this.leaveTypeName = leaveTypeName;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	
	

}
