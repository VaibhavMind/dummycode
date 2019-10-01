package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class ClaimTemplateResponse extends PageResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7193938732794110347L;

	private List<ClaimTemplateForm> rows;

	private Set<ClaimTemplateForm> claimTemplateSet;

	public List<ClaimTemplateForm> getRows() {
		return rows;
	}

	public void setRows(List<ClaimTemplateForm> rows) {
		this.rows = rows;
	}

	public Set<ClaimTemplateForm> getClaimTemplateSet() {
		return claimTemplateSet;
	}

	public void setClaimTemplateSet(Set<ClaimTemplateForm> claimTemplateSet) {
		this.claimTemplateSet = claimTemplateSet;
	}
}
