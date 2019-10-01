package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class LeaveReportDataDTO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6083504707284690235L;
	private Long employeeId;
	private String employeeNo;
	private String employeeName;
	private String leaveSchemeName;
	private String leaveTypeName;
	private BigDecimal days;
	private String remarks;
	private String firstName;
	private String lastName;
	private String counter;
	
	

	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmployeeNo() {
		return employeeNo;
	}
	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getLeaveSchemeName() {
		return leaveSchemeName;
	}
	public void setLeaveSchemeName(String leaveSchemeName) {
		this.leaveSchemeName = leaveSchemeName;
	}
	public String getLeaveTypeName() {
		return leaveTypeName;
	}
	public void setLeaveTypeName(String leaveTypeName) {
		this.leaveTypeName = leaveTypeName;
	}
	
	
	public BigDecimal getDays() {
		return days;
	}
	public void setDays(BigDecimal days) {
		this.days = days;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getCounter() {
		return counter;
	}
	public void setCounter(String counter) {
		this.counter = counter;
	}
	
	
	
	
	
	
	
	

}
