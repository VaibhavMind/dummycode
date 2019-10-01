package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class CoherentOTEmployeeListForm extends PageResponse implements Serializable{
	
	private List<CoherentOTEmployeeListForm> searchEmployeeList;
	private String employeeId;
	private String employeeName;
	private String employeeNumber;
	private String addOn;
	
	
	
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getAddOn() {
		return addOn;
	}
	public void setAddOn(String addOn) {
		this.addOn = addOn;
	}
	public List<CoherentOTEmployeeListForm> getSearchEmployeeList() {
		return searchEmployeeList;
	}
	public void setSearchEmployeeList(
			List<CoherentOTEmployeeListForm> searchEmployeeList) {
		this.searchEmployeeList = searchEmployeeList;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	
	
	
	

}
