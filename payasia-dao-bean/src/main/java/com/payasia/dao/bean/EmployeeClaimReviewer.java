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
 * The persistent class for the Employee_Claim_Reviewer database table.
 * 
 */
@Entity
@Table(name = "Employee_Claim_Reviewer")
public class EmployeeClaimReviewer extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Claim_Reviewer_ID")
	private long employeeClaimReviewerId;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Claim_Template_ID")
	private EmployeeClaimTemplate employeeClaimTemplate;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee1;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Reviewer_ID")
	private Employee employee2;

	 
	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	public EmployeeClaimReviewer() {
	}

	public long getEmployeeClaimReviewerId() {
		return this.employeeClaimReviewerId;
	}

	public void setEmployeeClaimReviewerId(long employeeClaimReviewerId) {
		this.employeeClaimReviewerId = employeeClaimReviewerId;
	}

	public EmployeeClaimTemplate getEmployeeClaimTemplate() {
		return employeeClaimTemplate;
	}

	public void setEmployeeClaimTemplate(
			EmployeeClaimTemplate employeeClaimTemplate) {
		this.employeeClaimTemplate = employeeClaimTemplate;
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

	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return this.workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}

}