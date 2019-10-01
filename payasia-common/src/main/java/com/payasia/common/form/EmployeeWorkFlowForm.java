package com.payasia.common.form;

import java.io.Serializable;

public class EmployeeWorkFlowForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2919778551877648042L;
	private String employeeName;
	private String employeeNumber;
	private String fromDate;
	private String toDate;
	private String delegateTo;
	private Long workFlow;
	private Long employeeId;
	private Long companyId;
	private long employeeWorkFlowDelegateId;

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

	public String getDelegateTo() {
		return delegateTo;
	}

	public void setDelegateTo(String delegateTo) {
		this.delegateTo = delegateTo;
	}

	public Long getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(Long workFlow) {
		this.workFlow = workFlow;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public long getEmployeeWorkFlowDelegateId() {
		return employeeWorkFlowDelegateId;
	}

	public void setEmployeeWorkFlowDelegateId(long employeeWorkFlowDelegateId) {
		this.employeeWorkFlowDelegateId = employeeWorkFlowDelegateId;
	}

}
