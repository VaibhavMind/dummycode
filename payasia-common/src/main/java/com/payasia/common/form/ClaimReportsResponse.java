package com.payasia.common.form;

import java.util.List;

public class ClaimReportsResponse extends PageResponse{

	private List<ClaimReportsForm> claimReportsFormList;
	/** The search employee list. */
	private List<EmployeeListForm> searchEmployeeList;


	public List<EmployeeListForm> getSearchEmployeeList() {
		return searchEmployeeList;
	}

	public void setSearchEmployeeList(List<EmployeeListForm> searchEmployeeList) {
		this.searchEmployeeList = searchEmployeeList;
	}

	public List<ClaimReportsForm> getClaimReportsFormList() {
		return claimReportsFormList;
	}

	public void setClaimReportsFormList(List<ClaimReportsForm> claimReportsFormList) {
		this.claimReportsFormList = claimReportsFormList;
	}
	
}
