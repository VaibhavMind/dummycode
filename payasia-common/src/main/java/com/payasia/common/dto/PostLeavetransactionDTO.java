package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class PostLeavetransactionDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4732132857274146398L;
	
	
	private Long employeeLeaveSchemeTypeHistoryId;
	private BigDecimal days;
	private BigDecimal hours;
	private String endDate;
	private Boolean forfeitAtEndDate;
	private String startDate;
	private Long startSessionMasterId;
	private Long endSessionMasterId;
	private String reason;
	private Long transactionTypeId;
	private String transactionTypeName;
	private Long employeeLeaveSchemeTypeId;
	private Long leaveApplicationId;
	private Long leaveStatusId;
	private String employeeNumber;
	private String leaveSchemeName;
	private String leaveTypeName;
	private Long leaveSchemeId;
	private Long leaveTypeId;
	private Long empLeaveSchemeTypeId;
	private String startSession;
	private String endSession;
	private BigDecimal balances;
	private String forfeitAtEndDateStr;
	private String postedDate;
	
	
	public Long getEmployeeLeaveSchemeTypeHistoryId() {
		return employeeLeaveSchemeTypeHistoryId;
	}
	public void setEmployeeLeaveSchemeTypeHistoryId(
			Long employeeLeaveSchemeTypeHistoryId) {
		this.employeeLeaveSchemeTypeHistoryId = employeeLeaveSchemeTypeHistoryId;
	}
	public BigDecimal getDays() {
		return days;
	}
	public void setDays(BigDecimal days) {
		this.days = days;
	}
	
	
	
	public Boolean getForfeitAtEndDate() {
		return forfeitAtEndDate;
	}
	public void setForfeitAtEndDate(Boolean forfeitAtEndDate) {
		this.forfeitAtEndDate = forfeitAtEndDate;
	}
	public Long getStartSessionMasterId() {
		return startSessionMasterId;
	}
	public void setStartSessionMasterId(Long startSessionMasterId) {
		this.startSessionMasterId = startSessionMasterId;
	}
	public Long getEndSessionMasterId() {
		return endSessionMasterId;
	}
	public void setEndSessionMasterId(Long endSessionMasterId) {
		this.endSessionMasterId = endSessionMasterId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}

	public Long getTransactionTypeId() {
		return transactionTypeId;
	}
	public void setTransactionTypeId(Long transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}
	public Long getEmployeeLeaveSchemeTypeId() {
		return employeeLeaveSchemeTypeId;
	}
	public void setEmployeeLeaveSchemeTypeId(Long employeeLeaveSchemeTypeId) {
		this.employeeLeaveSchemeTypeId = employeeLeaveSchemeTypeId;
	}
	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}
	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}
	public Long getLeaveStatusId() {
		return leaveStatusId;
	}
	public void setLeaveStatusId(Long leaveStatusId) {
		this.leaveStatusId = leaveStatusId;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getLeaveSchemeName() {
		return leaveSchemeName;
	}
	public void setLeaveSchemeName(String leaveSchemeName) {
		this.leaveSchemeName = leaveSchemeName;
	}
	public String getLeaveTypeName() {
		return leaveTypeName;
	}
	public void setLeaveTypeName(String leaveTypeName) {
		this.leaveTypeName = leaveTypeName;
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
	public Long getEmpLeaveSchemeTypeId() {
		return empLeaveSchemeTypeId;
	}
	public void setEmpLeaveSchemeTypeId(Long empLeaveSchemeTypeId) {
		this.empLeaveSchemeTypeId = empLeaveSchemeTypeId;
	}
	public String getStartSession() {
		return startSession;
	}
	public void setStartSession(String startSession) {
		this.startSession = startSession;
	}
	public String getEndSession() {
		return endSession;
	}
	public void setEndSession(String endSession) {
		this.endSession = endSession;
	}
	public BigDecimal getBalances() {
		return balances;
	}
	public void setBalances(BigDecimal balances) {
		this.balances = balances;
	}
	public String getTransactionTypeName() {
		return transactionTypeName;
	}
	public void setTransactionTypeName(String transactionTypeName) {
		this.transactionTypeName = transactionTypeName;
	}
	public String getForfeitAtEndDateStr() {
		return forfeitAtEndDateStr;
	}
	public void setForfeitAtEndDateStr(String forfeitAtEndDateStr) {
		this.forfeitAtEndDateStr = forfeitAtEndDateStr;
	}
	public String getPostedDate() {
		return postedDate;
	}
	public void setPostedDate(String postedDate) {
		this.postedDate = postedDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public BigDecimal getHours() {
		return hours;
	}
	public void setHours(BigDecimal hours) {
		this.hours = hours;
	}
	
	

}
