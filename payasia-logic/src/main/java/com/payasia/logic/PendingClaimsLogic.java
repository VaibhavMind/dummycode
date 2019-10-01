package com.payasia.logic;

import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimFormPdfDTO;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimApplicationItemForm;
import com.payasia.common.form.ClaimApplicationItemWorkflowForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingClaimsForm;
import com.payasia.common.form.PendingClaimsResponseForm;
import com.payasia.common.form.SortCondition;

public interface PendingClaimsLogic {

	PendingClaimsForm getDataForClaimReview(Long reviewId, Long empId, Locale locale, MessageSource messageSource);

	PendingClaimsForm acceptClaim(PendingClaimsForm pendingClaimsForm, Long employeeId, Long companyId);

	PendingClaimsForm rejectClaim(PendingClaimsForm pendingClaimsForm, Long employeeId, Long companyId);

	ClaimFormPdfDTO generateClaimFormPrintPDF(Long companyId, Long employeeId, Long claimApplicationReviewerId, boolean hasLundinTimesheetModule);

	ClaimApplicationItemAttach viewAttachment(AddClaimDTO addClaimDTO);

	PendingClaimsResponseForm getPendingClaims(AddClaimDTO addClaimDTO, PageRequest pageDTO, SortCondition sortDTO);

	PendingClaimsForm forwardClaim(PendingClaimsForm pendingClaimsForm, AddClaimDTO addClaimDTO);

	ClaimApplicationItemForm saveRejectItemInfo(AddClaimDTO addClaimDTO, ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm);

	ClaimApplicationItemForm saveOverrideItemInfo(AddClaimDTO addClaimDTO, ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm);

	Map<String, Object> saveOverrideClaimItemInfo(AddClaimDTO addClaimDTO, ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm);

	AddClaimForm getClaimReviewersData(AddClaimDTO claimDTO);//PendingClaimsForm

	ClaimApplicationItemForm getClaimItemDetail(Long claimItemId, Long empId, Long companyId);

}
