package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface EmployeeChangePasswordController.
 */
public interface EmployeeChangePasswordController {

	/**
	 * purpose : Get Password Policy for Change Password.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return PasswordPolicyPreferenceForm
	 */
	String getPasswordPolicy(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : Change the Password for Logged in employee.
	 * 
	 * @param String
	 *            the oldPassword
	 * @param String
	 *            the newPassword
	 * @param HttpServletRequest
	 *            the request
	 * @param Locale
	 *            the locale
	 * @return response
	 */
	String changePassword(String oldPassword, String newPassword,
			HttpServletRequest request, Locale locale);

	/**
	 * Emp change password after login.
	 * 
	 * @param oldPassword
	 *            the old password
	 * @param newPassword
	 *            the new password
	 * @param request
	 *            the request
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String empChangePasswordAfterLogin(String oldPassword, String newPassword,
			HttpServletRequest request, Locale locale);

}
