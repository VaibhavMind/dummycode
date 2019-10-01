package com.payasia.common.dto;

public class ClaimItemDTO {

	private String accountCode;
	private String description;
	private String name;
	private String code;
	private String visibility;
	private String claimCategory;
	private String claimItemName;
	private Long claimItemId;
	private Long employeeClaimTemplateItemId;
	private String helpText;

	public Long getEmployeeClaimTemplateItemId() {
		return employeeClaimTemplateItemId;
	}

	public void setEmployeeClaimTemplateItemId(Long employeeClaimTemplateItemId) {
		this.employeeClaimTemplateItemId = employeeClaimTemplateItemId;
	}

	public String getClaimItemName() {
		return claimItemName;
	}

	public void setClaimItemName(String claimItemName) {
		this.claimItemName = claimItemName;
	}

	public Long getClaimItemId() {
		return claimItemId;
	}

	public void setClaimItemId(Long claimItemId) {
		this.claimItemId = claimItemId;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getClaimCategory() {
		return claimCategory;
	}

	public void setClaimCategory(String claimCategory) {
		this.claimCategory = claimCategory;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

}
