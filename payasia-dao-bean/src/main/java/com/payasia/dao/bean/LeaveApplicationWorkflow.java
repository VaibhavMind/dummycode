package com.payasia.dao.bean;

import java.io.Serializable;
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
 * The persistent class for the Leave_Application_Workflow database table.
 * 
 */
@Entity
@Table(name = "Leave_Application_Workflow")
public class LeaveApplicationWorkflow extends CompanyUpdatedBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Leave_Application_Workflow_ID")
	private long leaveApplicationWorkflowID;

	@Column(name = "Created_Date")
	private Timestamp createdDate;

	@Column(name = "Email_CC")
	private String emailCC;

	@Column(name = "End_Date")
	private Timestamp endDate;

	@Column(name = "Forward_To")
	private String forwardTo;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Start_Date")
	private Timestamp startDate;

	@Column(name = "Total_Days")
	private Float totalDays;

	 
	@ManyToOne
	@JoinColumn(name = "Created_By")
	private Employee employee;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Application_ID")
	private LeaveApplication leaveApplication;

	 
	@ManyToOne
	@JoinColumn(name = "Leave_Status_ID")
	private LeaveStatusMaster leaveStatusMaster;

	 
	@ManyToOne
	@JoinColumn(name = "Start_Session")
	private LeaveSessionMaster startSessionMaster;

	 
	@ManyToOne
	@JoinColumn(name = "End_Session")
	private LeaveSessionMaster endSessionMaster;

	public LeaveApplicationWorkflow() {
	}

	public long getLeaveApplicationWorkflowID() {
		return this.leaveApplicationWorkflowID;
	}

	public void setLeaveApplicationWorkflowID(long leaveApplicationWorkflowID) {
		this.leaveApplicationWorkflowID = leaveApplicationWorkflowID;
	}

	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getEmailCC() {
		return this.emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public Timestamp getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getForwardTo() {
		return this.forwardTo;
	}

	public void setForwardTo(String forwardTo) {
		this.forwardTo = forwardTo;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Float getTotalDays() {
		return this.totalDays;
	}

	public void setTotalDays(Float totalDays) {
		this.totalDays = totalDays;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
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

}