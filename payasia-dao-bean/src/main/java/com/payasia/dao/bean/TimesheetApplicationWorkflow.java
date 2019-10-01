package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Lundin_Timesheet_Workflow database table.
 * 
 */
@Entity
@Table(name = "Timesheet_Application_Workflow")
public class TimesheetApplicationWorkflow extends CompanyUpdatedBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Timesheet_Workflow_ID")
	private long timesheetWorkflowId;

	@Column(name = "Email_CC")
	private String emailCC;

	@Column(name = "Forward_To")
	private String forwardTo;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Created_Date")
	private Timestamp createdDate;

	@ManyToOne
	@JoinColumn(name = "Created_By")
	private Employee createdBy;

	@ManyToOne
	@JoinColumn(name = "Timesheet_ID")
	private EmployeeTimesheetApplication employeeTimesheetApplication;

	@ManyToOne
	@JoinColumn(name = "Timesheet_Status_ID")
	private TimesheetStatusMaster timesheetStatusMaster;

	public TimesheetApplicationWorkflow() {
	}

	public long getTimesheetWorkflowId() {
		return timesheetWorkflowId;
	}

	public void setTimesheetWorkflowId(long timesheetWorkflowId) {
		this.timesheetWorkflowId = timesheetWorkflowId;
	}

	public String getEmailCC() {
		return emailCC;
	}

	public void setEmailCC(String emailCC) {
		this.emailCC = emailCC;
	}

	public String getForwardTo() {
		return forwardTo;
	}

	public void setForwardTo(String forwardTo) {
		this.forwardTo = forwardTo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Employee getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Employee createdBy) {
		this.createdBy = createdBy;
	}

	public EmployeeTimesheetApplication getEmployeeTimesheetApplication() {
		return employeeTimesheetApplication;
	}

	public void setEmployeeTimesheetApplication(
			EmployeeTimesheetApplication employeeTimesheetApplication) {
		this.employeeTimesheetApplication = employeeTimesheetApplication;
	}

	public TimesheetStatusMaster getTimesheetStatusMaster() {
		return timesheetStatusMaster;
	}

	public void setTimesheetStatusMaster(
			TimesheetStatusMaster timesheetStatusMaster) {
		this.timesheetStatusMaster = timesheetStatusMaster;
	}

}