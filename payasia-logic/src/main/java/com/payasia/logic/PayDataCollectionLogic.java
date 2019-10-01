package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PayDataCollectionForm;
import com.payasia.common.form.PayDataCollectionResponseForm;
import com.payasia.common.form.SortCondition;

@Transactional
public interface PayDataCollectionLogic {

	/**
	 * purpose : search Employee List.
	 * 
	 * @param employeeId
	 * 
	 * @param Long
	 *            the companyId
	 * @param String
	 *            the searchText
	 * @param String
	 *            the searchCondition
	 * @param PageRequest
	 *            the pageDTO
	 * @param SortCondition
	 *            the sortDTO
	 */
	PayDataCollectionResponseForm getEmployeeList(Long companyId,
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId);

	/**
	 * purpose : add PayDataCollection.
	 * 
	 * @param String
	 *            the metaData
	 * @param Long
	 *            the companyId
	 */
	String addPayDataCollection(Long companyId, String metaData);

	/**
	 * purpose : get EmployeeId for AutoComplete Search .
	 * 
	 * @param employeeId
	 * 
	 * @param String
	 *            the searchString
	 * @param Long
	 *            the companyId
	 */
	List<PayDataCollectionForm> getEmployeeId(Long companyId,
			String searchString, Long employeeId);

	/**
	 * purpose : get PayCode for AutoComplete Search .
	 * 
	 * @param String
	 *            the searchString
	 * @param Long
	 *            the companyId
	 */
	List<PayDataCollectionForm> getPayCode(Long companyId, String searchString);

	/**
	 * purpose : get PayDataCollection Of Employee for Edit.
	 * 
	 * @param Long
	 *            the payDataCollectionId
	 * @param Long
	 *            the companyId
	 */
	PayDataCollectionForm getPayDataCollectionForEdit(Long companyId,
			Long payDataCollectionId);

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
	 * @param Long
	 *            the companyId
	 */
	void updatePayDataCollection(PayDataCollectionForm payDataCollectionForm,
			Long companyId);

	/**
	 * purpose : get All PayCode.
	 * 
	 * @param Long
	 *            the companyId
	 */
	List<PayDataCollectionForm> getAllPayCode(Long companyId);

}
