package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class OTPendingTimesheetForm implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String field;
	private String oldValue;
	private String newValue;
	private String reason;
	private String userStatus;
	private String createdDate;
	private String createdBy;
	
	private String otTimesheetReviewer1;
	private Long otTimesheetReviewer1Id;
	private String otTimesheetReviewer2;
	private Long otTimesheetReviewer2Id;
	private String otTimesheetReviewer3;
	private Long otTimesheetReviewer3Id;
	
	private List<OTTimesheetWorkflowForm> otTimesheetWorkflowForms;
	
	public String getOtTimesheetReviewer1() {
		return otTimesheetReviewer1;
	}

	public void setOtTimesheetReviewer1(String otTimesheetReviewer1) {
		this.otTimesheetReviewer1 = otTimesheetReviewer1;
	}

	public Long getOtTimesheetReviewer1Id() {
		return otTimesheetReviewer1Id;
	}

	public void setOtTimesheetReviewer1Id(Long otTimesheetReviewer1Id) {
		this.otTimesheetReviewer1Id = otTimesheetReviewer1Id;
	}

	public String getOtTimesheetReviewer2() {
		return otTimesheetReviewer2;
	}

	public void setOtTimesheetReviewer2(String otTimesheetReviewer2) {
		this.otTimesheetReviewer2 = otTimesheetReviewer2;
	}

	public Long getOtTimesheetReviewer2Id() {
		return otTimesheetReviewer2Id;
	}

	public void setOtTimesheetReviewer2Id(Long otTimesheetReviewer2Id) {
		this.otTimesheetReviewer2Id = otTimesheetReviewer2Id;
	}

	public String getOtTimesheetReviewer3() {
		return otTimesheetReviewer3;
	}

	public void setOtTimesheetReviewer3(String otTimesheetReviewer3) {
		this.otTimesheetReviewer3 = otTimesheetReviewer3;
	}

	public Long getOtTimesheetReviewer3Id() {
		return otTimesheetReviewer3Id;
	}

	public void setOtTimesheetReviewer3Id(Long otTimesheetReviewer3Id) {
		this.otTimesheetReviewer3Id = otTimesheetReviewer3Id;
	}

	
	private int totalNoOfReviewers;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	
	public List<OTTimesheetWorkflowForm> getOtTimesheetWorkflowForms() {
		return otTimesheetWorkflowForms;
	}

	public void setOtTimesheetWorkflowForms(
			List<OTTimesheetWorkflowForm> otTimesheetWorkflowForms) {
		this.otTimesheetWorkflowForms = otTimesheetWorkflowForms;
	}

	public int getTotalNoOfReviewers() {
		return totalNoOfReviewers;
	}

	public void setTotalNoOfReviewers(int totalNoOfReviewers) {
		this.totalNoOfReviewers = totalNoOfReviewers;
	}
}
