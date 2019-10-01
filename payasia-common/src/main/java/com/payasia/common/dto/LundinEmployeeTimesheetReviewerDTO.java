package com.payasia.common.dto;


public class LundinEmployeeTimesheetReviewerDTO {
	private long employeeTimesheetReviewerId;

	private long employeeId;

	private long employeeReviewerId;

	private long workFlowRuleMasterId;
	private String empName;
	private String empReviewerName;
	private String empReviewerEmail;
	
	public long getEmployeeTimesheetReviewerId() {
		return employeeTimesheetReviewerId;
	}

	public void setEmployeeTimesheetReviewerId(long employeeTimesheetReviewerId) {
		this.employeeTimesheetReviewerId = employeeTimesheetReviewerId;
	}

	public long getEmployeeId() {
		return employeeId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmpReviewerName() {
		return empReviewerName;
	}

	public void setEmpReviewerName(String empReviewerName) {
		this.empReviewerName = empReviewerName;
	}

	public String getEmpReviewerEmail() {
		return empReviewerEmail;
	}

	public void setEmpReviewerEmail(String empReviewerEmail) {
		this.empReviewerEmail = empReviewerEmail;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public long getEmployeeReviewerId() {
		return employeeReviewerId;
	}

	public void setEmployeeReviewerId(long employeeReviewerId) {
		this.employeeReviewerId = employeeReviewerId;
	}

	public long getWorkFlowRuleMasterId() {
		return workFlowRuleMasterId;
	}

	public void setWorkFlowRuleMasterId(long workFlowRuleMasterId) {
		this.workFlowRuleMasterId = workFlowRuleMasterId;
	}

	


	
}
