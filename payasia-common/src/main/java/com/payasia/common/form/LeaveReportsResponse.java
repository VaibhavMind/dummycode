package com.payasia.common.form;

import java.util.List;

public class LeaveReportsResponse extends PageResponse{

	private List<LeaveReportsForm> leaveReportsFormList;
	/** The search employee list. */
	private List<EmployeeListForm> searchEmployeeList;

	public List<LeaveReportsForm> getLeaveReportsFormList() {
		return leaveReportsFormList;
	}

	public void setLeaveReportsFormList(List<LeaveReportsForm> leaveReportsFormList) {
		this.leaveReportsFormList = leaveReportsFormList;
	}

	public List<EmployeeListForm> getSearchEmployeeList() {
		return searchEmployeeList;
	}

	public void setSearchEmployeeList(List<EmployeeListForm> searchEmployeeList) {
		this.searchEmployeeList = searchEmployeeList;
	}
	
}
