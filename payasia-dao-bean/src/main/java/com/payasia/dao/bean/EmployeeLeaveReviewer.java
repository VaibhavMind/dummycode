package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Employee_Leave_Reviewer database table.
 * 
 */
@Entity
@Table(name = "Employee_Leave_Reviewer")
public class EmployeeLeaveReviewer extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Leave_Reviewer_ID")
	private long employeeLeaveReviewerID;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee1;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Reviewer_ID")
	private Employee employee2;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Leave_Scheme_ID")
	private EmployeeLeaveScheme employeeLeaveScheme;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Leave_Scheme_Type_ID")
	private EmployeeLeaveSchemeType employeeLeaveSchemeType;

	 
	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	public EmployeeLeaveReviewer() {
	}

	public long getEmployeeLeaveReviewerID() {
		return this.employeeLeaveReviewerID;
	}

	public void setEmployeeLeaveReviewerID(long employeeLeaveReviewerID) {
		this.employeeLeaveReviewerID = employeeLeaveReviewerID;
	}

	public Employee getEmployee1() {
		return this.employee1;
	}

	public void setEmployee1(Employee employee1) {
		this.employee1 = employee1;
	}

	public Employee getEmployee2() {
		return this.employee2;
	}

	public void setEmployee2(Employee employee2) {
		this.employee2 = employee2;
	}

	public EmployeeLeaveScheme getEmployeeLeaveScheme() {
		return employeeLeaveScheme;
	}

	public void setEmployeeLeaveScheme(EmployeeLeaveScheme employeeLeaveScheme) {
		this.employeeLeaveScheme = employeeLeaveScheme;
	}

	public EmployeeLeaveSchemeType getEmployeeLeaveSchemeType() {
		return employeeLeaveSchemeType;
	}

	public void setEmployeeLeaveSchemeType(
			EmployeeLeaveSchemeType employeeLeaveSchemeType) {
		this.employeeLeaveSchemeType = employeeLeaveSchemeType;
	}

	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return this.workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}

}