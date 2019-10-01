/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.HRLetterForm;

/**
 * The Interface HRLettersController.
 */
public interface HRLetterController {

	/**
	 * View hr letter list.
	 * 
	 * @return the string
	 */
	String viewHRLetterList(String columnName, String sortingType, int page,
			int rows, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Save hr letter.
	 * 
	 * @param hrLetterForm
	 *            the hr letter form
	 * @return
	 */
	String saveHRLetter(HRLetterForm hrLetterForm, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Delete hr letter.
	 * 
	 * @param letterId
	 *            the letter id
	 * @return
	 */
	String deleteHRLetter(long letterId);

	/**
	 * Gets the hR letter details.
	 * 
	 * @param letterId
	 *            the letter id
	 * @return the hR letter details
	 */
	String getHRLetterDetails(long letterId);

	/**
	 * Update hr letter.
	 * 
	 * @param hrLetterForm
	 *            the hr letter form
	 * @return
	 */
	String updateHRLetter(HRLetterForm hrLetterForm,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * View hr letter.
	 * 
	 * @param long the letterId
	 * @return
	 */
	String viewHRLetter(long letterId);

	/**
	 * purpose : search HRLetter.
	 * 
	 * @param String
	 *            the columnName
	 * @param String
	 *            the sortingType
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @param String
	 *            the searchCondition
	 * @param String
	 *            the searchText
	 * @param int the page
	 * @param int the rows
	 * @return HRLetterResponse contains HrLetter List
	 */
	String searchHRLetter(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response);

	void deleteFilter(Long filterId);

	String getEditEmployeeFilterList(Long hrLetterId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String saveEmployeeFilterList(String metaData, Long hrLetterId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	/**
	 * purpose : Send HRLetter to Given EmailId.
	 * 
	 * @param String
	 *            the emailId
	 * @param long the letterId
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @param Locale
	 *            the locale
	 * @return Response
	 */

	String sendHRLetter(long hrLetterId, HRLetterForm hrLetterForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String previewHRLetterBodyText(long hrLetterId, long employeeId,
			String[] userInputKeyArr, String[] userInputValArr,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String sendHrLetterWithPDF(long hrLetterId, long employeeId,
			String hrLetterBodyText, String ccEmails,
			boolean saveInDocCenterCheck, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String searchEmployeeHRLetter(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response);

	String previewHRLetterBodyText(long hrLetterId, String[] userInputKeyArr,
			String[] userInputValArr, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String custPlaceHolderList(long hrLetterId, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String isSaveHrLetterInDocumentCenter(HttpServletRequest request,
			HttpServletResponse response);

	void viewHrLetterInPDF(HRLetterForm hrLetterForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	void viewEmployeeHrLetterInPDF(HRLetterForm hrLetterForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

}
