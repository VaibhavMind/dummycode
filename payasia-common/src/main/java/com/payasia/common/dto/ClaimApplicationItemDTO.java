package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class ClaimApplicationItemDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1245583850255976330L;
	
	
	private Long categoryId;
	private String categoryName;
	
	private String claimantName;
	private String deactivationDate;
	
	private List<ClaimItemDTO> claimItems;

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<ClaimItemDTO> getClaimItems() {
		return claimItems;
	}

	public void setClaimItems(List<ClaimItemDTO> claimItems) {
		this.claimItems = claimItems;
	}

	public String getClaimantName() {
		return claimantName;
	}

	public void setClaimantName(String claimantName) {
		this.claimantName = claimantName;
	}

	public String getDeactivationDate() {
		return deactivationDate;
	}

	public void setDeactivationDate(String deactivationDate) {
		this.deactivationDate = deactivationDate;
	}
	
	
	

}
