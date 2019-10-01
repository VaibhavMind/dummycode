package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.PayCodeDataForm;
import com.payasia.common.form.PayDataCollectionForm;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface PayCodeDataController.
 */
public interface PayCodeDataController {

	/**
	 * purpose : payCode Details.
	 * 
	 * @param int the page
	 * @param int the rows
	 * @param String
	 *            the columnName
	 * @param String
	 *            the sortingType
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	String payCodeDetails(int page, int rows, String columnName,
			String sortingType, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : add PayCode.
	 * 
	 * @param String
	 *            the payCode
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	void addPayCode(String payCode, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : get PayCode Data for edit.
	 * 
	 * @param long the payCodeId
	 */
	String getPayCodeData(long payCodeId);

	/**
	 * purpose : update Paycode Data.
	 * 
	 * @param PayCodeDataForm
	 *            the payCodeDataForm
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	void updatePaycodeData(PayCodeDataForm payCodeDataForm,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : filter or search Employee .
	 * 
	 * @param String
	 *            the columnName
	 * @param String
	 *            the sortingType
	 * @param String
	 *            the searchText
	 * @param String
	 *            the searchCondition
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	String filterEmployeeList(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : get EmployeeId for AutoComplete Search .
	 * 
	 * @param String
	 *            the searchString
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	String getEmployeeId(String searchString, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : get PayCode for AutoComplete Search .
	 * 
	 * @param String
	 *            the searchString
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	String getPayCode(String searchString, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * purpose : get PayDataCollection Of Employee for Edit.
	 * 
	 * @param Long
	 *            the payDataCollectionId
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	String getPayDataCollectionForEmployee(Long payDataCollectionId,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : Delete PayDataCollection.
	 * 
	 * @param Long
	 *            the payDataCollectionId
	 */
	void deletePayDataCollection(Long payDataCollectionId);

	/**
	 * purpose : update PayDataCollection.
	 * 
	 * @param PayDataCollectionForm
	 *            the payDataCollectionForm
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	void updatePayDataCollection(PayDataCollectionForm payDataCollectionForm,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : get PayCode From Excel File and save to the database.
	 * 
	 * @param PayDataCollectionForm
	 *            the payDataCollectionForm
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 */
	void getPayCodeFromXL(PayCodeDataForm payCodeDataForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	/**
	 * purpose : delete Paycode.
	 * 
	 * @param long the payCodeId
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @param Locale
	 *            the locale
	 */
	String deletePaycode(long payCodeId, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * purpose : add PayDataCollection.
	 * 
	 * @param String
	 *            the metaData
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @param Locale
	 *            the locale
	 */
	String addPayDataCollection(String metaData, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String getPayCodeList(HttpServletRequest request,
			HttpServletResponse response);

}
