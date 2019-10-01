package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.payasia.common.form.ClaimApplicationItemForm;

public class ClaimApplicationItemWSResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2409491198053438252L;
	
	private BigDecimal claimAmount;
	private String totalItems;
	private Long claimNumber;
	private String createdDate;
	private String remarks;
	private String claimTemplateName;
	private List<ClaimApplicationItemForm> claimItems;
	public BigDecimal getClaimAmount() {
		return claimAmount;
	}
	public void setClaimAmount(BigDecimal claimAmount) {
		this.claimAmount = claimAmount;
	}
	public String getTotalItems() {
		return totalItems;
	}
	public void setTotalItems(String totalItems) {
		this.totalItems = totalItems;
	}
	public Long getClaimNumber() {
		return claimNumber;
	}
	public void setClaimNumber(Long claimNumber) {
		this.claimNumber = claimNumber;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getClaimTemplateName() {
		return claimTemplateName;
	}
	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}
	public List<ClaimApplicationItemForm> getClaimItems() {
		return claimItems;
	}
	public void setClaimItems(List<ClaimApplicationItemForm> claimItems) {
		this.claimItems = claimItems;
	}
	
	
}
