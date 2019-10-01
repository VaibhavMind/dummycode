/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.web.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;

import com.payasia.common.form.DataExportForm;

/**
 * The Interface DataExportController.
 */
public interface DataExportController {

	/**
	 * Purpose: To get the filters list for the template.
	 * 
	 * @param templateId
	 *            the template id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param model
	 *            the model
	 * @param locale
	 *            the locale
	 * @return the filters for template
	 */
	String getFiltersForTemplate(long templateId, HttpServletRequest request,
			HttpServletResponse response, ModelMap model, Locale locale);

	/**
	 * Purpose: To generate Excel sheet containing exported data.
	 * 
	 * @param dataExportForm
	 *            the data export form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	void generateExcel(DataExportForm dataExportForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws IOException;

	/**
	 * Purpose: To get the list of Export templates
	 * 
	 * @param searchCondition
	 *            the search condition
	 * @param searchText
	 *            the search text
	 * @param entityId
	 *            the entity id
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the export templates
	 */
	String getExportTemplates(String searchCondition, String searchText,
			Long entityId, String scope, String columnName, String sortingType,
			int page, int rows, HttpServletRequest request,
			HttpServletResponse response);

	String getFiltersForTemplateGroup(long templateId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model, Locale locale);

	void generateExcelGroup(DataExportForm dataExportForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws IOException;

}
