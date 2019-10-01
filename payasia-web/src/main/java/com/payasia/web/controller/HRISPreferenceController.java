package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.HRISPreferenceForm;

public interface HRISPreferenceController {

	void saveHRISPreferences(HRISPreferenceForm hrisPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	String getHRISPreference(HttpServletRequest request,
			HttpServletResponse response);

	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String empName, String empNumber,
			HttpServletRequest request, HttpServletResponse response);

}
