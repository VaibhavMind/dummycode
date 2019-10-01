package com.payasia.web.controller.claim;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.payasia.common.form.ClaimBatchForm;

public interface ClaimBatchController {

	String editClaimBatch(ClaimBatchForm claimBatchForm, Long claimBatchId,
			HttpServletRequest request);

	String getClaimBatch(String columnName, String sortingType, int page,
			int rows, String claimBatchFilter, String filterText, int year,
			HttpServletRequest request, Locale locale);

	String viewClaimBatchDetail(String columnName, String sortingType,
			int page, int rows, Long claimBatchId, HttpServletRequest request);

	String getClaimBatchData(Long claimBatchId, HttpServletRequest request);

	void deleteClaimBatch(Long claimBatchId, HttpServletRequest request);

	String getClaimBatchConf();

	String addClaimBatch(ClaimBatchForm claimBatchForm, HttpServletRequest request);

}
