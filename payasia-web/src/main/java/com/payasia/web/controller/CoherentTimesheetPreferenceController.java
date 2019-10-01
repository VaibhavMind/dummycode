package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.CoherentTimesheetPreferenceForm;

public interface CoherentTimesheetPreferenceController {

	String getCoherentTimesheetPreference(HttpServletRequest request,
			HttpServletResponse response);

	String saveCoherentTimesheetPreference(
			CoherentTimesheetPreferenceForm coherentTimesheetPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String empName, String empNumber,
			HttpServletRequest request, HttpServletResponse response);

	String saveCoherentOTEmployee(Long employeeId, HttpServletRequest request);

	String searchCoherentSelectOTEmployees(String searchCondition,
			String searchText, HttpServletRequest request);

	String deleteCoherentOTEmployee(String employeeID,
			HttpServletRequest request);

}
