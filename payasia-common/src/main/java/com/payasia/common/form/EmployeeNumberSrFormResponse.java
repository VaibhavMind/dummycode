package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

/**
 * The Class EmployeeNumberSrFormResponse.
 */
public class EmployeeNumberSrFormResponse extends PageResponse implements Serializable{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1067050701123196567L;
	/** The employee no sr list. */
	private List<EmployeeNumberSrForm> employeeNoSrList;

	

	

	/**
	 * Gets the employee no sr list.
	 * 
	 * @return the employee no sr list
	 */
	public List<EmployeeNumberSrForm> getEmployeeNoSrList() {
		return employeeNoSrList;
	}

	/**
	 * Sets the employee no sr list.
	 * 
	 * @param employeeNoSrList
	 *            the new employee no sr list
	 */
	public void setEmployeeNoSrList(List<EmployeeNumberSrForm> employeeNoSrList) {
		this.employeeNoSrList = employeeNoSrList;
	}

}
