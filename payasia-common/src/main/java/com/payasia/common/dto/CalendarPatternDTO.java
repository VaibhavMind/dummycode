package com.payasia.common.dto;

public class CalendarPatternDTO {
	private Long calendarPatternMasterId;
	private String patternName;
	private String patternDesc;
	private String calendarCode;
	
	
	public Long getCalendarPatternMasterId() {
		return calendarPatternMasterId;
	}
	public void setCalendarPatternMasterId(Long calendarPatternMasterId) {
		this.calendarPatternMasterId = calendarPatternMasterId;
	}
	public String getPatternName() {
		return patternName;
	}
	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}
	public String getPatternDesc() {
		return patternDesc;
	}
	public void setPatternDesc(String patternDesc) {
		this.patternDesc = patternDesc;
	}
	public String getCalendarCode() {
		return calendarCode;
	}
	public void setCalendarCode(String calendarCode) {
		this.calendarCode = calendarCode;
	}
}
