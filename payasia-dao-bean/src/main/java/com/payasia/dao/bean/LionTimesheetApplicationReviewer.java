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
 * The persistent class for the Lundin_Employee_OT_Reviewer database table.
 * 
 */
@Entity
@Table(name = "Lion_Timesheet_Application_Reviewer")
public class LionTimesheetApplicationReviewer extends CompanyBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Timesheet_Reviewer_ID")
	private long employeeTimesheetReviewerId;

	@ManyToOne
	@JoinColumn(name = "Employee_Timesheet_Detail_ID")
	private LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail;

	@ManyToOne
	@JoinColumn(name = "Employee_Reviewer_ID")
	private Employee employeeReviewer;

	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	@Column(name = "Reviewer_Email")
	private String reviewerEmail;

	@Column(name = "Pending")
	private boolean pending;

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public String getReviewerEmail() {
		return reviewerEmail;
	}

	public void setReviewerEmail(String reviewerEmail) {
		this.reviewerEmail = reviewerEmail;
	}

	public LionTimesheetApplicationReviewer() {
	}

	public LionEmployeeTimesheetApplicationDetail getLionEmployeeTimesheetApplicationDetail() {
		return lionEmployeeTimesheetApplicationDetail;
	}

	public void setLionEmployeeTimesheetApplicationDetail(
			LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail) {
		this.lionEmployeeTimesheetApplicationDetail = lionEmployeeTimesheetApplicationDetail;
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