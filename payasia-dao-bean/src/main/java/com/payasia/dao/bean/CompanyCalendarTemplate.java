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
 * The persistent class for the Calendar_Template_Master database table.
 * 
 */
@Entity
@Table(name = "Company_Calendar_Template")
public class CompanyCalendarTemplate extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Company_Calendar_Template_ID")
	private long companyCalendarTemplateId;

	@Column(name = "Template_Desc")
	private String templateDesc;

	@Column(name = "Template_Name")
	private String templateName;

	@Column(name = "Start_Year")
	private int Start_Year;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Calendar_Pattern_ID")
	private CalendarPatternMaster calendarPatternMaster;

	 
	@OneToMany(mappedBy = "companyCalendarTemplate", cascade = { CascadeType.REMOVE })
	private Set<CompanyCalendar> companyCalendars;

	 
	@OneToMany(mappedBy = "companyCalendarTemplate")
	private Set<EmployeeCalendarConfig> employeeCalendarConfigs;

	public CompanyCalendarTemplate() {
	}

	public long getCompanyCalendarTemplateId() {
		return companyCalendarTemplateId;
	}

	public void setCompanyCalendarTemplateId(long companyCalendarTemplateId) {
		this.companyCalendarTemplateId = companyCalendarTemplateId;
	}

	public String getTemplateDesc() {
		return templateDesc;
	}

	public void setTemplateDesc(String templateDesc) {
		this.templateDesc = templateDesc;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public int getStart_Year() {
		return Start_Year;
	}

	public void setStart_Year(int start_Year) {
		Start_Year = start_Year;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public CalendarPatternMaster getCalendarPatternMaster() {
		return calendarPatternMaster;
	}

	public void setCalendarPatternMaster(
			CalendarPatternMaster calendarPatternMaster) {
		this.calendarPatternMaster = calendarPatternMaster;
	}

	public Set<CompanyCalendar> getCompanyCalendars() {
		return companyCalendars;
	}

	public void setCompanyCalendars(Set<CompanyCalendar> companyCalendars) {
		this.companyCalendars = companyCalendars;
	}

	public Set<EmployeeCalendarConfig> getEmployeeCalendarConfigs() {
		return employeeCalendarConfigs;
	}

	public void setEmployeeCalendarConfigs(
			Set<EmployeeCalendarConfig> employeeCalendarConfigs) {
		this.employeeCalendarConfigs = employeeCalendarConfigs;
	}

}