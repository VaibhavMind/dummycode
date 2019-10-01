/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.EmailPreferenceForm;

/**
 * The Interface EmailPreferenceController.
 */
public interface EmailPreferenceController {

	/**
	 * Save email preferences.
	 * 
	 * @param emailPreferenceForm
	 *            the email preference form
	 * @param result
	 *            the result
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 */
	void saveEmailPreference(EmailPreferenceForm emailPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	/**
	 * Gets the email preference.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the email preference
	 */
	String getEmailPreference(HttpServletRequest request,
			HttpServletResponse response);

}
