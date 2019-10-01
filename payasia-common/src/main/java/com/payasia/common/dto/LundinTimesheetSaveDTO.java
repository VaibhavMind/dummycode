package com.payasia.common.dto;

import java.util.List;

public class LundinTimesheetSaveDTO {
	private long timesheetId;
	private String remarks;
	private String timesheetStatus;
	private boolean isEmptyTimesheetData;
	
	private List<LundinTimesheetDelDTO> delPostData;
	public List<LundinTimesheetDelDTO> getDelPostData() {
		return delPostData;
	}
	public void setDelPostData(List<LundinTimesheetDelDTO> delPostData) {
		this.delPostData = delPostData;
	}
	private  List<LundinTimesheetDetailSaveDTO> timesheetDetails;
	public long getTimesheetId() {
		return timesheetId;
	}
	public void setTimesheetId(long timesheetId) {
		this.timesheetId = timesheetId;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public List<LundinTimesheetDetailSaveDTO> getTimesheetDetails() {
		return timesheetDetails;
	}
	public void setTimesheetDetails(
			List<LundinTimesheetDetailSaveDTO> timesheetDetails) {
		this.timesheetDetails = timesheetDetails;
	}
	public String getTimesheetStatus() {
		return timesheetStatus;
	}
	public void setTimesheetStatus(String timesheetStatus) {
		this.timesheetStatus = timesheetStatus;
	}
	public boolean isEmptyTimesheetData() {
		return isEmptyTimesheetData;
	}
	public void setEmptyTimesheetData(boolean isEmptyTimesheetData) {
		this.isEmptyTimesheetData = isEmptyTimesheetData;
	}
	
}
