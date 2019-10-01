/**
 * @author vivekjain
 *
 */
package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.LundinTimesheetReviewerForm;

public interface LundinTimesheetReviewerController {

	String viewLundinReviewers(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getLundinTypeWorkFlow(HttpServletRequest request,
			HttpServletResponse response);

	String getLundinReviewerData(Long employeeId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String saveLundinReviewer(
			LundinTimesheetReviewerForm lundinTimesheetReviewerForm,
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

	String configureLundinRevWorkflow(
			LundinTimesheetReviewerForm lundinTimesheetReviewerForm,
			HttpServletRequest request, HttpServletResponse response);

	String getLundinWorkFlowLevel(HttpServletRequest request,
			HttpServletResponse response);

	String importLundinReviewer(
			LundinTimesheetReviewerForm lundinTimesheetReviewerForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception;

}
