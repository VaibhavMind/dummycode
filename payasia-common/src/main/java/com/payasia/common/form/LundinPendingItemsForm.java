package com.payasia.common.form;

import java.io.Serializable;


public class LundinPendingItemsForm implements Serializable {
	
	private static final long serialVersionUID = 3481933854758709873L;


	private Long timesheetId;
	private Long companyId;
	private Long employeeId;
	private String createdDate;
	private String updatedDate;
	private String remarks;
	private boolean canOverride;
	private boolean canReject;
	private boolean canApprove;
	private boolean canForward;
	private String emailCC;
	private String employeeName;
	public Long getTimesheetId() {
		return timesheetId;
	}
	public void setTimesheetId(Long timesheetId) {
		this.timesheetId = timesheetId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public boolean isCanOverride() {
		return canOverride;
	}
	public void setCanOverride(boolean canOverride) {
		this.canOverride = canOverride;
	}
	public boolean isCanReject() {
		return canReject;
	}
	public void setCanReject(boolean canReject) {
		this.canReject = canReject;
	}
	public boolean isCanApprove() {
		return canApprove;
	}
	public void setCanApprove(boolean canApprove) {
		this.canApprove = canApprove;
	}
	public boolean isCanForward() {
		return canForward;
	}
	public void setCanForward(boolean canForward) {
		this.canForward = canForward;
	}
	public String getEmailCC() {
		return emailCC;
	}
	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	

}
