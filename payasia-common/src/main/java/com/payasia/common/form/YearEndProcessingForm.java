package com.payasia.common.form;

import java.math.BigDecimal;

public class YearEndProcessingForm {
	
	private Long leaveSchemeId;
	private Long leaveTypeId;
	private Long leaveSchemeTypeId;
	
	private Long employeeId;
	private Long companyId;
	private Long leaveYearEndBatchId;
	private Long leaveYearEndEmployeeDetailId;
	private String employeesCount;
	private String processedDate;
	private String deletedDate;
	private String employeeName;
	private BigDecimal balance;
	private BigDecimal lapsed;
	private BigDecimal encashed;
	private BigDecimal closingBalance;
	
	private String leaveType;
	private String leavesCount;
	private Integer year;
	
	private String leaveRollOver;
	private String leaveActivate;
	
	private String leaveScheme;
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getLeaveYearEndBatchId() {
		return leaveYearEndBatchId;
	}
	public void setLeaveYearEndBatchId(Long leaveYearEndBatchId) {
		this.leaveYearEndBatchId = leaveYearEndBatchId;
	}
	public Long getLeaveYearEndEmployeeDetailId() {
		return leaveYearEndEmployeeDetailId;
	}
	public void setLeaveYearEndEmployeeDetailId(Long leaveYearEndEmployeeDetailId) {
		this.leaveYearEndEmployeeDetailId = leaveYearEndEmployeeDetailId;
	}
	public String getEmployeesCount() {
		return employeesCount;
	}
	public void setEmployeesCount(String employeesCount) {
		this.employeesCount = employeesCount;
	}
	public String getProcessedDate() {
		return processedDate;
	}
	public void setProcessedDate(String processedDate) {
		this.processedDate = processedDate;
	}
	public String getDeletedDate() {
		return deletedDate;
	}
	public void setDeletedDate(String deletedDate) {
		this.deletedDate = deletedDate;
	}
	
	
	
	
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public BigDecimal getLapsed() {
		return lapsed;
	}
	public void setLapsed(BigDecimal lapsed) {
		this.lapsed = lapsed;
	}
	public BigDecimal getEncashed() {
		return encashed;
	}
	public void setEncashed(BigDecimal encashed) {
		this.encashed = encashed;
	}
	public BigDecimal getClosingBalance() {
		return closingBalance;
	}
	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}
	public Long getLeaveSchemeId() {
		return leaveSchemeId;
	}
	public void setLeaveSchemeId(Long leaveSchemeId) {
		this.leaveSchemeId = leaveSchemeId;
	}
	public Long getLeaveTypeId() {
		return leaveTypeId;
	}
	public void setLeaveTypeId(Long leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}
	public Long getLeaveSchemeTypeId() {
		return leaveSchemeTypeId;
	}
	public void setLeaveSchemeTypeId(Long leaveSchemeTypeId) {
		this.leaveSchemeTypeId = leaveSchemeTypeId;
	}
	
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getLeaveScheme() {
		return leaveScheme;
	}
	public void setLeaveScheme(String leaveScheme) {
		this.leaveScheme = leaveScheme;
	}
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	public String getLeavesCount() {
		return leavesCount;
	}
	public void setLeavesCount(String leavesCount) {
		this.leavesCount = leavesCount;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getLeaveRollOver() {
		return leaveRollOver;
	}
	public void setLeaveRollOver(String leaveRollOver) {
		this.leaveRollOver = leaveRollOver;
	}
	public String getLeaveActivate() {
		return leaveActivate;
	}
	public void setLeaveActivate(String leaveActivate) {
		this.leaveActivate = leaveActivate;
	}
	
	
	
}
