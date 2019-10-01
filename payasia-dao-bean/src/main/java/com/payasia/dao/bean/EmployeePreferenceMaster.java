package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Employee_Preference_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Employee_Preference_Master")
public class EmployeePreferenceMaster extends BaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Emp_Pref_ID")
	private long empPrefID;

	@Column(name = "Allow_Future_Date_Joining")
	private boolean allowFutureDateJoining;

	@Column(name = "Display_Direct_Indirect_Employees")
	private boolean displayDirectIndirectEmployees;

	@Column(name = "Display_Resigned_Employees")
	private boolean displayResignedEmployees;

	@Column(name = "Probation_Period")
	private int probationPeriod;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Default_Emp_Status")
	private EmployeeTypeMaster employeeTypeMaster;

	public EmployeePreferenceMaster() {
	}

	public long getEmpPrefID() {
		return this.empPrefID;
	}

	public void setEmpPrefID(long empPrefID) {
		this.empPrefID = empPrefID;
	}

	public boolean getAllowFutureDateJoining() {
		return this.allowFutureDateJoining;
	}

	public void setAllowFutureDateJoining(boolean allowFutureDateJoining) {
		this.allowFutureDateJoining = allowFutureDateJoining;
	}

	public boolean getDisplayDirectIndirectEmployees() {
		return this.displayDirectIndirectEmployees;
	}

	public void setDisplayDirectIndirectEmployees(
			boolean displayDirectIndirectEmployees) {
		this.displayDirectIndirectEmployees = displayDirectIndirectEmployees;
	}

	public boolean getDisplayResignedEmployees() {
		return this.displayResignedEmployees;
	}

	public void setDisplayResignedEmployees(boolean displayResignedEmployees) {
		this.displayResignedEmployees = displayResignedEmployees;
	}

	public int getProbationPeriod() {
		return this.probationPeriod;
	}

	public void setProbationPeriod(int probationPeriod) {
		this.probationPeriod = probationPeriod;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public EmployeeTypeMaster getEmployeeTypeMaster() {
		return this.employeeTypeMaster;
	}

	public void setEmployeeTypeMaster(EmployeeTypeMaster employeeTypeMaster) {
		this.employeeTypeMaster = employeeTypeMaster;
	}

}