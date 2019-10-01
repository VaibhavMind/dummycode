/**
 * @author vivekjain
 *
 */
package com.payasia.web.controller;

import javax.servlet.http.HttpSession;

/**
 * The Interface SwitchUserController.
 */
public interface SwitchUserController {

	/**
	 * purpose : employee Page Request.
	 * 
	 * @param HttpSession
	 *            the session
	 * @param String
	 *            the companyCode
	 * @return Redirect to Admin home
	 */
	String employeePageRequest(HttpSession session, String companyCode);
}