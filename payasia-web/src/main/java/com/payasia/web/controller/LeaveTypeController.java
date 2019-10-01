package com.payasia.web.controller;

import com.payasia.common.form.LeaveTypeForm;

public interface LeaveTypeController {

	String viewLeaveType(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows);

	String updateLeaveType(LeaveTypeForm leaveTypeForm, Long leaveTypeId);

	String saveLeaveType(LeaveTypeForm leaveTypeForm);

	String deleteLeaveType(Long leaveTypeId);

	String assignedLeaveSchemes(int page, int rows, String columnName,
			String sortingType, String searchCondition, String searchText,
			Long leaveTypeId);

	String getLeaveType(Long leaveTypeId);

	String deleteLeaveScheme(Long leaveSchemeTypeId, Long leaveSchemeId);

	String editLeaveType(Long leaveTypeId);

	String updateLeaveTypeSortOrder(String[] sortOrder);

	void addLeaveScheme(String[] leaveSchemeId, Long leaveTypeId);

}
