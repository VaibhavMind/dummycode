/**
 * @author vivekjain
 *
 */
package com.payasia.common.dto;

import java.io.Serializable;

/**
 * The Class ManageRolesConditionDTO.
 */
public class ManageRolesConditionDTO implements Serializable {

/**
	 * 
	 */
	private static final long serialVersionUID = -3626964433853522840L;
    private String privilege;
    private String module;
    private String role;
    private String username;
    private String employeeNumber;
    private String firstName;
    private String lastName;
    private String section;
	public String getPrivilege() {
		return privilege;
	}
	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}

    
    
}
