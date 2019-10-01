/*
 * author praveen 
 */
package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.form.CompanyCopyForm;
import com.payasia.common.form.CompanyForm;

/**
 * The Interface CompanyInformationController.
 */
public interface CompanyInformationController {

	/**
	 * Searching company based on search conditon and search text.
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
	String searchCompany(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	/**
	 * Gets the column names of selected view.
	 * 
	 * @param viewID
	 *            the view id
	 * @return the custom column name
	 */
	String getCustomColumnName(Long viewID);

	/**
	 * Edits the company,Information related to company is retrieved.
	 * 
	 * @param companyForm
	 *            the company form
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @param companyId
	 *            the company id
	 * @return the string
	 */
	String editCompany(CompanyForm companyForm, HttpServletResponse response,
			Locale locale, Long companyId);

	/**
	 * Company Grid view data is fetched .
	 * 
	 * @param viewId
	 *            the view id
	 * @param request
	 *            the request
	 * @param res
	 *            the res
	 * @return the string
	 */
	String companyGridView(Long viewId, HttpServletRequest request,
			HttpServletResponse res);

	/**
	 * Save company grid view.
	 * 
	 * @param viewName
	 *            the view name
	 * @param recordsPerPage
	 *            the records per page
	 * @param dataDictionaryIdArr
	 *            the data dictionary id arr
	 * @param rowIndexsArr
	 *            the row indexs arr
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String saveCompanyGridView(String viewName, int recordsPerPage,
			String[] dataDictionaryIdArr, String[] rowIndexsArr,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * Gets the company grid view list.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the company grid view list
	 */
	String getCompanyGridViewList(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Delete view.
	 * 
	 * @param viewId
	 *            the view id
	 * @return the string
	 */
	String deleteCompanyGridView(Long viewId);

	/**
	 * Update company grid view.
	 * 
	 * @param viewName
	 *            the view name
	 * @param recordsPerPage
	 *            the records per page
	 * @param dataDictionaryIdArr
	 *            the data dictionary id arr
	 * @param rowIndexsArr
	 *            the row indexs arr
	 * @param viewId
	 *            the view id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String updateCompanyGridView(String viewName, int recordsPerPage,
			String[] dataDictionaryIdArr, String[] rowIndexsArr, Long viewId,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * Save company static data.
	 * 
	 * @param companyForm
	 *            the company form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String saveCompanyStaticData(CompanyForm companyForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	/**
	 * Update company static data.
	 * 
	 * @param companyForm
	 *            the company form
	 * @param entityKey
	 *            the entity key
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String updateCompanyStaticData(CompanyForm companyForm, Long entityKey,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	/**
	 * Loadgrid : Loads data into company dynamic form grid based on grid id.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param gridId
	 *            the grid id
	 * @param columnCount
	 *            the column count
	 * @param fieldNames
	 *            the field names
	 * @param fieldTypes
	 *            the field types
	 * @param editCompanyJqueryDateFormat
	 *            the edit company jquery date format
	 * @return the string
	 */
	String loadgrid(HttpServletRequest request, HttpServletResponse response,
			Long gridId, int columnCount, String[] fieldNames,
			String[] fieldTypes, String editCompanyJqueryDateFormat);

	/**
	 * Update the companies information.
	 * 
	 * @param companyForm
	 *            the company form
	 * @param xml
	 *            the xml
	 * @param entityId
	 *            the entity id
	 * @param formId
	 *            the form id
	 * @param version
	 *            the version
	 * @param tabNumber
	 *            the tab number
	 * @param entityKey
	 *            the entity key
	 * @param sectionId
	 *            the section id
	 * @param existingCompanyDateFormat
	 *            the existing company date format
	 * @param companyDateFormat
	 *            the company date format
	 * @param session
	 *            the session
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String updateCompany(CompanyForm companyForm, String xml, Long entityId,
			Long formId, Integer version, Integer tabNumber, Long entityKey,
			Long sectionId, String existingCompanyDateFormat,
			String companyDateFormat, HttpSession session,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	/**
	 * Delete company.
	 * 
	 * @param cmpId
	 *            the cmp id
	 * @param request
	 *            the request
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String deleteCompany(Long cmpId, HttpServletRequest request, Locale locale);

	String updateCompanyTableRecord(String tableXML, Long tabId, Integer seqNo,
			HttpServletRequest request, HttpServletResponse response);

	String deleteCompanyTableRecord(Long tableId, Integer seqNo,
			HttpServletRequest request, HttpServletResponse response);

	String saveCompanyTableRecord(String tableXML, Long tabId, Long formId,
			Integer version, Long entityKey, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Copy company.
	 * 
	 * @param companyCopyForm
	 *            the company copy form
	 * @param cmpId
	 *            the cmp id
	 * @param request
	 *            the request
	 * @return the string
	 */
	String copyCompany(CompanyCopyForm companyCopyForm, Long cmpId,
			HttpServletRequest request, Locale locale);

	/**
	 * Selected Company Grid View data is fetched.
	 * 
	 * @param viewId
	 *            the view id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String selectedCompanyGridView(Long viewId, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

}
