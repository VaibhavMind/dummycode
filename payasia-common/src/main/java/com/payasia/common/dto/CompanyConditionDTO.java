package com.payasia.common.dto;

import java.io.Serializable;

public class CompanyConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3141436928308236424L;
	private String companyName;
	private Long groupId;
	private String companyCode;
	private Long companyId;

	private String groupName;
	private String financialYear;
	private String country;
	private String paySlipFrequency;
	private String group;
	private String groupCode;
	private boolean includeActiveStatus;
	private boolean includeInactiveCompany;
	
	private boolean includeDemoCompanyStatus;
	private boolean includeDemoCompany;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPaySlipFrequency() {
		return paySlipFrequency;
	}

	public void setPaySlipFrequency(String paySlipFrequency) {
		this.paySlipFrequency = paySlipFrequency;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public boolean isIncludeInactiveCompany() {
		return includeInactiveCompany;
	}

	public void setIncludeInactiveCompany(boolean includeInactiveCompany) {
		this.includeInactiveCompany = includeInactiveCompany;
	}

	public boolean isIncludeActiveStatus() {
		return includeActiveStatus;
	}

	public void setIncludeActiveStatus(boolean includeActiveStatus) {
		this.includeActiveStatus = includeActiveStatus;
	}

	public boolean isIncludeDemoCompanyStatus() {
		return includeDemoCompanyStatus;
	}

	public void setIncludeDemoCompanyStatus(boolean includeDemoCompanyStatus) {
		this.includeDemoCompanyStatus = includeDemoCompanyStatus;
	}

	public boolean isIncludeDemoCompany() {
		return includeDemoCompany;
	}

	public void setIncludeDemoCompany(boolean includeDemoCompany) {
		this.includeDemoCompany = includeDemoCompany;
	}

	

	

}
