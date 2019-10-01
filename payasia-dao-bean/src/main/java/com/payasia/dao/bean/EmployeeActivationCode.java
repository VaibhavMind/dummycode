package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Employee_Document database table.
 * 
 */
@Entity
@Table(name = "Employee_Activation_Code")
public class EmployeeActivationCode extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Activation_Code_Id")
	private long employeeActivationCodeId;

	@Column(name = "Activation_Code")
	private String activationCode;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	 
	@OneToMany(mappedBy = "employeeActivationCode")
	private Set<EmployeeMobileDetails> employeeMobileDetails;

	@Column(name = "Active")
	private boolean active = false;

	public long getEmployeeActivationCodeId() {
		return employeeActivationCodeId;
	}

	public void setEmployeeActivationCodeId(long employeeActivationCodeId) {
		this.employeeActivationCodeId = employeeActivationCodeId;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Set<EmployeeMobileDetails> getEmployeeMobileDetails() {
		return employeeMobileDetails;
	}

	public void setEmployeeMobileDetails(
			Set<EmployeeMobileDetails> employeeMobileDetails) {
		this.employeeMobileDetails = employeeMobileDetails;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}