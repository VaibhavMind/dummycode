/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.web.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Interface ExcelImportToolController.
 */
public interface ExcelImportToolController {

	/**
	 * Purpose: To save a new Excel Import template.
	 * 
	 * @param metaData
	 *            the meta data
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String saveTemplate(String metaData, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * Purpose: To Delete a template.
	 * 
	 * @param templateId
	 *            the template id
	 */
	void deleteTemplate(long templateId);

	/**
	 * Purpose: Gets the existing non table data dictionary list and table
	 * names.
	 * 
	 * @param entityId
	 *            the entity id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the exist mapping
	 */
	String getExistMapping(long entityId, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * Purpose: To get the data for a saved Template.
	 * 
	 * @param templateId
	 *            the template id
	 * @return the data for template
	 */
	String getDataForTemplate(long templateId);

	/**
	 * Purpose: To edit a already saved template.
	 * 
	 * @param metaData
	 *            the meta data
	 * @param templateId
	 *            the template id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String editTemplate(String metaData, long templateId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	/**
	 * Purpose: To get the existing template definition list
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
	 * @return the exist import temp def
	 */
	String getExistImportTempDef(String searchCondition, String searchText,
			Long entityId, String columnName, String sortingType, int page,
			int rows, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose: To get the Data Dictionaries of selected table.
	 * 
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 * @param tablePosition
	 *            the table position
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the exist table mapping
	 */
	String getExistTableMapping(long entityId, long formId, int tablePosition,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	/**
	 * Purpose: To generate a Excel file for the template.
	 * 
	 * @param templateId
	 *            the template id
	 * @param response
	 *            the response
	 * @param request
	 *            the request
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	void generateExcel(long templateId, HttpServletResponse response,
			HttpServletRequest request) throws IOException;

}
