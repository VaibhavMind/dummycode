package com.payasia.common.dto;

import java.sql.Timestamp;

public class LundinTimesheetDetailDTO {
	private long timesheetDetailID;
	private boolean already;
	private long lundinTimesheetId;

	private Timestamp timesheetDate;

	private long lundinBlockId;

	public long getTimesheetDetailID() {
		return timesheetDetailID;
	}

	public void setTimesheetDetailID(long timesheetDetailID) {
		this.timesheetDetailID = timesheetDetailID;
	}

	public long getLundinTimesheetId() {
		return lundinTimesheetId;
	}

	public void setLundinTimesheetId(long lundinTimesheetId) {
		this.lundinTimesheetId = lundinTimesheetId;
	}

	public boolean isAlready() {
		return already;
	}

	public void setAlready(boolean already) {
		this.already = already;
	}

	public Timestamp getTimesheetDate() {
		return timesheetDate;
	}

	public void setTimesheetDate(Timestamp timesheetDate) {
		this.timesheetDate = timesheetDate;
	}

	public long getLundinBlockId() {
		return lundinBlockId;
	}

	public void setLundinBlockId(long lundinBlockId) {
		this.lundinBlockId = lundinBlockId;
	}

	public long getLundinAFEId() {
		return lundinAFEId;
	}

	public void setLundinAFEId(long lundinAFEId) {
		this.lundinAFEId = lundinAFEId;
	}

	public String getAppCodeMasterValue() {
		return appCodeMasterValue;
	}

	public void setAppCodeMasterValue(String appCodeMasterValue) {
		this.appCodeMasterValue = appCodeMasterValue;
	}

	private long lundinAFEId;

	private String appCodeMasterValue;
}
