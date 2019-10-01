package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Employee_Type_Master database table.
 * 
 */
@Entity
@Table(name = "Employee_Type_Master")
public class EmployeeTypeMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Emp_Type_ID")
	private long empTypeId;

	@Column(name = "Emp_Type")
	private String empType;

	@Column(name = "Emp_Type_Desc")
	private String empTypeDesc;

	 
	@OneToMany(mappedBy = "employeeTypeMaster")
	private Set<EmployeePreferenceMaster> employeePreferenceMasters;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	public EmployeeTypeMaster() {
	}

	public long getEmpTypeId() {
		return this.empTypeId;
	}

	public void setEmpTypeId(long empTypeId) {
		this.empTypeId = empTypeId;
	}

	public String getEmpType() {
		return this.empType;
	}

	public void setEmpType(String empType) {
		this.empType = empType;
	}

	public String getEmpTypeDesc() {
		return this.empTypeDesc;
	}

	public void setEmpTypeDesc(String empTypeDesc) {
		this.empTypeDesc = empTypeDesc;
	}

	public Set<EmployeePreferenceMaster> getEmployeePreferenceMasters() {
		return this.employeePreferenceMasters;
	}

	public void setEmployeePreferenceMasters(
			Set<EmployeePreferenceMaster> employeePreferenceMasters) {
		this.employeePreferenceMasters = employeePreferenceMasters;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}