package com.payasia.common.form;

import java.io.Serializable;

/**
 * The Class AdminAccessControlForm.
 */
public class AdminAccessControlForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1606834663898412323L;

	/** The employee id. */
	private long employeeId;
	
	/** The employee name. */
	private String employeeName;
	
	private String firstName;
	private String lastName;
	private String dob;
	/** The employee number. */
	private String employeeNumber;
	
	/** The join date. */
	private String joinDate;
	
	/** The status. */
	private String status;
	
	
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

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	

	

	/**
	 * Gets the employee number.
	 *
	 * @return the employee number
	 */
	public String getEmployeeNumber() {
		return employeeNumber;
	}

	/**
	 * Sets the employee number.
	 *
	 * @param employeeNumber the new employee number
	 */
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	/**
	 * Gets the join date.
	 *
	 * @return the join date
	 */
	public String getJoinDate() {
		return joinDate;
	}

	/**
	 * Sets the join date.
	 *
	 * @param joinDate the new join date
	 */
	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}


	/**
	 * Gets the employee id.
	 *
	 * @return the employee id
	 */
	public long getEmployeeId() {
		return employeeId;
	}

	/**
	 * Sets the employee id.
	 *
	 * @param employeeId the new employee id
	 */
	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * Gets the employee name.
	 *
	 * @return the employee name
	 */
	public String getEmployeeName() {
		return employeeName;
	}

	/**
	 * Sets the employee name.
	 *
	 * @param employeeName the new employee name
	 */
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
