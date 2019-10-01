package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.ClaimReportsForm;
import com.payasia.common.form.HRISReportsForm;

public interface HRISReportsController {

	/**
	 * Purpose : Generate Leave Head count Excel Report
	 * 
	 * @param LeaveReportsForm
	 *            the LeaveReportsForm
	 * @param HttpServletRequest
	 *            request
	 * @param HttpServletResponse
	 *            the response
	 * @param locale
	 *            the locale
	 * @return Excel File
	 */
	void genHRISHeadcountExcelReport(HRISReportsForm hrisReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	/**
	 * Purpose : Generate Leave Head count PDF Report
	 * 
	 * @param LeaveReportsForm
	 *            the LeaveReportsForm
	 * @param HttpServletRequest
	 *            request
	 * @param HttpServletResponse
	 *            the response
	 * @param locale
	 *            the locale
	 * @return PDF File
	 */
	void genHRISHeadcountPdfReport(HRISReportsForm hrisReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String getSwitchCompanyList(String searchCondition, String searchText,
			String groupName, Boolean includeInactiveCompany,
			HttpServletRequest request);

	String getEmployeeList(String searchCondition, String searchText,
			String companyName, String fromDate, String toDate,
			HttpServletRequest request);

	void genLoginDetailsPdfReport(ClaimReportsForm claimReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	void genLoginDetailseExcelReport(ClaimReportsForm claimReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	void genRolePrivilegePdfReport(ClaimReportsForm claimReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	void genRolePrivilegeExcelReport(ClaimReportsForm claimReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

}
