package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.payasia.common.form.ClaimApplicationWorkflowForm;

public class ClaimApplicationDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String claimTemplate;
	private Long claimNumber;
	private String noOfItems;
	private BigDecimal totalAmount;
	private String createdDate;
	private String applyTo;
	private String reason;
	private List<ClaimApplicationWorkflowForm> claimWorkflows;
	private Boolean isApproved;
	
	public String getClaimTemplate() {
		return claimTemplate;
	}
	public void setClaimTemplate(String claimTemplate) {
		this.claimTemplate = claimTemplate;
	}
	public Long getClaimNumber() {
		return claimNumber;
	}
	public void setClaimNumber(Long claimNumber) {
		this.claimNumber = claimNumber;
	}
	
	
	public String getNoOfItems() {
		return noOfItems;
	}
	public void setNoOfItems(String noOfItems) {
		this.noOfItems = noOfItems;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getApplyTo() {
		return applyTo;
	}
	public void setApplyTo(String applyTo) {
		this.applyTo = applyTo;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public List<ClaimApplicationWorkflowForm> getClaimWorkflows() {
		return claimWorkflows;
	}
	public void setClaimWorkflows(List<ClaimApplicationWorkflowForm> claimWorkflows) {
		this.claimWorkflows = claimWorkflows;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Boolean getIsApproved() {
		return isApproved;
	}
	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}
	
	
	

}
