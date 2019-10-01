package com.payasia.common.dto;

public class LeaveConditionDTO {
	
	private Long employeeId;
	private Long employeeLeaveSchemeTypeId;
	private String startDate;
	private String endDate;
	private Long startSession;
	private Long endSession;
	private boolean attachementStatus;
	private boolean isPost;
	private Long employeeLeaveApplicationId;
	private String dateFormat;
	private boolean isReviewer;
	private float totalHoursBetweenDates; 
	private boolean isLeaveUnitHours;
	
	
	public float getTotalHoursBetweenDates() {
		return totalHoursBetweenDates;
	}
	public void setTotalHoursBetweenDates(float totalHoursBetweenDates) {
		this.totalHoursBetweenDates = totalHoursBetweenDates;
	}
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	public boolean isPost() {
		return isPost;
	}
	public void setPost(boolean isPost) {
		this.isPost = isPost;
	}
	public boolean isAttachementStatus() {
		return attachementStatus;
	}
	public void setAttachementStatus(boolean attachementStatus) {
		this.attachementStatus = attachementStatus;
	}
	public Long getStartSession() {
		return startSession;
	}
	public void setStartSession(Long startSession) {
		this.startSession = startSession;
	}
	public Long getEndSession() {
		return endSession;
	}
	public void setEndSession(Long endSession) {
		this.endSession = endSession;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getEmployeeLeaveSchemeTypeId() {
		return employeeLeaveSchemeTypeId;
	}
	public void setEmployeeLeaveSchemeTypeId(Long employeeLeaveSchemeTypeId) {
		this.employeeLeaveSchemeTypeId = employeeLeaveSchemeTypeId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public Long getEmployeeLeaveApplicationId() {
		return employeeLeaveApplicationId;
	}
	public void setEmployeeLeaveApplicationId(Long employeeLeaveApplicationId) {
		this.employeeLeaveApplicationId = employeeLeaveApplicationId;
	}
	public boolean isReviewer() {
		return isReviewer;
	}
	public void setReviewer(boolean isReviewer) {
		this.isReviewer = isReviewer;
	}
	public boolean isLeaveUnitHours() {
		return isLeaveUnitHours;
	}
	public void setLeaveUnitHours(boolean isLeaveUnitHours) {
		this.isLeaveUnitHours = isLeaveUnitHours;
	}
	
	

}
