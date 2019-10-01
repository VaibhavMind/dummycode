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
 * The persistent class for the OT_Template_Workflow database table.
 * 
 */
@Entity
@Table(name="OT_Template_Workflow")
public class OTTemplateWorkflow implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="OT_Template_Workflow_ID")
	private long OTTemplateWorkflowId;

	 
    @ManyToOne
	@JoinColumn(name="OT_Template_ID")
	private OTTemplate otTemplate;

	 
    @ManyToOne
	@JoinColumn(name="Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

    public OTTemplateWorkflow() {
    }

	public long getOTTemplateWorkflowId() {
		return this.OTTemplateWorkflowId;
	}

	public void setOTTemplateWorkflowId(long OTTemplateWorkflowId) {
		this.OTTemplateWorkflowId = OTTemplateWorkflowId;
	}

	public OTTemplate getOtTemplate() {
		return this.otTemplate;
	}

	public void setOtTemplate(OTTemplate otTemplate) {
		this.otTemplate = otTemplate;
	}
	
	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return this.workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}
	
}