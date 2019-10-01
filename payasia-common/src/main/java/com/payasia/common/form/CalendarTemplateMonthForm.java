package com.payasia.common.form;

import java.io.Serializable;
import java.util.Arrays;

public class CalendarTemplateMonthForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8567009168105161972L;
	private long calTemplateId;
	private int year;
	private long[] monthId= new long[12];
	private long[] calTempMonthId= new long[12];
	private String[] monthAbbr= new String[12];
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public long[] getMonthId() {
		return monthId;
	}
	public void setMonthId(long monthId[]) {
		 if(monthId !=null){
			   this.monthId = Arrays.copyOf(monthId, monthId.length); 
		} 
	}
	public long getCalTemplateId() {
		return calTemplateId;
	}
	public void setCalTemplateId(long calTemplateId) {
		this.calTemplateId = calTemplateId;
	}
	public long[] getCalTempMonthId() {
		return calTempMonthId;
	}
	public void setCalTempMonthId(long calTempMonthId[]) {
		 if(calTempMonthId !=null){
			   this.calTempMonthId = Arrays.copyOf(calTempMonthId, calTempMonthId.length); 
		} 
	}
	public String[] getMonthAbbr() {
		return monthAbbr;
	}
	public void setMonthAbbr(String monthAbbr[]) {
		 if(monthAbbr !=null){
			   this.monthAbbr = Arrays.copyOf(monthAbbr, monthAbbr.length); 
		} 
	}	

}
