package com.payasia.common.dto;

import java.io.Serializable;

import com.payasia.dao.bean.ClaimApplication;

public class HRISMailDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7615521272993693819L;
	
	private Long loggedInEmpId;
	private Long loggedInCmpId;
	private String subcategoryName;
	private ClaimApplication claimApplication;
	private String reviewerRemarks;
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
	
	
	
	

}
