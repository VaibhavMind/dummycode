package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.CompanyDocumentCenterForm;

/**
 * The Interface CompanyDocumentCenterController.
 */
/**
 * @author ragulapraveen
 * 
 */
public interface CompanyDocumentCenterController {

	/**
	 * Purpose : Delete company document.
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
	 * Purpose : Search company document
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
	String searchDocument(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			String columnName, String sortingType, String searchCondition,
			String searchText, Long categoryId, int page, int rows);

	/**
	 * Purpose : Fetching the uploaded company data.
	 * 
	 * @param docId
	 *            the doc id
	 * @param model
	 *            the model
	 * @return the string
	 */
	String editUploadedData(long docId);

	/**
	 * Purpose : Updating company document.
	 * 
	 * @param companyDocumentCenterForm
	 *            the company document center form
	 * @param result
	 *            the result
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @return the string
	 */
	String updateDocument(CompanyDocumentCenterForm companyDocumentCenterForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	/**
	 * purpose : Checking company document already exists or not.
	 * 
	 * @param catergoryId
	 *            the catergory id
	 * @param documentName
	 *            the document name
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @return the string
	 */
	String checkCompanyDocument(Long catergoryId, String documentName,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	/**
	 * Purpose : Searching employee documents
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

	/**
	 * Purpose : Gets the employee filter list.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the employee filter list
	 */
	String getEmployeeFilterList(HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * Purpose : Gets the edits the employee filter list.
	 * 
	 * @param documentId
	 *            the document id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the edits the employee filter list
	 */
	String getEditEmployeeFilterList(Long documentId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	/**
	 * Purpose : Delete filter.
	 * 
	 * @param filterId
	 *            the filter id
	 */
	void deleteFilter(Long filterId);

	/**
	 * Purpose : Save employee filter list.
	 * 
	 * @param metaData
	 *            the meta data
	 * @param documentId
	 *            the document id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String saveEmployeeFilterList(String metaData, Long documentId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	void viewDocument(long docId, HttpServletResponse response,
			HttpServletRequest request);

	String uploadDocument(CompanyDocumentCenterForm companyDocumentCenterForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String deleteTaxDocuments(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Long[] docIds);

}
