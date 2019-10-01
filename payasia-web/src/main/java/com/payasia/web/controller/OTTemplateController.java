package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.OTTemplateForm;

public interface OTTemplateController {

	String configureClaimItem();

	String addCustomField();

	String accessControl(int page, int rows, String columnName,
			String sortingType, String searchCondition, String searchText,
			HttpServletRequest request, HttpServletResponse response);

	String addOTTemplate(String otTemplateName, HttpServletRequest request,
			HttpServletResponse response);

	void deleteOTTemplate(Long otTemplateId, HttpServletRequest request,
			HttpServletResponse response);

	String getOTTemplate(Long otTemplateId, HttpServletRequest request,
			HttpServletResponse response);

	String configureOTTemplate(OTTemplateForm otTemplateForm,
			HttpServletRequest request, HttpServletResponse response);

	String getOTType(Long otTemplateId, HttpServletRequest request,
			HttpServletResponse response);

	void addOTType(String[] otTypeId, Long otTemplateId);

	String viewOTType(int page, int rows, String columnName,
			String sortingType, Long otTemplateId, HttpServletRequest request,
			HttpServletResponse response);

	void editOTType(Long otTemplateTypeId, OTTemplateForm otTemplateForm);

	String getOTTypeForEdit(Long otTemplateTypeId);

	void deleteOTType(Long otTemplateTypeId, Long otTemplateId);
}
