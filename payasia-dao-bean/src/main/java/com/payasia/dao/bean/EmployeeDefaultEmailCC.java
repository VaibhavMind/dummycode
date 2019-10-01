package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Employee_Default_Email_CC database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Employee_Default_Email_CC")
public class EmployeeDefaultEmailCC extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Default_Email_CC_ID")
	private long employeeDefaultEmailCCId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "Module_ID")
	private ModuleMaster moduleMaster;

	@Column(name = "Email_CC")
	private String emailCC;

	public EmployeeDefaultEmailCC() {
	}

	public long getEmployeeDefaultEmailCCId() {
		return employeeDefaultEmailCCId;
	}

	public void setEmployeeDefaultEmailCCId(long employeeDefaultEmailCCId) {
		this.employeeDefaultEmailCCId = employeeDefaultEmailCCId;
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

	public ModuleMaster getModuleMaster() {
		return moduleMaster;
	}

	public void setModuleMaster(ModuleMaster moduleMaster) {
		this.moduleMaster = moduleMaster;
	}

	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

}