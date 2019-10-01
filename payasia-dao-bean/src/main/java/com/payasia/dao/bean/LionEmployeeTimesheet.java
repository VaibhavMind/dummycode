package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Lion_Employee_Timesheet")
public class LionEmployeeTimesheet extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Lion_Employee_Timesheet_ID")
	private long lionEmployeeTimesheetID;

	@ManyToOne
	@JoinColumn(name = "Timesheet_ID")
	private EmployeeTimesheetApplication employeeTimesheetApplication;

	@Column(name = "Timesheet_Total_Hours ")
	private Double TimesheetTotalHours;

	@Column(name = "Excess_Hours_Worked")
	private Double excessHoursWorked;

	public long getLionEmployeeTimesheetID() {
		return lionEmployeeTimesheetID;
	}

	public void setLionEmployeeTimesheetID(long lionEmployeeTimesheetID) {
		this.lionEmployeeTimesheetID = lionEmployeeTimesheetID;
	}

	public Double getTimesheetTotalHours() {
		return TimesheetTotalHours;
	}

	public void setTimesheetTotalHours(Double timesheetTotalHours) {
		TimesheetTotalHours = timesheetTotalHours;
	}

	public Double getExcessHoursWorked() {
		return excessHoursWorked;
	}

	public void setExcessHoursWorked(Double excessHoursWorked) {
		this.excessHoursWorked = excessHoursWorked;
	}

	public EmployeeTimesheetApplication getEmployeeTimesheetApplication() {
		return employeeTimesheetApplication;
	}

	public void setEmployeeTimesheetApplication(
			EmployeeTimesheetApplication employeeTimesheetApplication) {
		this.employeeTimesheetApplication = employeeTimesheetApplication;
	}

}
