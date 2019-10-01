package com.payasia.common.form;

import java.io.Serializable;

public class CalendarTemplateWeekForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3243001281775263328L;
	private long calTempWeekId;	
	private long shiftId;
	private long weekdayId;
	
	
	public long getCalTempWeekId() {
		return calTempWeekId;
	}
	public void setCalTempWeekId(long calTempWeekId) {
		this.calTempWeekId = calTempWeekId;
	}
	public long getShiftId() {
		return shiftId;
	}
	public void setShiftId(long shiftId) {
		this.shiftId = shiftId;
	}
	public long getWeekdayId() {
		return weekdayId;
	}
	public void setWeekdayId(long weekdayId) {
		this.weekdayId = weekdayId;
	}
	
	
	

}
