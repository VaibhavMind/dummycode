package com.payasia.common.dto;

import java.io.Serializable;

public class WorkflowTypeDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6171386597179604227L;

	private long workflowTypeId;

	private long companyId;
	
	private String workflowTypeDesc;

	private String workflowTypeValue;

	public long getWorkflowTypeId() {
		return workflowTypeId;
	}

	public void setWorkflowTypeId(long workflowTypeId) {
		this.workflowTypeId = workflowTypeId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getWorkflowTypeDesc() {
		return workflowTypeDesc;
	}

	public void setWorkflowTypeDesc(String workflowTypeDesc) {
		this.workflowTypeDesc = workflowTypeDesc;
	}

	public String getWorkflowTypeValue() {
		return workflowTypeValue;
	}

	public void setWorkflowTypeValue(String workflowTypeValue) {
		this.workflowTypeValue = workflowTypeValue;
	}

	
	
}
