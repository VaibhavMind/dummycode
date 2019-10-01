package com.payasia.common.dto;

import java.io.Serializable;

public class EmailDataDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5031998125579231361L;
	private String employeeName;
	private String currentEmployeeName;
	private String firstName;
	private String lastName;
	private String employeeNumber;
	private String batchDesc;
	private Long timesheetId;
	private String emailTo;
	private String emailFrom;
	private String overtimeShiftType;
	private String reviewerFirstName;
	private String reviewerLastName;
	private Long reviewerCompanyId;
	private Long empCompanyId;
	
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
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
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getBatchDesc() {
		return batchDesc;
	}
	public void setBatchDesc(String batchDesc) {
		this.batchDesc = batchDesc;
	}
	public Long getTimesheetId() {
		return timesheetId;
	}
	public void setTimesheetId(Long timesheetId) {
		this.timesheetId = timesheetId;
	}
	public String getEmailTo() {
		return emailTo;
	}
	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}
	public String getOvertimeShiftType() {
		return overtimeShiftType;
	}
	public void setOvertimeShiftType(String overtimeShiftType) {
		this.overtimeShiftType = overtimeShiftType;
	}
	public String getReviewerFirstName() {
		return reviewerFirstName;
	}
	public void setReviewerFirstName(String reviewerFirstName) {
		this.reviewerFirstName = reviewerFirstName;
	}
	public Long getReviewerCompanyId() {
		return reviewerCompanyId;
	}
	public void setReviewerCompanyId(Long reviewerCompanyId) {
		this.reviewerCompanyId = reviewerCompanyId;
	}
	public String getReviewerLastName() {
		return reviewerLastName;
	}
	public void setReviewerLastName(String reviewerLastName) {
		this.reviewerLastName = reviewerLastName;
	}
	public String getEmailFrom() {
		return emailFrom;
	}
	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}
	public String getCurrentEmployeeName() {
		return currentEmployeeName;
	}
	public void setCurrentEmployeeName(String currentEmployeeName) {
		this.currentEmployeeName = currentEmployeeName;
	}
	public Long getEmpCompanyId() {
		return empCompanyId;
	}
	public void setEmpCompanyId(Long empCompanyId) {
		this.empCompanyId = empCompanyId;
	}
	
	
	

}
