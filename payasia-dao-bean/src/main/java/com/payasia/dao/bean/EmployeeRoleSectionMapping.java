package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Employee_Role_Section_Mapping database table.
 * 
 */
@Entity
@Table(name = "Employee_Role_Section_Mapping")
public class EmployeeRoleSectionMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Role_Section_Mapping_ID")
	private long employeeRoleSectionMappingId;

	 
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	 
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "Role_ID")
	private RoleMaster roleMaster;

	@Column(name = "Form_ID")
	private long formId;

	public EmployeeRoleSectionMapping() {
	}

	public long getEmployeeRoleSectionMappingId() {
		return employeeRoleSectionMappingId;
	}

	public void setEmployeeRoleSectionMappingId(
			long employeeRoleSectionMappingId) {
		this.employeeRoleSectionMappingId = employeeRoleSectionMappingId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public RoleMaster getRoleMaster() {
		return roleMaster;
	}

	public void setRoleMaster(RoleMaster roleMaster) {
		this.roleMaster = roleMaster;
	}

	public long getFormId() {
		return formId;
	}

	public void setFormId(long formId) {
		this.formId = formId;
	}

}