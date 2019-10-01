package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.OTReviewerForm;

public interface OTReviewerController {

	String getOTReviewers(Long otTemplateId);

	String saveOTReviewer(OTReviewerForm otReviewerForm, BindingResult result,
			ModelMap model, HttpServletRequest request);

	String viewClaimReviewers(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getOTReviewerData(Long employeeId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String updateOTReviewer(OTReviewerForm otReviewerForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	String deleteOTReviewer(Long employeeId, BindingResult result,
			ModelMap model, HttpServletRequest request);

}
