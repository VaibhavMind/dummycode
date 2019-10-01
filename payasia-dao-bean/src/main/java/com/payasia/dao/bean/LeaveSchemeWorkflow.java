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
 * The persistent class for the Leave_Scheme_Workflow database table.
 * 
 */
@Entity
@Table(name = "Leave_Scheme_Workflow")
public class LeaveSchemeWorkflow extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Leave_Scheme_Workflow_ID")
	private long leaveSchemeWorkflowId;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_ID")
	private LeaveScheme leaveScheme;

	 
	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	public LeaveSchemeWorkflow() {
	}

	public long getLeaveSchemeWorkflowId() {
		return this.leaveSchemeWorkflowId;
	}

	public void setLeaveSchemeWorkflowId(long leaveSchemeWorkflowId) {
		this.leaveSchemeWorkflowId = leaveSchemeWorkflowId;
	}

	public LeaveScheme getLeaveScheme() {
		return this.leaveScheme;
	}

	public void setLeaveScheme(LeaveScheme leaveScheme) {
		this.leaveScheme = leaveScheme;
	}

	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return this.workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}

}