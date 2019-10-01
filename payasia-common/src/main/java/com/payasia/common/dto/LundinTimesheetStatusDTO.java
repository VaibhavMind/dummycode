package com.payasia.common.dto;

public class LundinTimesheetStatusDTO {
	private long timesheetStatusId;

	private String timesheetStatusDesc;

	public long getTimesheetStatusId() {
		return timesheetStatusId;
	}

	public void setTimesheetStatusId(long timesheetStatusId) {
		this.timesheetStatusId = timesheetStatusId;
	}

	public String getTimesheetStatusDesc() {
		return timesheetStatusDesc;
	}

	public void setTimesheetStatusDesc(String timesheetStatusDesc) {
		this.timesheetStatusDesc = timesheetStatusDesc;
	}

	public String getTimesheetStatusName() {
		return timesheetStatusName;
	}

	public void setTimesheetStatusName(String timesheetStatusName) {
		this.timesheetStatusName = timesheetStatusName;
	}

	private String timesheetStatusName;
}
