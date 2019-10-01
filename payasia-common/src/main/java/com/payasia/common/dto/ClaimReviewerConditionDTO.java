package com.payasia.common.dto;

import java.io.Serializable;

public class ClaimReviewerConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3446197933387386344L;
	private String employeeName;
	private String claimTemplateName;
	private String leaveReviewer1;
	private String leaveReviewer2;
	private String leaveReviewer3;
	private Long companyId;
	private Long employeeId;
	
	
	
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
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
	public String getClaimTemplateName() {
		return claimTemplateName;
	}
	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
