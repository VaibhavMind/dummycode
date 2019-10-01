package com.payasia.logic;

import com.payasia.common.form.PasswordPolicyPreferenceForm;
import com.payasia.dao.bean.EmployeePasswordChangeHistory;
import com.payasia.dao.bean.PasswordPolicyConfigMaster;

/**
 * The Interface PasswordPolicyLogic.
 */
/**
 * @author vivekjain
 * 
 */
public interface PasswordPolicyLogic {

	/**
	 * Checks is password allowed.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param companyId
	 *            the company id
	 * @param password
	 *            the password
	 * @return the boolean
	 */
	Boolean isPasswordAllowed(Long employeeId, Long companyId, String password);

	/**
	 * Generate encrypted password.
	 * 
	 * @param password
	 *            the password
	 * @param salt
	 *            the salt
	 * @return the string
	 */
	String generateEncryptedPassword(String password, String salt);

	/**
	 * Gets the password policy.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the password policy
	 */
	PasswordPolicyPreferenceForm getPasswordPolicy(Long companyId);

	/**
	 * Checks if is password change required.
	 * 
	 * @param empPasswordChangeHistory
	 *            the emp password change history
	 * @param passwordPolicyConfigMasterVO
	 *            the password policy config master vo
	 * @return the boolean
	 */
	Boolean isPasswordChangeRequired(
			EmployeePasswordChangeHistory empPasswordChangeHistory,
			PasswordPolicyConfigMaster passwordPolicyConfigMasterVO);
}
