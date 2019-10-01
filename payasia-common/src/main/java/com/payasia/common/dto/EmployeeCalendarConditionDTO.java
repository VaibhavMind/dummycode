package com.payasia.common.dto;

import java.io.Serializable;

public class EmployeeCalendarConditionDTO implements Serializable {
	
	
	
	private static final long serialVersionUID = 1L;
	
	private String employeeNumber;
	private String firstName;
	private String lastName;
	private Integer year;
	private String templateName;
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	

}
