package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface CoherentPendingShiftController {

	String getPendingShift(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getApprovedOTShift(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getRejectedOTShift(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getAllOTShift(String columnName, String sortingType,
			String searchCondition, String searchText, String transactionType,
			int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String openSubmittedShift(long shiftApplicationId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getDataForTimesheetReview(HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String acceptShift(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	String rejectShift(HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String forwardShift(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	String getTimesheetWorkflowHistory(Long otTimesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	byte[] printShiftDetail(Long timesheetId, HttpServletResponse response,
			HttpServletRequest request);

	String updateShiftForBatch(long shiftId, String totalShift, String remarks,
			String isShift, String coherentShiftType, String shiftTypePerDate,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

}
