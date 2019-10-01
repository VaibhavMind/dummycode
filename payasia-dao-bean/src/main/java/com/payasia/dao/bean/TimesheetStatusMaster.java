package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the Lundin_Timesheet_Status_Master database table.
 * 
 */
@Entity
@Table(name = "Timesheet_Status_Master")
public class TimesheetStatusMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Timesheet_Status_ID")
	private long timesheetStatusId;

	@Column(name = "Timesheet_Status_Desc")
	private String timesheetStatusDesc;

	@Column(name = "Timesheet_Status_Name")
	private String timesheetStatusName;

	public TimesheetStatusMaster() {
	}

	public long getTimesheetStatusId() {
		return timesheetStatusId;
	}

	public void setTimesheetStatusId(long timesheetStatusId) {
		this.timesheetStatusId = timesheetStatusId;
	}

	public String getTimesheetStatusDesc() {
		return timesheetStatusDesc;
	}

	public void setTimesheetStatusDesc(String timesheetStatusDesc) {
		this.timesheetStatusDesc = timesheetStatusDesc;
	}

	public String getTimesheetStatusName() {
		return timesheetStatusName;
	}

	public void setTimesheetStatusName(String timesheetStatusName) {
		this.timesheetStatusName = timesheetStatusName;
	}

}