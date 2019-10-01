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
 * The persistent class for the HRIS_Change_Request_Reviewer database table.
 * 
 */
@Entity
@Table(name = "HRIS_Change_Request_Reviewer")
public class HRISChangeRequestReviewer extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HRIS_Change_Request_Reviewer_ID ")
	private long hrisChangeRequestReviewerId;

	 
	@ManyToOne
	@JoinColumn(name = "HRIS_Change_Request_ID")
	private HRISChangeRequest hrisChangeRequest;

	@Column(name = "Pending")
	private boolean pending;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Reviewer_ID")
	private Employee employeeReviewer;

	 
	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	public HRISChangeRequestReviewer() {
	}

	public boolean getPending() {
		return this.pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
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

	public long getHrisChangeRequestReviewerId() {
		return hrisChangeRequestReviewerId;
	}

	public void setHrisChangeRequestReviewerId(long hrisChangeRequestReviewerId) {
		this.hrisChangeRequestReviewerId = hrisChangeRequestReviewerId;
	}

	public HRISChangeRequest getHrisChangeRequest() {
		return hrisChangeRequest;
	}

	public void setHrisChangeRequest(HRISChangeRequest hrisChangeRequest) {
		this.hrisChangeRequest = hrisChangeRequest;
	}

}