package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class PendingOTTimesheetConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4371755847331764799L;
	
	private String employeeName;
	private String createdDate;
	private String batch;
	private String otTimesheetStatusName;
	private Long employeeId;
	private List<String> statusNameList; 
	private boolean pendingStatus;
	
	public String getOtTimesheetStatusName() {
		return otTimesheetStatusName;
	}
	public void setOtTimesheetStatusName(String otTimesheetStatusName) {
		this.otTimesheetStatusName = otTimesheetStatusName;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public List<String> getStatusNameList() {
		return statusNameList;
	}
	public void setStatusNameList(List<String> statusNameList) {
		this.statusNameList = statusNameList;
	}
	public boolean isPendingStatus() {
		return pendingStatus;
	}
	public void setPendingStatus(boolean pendingStatus) {
		this.pendingStatus = pendingStatus;
	}
	
}
