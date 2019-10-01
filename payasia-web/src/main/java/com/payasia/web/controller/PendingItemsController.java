package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.PendingItemsForm;

public interface PendingItemsController {

	String viewLeaveTransactions(String columnName, String sortingType,
			Long createdById, Long leaveApplicationId, String startDate,
			String endDate, int page, int rows);

	String acceptLeave(PendingItemsForm pendingItemsForm, Locale locale);

	String getEmployeesOnLeaves(String columnName, String sortingType,
			String fromDate, String toDate, Long leaveApplicationId, int page,
			int rows);

	String getPendingLeaves(String columnName, String sortingType,
			String workflowType, String searchCondition, String searchText,
			int page, int rows, Locale locale);

	String getPendingLeavesForAdmin(String columnName, String sortingType,
			String workflowType, String searchCondition, String searchText,
			int page, int rows, Locale locale);

	String getDataForLeaveReview(Long reviewId, Locale locale);

	String rejectLeave(PendingItemsForm pendingItemsForm, Locale locale);

	String rejectLeaveForAdmin(PendingItemsForm pendingItemsForm, Locale locale);

	String forwardLeave(PendingItemsForm pendingItemsForm, Locale locale);

	String acceptLeaveforAdmin(PendingItemsForm pendingItemsForm, Locale locale);

	byte[] printLeaveApplicationForm(Long leaveApplicationId,
			HttpServletResponse response);

	String viewMultipleLeaveApplications(String[] leaveApplicationIds,
			Locale locale);

	String showEmpLeaveWorkflowStatus(Long leaveApplicationId,
			Locale locale);

	String reviewMultipleLeaveApp(PendingItemsForm pendingItemsForm,
			 Locale locale);

	String reviewMultipleLeaveAppByAdmin(PendingItemsForm pendingItemsFrm,
			 Locale locale);

	String getDataForLeaveReviewEmp(Long reviewId, Locale locale);

	byte[] viewAttachmentRev(long attachmentId, HttpServletResponse response);

	String searchEmployee(String columnName, String sortingType, int page, int rows, String empName, String empNumber);

	String getEmployeesOnLeavesEmp(String columnName, String sortingType, String fromDate, String toDate,
			Long leaveApplicationId, int page, int rows);

	String viewLeaveTransactionsEmp(String columnName, String sortingType, Long createdById, Long leaveApplicationId,
			String startDate, String endDate, int page, int rows);

	String forwardLeaveEmp(PendingItemsForm pendingItemsForm, Locale locale);

	byte[] printLeaveApplicationFormEmp(Long leaveApplicationReviewerId, HttpServletResponse response);

	String viewMultipleLeaveApplicationsEmp(String[] leaveApplicationRevIds, Locale locale);

	String showEmpLeaveWorkflowStatusEmp(Long leaveApplicationId, Locale locale);
}
