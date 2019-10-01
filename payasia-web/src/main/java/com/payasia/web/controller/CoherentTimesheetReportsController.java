package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.CoherentTimesheetReportsForm;

/**
 * The Interface LeaveReportsController.
 * 
 * @author ragulapraveen
 */
public interface CoherentTimesheetReportsController {

	/**
	 * Gets the OT Batch type list.
	 * 
	 * @param request
	 *            the request
	 * @return the OT Batch list
	 */
	String otBatchList(HttpServletRequest request, int year);

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
	void genEmpOvertimeDetailRepExcelFile(
			CoherentTimesheetReportsForm coherentTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	String coherentTimesheetEmpListForManager(HttpServletRequest request);

	String coherentTimesheetReviewerList(HttpServletRequest request);

	String coherentEmployeeList(HttpServletRequest request);

	String costCentreFieldList(HttpServletRequest request);

	void genEmpShiftDetailRepExcelFile(
			CoherentTimesheetReportsForm coherentTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	String otBatchEmpList(HttpServletRequest request, int year);

	String costCentreFieldEmpList(HttpServletRequest request);

	String coherentEmployeeeList(HttpServletRequest request);

	String coherentTimesheetReviewerEmpList(HttpServletRequest request);

	String coherentTimesheetEmployeeListForManager(HttpServletRequest request);

	void genEmployeeOvertimeDetailRepExcelFile(CoherentTimesheetReportsForm coherentTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request, HttpServletResponse response);

	void genEmployeeShiftDetailRepExcelFile(CoherentTimesheetReportsForm coherentTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request, HttpServletResponse response);

}
