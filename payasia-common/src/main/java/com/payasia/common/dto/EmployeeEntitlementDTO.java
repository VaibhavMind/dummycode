package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class EmployeeEntitlementDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1897916437856875840L;
	
	private String employeeNumber;
	
	private String claimTemplate;
	
	private BigDecimal entitlement;
	
	private String entitlementDetails;
	
	private String adjustments;
	
	private BigDecimal balance;
	
	private Long employeeClaimTemplateId;
	
	private String firstName;

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getClaimTemplate() {
		return claimTemplate;
	}

	public void setClaimTemplate(String claimTemplate) {
		this.claimTemplate = claimTemplate;
	}

	public BigDecimal getEntitlement() {
		return entitlement;
	}

	public void setEntitlement(BigDecimal entitlement) {
		this.entitlement = entitlement;
	}

	public String getEntitlementDetails() {
		return entitlementDetails;
	}

	public void setEntitlementDetails(String entitlementDetails) {
		this.entitlementDetails = entitlementDetails;
	}

	public String getAdjustments() {
		return adjustments;
	}

	public void setAdjustments(String adjustments) {
		this.adjustments = adjustments;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Long getEmployeeClaimTemplateId() {
		return employeeClaimTemplateId;
	}

	public void setEmployeeClaimTemplateId(Long employeeClaimTemplateId) {
		this.employeeClaimTemplateId = employeeClaimTemplateId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

}
