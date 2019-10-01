/**
 * 
 */
package com.payasia.common.form;

import java.util.List;

/**
 * @author ragulapraveen
 *
 */
public class HrisPendingItemWorkflowRes extends PageResponse{
	/** The search employee list. */
	private List<EmployeeListForm> searchEmployeeList;

	public List<EmployeeListForm> getSearchEmployeeList() {
		return searchEmployeeList;
	}

	public void setSearchEmployeeList(List<EmployeeListForm> searchEmployeeList) {
		this.searchEmployeeList = searchEmployeeList;
	}
}
