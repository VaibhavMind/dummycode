package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.EmployeePreferencesForm;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface EmployeePreferencesController.
 */
public interface EmployeePreferencesController {
	/**
	 * purpose : Save Employee Preferences.
	 * 
	 * @param EmployeePreferencesForm
	 *            the employeePreferencesForm
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	void employeePreferencesSave(
			EmployeePreferencesForm employeePreferencesForm,
			HttpServletRequest request, HttpServletResponse response);

}