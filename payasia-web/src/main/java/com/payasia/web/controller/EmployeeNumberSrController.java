package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.EmployeeNumberSrForm;

/**
 * @author vivekjain
 *
 */
/**
 * The Interface EmployeeNumberSrController.
 */
public interface EmployeeNumberSrController {

	/**
	 * purpose : View Employee Number Series List.
	 * 
	 * @param int the page
	 * @param int the rows
	 * @param String
	 *            the columnName
	 * @param String
	 *            the sortingType
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return EmployeeNumberSrFormResponse contains Employee Number Series List
	 */
	String viewEmpNoSr(int page, int rows, String columnName,
			String sortingType, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : Update Employee Number Series of Employee.
	 * 
	 * @param EmployeeNumberSrForm
	 *            the employeeNumberSrForm
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	String editSeries(EmployeeNumberSrForm employeeNumberSrForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	/**
	 * purpose : Delete Employee Number Series of Employee.
	 * 
	 * @param long the empNoSeriesId
	 */
	String deleteSeries(long empNoSeriesId, Locale locale);

	/**
	 * purpose : Get Employee Number Series of Employee for Edit.
	 * 
	 * @param long the empNoSeriesId
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return EmployeeNumberSrForm
	 */
	String getDataForSeries(long empNoSeriesId);

	/**
	 * purpose : Save Employee Number Series of Employee.
	 * 
	 * @param EmployeeNumberSrForm
	 *            the employeeNumberSrForm
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @param Locale
	 *            the locale
	 * @return response
	 */
	String saveNewSeries(EmployeeNumberSrForm employeeNumberSrForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

}