package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;

import com.payasia.common.form.CompanyGroupForm;

public interface CompanyGroupController {

	String addCompanyGroup(CompanyGroupForm companyGroupForm,
			HttpServletRequest request);

	String getCompanyGroup(Long groupId);

	String updateCompanyGroup(CompanyGroupForm companyGroupForm,
			HttpServletRequest request);

	String deleteCompanyGroup(Long groupId);

	String getCompanyGroupList();

	String viewGroups(String columnName, String sortingType, int page,
			int rows, HttpServletRequest request);

}
