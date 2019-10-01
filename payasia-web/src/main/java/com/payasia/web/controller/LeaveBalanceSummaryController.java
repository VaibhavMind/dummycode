package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.LeaveBalanceSummaryForm;

public interface LeaveBalanceSummaryController {

	String employeeSchemeDetail(String empNumber, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String getEmployeeId(String searchString);

	String getHolidaycalendar(int year);

	String myLeaveSchemeDetail(HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String getLeaveCalMonthList(String year, String month);

	String empLeaveSchemeTypeData(Long empLeaveSchemeTypeId);

	String getPostLeaveSchemeData(String empNumber, String year);

	String getLeaveScheme(String empNumber, String year);

	String getMyLeaveScheme(String year);

	String getCompletedLeaves(String columnName, String sortingType, int page,
			int rows, HttpServletRequest request);

	String getLeaveCalMonthListByManager(String year, String month);

	String getLeaveBalance(Long employeeLeaveSchemeTypeId);

	String myLeaveSchemeTypeList(Integer year, String columnName,
			String sortingType, int page, int rows);

	String getLeaveReviewers(Long leaveApplicationId);

	String getEmployeeIdForManager(String searchString);

	String getEmployeeName(String employeeNumber);

	String savePostTransactionLeaveType(
			LeaveBalanceSummaryForm leaveBalanceSummaryForm,
			Locale locale);

	String deleteLeaveTransaction(Long leaveTranId);

	String getLeavetransactionData(Long leaveTranId);

	String updatePostTransactionLeaveType(
			LeaveBalanceSummaryForm leaveBalanceSummaryForm,
			Locale locale);

	String cancelLeaveTransaction(Long leaveTranId);

	String importPostLeaveTran(LeaveBalanceSummaryForm leaveBalanceSummaryForm,
			Locale locale) throws Exception;

	String getTeamEmployeeLeaveSchemeType(int year, String employeeNumber,
			String searchStringEmpId, String columnName, String sortingType,
			int page, int rows);

	String getEmployeeLeaveSchemeType(int year, String employeeNumber,
			String columnName, String sortingType, int page, int rows);

	String isEncashedVisible();

	String getAppCodeDTOList(Locale locale);

	String empLeaveSchemeTypeHistoryList(Long leaveSchemeTypeId,
			String postLeaveTypeFilterId, int year, String employeeNumber,
			String columnName, String sortingType, int page, int rows,
			Locale locale);

	String getTeamEmpLeaveSchemeTypeHistoryList(Long leaveSchemeTypeId,
			String postLeaveTypeFilterId, int year, String employeeNumber,
			String searchStringEmpId, String columnName, String sortingType,
			int page, int rows, Locale locale);

	String cancelLeave(LeaveBalanceSummaryForm leaveBalanceSummaryForm, Locale locale);

	String getDays(String fromDate, String toDate, Long session1,
			Long session2, Long employeeLeaveSchemeTypeId,
			HttpServletRequest request, Locale locale);

	String getLeaveTransactionHistory(Long leaveApplicationId,
			String transactionType, Locale locale);

	String getEmployeeNameForManager(String employeeNumber);

	byte[] viewAttachment(long attachmentId, HttpServletResponse response);

	String dataImportStatus(String fileName);

	String getDashBoardEmpOnLeaveList(String fromDate, String toDate,
			String columnName, String sortingType, int page, int rows,
			Locale locale);

	String getEmpOnLeaveByDate(String[] leaveAppIds, String columnName,
			String sortingType, int page, int rows, Locale locale);

	String getDashBoardByManagerEmpOnLeaveList(String fromDate, String toDate,
			String columnName, String sortingType, int page, int rows,
			Locale locale);

	String getYearList();

	String getEmpLeaveSchemeInfo();

	String getDefaultEmailCCByEmp(String moduleName, HttpServletRequest request, HttpServletResponse response);

	String getLeaveTypes(Long leaveSchemeId);

	String getLeaveCustomFields(Long leaveSchemeId, Long leaveTypeId, Long employeeLeaveSchemeTypeId);

	String searchEmployee(String columnName, String sortingType, int page, int rows, String empName, String empNumber);

	String getDefaultEmailCCListByEmployee(String moduleName, HttpServletRequest request);

	void saveDefaultEmailCCByEmployee(String ccEmailIds, String moduleName, HttpServletRequest request);

	String getEmployeeLeaveSchemeTypeEmp(int year, String employeeNumber, String columnName, String sortingType,
			int page, int rows);

	String empLeaveSchemeTypeHistoryListEmp(Long leaveSchemeTypeId, String postLeaveTypeFilterId, int year,
			String employeeNumber, String columnName, String sortingType, int page, int rows, Locale locale);

	String savePostTransactionLeaveTypeEmp(LeaveBalanceSummaryForm leaveBalanceSummaryForm, Locale locale);

	String getHolidaycalendarEmp(int year);

	String getEmpOnLeaveByDateEmp(String[] leaveAppIds, String columnName, String sortingType, int page, int rows, Locale locale);

	String getPostLeaveSchemeDataEmp(String empNumber, String year);

	String getLeaveSchemeEmp(String empNumber, String year);

	String getLeaveBalanceEmp(Long employeeLeaveSchemeTypeId);

	String getDaysEmp(String fromDate, String toDate, Long session1, Long session2, Long employeeLeaveSchemeTypeId, Locale locale);

	String isEncashedVisibleEmp();

	String getLeaveTransactionHistoryEmp(Long leaveApplicationId, String transactionType, Locale locale);

	byte[] viewAttachmentEmp(long attachmentId, HttpServletResponse response);

	String getYearListEmp();

}
