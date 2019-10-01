package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class ChangeEmployeeNumberFromResponse extends PageResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<ChangeEmployeeNameListForm> changeEmployeeNameListForm;

	private List<EmployeeListForm> searchEmployeeList;


	public List<ChangeEmployeeNameListForm> getChangeEmployeeNameListForm() {
		return changeEmployeeNameListForm;
	}

	public void setChangeEmployeeNameListForm(
			List<ChangeEmployeeNameListForm> changeEmployeeNameListForm) {
		this.changeEmployeeNameListForm = changeEmployeeNameListForm;
	}

	public List<EmployeeListForm> getSearchEmployeeList() {
		return searchEmployeeList;
	}

	public void setSearchEmployeeList(List<EmployeeListForm> searchEmployeeList) {
		this.searchEmployeeList = searchEmployeeList;
	}

}
