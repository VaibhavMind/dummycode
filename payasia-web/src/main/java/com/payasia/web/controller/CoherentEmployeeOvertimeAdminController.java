package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.form.CoherentOvertimeDetailForm;
import com.payasia.common.form.ImportEmployeeOvertimeShiftForm;

public interface CoherentEmployeeOvertimeAdminController {
	String getPendingTimesheet(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String rejectTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String acceptTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	String forwardTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	byte[] printTimesheetDetail(Long timesheetId, HttpServletResponse response,
			HttpServletRequest request);

	String getApprovedOTTimesheet(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getRejectedOTTimesheet(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getAllOTTimesheet(String columnName, String sortingType,
			String searchCondition, String searchText, String transactionType,
			int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String openTimesheetForEditing(long timesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String updateCoherentOvertimeDetailByRev(long employeeTimesheetDetailId,
			String inTime, String outTime, String breakTime, String totalHours,
			String ot15hours, String ot10day, String ot20day,
			String grandot10day, String grandot15hours, String grandot20day,
			String grandTotalHours, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getTimesheetWorkflowHistory(Long otTimesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String viewMultipleTimesheetApps(String[] timesheetIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String showEmpWorkflowHistory(Long timesheetId, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String reviewMultipleAppByAdmin(
			CoherentOvertimeDetailForm coherentOvertimeDetailForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String getDataForTimesheetReview(HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String importEmployeeOvertime(
			ImportEmployeeOvertimeShiftForm importEmployeeOvertimeShift,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception;
}
