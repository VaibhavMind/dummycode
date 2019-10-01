package com.payasia.web.controller.claim;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.ClaimTemplateForm;
import com.payasia.common.form.ClaimTemplateItemForm;

public interface ClaimTemplateController {

	void addClaimType(String[] claimTypeId, Long claimTemplateId);

	void editClaimType(Long claimTemplateTypeId, ClaimTemplateForm claimTemplateForm);

	String getClaimItemCategories(HttpServletRequest request, HttpServletResponse response);

	String getAppCodeListForProration(HttpServletRequest request, HttpServletResponse response, Locale locale);

	String getPrefrenceFlag();

	String accessControl(int page, int rows, String columnName, String sortingType, String searchCondition,
			String searchText, Locale locale);

	String getAppCodeListForClaimTemplateList(Long claimTemplateId);

	String addClaimTemplate(ClaimTemplateForm claimTemplateForm);

	String saveClaimTemplateItemConf(ClaimTemplateItemForm claimTemplateItemForm);

	String updateClaimTemplateItemConf(ClaimTemplateItemForm claimTemplateItemForm);

	String deleteClaimTemplate(Long claimTemplateId);

	String getClaimTemplate(Long claimTemplateId);

	String getResignedEmployees(String columnName, String sortingType, int page, int rows, String fromDate,
			String toDate);

	String adjustClaimResignedEmp(String employeeIds);

	String getClaimTemplateIdConfData(Long claimTemplateItemId);

	String configureClaimTemplate(ClaimTemplateForm claimTemplateForm);

	String updateClaimTemplateItemWorkflow(ClaimTemplateForm claimTemplateForm);

	String getClaimType(Long claimTemplateId, Long itemCategoryId);

	String getClaimTypeForEdit(Long claimTemplateTypeId);

	String viewClaimType(int page, int rows, String columnName, String sortingType, Long claimTemplateId,
			Locale locale);

	String getClaimTemplateItemWorkFlow(Long claimTemplateItemId, HttpServletRequest request,
			HttpServletResponse response);

	void deleteFilter(Long filterId);

	String getClaimTemplateItemShortlist(Long claimTemplateItemId);

	String saveEmployeeFilterList(String metaData, Long claimTemplateItemId);

	String deleteClaimType(Long claimTemplateItemId);
}
