package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class ClaimApplicationItemsResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ClaimAppItemDTO> claimApplicationItems;

	public List<ClaimAppItemDTO> getClaimApplicationItems() {
		return claimApplicationItems;
	}

	public void setClaimApplicationItems(List<ClaimAppItemDTO> claimApplicationItems) {
		this.claimApplicationItems = claimApplicationItems;
	}
	
	

}
