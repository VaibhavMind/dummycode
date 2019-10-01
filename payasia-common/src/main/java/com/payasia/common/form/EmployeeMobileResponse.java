package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class EmployeeMobileResponse extends PageResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3130355787877444875L;
	/**
	 * An array that contains the actual objects
	 */
	private List<EmployeeListForm> rows;
	private List<EmployeeMobileForm> employeeMobileDetails;
	private EmployeeMobileForm employeeMobileForm;
	
	public List<EmployeeMobileForm> getEmployeeMobileDetails() {
		return employeeMobileDetails;
	}
	public void setEmployeeMobileDetails(
			List<EmployeeMobileForm> employeeMobileDetails) {
		this.employeeMobileDetails = employeeMobileDetails;
	}
	public List<EmployeeListForm> getRows() {
		return rows;
	}
	public void setRows(List<EmployeeListForm> rows) {
		this.rows = rows;
	}
	public EmployeeMobileForm getEmployeeMobileForm() {
		return employeeMobileForm;
	}
	public void setEmployeeMobileForm(EmployeeMobileForm employeeMobileForm) {
		this.employeeMobileForm = employeeMobileForm;
	}


	
}
