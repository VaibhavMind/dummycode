package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class EmployeeDetailsResponse extends PageResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3550643983911931464L;
	private List<EmployeeDetailForm> employeeDetailFormList;
	
	public List<EmployeeDetailForm> getEmployeeDetailFormList() {
		return employeeDetailFormList;
	}
	public void setEmployeeDetailFormList(
			List<EmployeeDetailForm> employeeDetailFormList) {
		this.employeeDetailFormList = employeeDetailFormList;
	}
	
	

	
}
