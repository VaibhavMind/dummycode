package com.payasia.logic;

import java.util.Locale;

import org.springframework.context.MessageSource;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimDetailsReportDTO;
import com.payasia.common.dto.ClaimFormPdfDTO;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimPreferenceForm;
import com.payasia.common.form.ClaimReportsForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface MyClaimLogic {

	String getSubmittedOnDate(Long claimApplicationId);

	ClaimPreferenceForm getClaimPreferences(Long companyId);

	ClaimApplicationItemAttach viewAttachment(AddClaimDTO addClaimDTO);

	AddClaimFormResponse getAllClaims(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO);

	AddClaimForm getClaimApplicationData(AddClaimDTO claimDTO);

	AddClaimFormResponse getPendingClaims(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO);

	AddClaimFormResponse getSubmittedClaims(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO);

	AddClaimFormResponse getApprovedClaims(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO);

	AddClaimFormResponse getRejectedClaims(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO);

	AddClaimFormResponse getWithdrawnClaims(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO);

	AddClaimFormResponse searchClaimTemplateItems(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO, MessageSource messageSource);

	ClaimFormPdfDTO generateClaimFormPrintPDF(AddClaimDTO claimDTO);

	AddClaimForm getClaimAppDataMsgSource(AddClaimDTO claimDTO, MessageSource messageSource);
	
	ClaimDetailsReportDTO showClaimTransactionReport(Long companyId, ClaimReportsForm claimReportsForm, Long employeeId,
			String[] dataDictionaryIds, boolean hasLundinTimesheetModule, Locale locale, Boolean isCheckedFromCreatedDate);

}
