package com.payasia.common.dto;

public class LeaveEventReminderConditionDTO {
	
	private Long companyId;
	
	private String searchType;
	private Long searchTypeId;
	
	

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public Long getSearchTypeId() {
		return searchTypeId;
	}

	public void setSearchTypeId(Long searchTypeId) {
		this.searchTypeId = searchTypeId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	

}
