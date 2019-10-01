package com.payasia.logic;

public interface LionTimesheetApplicationReviewerLogic {

	String getTimesheetApplications(long timesheetId, long employeeId,
			long companyId);

	String getMultipleTimesheetApplications(String timesheetId,
			long employeeId, long companyId);

}
