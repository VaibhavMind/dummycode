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
 * The persistent class for the OT_Application_Reviewer database table.
 * 
 */
@Entity
@Table(name="OT_Application_Reviewer")
public class OTApplicationReviewer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="OT_Application_Reviewer_ID")
	private long otApplicationReviewerId;

	@Column(name="Pending")
	private boolean pending;

	 
    @ManyToOne
	@JoinColumn(name="Employee_Reviewer_ID")
	private Employee employee;

	 
    @ManyToOne
	@JoinColumn(name="OT_Application_ID")
	private OTApplication otApplication;

	 
    @ManyToOne
	@JoinColumn(name="Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

    public OTApplicationReviewer() {
    }

	public long getOtApplicationReviewerId() {
		return this.otApplicationReviewerId;
	}

	public void setOtApplicationReviewerId(long otApplicationReviewerId) {
		this.otApplicationReviewerId = otApplicationReviewerId;
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
	
	public OTApplication getOtApplication() {
		return this.otApplication;
	}

	public void setOtApplication(OTApplication otApplication) {
		this.otApplication = otApplication;
	}
	
	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return this.workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}
	
}