package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Holiday_Master database table.
 * 
 */
@Entity
@Table(name = "Employee_Holiday_Calendar")
public class EmployeeHolidayCalendar extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Holiday_Calendar_ID")
	private long employeeHolidayCalendarId;

	 
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	 
	@ManyToOne
	@JoinColumn(name = "Company_Holiday_Calendar_ID")
	private CompanyHolidayCalendar companyHolidayCalendar;

	public EmployeeHolidayCalendar() {
	}

	public long getEmployeeHolidayCalendarId() {
		return employeeHolidayCalendarId;
	}

	public void setEmployeeHolidayCalendarId(long employeeHolidayCalendarId) {
		this.employeeHolidayCalendarId = employeeHolidayCalendarId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public CompanyHolidayCalendar getCompanyHolidayCalendar() {
		return companyHolidayCalendar;
	}

	public void setCompanyHolidayCalendar(
			CompanyHolidayCalendar companyHolidayCalendar) {
		this.companyHolidayCalendar = companyHolidayCalendar;
	}

}