package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Lundin_Timesheet_Reviewer database table.
 * 
 */
@Entity
@Table(name = "Timesheet_Application_Reviewer")
public class TimesheetApplicationReviewer extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Timesheet_Reviewer_ID")
	private long timesheetReviewerId;

	@Column(name = "Pending")
	private boolean pending;

	@ManyToOne
	@JoinColumn(name = "Timesheet_ID")
	private EmployeeTimesheetApplication employeeTimesheetApplication;

	@ManyToOne
	@JoinColumn(name = "Employee_Reviewer_ID")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	public TimesheetApplicationReviewer() {
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}

	public long getTimesheetReviewerId() {
		return timesheetReviewerId;
	}

	public void setTimesheetReviewerId(long timesheetReviewerId) {
		this.timesheetReviewerId = timesheetReviewerId;
	}

	public EmployeeTimesheetApplication getEmployeeTimesheetApplication() {
		return employeeTimesheetApplication;
	}

	public void setEmployeeTimesheetApplication(
			EmployeeTimesheetApplication employeeTimesheetApplication) {
		this.employeeTimesheetApplication = employeeTimesheetApplication;
	}

}