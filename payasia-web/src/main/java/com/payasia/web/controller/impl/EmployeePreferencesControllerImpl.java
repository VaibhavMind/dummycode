package com.payasia.web.controller.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.EmployeePreferencesForm;
import com.payasia.logic.EmployeePreferencesLogic;
import com.payasia.web.controller.EmployeePreferencesController;
import com.payasia.web.util.PayAsiaSessionAttributes;

/**
 * The Class EmployeePreferencesControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/admin/employeePreferences")
public class EmployeePreferencesControllerImpl implements
		EmployeePreferencesController {

	/** The employee preferences logic. */
	@Resource
	EmployeePreferencesLogic employeePreferencesLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmployeePreferencesController#
	 * employeePreferencesSave(com.payasia.common.form.EmployeePreferencesForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/employeePreferencesSave.html", method = RequestMethod.POST)
	public @ResponseBody
	void employeePreferencesSave(
			@ModelAttribute("employeePreferencesForm") EmployeePreferencesForm employeePreferencesForm,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		employeePreferencesLogic.employeePreferencesSave(
				employeePreferencesForm, companyId);
	}

}