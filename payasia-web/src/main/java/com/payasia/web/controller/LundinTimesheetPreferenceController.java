package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.LundinTimesheetPreferenceForm;

public interface LundinTimesheetPreferenceController {

	String getLundinTimesheetPreference(HttpServletRequest request,
			HttpServletResponse response);

	String saveLundinTimesheetPreference(
			LundinTimesheetPreferenceForm lundinTimesheetPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	String getEmployeeFilterList(HttpServletRequest request,
			HttpServletResponse response, Locale locale);

}
