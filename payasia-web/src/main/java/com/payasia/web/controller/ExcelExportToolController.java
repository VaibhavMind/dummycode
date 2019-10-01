/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Interface ExcelExportToolController.
 */
public interface ExcelExportToolController {

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
	 * Purpose: To save a new Excel Export template.
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
	 * Purpose: To get the data for a saved Template.
	 * 
	 * @param templateId
	 *            the template id
	 * @return the data for template
	 */
	String getDataForTemplate(long templateId);

	/**
	 * Purpose: To delete a filter of a saved Template.
	 * 
	 * @param filterId
	 *            the filter id
	 */
	void deleteFilter(long filterId);

	/**
	 * Purpose: To edit a already saved template.
	 * 
	 * @param templateId
	 *            the template id
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
	String saveEditTemplate(long templateId, String metaData,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	/**
	 * Purpose: To get the existing template defination list
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
			Long entityId, String scope, String columnName, String sortingType,
			int page, int rows, HttpServletRequest request,
			HttpServletResponse response);

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

	String getExistMappingForGroup(long entityId, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

}
