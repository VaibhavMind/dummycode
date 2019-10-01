package com.payasia.common.form;

import java.io.Serializable;

/**
 * The Class UsersForm.
 */
public class UsersForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -174204970868597982L;

	private Long employeeId;
	
	private Long roleId;
	
	private String companyId;
	
	private String firstName;
	
	private String lastName;
	
	private String roleName;
	
	private String companyName;
	
	private String loginName;
	
	private String employeeNumber;
	
	private boolean roleAssigned;
	
	private String assignCompanyCount ;
	
	/**
	 * Gets the employee id.
	 *
	 * @return the employee id
	 */
	public Long getEmployeeId() {
		return employeeId;
	}

	/**
	 * Sets the employee id.
	 *
	 * @param employeeId the new employee id
	 */
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * Gets the role id.
	 *
	 * @return the role id
	 */
	public Long getRoleId() {
		return roleId;
	}

	/**
	 * Sets the role id.
	 *
	 * @param roleId the new role id
	 */
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}


	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName the new first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName the new last name
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	/**
	 * Gets the company name.
	 *
	 * @return the company name
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Sets the company name.
	 *
	 * @param companyName the new company name
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	

	

	/**
	 * Gets the login name.
	 *
	 * @return the login name
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * Sets the login name.
	 *
	 * @param loginName the new login name
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * Gets the role name.
	 *
	 * @return the role name
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * Sets the role name.
	 *
	 * @param roleName the new role name
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * Checks if is role assigned.
	 *
	 * @return true, if is role assigned
	 */
	public boolean isRoleAssigned() {
		return roleAssigned;
	}

	/**
	 * Sets the role assigned.
	 *
	 * @param roleAssigned the new role assigned
	 */
	public void setRoleAssigned(boolean roleAssigned) {
		this.roleAssigned = roleAssigned;
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

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getAssignCompanyCount() {
		return assignCompanyCount;
	}

	public void setAssignCompanyCount(String assignCompanyCount) {
		this.assignCompanyCount = assignCompanyCount;
	}





}
