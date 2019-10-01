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
 * The persistent class for the Leave_Application_Reviewer database table.
 * 
 */
@Entity
@Table(name = "Leave_Application_Reviewer")
public class LeaveApplicationReviewer extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Application_Reviewer_ID")
	private long leaveApplicationReviewerId;

	@Column(name = "Pending")
	private boolean pending;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Reviewer_ID")
	private Employee employee;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Application_ID")
	private LeaveApplication leaveApplication;

	 
	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	public LeaveApplicationReviewer() {
	}

	public long getLeaveApplicationReviewerId() {
		return this.leaveApplicationReviewerId;
	}

	public void setLeaveApplicationReviewerId(long leaveApplicationReviewerId) {
		this.leaveApplicationReviewerId = leaveApplicationReviewerId;
	}

	public boolean getPending() {
		return this.pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public LeaveApplication getLeaveApplication() {
		return this.leaveApplication;
	}

	public void setLeaveApplication(LeaveApplication leaveApplication) {
		this.leaveApplication = leaveApplication;
	}

	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return this.workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}

}