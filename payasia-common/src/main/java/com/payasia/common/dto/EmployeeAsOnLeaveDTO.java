package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class EmployeeAsOnLeaveDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2843081135555937599L;
	
	private Long employeeId;
	private String employeeNumber;
	private String firstName;
	private String lastName;
	private String leaveTypeName;
	private BigDecimal balance;
	private BigDecimal available;
	private BigDecimal taken;
	
	private String leaveSchemeName;
	private String reviewer1EmployeeNo;
	private String reviewer1FirstName;
	private String reviewer1LastName;
	private String reviewer1Email;
	private String reviewer2EmployeeNo;
	private String reviewer2FirstName;
	private String reviewer2LastName;
	private String reviewer2Email;
	private String reviewer3EmployeeNo;
	private String reviewer3FirstName;
	private String reviewer3LastName;
	private String reviewer3Email;
	
	
	public String getReviewer1Email() {
		return reviewer1Email;
	}
	public void setReviewer1Email(String reviewer1Email) {
		this.reviewer1Email = reviewer1Email;
	}
	public String getReviewer2Email() {
		return reviewer2Email;
	}
	public void setReviewer2Email(String reviewer2Email) {
		this.reviewer2Email = reviewer2Email;
	}
	public String getReviewer3Email() {
		return reviewer3Email;
	}
	public void setReviewer3Email(String reviewer3Email) {
		this.reviewer3Email = reviewer3Email;
	}
	public String getLeaveSchemeName() {
		return leaveSchemeName;
	}
	public void setLeaveSchemeName(String leaveSchemeName) {
		this.leaveSchemeName = leaveSchemeName;
	}
	public String getReviewer1EmployeeNo() {
		return reviewer1EmployeeNo;
	}
	public void setReviewer1EmployeeNo(String reviewer1EmployeeNo) {
		this.reviewer1EmployeeNo = reviewer1EmployeeNo;
	}
	public String getReviewer1FirstName() {
		return reviewer1FirstName;
	}
	public void setReviewer1FirstName(String reviewer1FirstName) {
		this.reviewer1FirstName = reviewer1FirstName;
	}
	public String getReviewer1LastName() {
		return reviewer1LastName;
	}
	public void setReviewer1LastName(String reviewer1LastName) {
		this.reviewer1LastName = reviewer1LastName;
	}
	public String getReviewer2EmployeeNo() {
		return reviewer2EmployeeNo;
	}
	public void setReviewer2EmployeeNo(String reviewer2EmployeeNo) {
		this.reviewer2EmployeeNo = reviewer2EmployeeNo;
	}
	public String getReviewer2FirstName() {
		return reviewer2FirstName;
	}
	public void setReviewer2FirstName(String reviewer2FirstName) {
		this.reviewer2FirstName = reviewer2FirstName;
	}
	public String getReviewer2LastName() {
		return reviewer2LastName;
	}
	public void setReviewer2LastName(String reviewer2LastName) {
		this.reviewer2LastName = reviewer2LastName;
	}
	public String getReviewer3EmployeeNo() {
		return reviewer3EmployeeNo;
	}
	public void setReviewer3EmployeeNo(String reviewer3EmployeeNo) {
		this.reviewer3EmployeeNo = reviewer3EmployeeNo;
	}
	public String getReviewer3FirstName() {
		return reviewer3FirstName;
	}
	public void setReviewer3FirstName(String reviewer3FirstName) {
		this.reviewer3FirstName = reviewer3FirstName;
	}
	public String getReviewer3LastName() {
		return reviewer3LastName;
	}
	public void setReviewer3LastName(String reviewer3LastName) {
		this.reviewer3LastName = reviewer3LastName;
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
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public BigDecimal getAvailable() {
		return available;
	}
	public void setAvailable(BigDecimal available) {
		this.available = available;
	}
	public BigDecimal getTaken() {
		return taken;
	}
	public void setTaken(BigDecimal taken) {
		this.taken = taken;
	}
	
	
	
	
	

}
