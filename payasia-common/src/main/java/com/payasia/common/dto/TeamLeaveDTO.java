package com.payasia.common.dto;

import java.io.Serializable;

public class TeamLeaveDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String employeeName;
	private String employeeNumber;
	private byte[] employeeImage;
	private Integer submittedLeaves;
	private Integer pendingLeavesForApproval;
	private boolean isLeaveReviewer;
	private boolean isOnLeave;
	
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
	public byte[] getEmployeeImage() {
		return employeeImage;
	}
	public void setEmployeeImage(byte[] employeeImage) {
		this.employeeImage = employeeImage;
	}
	public Integer getSubmittedLeaves() {
		return submittedLeaves;
	}
	public void setSubmittedLeaves(Integer submittedLeaves) {
		this.submittedLeaves = submittedLeaves;
	}
	public Integer getPendingLeavesForApproval() {
		return pendingLeavesForApproval;
	}
	public void setPendingLeavesForApproval(Integer pendingLeavesForApproval) {
		this.pendingLeavesForApproval = pendingLeavesForApproval;
	}
	public boolean isLeaveReviewer() {
		return isLeaveReviewer;
	}
	public void setLeaveReviewer(boolean isLeaveReviewer) {
		this.isLeaveReviewer = isLeaveReviewer;
	}
	public boolean isOnLeave() {
		return isOnLeave;
	}
	public void setOnLeave(boolean isOnLeave) {
		this.isOnLeave = isOnLeave;
	}
	
}
