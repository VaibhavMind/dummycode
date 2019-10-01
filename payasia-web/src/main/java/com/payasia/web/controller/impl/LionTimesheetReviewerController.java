package com.payasia.web.controller.impl;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.LionTimesheetReviewerForm;
import com.payasia.common.form.LundinTimesheetReviewerForm;

public interface LionTimesheetReviewerController {

	String viewLionReviewers(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getLionTypeWorkFlow(HttpServletRequest request,
			HttpServletResponse response);

	String saveLundinReviewer(
			LionTimesheetReviewerForm lionTimesheetReviewerForm,
			ModelMap model, HttpServletRequest request);

	String updateLundinReviewer(
			LundinTimesheetReviewerForm lundinTimesheetReviewerForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	String deleteLundinReviewer(Long employeeId, ModelMap model,
			HttpServletRequest request);

	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String empName, String empNumber,
			HttpServletRequest request, HttpServletResponse response);

	String searchEmployeeBySessionCompany(String columnName,
			String sortingType, int page, int rows, String empName,
			String empNumber, HttpServletRequest request,
			HttpServletResponse response);

	String getCompanyGroupEmployeeId(String searchString,
			HttpServletRequest request, HttpServletResponse response);

	String getEmployeeId(String searchString, HttpServletRequest request,
			HttpServletResponse response);

	String configureLionRevWorkflow(
			LundinTimesheetReviewerForm lundinTimesheetReviewerForm,
			HttpServletRequest request, HttpServletResponse response);

	String getLionWorkFlowLevel(HttpServletRequest request,
			HttpServletResponse response);

	String importLionReviewer(
			LundinTimesheetReviewerForm lundinTimesheetReviewerForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception;

	String getLionReviewerData(Long employeeId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

}
