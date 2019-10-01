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
@Table(name = "Lion_Timesheet_Application_Workflow")
public class LionTimesheetApplicationWorkflow extends CompanyUpdatedBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Timesheet_Workflow_ID")
	private long timesheetWorkflowId;

	@ManyToOne
	@JoinColumn(name = "Employee_Timesheet_Detail_ID")
	private LionEmployeeTimesheetApplicationDetail employeeTimesheetApplicationDetail;

	@ManyToOne
	@JoinColumn(name = "Timesheet_Status_ID")
	private TimesheetStatusMaster timesheetStatusMaster;

	@Column(name = "Created_Date")
	private Timestamp createdDate;

	@ManyToOne
	@JoinColumn(name = "Created_By")
	private Employee createdBy;

	public LionTimesheetApplicationWorkflow() {
	}

	public long getTimesheetWorkflowId() {
		return timesheetWorkflowId;
	}

	public void setTimesheetWorkflowId(long timesheetWorkflowId) {
		this.timesheetWorkflowId = timesheetWorkflowId;
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

	public TimesheetStatusMaster getTimesheetStatusMaster() {
		return timesheetStatusMaster;
	}

	public void setTimesheetStatusMaster(
			TimesheetStatusMaster timesheetStatusMaster) {
		this.timesheetStatusMaster = timesheetStatusMaster;
	}

	public LionEmployeeTimesheetApplicationDetail getEmployeeTimesheetApplicationDetail() {
		return employeeTimesheetApplicationDetail;
	}

	public void setEmployeeTimesheetApplicationDetail(
			LionEmployeeTimesheetApplicationDetail employeeTimesheetApplicationDetail) {
		this.employeeTimesheetApplicationDetail = employeeTimesheetApplicationDetail;
	}

}