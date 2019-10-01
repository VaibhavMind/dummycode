package com.payasia.web.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.payasia.common.form.ForgotPasswordForm;

/**
 * The Interface LoginController.
 *
 * @author vivekjain
 */
/**
 * The Interface LoginController.
 */
public interface LoginController {

	/**
	 * purpose : For login Request.
	 * 
	 * @param session
	 *            the session
	 * @param response
	 *            the response
	 * @param request
	 *            the request
	 * @return Redirect to Home page
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	ModelAndView loginRequest(HttpSession session,
			HttpServletResponse response, HttpServletRequest request)
			throws IOException;

	/**
	 * purpose : For logout Request.
	 * 
	 * @param session
	 *            the session
	 * @param response
	 *            the response
	 * @param request
	 *            the request
	 * @return Redirect to Login Page
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	ModelAndView logoutRequest(HttpSession session,
			HttpServletResponse response, HttpServletRequest request)
			throws IOException;

	/**
	 * purpose : For Login Request.
	 * 
	 * @param companyCode
	 *            the company code
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @return Redirect to Login Page
	 */
	ModelAndView loginRequest(String companyCode, ModelMap model,
			HttpServletRequest request);

	/**
	 * purpose : For Session Expired Page.
	 * 
	 * @param companyCode
	 *            the company code
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return Redirect to Login Page
	 */
	ModelAndView sessionExpired(String companyCode, ModelMap model,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : get Login Page Logo.
	 * 
	 * @param companyCode
	 *            the company code
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return Image Logo with byte Array
	 */
	void getLoginPageLogo(String companyCode, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : get Company Logo image Height and Width Size.
	 * 
	 * @param companyCode
	 *            the company code
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return Image size
	 */

	String getCompanyLogoSize(String companyCode, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Checks if is ajax session is valid. If invalid, it returns error code
	 * 403.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param reqSessId
	 *            the req sess id
	 * @return the string
	 */
	String isAjaxSessionValid(HttpServletRequest request,
			HttpServletResponse response, String reqSessId);

	/**
	 * Gets the contact email.
	 * 
	 * @param companyCode
	 *            the company code
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the contact email
	 */
	String getContactEmail(String companyCode, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Sets the window id.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param windowId
	 *            the window id
	 * @return the string
	 */
	String setWindowId(HttpServletRequest request,
			HttpServletResponse response, String windowId);

	/**
	 * Employee page request.
	 * 
	 * @param request
	 *            the request
	 * @param session
	 *            the session
	 * @param companyCode
	 *            the company code
	 * @param response
	 *            the response
	 * @return the string
	 */
	String employeePageRequest(HttpServletRequest request, HttpSession session,
			String companyCode, HttpServletResponse response);

	ModelAndView resetpasswordRequest(String token, HttpServletRequest request,
			HttpServletResponse response, ModelMap model, Locale locale);

	/**
	 * Reset Forgot password.
	 * 
	 * @param username
	 *            username
	 * @param newPassword
	 *            the new password
	 * @param request
	 *            the request
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String resetForgotPassword(String username, String newPassword,
			String employeeToken, Long employeeCompany,
			HttpServletRequest request, Locale locale);

	/**
	 * purpose : Forget Password that send mail to Employee with password.
	 * 
	 * @param employeeNum
	 *            the employeeNum
	 * @param companyCode
	 *            the company code
	 * @param locale
	 *            the locale
	 * @return response
	 */

	String forgetPassword(ForgotPasswordForm forgotPasswordForm,
			HttpServletRequest request, HttpSession session, Locale locale);

}
