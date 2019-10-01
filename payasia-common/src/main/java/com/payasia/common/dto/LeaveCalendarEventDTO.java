package com.payasia.common.dto;

public class LeaveCalendarEventDTO {
	private String calendarDate;
	private String patternCode;
	private String patternValue;
	private Boolean isChanged;
	
	public String getCalendarDate() {
		return calendarDate;
	}
	public void setCalendarDate(String calendardate) {
		this.calendarDate = calendardate;
	}
	public String getPatternCode() {
		return patternCode;
	}
	public void setPatternCode(String patternCode) {
		this.patternCode = patternCode;
	}
	public String getPatternValue() {
		return patternValue;
	}
	public void setPatternValue(String patternValue) {
		this.patternValue = patternValue;
	}
	public Boolean getIsChanged() {
		return isChanged;
	}
	public void setIsChanged(Boolean isChanged) {
		this.isChanged = isChanged;
	}
	

}
