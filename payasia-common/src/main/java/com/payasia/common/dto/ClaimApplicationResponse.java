package com.payasia.common.dto;

import java.io.Serializable;

import com.payasia.common.form.AddClaimForm;

public class ClaimApplicationResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 601312739218291136L;
	private AddClaimForm claimApplicationData;
	private AddClaimForm claimApplicationItem;
	private ClaimApplicationDTO claimApplicationDTO;
	
	public AddClaimForm getClaimApplicationData() {
		return claimApplicationData;
	}
	public void setClaimApplicationData(AddClaimForm claimApplicationData) {
		this.claimApplicationData = claimApplicationData;
	}
	public AddClaimForm getClaimApplicationItem() {
		return claimApplicationItem;
	}
	public void setClaimApplicationItem(AddClaimForm claimApplicationItem) {
		this.claimApplicationItem = claimApplicationItem;
	}
	public ClaimApplicationDTO getClaimApplicationDTO() {
		return claimApplicationDTO;
	}
	public void setClaimApplicationDTO(ClaimApplicationDTO claimApplicationDTO) {
		this.claimApplicationDTO = claimApplicationDTO;
	}
	
	

}
