package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payasia.common.dto.HRISChangeRequestWorkflowDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrisPendingItemsForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4131221104992120612L;
	private Long hRISChangeRequestReviewerId;
	private String field;
	private String oldValue;
	private String newValue;
	private String  createdBy;
	private Long createdById;
	private String createdDate;
	private boolean canOverride;
	private boolean canReject;
	private boolean canApprove;
	private boolean canForward;
	private String changeRequestReviewer1;
	private Long changeRequestReviewer1Id;
	private String changeRequestReviewer2;
	private Long changeRequestReviewer2Id;
	private String changeRequestReviewer3;
	private Long changeRequestReviewer3Id;
	private String applyTo;
	private Long applyToId;
	private List<HRISChangeRequestWorkflowDTO> hRISChangeRequestWorkflowDTOs;
	private String forwardTo;
	private Long forwardToId;
	private String reason;
	private String userStatus;
	private String userRemarks;
	private String emailCC;
	
	private int totalNoOfReviewers;
	
	
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
	public Long getChangeRequestReviewer1Id() {
		return changeRequestReviewer1Id;
	}
	public void setChangeRequestReviewer1Id(Long changeRequestReviewer1Id) {
		this.changeRequestReviewer1Id = changeRequestReviewer1Id;
	}
	public Long getChangeRequestReviewer2Id() {
		return changeRequestReviewer2Id;
	}
	public void setChangeRequestReviewer2Id(Long changeRequestReviewer2Id) {
		this.changeRequestReviewer2Id = changeRequestReviewer2Id;
	}
	public Long getChangeRequestReviewer3Id() {
		return changeRequestReviewer3Id;
	}
	public void setChangeRequestReviewer3Id(Long changeRequestReviewer3Id) {
		this.changeRequestReviewer3Id = changeRequestReviewer3Id;
	}
	public Long getApplyToId() {
		return applyToId;
	}
	public void setApplyToId(Long applyToId) {
		this.applyToId = applyToId;
	}
	public String getChangeRequestReviewer3() {
		return changeRequestReviewer3;
	}
	public void setChangeRequestReviewer3(String changeRequestReviewer3) {
		this.changeRequestReviewer3 = changeRequestReviewer3;
	}
	public String getChangeRequestReviewer1() {
		return changeRequestReviewer1;
	}
	public void setChangeRequestReviewer1(String changeRequestReviewer1) {
		this.changeRequestReviewer1 = changeRequestReviewer1;
	}
	public String getChangeRequestReviewer2() {
		return changeRequestReviewer2;
	}
	public void setChangeRequestReviewer2(String changeRequestReviewer2) {
		this.changeRequestReviewer2 = changeRequestReviewer2;
	}
	public Long gethRISChangeRequestReviewerId() {
		return hRISChangeRequestReviewerId;
	}
	public void sethRISChangeRequestReviewerId(Long hRISChangeRequestReviewerId) {
		this.hRISChangeRequestReviewerId = hRISChangeRequestReviewerId;
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
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public boolean isCanOverride() {
		return canOverride;
	}
	public void setCanOverride(boolean canOverride) {
		this.canOverride = canOverride;
	}
	public boolean isCanReject() {
		return canReject;
	}
	public void setCanReject(boolean canReject) {
		this.canReject = canReject;
	}
	public boolean isCanApprove() {
		return canApprove;
	}
	public void setCanApprove(boolean canApprove) {
		this.canApprove = canApprove;
	}
	public boolean isCanForward() {
		return canForward;
	}
	public void setCanForward(boolean canForward) {
		this.canForward = canForward;
	}
	public String getApplyTo() {
		return applyTo; 
	}
	public void setApplyTo(String applyTo) {
		this.applyTo = applyTo;
	}
	public List<HRISChangeRequestWorkflowDTO> gethRISChangeRequestWorkflowDTOs() {
		return hRISChangeRequestWorkflowDTOs;
	}
	public void sethRISChangeRequestWorkflowDTOs(
			List<HRISChangeRequestWorkflowDTO> hRISChangeRequestWorkflowDTOs) {
		this.hRISChangeRequestWorkflowDTOs = hRISChangeRequestWorkflowDTOs;
	}
	public Long getCreatedById() {
		return createdById;
	}
	public void setCreatedById(Long createdById) {
		this.createdById = createdById;
	}
	public String getForwardTo() {
		return forwardTo;
	}
	public void setForwardTo(String forwardTo) {
		this.forwardTo = forwardTo;
	}
	public Long getForwardToId() {
		return forwardToId;
	}
	public void setForwardToId(Long forwardToId) {
		this.forwardToId = forwardToId;
	}
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public String getUserRemarks() {
		return userRemarks;
	}
	public void setUserRemarks(String userRemarks) {
		this.userRemarks = userRemarks;
	}
	public String getEmailCC() {
		return emailCC;
	}
	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}
	public int getTotalNoOfReviewers() {
		return totalNoOfReviewers;
	}
	public void setTotalNoOfReviewers(int totalNoOfReviewers) {
		this.totalNoOfReviewers = totalNoOfReviewers;
	}
	
	

}
