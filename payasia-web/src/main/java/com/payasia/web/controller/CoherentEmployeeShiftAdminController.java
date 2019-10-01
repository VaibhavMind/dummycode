package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.form.CoherentShiftDetailForm;
import com.payasia.common.form.ImportEmployeeOvertimeShiftForm;

public interface CoherentEmployeeShiftAdminController {

	String getPendingTimesheet(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getApprovedShift(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getRejectedShift(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getAllShift(String columnName, String sortingType,
			String searchCondition, String searchText, String transactionType,
			int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getDataForTimesheetReview(HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String viewMultipleTimesheetApps(String[] timesheetIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String reviewMultipleAppByAdmin(
			CoherentShiftDetailForm coherentShiftDetailForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String rejectTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String acceptTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	String forwardTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	String importEmployeeShift(
			ImportEmployeeOvertimeShiftForm importEmployeeOvertimeShift,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception;

}
