package com.payasia.common.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeLeaveSchemeTypeDTO {

	private long employeeLeaveSchemeTypeId;
	private long leaveTypeId;
	private String leaveType;
	private long leaveSchemeTypeId;
	private long employeeLeaveSchemeId;
	private BigDecimal balance;
	private BigDecimal carriedForward;
	private String createdBy;
	private String createdDate;
	private BigDecimal credited;
	private BigDecimal encashed;
	private String endDate;
	private BigDecimal forfeited;
	private BigDecimal pending;
	private String startDate;
	private BigDecimal taken;
	private Long employeeId;
	private String updatedBy;
	private String updatedDate;
	private int year;
	private int startYear;
    private String status;
	private Long leaveApplicationId;
	private String leaveSchemeName;
	private String employeeNumber;
	private String employeeName;
	private String noOfDays;
	private String startSession;
	private String endSession;
	private BigDecimal fullEntitlement;
	private String yearKey;
	private int yearValue;
	private Timestamp startDateTime;
	private Timestamp endDateTime ;
	private boolean isCurrentDateInLeaveCal;
	private Boolean action;
	
	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public long getEmployeeLeaveSchemeTypeId() {
		return employeeLeaveSchemeTypeId;
	}

	public void setEmployeeLeaveSchemeTypeId(long employeeLeaveSchemeTypeId) {
		this.employeeLeaveSchemeTypeId = employeeLeaveSchemeTypeId;
	}

	public long getLeaveTypeId() {
		return leaveTypeId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setLeaveTypeId(long leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public long getEmployeeLeaveSchemeId() {
		return employeeLeaveSchemeId;
	}

	public void setEmployeeLeaveSchemeId(long employeeLeaveSchemeId) {
		this.employeeLeaveSchemeId = employeeLeaveSchemeId;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getCarriedForward() {
		return carriedForward;
	}

	public void setCarriedForward(BigDecimal carriedForward) {
		this.carriedForward = carriedForward;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public BigDecimal getCredited() {
		return credited;
	}

	public void setCredited(BigDecimal credited) {
		this.credited = credited;
	}

	public BigDecimal getEncashed() {
		return encashed;
	}

	public void setEncashed(BigDecimal encashed) {
		this.encashed = encashed;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getForfeited() {
		return forfeited;
	}

	public void setForfeited(BigDecimal forfeited) {
		this.forfeited = forfeited;
	}

	public BigDecimal getPending() {
		return pending;
	}

	public void setPending(BigDecimal pending) {
		this.pending = pending;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public BigDecimal getTaken() {
		return taken;
	}

	public void setTaken(BigDecimal taken) {
		this.taken = taken;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(String noOfDays) {
		this.noOfDays = noOfDays;
	}

	public Long getLeaveApplicationId() {
		return leaveApplicationId;
	}

	public void setLeaveApplicationId(Long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}

	public String getLeaveSchemeName() {
		return leaveSchemeName;
	}

	public void setLeaveSchemeName(String leaveSchemeName) {
		this.leaveSchemeName = leaveSchemeName;
	}

	public long getLeaveSchemeTypeId() {
		return leaveSchemeTypeId;
	}

	public void setLeaveSchemeTypeId(long leaveSchemeTypeId) {
		this.leaveSchemeTypeId = leaveSchemeTypeId;
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

	public BigDecimal getFullEntitlement() {
		return fullEntitlement;
	}

	public void setFullEntitlement(BigDecimal fullEntitlement) {
		this.fullEntitlement = fullEntitlement;
	}

	public String getYearKey() {
		return yearKey;
	}

	public void setYearKey(String yearKey) {
		this.yearKey = yearKey;
	}

	public int getYearValue() {
		return yearValue;
	}

	public void setYearValue(int yearValue) {
		this.yearValue = yearValue;
	}

	public Timestamp getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Timestamp startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Timestamp getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(Timestamp endDateTime) {
		this.endDateTime = endDateTime;
	}

	public boolean isCurrentDateInLeaveCal() {
		return isCurrentDateInLeaveCal;
	}

	public void setCurrentDateInLeaveCal(boolean isCurrentDateInLeaveCal) {
		this.isCurrentDateInLeaveCal = isCurrentDateInLeaveCal;
	}

	public Boolean getAction() {
		return action;
	}

	public void setAction(Boolean action) {
		this.action = action;
	}
	

}
