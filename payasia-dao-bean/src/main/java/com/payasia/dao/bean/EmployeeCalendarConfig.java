package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
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
 * The persistent class for the Calendar_Template_Master database table.
 * 
 */
@Entity
@Table(name = "Employee_Calendar_Config")
public class EmployeeCalendarConfig extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Employee_Calendar_Config_ID")
	private long employeeCalendarConfigId;

	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	@Column(name = "Start_Date")
	private Timestamp startDate;

	@Column(name = "End_Date")
	private Timestamp endDate;

	 
	@ManyToOne
	@JoinColumn(name = "Company_Calendar_Template_ID")
	private CompanyCalendarTemplate companyCalendarTemplate;

	 
	@OneToMany(mappedBy = "employeeCalendarConfig", cascade = { CascadeType.REMOVE })
	private Set<EmployeeCalendar> employeeCalendar;

	public EmployeeCalendarConfig() {
	}

	public long getEmployeeCalendarConfigId() {
		return employeeCalendarConfigId;
	}

	public void setEmployeeCalendarConfigId(long employeeCalendarConfigId) {
		this.employeeCalendarConfigId = employeeCalendarConfigId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public CompanyCalendarTemplate getCompanyCalendarTemplate() {
		return companyCalendarTemplate;
	}

	public void setCompanyCalendarTemplate(
			CompanyCalendarTemplate companyCalendarTemplate) {
		this.companyCalendarTemplate = companyCalendarTemplate;
	}

	public Set<EmployeeCalendar> getEmployeeCalendar() {
		return employeeCalendar;
	}

	public void setEmployeeCalendar(Set<EmployeeCalendar> employeeCalendar) {
		this.employeeCalendar = employeeCalendar;
	}

}