package com.payasia.common.dto;

public class TimeZoneDTO {
	
	private Long timeZoneId;
	private String timeZoneName;
	private String timeZoneGMTOffset;
	public Long getTimeZoneId() {
		return timeZoneId;
	}
	public void setTimeZoneId(Long timeZoneId) {
		this.timeZoneId = timeZoneId;
	}
	public String getTimeZoneName() {
		return timeZoneName;
	}
	public void setTimeZoneName(String timeZoneName) {
		this.timeZoneName = timeZoneName;
	}
	public String getTimeZoneGMTOffset() {
		return timeZoneGMTOffset;
	}
	public void setTimeZoneGMTOffset(String timeZoneGMTOffset) {
		this.timeZoneGMTOffset = timeZoneGMTOffset;
	}
	
	
	

}
