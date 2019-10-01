/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.payasia.common.form.EmailPreferenceForm;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.logic.EmailPreferenceLogic;

/**
 * The Class EmailPreferenceLogicImpl.
 */
@Component
public class EmailPreferenceLogicImpl implements EmailPreferenceLogic {

	@Resource
	EmailPreferenceMasterDAO emailPreferenceMasterDAO;

	@Resource
	CompanyDAO companyDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.logic.EmailPreferenceLogic#saveEmailPreference
	 * (com.mind.payasia.common.form.EmailPreferenceForm)
	 */
	@Override
	public void saveEmailPreference(EmailPreferenceForm emailPreferenceForm,
			Long companyId) {
		EmailPreferenceMaster emailPreferenceMaster = new EmailPreferenceMaster();

		Company company = companyDAO.findById(companyId);
		emailPreferenceMaster.setCompany(company);

		emailPreferenceMaster.setSystemEmail(emailPreferenceForm
				.getSystemEmail());
		emailPreferenceMaster.setContactEmail(emailPreferenceForm
				.getContactEmail());
		emailPreferenceMaster.setLogoLocation(emailPreferenceForm
				.getLogoLocation());
		emailPreferenceMaster
				.setSystemEmailForSendingEmails(emailPreferenceForm
						.getSystemSendingEmail());
		emailPreferenceMaster
				.setCompanyUrl(emailPreferenceForm.getCompanyURL());
		EmailPreferenceMaster emailPreferenceMasterData = emailPreferenceMasterDAO
				.findByConditionCompany(companyId);
		if (emailPreferenceMasterData == null) {
			emailPreferenceMasterDAO.save(emailPreferenceMaster);
		} else {
			emailPreferenceMaster.setEmailPrefId(emailPreferenceMasterData
					.getEmailPrefId());
			emailPreferenceMasterDAO.update(emailPreferenceMaster);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.logic.EmailPreferenceLogic#getEmailPrefrence(int)
	 */
	@Override
	public EmailPreferenceForm getEmailPrefrence(Long companyId) {

		EmailPreferenceForm emailPreferenceForm = new EmailPreferenceForm();
		EmailPreferenceMaster emailPreferenceMaster = emailPreferenceMasterDAO
				.findByConditionCompany(companyId);

		if (emailPreferenceMaster != null) {
			emailPreferenceForm.setContactEmail(emailPreferenceMaster
					.getContactEmail());
			emailPreferenceForm.setSystemEmail(emailPreferenceMaster
					.getSystemEmail());
			emailPreferenceForm.setLogoLocation(emailPreferenceMaster
					.getLogoLocation());
			emailPreferenceForm.setSystemSendingEmail(emailPreferenceMaster
					.getSystemEmailForSendingEmails());
			emailPreferenceForm.setCompanyURL(emailPreferenceMaster
					.getCompanyUrl());

		}
		return emailPreferenceForm;
	}

}
