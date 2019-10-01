package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class HolidayResponse implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 773318340679058980L;
	
	private List <HolidayDTO> holidays;

	public List<HolidayDTO> getHolidays() {
		return holidays;
	}

	public void setHolidays(List<HolidayDTO> holidays) {
		this.holidays = holidays;
	}
	
	
	
	
	
	

}
