/**
 * @author ragulapraveen
 *
 */
package com.payasia.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.PayCodeDataConditionDTO;
import com.payasia.common.dto.PayDataCollectionConditionDTO;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.PayDataCollection;
import com.payasia.dao.bean.Paycode;

/**
 * The Interface PayDataCollectionDAO.
 */
public interface PayDataCollectionDAO {

	/**
	 * Update PayDataCollection Object.
	 * 
	 * @param payDataCollection
	 *            the pay data collection
	 */
	void update(PayDataCollection payDataCollection);

	/**
	 * Save PayDataCollection Object.
	 * 
	 * @param payDataCollection
	 *            the pay data collection
	 */
	void save(PayDataCollection payDataCollection);

	/**
	 * Delete PayDataCollection Object.
	 * 
	 * @param payDataCollection
	 *            the pay data collection
	 */
	void delete(PayDataCollection payDataCollection);

	/**
	 * Find PayDataCollection Object by id.
	 * 
	 * @param payDataCollectionId
	 *            the pay data collection id
	 * @return the pay data collection
	 */
	PayDataCollection findByID(long payDataCollectionId);

	/**
	 * Gets the count for all PayDataCollection Object.
	 * 
	 * @param payCodeDataConditionDTO
	 *            the pay code data condition dto
	 * @return the count for all
	 */
	int getCountForAll(PayCodeDataConditionDTO payCodeDataConditionDTO);

	/**
	 * Gets the count for PayDataCollection Object .
	 * 
	 * @param payDataCollectionConditionDTO
	 *            the pay data collection condition dto
	 * @param companyId
	 *            the company id
	 * @return the count for condition
	 */
	int getCountForCondition(
			PayDataCollectionConditionDTO payDataCollectionConditionDTO,
			Long companyId);

	/**
	 * Gets the sort path for pay data.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param payDataRoot
	 *            the pay data root
	 * @param PayDataCollectionEmpJoin
	 *            the pay data collection emp join
	 * @param PayDataCollectionPayCodeJoin
	 *            the pay data collection pay code join
	 * @return the sort path for pay data
	 */
	Path<String> getSortPathForPayData(SortCondition sortDTO,
			Root<PayDataCollection> payDataRoot,
			Join<PayDataCollection, Employee> PayDataCollectionEmpJoin,
			Join<PayDataCollection, Paycode> PayDataCollectionPayCodeJoin);

	/**
	 * Gets PayDataCollection Object for edit by companyId and
	 * payDataCollectionId.
	 * 
	 * @param companyId
	 *            the company id
	 * @param payDataCollectionId
	 *            the pay data collection id
	 * @return the pay data collection for edit
	 */
	PayDataCollection getPayDataCollectionForEdit(Long companyId,
			Long payDataCollectionId);

	/**
	 * Find PayDataCollection Object by companyId and payCodeId.
	 * 
	 * @param companyId
	 *            the company id
	 * @param payCodeId
	 *            the pay code id
	 * @return the list
	 */
	List<PayDataCollection> findByPayCode(Long companyId, Long payCodeId);

	/**
	 * Delete PayDataCollection Object by employeeId and companyID.
	 * 
	 * @param enployeeId
	 *            the enployee id
	 * @param companyId
	 *            the company id
	 */
	void deleteByCondition(Long enployeeId, Long companyId);

	/**
	 * New tran delete PayDataCollection Object by employeeId and companyID.
	 * 
	 * @param enployeeId
	 *            the enployee id
	 * @param companyId
	 *            the company id
	 */
	void deleteByEmpCondition(Long enployeeId, Long companyId);

	/**
	 * New tran save PayDataCollection Object.
	 * 
	 * @param payDataCollection
	 *            the pay data collection
	 */
	void newTranSave(PayDataCollection payDataCollection);

	/**
	 * Save return PayDataCollection Object.
	 * 
	 * @param payDataCollection
	 *            the pay data collection
	 * @return the pay data collection
	 */
	PayDataCollection saveReturn(PayDataCollection payDataCollection);

	/**
	 * New tran save return PayDataCollection Object.
	 * 
	 * @param payDataCollection
	 *            the pay data collection
	 * @return the pay data collection
	 */
	PayDataCollection newTranSaveReturn(PayDataCollection payDataCollection);

	/**
	 * Find all PayDataCollection Object by companyID.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<PayDataCollection> findAll(long companyId);

	/**
	 * Find Object by colMAp , finalFilterLIst , companyID and dateFormat.
	 * 
	 * @param colMap
	 *            the col map
	 * @param finalFilterList
	 *            the final filter list
	 * @param companyId
	 *            the company id
	 * @param dateFormat
	 *            the date format
	 * @return the list
	 */
	List<Object[]> findByCondition(Map<String, DataImportKeyValueDTO> colMap,
			List<ExcelExportFiltersForm> finalFilterList, Long companyId,
			String dateFormat);

	/**
	 * Find PayDataCollection Object list by payDataCollectionConditionDTO and
	 * companyID.
	 * 
	 * @param payDataCollectionConditionDTO
	 *            the pay data collection condition dto
	 * @param companyId
	 *            the company id
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @return the list
	 */
	List<PayDataCollection> findByCondition(
			PayDataCollectionConditionDTO payDataCollectionConditionDTO,
			Long companyId, PageRequest pageDTO, SortCondition sortDTO);

}
