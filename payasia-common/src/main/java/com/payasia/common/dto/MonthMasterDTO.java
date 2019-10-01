package com.payasia.common.dto;

import java.io.Serializable;

public class MonthMasterDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3862899413329578516L;
	private String monthName;
	private long monthId;
	public String getMonthName() {
		return monthName;
	}
	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}
	public long getMonthId() {
		return monthId;
	}
	public void setMonthId(long monthId) {
		this.monthId = monthId;
	}
	
	
}
