package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.CurrencyDefinitionForm;
import com.payasia.common.form.CurrencyDefinitionResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface CurrencyDefinitionLogic.
 */
@Transactional
public interface CurrencyDefinitionLogic {

	/**
	 * purpose : Update Currency Definition.
	 * 
	 * @param CurrencyDefinitionForm
	 *            the currencyDefinitionForm
	 * @param Long
	 *            the currencyId
	 * @param Integer
	 *            the year
	 * @param Long
	 *            the companyId
	 * @return CurrencyDefinitionForm
	 */
	String updateCurrencyDefinition(
			CurrencyDefinitionForm currencyDefinitionForm, Long companyId);

	/**
	 * purpose : get Currency name for Defined Currency Definition.
	 * 
	 * @param Long
	 *            the companyId
	 * @return CurrencyDefinitionForm List
	 */
	List<CurrencyDefinitionForm> getCurrencyName();

	/**
	 * purpose : Add Currency Definition.
	 * 
	 * @param CurrencyDefinitionForm
	 *            the currencyDefinitionForm
	 * @param Long
	 *            the companyId
	 * @return Response
	 */
	String addCurrencyDefinition(CurrencyDefinitionForm currencyDefinitionForm,
			Long companyId);

	/**
	 * purpose : View Currency Definition.
	 * 
	 * @param Long
	 *            the year
	 * @param Long
	 *            the companyId
	 * @param PageRequest
	 *            the pageDTO
	 * @param SortCondition
	 *            the sortDTO
	 * @return CurrencyDefinitionResponse contains List
	 */
	CurrencyDefinitionResponse viewCurrencyDefinition(Long companyId,
			Long currencyId, String currencyDate, PageRequest pageDTO,
			SortCondition sortDTO);

	/**
	 * purpose : get Data For CurrencyDefinition for Edit .
	 * 
	 * @param Long
	 *            the currencyId
	 * @param Long
	 *            the companyId
	 * @param Integer
	 *            the year
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @return Response
	 */
	CurrencyDefinitionForm getDataForCurrencyDefinition(Long companyId,
			Long companyExchangeRateId);

	String deleteCurrencyDefinition(Long companyId, Long companyExchangeRateId);

	String getBaseCurrency(Long companyId);

	CurrencyDefinitionForm importCompanyExchangeRate(
			CurrencyDefinitionForm currencyDefinitionForm, Long companyId);

}
