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

import com.payasia.common.form.HRISReviewerForm;

public interface HRISReviewerController {

	String viewHRISReviewers(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getEmployeeId(String searchString, HttpServletRequest request,
			HttpServletResponse response);

	String getCompanyGroupEmployeeId(String searchString,
			HttpServletRequest request, HttpServletResponse response);

	String searchEmployeeBySessionCompany(String columnName,
			String sortingType, int page, int rows, String empName,
			String empNumber, HttpServletRequest request,
			HttpServletResponse response);

	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String empName, String empNumber,
			HttpServletRequest request, HttpServletResponse response);

	String getHRISTypeWorkFlow(HttpServletRequest request,
			HttpServletResponse response);

	String updateHRISReviewer(HRISReviewerForm hrisReviewerForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	String deleteHRISReviewer(Long employeeHRISReviewerId, ModelMap model,
			HttpServletRequest request);

	String saveHRISReviewer(HRISReviewerForm hrisReviewerForm, ModelMap model,
			HttpServletRequest request);

	String configureHRISRevWorkflow(HRISReviewerForm hrisReviewerForm,
			HttpServletRequest request, HttpServletResponse response);

	String getHRISWorkFlowLevel(HttpServletRequest request,
			HttpServletResponse response);

	String getHRISReviewerData(Long employeeId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String importHRISReviewer(HRISReviewerForm hrisReviewerForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception;

}
