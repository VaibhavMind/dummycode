package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.payasia.common.dto.EmployeeClaimAdjustmentDTO;

public class EmployeeClaimAdjustments extends PageResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -494659830611172379L;
	
	private List<EmployeeClaimAdjustmentDTO> rows;

	private String claimTemplateName;
	private BigDecimal entitlement;
	
	public List<EmployeeClaimAdjustmentDTO> getRows() {
		return rows;
	}

	public void setRows(List<EmployeeClaimAdjustmentDTO> rows) {
		this.rows = rows;
	}

	public String getClaimTemplateName() {
		return claimTemplateName;
	}

	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}

	public BigDecimal getEntitlement() {
		return entitlement;
	}

	public void setEntitlement(BigDecimal entitlement) {
		this.entitlement = entitlement;
	}

}
 