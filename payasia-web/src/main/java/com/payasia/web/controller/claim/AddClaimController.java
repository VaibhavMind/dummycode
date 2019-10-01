package com.payasia.web.controller.claim;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimApplicationItemForm;

public interface AddClaimController {

	String getClaimTemplateItems(Long claimTemplateId, HttpServletRequest request);

	String getClaimTemplateItemData(Long employeeClaimTemplateItemId, Long claimApplicationId,
			Long claimApplicationItemId, HttpServletRequest request, Locale locale);

	String persistClaimApplication(AddClaimForm addClaimForm, HttpServletRequest request, Locale locale);

	String saveAsDraftFromWithdrawClaim(AddClaimForm addClaimForm, HttpServletRequest request, Locale locale);

	String copyClaimApplication(AddClaimForm addClaimForm, HttpServletRequest request, Locale locale);

	String persistAdminClaimApplication(AddClaimForm addClaimForm, HttpServletRequest request, Locale locale);

	String saveClaimApplication(Long claimTemplateId, HttpServletRequest request, Locale locale);

	String saveAdminClaimApplication(Long claimTemplateId, String employeeNumber, HttpServletRequest request,
			Locale locale);

	String saveClaimTemplateItem(ClaimApplicationItemForm claimApplicationItemForm, HttpServletRequest request,
			Locale locale);

	String updateClaimTemplateItem(ClaimApplicationItemForm claimApplicationItemForm, HttpServletRequest request,
			Locale locale);

	String deleteClaimTemplateItem(Long claimTemplateItemId, HttpServletRequest request);

	String deleteClaimApplicationAttachement(Long attachementId, HttpServletRequest request);

	String deleteClaimApplication(Long claimApplicationId, HttpServletRequest request);

	String withdrawClaim(Long claimApplicationId, HttpServletRequest request);

	String getEmployeeItemData(Long claimTemplateItemId, HttpServletRequest request);

	String uploadClaimItemAttachement(ClaimApplicationItemAttach claimApplicationItemAttach, Long claimAppItemId,
			HttpServletRequest request);

	String getForexRate(String currencyDate, Long currencyId, HttpServletRequest request);

	String getEmployeeClaimTemplates(String employeeNumber, HttpServletRequest request);

	String getLoggedinEmpTemplates(HttpServletRequest request);

	String deleteApprovedClaimApplication(Long attachementId, HttpServletRequest request);

	String lundinBlockList(String claimItemAccountCode, HttpServletRequest request);

	String lundinAFEList(HttpServletRequest request, Long blockId, String claimItemAccountCode);

	String getClaimApplicationItemList(Long claimApplicationId, HttpServletRequest request);

	String getAdminClaimTemplateItems(Long claimTemplateId, String employeeNumber);

	byte[] viewAttachment(long attachmentId, HttpServletResponse response);
}
