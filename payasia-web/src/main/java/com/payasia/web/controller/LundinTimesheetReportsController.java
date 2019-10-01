package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.LundinTimesheetReportsForm;

/**
 * The Interface LeaveReportsController.
 * 
 * @author ragulapraveen
 */
public interface LundinTimesheetReportsController {

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
	 * @param LundinOTTimesheetReportsForm
	 *            the lundinOTTimesheetReportsForm
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	void genTimewritingAndCostAllocationReportExcelFile(
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			HttpServletRequest request, HttpServletResponse response);

	String lundinDepartmentList(HttpServletRequest request);

	String lundinBlockList(HttpServletRequest request);

	String lundinAFEList(HttpServletRequest request, Long blockId);

	String showDailyPaidTimesheet(
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	void genDailyPaidTimesheetExcelFile(
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	void genDailyPaidTimesheetPDF(
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	String lundinEmployeeList(HttpServletRequest request);

	String showTimesheetStatusReport(
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	void genTimesheetStatusReportExcelFile(
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	void genTimesheetStatusReportPDF(
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Search employee.
	 * 
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @param searchCondition
	 *            the search condition
	 * @param searchText
	 *            the search text
	 * @param metaData
	 *            the meta data
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */

	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String searchCondition, String searchText,
			Boolean includeResignedEmployees, long departmentId,
			HttpServletRequest request, HttpServletResponse response);

	byte[] genTimesheetHistoryReportPDF(
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			HttpServletRequest request, HttpServletResponse response);

	byte[] genShortListTimesheetHistoryReportPDF(
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			HttpServletRequest request, HttpServletResponse response);

	String searchEmployeeByManager(String columnName, String sortingType,
			int page, int rows, String searchCondition, String searchText,
			Boolean includeResignedEmployees, long departmentId,
			HttpServletRequest request, HttpServletResponse response);

	String isEmployeeRoleAsTimesheetReviewer(HttpServletRequest request,
			HttpServletResponse response);

	String lundinEmpBlockList(HttpServletRequest request);

	String lundinEmpDepartmentList(HttpServletRequest request);

	String otBatchEmpList(HttpServletRequest request, int year);

	String lundinAFEEmpList(HttpServletRequest request, Long blockId);

	String lundinEmployeList(HttpServletRequest request);

	void genTimewritingAndCostAllocationEmpReportExcelFile(LundinTimesheetReportsForm lundinTimesheetReportsForm,
			HttpServletRequest request, HttpServletResponse response);

	String showEmpDailyPaidTimesheet(LundinTimesheetReportsForm lundinTimesheetReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale);

	void genDailyPaidEmpTimesheetExcelFile(LundinTimesheetReportsForm lundinTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request, HttpServletResponse response);

	void genDailyPaidEmpTimesheetPDF(LundinTimesheetReportsForm lundinTimesheetReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	String showTimesheetStatusEmpReport(LundinTimesheetReportsForm lundinTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request, HttpServletResponse response, Locale locale);

	void genTimesheetStatusEmpReportExcelFile(LundinTimesheetReportsForm lundinTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request, HttpServletResponse response);

	void genTimesheetStatusEmpReportPDF(LundinTimesheetReportsForm lundinTimesheetReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request, HttpServletResponse response);

	String searchEmploye(String columnName, String sortingType, int page, int rows, String searchCondition,
			String searchText, Boolean includeResignedEmployees, long departmentId, HttpServletRequest request,
			HttpServletResponse response);

	String searchEmployeeByEmpManager(String columnName, String sortingType, int page, int rows, String searchCondition,
			String searchText, Boolean includeResignedEmployees, long departmentId, HttpServletRequest request,
			HttpServletResponse response);

	byte[] genTimesheetHistoryEmpReportPDF(LundinTimesheetReportsForm lundinTimesheetReportsForm,
			HttpServletRequest request, HttpServletResponse response);

	byte[] genShortListTimesheetHistoryEmpReportPDF(LundinTimesheetReportsForm lundinTimesheetReportsForm,
			HttpServletRequest request, HttpServletResponse response);

	String isEmployeRoleAsTimesheetReviewer(HttpServletRequest request, HttpServletResponse response);

}
