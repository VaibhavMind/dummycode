package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class EmployeeClaimAdjustmentDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2141682766623520801L;
	
	private long employeeClaimAdjustmentID;
	private String effectiveDate;
	private String claimTemplateName;
	private String remarks;
	private BigDecimal amount;
	private String employeeName;
	private String employeeNumber;
	private String entitlement;

	public long getEmployeeClaimAdjustmentID() {
		return employeeClaimAdjustmentID;
	}

	public void setEmployeeClaimAdjustmentID(long employeeClaimAdjustmentID) {
		this.employeeClaimAdjustmentID = employeeClaimAdjustmentID;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getClaimTemplateName() {
		return claimTemplateName;
	}

	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getEntitlement() {
		return entitlement;
	}

	public void setEntitlement(String entitlement) {
		this.entitlement = entitlement;
	}

	
	
	
	

}
