/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.DataImportForm;

/**
 * The Interface DataImportController.
 */
public interface DataImportController {

	/**
	 * Purpose: To get the company payslip frequency details.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the company freq
	 */
	String getCompanyFreq(HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Purpose: To show the previous import attempts history.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String showHistory(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Purpose: To show the import template list.
	 * 
	 * @param entityId
	 *            the entity id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the string
	 */
	String showTemplateList(long entityId, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Purpose: To perform import via file upload.
	 * 
	 * @param dataImportForm
	 *            the data import form
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
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String uploadFile(DataImportForm dataImportForm, BindingResult result,
			ModelMap model, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale);

	String dataImportStatus(String fileName, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String getPayslipFormat(HttpServletRequest request,
			HttpServletResponse response);

	String previewTextPayslipPdf(DataImportForm dataImportForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response);

	void getPreviewTextPayslipPdf(String previewPdfFilePath,
			HttpServletRequest request, HttpServletResponse response);

	String getCompanyFilterList(HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String configureCompanyAddress(String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String getCompanyAddressMappingList(HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String getCompanyAddress(HttpServletRequest request,
			HttpServletResponse response, Locale locale);

}
