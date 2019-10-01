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
 * The persistent class for the Claim_Template_Item database table.
 * 
 */
@Entity
@Table(name = "Claim_Template_Item_Workflow")
public class ClaimTemplateItemWorkflow extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Claim_Template_Item_Workflow_ID")
	private long claimTemplateItemWorkflowId;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Template_Item_ID")
	private ClaimTemplateItem claimTemplateItem;

	 
	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	public ClaimTemplateItemWorkflow() {
	}

	public long getClaimTemplateItemWorkflowId() {
		return claimTemplateItemWorkflowId;
	}

	public void setClaimTemplateItemWorkflowId(long claimTemplateItemWorkflowId) {
		this.claimTemplateItemWorkflowId = claimTemplateItemWorkflowId;
	}

	public ClaimTemplateItem getClaimTemplateItem() {
		return claimTemplateItem;
	}

	public void setClaimTemplateItem(ClaimTemplateItem claimTemplateItem) {
		this.claimTemplateItem = claimTemplateItem;
	}

	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}

}