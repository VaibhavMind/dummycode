package com.payasia.logic;

import java.util.List;
import java.util.Locale;

import com.payasia.common.dto.ClaimFormPdfDTO;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.ImportEmployeeClaimForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimApplicationReviewer;
import com.payasia.dao.bean.EmployeeClaimReviewer;

public interface EmployeeClaimsLogic {

	List<EmployeeListForm> getEmployeeId(Long companyId, String searchString, Long employeeId);

	AddClaimFormResponse getPendingClaims(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String transactionType, String employeeNumber, Long companyId);

	AddClaimFormResponse getApprovedClaims(String fromDate, String toDate, Long sessionEmpId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String transactionType, String employeeNumber,
			Long companyId);

	AddClaimFormResponse getRejectedClaims(String fromDate, String toDate, Long sessionEmpId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String transactionType, String employeeNumber,
			Long companyId);

	AddClaimFormResponse getWithdrawnClaims(String fromDate, String toDate, Long sessionEmpId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String transactionType, String employeeNumber,
			Long companyId);

	AddClaimFormResponse getSubmittedClaims(String fromDate, String toDate, Long sessionEmpId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String transactionType, String employeeNumber,
			Long companyId);

	AddClaimFormResponse getAllClaims(String fromDate, String toDate, Long sessionEmpId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String transactionType, String employeeNumber,
			Long companyId, Locale locale);

	AddClaimForm acceptClaim(AddClaimForm addClaimForm, Long employeeId);

	AddClaimForm rejectClaim(AddClaimForm addClaimForm, Long employeeId);

	void getRejectedClaimReviewers(List<ClaimApplicationReviewer> claimApplicationReviewers, AddClaimForm addClaimForm,
			String pageContextPath, ClaimApplication claimApplication);

	void getApprovedClaimReviewers(List<ClaimApplicationReviewer> claimApplicationReviewers, AddClaimForm addClaimForm,
			String pageContextPath, ClaimApplication claimApplication);

	void getSubmittedClaimReviewers(List<ClaimApplicationReviewer> claimApplicationReviewers, AddClaimForm addClaimForm,
			String pageContextPath, ClaimApplication claimApplication);

	void getPendingClaimReviewers(List<EmployeeClaimReviewer> empApplicationReviewers, AddClaimForm addClaimForm);

	void getWithdrawClaimReviewers(List<ClaimApplicationReviewer> claimApplicationReviewers, AddClaimForm addClaimForm);

	String isPayAsiaAdminCanApprove(Long companyId);

	String getEmployeeName(String employeeNumber, Long companyId);

	ImportEmployeeClaimForm importEmployeeClaim(ImportEmployeeClaimForm importEmployeeClaimForm, Long companyId,
			Long employeeId, boolean hasLundinTimesheetModule);

	ClaimFormPdfDTO generateClaimFormPrintPDF(Long companyId, Long employeeId, Long claimApplicationId,
			boolean hasLundinTimesheetModule);

	String isAdminCanEditClaimBeforeApproval(Long claimApplicationId, Long companyId);

	Long getEmployeeId(Long companyId, String employeeNumber);


}
