package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Holiday_Master database table.
 * 
 */
@Entity
@Table(name = "Company_Holiday_Calendar")
public class CompanyHolidayCalendar extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Company_Holiday_Calendar_ID")
	private long companyHolidayCalendarId;

	@Column(name = "Calendar_Name")
	private String calendarName;

	@Column(name = "Calendar_Desc")
	private String calendarDesc;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@OneToMany(mappedBy = "companyHolidayCalendar")
	private Set<CompanyHolidayCalendarDetail> companyHolidayCalendarDetails;

	 
	@OneToMany(mappedBy = "companyHolidayCalendar")
	private Set<EmployeeHolidayCalendar> employeeHolidayCalendars;

	public CompanyHolidayCalendar() {
	}

	public long getCompanyHolidayCalendarId() {
		return companyHolidayCalendarId;
	}

	public void setCompanyHolidayCalendarId(long companyHolidayCalendarId) {
		this.companyHolidayCalendarId = companyHolidayCalendarId;
	}

	public String getCalendarName() {
		return calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}

	public String getCalendarDesc() {
		return calendarDesc;
	}

	public void setCalendarDesc(String calendarDesc) {
		this.calendarDesc = calendarDesc;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<CompanyHolidayCalendarDetail> getCompanyHolidayCalendarDetails() {
		return companyHolidayCalendarDetails;
	}

	public void setCompanyHolidayCalendarDetails(
			Set<CompanyHolidayCalendarDetail> companyHolidayCalendarDetails) {
		this.companyHolidayCalendarDetails = companyHolidayCalendarDetails;
	}

	public Set<EmployeeHolidayCalendar> getEmployeeHolidayCalendars() {
		return employeeHolidayCalendars;
	}

	public void setEmployeeHolidayCalendars(
			Set<EmployeeHolidayCalendar> employeeHolidayCalendars) {
		this.employeeHolidayCalendars = employeeHolidayCalendars;
	}

}