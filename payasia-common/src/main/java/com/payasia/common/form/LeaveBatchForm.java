package com.payasia.common.form;

import java.io.Serializable;

public class LeaveBatchForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8081936479648309592L;
	private long leaveBatchID;
	private String description;
	private String startDate;
	private String endDate;
		

	public long getLeaveBatchID() {
		return leaveBatchID;
	}

	public void setLeaveBatchID(long leaveBatchID) {
		this.leaveBatchID = leaveBatchID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	
}
