package com.payasia.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeLeaveApplicationReviewerDTO implements Serializable {
	

	private static final long serialVersionUID = 1L;
	private Long leaveApplicationReviewerId;
	
	private String leaveApplicationReviewer;
	private String ruleValue;
	private byte[] leaveApplicationReviewerImage;
	private String reviewerType;
	public Long getLeaveApplicationReviewerId() {
		return leaveApplicationReviewerId;
	}
	public void setLeaveApplicationReviewerId(Long leaveApplicationReviewerId) {
		this.leaveApplicationReviewerId = leaveApplicationReviewerId;
	}
	public String getLeaveApplicationReviewer() {
		return leaveApplicationReviewer;
	}
	public void setLeaveApplicationReviewer(String leaveApplicationReviewer) {
		this.leaveApplicationReviewer = leaveApplicationReviewer;
	}
	public String getRuleValue() {
		return ruleValue;
	}
	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}
	public byte[] getLeaveApplicationReviewerImage() {
		return leaveApplicationReviewerImage;
	}
	public void setLeaveApplicationReviewerImage(byte[] leaveApplicationReviewerImage) {
		this.leaveApplicationReviewerImage = leaveApplicationReviewerImage;
	}
	public String getReviewerType() {
		return reviewerType;
	}
	public void setReviewerType(String reviewerType) {
		this.reviewerType = reviewerType;
	}
	
	
	

}
