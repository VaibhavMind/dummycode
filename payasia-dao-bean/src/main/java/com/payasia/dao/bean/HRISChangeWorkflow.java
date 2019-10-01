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
 * The persistent class for the HRIS_Change_Workflow database table.
 * 
 */
@Entity
@Table(name = "HRIS_Change_Workflow")
public class HRISChangeWorkflow extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "HRIS_Change_Workflow_ID")
	private long hrisChangeWorkflowId;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Work_Flow_Rule_ID")
	private WorkFlowRuleMaster workFlowRuleMaster;

	public HRISChangeWorkflow() {
	}

	public long getHrisChangeWorkflowId() {
		return hrisChangeWorkflowId;
	}

	public void setHrisChangeWorkflowId(long hrisChangeWorkflowId) {
		this.hrisChangeWorkflowId = hrisChangeWorkflowId;
	}

	public WorkFlowRuleMaster getWorkFlowRuleMaster() {
		return workFlowRuleMaster;
	}

	public void setWorkFlowRuleMaster(WorkFlowRuleMaster workFlowRuleMaster) {
		this.workFlowRuleMaster = workFlowRuleMaster;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}