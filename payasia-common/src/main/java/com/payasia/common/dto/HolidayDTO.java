package com.payasia.common.dto;

import java.io.Serializable;

public class HolidayDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 132140840396930125L;
	
	private String day;
	private String description;
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	
	
}