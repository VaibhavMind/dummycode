package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class PendingClaimsWSRes implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2657930236446436197L; 
	private List<PendingClaimsForm> pendingClaims;

	public List<PendingClaimsForm> getPendingClaims() {
		return pendingClaims;
	}

	public void setPendingClaims(List<PendingClaimsForm> pendingClaims) {
		this.pendingClaims = pendingClaims;
	}
	
	
	

}
