package com.payasia.common.dto;

public class WorkDayUKNewStarterReportDTO extends WorkDayReportDTO{
	
	private NewStarterSpreadsheet newStarterSpreadsheet; 	
	private RecurringAllowances recurringAllowances;
	
	public NewStarterSpreadsheet getNewStarterSpreadsheet() {
		return newStarterSpreadsheet;
	}
	public void setNewStarterSpreadsheet(NewStarterSpreadsheet newStarterSpreadsheet) {
		this.newStarterSpreadsheet = newStarterSpreadsheet;
	}
	public RecurringAllowances getRecurringAllowances() {
		return recurringAllowances;
	}
	public void setRecurringAllowances(RecurringAllowances recurringAllowances) {
		this.recurringAllowances = recurringAllowances;
	}
	
	

}
