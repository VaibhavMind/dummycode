package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.form.PaySlipReleaseForm;

public interface PaySlipReleaseController {

	String viewPayslipReleaseList(String columnName, String sortingType,
			String searchCriteria, String keyword, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String savePaySlipRelease(PaySlipReleaseForm paySlipReleaseForm,
			HttpServletRequest request, HttpServletResponse response);

	String deletePaySlipRelease(Long companyPayslipReleaseId,
			HttpServletRequest request, HttpServletResponse response);

	String getPaySlipReleaseDetails(long companyPayslipReleaseId,
			HttpServletRequest request, HttpServletResponse response);

	String getPaySlipPart(HttpServletRequest request,
			HttpServletResponse response);

	String editPaySlipRelease(PaySlipReleaseForm paySlipReleaseForm,
			long companyPayslipReleaseId, HttpServletRequest request,
			HttpServletResponse response);

	String getPayslipSendMailDetails(HttpServletRequest request,
			HttpServletResponse response);

}
