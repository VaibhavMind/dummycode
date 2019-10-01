package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class AddClaimFormResponse extends PageResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1982729121829278744L;
	private List<AddClaimForm> addClaimFormList;
	private List<ClaimApplicationItemForm> claimApplicationItemForm;
	
	
	
	public List<ClaimApplicationItemForm> getClaimApplicationItemForm() {
		return claimApplicationItemForm;
	}
	public void setClaimApplicationItemForm(
			List<ClaimApplicationItemForm> claimApplicationItemForm) {
		this.claimApplicationItemForm = claimApplicationItemForm;
	}
	public List<AddClaimForm> getAddClaimFormList() {
		return addClaimFormList;
	}
	public void setAddClaimFormList(List<AddClaimForm> addClaimFormList) {
		this.addClaimFormList = addClaimFormList;
	}

	

}
