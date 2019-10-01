package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.form.EmployeeContactUSForm;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface EmployeeContactUSController.
 */
public interface EmployeeContactUSController {
	/**
	 * purpose : get Contact email id.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return contact email id
	 */
	String getContactEmail(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : Send Mail by Logged In employee.
	 * 
	 * @param EmployeeContactUSForm
	 *            the employeeContactUSForm
	 * @param HttpServletResponse
	 *            the response
	 * @return
	 */

	String sendMail(EmployeeContactUSForm employeeContactUSForm,
			HttpServletRequest request, HttpSession session, Locale locale);

}
