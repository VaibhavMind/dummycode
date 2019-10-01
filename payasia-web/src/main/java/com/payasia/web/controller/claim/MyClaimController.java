package com.payasia.web.controller.claim;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.ClaimReportsForm;

public interface MyClaimController {

	byte[] viewAttachment(long claimApplicationItemAttachmentId, HttpServletResponse response,
			HttpServletRequest request);

	String getSubmittedClaims(String columnName, String sortingType, String fromDate, String toDate,
			String searchCondition, String searchText, int page, int rows, HttpServletRequest request);

	String getApprovedClaims(String columnName, String sortingType, String fromDate, String toDate,
			String searchCondition, String searchText, int page, int rows, HttpServletRequest request);

	String getRejectedClaims(String columnName, String sortingType, String fromDate, String toDate,
			String searchCondition, String searchText, int page, int rows, HttpServletRequest request);

	String getWithdrawnClaims(String columnName, String sortingType, String fromDate, String toDate,
			String searchCondition, String searchText, int page, int rows, HttpServletRequest request);

	String getAllClaims(String columnName, String sortingType, String searchCondition, String searchText,
			String transactionType, String fromDate, String toDate, int page, int rows, HttpServletRequest request,
			Locale locale);

	String searchClaimTemplateItems(String columnName, String sortingType, Long claimApplicationId, int page, int rows,
			HttpServletRequest request, Locale locale);

	String getClaimPreferences(HttpServletRequest request);

	byte[] printClaimApplicationFormEmp(Long claimApplicationId, HttpServletResponse response,
			HttpServletRequest request);

	String getClaimApplicationData(Long claimApplicationId, HttpServletRequest request, Locale locale);

	String getPendingClaims(String columnName, String sortingType, String fromDate, String toDate,
			String searchCondition, String searchText, int page, int rows, Locale locale);
	

	void genEmployeeClaimTranasactionReportPdfFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			Boolean isCheckedFromCreatedDate, HttpServletRequest request, HttpServletResponse response, Locale locale);

	String showClaimTransactionReport(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			Boolean isCheckedFromCreatedDate, HttpServletRequest request, HttpServletResponse response, Locale locale);

	void genEmployeeClaimTranasactionReportExcelFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			Boolean isCheckedFromCreatedDate, HttpServletRequest request, HttpServletResponse response, Locale locale);
}
