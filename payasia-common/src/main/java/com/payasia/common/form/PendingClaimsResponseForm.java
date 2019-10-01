package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class PendingClaimsResponseForm extends PageResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8840575696189273330L;
	
	private List<PendingClaimsForm> pendingClaims;

	public List<PendingClaimsForm> getPendingClaims() {
		return pendingClaims;
	}

	public void setPendingClaims(List<PendingClaimsForm> pendingClaims) {
		this.pendingClaims = pendingClaims;
	}
	
	
	

}
