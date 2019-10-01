package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.OTBatchForm;

public interface OTBatchController {

	String saveOTBatch(OTBatchForm oTBatchForm, BindingResult result,
			ModelMap model, HttpServletRequest request);

	String viewOTBatch(String columnName, String sortingType,
			String searchCriteria, String keyword, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String updateOTBatch(OTBatchForm oTBatchForm, BindingResult result,
			ModelMap model, HttpServletRequest request);

	String deleteOTBatch(Long otBatchId);

}