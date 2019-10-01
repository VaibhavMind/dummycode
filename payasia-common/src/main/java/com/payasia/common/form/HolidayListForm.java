/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;


/**
 * The Class HolidayListForm.
 */
public class HolidayListForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6670431722160015621L;
	/** The holiday id. */
	private long holidayId;
	/** The day. */
	private String day;

	/** The dated. */
	private String dated;

	/** The occasion. */
	private String occasion;

	/** The restricted. */
	private boolean restricted = false;

	/** The company id. */
	private long companyId;
	
	/** The year. */
	private Integer year;
	
	private String status;
	
	private List<Integer> yearList;
	
	private Long cmpHolidayCalendarId;
	private String cmpHolidayCalendarName;
	
	private Long employeeHolidayCalendarId;
	private String employeeName;
	private String calendarName;
	
	
	
	
	
	
	

	public Long getEmployeeHolidayCalendarId() {
		return employeeHolidayCalendarId;
	}

	public void setEmployeeHolidayCalendarId(Long employeeHolidayCalendarId) {
		this.employeeHolidayCalendarId = employeeHolidayCalendarId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getCalendarName() {
		return calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}

	public Long getCmpHolidayCalendarId() {
		return cmpHolidayCalendarId;
	}

	public void setCmpHolidayCalendarId(Long cmpHolidayCalendarId) {
		this.cmpHolidayCalendarId = cmpHolidayCalendarId;
	}

	public String getCmpHolidayCalendarName() {
		return cmpHolidayCalendarName;
	}

	public void setCmpHolidayCalendarName(String cmpHolidayCalendarName) {
		this.cmpHolidayCalendarName = cmpHolidayCalendarName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Integer> getYearList() {
		return yearList;
	}

	public void setYearList(List<Integer> yearList) {
		this.yearList = yearList;
	}

	/**
	 * Gets the company id.
	 *
	 * @return the company id
	 */
	public long getCompanyId() {
		return companyId;
	}

	/**
	 * Sets the company id.
	 *
	 * @param companyId the new company id
	 */
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	/**
	 * Gets the year.
	 *
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * Sets the year.
	 *
	 * @param year the new year
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	/**
	 * Gets the holiday id.
	 * 
	 * @return the holiday id
	 */
	public long getHolidayId() {
		return holidayId;
	}

	/**
	 * Sets the holiday id.
	 * 
	 * @param holidayId
	 *            the new holiday id
	 */
	public void setHolidayId(long holidayId) {
		this.holidayId = holidayId;
	}

	/**
	 * Gets the day.
	 * 
	 * @return the day
	 */
	public String getDay() {
		return day;
	}

	/**
	 * Sets the day.
	 * 
	 * @param day
	 *            the new day
	 */
	public void setDay(String day) {
		this.day = day;
	}


	/**
	 * Gets the dated.
	 *
	 * @return the dated
	 */
	public String getDated() {
		return dated;
	}

	/**
	 * Sets the dated.
	 *
	 * @param dated the new dated
	 */
	public void setDated(String dated) {
		this.dated = dated;
	}

	/**
	 * Gets the occasion.
	 * 
	 * @return the occasion
	 */
	public String getOccasion() {
		return occasion;
	}

	/**
	 * Sets the occasion.
	 * 
	 * @param occasion
	 *            the new occasion
	 */
	public void setOccasion(String occasion) {
		this.occasion = occasion;
	}

	/**
	 * Gets the restricted.
	 * 
	 * @return the restricted
	 */
	public boolean getRestricted() {
		return restricted;
	}

	/**
	 * Sets the restricted.
	 * 
	 * @param restricted
	 *            the new restricted
	 */
	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

}
