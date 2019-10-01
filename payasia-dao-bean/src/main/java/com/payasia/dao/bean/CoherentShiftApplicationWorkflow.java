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
@Table(name = "Coherent_Shift_Application_Workflow")
public class CoherentShiftApplicationWorkflow extends CompanyUpdatedBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Shift_Application_Workflow_ID")
	private long shiftApplicationWorkflowID;

	@ManyToOne
	@JoinColumn(name = "Shift_Application_ID")
	private CoherentShiftApplication coherentShiftApplication;

	@ManyToOne
	@JoinColumn(name = "Timesheet_Status_ID")
	private TimesheetStatusMaster timesheetStatusMaster;

	@ManyToOne
	@JoinColumn(name = "Created_By")
	private Employee createdBy;

	@Column(name = "Created_Date")
	private Timestamp createdDate;

	@Column(name = "Remarks")
	private String remarks;

	public long getShiftApplicationWorkflowID() {
		return shiftApplicationWorkflowID;
	}

	public void setShiftApplicationWorkflowID(long shiftApplicationWorkflowID) {
		this.shiftApplicationWorkflowID = shiftApplicationWorkflowID;
	}

	public CoherentShiftApplication getCoherentShiftApplication() {
		return coherentShiftApplication;
	}

	public void setCoherentShiftApplication(
			CoherentShiftApplication coherentShiftApplication) {
		this.coherentShiftApplication = coherentShiftApplication;
	}

	public Employee getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Employee createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public TimesheetStatusMaster getTimesheetStatusMaster() {
		return timesheetStatusMaster;
	}

	public void setTimesheetStatusMaster(
			TimesheetStatusMaster timesheetStatusMaster) {
		this.timesheetStatusMaster = timesheetStatusMaster;
	}

}
