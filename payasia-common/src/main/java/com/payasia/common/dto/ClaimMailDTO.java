package com.payasia.common.dto;

import java.io.Serializable;

import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimApplicationWorkflow;

public class ClaimMailDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7615521272993693819L;
	
	private Long loggedInEmpId;
	private Long loggedInCmpId;
	private String subcategoryName;
	private ClaimApplication claimApplication;
	private String reviewerRemarks;
	private ClaimApplicationWorkflow claimApplicationWorkflow;
	private boolean attachmentRequired;

	
	public Long getLoggedInEmpId() {
		return loggedInEmpId;
	}
	public void setLoggedInEmpId(Long loggedInEmpId) {
		this.loggedInEmpId = loggedInEmpId;
	}
	public Long getLoggedInCmpId() {
		return loggedInCmpId;
	}
	public void setLoggedInCmpId(Long loggedInCmpId) {
		this.loggedInCmpId = loggedInCmpId;
	}
	public String getSubcategoryName() {
		return subcategoryName;
	}
	public void setSubcategoryName(String subcategoryName) {
		this.subcategoryName = subcategoryName;
	}
	public ClaimApplication getClaimApplication() {
		return claimApplication;
	}
	public void setClaimApplication(ClaimApplication claimApplication) {
		this.claimApplication = claimApplication;
	}
	public String getReviewerRemarks() {
		return reviewerRemarks;
	}
	public void setReviewerRemarks(String reviewerRemarks) {
		this.reviewerRemarks = reviewerRemarks;
	}
	public ClaimApplicationWorkflow getClaimApplicationWorkflow() {
		return claimApplicationWorkflow;
	}
	public void setClaimApplicationWorkflow(ClaimApplicationWorkflow claimApplicationWorkflow) {
		this.claimApplicationWorkflow = claimApplicationWorkflow;
	}
	public boolean isAttachmentRequired() {
		return attachmentRequired;
	}
	public void setAttachmentRequired(boolean attachmentRequired) {
		this.attachmentRequired = attachmentRequired;
	}
	
	
	
	
	

}
