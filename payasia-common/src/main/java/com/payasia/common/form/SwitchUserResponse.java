package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class SwitchUserResponse extends PageResponse implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5916083464456221285L;

	private List<SwitchUserForm> switchUser;
	
	private List<EmployeeListForm> searchEmployeeList;

	public List<SwitchUserForm> getSwitchUser() {
		return switchUser;
	}

	public void setSwitchUser(
			List<SwitchUserForm> switchUser) {
		this.switchUser = switchUser;
	}

	public List<EmployeeListForm> getSearchEmployeeList() {
		return searchEmployeeList;
	}

	public void setSearchEmployeeList(List<EmployeeListForm> searchEmployeeList) {
		this.searchEmployeeList = searchEmployeeList;
	}
	
	

}
