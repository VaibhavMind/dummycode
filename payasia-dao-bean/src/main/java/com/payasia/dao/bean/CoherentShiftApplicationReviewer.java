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
@Table(name = "Coherent_Shift_Application_Reviewer")
public class CoherentShiftApplicationReviewer extends CompanyBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Shift_Application_Reviewer_ID")
	private long shiftApplicationReviewerID;

	@ManyToOne
	@JoinColumn(name = "Shift_Application_ID")
	private CoherentShiftApplication coherentShiftApplication;

	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	@ManyToOne
	@JoinColumn(name = "Employee_Reviewer_ID")
	private Employee employeeReviewer;

	@Column(name = "Pending")
	private boolean pending;

	public long getShiftApplicationReviewerID() {
		return shiftApplicationReviewerID;
	}

	public void setShiftApplicationReviewerID(long shiftApplicationReviewerID) {
		this.shiftApplicationReviewerID = shiftApplicationReviewerID;
	}

	public CoherentShiftApplication getCoherentShiftApplication() {
		return coherentShiftApplication;
	}

	public void setCoherentShiftApplication(
			CoherentShiftApplication coherentShiftApplication) {
		this.coherentShiftApplication = coherentShiftApplication;
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
