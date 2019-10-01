package com.payasia.common.form;

import java.io.Serializable;

public class LionTimesheetReviewerForm implements Serializable{
	
	private static final long serialVersionUID = -9041056258686795781L;
	private String status;
	private String ruleName;
	private String ruleValue;
	private String employeeStatus;
	private String employeeName;
	private String lundinReviewer1;
	
	private long employeeId;

	private Long lundinReviewerId1;
	private Long lundinReviewerRuleId;
	private Long lundinReviewerRuleId1;
	private Long employeeLundinReviewerId;
	
	private String email;
	
	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}
	public String getEmployeeStatus() {
		return employeeStatus;
	}


	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getEmployeeName() {
		return employeeName;
	}


	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}


	public String getLundinReviewer1() {
		return lundinReviewer1;
	}


	public void setLundinReviewer1(String lundinReviewer1) {
		this.lundinReviewer1 = lundinReviewer1;
	}


	


	public Long getLundinReviewerId1() {
		return lundinReviewerId1;
	}


	public void setLundinReviewerId1(Long lundinReviewerId1) {
		this.lundinReviewerId1 = lundinReviewerId1;
	}


	public Long getEmployeeLundinReviewerId() {
		return employeeLundinReviewerId;
	}


	public void setEmployeeLundinReviewerId(Long employeeLundinReviewerId) {
		this.employeeLundinReviewerId = employeeLundinReviewerId;
	}


	public long getEmployeeId() {
		return employeeId;
	}


	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}


	public String getRuleName() {
		return ruleName;
	}


	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}


	public String getRuleValue() {
		return ruleValue;
	}


	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}


	public Long getLundinReviewerRuleId() {
		return lundinReviewerRuleId;
	}


	public void setLundinReviewerRuleId(Long lundinReviewerRuleId) {
		this.lundinReviewerRuleId = lundinReviewerRuleId;
	}


	public Long getLundinReviewerRuleId1() {
		return lundinReviewerRuleId1;
	}


	public void setLundinReviewerRuleId1(Long lundinReviewerRuleId1) {
		this.lundinReviewerRuleId1 = lundinReviewerRuleId1;
	}


	
}
