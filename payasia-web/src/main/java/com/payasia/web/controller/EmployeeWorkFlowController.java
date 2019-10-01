package com.payasia.web.controller;

/**
 * @author vivekjain
 * 
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.EmployeeWorkFlowForm;

/**
 * The Interface EmployeeWorkFlowController.
 */
public interface EmployeeWorkFlowController {

	/**
	 * purpose : Get Employee WorkFlow Data.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @return Employee WorkFlow Data
	 */
	String getEmployeeWorkFlowData(HttpServletRequest request);

	/**
	 * purpose : get Employee WorkFlow Details List.
	 * 
	 * @param String
	 *            the searchString
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return EmployeeWorkFlowForm contains List
	 */
	String getEmployeeWorkFlowDetails(String searchString,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : Save Employee WorkFlow Details.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param EmployeeWorkFlowForm
	 *            the employeeWorkFlowForm
	 * @return String Response
	 */
	String setEmployeeWorkFlowDetails(
			EmployeeWorkFlowForm employeeWorkFlowForm,
			HttpServletRequest request);

	/**
	 * purpose : Employee WorkFlow Delegate Data.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param String
	 *            the columnName
	 * @param String
	 *            the sortingType
	 * @param int the page
	 * @param int the rows
	 * @return EmployeeWorkFlowDelegateResponse Contains List Of Employees of
	 *         workFlow
	 */
	String employeeWorkFlowDelegateData(HttpServletRequest request,
			String columnName, int page, int rows, String sortingType);

	/**
	 * purpose : Delete Employee WorkFlow Delegate.
	 * 
	 * @param Long
	 *            the workFlowDelegateId
	 * @return response
	 */
	String deleteEmployeeWorkFlowDelegate(Long workFlowDelegateId);

	/**
	 * purpose : Edit Employee WorkFlow Details.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param EmployeeWorkFlowForm
	 *            the employeeWorkFlowForm
	 * @param String
	 *            the sortingType
	 * @param int the page
	 * @param int the rows
	 * @return response
	 */
	String editEmployeeWorkFlowDetails(
			EmployeeWorkFlowForm employeeWorkFlowForm,
			HttpServletRequest request);
}
