package com.payasia.logic.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.payasia.common.exception.PayAsiaPasswordPolicyException;
import com.payasia.common.form.PasswordPolicyPreferenceForm;
import com.payasia.common.util.DateUtils;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeePasswordChangeHistoryDAO;
import com.payasia.dao.PasswordPolicyConfigMasterDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeePasswordChangeHistory;
import com.payasia.dao.bean.PasswordPolicyConfigMaster;
import com.payasia.logic.PasswordPolicyLogic;
import com.payasia.logic.SecurityLogic;

 
/**
 * The Class PasswordPolicyLogicImpl.
 */
@Component
public class PasswordPolicyLogicImpl implements PasswordPolicyLogic {

	/** The password policy config master dao. */
	@Resource
	PasswordPolicyConfigMasterDAO passwordPolicyConfigMasterDAO;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The employee password change history dao. */
	@Resource
	EmployeePasswordChangeHistoryDAO employeePasswordChangeHistoryDAO;

	/** The security logic. */
	@Resource
	SecurityLogic securityLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PasswordPolicyLogic#isPasswordAllowed(java.lang.Long,
	 * java.lang.Long, java.lang.String)
	 */
	@Override
	public Boolean isPasswordAllowed(Long employeeId, Long companyId,
			String password) throws PayAsiaPasswordPolicyException {
		boolean isEnablePwdPolicy = false;
		PasswordPolicyConfigMaster passwordPolicyConfigMasterVO = passwordPolicyConfigMasterDAO
				.findByConditionCompany(companyId);
		if (passwordPolicyConfigMasterVO == null) {
			return true;
		}
		isEnablePwdPolicy = passwordPolicyConfigMasterVO.isEnablePwdPolicy();
		if (!isEnablePwdPolicy) {
			return true;
		}
		isIncludeAlphabetChar(password);
		if (passwordPolicyConfigMasterVO.isEnablePasswordComplexity()) {
			isIncludeSpecialCharacter(password, passwordPolicyConfigMasterVO);
			isIncludeNumericCharacter(password, passwordPolicyConfigMasterVO);
			isIncludeUpperLowerCase(password, passwordPolicyConfigMasterVO);
		}
		canNotBeSameAsLastPwd(employeeId, password,
				passwordPolicyConfigMasterVO);
		minPwdLength(password, passwordPolicyConfigMasterVO);
		maxPwdLength(password, passwordPolicyConfigMasterVO);
		passwordContainsAnyAttribute(password, employeeId);
		return true;

	}

	/**
	 * Checks if is include alphabet char.
	 * 
	 * @param password
	 *            the password
	 */
	private void isIncludeAlphabetChar(String password) {
		boolean found = false;
		String[] args = new String[1];
		Pattern p = Pattern.compile("[a-zA-Z]");
		Matcher matcher = p.matcher(password);
		if (matcher.find()) {
			found = true;
		}
		if (!found) {
			throw new PayAsiaPasswordPolicyException(
					"payasia.change.password.atleast.one.alphabet.char.included.error",
					args);
		}

	}

	/**
	 * Can not be same as last pwd.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param password
	 *            the password
	 * @param passwordPolicyConfigMasterVO
	 *            the password policy config master vo
	 */
	private void canNotBeSameAsLastPwd(Long employeeId, String password,
			PasswordPolicyConfigMaster passwordPolicyConfigMasterVO) {
		boolean status = true;
		String[] args = new String[1];
		int memoryListSize = passwordPolicyConfigMasterVO.getMemoryListSize();
		List<EmployeePasswordChangeHistory> empPwdChangedHistoryVOList = employeePasswordChangeHistoryDAO
				.getPreviousPasswords(employeeId, memoryListSize);
		for (EmployeePasswordChangeHistory passwordChangeHistory : empPwdChangedHistoryVOList) {
			String encrptedPassWord = securityLogic.encrypt(password,
					passwordChangeHistory.getSalt());
			if (encrptedPassWord.equals(passwordChangeHistory
					.getChangedPassword())) {
				status = false;
			}
		}
		if (!status) {
			throw new PayAsiaPasswordPolicyException(
					"payasia.change.password.current.pwd.not.be.same.as.last.pwd.error",
					args);
		}
	}

	/**
	 * Checks if is include special character.
	 * 
	 * @param password
	 *            the password
	 * @param passwordPolicyConfigMasterVO
	 *            the password policy config master vo
	 */
	private void isIncludeSpecialCharacter(String password,
			PasswordPolicyConfigMaster passwordPolicyConfigMasterVO) {
		if (passwordPolicyConfigMasterVO.isIncludeSpecialCharacter()) {

			String[] args = new String[1];
			boolean hasSpecialChar = password.matches("^.*[^a-zA-Z0-9 ].*$");
			if (!hasSpecialChar) {
				throw new PayAsiaPasswordPolicyException(
						"payasia.change.password.special.char.included.error",
						args);
			}
		}

	}

	/**
	 * Checks Min password length.
	 * 
	 * @param password
	 *            the password
	 * @param passwordPolicyConfigMasterVO
	 *            the password policy config master vo
	 */
	private void minPwdLength(String password,
			PasswordPolicyConfigMaster passwordPolicyConfigMasterVO) {
		int minPwdLen = passwordPolicyConfigMasterVO.getMinPwdLength();
		String[] args = new String[1];
		args[0] = String.valueOf(minPwdLen);
		if (minPwdLen != 0) {
			if (password.length() < minPwdLen) {
				throw new PayAsiaPasswordPolicyException(
						"payasia.change.password.minimum.pwd.length.error",
						args);
			}
		}

	}

	/**
	 * Checks Max password length.
	 * 
	 * @param password
	 *            the password
	 * @param passwordPolicyConfigMasterVO
	 *            the password policy config master vo
	 */
	private void maxPwdLength(String password,
			PasswordPolicyConfigMaster passwordPolicyConfigMasterVO) {
		int maxPwdLen = passwordPolicyConfigMasterVO.getMaxPwdLength();
		String[] args = new String[1];
		args[0] = String.valueOf(maxPwdLen);
		if (maxPwdLen != 0) {
			if (password.length() > maxPwdLen) {
				throw new PayAsiaPasswordPolicyException(
						"payasia.change.password.max.pwd.length.error", args);
			}
		}
	}

	/**
	 * Checks if is include numeric character.
	 * 
	 * @param password
	 *            the password
	 * @param passwordPolicyConfigMasterVO
	 *            the password policy config master vo
	 */
	private void isIncludeNumericCharacter(String password,
			PasswordPolicyConfigMaster passwordPolicyConfigMasterVO) {
		if (passwordPolicyConfigMasterVO.getIncludeNumericCharacter() != null
				&& passwordPolicyConfigMasterVO.getIncludeNumericCharacter()) {
			String[] args = new String[1];
			if (passwordPolicyConfigMasterVO.getIncludeNumericCharacter()) {
				if (!password.matches(".*\\d.*")) {
					throw new PayAsiaPasswordPolicyException(
							"payasia.change.password.numeric.char.included.error",
							args);
				}
			}
		}

	}

	/**
	 * Checks if is include upper lower case.
	 * 
	 * @param password
	 *            the password
	 * @param passwordPolicyConfigMasterVO
	 *            the password policy config master vo
	 */
	private void isIncludeUpperLowerCase(String password,
			PasswordPolicyConfigMaster passwordPolicyConfigMasterVO) {
		String[] args = new String[1];
		if (passwordPolicyConfigMasterVO.getIncludeUpperLowerCase() != null
				&& passwordPolicyConfigMasterVO.getIncludeUpperLowerCase()) {
			if (password.equals(password.toLowerCase())
					|| password.equals(password.toUpperCase())) {
				throw new PayAsiaPasswordPolicyException(
						"payasia.change.password.combination.of.lower.and.upper.case.error",
						args);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PasswordPolicyLogic#generateEncryptedPassword(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public String generateEncryptedPassword(String password, String salt) {

		String encrptedPassWord = securityLogic.encrypt(password, salt);
		return encrptedPassWord;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeChangePasswordLogic#getPasswordPolicy(java.
	 * lang.Long)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PasswordPolicyLogic#getPasswordPolicy(java.lang.Long)
	 */
	@Override
	public PasswordPolicyPreferenceForm getPasswordPolicy(Long companyId) {
		PasswordPolicyPreferenceForm passwordPolicyPreferenceForm = new PasswordPolicyPreferenceForm();

		PasswordPolicyConfigMaster passwordPolicyConfigMaster = passwordPolicyConfigMasterDAO
				.findByConditionCompany(companyId);
		if (passwordPolicyConfigMaster != null) {
			passwordPolicyPreferenceForm
					.setMinPasswordLength(passwordPolicyConfigMaster
							.getMinPwdLength());
			passwordPolicyPreferenceForm
					.setSpecialCharacters(passwordPolicyConfigMaster
							.isIncludeSpecialCharacter());

			return passwordPolicyPreferenceForm;
		}
		return passwordPolicyPreferenceForm;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PasswordPolicyLogic#isPasswordChangeRequired(com.payasia
	 * .dao.bean.EmployeePasswordChangeHistory,
	 * com.payasia.dao.bean.PasswordPolicyConfigMaster)
	 */
	@Override
	public Boolean isPasswordChangeRequired(
			EmployeePasswordChangeHistory empPasswordChangeHistory,
			PasswordPolicyConfigMaster passwordPolicyConfigMasterVO) {

		if (passwordPolicyConfigMasterVO != null) {
			if (passwordPolicyConfigMasterVO.getMaxExpiryDaysLimit() != 0) {
				int maxPwdAge = passwordPolicyConfigMasterVO
						.getMaxExpiryDaysLimit();
				if (empPasswordChangeHistory != null && empPasswordChangeHistory
						.getChangeDate()!=null) {
					Date maxPwdAgeDate = DateUtils.timeStampToDate(empPasswordChangeHistory
									.getChangeDate());
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(maxPwdAgeDate);
					cal.add(Calendar.DATE, maxPwdAge);  
					maxPwdAgeDate = cal.getTime();

					Date currentDate = DateUtils.timeStampToDate(DateUtils.getCurrentTimestamp());
					if (currentDate.after(maxPwdAgeDate)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Check if the password contains employee_number,first name,last name
	 * 
	 * @param password
	 * @param employeeId
	 * @return true/false
	 */
	private boolean passwordContainsAnyAttribute(String password,
			long employeeId) {
		Employee employee = employeeDAO.findById(employeeId);

		if (password.toLowerCase().contains(
				employee.getEmployeeNumber().toLowerCase())) {
			throw new PayAsiaPasswordPolicyException(
					"payasia.change.password.contain.attribute.error");
		}

		StringTokenizer stringTokenizer = new StringTokenizer(
				employee.getFirstName());
		while (stringTokenizer.hasMoreElements()) {
			if (password.toLowerCase().contains(
					stringTokenizer.nextElement().toString().toLowerCase())) {
				throw new PayAsiaPasswordPolicyException(
						"payasia.change.password.contain.attribute.error");
			}
		}

		if (employee.getLastName() != null && !employee.getLastName().isEmpty()) {
			stringTokenizer = new StringTokenizer(employee.getLastName());
			while (stringTokenizer.hasMoreElements()) {
				if (password.toLowerCase().contains(
						stringTokenizer.nextElement().toString().toLowerCase())) {
					throw new PayAsiaPasswordPolicyException(
							"payasia.change.password.contain.attribute.error");
				}
			}
		}
		return true;
	}

}
