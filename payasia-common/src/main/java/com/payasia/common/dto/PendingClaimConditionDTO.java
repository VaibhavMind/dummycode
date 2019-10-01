package com.payasia.common.dto;

import java.io.Serializable;

public class PendingClaimConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4371755847331764799L;
	
	private String claimStatusName;
	private Long claimNumber;
	private String createdDate;
	private String claimGroup;
	private String createdDateSortOrder;
	private String claimNumberSortOrder;
	
	

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

	public String getClaimGroup() {
		return claimGroup;
	}

	public void setClaimGroup(String claimGroup) {
		this.claimGroup = claimGroup;
	}

	public String getClaimStatusName() {
		return claimStatusName;
	}

	public void setClaimStatusName(String claimStatusName) {
		this.claimStatusName = claimStatusName;
	}

	public String getCreatedDateSortOrder() {
		return createdDateSortOrder;
	}

	public void setCreatedDateSortOrder(String createdDateSortOrder) {
		this.createdDateSortOrder = createdDateSortOrder;
	}

	public String getClaimNumberSortOrder() {
		return claimNumberSortOrder;
	}

	public void setClaimNumberSortOrder(String claimNumberSortOrder) {
		this.claimNumberSortOrder = claimNumberSortOrder;
	}
	
	

}
