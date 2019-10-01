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
 * The persistent class for the Lundin_Employee_OT_Reviewer database table.
 * 
 */
@Entity
@Table(name = "Employee_Timesheet_Reviewer")
public class EmployeeTimesheetReviewer extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Timesheet_Reviewer_ID")
	private long employeeTimesheetReviewerId;

	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "Employee_Reviewer_ID")
	private Employee employeeReviewer;

	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	@Column(name = "Email")
	private String reviewerEmail;

	public String getReviewerEmail() {
		return reviewerEmail;
	}

	public void setReviewerEmail(String reviewerEmail) {
		this.reviewerEmail = reviewerEmail;
	}

	public EmployeeTimesheetReviewer() {
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
		return workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}

	public long getEmployeeTimesheetReviewerId() {
		return employeeTimesheetReviewerId;
	}

	public void setEmployeeTimesheetReviewerId(long employeeTimesheetReviewerId) {
		this.employeeTimesheetReviewerId = employeeTimesheetReviewerId;
	}

}