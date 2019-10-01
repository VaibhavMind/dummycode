package com.payasia.common.dto;

import java.sql.Timestamp;

public class LeaveBalanceSummaryConditionDTO {
	
	private Timestamp fromDate;
	private Timestamp toDate;
	private String fromDateString;
	private String toDateString;
	private EmployeeShortListDTO employeeShortListDTO;
	
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
	public EmployeeShortListDTO getEmployeeShortListDTO() {
		return employeeShortListDTO;
	}
	public void setEmployeeShortListDTO(EmployeeShortListDTO employeeShortListDTO) {
		this.employeeShortListDTO = employeeShortListDTO;
	}

}
