package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.LeaveEventReminderForm;

public interface LeaveEventReminderController {

	String saveLeaveEventReminder(
			LeaveEventReminderForm leaveEventReminderForm);

	String getLeaveEventReminderData(Long eventReminderConfigId);

	String updateEventReminderData(
			LeaveEventReminderForm leaveEventReminderForm,
			Long eventReminderConfigId);

	String deleteLeaveEventReminder(Long eventReminderConfigId);

	String getEventReminders(String columnName, String sortingType,
			String searchType, Long searchValue, int page, int rows);

	void scheDulerExample(HttpServletRequest request,
			HttpServletResponse response);

	String getLeaveTypes(Long leaveSchemeId);

}
