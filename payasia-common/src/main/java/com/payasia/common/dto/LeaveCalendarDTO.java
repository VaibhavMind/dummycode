package com.payasia.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveCalendarDTO implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	private Long leaveApplicationId;
	private String leaveStatus;
	private String statusDate;
	private String leaveAppByEmployee;
	private long employeeLeaveId;
	
	public long getEmployeeLeaveId() {
		return employeeLeaveId;
	}
	public void setEmployeeLeaveId(long employeeLeaveId) {
		this.employeeLeaveId = employeeLeaveId;
	}
	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}
	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}
	public String getLeaveStatus() {
		return leaveStatus;
	}
	public void setLeaveStatus(String leaveStatus) {
		this.leaveStatus = leaveStatus;
	}
	public String getStatusDate() {
		return statusDate;
	}
	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}
	public String getLeaveAppByEmployee() {
		return leaveAppByEmployee;
	}
	public void setLeaveAppByEmployee(String leaveAppByEmployee) {
		this.leaveAppByEmployee = leaveAppByEmployee;
	}
	
	
	
	
	
	
}
