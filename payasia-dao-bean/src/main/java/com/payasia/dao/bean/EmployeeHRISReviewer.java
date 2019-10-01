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
 * The persistent class for the Employee_HRIS_Reviewer database table.
 * 
 */
@Entity
@Table(name = "Employee_HRIS_Reviewer")
public class EmployeeHRISReviewer extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_HRIS_Reviewer_ID")
	private long employeeHRISReviewerId;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Reviewer_ID")
	private Employee employeeReviewer;

	 
	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	public EmployeeHRISReviewer() {
	}

	public long getEmployeeHRISReviewerId() {
		return employeeHRISReviewerId;
	}

	public void setEmployeeHRISReviewerId(long employeeHRISReviewerId) {
		this.employeeHRISReviewerId = employeeHRISReviewerId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Employee getEmployeeReviewer() {
		return employeeReviewer;
	}

	public void setEmployeeReviewer(Employee employeeReviewer) {
		this.employeeReviewer = employeeReviewer;
	}

	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return this.workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}

}