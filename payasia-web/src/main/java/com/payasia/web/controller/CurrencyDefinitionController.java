package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.CurrencyDefinitionForm;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface CurrencyDefinitionController.
 */
public interface CurrencyDefinitionController {

	void deleteCurrencyDefinition(Long companyExchangeRateId,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : View Currency Definition.
	 * 
	 * @param Long
	 *            the year
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
	 * @return CurrencyDefinitionResponse contains List
	 */
	String viewCurrencyDefinition(int page, int rows, String columnName,
			String sortingType, Long currencyId, String currencyDate,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : get Data For CurrencyDefinition for Edit .
	 * 
	 * @param Long
	 *            the currencyId
	 * @param Integer
	 *            the year
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return Response
	 */
	String getDataForCurrencyDefinition(Long companyExchangeRateId,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : Update Currency Definition.
	 * 
	 * @param CurrencyDefinitionForm
	 *            the currencyDefinitionForm
	 * @param Long
	 *            the currencyId
	 * @param Integer
	 *            the year
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return CurrencyDefinitionForm
	 */
	String updateCurrencyDefinition(
			CurrencyDefinitionForm currencyDefinitionForm,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * purpose : Add Currency Definition.
	 * 
	 * @param CurrencyDefinitionForm
	 *            the currencyDefinitionForm
	 * @param Locale
	 *            the locale
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return Response
	 */
	String addCurrencyDefinition(CurrencyDefinitionForm currencyDefinitionForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String getBaseCurrency(HttpServletRequest request,
			HttpServletResponse response);

	String importCompanyExchangeRate(
			CurrencyDefinitionForm currencyDefinitionForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception;

}
