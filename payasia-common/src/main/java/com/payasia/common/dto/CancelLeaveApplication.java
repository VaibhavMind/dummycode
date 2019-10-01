package com.payasia.common.dto;

import java.io.Serializable;

public class CancelLeaveApplication implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7333609627335510043L;
	private String fromDate;
	private String toDate;
	private String leaveScheme;
	private String leaveType;
	private long leaveApplicationId;
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getLeaveScheme() {
		return leaveScheme;
	}
	public void setLeaveScheme(String leaveScheme) {
		this.leaveScheme = leaveScheme;
	}
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	public long getLeaveApplicationId() {
		return leaveApplicationId;
	}
	public void setLeaveApplicationId(long leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}
	
	
	

}
