package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.LeaveSchemeForm;

public interface LeaveSchemeController {

	String viewLeaveScheme(int page, int rows, String columnName,
			String sortingType, String searchCondition, String searchText);

	String deleteLeaveScheme(Long leaveSchemeId);

	String getLeaveScheme(Long leaveSchemeId);

	String configureLeaveScheme(LeaveSchemeForm leaveSchemeForm);

	String getLeaveType(Long leaveSchemeId);

	String viewLeaveType(int page, int rows, String columnName,
			String sortingType, Long leaveSchemeId);

	String getLeaveTypeForEdit(Long leaveSchemeTypeId);

	void editLeaveType(Long leaveSchemeTypeId, LeaveSchemeForm leaveSchemeForm);

	String configureLeaveSchemeType(LeaveSchemeForm leaveSchemeForm);

	String getLeaveSchemeTypeForEdit(Long leaveSchemeTypeId);

	String getEmployeeFilterList();

	String saveEmployeeFilterList(String metaData, Long leaveSchemeTypeId);

	String getEditEmployeeFilterList(Long leaveSchemeTypeId);

	String addLeaveScheme(LeaveSchemeForm leaveSchemeForm);

	String getLeaveTypeLeaveScheme(Long leaveSchemeId, Long leaveTypeId);

	String configureLeaveSchemeTypeWorkFlow(LeaveSchemeForm leaveSchemeForm);

	String viewAssignedEmployees(int page, int rows, String columnName,
			String sortingType, Long leaveSchemeId);

	String getLeaveTypeForCombo(Long leaveSchemeId, Long leaveSchemeTypeId);

	String getAppCodeListForProration(String distMethodName);

	String addLeaveType(String[] leaveTypeId, Long leaveSchemeId,
			Locale locale);

	String deleteLeaveType(Long leaveSchemeTypeId, Long leaveSchemeId,
			Locale locale);

	void deleteFilter(Long filterId, HttpServletRequest request,
			HttpServletResponse response);

	String callRedistributeProc(Long leaveSchemeTypeId,
			Locale locale);

	String getEmployeeFilterListForAdmin();

	String getEmployeeFilterListEmp();

	String getEmployeeFilterListForAdminEmp();

}
