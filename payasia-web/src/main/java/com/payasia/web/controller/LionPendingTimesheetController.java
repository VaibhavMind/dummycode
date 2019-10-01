package com.payasia.web.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface LionPendingTimesheetController {

	/**
	 * @param columnName
	 * @param sortingType
	 * @param searchCondition
	 * @param searchText
	 * @param page
	 * @param rows
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	String getPendingTimesheet(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getDataForTimesheetReview(HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String acceptTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	String rejectTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getotPendingTimesheetReviewWorkflow(Long otTimesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	byte[] printTimesheetDetail(Long timesheetId, HttpServletResponse response,
			HttpServletRequest request);

	String getSubmittedTimesheet(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getApprovedTimesheet(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String openTimesheetForEditingReviewer(long timesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String openMultipleTimesheetForEditingReviewer(String timesheetIds,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	void exportTimesheetDetail(Long timesheetId, HttpServletResponse response,
			HttpServletRequest request) throws IOException;

	String approveTimesheetRow(long employeeTimesheetDetailId, String inTime,
			String outTime, String breakTime, String totalHours,
			String remarks, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String updateTimesheetRow(long employeeTimesheetDetailId, String inTime,
			String outTime, String breakTime, String totalHours,
			String remarks, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

}
