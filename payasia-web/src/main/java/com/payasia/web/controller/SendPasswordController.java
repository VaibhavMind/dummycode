package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface SendPasswordController.
 */
public interface SendPasswordController {

	/**
	 * purpose : filter or search Employee.
	 * 
	 * @param String
	 *            the columnName
	 * @param String
	 *            the sortingType
	 * @param String
	 *            the searchCondition
	 * @param String
	 *            the searchText
	 * @param int the page
	 * @param int the rows
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return SendPasswordResponse contains Employees list
	 */
	String filterEmployeeList(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : send Email with Password to employees.
	 * 
	 * @param String
	 *            [] the employeeId
	 * @param Locale
	 *            the locale
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return Response
	 */
	String sendPwdEmail(String[] employeeId, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

}
