package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class OTBatchMasterConditionDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4915596240759244943L;
	private String description;
	private Timestamp fromDate;
	private Timestamp toDate;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getFromDate() {
		return fromDate;
	}
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	public Timestamp getToDate() {
		return toDate;
	}
	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}
	
	
	
	

}
