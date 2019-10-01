package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface LundinPendingTimesheetController {
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

	String getotPendingTimesheetReviewWorkflow(Long otTimesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	byte[] printTimesheetDetail(Long timesheetId, HttpServletResponse response,
			HttpServletRequest request);
}
