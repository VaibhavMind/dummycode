package com.payasia.common.form;

import java.io.Serializable;

public class LeaveReminderDTO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3923216986981693358L;
	private String URL;
	private String emailFrom;
	private String emailTo;
	private Long employeeId;
	private String employeeName;
	private String employeeEmail;
	private String employeeNumber;
	private String timesheetBatch;
	private Long timesheetId;
	private String overtimeShiftType;

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeEmail() {
		return employeeEmail;
	}

	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getTimesheetBatch() {
		return timesheetBatch;
	}

	public void setTimesheetBatch(String timesheetBatch) {
		this.timesheetBatch = timesheetBatch;
	}

	public Long getTimesheetId() {
		return timesheetId;
	}

	public void setTimesheetId(Long timesheetId) {
		this.timesheetId = timesheetId;
	}

	public String getOvertimeShiftType() {
		return overtimeShiftType;
	}

	public void setOvertimeShiftType(String overtimeShiftType) {
		this.overtimeShiftType = overtimeShiftType;
	}
	
	

}
