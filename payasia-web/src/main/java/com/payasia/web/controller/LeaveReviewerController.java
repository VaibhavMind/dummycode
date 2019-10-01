package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;

import com.payasia.common.form.LeaveReviewerForm;

public interface LeaveReviewerController {

	String saveLeaveReviewer(LeaveReviewerForm leaveReviewerForm);

	String viewLeaveReviewers(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows);

	String updateLeaveReviewer(LeaveReviewerForm leaveReviewerForm);

	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String empName, String empNumber);

	String getLeaveReviewerData(Long[] filterIds);

	String deleteLeaveReviewer(Long[] filterIds, ModelMap model,
			HttpServletRequest request);

	String getLeaveTypeWorkFlow(Long employeeLeaveSchemeTypeId,
			Long leaveSchemeId);

	String isAllowManagerSelfApproveLeave();

	String getCompanyGroupEmployeeId(String searchString);

	String getLeaveType(Long employeeId, Locale locale);

	String getEmployeeId(String searchString);

	String searchEmployeeBySessionCompany(String columnName,
			String sortingType, int page, int rows, String empName,
			String empNumber);

	String getActiveWithFutureLeaveScheme(Long employeeId,
			Locale locale);

	String getLeaveTypeListForleaveScheme(Long employeeLeaveSchemeId,
			Locale locale);

	String importLeaveReviewer(LeaveReviewerForm leaveReviewerForm,
			Locale locale) throws Exception;

	String searchEmployeeEmp(String columnName, String sortingType, int page, int rows, String empName,
			String empNumber);

}