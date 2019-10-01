package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payasia.common.dto.HRISChangeRequestWorkflowDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrisChangeRequestForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String field;
	private String oldValue;
	private String newValue;
	private String reviewer1;
	private String reviewer2;
	private String reviewer3;
	private String reason;
	private Long hrisChangeRequestId;
	private String userStatus;
	private String createdDate;
	private String createdBy;
	
	private String changeRequestReviewer1;
	private Long changeRequestReviewer1Id;
	private String changeRequestReviewer2;
	private Long changeRequestReviewer2Id;
	private String changeRequestReviewer3;
	private Long changeRequestReviewer3Id;
	private List<HRISChangeRequestWorkflowDTO> hRISChangeRequestWorkflowDTOs;
	
	private Boolean action;
	
	private int totalNoOfReviewers;
	


	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public List<HRISChangeRequestWorkflowDTO> gethRISChangeRequestWorkflowDTOs() {
		return hRISChangeRequestWorkflowDTOs;
	}
	public void sethRISChangeRequestWorkflowDTOs(
			List<HRISChangeRequestWorkflowDTO> hRISChangeRequestWorkflowDTOs) {
		this.hRISChangeRequestWorkflowDTOs = hRISChangeRequestWorkflowDTOs;
	}
	public String getChangeRequestReviewer1() {
		return changeRequestReviewer1;
	}
	public void setChangeRequestReviewer1(String changeRequestReviewer1) {
		this.changeRequestReviewer1 = changeRequestReviewer1;
	}
	public Long getChangeRequestReviewer1Id() {
		return changeRequestReviewer1Id;
	}
	public void setChangeRequestReviewer1Id(Long changeRequestReviewer1Id) {
		this.changeRequestReviewer1Id = changeRequestReviewer1Id;
	}
	public String getChangeRequestReviewer2() {
		return changeRequestReviewer2;
	}
	public void setChangeRequestReviewer2(String changeRequestReviewer2) {
		this.changeRequestReviewer2 = changeRequestReviewer2;
	}
	public Long getChangeRequestReviewer2Id() {
		return changeRequestReviewer2Id;
	}
	public void setChangeRequestReviewer2Id(Long changeRequestReviewer2Id) {
		this.changeRequestReviewer2Id = changeRequestReviewer2Id;
	}
	public String getChangeRequestReviewer3() {
		return changeRequestReviewer3;
	}
	public void setChangeRequestReviewer3(String changeRequestReviewer3) {
		this.changeRequestReviewer3 = changeRequestReviewer3;
	}
	public Long getChangeRequestReviewer3Id() {
		return changeRequestReviewer3Id;
	}
	public void setChangeRequestReviewer3Id(Long changeRequestReviewer3Id) {
		this.changeRequestReviewer3Id = changeRequestReviewer3Id;
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
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
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
	public String getReviewer1() {
		return reviewer1;
	}
	public void setReviewer1(String reviewer1) {
		this.reviewer1 = reviewer1;
	}
	public String getReviewer2() {
		return reviewer2;
	}
	public void setReviewer2(String reviewer2) {
		this.reviewer2 = reviewer2;
	}
	public String getReviewer3() {
		return reviewer3;
	}
	public void setReviewer3(String reviewer3) {
		this.reviewer3 = reviewer3;
	}
	public Long getHrisChangeRequestId() {
		return hrisChangeRequestId;
	}
	public void setHrisChangeRequestId(Long hrisChangeRequestId) {
		this.hrisChangeRequestId = hrisChangeRequestId;
	}
	public int getTotalNoOfReviewers() {
		return totalNoOfReviewers;
	}
	public void setTotalNoOfReviewers(int totalNoOfReviewers) {
		this.totalNoOfReviewers = totalNoOfReviewers;
	}
	public Boolean getAction() {
		return action;
	}
	public void setAction(Boolean action) {
		this.action = action;
	}
	
	
	

}
