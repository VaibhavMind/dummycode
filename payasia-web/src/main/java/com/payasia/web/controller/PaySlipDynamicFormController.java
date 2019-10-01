/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.PaySlipDynamicForm;

 
/**
 * The Interface PaySlipDynamicFormController.
 */
public interface PaySlipDynamicFormController {

	/**
	 * Purpose: Edit and Save a section metadata.
	 * 
	 * @param metaData
	 *            the meta data
	 * @param tabName
	 *            the tab name
	 * @param year
	 *            the year
	 * @param month
	 *            the month
	 * @param part
	 *            the part
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String saveDynamicXML(String metaData, String tabName, int year,
			long month, int part, HttpServletRequest request,
			HttpServletResponse response);

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
	 * @param paySlipDynamicForm
	 *            the pay slip dynamic form
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

	String getOptionsFromXL(PaySlipDynamicForm paySlipDynamicForm,
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
	 * @param formId
	 *            the form id
	 * @param year
	 *            the year
	 * @param month
	 *            the month
	 * @param part
	 *            the part
	 * @param effectiveDateChanged
	 *            the effective date changed
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String saveXML(String metaData, String tabName, long formId, int year,
			long month, int part, boolean effectiveDateChanged,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose: to get the calculatiory fields.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the calculatiory fields
	 */
	String getCalculatioryFields(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Purpose: to Get the dictionary names.
	 * 
	 * @param dictionaryIds
	 *            the dictionary ids
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the dictionary names
	 */
	String getDictionaryNames(String[] dictionaryIds,
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

	/**
	 * Gets the month list.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the month list
	 */
	String getMonthList(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Get Payslip Released Status for given Month, year , Part
	 * 
	 * @param monthId
	 *            the month Id
	 * @param year
	 *            the year
	 * @param part
	 *            the part
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return Payslip Released Status
	 */
	String getPayslipReleasedStatus(Long monthId, int year, Integer part,
			HttpServletRequest request, HttpServletResponse response);

}
