package com.payasia.common.dto;

public class DateFormatDTO {
	
	private String dateFormatDesc;
	
	private String dateFormatValue;
	
	public DateFormatDTO() {}
	
	public DateFormatDTO(String dateFormatValue, String dateFormatDesc) {
		this.dateFormatValue = dateFormatValue;
		this.dateFormatDesc = dateFormatDesc;
	}
	
	public String getDateFormatDesc() {
		return dateFormatDesc;
	}
	
	public void setDateFormatDesc(String dateFormatDesc) {
		this.dateFormatDesc = dateFormatDesc;
	}
	
	public String getDateFormatValue() {
		return dateFormatValue;
	}
	
	public void setDateFormatValue(String dateFormatValue) {
		this.dateFormatValue = dateFormatValue;
	}
}
