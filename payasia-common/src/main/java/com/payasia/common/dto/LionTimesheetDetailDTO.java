package com.payasia.common.dto;

import java.io.Serializable;

public class LionTimesheetDetailDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3544314311006552093L;
	
	private Long employeeId;
	private Long timesheetId;
	private String timesheetStatus ;
	private String timesheetDate ;
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getTimesheetId() {
		return timesheetId;
	}
	public void setTimesheetId(Long timesheetId) {
		this.timesheetId = timesheetId;
	}
	public String getTimesheetStatus() {
		return timesheetStatus;
	}
	public void setTimesheetStatus(String timesheetStatus) {
		this.timesheetStatus = timesheetStatus;
	}
	public String getTimesheetDate() {
		return timesheetDate;
	}
	public void setTimesheetDate(String timesheetDate) {
		this.timesheetDate = timesheetDate;
	}
	
	
}
