/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.WorkFlowDelegateForm;

/**
 * The Interface WorkFlowDelegateController.
 */
public interface WorkFlowDelegateController {

	/**
	 * Save work flow delegate.
	 * 
	 * @param workFlowDelegateForm
	 *            the work flow delegate form
	 * @param result
	 *            the result
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 */
	void saveWorkFlowDelegate(WorkFlowDelegateForm workFlowDelegateForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	/**
	 * Gets the work flow delegate data.
	 * 
	 * @param configId
	 *            the config id
	 * @return the work flow delegate data
	 */
	String getWorkFlowDelegateData(long workflowDelegateId,
			HttpServletRequest request);

	/**
	 * purpose : search Employee for workFlow Delegatee.
	 * 
	 * @param String
	 *            the columnName
	 * @param String
	 *            the sortingType
	 * @param int the page
	 * @param int the rows
	 * @param String
	 *            the empName
	 * @param String
	 *            the empNumber
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return WorkFlowDelegateResponse contains All Employee list
	 */
	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String empName, String empNumber,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : view WorkFlow Delegate List on search criteria.
	 * 
	 * @param String
	 *            the columnName
	 * @param String
	 *            the sortingType
	 * @param int the page
	 * @param int the rows
	 * @param String
	 *            the criteria
	 * @param String
	 *            the keyword
	 * @param String
	 *            the workFlowType
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return WorkFlowDelegateResponse contains All Employee list
	 */
	String viewWorkFlowDelegate(String columnName, String sortingType,
			int page, int rows, String criteria, String keyword,
			String workFlowType, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : update WorkFlow Delegate.
	 * 
	 * @param WorkFlowDelegateForm
	 *            the workFlowDelegateForm
	 * @param long the workflowDelegateId
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	void updateWorkFlowDelegate(WorkFlowDelegateForm workFlowDelegateForm,
			long workflowDelegateId, HttpServletRequest request);

	String viewEmployeeWorkFlowDelegate(String columnName, String sortingType,
			int page, int rows, String criteria, String keyword,
			String workFlowType, HttpServletRequest request,
			HttpServletResponse response);

	void deleteWorkFlowDelegate(long workflowDelegateId,
			HttpServletRequest request);

	String searchGroupEmployee(String columnName, String sortingType, int page,
			int rows, String empName, String empNumber,
			HttpServletRequest request, HttpServletResponse response);

	String getEmployeeId(String searchString, HttpServletRequest request,
			HttpServletResponse response);

	String getCompanyGroupEmployeeId(String searchString,
			HttpServletRequest request, HttpServletResponse response);

	String searchWorkflowEmployee(String columnName, String sortingType,
			int page, int rows, String empName, String empNumber,
			HttpServletRequest request, HttpServletResponse response);

}