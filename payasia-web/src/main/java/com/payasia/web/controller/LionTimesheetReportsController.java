package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.LionTimesheetReportsForm;

/**
 * The Interface LeaveReportsController.
 * 
 * @author ragulapraveen
 */
public interface LionTimesheetReportsController {

	/**
	 * Gets the OT Batch type list.
	 * 
	 * @param request
	 *            the request
	 * @return the OT Batch list
	 */
	String otBatchList(HttpServletRequest request, int year);

	String lionEmployeeList(HttpServletRequest request);

	/**
	 * Function to generate excel file for Employee Timesheet report.
	 * 
	 * @param IngersollOTTimesheetReportsForm
	 *            the ingersollOTTimesheetReportsForm
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	String showTimesheetSummaryReport(
			LionTimesheetReportsForm lionTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	void genTimesheetSummaryReportPDF(
			LionTimesheetReportsForm lionTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	void genTimesheetSummaryReportExcelFile(
			LionTimesheetReportsForm lionTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	String locationFieldList(HttpServletRequest request);

	String lionTimesheetReviewerList(HttpServletRequest request);

	String lionTimesheetEmpListForManager(HttpServletRequest request);

	String showLionTimesheetDetailsSummaryReport(
			LionTimesheetReportsForm lionTimesheetDetailListingReportForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	void genTimesheetSummaryReportExcelFileDetails(
			LionTimesheetReportsForm lionTimesheetDetailListingReportForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	void genTimesheetSummaryReportPDFDetails(
			LionTimesheetReportsForm lionTimesheetDetailListingReportForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

}
