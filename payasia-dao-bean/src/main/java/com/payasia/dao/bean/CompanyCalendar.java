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
@Table(name = "Company_Calendar")
public class CompanyCalendar extends CompanyBaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Company_Calendar_ID")
	private long companyCalendarId;

	@Column(name = "Calendar_Date")
	private Timestamp calendarDate;

	 
	@ManyToOne
	@JoinColumn(name = "Calendar_Code")
	private CalendarCodeMaster calendarCodeMaster;

	 
	@ManyToOne
	@JoinColumn(name = "Company_Calendar_Template_ID")
	private CompanyCalendarTemplate companyCalendarTemplate;

	public CompanyCalendar() {
	}

	public long getCompanyCalendarId() {
		return companyCalendarId;
	}

	public void setCompanyCalendarId(long companyCalendarId) {
		this.companyCalendarId = companyCalendarId;
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

	public CompanyCalendarTemplate getCompanyCalendarTemplate() {
		return companyCalendarTemplate;
	}

	public void setCompanyCalendarTemplate(
			CompanyCalendarTemplate companyCalendarTemplate) {
		this.companyCalendarTemplate = companyCalendarTemplate;
	}

}