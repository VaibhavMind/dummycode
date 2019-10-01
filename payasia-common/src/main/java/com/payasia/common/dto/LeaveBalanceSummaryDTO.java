package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.form.LeaveBalanceSummaryForm;

public class LeaveBalanceSummaryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String monthName;
	private List<LeaveBalanceSummaryForm> holidayDetails;

	public LeaveBalanceSummaryDTO() {}
	
	public LeaveBalanceSummaryDTO(String monthName, List<LeaveBalanceSummaryForm> holidayDetails) {
		super();
		this.monthName = monthName;
		this.holidayDetails = holidayDetails;
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	public List<LeaveBalanceSummaryForm> getHolidayDetails() {
		return holidayDetails;
	}

	public void setHolidayDetails(List<LeaveBalanceSummaryForm> holidayDetails) {
		this.holidayDetails = holidayDetails;
	}

}
