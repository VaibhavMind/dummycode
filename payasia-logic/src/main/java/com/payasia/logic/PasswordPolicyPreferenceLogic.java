/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PasswordPolicyPreferenceForm;
import com.payasia.common.form.PasswordSecurityQuestionForm;
import com.payasia.common.form.PasswordSecurityQuestionResponse;
import com.payasia.common.form.SortCondition;

/**
 * The Interface PasswordPolicyPreferenceLogic.
 */
@Transactional
public interface PasswordPolicyPreferenceLogic {

	/**
	 * Gets the security question list.
	 * 
	 * @return the security question list
	 */
	PasswordSecurityQuestionResponse getSecurityQuestionList(Long companyId,
			PageRequest pageDTO, SortCondition sortDTO);

	/**
	 * Adds the security question list.
	 * 
	 * @param question
	 *            the question
	 * @param companyId
	 *            the company id
	 */
	void addSecurityQuestion(String question, Long companyId);

	/**
	 * Delete security question.
	 * 
	 * @param pwdSecurityQuestionId
	 *            the pwd security question id
	 */
	void deleteSecurityQuestion(Long pwdSecurityQuestionId,Long companyId);

	/**
	 * Gets the security question.
	 * 
	 * @param pwdSecurityQuestionId
	 *            the pwd security question id
	 * @return the security question
	 */
	PasswordSecurityQuestionForm getSecurityQuestion(Long companyId,
			Long pwdSecurityQuestionId);

	/**
	 * Update security question.
	 * 
	 * @param question
	 *            the question
	 * @param companyId
	 *            the company id
	 */
	void updateSecurityQuestion(String question, Long questionId, Long companyId);

	/**
	 * Gets the password policy.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the password policy
	 */
	PasswordPolicyPreferenceForm getPasswordPolicy(Long companyId);

	/**
	 * Save password policy.
	 * 
	 * @param passwordPolicyPreferenceForm
	 *            the password policy preference form
	 */
	void savePasswordPolicy(
			PasswordPolicyPreferenceForm passwordPolicyPreferenceForm,
			Long companyId);
}
