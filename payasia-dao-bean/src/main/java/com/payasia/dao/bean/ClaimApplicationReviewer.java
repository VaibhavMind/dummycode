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
 * The persistent class for the Claim_Application_Reviewer database table.
 * 
 */
@Entity
@Table(name = "Claim_Application_Reviewer")
public class ClaimApplicationReviewer extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Claim_Application_Reviewer_ID")
	private long claimApplicationReviewerId;

	@Column(name = "Pending")
	private boolean pending;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Application_ID")
	private ClaimApplication claimApplication;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Reviewer_ID")
	private Employee employee;

	 
	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	public ClaimApplicationReviewer() {
	}

	public long getClaimApplicationReviewerId() {
		return this.claimApplicationReviewerId;
	}

	public void setClaimApplicationReviewerId(long claimApplicationReviewerId) {
		this.claimApplicationReviewerId = claimApplicationReviewerId;
	}

	public boolean getPending() {
		return this.pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public ClaimApplication getClaimApplication() {
		return this.claimApplication;
	}

	public void setClaimApplication(ClaimApplication claimApplication) {
		this.claimApplication = claimApplication;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return this.workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}

}