package com.payasia.common.form;

import java.io.Serializable;

/**
 * The Class EmployeePreferencesForm.
 */
public class EmployeePreferencesForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1659599901935147803L;

	/** The employee type id. */
	private Long  employeeTypeId;
	
	/** The employee type. */
	private String employeeType;
	
	/** The probation period. */
	private Integer probationPeriod;
	
	/** The allow future date joining. */
	private boolean allowFutureDateJoining;
	
	/** The display resigned employee. */
	private boolean displayResignedEmployee;
	
	/** The display direct indirect employee. */
	private boolean displayDirectIndirectEmployee;

	

	/**
	 * Gets the probation period.
	 *
	 * @return the probation period
	 */
	public Integer getProbationPeriod() {
		return probationPeriod;
	}

	/**
	 * Sets the probation period.
	 *
	 * @param probationPeriod the new probation period
	 */
	public void setProbationPeriod(Integer probationPeriod) {
		this.probationPeriod = probationPeriod;
	}

	/**
	 * Checks if is allow future date joining.
	 *
	 * @return true, if is allow future date joining
	 */
	public boolean isAllowFutureDateJoining() {
		return allowFutureDateJoining;
	}

	/**
	 * Sets the allow future date joining.
	 *
	 * @param allowFutureDateJoining the new allow future date joining
	 */
	public void setAllowFutureDateJoining(boolean allowFutureDateJoining) {
		this.allowFutureDateJoining = allowFutureDateJoining;
	}

	/**
	 * Checks if is display resigned employee.
	 *
	 * @return true, if is display resigned employee
	 */
	public boolean isDisplayResignedEmployee() {
		return displayResignedEmployee;
	}

	/**
	 * Sets the display resigned employee.
	 *
	 * @param displayResignedEmployee the new display resigned employee
	 */
	public void setDisplayResignedEmployee(boolean displayResignedEmployee) {
		this.displayResignedEmployee = displayResignedEmployee;
	}

	/**
	 * Checks if is display direct indirect employee.
	 *
	 * @return true, if is display direct indirect employee
	 */
	public boolean isDisplayDirectIndirectEmployee() {
		return displayDirectIndirectEmployee;
	}

	/**
	 * Sets the display direct indirect employee.
	 *
	 * @param displayDirectIndirectEmployee the new display direct indirect employee
	 */
	public void setDisplayDirectIndirectEmployee(
			boolean displayDirectIndirectEmployee) {
		this.displayDirectIndirectEmployee = displayDirectIndirectEmployee;
	}

	/**
	 * Gets the employee type.
	 *
	 * @return the employee type
	 */
	public String getEmployeeType() {
		return employeeType;
	}

	/**
	 * Sets the employee type.
	 *
	 * @param employeeType the new employee type
	 */
	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}

	/**
	 * Gets the employee type id.
	 *
	 * @return the employee type id
	 */
	public Long getEmployeeTypeId() {
		return employeeTypeId;
	}

	/**
	 * Sets the employee type id.
	 *
	 * @param employeeTypeId the new employee type id
	 */
	public void setEmployeeTypeId(Long employeeTypeId) {
		this.employeeTypeId = employeeTypeId;
	}

	

}
