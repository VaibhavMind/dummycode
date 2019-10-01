package com.payasia.common.dto;

import java.io.Serializable;

import com.payasia.common.form.ClaimApplicationItemForm;

public class ClaimApplicationItemResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5342491344221575880L;
	
	private ClaimApplicationItemForm claimApplicationItemForm;

	public ClaimApplicationItemForm getClaimApplicationItemForm() {
		return claimApplicationItemForm;
	}

	public void setClaimApplicationItemForm(
			ClaimApplicationItemForm claimApplicationItemForm) {
		this.claimApplicationItemForm = claimApplicationItemForm;
	}
	
	

}
