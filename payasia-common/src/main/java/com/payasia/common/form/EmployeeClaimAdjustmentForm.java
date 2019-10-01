package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;

public class EmployeeClaimAdjustmentForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -652581320861100409L;
	
	
	private long employeeClaimTemplateId;
	
	private BigDecimal amount;
	
	private String remarks;
	
	private String effectiveDate;
	private String startDate;
	private String endDate;
	
	
	private long employeeClaimAdjustmentId;

	public long getEmployeeClaimTemplateId() {
		return employeeClaimTemplateId;
	}

	public void setEmployeeClaimTemplateId(long employeeClaimTemplateId) {
		this.employeeClaimTemplateId = employeeClaimTemplateId;
	}


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public long getEmployeeClaimAdjustmentId() {
		return employeeClaimAdjustmentId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setEmployeeClaimAdjustmentId(long employeeClaimAdjustmentId) {
		this.employeeClaimAdjustmentId = employeeClaimAdjustmentId;
	}
	
	
	
	
	
	
	

}
