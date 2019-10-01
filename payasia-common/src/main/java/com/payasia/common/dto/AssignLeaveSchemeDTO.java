package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;


public class AssignLeaveSchemeDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7019002345490284784L;
	private String employeeName;
	private String employeeNumber;
	private String leaveSchemeName;
	private String active;
	private Timestamp fromDate;
	private Timestamp toDate;
	private String fromDateString;
	private String toDateString;

	
	public String getFromDateString() {
		return fromDateString;
	}
	public void setFromDateString(String fromDateString) {
		this.fromDateString = fromDateString;
	}
	public String getToDateString() {
		return toDateString;
	}
	public void setToDateString(String toDateString) {
		this.toDateString = toDateString;
	}
	public Timestamp getFromDate() {
		return fromDate;
	}
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	public Timestamp getToDate() {
		return toDate;
	}
	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
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
	public String getLeaveSchemeName() {
		return leaveSchemeName;
	}
	public void setLeaveSchemeName(String leaveSchemeName) {
		this.leaveSchemeName = leaveSchemeName;
	}


	
}
