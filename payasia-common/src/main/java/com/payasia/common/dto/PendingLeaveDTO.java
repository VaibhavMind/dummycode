package com.payasia.common.dto;

public class PendingLeaveDTO {
	
	private String leaveType;
	
	private String pendingEmployeeName;
	private String createdDate;
	private String status;
	private String fromDate;
	private String toDate;
	
	

	public String getPendingEmployeeName() {
		return pendingEmployeeName;
	}

	public void setPendingEmployeeName(String pendingEmployeeName) {
		this.pendingEmployeeName = pendingEmployeeName;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	
	
	
	

}
