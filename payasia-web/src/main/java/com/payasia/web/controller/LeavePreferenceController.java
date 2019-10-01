package com.payasia.web.controller;

import com.payasia.common.form.LeavePreferenceForm;

public interface LeavePreferenceController {

	void saveLeavePreference(LeavePreferenceForm leavePreferenceForm);

	String getLeavePreference();

	String getEmployeeFilterList();

}
