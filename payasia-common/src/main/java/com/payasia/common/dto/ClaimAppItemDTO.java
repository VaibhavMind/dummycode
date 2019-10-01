package com.payasia.common.dto;

import java.io.Serializable;

public class ClaimAppItemDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9208089797118597441L;
	
	private String claimItemCategory;
	private String claimItem;
	private Long claimApplicationItemId;
	private Long employeeClaimTemplateItemId;
	public String getClaimItemCategory() {
		return claimItemCategory;
	}
	public void setClaimItemCategory(String claimItemCategory) {
		this.claimItemCategory = claimItemCategory;
	}
	public String getClaimItem() {
		return claimItem;
	}
	public void setClaimItem(String claimItem) {
		this.claimItem = claimItem;
	}
	public Long getClaimApplicationItemId() {
		return claimApplicationItemId;
	}
	public void setClaimApplicationItemId(Long claimApplicationItemId) {
		this.claimApplicationItemId = claimApplicationItemId;
	}
	public Long getEmployeeClaimTemplateItemId() {
		return employeeClaimTemplateItemId;
	}
	public void setEmployeeClaimTemplateItemId(Long employeeClaimTemplateItemId) {
		this.employeeClaimTemplateItemId = employeeClaimTemplateItemId;
	}
	

}
