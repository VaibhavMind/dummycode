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

@Entity
@Table(name = "Coherent_Overtime_Application_Workflow")
public class CoherentOvertimeApplicationWorkflow extends
		CompanyUpdatedBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Overtime_Application_Workflow_ID")
	private long overtimeApplicationWorkflowID;

	@ManyToOne
	@JoinColumn(name = "Overtime_Application_ID")
	private CoherentOvertimeApplication coherentOvertimeApplication;

	@Column(name = "Created_Date")
	private Timestamp createdDate;

	@ManyToOne
	@JoinColumn(name = "Created_By")
	private Employee createdBy;

	@ManyToOne
	@JoinColumn(name = "Timesheet_Status_ID")
	private TimesheetStatusMaster timesheetStatusMaster;

	@Column(name = "Remarks")
	private String Remarks;

	public long getOvertimeApplicationWorkflowID() {
		return overtimeApplicationWorkflowID;
	}

	public void setOvertimeApplicationWorkflowID(
			long overtimeApplicationWorkflowID) {
		this.overtimeApplicationWorkflowID = overtimeApplicationWorkflowID;
	}

	public CoherentOvertimeApplication getCoherentOvertimeApplication() {
		return coherentOvertimeApplication;
	}

	public void setCoherentOvertimeApplication(
			CoherentOvertimeApplication coherentOvertimeApplication) {
		this.coherentOvertimeApplication = coherentOvertimeApplication;
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

	public String getRemarks() {
		return Remarks;
	}

	public void setRemarks(String remarks) {
		Remarks = remarks;
	}

}
