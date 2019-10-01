/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.EmailPreferenceForm;

/**
 * The Interface EmailPreferenceLogic.
 */
@Transactional
public interface EmailPreferenceLogic {

	/**
	 * Save email preference.
	 * 
	 * @param emailPreferenceForm
	 *            the email preference form
	 */
	void saveEmailPreference(EmailPreferenceForm emailPreferenceForm,
			Long companyId);

	/**
	 * Gets the email prefrence.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the email prefrence
	 */
	EmailPreferenceForm getEmailPrefrence(Long companyId);

}
