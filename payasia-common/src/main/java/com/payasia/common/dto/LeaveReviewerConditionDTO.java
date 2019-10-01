package com.payasia.common.dto;

import java.io.Serializable;

public class LeaveReviewerConditionDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8511712173064539675L;
	private String employeeName;
	private String employeeNumber;
	private String leaveSchemeName;
	private String leaveType;
	private String leaveReviewer1;
	private String leaveReviewer2;
	private String leaveReviewer3;
	
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
	public String getLeaveReviewer1() {
		return leaveReviewer1;
	}
	public void setLeaveReviewer1(String leaveReviewer1) {
		this.leaveReviewer1 = leaveReviewer1;
	}
	public String getLeaveReviewer2() {
		return leaveReviewer2;
	}
	public void setLeaveReviewer2(String leaveReviewer2) {
		this.leaveReviewer2 = leaveReviewer2;
	}
	public String getLeaveReviewer3() {
		return leaveReviewer3;
	}
	public void setLeaveReviewer3(String leaveReviewer3) {
		this.leaveReviewer3 = leaveReviewer3;
	}
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	

}
