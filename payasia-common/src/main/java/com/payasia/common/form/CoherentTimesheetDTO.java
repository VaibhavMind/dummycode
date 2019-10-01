package com.payasia.common.form;

import java.io.Serializable;


public class CoherentTimesheetDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6347952981229536499L;
	private String inTime;
	private String outTime;
	private long employeeTimesheetDetailId;
	private String breakTime;
	private String dayType;
	private String totalHours;
	private String ot15hours;
	private String ot10day;
	private String ot20day;
	private String remarks;
	
	private String grandtotalhours;
	private String grandot15hours;
	private String grandot10day;
	private String grandot20day;
	public String getInTime() {
		return inTime;
	}
	public void setInTime(String inTime) {
		this.inTime = inTime;
	}
	public String getOutTime() {
		return outTime;
	}
	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}
	public long getEmployeeTimesheetDetailId() {
		return employeeTimesheetDetailId;
	}
	public void setEmployeeTimesheetDetailId(long employeeTimesheetDetailId) {
		this.employeeTimesheetDetailId = employeeTimesheetDetailId;
	}
	public String getBreakTime() {
		return breakTime;
	}
	public void setBreakTime(String breakTime) {
		this.breakTime = breakTime;
	}
	public String getDayType() {
		return dayType;
	}
	public void setDayType(String dayType) {
		this.dayType = dayType;
	}
	public String getTotalHours() {
		return totalHours;
	}
	public void setTotalHours(String totalHours) {
		this.totalHours = totalHours;
	}
	public String getOt15hours() {
		return ot15hours;
	}
	public void setOt15hours(String ot15hours) {
		this.ot15hours = ot15hours;
	}
	public String getOt10day() {
		return ot10day;
	}
	public void setOt10day(String ot10day) {
		this.ot10day = ot10day;
	}
	public String getOt20day() {
		return ot20day;
	}
	public void setOt20day(String ot20day) {
		this.ot20day = ot20day;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getGrandtotalhours() {
		return grandtotalhours;
	}
	public void setGrandtotalhours(String grandtotalhours) {
		this.grandtotalhours = grandtotalhours;
	}
	public String getGrandot15hours() {
		return grandot15hours;
	}
	public void setGrandot15hours(String grandot15hours) {
		this.grandot15hours = grandot15hours;
	}
	public String getGrandot10day() {
		return grandot10day;
	}
	public void setGrandot10day(String grandot10day) {
		this.grandot10day = grandot10day;
	}
	public String getGrandot20day() {
		return grandot20day;
	}
	public void setGrandot20day(String grandot20day) {
		this.grandot20day = grandot20day;
	}
	
	
	
		
}
