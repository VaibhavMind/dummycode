package com.payasia.web.controller.claim;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.ClaimReviewerForm;

public interface ClaimReviewController {

	String getClaimReviewers(Long claimTemplateId);

	String saveClaimReviewer(ClaimReviewerForm claimReviewerForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	String deleteClaimReviewer(Long[] employeeIds, HttpServletRequest request);

	String viewClaimReviewers(String columnName, String sortingType, String searchCondition, String searchText,
			int page, int rows);

	String getClaimReviewerData(Long[] filterIds);

	String updateClaimReviewer(ClaimReviewerForm claimReviewerForm);

	String searchEmployee(String columnName, String sortingType, int page, int rows, String empName, String empNumber);

	String getEmployeeClaimTemplates(Long employeeId);

	String importClaimReviewer(ClaimReviewerForm claimReviewerForm) throws Exception;

	String searchEmployeeBySessionCompany(String columnName, String sortingType, int page, int rows, String empName,
			String empNumber);

}
