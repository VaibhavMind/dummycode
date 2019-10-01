package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.payasia.common.form.AddLeaveForm;

public interface AddLeaveController {

	String getLeaveTypes(Long leaveSchemeId);

	byte[] viewAttachment(long attachmentId, HttpServletResponse response);

	//void deleteAttachment(long attachmentId);

	void deleteLeave(long leaveApplicationId);

	String getDataForPendingLeave(Long leaveApplicationId);

	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String empName, String empNumber);

	String getLeaveCustomFields(Long leaveSchemeId, Long leaveTypeId,
			Long employeeLeaveSchemeId);

	String getLeaveBalance(Long employeeLeaveSchemeTypeId,
			HttpServletRequest request);

	String getPendingLeaves(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows);

	String getSubmittedLeaves(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request
			);

	String getSubmittedCancelledLeaves(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request
			);

	String getCompletedLeaves(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request
			);

	String getCompletedCancelLeaves(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request
			);

	String getRejectedLeaves(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request
			);

	String getWithDrawnLeaves(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request);

	ModelAndView applyLeave(AddLeaveForm addLeaveForm,
			ModelMap model,Locale locale);

	String getViewLeaveCustomFields(Long leaveSchemeId, Long leaveTypeId,
			Long employeeLeaveSchemeId, HttpServletRequest request);

	String getRejectedCancelLeaves(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request);

	String getWithDrawnCancelLeaves(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request);

	String viewLeave(Long leaveApplicationId, Locale locale);

	ModelAndView addLeave(AddLeaveForm addLeaveForm, BindingResult result,
			ModelMap model, HttpServletRequest request, Locale locale);

	ModelAndView editLeave(AddLeaveForm addLeaveForm, BindingResult result,
			ModelMap model, HttpServletRequest request, Locale locale);

	String withdrawLeave(Long leaveApplicationId, Locale locale);

	String getDays(String fromDate, String toDate, Long session1,
			Long session2, Long employeeLeaveSchemeTypeId,
			HttpServletRequest request, Locale locale);

	byte[] printLeaveApplicationForm(Long leaveApplicationId,
			HttpServletResponse response);

	String getEmpLeaveSchemeInfo(HttpServletRequest request);

	String getLeaveCustomFieldsForAdmin(Long leaveSchemeId, Long leaveTypeId,
			Long employeeLeaveSchemeId);

	ModelAndView addLeaveByAdmin(AddLeaveForm addLeaveForm,
			ModelMap model, Locale locale);
			
    ModelAndView extensionLeave(AddLeaveForm addLeaveForm,ModelMap model,Locale locale);

	String extensionLeave(Long leaveApplicationId, Locale locale);
	void deleteAttachment(long attachmentId);

	String extensionLeaveEmp(Long leaveApplicationId);

	byte[] viewAttachmentEmp(long attachmentId, HttpServletResponse response);

	String getLeaveBalanceEmp(Long employeeLeaveSchemeTypeId);

	String searchEmployeeEmp(String columnName, String sortingType, int page, int rows, String empName,
			String empNumber);

	String getLeaveTypesEmp(Long leaveSchemeId);

	String getDaysEmp(String fromDate, String toDate, Long session1, Long session2, Long employeeLeaveSchemeTypeId,
			Locale locale);
}
