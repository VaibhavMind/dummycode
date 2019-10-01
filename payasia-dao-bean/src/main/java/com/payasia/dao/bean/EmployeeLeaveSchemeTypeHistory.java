package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Employee_Leave_Scheme_Type_History database
 * table.
 * 
 */
@Entity
@Table(name = "Employee_Leave_Scheme_Type_History")
public class EmployeeLeaveSchemeTypeHistory extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Leave_Scheme_Type_History_ID")
	private long employeeLeaveSchemeTypeHistoryId;

	@Column(name = "Days")
	private BigDecimal days;

	@Column(name = "End_Date")
	private Timestamp endDate;

	@Column(name = "Forfeit_At_End_Date")
	private Boolean forfeitAtEndDate;

	@Column(name = "Start_Date")
	private Timestamp startDate;

	 
	@ManyToOne
	@JoinColumn(name = "Start_Session")
	private LeaveSessionMaster startSessionMaster;

	 
	@ManyToOne
	@JoinColumn(name = "End_Session")
	private LeaveSessionMaster endSessionMaster;

	@Column(name = "Reason")
	private String reason;

	 
	@ManyToOne
	@JoinColumn(name = "Transaction_Type")
	private AppCodeMaster appCodeMaster;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Leave_Scheme_Type_ID")
	private EmployeeLeaveSchemeType employeeLeaveSchemeType;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Application_ID")
	private LeaveApplication leaveApplication;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Status_ID")
	private LeaveStatusMaster leaveStatusMaster;

	public EmployeeLeaveSchemeTypeHistory() {
	}

	public long getEmployeeLeaveSchemeTypeHistoryId() {
		return this.employeeLeaveSchemeTypeHistoryId;
	}

	public void setEmployeeLeaveSchemeTypeHistoryId(
			long employeeLeaveSchemeTypeHistoryId) {
		this.employeeLeaveSchemeTypeHistoryId = employeeLeaveSchemeTypeHistoryId;
	}

	public BigDecimal getDays() {
		return this.days;
	}

	public void setDays(BigDecimal days) {
		this.days = days;
	}

	public Timestamp getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public AppCodeMaster getAppCodeMaster() {
		return this.appCodeMaster;
	}

	public void setAppCodeMaster(AppCodeMaster appCodeMaster) {
		this.appCodeMaster = appCodeMaster;
	}

	public EmployeeLeaveSchemeType getEmployeeLeaveSchemeType() {
		return this.employeeLeaveSchemeType;
	}

	public void setEmployeeLeaveSchemeType(
			EmployeeLeaveSchemeType employeeLeaveSchemeType) {
		this.employeeLeaveSchemeType = employeeLeaveSchemeType;
	}

	public LeaveApplication getLeaveApplication() {
		return this.leaveApplication;
	}

	public void setLeaveApplication(LeaveApplication leaveApplication) {
		this.leaveApplication = leaveApplication;
	}

	public LeaveStatusMaster getLeaveStatusMaster() {
		return this.leaveStatusMaster;
	}

	public void setLeaveStatusMaster(LeaveStatusMaster leaveStatusMaster) {
		this.leaveStatusMaster = leaveStatusMaster;
	}

	public LeaveSessionMaster getStartSessionMaster() {
		return startSessionMaster;
	}

	public void setStartSessionMaster(LeaveSessionMaster startSessionMaster) {
		this.startSessionMaster = startSessionMaster;
	}

	public LeaveSessionMaster getEndSessionMaster() {
		return endSessionMaster;
	}

	public void setEndSessionMaster(LeaveSessionMaster endSessionMaster) {
		this.endSessionMaster = endSessionMaster;
	}

	public Boolean getForfeitAtEndDate() {
		return forfeitAtEndDate;
	}

	public void setForfeitAtEndDate(Boolean forfeitAtEndDate) {
		this.forfeitAtEndDate = forfeitAtEndDate;
	}

}