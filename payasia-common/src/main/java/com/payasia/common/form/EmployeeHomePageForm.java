package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.payasia.common.dto.RecentActivityDTO;

@JsonInclude(Include.NON_NULL)
public class EmployeeHomePageForm extends PageResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8069574826662657906L;
	private Long payslipUploadId;
	private String payslipUploadDate;
	private String companyName;
	private String month;
	private List<PendingItemsForm> reviewLeaveList;
	private List<AddLeaveForm> submittedLeaveList;
	
	@JsonIgnore
	private int year;
	
	private List<AddClaimForm> pendingClaims;
	private List<PendingClaimsForm> pendingClaimRequests; 
	private List<RecentActivityDTO> recentActivityDTOList; 
	private String activationCode;
	private Integer totalRecords;

	public List<PendingClaimsForm> getPendingClaimRequests() {
		return pendingClaimRequests;
	}

	public void setPendingClaimRequests(List<PendingClaimsForm> pendingClaimRequests) {
		this.pendingClaimRequests = pendingClaimRequests;
	}

	public List<AddClaimForm> getPendingClaims() {
		return pendingClaims;
	}

	public void setPendingClaims(List<AddClaimForm> pendingClaims) {
		this.pendingClaims = pendingClaims;
	}

	public String getPayslipUploadDate() {
		return payslipUploadDate;
	}

	public void setPayslipUploadDate(String payslipUploadDate) {
		this.payslipUploadDate = payslipUploadDate;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Long getPayslipUploadId() {
		return payslipUploadId;
	}

	public void setPayslipUploadId(Long payslipUploadId) {
		this.payslipUploadId = payslipUploadId;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
public List<PendingItemsForm> getReviewLeaveList() {
		return reviewLeaveList;
	}

	public void setReviewLeaveList(List<PendingItemsForm> reviewLeaveList) {
		this.reviewLeaveList = reviewLeaveList;
	}

	public List<AddLeaveForm> getSubmittedLeaveList() {
		return submittedLeaveList;
	}

	public void setSubmittedLeaveList(List<AddLeaveForm> submittedLeaveList) {
		this.submittedLeaveList = submittedLeaveList;
	}

	public List<RecentActivityDTO> getRecentActivityDTOList() {
		return recentActivityDTOList;
	}

	public void setRecentActivityDTOList(List<RecentActivityDTO> recentActivityDTOList) {
		this.recentActivityDTOList = recentActivityDTOList;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}
	

}
