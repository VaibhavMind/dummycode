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
 * The persistent class for the Calendar_Template_Master database table.
 * 
 */
@Entity
@Table(name = "Employee_Calendar")
public class EmployeeCalendar extends CompanyBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Calendar_ID")
	private long employeeCalendarId;

	@Column(name = "Calendar_Date")
	private Timestamp calendarDate;

	 
	@ManyToOne
	@JoinColumn(name = "Calendar_Code")
	private CalendarCodeMaster calendarCodeMaster;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_Calendar_Config_ID")
	private EmployeeCalendarConfig employeeCalendarConfig;

	public EmployeeCalendar() {
	}

	public long getEmployeeCalendarId() {
		return employeeCalendarId;
	}

	public void setEmployeeCalendarId(long employeeCalendarId) {
		this.employeeCalendarId = employeeCalendarId;
	}

	public Timestamp getCalendarDate() {
		return calendarDate;
	}

	public void setCalendarDate(Timestamp calendarDate) {
		this.calendarDate = calendarDate;
	}

	public CalendarCodeMaster getCalendarCodeMaster() {
		return calendarCodeMaster;
	}

	public void setCalendarCodeMaster(CalendarCodeMaster calendarCodeMaster) {
		this.calendarCodeMaster = calendarCodeMaster;
	}

	public EmployeeCalendarConfig getEmployeeCalendarConfig() {
		return employeeCalendarConfig;
	}

	public void setEmployeeCalendarConfig(
			EmployeeCalendarConfig employeeCalendarConfig) {
		this.employeeCalendarConfig = employeeCalendarConfig;
	}

}