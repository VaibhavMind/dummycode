package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmployeeClaimSummaryDTO implements Serializable {
	private static final long serialVersionUID = -11906035314906210L;

	private Long employeeId;
	private String employeeNumber;
	private String firstName;
	private String lastName;
	private String claimTemplateName;
	private String claimItemName;
	private String entitlement;
	private String entitlementDetails;
	private BigDecimal claimedAmount;
	private BigDecimal balance;
	private String adjustments;
	private String adjustmentsCount;
	private Long employeeClaimTemplateId;
	
	private Boolean action;

	public String getClaimTemplateName() {
		return claimTemplateName;
	}

	public void setClaimTemplateName(String claimTemplateName) {
		this.claimTemplateName = claimTemplateName;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getClaimItemName() {
		return claimItemName;
	}

	public void setClaimItemName(String claimItemName) {
		this.claimItemName = claimItemName;
	}


	public String getEntitlement() {
		return entitlement;
	}

	public void setEntitlement(String entitlement) {
		this.entitlement = entitlement;
	}

	public BigDecimal getClaimedAmount() {
		return claimedAmount;
	}

	public void setClaimedAmount(BigDecimal claimedAmount) {
		this.claimedAmount = claimedAmount;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getAdjustments() {
		return adjustments;
	}

	public void setAdjustments(String adjustments) {
		this.adjustments = adjustments;
	}

	public Long getEmployeeClaimTemplateId() {
		return employeeClaimTemplateId;
	}

	public void setEmployeeClaimTemplateId(Long employeeClaimTemplateId) {
		this.employeeClaimTemplateId = employeeClaimTemplateId;
	}

	public String getAdjustmentsCount() {
		return adjustmentsCount;
	}

	public void setAdjustmentsCount(String adjustmentsCount) {
		this.adjustmentsCount = adjustmentsCount;
	}

	public String getEntitlementDetails() {
		return entitlementDetails;
	}

	public void setEntitlementDetails(String entitlementDetails) {
		this.entitlementDetails = entitlementDetails;
	}

	public Boolean getAction() {
		return action;
	}

	public void setAction(Boolean action) {
		this.action = action;
	}

}
