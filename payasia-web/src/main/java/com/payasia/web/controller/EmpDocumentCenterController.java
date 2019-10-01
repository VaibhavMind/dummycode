/**
 * @author ragulapraveen
 *
 */
package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.EmpDocumentCenterForm;

/**
 * The Interface EmpDocumentCenterController.
 */
public interface EmpDocumentCenterController {

	/**
	 * Search document.
	 * 
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param searchCondition
	 *            the search condition
	 * @param searchText
	 *            the search text
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @return the string
	 */
	@ResponseBody
	String searchDocument(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	/**
	 * Download employee document.
	 * 
	 * @param docId
	 *            the doc id
	 * @param response
	 *            the response
	 * @return
	 */
	String downloadEmployeeDocument(long docId, HttpServletResponse response);

	/**
	 * Upload document.
	 * 
	 * @param employeeDocumentCenterForm
	 *            the employee document center form
	 * @param result
	 *            the result
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 */
	void uploadDocument(EmpDocumentCenterForm employeeDocumentCenterForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	/**
	 * Edits the uploaded data.
	 * 
	 * @param docId
	 *            the doc id
	 * @return the string
	 */
	String editUploadedData(long docId);

	/**
	 * Update document.
	 * 
	 * @param employeeDocumentCenterForm
	 *            the employee document center form
	 * @param result
	 *            the result
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @return the string
	 */
	String updateDocument(EmpDocumentCenterForm employeeDocumentCenterForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	/**
	 * Delete company document.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @param docId
	 *            the doc id
	 * @return the string
	 */
	String deleteCompanyDocument(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Long docId);

	/**
	 * Search document employee document center.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param searchCondition
	 *            the search condition
	 * @param searchText
	 *            the search text
	 * @param categoryId
	 *            the category id
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @return the string
	 */
	String searchDocumentEmployeeDocumentCenter(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			String columnName, String sortingType, String searchCondition,
			String searchText, Long categoryId, int page, int rows);

	void viewDocument(long docId, HttpServletResponse response,
			HttpServletRequest request);
}
