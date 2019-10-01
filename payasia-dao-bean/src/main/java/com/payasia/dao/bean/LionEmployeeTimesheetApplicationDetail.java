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
@Table(name = "Lion_Employee_Timesheet_Application_Detail")
public class LionEmployeeTimesheetApplicationDetail extends CompanyBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Employee_Timesheet_Detail_ID")
	private long employeeTimesheetDetailID;

	@ManyToOne
	@JoinColumn(name = "Timesheet_ID")
	private EmployeeTimesheetApplication employeeTimesheetApplication;

	@Column(name = "Timesheet_Date")
	private Timestamp timesheetDate;

	@Column(name = "In_Time")
	private Timestamp inTime;

	
	@Column(name = "Remarks")
	private String remarks;
	
	@Column(name = "Out_Time")
	private Timestamp outTime;

	@Column(name = "Break_Time_Hours")
	private Timestamp breakTimeHours;

	@Column(name = "Total_Hours_Worked")
	private Double totalHoursWorked;

	@Column(name = "In_Time_Changed")
	private boolean inTimeChanged;

	@Column(name = "Out_Time_Changed")
	private boolean outTimeChanged;

	@Column(name = "Break_Time_Hours_Changed")
	private boolean breakTimeHoursChanged;

	@ManyToOne
	@JoinColumn(name = "Timesheet_Status_ID")
	private TimesheetStatusMaster timesheetStatusMaster;

	public long getEmployeeTimesheetDetailID() {
		return employeeTimesheetDetailID;
	}

	public void setEmployeeTimesheetDetailID(long employeeTimesheetDetailID) {
		this.employeeTimesheetDetailID = employeeTimesheetDetailID;
	}

	public EmployeeTimesheetApplication getEmployeeTimesheetApplication() {
		return employeeTimesheetApplication;
	}

	public void setEmployeeTimesheetApplication(
			EmployeeTimesheetApplication employeeTimesheetApplication) {
		this.employeeTimesheetApplication = employeeTimesheetApplication;
	}

	public Timestamp getTimesheetDate() {
		return timesheetDate;
	}

	public void setTimesheetDate(Timestamp timesheetDate) {
		this.timesheetDate = timesheetDate;
	}

	public Timestamp getInTime() {
		return inTime;
	}

	public void setInTime(Timestamp inTime) {
		this.inTime = inTime;
	}

	public Timestamp getOutTime() {
		return outTime;
	}

	public void setOutTime(Timestamp outTime) {
		this.outTime = outTime;
	}

	public Timestamp getBreakTimeHours() {
		return breakTimeHours;
	}

	public void setBreakTimeHours(Timestamp breakTimeHours) {
		this.breakTimeHours = breakTimeHours;
	}

	public Double getTotalHoursWorked() {
		return totalHoursWorked;
	}

	public void setTotalHoursWorked(Double totalHoursWorked) {
		this.totalHoursWorked = totalHoursWorked;
	}

	public boolean isInTimeChanged() {
		return inTimeChanged;
	}

	public void setInTimeChanged(boolean inTimeChanged) {
		this.inTimeChanged = inTimeChanged;
	}

	public boolean isOutTimeChanged() {
		return outTimeChanged;
	}

	public void setOutTimeChanged(boolean outTimeChanged) {
		this.outTimeChanged = outTimeChanged;
	}

	public boolean isBreakTimeHoursChanged() {
		return breakTimeHoursChanged;
	}

	public void setBreakTimeHoursChanged(boolean breakTimeHoursChanged) {
		this.breakTimeHoursChanged = breakTimeHoursChanged;
	}

	public TimesheetStatusMaster getTimesheetStatusMaster() {
		return timesheetStatusMaster;
	}

	public void setTimesheetStatusMaster(
			TimesheetStatusMaster timesheetStatusMaster) {
		this.timesheetStatusMaster = timesheetStatusMaster;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
