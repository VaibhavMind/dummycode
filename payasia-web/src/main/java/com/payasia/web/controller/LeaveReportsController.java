package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.LeaveReportsForm;

/**
 * The Interface LeaveReportsController.
 * 
 * @author ragulapraveen
 */
public interface LeaveReportsController {

	/**
	 * Purpose : View Leave Reports .
	 * 
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @param request
	 *            the request
	 * @return the string
	 */
	String viewLeaveReports(String columnName, String sortingType, int page,
			int rows, HttpServletRequest request);

	/**
	 * Gets the leave type list.
	 * 
	 * @param request
	 *            the request
	 * @return the leave type list
	 */
	String getLeaveTypeList(HttpServletRequest request);

	/**
	 * Gets the leave transaction list.
	 * 
	 * @param request
	 *            the request
	 * @return the leave transaction list
	 */
	String getLeaveTransactionList(HttpServletRequest request, Locale locale);

	/**
	 * Function to show leave balance as on day report data in Data Tables
	 * 
	 * @param leaveReportsForm
	 *            the leave reports form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String showLeaveBalanceAsOnDateReport(LeaveReportsForm leaveReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * Function to show leave Reviewer report data in Data Tables.
	 * 
	 * @param leaveReportsForm
	 *            the leave reports form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String showLeaveReviewerReport(LeaveReportsForm leaveReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	/**
	 * Function to generate excel file for leave balance as on day report.
	 * 
	 * @param leaveReportsForm
	 *            the leave reports form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	void genLeaveBalAsOnDayReportExcelFile(LeaveReportsForm leaveReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Function to generate excel file for leave reviewer report.
	 * 
	 * @param leaveReportsForm
	 *            the leave reports form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	void genLeaveReviewerReportExcelFile(LeaveReportsForm leaveReportsForm,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * Function to generate pdf file for leave balance as on day report.
	 * 
	 * @param leaveReportsForm
	 *            the leave reports form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	void genLeaveBalAsOnDayReportPDFFile(LeaveReportsForm leaveReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Function to generate pdf file for leave reviewer report.
	 * 
	 * @param leaveReportsForm
	 *            the leave reports form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	void genLeaveReviewerReportPDFFile(LeaveReportsForm leaveReportsForm,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * Function to show year wise summary report data in Data Tables.
	 * 
	 * @param leaveReportsForm
	 *            the leave reports form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String showYearWiseSummaryReport(LeaveReportsForm leaveReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * Function to generate pdf file for year wise summary report.
	 * 
	 * @param leaveReportsForm
	 *            the leave reports form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	void genYearWiseSummarryReportPDFFile(LeaveReportsForm leaveReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	void genYearWiseSummarryExcelReport(LeaveReportsForm leaveReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * Function to show leave transaction report data in Data Tables
	 * 
	 * @param leaveReportsForm
	 *            the leave reports form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String showLeaveTranReport(LeaveReportsForm leaveReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * Function to generate excel file for leave transaction report.
	 * 
	 * @param leaveReportsForm
	 *            the leave reports form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	void genLeaveTranReportExcelFile(LeaveReportsForm leaveReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Function to generate pdf file for leave transaction report.
	 * 
	 * @param leaveReportsForm
	 *            the leave reports form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	void genLeaveTranReportPDF(LeaveReportsForm leaveReportsForm,
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
			String metaData, Boolean includeResignedEmployees,
			HttpServletRequest request, HttpServletResponse response);

	byte[] genLeaveBalAsOnDayCustReport(LeaveReportsForm leaveReportsForm,
			HttpServletResponse response, HttpServletRequest request);

	String getLeavePreferenceDetail(HttpServletRequest request,
			HttpServletResponse response);

	void genDayWiseLeaveTranReportExcelFile(LeaveReportsForm leaveReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	void genDayWiseLeaveTranReportPDF(LeaveReportsForm leaveReportsForm,
			String[] dataDictionaryIds, HttpServletRequest request,
			HttpServletResponse response);

	String getYearList(HttpServletRequest request, Locale locale);

	String getLeaveReviewerList(HttpServletRequest request);

	void genLeaveHeadcountExcelReport(LeaveReportsForm leaveReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	void genLeaveHeadcountPdfReport(LeaveReportsForm leaveReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String getSwitchCompanyList(String searchCondition, String searchText,
			String groupName, HttpServletRequest request);

	String getOrderedCompanyList(String searchCondition, String searchText,
			String groupName, HttpServletRequest request);

}
