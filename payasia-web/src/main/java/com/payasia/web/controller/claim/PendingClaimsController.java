package com.payasia.web.controller.claim;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.ClaimApplicationItemWorkflowForm;
import com.payasia.common.form.PendingClaimsForm;

public interface PendingClaimsController {

	byte[] printClaimApplicationForm(Long claimApplicationReviewerId, HttpServletResponse response,
			HttpServletRequest request);

	byte[] viewAttachment(long claimApplicationItemAttachmentId, HttpServletResponse response,
			HttpServletRequest request);

	String forwardLeave(PendingClaimsForm pendingClaimsForm, HttpServletRequest request, Locale locale);

	String acceptClaim(PendingClaimsForm pendingClaimsForm, HttpServletRequest request, Locale locale);

	String rejectClaim(PendingClaimsForm pendingClaimsForm, HttpServletRequest request);

	String getPendingClaims(String columnName, String sortingType, String searchCondition, String searchText, int page,
			int rows, HttpServletRequest request);

	String saveRejectItemInfo(ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm,
			HttpServletRequest request);

	String getDataForClaimReview(Long claimApplicationreviewerId, HttpServletRequest request, Locale locale);

	String saveOverrideItemInfo(ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm,
			HttpServletRequest request, Locale locale);

}
