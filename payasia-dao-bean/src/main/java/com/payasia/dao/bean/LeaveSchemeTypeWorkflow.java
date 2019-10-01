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
@Table(name = "Leave_Scheme_Type_Workflow")
public class LeaveSchemeTypeWorkflow extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Leave_Scheme_Type_Workflow_ID")
	private long leaveSchemeTypeWorkflowId;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Scheme_Type_ID")
	private LeaveSchemeType leaveSchemeType;

	 
	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	public LeaveSchemeTypeWorkflow() {
	}

	public long getLeaveSchemeTypeWorkflowId() {
		return leaveSchemeTypeWorkflowId;
	}

	public void setLeaveSchemeTypeWorkflowId(long leaveSchemeTypeWorkflowId) {
		this.leaveSchemeTypeWorkflowId = leaveSchemeTypeWorkflowId;
	}

	public LeaveSchemeType getLeaveSchemeType() {
		return leaveSchemeType;
	}

	public void setLeaveSchemeType(LeaveSchemeType leaveSchemeType) {
		this.leaveSchemeType = leaveSchemeType;
	}

	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}

}