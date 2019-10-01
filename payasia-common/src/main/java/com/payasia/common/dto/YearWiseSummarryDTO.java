package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashMap;

public class YearWiseSummarryDTO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5935403799704608101L;
	private Long employeeId;
	private String employeeNumber;
	private String firstName;
	private String lastName;
	private String leaveTypeName;
	private String carryForward;
	private String credited;
	private String taken;
	private String closingBalance;	
	private HashMap<String,String> transactions;
	
	
	private String forfeited;
	private String encashed;
	private HashMap<String, String> custFieldMap;
	
	
	
	
	
	
	
	
	
	
	public String getForfeited() {
		return forfeited;
	}
	public void setForfeited(String forfeited) {
		this.forfeited = forfeited;
	}
	public String getEncashed() {
		return encashed;
	}
	public void setEncashed(String encashed) {
		this.encashed = encashed;
	}
	public HashMap<String, String> getTransactions() {
		return transactions;
	}
	public void setTransactions(HashMap<String, String> transactions) {
		this.transactions = transactions;
	}
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
	public String getLeaveTypeName() {
		return leaveTypeName;
	}
	public void setLeaveTypeName(String leaveTypeName) {
		this.leaveTypeName = leaveTypeName;
	}
	public String getCarryForward() {
		return carryForward;
	}
	public void setCarryForward(String carryForward) {
		this.carryForward = carryForward;
	}
	public String getCredited() {
		return credited;
	}
	public void setCredited(String credited) {
		this.credited = credited;
	}

	public String getTaken() {
		return taken;
	}
	public void setTaken(String taken) {
		this.taken = taken;
	}
	public String getClosingBalance() {
		return closingBalance;
	}
	public void setClosingBalance(String closingBalance) {
		this.closingBalance = closingBalance;
	}
	public HashMap<String, String> getCustFieldMap() {
		return custFieldMap;
	}
	public void setCustFieldMap(HashMap<String, String> custFieldMap) {
		this.custFieldMap = custFieldMap;
	}
	
	
	

}
