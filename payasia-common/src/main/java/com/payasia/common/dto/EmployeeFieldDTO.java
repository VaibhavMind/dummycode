package com.payasia.common.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class EmployeeFieldDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5893916212070431037L;

	private Long employeeId;
	private String employeeName;
	private String employeeEmail;
	private String employeeNumber;
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getEmployeeEmail() {
		return employeeEmail;
	}
	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String empNumber) {
		this.employeeNumber = empNumber;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(employeeId).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EmployeeFieldDTO))
			return false;
		if (obj == this)
			return true;

		EmployeeFieldDTO empDto = (EmployeeFieldDTO) obj;
		return new EqualsBuilder().
				append(employeeId, empDto.employeeId).
				isEquals();
	}


}
