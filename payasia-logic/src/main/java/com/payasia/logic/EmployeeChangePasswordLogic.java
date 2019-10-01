package com.payasia.logic;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.MessageDTO;
import com.payasia.common.form.ChangePasswordForm;

/**
 * The Interface EmployeeChangePasswordLogic.
 *
 * @author vivekjain
 */
/**
 * The Interface EmployeeChangePasswordLogic.
 */
@Transactional
public interface EmployeeChangePasswordLogic {
	/**
	 * purpose : Change the Password for Logged in employee.
	 * 
	 * @param employeeId
	 * @param companyId
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	MessageDTO changePassword(Long employeeId, Long companyId,
			String oldPassword, String newPassword);

	/**
	 * Changed password for first time login .
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param oldPassword
	 *            the old password
	 * @param newPassword
	 *            the new password
	 * @param string
	 * @return the string
	 */

	MessageDTO changePwdForFirstTimeLogin(Long employeeId, Long companyId,
			String oldPassword, String newPassword, String ipAddrs);

	ChangePasswordForm getPassWordPolicyDetails(Long companyId);

	/**
	 * Reset Forgot password
	 * 
	 * @param username
	 *            the username
	 * @param oldPassword
	 *            the old password
	 * @param employeeCompany
	 *            the new password
	 * @param string
	 * @return the string
	 */
	MessageDTO resetForgotPassword(String username, String newPassword,
			String employeeToken, Long employeeCompany, String remoteAddr);

}
