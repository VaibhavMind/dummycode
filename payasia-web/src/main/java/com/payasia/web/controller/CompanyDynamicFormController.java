/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.CompanyDynamicForm;

/**
 * The Interface CompanyDynamicFormController.
 */
public interface CompanyDynamicFormController {

	/**
	 * Purpose: Edit and Save a section metadata.
	 * 
	 * @param metaData
	 *            the meta data
	 * @param tabName
	 *            the tab name
	 * @param formId
	 *            the form id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String saveXML(String metaData, String tabName, long formId,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose: To get the Metadata for selected Section.
	 * 
	 * @param formId
	 *            the form id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the xML
	 */
	String getXML(long formId, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Purpose: To extract the DropDown options from imported XLs.
	 * 
	 * @param companyDynamicForm
	 *            the company dynamic form
	 * @param result
	 *            the result
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the options from xl
	 * @throws Exception
	 *             the exception
	 */

	String getOptionsFromXL(CompanyDynamicForm companyDynamicForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	/**
	 * Purpose: To Delete a section.
	 * 
	 * @param formId
	 *            the form id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String deleteForm(long formId, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Purpose: To save the a dummy metadata for the basic tab if no record for
	 * basic tab exist.
	 * 
	 * @param metaData
	 *            the meta data
	 * @param tabName
	 *            the tab name
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String saveDynamicXML(String metaData, String tabName,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose: To get the Section list.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the tab list
	 */
	String getTabList(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose: To extract the code and description from the Imported xls.
	 * 
	 * @param companyDynamicForm
	 *            the company dynamic form
	 * @param result
	 *            the result
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the code desc from xl
	 * @throws Exception
	 *             the exception
	 */
	String getCodeDescFromXL(CompanyDynamicForm companyDynamicForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	/**
	 * Purpose: To check if the String or Numeric type field is eligible for
	 * Property edit.
	 * 
	 * @param formId
	 *            the form id
	 * @param fieldName
	 *            the field name
	 * @param isTable
	 *            the is table
	 * @param tablePosition
	 *            the table position
	 * @param request
	 *            the request
	 * @return the string
	 */
	String checkFieldEdit(Long formId, String fieldName, boolean isTable,
			String tablePosition, HttpServletRequest request);

}
