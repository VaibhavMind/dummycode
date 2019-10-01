package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class ClaimTemplatesReponse implements Serializable {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6979441984495009230L;
	private List<ClaimTemplateForm> claimTemplates;

	public List<ClaimTemplateForm> getClaimTemplates() {
		return claimTemplates;
	}

	public void setClaimTemplates(List<ClaimTemplateForm> claimTemplates) {
		this.claimTemplates = claimTemplates;
	}
	
	
}
