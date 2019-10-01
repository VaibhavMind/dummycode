/**
 * @author Vivek Jain
 *
 */
package com.payasia.common.form;

import java.io.Serializable;


/**
 * The Class CompanyHolidayCalendarForm.
 */
public class CompanyHolidayCalendarForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6670431722160015621L;
	private Long holidayCalId;
	private Long companyHolidayCalId;
	private Long companyHolidayCalDetailId;
	private Long holidayConfigMasterId;
	private String calName;
	private String calDesc;
	private String noOfHolidays;
	
	private Long countryId;
	private String countryName;
	private Long stateId;
	private String stateName;
	private String holidayDate;
	private String holidayDesc;
	public Long getHolidayCalId() {
		return holidayCalId;
	}
	public void setHolidayCalId(Long holidayCalId) {
		this.holidayCalId = holidayCalId;
	}
	public Long getCompanyHolidayCalId() {
		return companyHolidayCalId;
	}
	public void setCompanyHolidayCalId(Long companyHolidayCalId) {
		this.companyHolidayCalId = companyHolidayCalId;
	}
	public String getCalName() {
		return calName;
	}
	public void setCalName(String calName) {
		this.calName = calName;
	}
	public String getCalDesc() {
		return calDesc;
	}
	public void setCalDesc(String calDesc) {
		this.calDesc = calDesc;
	}
	
	public Long getCountryId() {
		return countryId;
	}
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public Long getStateId() {
		return stateId;
	}
	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public String getHolidayDate() {
		return holidayDate;
	}
	public void setHolidayDate(String holidayDate) {
		this.holidayDate = holidayDate;
	}
	public String getHolidayDesc() {
		return holidayDesc;
	}
	public void setHolidayDesc(String holidayDesc) {
		this.holidayDesc = holidayDesc;
	}
	public String getNoOfHolidays() {
		return noOfHolidays;
	}
	public void setNoOfHolidays(String noOfHolidays) {
		this.noOfHolidays = noOfHolidays;
	}
	public Long getCompanyHolidayCalDetailId() {
		return companyHolidayCalDetailId;
	}
	public void setCompanyHolidayCalDetailId(Long companyHolidayCalDetailId) {
		this.companyHolidayCalDetailId = companyHolidayCalDetailId;
	}
	public Long getHolidayConfigMasterId() {
		return holidayConfigMasterId;
	}
	public void setHolidayConfigMasterId(Long holidayConfigMasterId) {
		this.holidayConfigMasterId = holidayConfigMasterId;
	}

}
