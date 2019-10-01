/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.PasswordPolicyPreferenceForm;

/**
 * The Interface PasswordPolicyPreferenceController.
 */
public interface PasswordPolicyPreferenceController {

	/**
	 * Gets the security question list.
	 * 
	 * @return the security question list
	 */
	String getSecurityQuestionList(String sortingType, String columnName,
			int page, int rows, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Adds the security question list.
	 * 
	 * @param question
	 *            the question
	 * @param companyId
	 *            the company id
	 */
	void addSecurityQuestion(String question, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Delete security question.
	 * 
	 * @param pwdSecurityQuestionId
	 *            the pwd security question id
	 */
	void deleteSecurityQuestion(Long pwdSecurityQuestionId,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * Gets the security question details.
	 * 
	 * @param pwdSecurityQuestionId
	 *            the pwd security question id
	 * @return the security question details
	 */
	String getSecurityQuestionDetails(Long pwdSecurityQuestionId,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * Update security question list.
	 * 
	 * @param question
	 *            the question
	 * @param companyId
	 *            the company id
	 */
	void updateSecurityQuestion(String question, Long questionId,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * Gets the password policy.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the password policy
	 */
	String getPasswordPolicy(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Save password policy.
	 * 
	 * @param passwordPolicyPreferenceForm
	 *            the password policy preference form
	 * @param result
	 *            the result
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @return
	 */
	String savePasswordPolicy(
			PasswordPolicyPreferenceForm passwordPolicyPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

}
