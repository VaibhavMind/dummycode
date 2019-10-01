package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;



public class LeaveBatchConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7849309705101452254L;
	private String description;
	private Timestamp startDate;
	private Timestamp endDate;
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getStartDate() {
		return startDate;
	}
	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}
	public Timestamp getEndDate() {
		return endDate;
	}
	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	
	
}
