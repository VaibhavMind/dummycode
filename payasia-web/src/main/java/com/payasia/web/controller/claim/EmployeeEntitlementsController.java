package com.payasia.web.controller.claim;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.EmployeeClaimAdjustmentForm;

public interface EmployeeEntitlementsController {

	void exportAdjustmentsExcelFile(EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm, HttpServletRequest request,
			HttpServletResponse response);

	String getEmployeeEntitlements(String employeeNumber, Long claimTemplateId, Integer year, String columnName,
			String sortingType, int page, int rows, Locale locale);

	String updateClaimAdjustment(EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm);

	String saveClaimAdjustment(EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm);

	String deleteClaimAdjustment(Long employeeClaimAdjustmentId);

}
