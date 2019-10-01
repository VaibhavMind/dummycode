package com.payasia.common.form;

import java.io.Serializable;

public class EmployeeDetailForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -446440113224111742L;
	
	private String employeeId;
	private String email;
	private String employeeNumber;
	private String firstName;
	private String middleName;
	private String lastName;
	private String hiredate;
	private String confirmationdate;
	private String terminationdate;
	private String originalhiredate;
	private String employmentstatus;
	private byte[] profileImg;
	
	public byte[] getProfileImg() {
		return profileImg;
	}
	public void setProfileImg(byte[] profileImg) {
		this.profileImg = profileImg;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getHiredate() {
		return hiredate;
	}
	public void setHiredate(String hiredate) {
		this.hiredate = hiredate;
	}
	public String getConfirmationdate() {
		return confirmationdate;
	}
	public void setConfirmationdate(String confirmationdate) {
		this.confirmationdate = confirmationdate;
	}
	public String getTerminationdate() {
		return terminationdate;
	}
	public void setTerminationdate(String terminationdate) {
		this.terminationdate = terminationdate;
	}
	public String getOriginalhiredate() {
		return originalhiredate;
	}
	public void setOriginalhiredate(String originalhiredate) {
		this.originalhiredate = originalhiredate;
	}
	public String getEmploymentstatus() {
		return employmentstatus;
	}
	public void setEmploymentstatus(String employmentstatus) {
		this.employmentstatus = employmentstatus;
	}
	
	
		
}
