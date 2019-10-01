package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.LundinTimesheetSaveDTO;

public class LundinTimesheetForm extends PageResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6650101960060420115L;
	
	private String startDate;
	
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public boolean isCanWithdraw() {
		return canWithdraw;
	}
	public void setCanWithdraw(boolean canWithdraw) {
		this.canWithdraw = canWithdraw;
	}
	public List<String> getPublicHolidays() {
		return publicHolidays;
	}
	public void setPublicHolidays(List<String> publicHolidays) {
		this.publicHolidays = publicHolidays;
	}
	private String endDate;
	private List<String> publicHolidays;
	private List<String> blockedDays;
	private boolean canWithdraw;


	public LundinTimesheetSaveDTO getTimesheetDTO() {
		return TimesheetDTO;
	}
	public void setTimesheetDTO(LundinTimesheetSaveDTO timesheetDTO) {
		TimesheetDTO = timesheetDTO;
	}
	private LundinTimesheetSaveDTO TimesheetDTO;


	public List<String> getBlockedDays() {
		return blockedDays;
	}
	public void setBlockedDays(List<String> blockedDays) {
		this.blockedDays = blockedDays;
	}

	
}