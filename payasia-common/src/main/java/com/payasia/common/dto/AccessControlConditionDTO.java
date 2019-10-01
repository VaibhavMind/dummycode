/**
 * @author vivekjain
 *
 */
package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * The Class EmployeeConditionDTO.
 */
public class AccessControlConditionDTO implements Serializable {

/**
	 * 
	 */
	private static final long serialVersionUID = -3626964433853522840L;

	/** The employee number. */
	private String employeeNumber;

	/** The employee name. */
	private String employeeName;
			
	private String employeeStatus;

	private Timestamp joinDate;
	
	private String fromDate;

	private String toDate;
	
	
	private String firstName;
	
	private String lastName;
	
	private Timestamp dob;
	
	private String email;
	
	private List<Long> employeeIds;
	
	private EmployeeShortListDTO employeeShortListDTO;
	
	
	

	public EmployeeShortListDTO getEmployeeShortListDTO() {
		return employeeShortListDTO;
	}

	public void setEmployeeShortListDTO(EmployeeShortListDTO employeeShortListDTO) {
		this.employeeShortListDTO = employeeShortListDTO;
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

	public Timestamp getDob() {
		return dob;
	}

	public void setDob(Timestamp dob) {
		this.dob = dob;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
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
	 * @param employeeNumber
	 *            the new employee number
	 */
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
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
	 * @param employeeName
	 *            the new employee name
	 */
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	public Timestamp getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Timestamp joinDate) {
		this.joinDate = joinDate;
	}

	public List<Long> getEmployeeIds() {
		return employeeIds;
	}

	public void setEmployeeIds(List<Long> employeeIds) {
		this.employeeIds = employeeIds;
	}


}
