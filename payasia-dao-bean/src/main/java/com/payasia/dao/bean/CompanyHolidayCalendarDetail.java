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

/**
 * The persistent class for the Holiday_Master database table.
 * 
 */
@Entity
@Table(name = "Company_Holiday_Calendar_Detail")
public class CompanyHolidayCalendarDetail extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Company_Holiday_Calendar_Detail_ID")
	private long companyHolidayCalendarDetailId;

	 
	@ManyToOne
	@JoinColumn(name = "Country_ID")
	private CountryMaster countryMaster;

	 
	@ManyToOne
	@JoinColumn(name = "State_ID")
	private StateMaster stateMaster;

	@Column(name = "Holiday_Date")
	private Timestamp holidayDate;

	@Column(name = "Holiday_Desc")
	private String holidayDesc;

	 
	@ManyToOne
	@JoinColumn(name = "Company_Holiday_Calendar_ID")
	private CompanyHolidayCalendar companyHolidayCalendar;

	public CompanyHolidayCalendarDetail() {
	}

	public long getCompanyHolidayCalendarDetailId() {
		return companyHolidayCalendarDetailId;
	}

	public void setCompanyHolidayCalendarDetailId(
			long companyHolidayCalendarDetailId) {
		this.companyHolidayCalendarDetailId = companyHolidayCalendarDetailId;
	}

	public CountryMaster getCountryMaster() {
		return countryMaster;
	}

	public void setCountryMaster(CountryMaster countryMaster) {
		this.countryMaster = countryMaster;
	}

	public StateMaster getStateMaster() {
		return stateMaster;
	}

	public void setStateMaster(StateMaster stateMaster) {
		this.stateMaster = stateMaster;
	}

	public Timestamp getHolidayDate() {
		return holidayDate;
	}

	public void setHolidayDate(Timestamp holidayDate) {
		this.holidayDate = holidayDate;
	}

	public String getHolidayDesc() {
		return holidayDesc;
	}

	public void setHolidayDesc(String holidayDesc) {
		this.holidayDesc = holidayDesc;
	}

	public CompanyHolidayCalendar getCompanyHolidayCalendar() {
		return companyHolidayCalendar;
	}

	public void setCompanyHolidayCalendar(
			CompanyHolidayCalendar companyHolidayCalendar) {
		this.companyHolidayCalendar = companyHolidayCalendar;
	}

}