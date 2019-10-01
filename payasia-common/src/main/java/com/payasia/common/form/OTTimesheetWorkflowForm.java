package com.payasia.common.form;

import java.io.Serializable;

public class OTTimesheetWorkflowForm implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String employeeInfo;
	private String createdDate;
	private String status;
	private String Remarks;
	private String userRemarks;
	private Integer order;
	private String createdBy;
	
	private String otTimesheetReviewer;
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getOtTimesheetReviewer() {
		return otTimesheetReviewer;
	}
	public void setOtTimesheetReviewer(String otTimesheetReviewer) {
		this.otTimesheetReviewer = otTimesheetReviewer;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getEmployeeInfo() {
		return employeeInfo;
	}
	public void setEmployeeInfo(String employeeInfo) {
		this.employeeInfo = employeeInfo;
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
	public String getRemarks() {
		return Remarks;
	}
	public void setRemarks(String remarks) {
		Remarks = remarks;
	}
	public String getUserRemarks() {
		return userRemarks;
	}
	public void setUserRemarks(String userRemarks) {
		this.userRemarks = userRemarks;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	
}
