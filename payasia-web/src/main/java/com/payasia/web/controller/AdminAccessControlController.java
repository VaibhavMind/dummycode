package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface AdminAccessControlController.
 */
public interface AdminAccessControlController {

	/**
	 * purpose : Enable employees to access application.
	 * 
	 * @param String
	 *            [] employeeId
	 */
	void enableEmployee(String[] employeeId);

	/**
	 * purpose : Disable employees to access application.
	 * 
	 * @param String
	 *            [] employeeId
	 */
	void disableEmployee(String[] employeeId);

	/**
	 * purpose : search employees who have enabled or disabled to access
	 * application.
	 * 
	 * @param String
	 *            the columnName
	 * @param String
	 *            the sortingType
	 * @param int the rows
	 * @param int the page
	 * @param String
	 *            searchCondition
	 * @param String
	 *            searchText
	 * @param String
	 *            employeeStatus
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @param HttpSession
	 *            the session
	 * @return CustomAdminAccessControlResponse contains Employee List .
	 */
	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String searchCondition, String searchText,
			String employeeStatus, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

}
