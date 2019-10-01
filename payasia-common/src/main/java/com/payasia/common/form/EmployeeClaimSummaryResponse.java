package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.EmployeeClaimSummaryDTO;

public class EmployeeClaimSummaryResponse extends PageResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 92533690921093650L;
	private List<EmployeeClaimSummaryDTO> rows;
	
	private String claimItemEntitlement;

	public List<EmployeeClaimSummaryDTO> getRows() {
		return rows;
	}

	public void setRows(List<EmployeeClaimSummaryDTO> rows) {
		this.rows = rows;
	}

	public String getClaimItemEntitlement() {
		return claimItemEntitlement;
	}

	public void setClaimItemEntitlement(String claimItemEntitlement) {
		this.claimItemEntitlement = claimItemEntitlement;
	}

}
