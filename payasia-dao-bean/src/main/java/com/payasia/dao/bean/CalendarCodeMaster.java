package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Calendar_Code_Master database table.
 * 
 */
@Entity
@Table(name = "Calendar_Code_Master")
public class CalendarCodeMaster extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Calendar_Code_ID")
	private long calendarCodeId;

	@Column(name = "Code")
	private String code;

	 
	@ManyToOne
	@JoinColumn(name = "Value")
	private AppCodeMaster appCodeMaster;

	 
	@ManyToOne
	@JoinColumn(name = "Module_ID")
	private ModuleMaster moduleMaster;

	 
	@OneToMany(mappedBy = "calendarCodeMaster", cascade = { CascadeType.REMOVE })
	private Set<CalendarPatternDetail> calendarPatternDetails;

	 
	@OneToMany(mappedBy = "calendarCodeMaster")
	private Set<CompanyCalendar> companyCalendars;

	 
	@OneToMany(mappedBy = "calendarCodeMaster")
	private Set<EmployeeCalendar> employeeCalendar;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	public CalendarCodeMaster() {
	}

	public long getCalendarCodeId() {
		return calendarCodeId;
	}

	public void setCalendarCodeId(long calendarCodeId) {
		this.calendarCodeId = calendarCodeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public AppCodeMaster getAppCodeMaster() {
		return appCodeMaster;
	}

	public void setAppCodeMaster(AppCodeMaster appCodeMaster) {
		this.appCodeMaster = appCodeMaster;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<CalendarPatternDetail> getCalendarPatternDetails() {
		return calendarPatternDetails;
	}

	public void setCalendarPatternDetails(
			Set<CalendarPatternDetail> calendarPatternDetails) {
		this.calendarPatternDetails = calendarPatternDetails;
	}

	public Set<CompanyCalendar> getCompanyCalendars() {
		return companyCalendars;
	}

	public void setCompanyCalendars(Set<CompanyCalendar> companyCalendars) {
		this.companyCalendars = companyCalendars;
	}

	public ModuleMaster getModuleMaster() {
		return moduleMaster;
	}

	public void setModuleMaster(ModuleMaster moduleMaster) {
		this.moduleMaster = moduleMaster;
	}

	public Set<EmployeeCalendar> getEmployeeCalendar() {
		return employeeCalendar;
	}

	public void setEmployeeCalendar(Set<EmployeeCalendar> employeeCalendar) {
		this.employeeCalendar = employeeCalendar;
	}

}