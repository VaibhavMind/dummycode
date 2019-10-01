package com.payasia.api.claim;

import org.springframework.http.ResponseEntity;

import com.payasia.common.form.ClaimReportsForm;

public interface EmployeeClaimReportApi {
	
	ResponseEntity<?> getClaimReportType();
	
	ResponseEntity<?> getAllClaimTemplate();
	
	ResponseEntity<?> getClaimItemListForClaimDetailsReport(Long[] claimTemplateIdArr);

	ResponseEntity<?> showClaimTransactionReport(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			Boolean isCheckedFromCreatedDate);

	ResponseEntity<?> genEmployeeClaimTranasactionReport(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			Boolean isCheckedFromCreatedDate, String fileType);

	
	
	
	/*ResponseEntity<?> getAllClaimCategory(HttpServletRequest request);
	
	ResponseEntity<?> getAllClaimTemplateWithTemplateCapping(HttpServletRequest request);

	ResponseEntity<?> getAllClaimTemplateWithClaimItemCapping(HttpServletRequest request);
	
	ResponseEntity<?> getClaimCategoryList(HttpServletRequest request);

	ResponseEntity<?> getClaimItemList(Long claimCategoryId, HttpServletRequest request);
	
	ResponseEntity<?> getClaimItemListByTemplateId(Long claimTemplateId, HttpServletRequest request);

	
	ResponseEntity<?> getClaimBatchList(HttpServletRequest request);

	String showClaimReviewerReport(ClaimReportsForm claimReportsForm, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	void genClaimReviewerReportExcelFile(ClaimReportsForm claimReportsForm, HttpServletRequest request,
			HttpServletResponse response);

	void genClaimReviewerReportPDFFile(ClaimReportsForm claimReportsForm, HttpServletRequest request,
			HttpServletResponse response);

	String searchEmployee(String columnName, String sortingType, int page, int rows, String searchCondition,
			String searchText, boolean isShortList, String metaData, HttpServletRequest request,
			HttpServletResponse response);

	String getAdvanceFilterComboHashmap(HttpServletRequest request, HttpServletResponse response);	
	

	String showClaimDetailsReport(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale);

	void genClaimDetailsReportExcelFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale);

	void genClaimDetailsReportPDFFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale);

	String showBatchWiseClaimDetailsReport(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale);

	void genBatchWiseClaimDetailsReportExcelFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	void genBatchWiseClaimDetailsReportPDFFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	void genEmpWiseConsClaimReportExcelFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	void genEmpWiseConsClaimReportPDFFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	String showEmpWiseConsClaimReport(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale);

	String showEmpWiseConsClaimReportII(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale);

	void genEmpWiseConsClaimReportIIExcelFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	void genEmpWiseConsClaimReportIIPDFFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	String showEmpWiseTemplateClaimReport(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale);

	void genEmpWiseTemplateClaimReportExcelFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	void genEmpWiseTemplateClaimReportPDFFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	String showMonthlyConsFinanceReport(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale);

	void genMonthlyConsFinanceReportExcelFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	void genMonthlyConsFinanceReportPDFFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	
	void genClaimHeadcountPdfReport(ClaimReportsForm claimReportsForm, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	void genClaimHeadcountExcelReport(ClaimReportsForm claimReportsForm, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	
	void genCategoryWiseClaimDetailsReportExcelFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	void genCategoryWiseClaimDetailsReportPDFFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	void genPaidClaimDetailsReportPDFFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	void genPaidClaimDetailsReportExcelFile(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response);

	String showPaidClaimDetailsReport(ClaimReportsForm claimReportsForm, String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale);

	String getCompanyGroupEmployeeId(String searchString);*/

}
