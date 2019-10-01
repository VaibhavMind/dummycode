/**
 * @author vivekjain
 * 
 */
package com.payasia.web.controller;

import java.util.Locale;

import com.payasia.common.form.AssignLeaveSchemeForm;
import com.payasia.common.form.EmployeeLeaveDistributionForm;

/**
 * The Interface assignLeaveSchemeController.
 */

public interface AssignLeaveSchemeController {

	/**
	 * Search assign leave scheme.
	 * 
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @param searchCondition
	 *            the search condition
	 * @param searchText
	 *            the search text
	 * @param empLeaveSchemeStatus
	 *            the emp leave scheme status
	 * @param request
	 *            the request
	 * @return the string
	 */
	String searchAssignLeaveScheme(String columnName, String sortingType,
			int page, int rows, String searchCondition, String searchText,
			String fromDate, String toDate);

	/**
	 * Search employee.
	 * 
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @param empName
	 *            the emp name
	 * @param empNumber
	 *            the emp number
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String empName, String empNumber);

	/**
	 * Adds the emp leave scheme.
	 * 
	 * @param assignLeaveSchemeForm
	 *            the assign leave scheme form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String addEmpLeaveScheme(AssignLeaveSchemeForm assignLeaveSchemeForm);

	/**
	 * Edits the emp leave scheme.
	 * 
	 * @param assignLeaveSchemeForm
	 *            the assign leave scheme form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String editEmpLeaveScheme(AssignLeaveSchemeForm assignLeaveSchemeForm,
			Locale locale);

	/**
	 * Delete emp leave scheme.
	 * 
	 * @param empLeaveSchemeId
	 *            the emp leave scheme id
	 * @return
	 */
	String deleteEmpLeaveScheme(Long empLeaveSchemeId);

	/**
	 * Gets the emp leave scheme for edit.
	 * 
	 * @param empLeaveSchemeId
	 *            the emp leave scheme id
	 * @return the emp leave scheme for edit
	 */
	String getEmpLeaveSchemeForEdit(Long empLeaveSchemeId);

	/**
	 * Gets the leave scheme list.
	 * 
	 * @param request
	 *            the request
	 * @return the leave scheme list
	 */
	String getLeaveSchemeList();

	/**
	 * Edits the emp leave distribution.
	 * 
	 * @param employeeLeaveDistributionForm
	 *            the employee leave distribution form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String editEmpLeaveDistribution(
			EmployeeLeaveDistributionForm employeeLeaveDistributionForm);

	/**
	 * Import assign leave scheme.
	 * 
	 * @param assignLeaveSchemeForm
	 *            the assign leave scheme form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */

	String importAssignLeaveScheme(AssignLeaveSchemeForm assignLeaveSchemeForm,
			Locale locale) throws Exception;

	String getEmployeeId(String searchString);

	String viewEmployeeleaveDistribution(Long empLeaveSchemeId, Integer year,
			String columnName, String sortingType, int page, int rows);

	String getEmployeeName(String employeeNumber);

	String importLeaveDistribution(AssignLeaveSchemeForm assignLeaveSchemeForm,
			Locale locale) throws Exception;

	String searchEmployeeEmp(String columnName, String sortingType, int page, int rows, String empName,
			String empNumber);

	String getEmployeeIdEmp(String searchString);

}
