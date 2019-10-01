package com.payasia.common.dto;

import java.io.Serializable;
import java.util.Date;

public class HolidayListMasterDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 180049119318624036L;
	private String country;
	private String state;
	private Date holidayDate;
	private String holidayDateString;
	private String occasion;
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getOccasion() {
		return occasion;
	}
	public void setOccasion(String occasion) {
		this.occasion = occasion;
	}
	public Date getHolidayDate() {
		return holidayDate;
	}
	public void setHolidayDate(Date holidayDate) {
		this.holidayDate = holidayDate;
	}
	public String getHolidayDateString() {
		return holidayDateString;
	}
	public void setHolidayDateString(String holidayDateString) {
		this.holidayDateString = holidayDateString;
	}
	
}
