package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class LeaveGrantProcDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6677531259596530445L;
	private Long companyId;
	private String leaveSchemeTypeIds;
	private Timestamp currentDate;
	private Boolean isYearEndProcess;
	private Timestamp fromDate;
	private Timestamp toDate;
	private Boolean isNewHires;
	private String employeeIds;
	
	
	
	public Boolean getIsNewHires() {
		return isNewHires;
	}
	public void setIsNewHires(Boolean isNewHires) {
		this.isNewHires = isNewHires;
	}
	public Timestamp getFromDate() {
		return fromDate;
	}
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	public Timestamp getToDate() {
		return toDate;
	}
	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getLeaveSchemeTypeIds() {
		return leaveSchemeTypeIds;
	}
	public void setLeaveSchemeTypeIds(String leaveSchemeTypeIds) {
		this.leaveSchemeTypeIds = leaveSchemeTypeIds;
	}
	public Timestamp getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(Timestamp currentDate) {
		this.currentDate = currentDate;
	}
	public Boolean getIsYearEndProcess() {
		return isYearEndProcess;
	}
	public void setIsYearEndProcess(Boolean isYearEndProcess) {
		this.isYearEndProcess = isYearEndProcess;
	}
	public String getEmployeeIds() {
		return employeeIds;
	}
	public void setEmployeeIds(String employeeIds) {
		this.employeeIds = employeeIds;
	}

	
	
}
