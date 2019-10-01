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
 * The persistent class for the Claim_Template_Workflow database table.
 * 
 */
@Entity
@Table(name = "Claim_Template_Workflow")
public class ClaimTemplateWorkflow extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Claim_Template_Workflow_ID")
	private long claimTemplateWorkflowId;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Template_ID")
	private ClaimTemplate claimTemplate;

	 
	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	public ClaimTemplateWorkflow() {
	}

	public long getClaimTemplateWorkflowId() {
		return this.claimTemplateWorkflowId;
	}

	public void setClaimTemplateWorkflowId(long claimTemplateWorkflowId) {
		this.claimTemplateWorkflowId = claimTemplateWorkflowId;
	}

	public ClaimTemplate getClaimTemplate() {
		return this.claimTemplate;
	}

	public void setClaimTemplate(ClaimTemplate claimTemplate) {
		this.claimTemplate = claimTemplate;
	}

	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return this.workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}

}