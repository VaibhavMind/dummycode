package com.payasia.logic;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PayCodeDataForm;
import com.payasia.common.form.PayCodeDataFormResponse;
import com.payasia.common.form.SortCondition;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface PayCodeDataLogic.
 */
@Transactional
public interface PayCodeDataLogic {
	/**
	 * purpose : payCode Details.
	 * 
	 * @param Long
	 *            the companyId
	 * @param PageRequest
	 *            the pageDTO
	 * @param SortCondition
	 *            the sortDTO
	 */
	PayCodeDataFormResponse getPayCodedetails(Long companyId,
			PageRequest pageDTO, SortCondition sortDTO);

	/**
	 * purpose : add PayCode.
	 * 
	 * @param String
	 *            the companyId
	 * @param String
	 *            the payCode
	 */
	void addPayCode(Long companyId, String payCode);

	/**
	 * purpose : get PayCode Data for edit.
	 * 
	 * @param long the payCodeId
	 */
	PayCodeDataForm getPayCodeData(long payCodeId);

	/**
	 * purpose : update Paycode Data.
	 * 
	 * @param PayCodeDataForm
	 *            the payCodeDataForm
	 * @param Long
	 *            the companyId
	 */
	void updatePaycodeData(PayCodeDataForm payCodeDataForm, Long companyId);

	/**
	 * purpose : delete Paycode.
	 * 
	 * @param Long
	 *            the companyId
	 * @param long the payCodeId
	 */
	String deletePaycode(Long companyId, long payCodeId);

	/**
	 * purpose : get PayCode From Excel File and save to the database.
	 * 
	 * @param PayDataCollectionForm
	 *            the payDataCollectionForm
	 * @param Long
	 *            the companyId
	 */
	void getPayCodeFromXL(PayCodeDataForm payCodeDataForm, Long companyId);

}
