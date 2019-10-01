package com.payasia.common.dto;

public class EmployeeCalendarDTO {
	
	private String employeeNumber;
	private String employeeName;
	private Long employeeId;
	private Integer year;
	private String calTemplateName;
	private Long calTemplateId;
	private String startDate;
	private String endDate;
	private String configureCalendar;
	
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	private Long employeeCalendarId;
	
	
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getCalTemplateName() {
		return calTemplateName;
	}
	public void setCalTemplateName(String calTemplateName) {
		this.calTemplateName = calTemplateName;
	}
	public Long getCalTemplateId() {
		return calTemplateId;
	}
	public void setCalTemplateId(Long calTemplateId) {
		this.calTemplateId = calTemplateId;
	}
	public Long getEmployeeCalendarId() {
		return employeeCalendarId;
	}
	public void setEmployeeCalendarId(Long employeeCalendarId) {
		this.employeeCalendarId = employeeCalendarId;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getConfigureCalendar() {
		return configureCalendar;
	}
	public void setConfigureCalendar(String configureCalendar) {
		this.configureCalendar = configureCalendar;
	}
	
	
	

}
