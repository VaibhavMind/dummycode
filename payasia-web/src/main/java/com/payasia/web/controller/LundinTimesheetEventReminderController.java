package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.LundinTimesheetEventReminderForm;

public interface LundinTimesheetEventReminderController {

	String saveTimesheetEventReminder(
			LundinTimesheetEventReminderForm timesheetEventReminderForm,
			HttpServletRequest request, HttpServletResponse response);

	String getTimesheetEventReminderData(Long eventReminderConfigId,
			HttpServletRequest request, HttpServletResponse response);

	String deleteTimesheetEventReminder(Long eventReminderConfigId,
			HttpServletRequest request, HttpServletResponse response);

	String getTimesheetEventReminders(String columnName, String sortingType,
			String searchType, Long searchValue, int page, int rows,
			HttpServletRequest request, HttpServletResponse response);

	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String empName, String empNumber,
			HttpServletRequest request, HttpServletResponse response);

	String updateTimesheetEventReminderData(
			LundinTimesheetEventReminderForm timesheetEventReminderForm,
			HttpServletRequest request, HttpServletResponse response);

	String getMailTemplateList(HttpServletRequest request);

}
