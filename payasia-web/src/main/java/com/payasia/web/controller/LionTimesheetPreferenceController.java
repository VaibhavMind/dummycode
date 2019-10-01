package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.LionTimesheetPreferenceForm;
import com.payasia.common.form.LundinTimesheetPreferenceForm;

public interface LionTimesheetPreferenceController {

	String getLundinTimesheetPreference(HttpServletRequest request,
			HttpServletResponse response);

	String saveLionTimesheetPreference(
			LundinTimesheetPreferenceForm lundinTimesheetPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	/*
	 * String getEmployeeFilterList(HttpServletRequest request,
	 * HttpServletResponse response, Locale locale);
	 */

	String saveLionTimesheetPreference(
			LionTimesheetPreferenceForm lionTimesheetPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	String lionTimesheetAppCodeMasterData(HttpServletRequest request,
			HttpServletResponse response);

}