package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.form.LeaveYearEndEmployeeDetailForm;
import com.payasia.common.form.PendingItemsForm;
import com.payasia.common.form.YearEndProcessForm;

public interface YearEndProcessingController {

	String getYEPLeaveTypeList(String columnName, String sortingType, int page,
			int rows, Long leaveSchemeId, HttpServletRequest request,
			HttpServletResponse response);

	String getEmpsPendingLeaves(String columnName, String sortingType,
			Long leaveTypeId, Long leaveSchemeId, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getDataForLeaveReview(Long leaveApplicationId,
			HttpServletRequest request);

	String getLeaveYearEndEmpDetailList(String columnName, String sortingType,
			int page, int rows, Long leaveYearEndBatchId,
			String employeeNumber, HttpServletRequest request,
			HttpServletResponse response);

	String getLeaveType(HttpServletRequest request, HttpServletResponse response);

	String deleteLeaveYearEndBatch(Long leaveYearBatchId,
			HttpServletRequest request, HttpServletResponse response);

	String deleteLeaveYearEndEmpDetail(Long leaveGrantBatchEmpDetailId,
			HttpServletRequest request, HttpServletResponse response);

	String acceptLeave(PendingItemsForm pendingItemsForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String rejectLeave(PendingItemsForm pendingItemsForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String getGroupCompanies(HttpServletRequest request,
			HttpServletResponse response);

	String performYearEndProcessSave(YearEndProcessForm yearEndProcessForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String groupChange(Long groupId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	String companyChange(Long companyId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	String onLeaveTypeChange(Long leaveSchemeId, Long companyId,
			HttpServletRequest request, HttpServletResponse response);

	String getLeaveYearEndBatchList(String columnName, String sortingType,
			int page, int rows, int year, Long leaveTypeId, Long companyId,
			Long groupId, Long leaveSchemeId, HttpServletRequest request,
			HttpServletResponse response);

	String editLeaveYearEndEmpDetail(Long leaveYearEndEmployeeDetailId,
			HttpServletRequest request, HttpServletResponse response);

	String updateEmployeeYearEndSummaryDetail(
			LeaveYearEndEmployeeDetailForm leaveYearEndEmployeeDetailForm,
			HttpServletRequest request, HttpServletResponse response);

}
