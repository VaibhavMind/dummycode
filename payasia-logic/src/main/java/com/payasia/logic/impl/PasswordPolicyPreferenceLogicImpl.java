/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PasswordPolicyPreferenceForm;
import com.payasia.common.form.PasswordSecurityQuestionForm;
import com.payasia.common.form.PasswordSecurityQuestionResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.PasswordPolicyConfigMasterDAO;
import com.payasia.dao.PasswordSecurityQuestionMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.PasswordPolicyConfigMaster;
import com.payasia.dao.bean.PasswordSecurityQuestionMaster;
import com.payasia.logic.PasswordPolicyPreferenceLogic;

/**
 * The Class PasswordPolicyPreferenceLogicImpl.
 */
@Component
public class PasswordPolicyPreferenceLogicImpl implements
		PasswordPolicyPreferenceLogic {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(PasswordPolicyPreferenceLogicImpl.class);

	/** The password security question master dao. */
	@Resource
	PasswordSecurityQuestionMasterDAO passwordSecurityQuestionMasterDAO;

	/** The password policy config master dao. */
	@Resource
	PasswordPolicyConfigMasterDAO passwordPolicyConfigMasterDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.logic.PasswordPolicyPreferenceLogic#getPasswordPolicy
	 * (java.lang.String)
	 */
	@Override
	public PasswordPolicyPreferenceForm getPasswordPolicy(Long companyId) {

		PasswordPolicyPreferenceForm passwordPolicyPreferenceForm = new PasswordPolicyPreferenceForm();

		PasswordPolicyConfigMaster passwordPolicyConfigMaster = passwordPolicyConfigMasterDAO
				.findByConditionCompany(companyId);

		if (passwordPolicyConfigMaster != null) {

			passwordPolicyPreferenceForm
					.setPasswordPolicyEnabled(passwordPolicyConfigMaster
							.isEnablePwdPolicy());

			if (passwordPolicyConfigMaster.isEnablePwdPolicy()) {

				passwordPolicyPreferenceForm
						.setPasswordPolicyConfigId(passwordPolicyConfigMaster
								.getPwdPolicyConfigId());
				passwordPolicyPreferenceForm
						.setExpiryReminder(passwordPolicyConfigMaster
								.getExpiryReminderDays());
				passwordPolicyPreferenceForm
						.setInvalidAttemptsAllowed(passwordPolicyConfigMaster
								.getAllowedInvalidAttempts());
				passwordPolicyPreferenceForm
						.setMaxExpiry(passwordPolicyConfigMaster
								.getMaxExpiryDaysLimit());
				passwordPolicyPreferenceForm
						.setCanNOtSameAsLastPwd(passwordPolicyConfigMaster
								.getMemoryListSize());
				passwordPolicyPreferenceForm
						.setMinPasswordLength(passwordPolicyConfigMaster
								.getMinPwdLength());
				passwordPolicyPreferenceForm
						.setMaxPasswordLength(passwordPolicyConfigMaster
								.getMaxPwdLength());
				passwordPolicyPreferenceForm
						.setPasswordComplexity(passwordPolicyConfigMaster
								.isEnablePasswordComplexity());

				passwordPolicyPreferenceForm
						.setSpecialCharacters(passwordPolicyConfigMaster
								.isIncludeSpecialCharacter());
				passwordPolicyPreferenceForm
						.setNumericCharacters(passwordPolicyConfigMaster
								.getIncludeNumericCharacter());
				passwordPolicyPreferenceForm
						.setCombinationLowerUpperCase(passwordPolicyConfigMaster
								.getIncludeUpperLowerCase());

			}

			return passwordPolicyPreferenceForm;
		}

		return passwordPolicyPreferenceForm;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind.payasia.common.logic.PasswordPolicyPreferenceLogic#
	 * addSecurityQuestionList(java.lang.String, int)
	 */
	@Override
	public void addSecurityQuestion(String question, Long companyId) {
		PasswordSecurityQuestionMaster passwordSecurityQuestionMaster = new PasswordSecurityQuestionMaster();

		Company company = companyDAO.findById(companyId);
		passwordSecurityQuestionMaster.setCompany(company);

		passwordSecurityQuestionMaster.setSecretQuestion(question);

		passwordSecurityQuestionMasterDAO.save(passwordSecurityQuestionMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind.payasia.common.logic.PasswordPolicyPreferenceLogic#
	 * deleteSecurityQuestion(int)
	 */
	@Override
	public void deleteSecurityQuestion(Long pwdSecurityQuestionId,Long companyId) {
		PasswordSecurityQuestionMaster passwordSecurityQuestionMaster = passwordSecurityQuestionMasterDAO.getQuestionForEdit(pwdSecurityQuestionId,companyId);
		if(passwordSecurityQuestionMaster!=null){
		   passwordSecurityQuestionMasterDAO.delete(passwordSecurityQuestionMaster);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind.payasia.common.logic.PasswordPolicyPreferenceLogic#
	 * getSecurityQuestion(int)
	 */
	@Override
	public PasswordSecurityQuestionForm getSecurityQuestion(Long companyId,
			Long pwdSecurityQuestionId) {
		PasswordSecurityQuestionForm passwordSecurityQuestionForm = new PasswordSecurityQuestionForm();

		PasswordSecurityQuestionMaster passwordSecurityQuestionMaster = passwordSecurityQuestionMasterDAO
				.getQuestionForEdit(companyId, pwdSecurityQuestionId);
		if (passwordSecurityQuestionMaster != null) {
			passwordSecurityQuestionForm
					.setSecurityQuestion(passwordSecurityQuestionMaster
							.getSecretQuestion());
			return passwordSecurityQuestionForm;
		}

		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind.payasia.common.logic.PasswordPolicyPreferenceLogic#
	 * updateSecurityQuestion(java.lang.String, int)
	 */
	@Override
	public void updateSecurityQuestion(String question, Long questionId,
			Long companyId) {
		PasswordSecurityQuestionMaster passwordSecurityQuestionMaster = new PasswordSecurityQuestionMaster();

		Company company = companyDAO.findById(companyId);
		passwordSecurityQuestionMaster.setCompany(company);

		passwordSecurityQuestionMaster.setPwdSecurityQuestionId(questionId);
		passwordSecurityQuestionMaster.setSecretQuestion(question);

		passwordSecurityQuestionMasterDAO
				.update(passwordSecurityQuestionMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind.payasia.common.logic.PasswordPolicyPreferenceLogic#
	 * getSecurityQuestionList()
	 */
	@Override
	public PasswordSecurityQuestionResponse getSecurityQuestionList(
			Long companyId, PageRequest pageDTO, SortCondition sortDTO) {

		List<PasswordSecurityQuestionForm> PasswordSecurityQuestionFormList = new ArrayList<PasswordSecurityQuestionForm>();
		List<PasswordSecurityQuestionMaster> passwordSecurityQuestionMasterList = passwordSecurityQuestionMasterDAO
				.findByConditionCompany(companyId, pageDTO, sortDTO);
		for (PasswordSecurityQuestionMaster passwordSecurityQuestionMaster : passwordSecurityQuestionMasterList) {
			PasswordSecurityQuestionForm passwordSecurityQuestionForm = new PasswordSecurityQuestionForm();

			passwordSecurityQuestionForm
					.setPwdSecurityQuestionId(passwordSecurityQuestionMaster
							.getPwdSecurityQuestionId());
			passwordSecurityQuestionForm
					.setSecurityQuestion(passwordSecurityQuestionMaster
							.getSecretQuestion());
			PasswordSecurityQuestionFormList.add(passwordSecurityQuestionForm);
		}
		int recordSize = passwordSecurityQuestionMasterDAO
				.getCountForConditionCompany(companyId);
		PasswordSecurityQuestionResponse response = new PasswordSecurityQuestionResponse();
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);

		}
		response.setRecords(recordSize);
		response.setRows(PasswordSecurityQuestionFormList);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind.payasia.common.logic.PasswordPolicyPreferenceLogic#
	 * savePasswordPolicy
	 * (com.mind.payasia.common.form.PasswordPolicyPreferenceForm)
	 */
	@Override
	public void savePasswordPolicy(
			PasswordPolicyPreferenceForm passwordPolicyPreferenceForm,
			Long companyId) {
		PasswordPolicyConfigMaster passwordPolicyConfigMaster = new PasswordPolicyConfigMaster();

		Company company = companyDAO.findById(companyId);
		passwordPolicyConfigMaster.setCompany(company);

		passwordPolicyConfigMaster
				.setEnablePwdPolicy(passwordPolicyPreferenceForm
						.isPasswordPolicyEnabled());

		if (passwordPolicyPreferenceForm.isPasswordPolicyEnabled()) {

			if (passwordPolicyPreferenceForm.getInvalidAttemptsAllowed() != null) {
				passwordPolicyConfigMaster
						.setAllowedInvalidAttempts(passwordPolicyPreferenceForm
								.getInvalidAttemptsAllowed());
			}

			if (passwordPolicyPreferenceForm.getCanNOtSameAsLastPwd() != null) {
				passwordPolicyConfigMaster
						.setMemoryListSize(passwordPolicyPreferenceForm
								.getCanNOtSameAsLastPwd());
			}

			passwordPolicyConfigMaster
					.setIncludeSpecialCharacter(passwordPolicyPreferenceForm
							.isSpecialCharacters());
			passwordPolicyConfigMaster
					.setIncludeNumericCharacter(passwordPolicyPreferenceForm
							.isNumericCharacters());
			passwordPolicyConfigMaster
					.setEnablePasswordComplexity(passwordPolicyPreferenceForm
							.isPasswordComplexity());
			passwordPolicyConfigMaster
					.setIncludeUpperLowerCase(passwordPolicyPreferenceForm
							.isCombinationLowerUpperCase());

			if (passwordPolicyPreferenceForm.getExpiryReminder() != null) {
				passwordPolicyConfigMaster
						.setExpiryReminderDays(passwordPolicyPreferenceForm
								.getExpiryReminder());
			}

			if (passwordPolicyPreferenceForm.getMaxExpiry() != null) {
				passwordPolicyConfigMaster
						.setMaxExpiryDaysLimit(passwordPolicyPreferenceForm
								.getMaxExpiry());
			}

			if (passwordPolicyPreferenceForm.getMinPasswordLength() != null) {
				passwordPolicyConfigMaster
						.setMinPwdLength(passwordPolicyPreferenceForm
								.getMinPasswordLength());
			}
			if (passwordPolicyPreferenceForm.getMaxPasswordLength() != null) {
				passwordPolicyConfigMaster
						.setMaxPwdLength(passwordPolicyPreferenceForm
								.getMaxPasswordLength());
			}

		}
		PasswordPolicyConfigMaster passwordPolicyConfigMasterData = passwordPolicyConfigMasterDAO
				.findByConditionCompany(companyId);
		if (passwordPolicyConfigMasterData == null) {
			passwordPolicyConfigMasterDAO.save(passwordPolicyConfigMaster);
		} else {
			passwordPolicyConfigMaster
					.setPwdPolicyConfigId(passwordPolicyConfigMasterData
							.getPwdPolicyConfigId());
			passwordPolicyConfigMasterDAO.update(passwordPolicyConfigMaster);
		}

	}

}
