package com.payasia.web.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface CoherentEmployeeOvertimeController {
	String getPendingTimesheet(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String rejectTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getDataForTimesheetReview(HttpServletRequest request,
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

	void exportOvertimeDetail(Long timesheetId, HttpServletResponse response,
			HttpServletRequest request) throws IOException;

	String getPendingEmpTimesheet(String columnName, String sortingType, String searchCondition, String searchText,
			int page, int rows, HttpServletRequest request, HttpServletResponse response, HttpSession session);

	String getApprovedOTEmpTimesheet(String columnName, String sortingType, String searchCondition, String searchText,
			int page, int rows, HttpServletRequest request, HttpServletResponse response, HttpSession session);

	String getRejectedOTEmpTimesheet(String columnName, String sortingType, String searchCondition, String searchText,
			int page, int rows, HttpServletRequest request, HttpServletResponse response, HttpSession session);

	String getAllOTEmpTimesheet(String columnName, String sortingType, String searchCondition, String searchText,
			String transactionType, int page, int rows, HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String openTimesheetEmpForEditing(long timesheetId, HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getDataForTimesheetEmpReview(HttpServletRequest request, HttpServletResponse response, HttpSession session);

	String updateCoherentOvertimeEmpDetailByRev(long employeeTimesheetDetailId, String inTime, String outTime,
			String breakTime, String totalHours, String ot15hours, String ot10day, String ot20day, String grandot10day,
			String grandot15hours, String grandot20day, String grandTotalHours, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String forwardEmpTimesheet(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Locale locale);

	String acceptEmpTimesheet(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Locale locale);

	String rejectEmpTimesheet(HttpServletRequest request, HttpServletResponse response, HttpSession session);

	String getTimesheetEmpWorkflowHistory(Long otTimesheetId, HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	byte[] printEmpTimesheetDetail(Long timesheetId, HttpServletResponse response, HttpServletRequest request);

	void exportOvertimeEmpDetail(Long timesheetId, HttpServletResponse response, HttpServletRequest request)
			throws IOException;
}
