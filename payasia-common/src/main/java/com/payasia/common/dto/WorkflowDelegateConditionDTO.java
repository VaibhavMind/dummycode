package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class WorkflowDelegateConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 37048526794843773L;
	private long workFlowDelegateId;
	private String employeeName;
	private String employeeNumber;
	private String workFlowType;
	private Long EmployeeId;
	private Long delegateId;
	private List<Long> appCodeIds;
	
	
	

	public List<Long> getAppCodeIds() {
		return appCodeIds;
	}

	public void setAppCodeIds(List<Long> appCodeIds) {
		this.appCodeIds = appCodeIds;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public long getWorkFlowDelegateId() {
		return workFlowDelegateId;
	}

	public void setWorkFlowDelegateId(long workFlowDelegateId) {
		this.workFlowDelegateId = workFlowDelegateId;
	}

	public String getWorkFlowType() {
		return workFlowType;
	}

	public void setWorkFlowType(String workFlowType) {
		this.workFlowType = workFlowType;
	}

	public Long getEmployeeId() {
		return EmployeeId;
	}

	public void setEmployeeId(Long employeeId) {
		EmployeeId = employeeId;
	}

	public Long getDelegateId() {
		return delegateId;
	}

	public void setDelegateId(Long delegateId) {
		this.delegateId = delegateId;
	}

}
