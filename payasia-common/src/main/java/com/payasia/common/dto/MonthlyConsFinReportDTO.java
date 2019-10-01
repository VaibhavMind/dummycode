package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class MonthlyConsFinReportDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5935403799704608101L;
	private Long employeeId;
	private String employeeNumber;
	private String firstName;
	private String lastName;
	private String employeeName;
	
	
	private HashMap<String,String> transactions;
	private HashMap<String,List<MonthlyConsolidatedFinanceReportDataDTO>> monthlyConFinDetailMap;
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
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
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public HashMap<String, String> getTransactions() {
		return transactions;
	}
	public void setTransactions(HashMap<String, String> transactions) {
		this.transactions = transactions;
	}
	public HashMap<String, List<MonthlyConsolidatedFinanceReportDataDTO>> getMonthlyConFinDetailMap() {
		return monthlyConFinDetailMap;
	}
	public void setMonthlyConFinDetailMap(
			HashMap<String, List<MonthlyConsolidatedFinanceReportDataDTO>> monthlyConFinDetailMap) {
		this.monthlyConFinDetailMap = monthlyConFinDetailMap;
	}
	

	

	
	
	
	

}
