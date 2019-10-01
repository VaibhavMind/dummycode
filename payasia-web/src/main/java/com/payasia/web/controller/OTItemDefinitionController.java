package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.OTItemDefinitionForm;

public interface OTItemDefinitionController {
	String viewClaimItems(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String updateOTItem(OTItemDefinitionForm otItemDefinitionForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response, Long otItemId);

	String saveOTItem(OTItemDefinitionForm otItemDefinitionForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	String deleteOTItem(Long otItemId);
}
