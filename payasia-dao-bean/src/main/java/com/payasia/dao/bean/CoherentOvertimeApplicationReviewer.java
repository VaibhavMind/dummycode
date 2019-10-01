package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Coherent_Overtime_Application_Reviewer")
public class CoherentOvertimeApplicationReviewer extends CompanyBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Overtime_Applciation_Reviewer_ID")
	private long overtimeApplciationReviewerID;

	@ManyToOne
	@JoinColumn(name = "Overtime_Application_ID")
	private CoherentOvertimeApplication coherentOvertimeApplication;

	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	@ManyToOne
	@JoinColumn(name = "Employee_Reviewer_ID")
	private Employee employeeReviewer;

	@Column(name = "Pending")
	private boolean pending;

	public long getOvertimeApplciationReviewerID() {
		return overtimeApplciationReviewerID;
	}

	public void setOvertimeApplciationReviewerID(
			long overtimeApplciationReviewerID) {
		this.overtimeApplciationReviewerID = overtimeApplciationReviewerID;
	}

	public CoherentOvertimeApplication getCoherentOvertimeApplication() {
		return coherentOvertimeApplication;
	}

	public void setCoherentOvertimeApplication(
			CoherentOvertimeApplication coherentOvertimeApplication) {
		this.coherentOvertimeApplication = coherentOvertimeApplication;
	}

	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}

	public Employee getEmployeeReviewer() {
		return employeeReviewer;
	}

	public void setEmployeeReviewer(Employee employeeReviewer) {
		this.employeeReviewer = employeeReviewer;
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

}
