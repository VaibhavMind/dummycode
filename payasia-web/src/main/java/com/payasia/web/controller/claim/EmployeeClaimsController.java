package com.payasia.web.controller.claim;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.ImportEmployeeClaimForm;

public interface EmployeeClaimsController {

	String getEmployeeId(String searchString, HttpServletRequest request, HttpServletResponse response);

	String getPendingClaims(String columnName, String sortingType, String transactionType, String employeeNumber,
			String fromDate, String toDate, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getSubmittedClaims(String columnName, String sortingType, String transactionType, String employeeNumber,
			String fromDate, String toDate, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getApprovedClaims(String columnName, String sortingType, String transactionType, String employeeNumber,
			String fromDate, String toDate, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getRejectedClaims(String columnName, String sortingType, String transactionType, String employeeNumber,
			String fromDate, String toDate, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getWithdrawnClaims(String columnName, String sortingType, String transactionType, String employeeNumber,
			String fromDate, String toDate, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getAllClaims(String columnName, String sortingType, String transactionType, String employeeNumber,
			String fromDate, String toDate, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	String rejectClaim(AddClaimForm addClaimForm, HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String isPayAsiaAdminCanApprove(HttpServletRequest request);

	String getEmployeeName(String employeeNumber, HttpServletResponse response, HttpServletRequest request);

	String importEmployeeClaim(ImportEmployeeClaimForm importEmployeeClaimForm, HttpServletRequest request,
			HttpServletResponse response, Locale locale) throws Exception;

	byte[] printClaimApplicationForm(Long claimApplicationId, HttpServletResponse response, HttpServletRequest request);

	String acceptClaim(AddClaimForm addClaimForm, HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String isAdminCanEditClaimBeforeApproval(Long claimApplicationId, HttpServletRequest request);

	String getAdminClaimApplicationData(Long claimApplicationId, String employeeNumber, BindingResult result,
			ModelMap model, HttpServletRequest request, Locale locale);

	String searchClaimTemplateItems(String columnName, String sortingType, Long claimApplicationId, int page, int rows,
			HttpServletRequest request, HttpServletResponse response, HttpSession session, Locale locale);

}
