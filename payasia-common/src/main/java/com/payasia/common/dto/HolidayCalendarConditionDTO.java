package com.payasia.common.dto;

import java.io.Serializable;


public class HolidayCalendarConditionDTO implements Serializable{

	
	private static final long serialVersionUID = 7527698699374809550L;
	private String calName;
	private String calDesc;
	private Long companyId;
	private String employeeNumber;
	private String employeeName;
	
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getCalName() {
		return calName;
	}
	public void setCalName(String calName) {
		this.calName = calName;
	}
	public String getCalDesc() {
		return calDesc;
	}
	public void setCalDesc(String calDesc) {
		this.calDesc = calDesc;
	}
	
	
}
