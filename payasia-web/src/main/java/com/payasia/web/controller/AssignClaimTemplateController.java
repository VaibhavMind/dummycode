package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.AssignClaimTemplateForm;

public interface AssignClaimTemplateController {

	String deleteAssignClaimTemplate(Long employeeClaimTemplateId);

	String claimTemplateList();

	String assignClaimTemplate(AssignClaimTemplateForm assignClaimTemplateForm);

	String searchAssignClaimTemplate(String columnName, String sortingType, int page, int rows, String searchCondition,
			String searchText, String fromDate, String toDate);

	String getEmployeeClaimTemplateData(Long employeeClaimTempateId);

	String updateAssignClaimTemplate(AssignClaimTemplateForm assignClaimTemplateForm, Locale locale);

	String importAssignClaimTemplate(AssignClaimTemplateForm assignClaimTemplateForm, Locale locale);

	void getImportAssignCLaimTemplateFile(String fileName, HttpServletRequest request, HttpServletResponse response);

	String searchEmployee(String columnName, String sortingType, int page, int rows, String empName, String empNumber);
}
