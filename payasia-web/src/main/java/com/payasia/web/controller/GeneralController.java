package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Interface GeneralController.
 */
public interface GeneralController {

	/**
	 * Generate encrypted password for All employees.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	void generateEncryptedPassword(HttpServletRequest request,
			HttpServletResponse response);

}
