package com.payasia.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface SwitchCompanyController.
 */
public interface SwitchCompanyController {

	/**
	 * purpose : get Company Name.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 */
	String getCompanyName(HttpServletRequest request);

	/**
	 * purpose : Switch Company.
	 * 
	 * @param Long
	 *            the companyId
	 * @param String
	 *            the companyCode
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @param HttpSession
	 *            the session
	 * @return CompanyCode
	 */
	String switchCompany(Long companyId, String companyCode,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) throws IOException;

	/**
	 * Switch role.
	 * 
	 * @param companyCode
	 *            the company code
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	String switchRole(Long companyId, String companyCode,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) throws IOException;

	String getExportCompanies(int page, int rows, String columnName,
			String sortingType, HttpServletRequest request);

	/**
	 * purpose : get Switch Company List.
	 * 
	 * @param String
	 *            the columnName
	 * @param String
	 *            the sortingType
	 * @param int the page
	 * @param int the rows
	 * @param HttpServletRequest
	 *            the request
	 * @return CompanyCode
	 */
	String getSwitchCompanyList(int page, int rows, String columnName,
			String sortingType, String searchCondition, String searchText,
			Boolean includeInactiveCompany, HttpServletRequest request);

}